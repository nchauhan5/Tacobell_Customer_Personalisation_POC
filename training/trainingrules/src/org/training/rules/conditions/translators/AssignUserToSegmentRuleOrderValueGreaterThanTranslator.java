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

import de.hybris.platform.ruledefinitions.AmountOperator;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerContext;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerException;
import de.hybris.platform.ruleengineservices.compiler.RuleConditionTranslator;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeOperator;
import de.hybris.platform.ruleengineservices.compiler.RuleIrCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrFalseCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrGroupCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrGroupOperator;
import de.hybris.platform.ruleengineservices.rao.CartRAO;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionDefinitionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleParameterData;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.MapUtils;


/**
 *
 */
public class AssignUserToSegmentRuleOrderValueGreaterThanTranslator implements RuleConditionTranslator
{

	public static final String OPERATOR_PARAM = "operator";
	public static final String VALUE_PARAM = "value";
	public static final String CART_RAO_CURRENCY_ATTRIBUTE = "currencyIsoCode";
	public static final String CART_RAO_TOTAL_ATTRIBUTE = "total";

	@Override
	public RuleIrCondition translate(final RuleCompilerContext context, final RuleConditionData condition,
			final RuleConditionDefinitionData conditionDefinition) throws RuleCompilerException
	{
		final RuleParameterData operatorParameter = condition.getParameters().get(OPERATOR_PARAM);
		final RuleParameterData valueParameter = condition.getParameters().get(VALUE_PARAM);

		if (operatorParameter == null || valueParameter == null)
		{
			return new RuleIrFalseCondition();
		}
		final AmountOperator operator = (AmountOperator) operatorParameter.getValue();
		final Map value = (Map) valueParameter.getValue();

		if (operator == null || MapUtils.isEmpty(value))
		{
			return new RuleIrFalseCondition();
		}

		final RuleIrGroupCondition irCartTotalCondition = new RuleIrGroupCondition();
		irCartTotalCondition.setOperator(RuleIrGroupOperator.OR);
		irCartTotalCondition.setChildren(new ArrayList());

		this.addCartTotalConditions(context, operator, value, irCartTotalCondition);
		return irCartTotalCondition;
	}

	protected void addCartTotalConditions(final RuleCompilerContext context, final AmountOperator operator,
			final Map<String, BigDecimal> value, final RuleIrGroupCondition irCartTotalCondition)
	{
		final String cartRaoVariable = context.generateVariable(CartRAO.class);

		final Iterator currencyValueItr = value.entrySet().iterator();
		while (currencyValueItr.hasNext())
		{
			final Entry entry = (Entry) currencyValueItr.next();
			if (entry.getKey() != null && entry.getValue() != null)
			{

				final RuleIrGroupCondition irCurrencyGroupCondition = new RuleIrGroupCondition();
				irCurrencyGroupCondition.setOperator(RuleIrGroupOperator.AND);
				irCurrencyGroupCondition.setChildren(new ArrayList());

				final RuleIrAttributeCondition irCurrencyCondition = new RuleIrAttributeCondition();
				irCurrencyCondition.setVariable(cartRaoVariable);
				irCurrencyCondition.setAttribute("currencyIsoCode");
				irCurrencyCondition.setOperator(RuleIrAttributeOperator.EQUAL);
				irCurrencyCondition.setValue(entry.getKey());

				final RuleIrAttributeCondition irTotalCondition = new RuleIrAttributeCondition();
				irTotalCondition.setVariable(cartRaoVariable);
				irTotalCondition.setAttribute("total");
				irTotalCondition.setOperator(RuleIrAttributeOperator.valueOf(operator.name()));
				irTotalCondition.setValue(entry.getValue());

				irCurrencyGroupCondition.getChildren().add(irCurrencyCondition);
				irCurrencyGroupCondition.getChildren().add(irTotalCondition);

				irCartTotalCondition.getChildren().add(irCurrencyGroupCondition);
			}
		}

	}

}