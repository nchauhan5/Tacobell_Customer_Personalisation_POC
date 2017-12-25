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

import de.hybris.platform.acceleratorcms.model.components.CategoryFeatureComponentModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.training.storefront.controllers.ControllerConstants;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * Controller for CMS ProductFeatureComponent.
 */
@Controller("CategoryFeatureComponentController")
@RequestMapping(value = ControllerConstants.Actions.Cms.CategoryFeatureComponent)
public class CategoryFeatureComponentController extends AbstractAcceleratorCMSComponentController<CategoryFeatureComponentModel>
{
	@Resource(name = "categoryUrlConverter")
	private Converter<CategoryModel, CategoryData> categoryUrlConverter;

	@Override
	protected void fillModel(final HttpServletRequest request, final Model model, final CategoryFeatureComponentModel component)
	{
		final CategoryModel category = component.getCategory();
		if (category != null)
		{
			final String url = categoryUrlConverter.convert(category).getUrl();
			model.addAttribute("url", url);
		}
	}
}
