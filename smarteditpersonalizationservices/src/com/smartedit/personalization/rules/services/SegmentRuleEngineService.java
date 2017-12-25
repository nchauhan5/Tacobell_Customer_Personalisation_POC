package com.smartedit.personalization.rules.services;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.ruleengine.RuleEvaluationResult;


public interface SegmentRuleEngineService
{

	/**
	 * This method evaluates if a particular CustomerModel converted to UserRAO object has the field values as expected
	 * in the SegmentSourceRuleCondition.
	 *
	 * @param customerModel
	 *           the customer model
	 * @return the rule evaluation result
	 */
	RuleEvaluationResult evaluate(final CustomerModel customerModel);

	/**
	 * This method evaluates if a particular CartModel converted to CartRAO has the field values as expected in the
	 * SegmentSourceRuleCondition.
	 *
	 * @param cartModel
	 *           must be added to open recipe.
	 * @return Drool Rule Evaluation result
	 */
	RuleEvaluationResult evaluate(final CartModel cartModel);

	/**
	 * This method evaluates if a particular CartModel converted to CartRAO and CategoryModel converted to CategoryRAO
	 * have the field values as expected in the SegmentSourceRuleCondition.
	 *
	 * @param cartModel
	 *           must be added to open recipe.
	 * @return Drool Rule Evaluation result
	 */
	RuleEvaluationResult evaluate(final CategoryModel categoryModel, final CartModel cartModel);


	/**
	 * This method evaluates if a particular OrderModel converted to OrderRAO have the field values as expected in the
	 * SegmentSourceRuleCondition.
	 *
	 * @param orderModel
	 *           the order model
	 * @return the rule evaluation result
	 */
	RuleEvaluationResult evaluate(final OrderModel orderModel);
}
