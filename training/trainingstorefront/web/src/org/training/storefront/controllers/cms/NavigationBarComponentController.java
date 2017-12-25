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

import de.hybris.platform.acceleratorcms.enums.NavigationBarMenuLayout;
import de.hybris.platform.acceleratorcms.model.components.NavigationBarComponentModel;
import org.training.storefront.controllers.ControllerConstants;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 *
 */
@Controller("NavigationBarComponentController")
@RequestMapping(value = ControllerConstants.Actions.Cms.NavigationBarComponent)
public class NavigationBarComponentController extends AbstractAcceleratorCMSComponentController<NavigationBarComponentModel>
{
	@Override
	protected void fillModel(final HttpServletRequest request, final Model model, final NavigationBarComponentModel component)
	{
		if (component.getDropDownLayout() != null)
		{
			model.addAttribute("dropDownLayout", component.getDropDownLayout().getCode().toLowerCase());
		}
		else if (component.getNavigationNode() != null && component.getNavigationNode().getChildren() != null
				&& !component.getNavigationNode().getChildren().isEmpty())
		{
			// Component has children but not drop down layout, default to auto
			model.addAttribute("dropDownLayout", NavigationBarMenuLayout.AUTO.getCode().toLowerCase());
		}
	}
}
