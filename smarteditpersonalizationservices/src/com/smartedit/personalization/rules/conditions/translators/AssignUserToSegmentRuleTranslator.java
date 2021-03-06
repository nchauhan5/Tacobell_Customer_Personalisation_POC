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
import de.hybris.platform.ruleengineservices.rao.UserRAO;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionDefinitionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleParameterData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


/**
 * The Class AssignUserToSegmentRuleTranslator converts the condition definition to IR - Intermediate Representation for
 * drools engine.
 *
 */
public class AssignUserToSegmentRuleTranslator implements RuleConditionTranslator
{

	/**
	 *
	 */
	private static final String _OPERATOR = "_operator";
	public static final String OPERATOR_PARAM = "operator";
	public static final String GENDER_PARAM = "gender";
	public static final String ELIGIBLE_AGE_PARAM = "eligibleage";
	public static final String CUSTOMER_RAO_AGE_ATTR = "age";
	public static final String CUSTOMER_RAO_GENDER_ATTR = "gender";


	/**
	 * Translate method converts the condition definition to IR - Intermediate Representation for drools engine. In this
	 * case it is checked if the user has age greater than the value mentioned in the age parameter associated with a
	 * segment source rule condition from backoffice.
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

		final Map<String, RuleParameterData> parameterMap = condition.getParameters();
		final String customerRaoVariable = context.generateVariable(UserRAO.class);
		final List<RuleIrCondition> irConditions = new ArrayList<>();

		for (final Entry<String, RuleParameterData> entry : parameterMap.entrySet())
		{
			if (!entry.getKey().endsWith(_OPERATOR))
			{
				final RuleParameterData ruleParameterData = entry.getValue();
				final RuleParameterData operatorParameterData = parameterMap.get(entry.getKey() + _OPERATOR);
				final Object parameterValue = ruleParameterData.getValue();
				final AmountOperator operator = (AmountOperator) operatorParameterData.getValue();

				if (parameterValue == null || operator == null)
				{
					return new RuleIrFalseCondition();
				}


				final RuleIrAttributeCondition irCustomerAge = new RuleIrAttributeCondition();
				irCustomerAge.setVariable(customerRaoVariable);
				irCustomerAge.setAttribute(entry.getKey());
				irCustomerAge.setOperator(RuleIrAttributeOperator.valueOf(operator.name()));
				irCustomerAge.setValue(parameterValue);
				irConditions.add(irCustomerAge);
			}
		}

		final RuleIrGroupCondition irCurrencyGroupCondition = new RuleIrGroupCondition();
		irCurrencyGroupCondition.setOperator(RuleIrGroupOperator.AND);
		irCurrencyGroupCondition.setChildren(irConditions);

		return irCurrencyGroupCondition;
	}
}
