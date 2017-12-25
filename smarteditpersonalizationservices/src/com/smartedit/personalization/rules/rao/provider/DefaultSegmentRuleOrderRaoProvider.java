package com.smartedit.personalization.rules.rao.provider;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ruleengineservices.calculation.RuleEngineCalculationService;
import de.hybris.platform.ruleengineservices.rao.OrderRAO;
import de.hybris.platform.ruleengineservices.rao.providers.impl.AbstractExpandedRAOProvider;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;


public class DefaultSegmentRuleOrderRaoProvider extends AbstractExpandedRAOProvider<OrderModel, OrderRAO>
{
	private static final String INCLUDE_ORDER = "INCLUDE_ORDER";

	public DefaultSegmentRuleOrderRaoProvider()
	{
		this.validOptions = Arrays.asList(new String[]
		{ INCLUDE_ORDER });

		this.defaultOptions = Collections.singleton(INCLUDE_ORDER);

		this.minOptions = Arrays.asList(new String[]
		{ INCLUDE_ORDER });
	}

	private Converter<OrderModel, OrderRAO> orderRaoConverter;
	private RuleEngineCalculationService ruleEngineCalculationService;

	@Override
	protected OrderRAO createRAO(final OrderModel orderModel)
	{
		return this.getOrderRaoConverter().convert(orderModel);
	}

	@Override
	protected Set<Object> expandRAO(final OrderRAO rao, final Collection<String> options)
	{
		final LinkedHashSet facts = new LinkedHashSet();
		final Iterator arg4 = options.iterator();

		while (arg4.hasNext())
		{
			final String option = (String) arg4.next();
			switch (option.hashCode())
			{
				default:
					if (option.equals("INCLUDE_ORDER"))
					{
						facts.add(rao);
					}
					break;
			}
		}

		return facts;
	}

	/**
	 * @return the orderRaoConverter
	 */
	public Converter<OrderModel, OrderRAO> getOrderRaoConverter()
	{
		return orderRaoConverter;
	}

	/**
	 * @param orderRaoConverter
	 *           the orderRaoConverter to set
	 */
	public void setOrderRaoConverter(final Converter<OrderModel, OrderRAO> orderRaoConverter)
	{
		this.orderRaoConverter = orderRaoConverter;
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
