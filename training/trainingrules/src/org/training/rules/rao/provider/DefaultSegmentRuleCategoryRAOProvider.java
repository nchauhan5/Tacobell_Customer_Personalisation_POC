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

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.ruleengineservices.calculation.RuleEngineCalculationService;
import de.hybris.platform.ruleengineservices.rao.CategoryRAO;
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
public class DefaultSegmentRuleCategoryRAOProvider extends AbstractExpandedRAOProvider<CategoryModel, CategoryRAO>
{
	public static final String INCLUDE_CATEGORY = "INCLUDE_CATEGORY";
	private final Collection<String> validOptions = Arrays.asList(new String[]
	{ "INCLUDE_CATEGORY" });
	private final Collection<String> defaultOptions = Collections.singleton("INCLUDE_CATEGORY");
	private final Collection<String> minOptions = Arrays.asList(new String[]
	{ "INCLUDE_CATEGORY" });

	private Converter<CategoryModel, CategoryRAO> categoryRaoConverter;
	private RuleEngineCalculationService ruleEngineCalculationService;

	@Override
	protected CategoryRAO createRAO(final CategoryModel categoryModel)
	{
		return this.getCategoryRaoConverter().convert(categoryModel);
	}

	@Override
	protected Set<Object> expandRAO(final CategoryRAO rao, final Collection<String> options)
	{
		final LinkedHashSet facts = new LinkedHashSet();
		final Iterator arg4 = options.iterator();

		while (arg4.hasNext())
		{
			final String option = (String) arg4.next();
			switch (option.hashCode())
			{
				default:
					if (option.equals("INCLUDE_CATEGORY"))
					{
						facts.add(rao);
					}
					break;
			}
		}

		return facts;
	}


	/**
	 * @return the categoryRaoConverter
	 */
	public Converter<CategoryModel, CategoryRAO> getCategoryRaoConverter()
	{
		return categoryRaoConverter;
	}

	/**
	 * @param categoryRaoConverter
	 *           the categoryRaoConverter to set
	 */
	public void setCategoryRaoConverter(final Converter<CategoryModel, CategoryRAO> categoryRaoConverter)
	{
		this.categoryRaoConverter = categoryRaoConverter;
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
