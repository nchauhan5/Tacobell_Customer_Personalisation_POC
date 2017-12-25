package com.smartedit.personalization.rules.conditions.translators;

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
import de.hybris.platform.ruleengineservices.rao.OrderRAO;
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
 * The Class AssignUserToSegmentRuleOrderValueGreaterThanTranslator converts the condition definition to IR -
 * Intermediate Representation for drools engine.
 */
public class AssignUserToSegmentRuleOrderValueGreaterThanTranslator implements RuleConditionTranslator
{

	public static final String OPERATOR_PARAM = "operator";
	public static final String VALUE_PARAM = "value";
	public static final String CART_RAO_CURRENCY_ATTRIBUTE = "currencyIsoCode";
	public static final String CART_RAO_TOTAL_ATTRIBUTE = "total";

	/**
	 * Translate method converts the condition definition to IR - Intermediate Representation for drools engine. In this
	 * case it is checked if the order placed by the user has value greater than the amount mentioned in the categories
	 * parameter associated with a segment source rule condition from backoffice.
	 *
	 * @param context
	 *           the context
	 * @param condition
	 *           the condition
	 * @param conditionDefinition
	 *           the condition definition
	 * @return the rule ir condition
	 * @throws RuleCompilerException
	 *            the rule compiler exception
	 */
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
		final String orderRaoVariable = context.generateVariable(OrderRAO.class);

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
				irCurrencyCondition.setVariable(orderRaoVariable);
				irCurrencyCondition.setAttribute("currencyIsoCode");
				irCurrencyCondition.setOperator(RuleIrAttributeOperator.EQUAL);
				irCurrencyCondition.setValue(entry.getKey());

				final RuleIrAttributeCondition irTotalCondition = new RuleIrAttributeCondition();
				irTotalCondition.setVariable(orderRaoVariable);
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