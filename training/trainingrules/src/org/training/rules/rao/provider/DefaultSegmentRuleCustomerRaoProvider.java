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
package org.training.rules.rao.provider;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.ruleengineservices.calculation.RuleEngineCalculationService;
import de.hybris.platform.ruleengineservices.rao.providers.impl.AbstractExpandedRAOProvider;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.training.data.CustomerRAO;


/**
 *
 */
public class DefaultSegmentRuleCustomerRaoProvider extends AbstractExpandedRAOProvider<CustomerModel, CustomerRAO>
{
	public static final String INCLUDE_CUSTOMER = "INCLUDE_CUSTOMER";
	private final Collection<String> validOptions = Arrays.asList(new String[]
	{ "INCLUDE_CUSTOMER" });
	private final Collection<String> defaultOptions = Collections.singleton("INCLUDE_CUSTOMER");
	private final Collection<String> minOptions = Arrays.asList(new String[]
	{ "INCLUDE_CUSTOMER" });


	private Converter<CustomerModel, CustomerRAO> customerRaoConverter;
	private RuleEngineCalculationService ruleEngineCalculationService;

	@Override
	protected CustomerRAO createRAO(final CustomerModel customerModel)
	{
		return this.getCustomerRaoConverter().convert(customerModel);
	}

	@Override
	protected Set<Object> expandRAO(final CustomerRAO rao, final Collection<String> options)
	{
		final LinkedHashSet facts = new LinkedHashSet();
		final Iterator arg4 = options.iterator();

		while (arg4.hasNext())
		{
			final String option = (String) arg4.next();
			switch (option.hashCode())
			{
				default:
					if (option.equals("INCLUDE_CUSTOMER"))
					{
						facts.add(rao);
					}
					break;
			}
		}

		return facts;
	}

	@Override
	public Set<?> expandFactModel(final CustomerModel modelFact)
	{
		return this.expandFactModel(modelFact, this.getDefaultOptions());
	}

	protected Set<Object> expandRAOs(final CustomerModel source, final Collection<String> options)
	{
		return this.expandRAO(this.createRAO(source), options);
	}

	/**
	 * @return the customerRaoConverter
	 */
	public Converter<CustomerModel, CustomerRAO> getCustomerRaoConverter()
	{
		return customerRaoConverter;
	}

	/**
	 * @param customerRaoConverter
	 *           the customerRaoConverter to set
	 */
	public void setCustomerRaoConverter(final Converter<CustomerModel, CustomerRAO> customerRaoConverter)
	{
		this.customerRaoConverter = customerRaoConverter;
	}


	/**
	 * @return the ruleEngineCalculationService
	 */
	public RuleEngineCalculationService getRuleEngineCalculationService()
	{
		return ruleEngineCalculationService;
	}

	/**
	 * @param ruleEngineCalculationService
	 *           the ruleEngineCalculationService to set
	 */
	public void setRuleEngineCalculationService(final RuleEngineCalculationService ruleEngineCalculationService)
	{
		this.ruleEngineCalculationService = ruleEngineCalculationService;
	}


	/**
	 * @return the validOptions
	 */
	@Override
	public Collection<String> getValidOptions()
	{
		return validOptions;
	}

	/**
	 * @return the defaultOptions
	 */
	@Override
	public Collection<String> getDefaultOptions()
	{
		return defaultOptions;
	}

	/**
	 * @return the minOptions
	 */
	@Override
	public Collection<String> getMinOptions()
	{
		return minOptions;
	}



}
