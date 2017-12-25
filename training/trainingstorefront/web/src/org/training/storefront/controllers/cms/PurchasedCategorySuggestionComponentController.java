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

import de.hybris.platform.acceleratorcms.model.components.PurchasedCategorySuggestionComponentModel;
import de.hybris.platform.acceleratorcms.model.components.SimpleSuggestionComponentModel;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.cms.AbstractCMSComponentController;
import de.hybris.platform.commercefacades.product.data.ProductData;
import org.training.facades.suggestion.SimpleSuggestionFacade;
import org.training.storefront.controllers.ControllerConstants;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * Controller for CMS PurchasedCategorySuggestionComponent
 */
@Controller("PurchasedCategorySuggestionComponentController")
@RequestMapping(value = ControllerConstants.Actions.Cms.PurchasedCategorySuggestionComponent)
public class PurchasedCategorySuggestionComponentController extends
		AbstractCMSComponentController<PurchasedCategorySuggestionComponentModel>
{
	@Resource(name = "simpleSuggestionFacade")
	private SimpleSuggestionFacade simpleSuggestionFacade;

	@Override
	protected void fillModel(final HttpServletRequest request, final Model model,
			final PurchasedCategorySuggestionComponentModel component)
	{
		final List<ProductData> productSuggestions = simpleSuggestionFacade
				.getReferencesForPurchasedInCategory(component.getCategory().getCode(), component.getProductReferenceTypes(),
						component.isFilterPurchased(), component.getMaximumNumberProducts());

		model.addAttribute("title", component.getTitle());
		model.addAttribute("suggestions", productSuggestions);
	}

	@Override
	protected String getView(final PurchasedCategorySuggestionComponentModel component)
	{
		return ControllerConstants.Views.Cms.ComponentPrefix + StringUtils.lowerCase(SimpleSuggestionComponentModel._TYPECODE);
	}
}
