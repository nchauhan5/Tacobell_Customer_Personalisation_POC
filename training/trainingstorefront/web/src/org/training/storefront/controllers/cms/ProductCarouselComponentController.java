/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package org.training.storefront.controllers.cms;

import static de.hybris.platform.cms2.misc.CMSFilter.PREVIEW_TICKET_ID_PARAM;
import static java.util.stream.Collectors.toList;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2lib.model.components.ProductCarouselComponentModel;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.ProductSearchFacade;
import de.hybris.platform.commercefacades.search.data.SearchQueryData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import org.training.storefront.controllers.ControllerConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * Controller for CMS ProductReferencesComponent.
 */
@Controller("ProductCarouselComponentController")
@RequestMapping(value = ControllerConstants.Actions.Cms.ProductCarouselComponent)
public class ProductCarouselComponentController extends AbstractAcceleratorCMSComponentController<ProductCarouselComponentModel>
{
	protected static final List<ProductOption> PRODUCT_OPTIONS = Arrays.asList(ProductOption.BASIC, ProductOption.PRICE);

	@Resource(name = "accProductFacade")
	private ProductFacade productFacade;

	@Resource(name = "productSearchFacade")
	private ProductSearchFacade<ProductData> productSearchFacade;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource(name = "catalogVersionService")
	private CatalogVersionService catalogVersionService;

	@Resource(name = "productService")
	private ProductService productService;

	@Resource(name = "searchRestrictionService")
	private SearchRestrictionService searchRestrictionService;

	@Resource(name = "modelService")
	private ModelService modelService;

	@Override
	protected void fillModel(final HttpServletRequest request, final Model model, final ProductCarouselComponentModel component)
	{
		final List<ProductData> products = new ArrayList<>();

		products.addAll(collectLinkedProducts(component));
		products.addAll(collectSearchProducts(component));

		model.addAttribute("title", component.getTitle());
		model.addAttribute("productData", products);
	}

	protected List<ProductData> collectLinkedProducts(final ProductCarouselComponentModel component)
	{
		final List<ProductData> products = new ArrayList<>();

		for (final ProductModel productModel : !isPreview() ? component.getProducts()
				: retrieveProductsForVersionsInSession(getUnfilterdListOfProducts(component)))
		{
			products.add(productFacade.getProductForOptions(productModel, PRODUCT_OPTIONS));
		}

		for (final CategoryModel categoryModel : component.getCategories())
		{
			for (final ProductModel productModel : !isPreview() ? categoryModel.getProducts()
					: retrieveProductsForVersionsInSession(getUnfilterdListOfProducts(categoryModel)))
			{
				products.add(productFacade.getProductForOptions(productModel, PRODUCT_OPTIONS));
			}
		}

		return products;
	}

	protected List<ProductData> collectSearchProducts(final ProductCarouselComponentModel component)
	{
		final SearchQueryData searchQueryData = new SearchQueryData();
		searchQueryData.setValue(component.getSearchQuery());
		final String categoryCode = component.getCategoryCode();

		if (searchQueryData.getValue() != null && categoryCode != null)
		{
			final SearchStateData searchState = new SearchStateData();
			searchState.setQuery(searchQueryData);

			final PageableData pageableData = new PageableData();
			pageableData.setPageSize(100); // Limit to 100 matching results

			return productSearchFacade.categorySearch(categoryCode, searchState, pageableData).getResults();
		}

		return Collections.emptyList();
	}


	/**
	 * When in preview mode, we will swap the given products for their counterpart in the catalog version in session for
	 * their respective catalogs. The session catalog version is not always the active version when used in CMS tooling
	 *
	 * @param persistentProducts
	 *           the list of {@link ProductModel} as they are persisted for the given
	 *           {@link ProductCarouselComponentModel}
	 * @return a list of {@link ProductModel} for the catalog versions in session
	 */
	protected List<ProductModel> retrieveProductsForVersionsInSession(final List<ProductModel> persistentProducts)
	{

		if (!isPreview())
		{
			return persistentProducts;
		}
		else
		{
			return sessionService.executeInLocalView(new SessionExecutionBody()
			{

				@Override
				public Object execute()
				{
					try
					{
						searchRestrictionService.disableSearchRestrictions();

						return persistentProducts
								.stream()
								.map(productModel -> {

									final String code = productModel.getCode();
									final CatalogModel catalog = productModel.getCatalogVersion().getCatalog();
									final CatalogVersionModel sessionCatalogVersionForCatalog = catalogVersionService
											.getSessionCatalogVersionForCatalog(catalog.getId());
									//because of the possibility for a product code to be in multiple ProductCatalog, we explicitly fetch the product with the version in session for the product's catalog
									try
									{
										return productService.getProductForCode(sessionCatalogVersionForCatalog, code);
									}
									catch (final UnknownIdentifierException e)
									{
										return productModel;
									}
								}).collect(toList());

					}
					finally
					{
						searchRestrictionService.enableSearchRestrictions();
					}
				}

			});
		}

	}

	/**
	 * Returns the full list products without the session catalog version filtering out the ones from different versions.
	 * This is needed when the session catalog version is not the active version. This is possible through CMS tooling
	 */
	protected List<ProductModel> getUnfilterdListOfProducts(final ProductCarouselComponentModel component)
	{

		return sessionService.executeInLocalView(new SessionExecutionBody()
		{

			@Override
			public Object execute()
			{
				try
				{
					searchRestrictionService.disableSearchRestrictions();
					final ProductCarouselComponentModel refreshed = (ProductCarouselComponentModel) modelService.get(component.getPk());
					return refreshed.getProducts();
				}
				finally
				{
					searchRestrictionService.enableSearchRestrictions();
				}
			}

		});
	}

	/**
	 * Returns the full list products without the session catalog version filtering out the ones from different versions.
	 * This is needed when the session catalog version is not the active version. This is possible through CMS tooling
	 */
	protected List<ProductModel> getUnfilterdListOfProducts(final CategoryModel category)
	{

		return sessionService.executeInLocalView(new SessionExecutionBody()
		{

			@Override
			public Object execute()
			{
				try
				{
					searchRestrictionService.disableSearchRestrictions();
					final CategoryModel refreshed = (CategoryModel) modelService.get(category.getPk());
					return refreshed.getProducts();
				}
				finally
				{
					searchRestrictionService.enableSearchRestrictions();
				}
			}

		});
	}


	/**
	 * Checks if we are in preview mode by checking the presence of a cmsTicketId in session.
	 *
	 * @return true if in preview mode
	 */
	protected boolean isPreview()
	{
		return sessionService.getAttribute(PREVIEW_TICKET_ID_PARAM) != null;
	}

}
