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
package org.training.core.checkout.flow.impl;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.order.impl.DefaultAcceleratorCheckoutService;
import de.hybris.platform.commerceservices.enums.PickupInStoreMode;
import de.hybris.platform.commerceservices.strategies.PickupStrategy;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.acceleratorservices.checkout.flow.CheckoutFlowStrategy;
import de.hybris.platform.acceleratorservices.enums.CheckoutFlowEnum;
import de.hybris.platform.acceleratorservices.checkout.flow.impl.FixedCheckoutFlowStrategy;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

@SuppressWarnings("deprecation")
@Deprecated
@UnitTest
public class PickUpInStoreCheckoutFlowStrategyTest
{

	private PickUpInStoreCheckoutFlowStrategy strategy;

	@Mock
	private CartService cartService;
	@Mock
	private FixedCheckoutFlowStrategy multiStepCheckoutFlowStrategy;
	@Mock
	private CheckoutFlowStrategy defaultStrategy;
	@Mock
	private PickupStrategy pickupStrategy;

	@Spy
	private final DefaultAcceleratorCheckoutService defaultAcceleratorCheckoutService = new DefaultAcceleratorCheckoutService(); //NOPMD

	@Mock
	private CartModel cartModel;
	@Mock
	private AbstractOrderEntryModel cartEntry;
	@Mock
	private PointOfServiceModel deliveryPOS; //NOPMD



	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		strategy = new PickUpInStoreCheckoutFlowStrategy();
		strategy.setDefaultStrategy(defaultStrategy);
		strategy.setMultiStepCheckoutFlowStrategy(multiStepCheckoutFlowStrategy);
		strategy.setPickupStrategy(pickupStrategy);

		given(pickupStrategy.getPickupInStoreMode()).willReturn(PickupInStoreMode.BUY_AND_COLLECT);
		given(multiStepCheckoutFlowStrategy.getCheckoutFlow()).willReturn(CheckoutFlowEnum.MULTISTEP);
		given(defaultStrategy.getCheckoutFlow()).willReturn(CheckoutFlowEnum.MULTISTEP);

		given(cartService.getSessionCart()).willReturn(cartModel);
		given(cartModel.getEntries()).willReturn(Collections.singletonList(cartEntry));
	}


	@Test
	public void testPickupInStoreFlow()
	{
		given(pickupStrategy.getPickupInStoreMode()).willReturn(PickupInStoreMode.BUY_AND_COLLECT);

		final CheckoutFlowEnum result = strategy.getCheckoutFlow();

		Assert.assertEquals(CheckoutFlowEnum.MULTISTEP, result);
	}


	@Test
	public void testNotPickupInStoreFlow()
	{
		given(pickupStrategy.getPickupInStoreMode()).willReturn(PickupInStoreMode.DISABLED);

		final CheckoutFlowEnum result = strategy.getCheckoutFlow();

		Assert.assertEquals(CheckoutFlowEnum.MULTISTEP, result);
	}

}
