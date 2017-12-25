package com.smartedit.personalization.rules.rao.provider;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.ruleengineservices.calculation.RuleEngineCalculationService;
import de.hybris.platform.ruleengineservices.rao.UserRAO;
import de.hybris.platform.ruleengineservices.rao.providers.impl.AbstractExpandedRAOProvider;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;



/**
 *
 */
public class DefaultSegmentRuleCustomerRaoProvider extends AbstractExpandedRAOProvider<UserModel, UserRAO>
{
	private static final String INCLUDE_CUSTOMER = "INCLUDE_CUSTOMER";

	public DefaultSegmentRuleCustomerRaoProvider()
	{
		this.validOptions = Arrays.asList(new String[]
		{ INCLUDE_CUSTOMER });

		this.defaultOptions = Collections.singleton(INCLUDE_CUSTOMER);

		this.minOptions = Arrays.asList(new String[]
		{ INCLUDE_CUSTOMER });
	}

	private Converter<UserModel, UserRAO> customerRaoConverter;
	private RuleEngineCalculationService ruleEngineCalculationService;

	@Override
	protected UserRAO createRAO(final UserModel userModel)
	{
		return this.getCustomerRaoConverter().convert(userModel);
	}

	@Override
	protected Set<Object> expandRAO(final UserRAO rao, final Collection<String> options)
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
	public Set<?> expandFactModel(final UserModel modelFact)
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
	public Converter<UserModel, UserRAO> getCustomerRaoConverter()
	{
		return customerRaoConverter;
	}

	/**
	 * @param customerRaoConverter
	 *           the customerRaoConverter to set
	 */
	public void setCustomerRaoConverter(final Converter<UserModel, UserRAO> customerRaoConverter)
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
