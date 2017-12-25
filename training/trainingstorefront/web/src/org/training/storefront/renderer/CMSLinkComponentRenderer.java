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
package org.training.storefront.renderer;

import de.hybris.platform.acceleratorcms.component.renderer.CMSComponentRenderer;
import de.hybris.platform.acceleratorstorefrontcommons.tags.Functions;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.enums.LinkTargets;
import de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.taglibs.standard.tag.common.core.UrlSupport;
import org.springframework.beans.factory.annotation.Required;


/**
 */
public class CMSLinkComponentRenderer implements CMSComponentRenderer<CMSLinkComponentModel>
{
	private static final Logger LOG = Logger.getLogger(CMSLinkComponentRenderer.class);

	private Converter<ProductModel, ProductData> productUrlConverter;
	private Converter<CategoryModel, CategoryData> categoryUrlConverter;

	protected Converter<ProductModel, ProductData> getProductUrlConverter()
	{
		return productUrlConverter;
	}

	@Required
	public void setProductUrlConverter(final Converter<ProductModel, ProductData> productUrlConverter)
	{
		this.productUrlConverter = productUrlConverter;
	}

	protected Converter<CategoryModel, CategoryData> getCategoryUrlConverter()
	{
		return categoryUrlConverter;
	}

	@Required
	public void setCategoryUrlConverter(final Converter<CategoryModel, CategoryData> categoryUrlConverter)
	{
		this.categoryUrlConverter = categoryUrlConverter;
	}

	protected String getUrl(final CMSLinkComponentModel component)
	{
		// Call the function getUrlForCMSLinkComponent so that this code is only in one place
		return Functions.getUrlForCMSLinkComponent(component, getProductUrlConverter(), getCategoryUrlConverter());
	}

	@Override
	public void renderComponent(final PageContext pageContext, final CMSLinkComponentModel component) throws ServletException,
			IOException
	{
		try
		{
			final String url = getUrl(component);
			final String encodedUrl = UrlSupport.resolveUrl(url, null, pageContext);

			final JspWriter out = pageContext.getOut();

			if (StringUtils.isNotBlank(component.getLinkName()) && StringUtils.isBlank(encodedUrl))
			{
				// <span class="empty-nav-item">${component.linkName}</span>
				out.write("<span class=\"empty-nav-item\">");
				out.write(component.getLinkName());
				out.write("</span>");
			}
			else
			{
				// <a href="${encodedUrl}" ${component.styleAttributes} title="${component.linkName}"
				// ${component.target == null || component.target == 'SAMEWINDOW' ? '' : 'target="_blank"'}>${component.linkName}</a>

				out.write("<a href=\"");
				out.write(encodedUrl);
				out.write("\" ");

				// Write additional attributes onto the link
				if (component.getStyleAttributes() != null)
				{
					out.write(component.getStyleAttributes());
				}

				if (StringUtils.isNotBlank(component.getLinkName()))
				{
					out.write(" title=\"");
					out.write(component.getLinkName());
					out.write("\" ");
				}

				if (component.getTarget() != null && !LinkTargets.SAMEWINDOW.equals(component.getTarget()))
				{
					out.write(" target=\"_blank\"");
				}
				out.write(">");
				if (StringUtils.isNotBlank(component.getLinkName()))
				{
					out.write(component.getLinkName());
				}
				out.write("</a>");
			}
		}
		catch (final JspException e)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug(e);
			}
		}
	}
}
