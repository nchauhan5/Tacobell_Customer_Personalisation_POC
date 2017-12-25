package org.training.rules.services;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.ruleengine.RuleEvaluationResult;


public interface SegmentRuleEngineService
{
	/**
	 * This method will run all the rules configured for CSM Recipe for given cart.
	 *
	 * @param cartModel
	 *           must be added to open recipe.
	 * @return Drool Rule Evaluation result
	 */
	RuleEvaluationResult evaluate(final CustomerModel customerModel);

	/**
	 * This method will run all the rules configured for CSM Recipe for given cart.
	 *
	 * @param cartModel
	 *           must be added to open recipe.
	 * @return Drool Rule Evaluation result
	 */
	RuleEvaluationResult evaluate(final CartModel cartModel);

	/**
	 * This method will run all the rules configured for CSM Recipe for given cart.
	 *
	 * @param cartModel
	 *           must be added to open recipe.
	 * @return Drool Rule Evaluation result
	 */
	RuleEvaluationResult evaluate(final CategoryModel categoryModel, final CartModel cartModel);

}
