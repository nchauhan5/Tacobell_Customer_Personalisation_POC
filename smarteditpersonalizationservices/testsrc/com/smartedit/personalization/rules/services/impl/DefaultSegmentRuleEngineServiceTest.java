/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2017 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package com.smartedit.personalization.rules.services.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.droolsruleengineservices.impl.DefaultCommerceRuleEngineService;
import de.hybris.platform.ruleengine.RuleEvaluationContext;
import de.hybris.platform.ruleengine.RuleEvaluationResult;
import de.hybris.platform.ruleengine.dao.RuleEngineContextDao;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineContextModel;
import de.hybris.platform.ruleengineservices.enums.FactContextType;
import de.hybris.platform.ruleengineservices.rao.providers.FactContextFactory;
import de.hybris.platform.ruleengineservices.rao.providers.RAOProvider;
import de.hybris.platform.ruleengineservices.rao.providers.impl.DefaultCartRAOProvider;
import de.hybris.platform.ruleengineservices.rao.providers.impl.FactContext;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.smartedit.personalization.rules.rao.provider.DefaultSegmentRuleCategoryRAOProvider;
import com.smartedit.personalization.rules.rao.provider.DefaultSegmentRuleCustomerRaoProvider;
import com.smartedit.personalization.rules.rao.provider.DefaultSegmentRuleOrderRaoProvider;


/**
 *
 */
@UnitTest
public class DefaultSegmentRuleEngineServiceTest
{
	@InjectMocks
	private DefaultSegmentRuleEngineService defaultSegmentRuleEngineService;

	@Mock
	private DefaultCommerceRuleEngineService commerceRuleEngineService;

	@Mock
	private RuleEngineContextDao ruleEngineContextDao;

	@Mock
	private FactContextFactory factContextFactory;

	@Mock
	private FactContext factContext;

	@Mock
	private DefaultSegmentRuleCustomerRaoProvider defaultSegmentRuleCustomerRaoProvider;

	@Mock
	private DefaultCartRAOProvider cartRAOProvider;

	@Mock
	private DefaultSegmentRuleOrderRaoProvider segmentRuleOrderRaoProvider;

	@Mock
	private DefaultSegmentRuleCategoryRAOProvider segmentRuleCategoryRaoProvider;

	@Mock
	private AbstractRuleEngineContextModel engineContext;

	private CustomerModel customerModel;

	private CartModel cartModel;

	private CategoryModel categoryModel;

	private OrderModel orderModel;

	private Set<RAOProvider> raoProviders;

	private RuleEvaluationResult evalResult;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		customerModel = new CustomerModel();
		cartModel = new CartModel();
		categoryModel = new CategoryModel();
		orderModel = new OrderModel();
		raoProviders = new HashSet<>();
		evalResult = new RuleEvaluationResult();
		Mockito.when(factContextFactory.createFactContext(Mockito.any(FactContextType.class), Mockito.anyList()))
				.thenReturn(factContext);
		Mockito.when(factContext.getProviders(Mockito.any())).thenReturn(raoProviders);
		Mockito.when(ruleEngineContextDao.getRuleEngineContextByName(Mockito.anyString())).thenReturn(engineContext);
		Mockito.when(commerceRuleEngineService.evaluate(Mockito.any(RuleEvaluationContext.class))).thenReturn(evalResult);
	}

	@Test
	public void testEvaluateForCustomer_IfRuleEngineFound()
	{
		final List<Object> facts = new ArrayList<Object>();
		facts.add(customerModel);
		raoProviders.add(defaultSegmentRuleCustomerRaoProvider);
		Mockito.when(factContext.getFacts()).thenReturn((Collection) facts);
		Mockito.when(defaultSegmentRuleCustomerRaoProvider.expandFactModel(customerModel)).thenReturn(new HashSet<>());
		final RuleEvaluationResult ruleEvaluationResult = defaultSegmentRuleEngineService.evaluate(customerModel);
		Assert.assertNotNull(ruleEvaluationResult);
		Assert.assertEquals(evalResult, ruleEvaluationResult);
	}

	@Test(expected = ModelNotFoundException.class)
	public void testEvaluateForCustomer_IfRuleEngineNotFound()
	{
		final List<Object> facts = new ArrayList<Object>();
		facts.add(customerModel);
		raoProviders.add(defaultSegmentRuleCustomerRaoProvider);
		Mockito.when(factContext.getFacts()).thenReturn((Collection) facts);
		Mockito.when(defaultSegmentRuleCustomerRaoProvider.expandFactModel(customerModel)).thenReturn(new HashSet<>());
		Mockito.doThrow(new ModelNotFoundException("ModelNotFoundException Occurred")).when(ruleEngineContextDao)
				.getRuleEngineContextByName(Mockito.anyString());
		final RuleEvaluationResult ruleEvaluationResult = defaultSegmentRuleEngineService.evaluate(customerModel);
		Assert.assertNotNull(ruleEvaluationResult);
		Assert.assertEquals(evalResult, ruleEvaluationResult);
	}

	@Test(expected = AmbiguousIdentifierException.class)
	public void testEvaluateForCustomer_IfMultipleRuleEngineFound()
	{
		final List<Object> facts = new ArrayList<Object>();
		facts.add(customerModel);
		raoProviders.add(defaultSegmentRuleCustomerRaoProvider);
		Mockito.when(factContext.getFacts()).thenReturn((Collection) facts);
		Mockito.when(defaultSegmentRuleCustomerRaoProvider.expandFactModel(customerModel)).thenReturn(new HashSet<>());
		Mockito.doThrow(new AmbiguousIdentifierException("AmbiguousIdentifierException Occurred")).when(ruleEngineContextDao)
				.getRuleEngineContextByName(Mockito.anyString());
		final RuleEvaluationResult ruleEvaluationResult = defaultSegmentRuleEngineService.evaluate(customerModel);
		Assert.assertNotNull(ruleEvaluationResult);
		Assert.assertEquals(evalResult, ruleEvaluationResult);
	}

	@Test
	public void testEvaluateForCart_IfRuleEngineFound()
	{
		final List<Object> facts = new ArrayList<Object>();
		facts.add(cartModel);
		raoProviders.add(cartRAOProvider);
		Mockito.when(factContext.getFacts()).thenReturn((Collection) facts);
		Mockito.when(cartRAOProvider.expandFactModel(cartModel)).thenReturn(new HashSet<>());
		final RuleEvaluationResult ruleEvaluationResult = defaultSegmentRuleEngineService.evaluate(cartModel);
		Assert.assertNotNull(ruleEvaluationResult);
		Assert.assertEquals(evalResult, ruleEvaluationResult);
	}

	@Test
	public void testEvaluateForOrder_IfRuleEngineFound()
	{
		final List<Object> facts = new ArrayList<Object>();
		facts.add(orderModel);
		raoProviders.add(segmentRuleOrderRaoProvider);
		Mockito.when(factContext.getFacts()).thenReturn((Collection) facts);
		Mockito.when(cartRAOProvider.expandFactModel(orderModel)).thenReturn(new HashSet<>());
		final RuleEvaluationResult ruleEvaluationResult = defaultSegmentRuleEngineService.evaluate(orderModel);
		Assert.assertNotNull(ruleEvaluationResult);
		Assert.assertEquals(evalResult, ruleEvaluationResult);
	}

	@Test
	public void testEvaluateForCategoryAndCart_IfRuleEngineFound()
	{
		final List<Object> facts = new ArrayList<Object>();
		facts.add(cartModel);
		facts.add(categoryModel);
		raoProviders.add(cartRAOProvider);
		raoProviders.add(segmentRuleCategoryRaoProvider);
		Mockito.when(factContext.getFacts()).thenReturn((Collection) facts);
		Mockito.when(cartRAOProvider.expandFactModel(cartModel)).thenReturn(new HashSet<>());
		Mockito.when(segmentRuleCategoryRaoProvider.expandFactModel(categoryModel)).thenReturn(new HashSet<>());
		final RuleEvaluationResult ruleEvaluationResult = defaultSegmentRuleEngineService.evaluate(categoryModel, cartModel);
		Assert.assertNotNull(ruleEvaluationResult);
		Assert.assertEquals(evalResult, ruleEvaluationResult);
	}

}
