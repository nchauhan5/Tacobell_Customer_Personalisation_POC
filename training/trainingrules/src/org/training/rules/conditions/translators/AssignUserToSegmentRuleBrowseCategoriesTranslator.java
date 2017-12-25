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
package org.training.rules.conditions.translators;

import de.hybris.platform.ruleengineservices.compiler.RuleCompilerContext;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerException;
import de.hybris.platform.ruleengineservices.compiler.RuleConditionTranslator;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeOperator;
import de.hybris.platform.ruleengineservices.compiler.RuleIrCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrFalseCondition;
import de.hybris.platform.ruleengineservices.rao.CategoryRAO;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionDefinitionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleParameterData;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;


/**
 *
 */
public class AssignUserToSegmentRuleBrowseCategoriesTranslator implements RuleConditionTranslator
{

	public static final String CATEGORIES_PARAM = "categories";

	@Override
	public RuleIrCondition translate(final RuleCompilerContext context, final RuleConditionData condition,
			final RuleConditionDefinitionData conditionDefinition) throws RuleCompilerException
	{
		final RuleParameterData categoriesParameter = condition.getParameters().get(CATEGORIES_PARAM);

		if (categoriesParameter == null)
		{
			return new RuleIrFalseCondition();
		}
		final List categories = (List) categoriesParameter.getValue();

		if (CollectionUtils.isEmpty(categories))
		{
			return new RuleIrFalseCondition();
		}

		final String categoryRaoVariable = context.generateVariable(CategoryRAO.class);

		final RuleIrAttributeCondition irCategoryCondition = new RuleIrAttributeCondition();
		irCategoryCondition.setVariable(categoryRaoVariable);
		irCategoryCondition.setAttribute("code");
		irCategoryCondition.setOperator(RuleIrAttributeOperator.IN);
		irCategoryCondition.setValue(categories);

		return irCategoryCondition;
	}
}
