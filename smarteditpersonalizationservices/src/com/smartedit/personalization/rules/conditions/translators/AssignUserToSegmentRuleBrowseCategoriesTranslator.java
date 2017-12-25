package com.smartedit.personalization.rules.conditions.translators;

import de.hybris.platform.ruleengineservices.compiler.RuleCompilerContext;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerException;
import de.hybris.platform.ruleengineservices.compiler.RuleConditionTranslator;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeOperator;
import de.hybris.platform.ruleengineservices.compiler.RuleIrCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrFalseCondition;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionDefinitionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleParameterData;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.smartedit.personalization.data.BrowsedCategoryRAO;


/**
 * The Class AssignUserToSegmentRuleBrowseCategoriesTranslator converts the condition definition to IR - Intermediate
 * Representation for drools engine.
 */
public class AssignUserToSegmentRuleBrowseCategoriesTranslator implements RuleConditionTranslator
{

	public static final String CATEGORIES_PARAM = "categories";

	/**
	 * Translate method converts the condition definition to IR - Intermediate Representation for drools engine. In this
	 * case it is checked if the category browsed by the user lies in a list of categories mentioned in the categories
	 * mentioned as segment source rule condition from backoffice.
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

		final String categoryRaoVariable = context.generateVariable(BrowsedCategoryRAO.class);

		final RuleIrAttributeCondition irCategoryCondition = new RuleIrAttributeCondition();
		irCategoryCondition.setVariable(categoryRaoVariable);
		irCategoryCondition.setAttribute("code");
		irCategoryCondition.setOperator(RuleIrAttributeOperator.IN);
		irCategoryCondition.setValue(categories);

		return irCategoryCondition;
	}
}
