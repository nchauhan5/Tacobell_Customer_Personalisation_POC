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
package org.training.storefront.controllers.misc;

import de.hybris.platform.acceleratorstorefrontcommons.controllers.AbstractController;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * Controller to handle the tax estimation on a cart.
 */
@Controller
public class EstimateTaxesController extends AbstractController
{
	@Resource(name = "cartFacade")
	private CartFacade cartFacade;


	@RequestMapping(value = "/cart/estimate", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public CartData addToCart(@RequestParam("zipCode") final String zipCode, @RequestParam("isocode") final String countryIsoCode,
			final Model model)
	{
		return cartFacade.estimateExternalTaxes(zipCode, countryIsoCode);
	}
}
