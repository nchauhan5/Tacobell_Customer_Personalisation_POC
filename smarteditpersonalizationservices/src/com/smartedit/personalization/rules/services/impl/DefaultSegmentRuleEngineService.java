package com.smartedit.personalization.rules.services.impl;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.droolsruleengineservices.impl.DefaultCommerceRuleEngineService;
import de.hybris.platform.ruleengine.RuleEvaluationContext;
import de.hybris.platform.ruleengine.RuleEvaluationResult;
import de.hybris.platform.ruleengine.dao.RuleEngineContextDao;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineContextModel;
import de.hybris.platform.ruleengineservices.enums.FactContextType;
import de.hybris.platform.ruleengineservices.rao.providers.FactContextFactory;
import de.hybris.platform.ruleengineservices.rao.providers.RAOProvider;
import de.hybris.platform.ruleengineservices.rao.providers.impl.FactContext;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.internal.service.AbstractBusinessService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartedit.personalization.rules.services.SegmentRuleEngineService;


/**
 *
 */
public class DefaultSegmentRuleEngineService extends AbstractBusinessService implements SegmentRuleEngineService
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSegmentRuleEngineService.class);

	private DefaultCommerceRuleEngineService commerceRuleEngineService;
	private RuleEngineContextDao ruleEngineContextDao;
	private String defaultRuleEngineContextName;
	private FactContextFactory factContextFactory;

	@Override
	public RuleEvaluationResult evaluate(final CustomerModel customerModel)
	{
		LOGGER.debug("Entering->evaluate");
		final List<Object> facts = new ArrayList<Object>();
		facts.add(customerModel);
		final FactContext factContext = factContextFactory.createFactContext(FactContextType.USER_SEGMENT, facts);
		final RuleEvaluationContext context = prepareContext(factContext);
		final RuleEvaluationResult evalResult = commerceRuleEngineService.evaluate(context);
		LOGGER.debug("Exiting->evaluate");
		return evalResult;
	}

	@Override
	public RuleEvaluationResult evaluate(final CartModel cartModel)
	{
		LOGGER.debug("Entering->evaluate");
		final List<Object> facts = new ArrayList<Object>();
		facts.add(cartModel);
		final FactContext factContext = factContextFactory.createFactContext(FactContextType.USER_SEGMENT, facts);
		final RuleEvaluationContext context = prepareContext(factContext);
		final RuleEvaluationResult evalResult = commerceRuleEngineService.evaluate(context);
		LOGGER.debug("Exiting->evaluate");
		return evalResult;
	}

	@Override
	public RuleEvaluationResult evaluate(final CategoryModel categoryModel, final CartModel cartModel)
	{
		LOGGER.debug("Entering->evaluate");
		final List<Object> facts = new ArrayList<Object>();
		facts.add(categoryModel);
		facts.add(cartModel);
		final FactContext factContext = factContextFactory.createFactContext(FactContextType.USER_SEGMENT, facts);
		final RuleEvaluationContext context = prepareContext(factContext);
		final RuleEvaluationResult evalResult = commerceRuleEngineService.evaluate(context);
		LOGGER.debug("Exiting->evaluate");
		return evalResult;
	}

	@Override
	public RuleEvaluationResult evaluate(final OrderModel orderModel)
	{
		LOGGER.debug("Entering->evaluate");
		final List<Object> facts = new ArrayList<Object>();
		facts.add(orderModel);
		final FactContext factContext = factContextFactory.createFactContext(FactContextType.USER_SEGMENT, facts);
		final RuleEvaluationContext context = prepareContext(factContext);
		final RuleEvaluationResult evalResult = commerceRuleEngineService.evaluate(context);
		LOGGER.debug("Exiting->evaluate");
		return evalResult;
	}

	/**
	 * This method prepare the Fact Context required for rule execution and convert all the facts required for rules.
	 *
	 * @param factContext
	 *           the fact context
	 * @return the rule evaluation context
	 */
	private RuleEvaluationContext prepareContext(final FactContext factContext)
	{
		final Set<Object> convertedFacts = provideRAOs(factContext);
		final RuleEvaluationContext evaluationContext = new RuleEvaluationContext();
		try
		{
			final AbstractRuleEngineContextModel engineContext = ruleEngineContextDao
					.getRuleEngineContextByName(defaultRuleEngineContextName);
			evaluationContext.setRuleEngineContext(engineContext);
			evaluationContext.setFacts(convertedFacts);
			LOGGER.debug("Exiting->prepareContext");
			return evaluationContext;
		}
		catch (final ModelNotFoundException | AmbiguousIdentifierException exp)
		{
			LOGGER.error(String.format("RuleEngineContext with name %s not found", defaultRuleEngineContextName));
			LOGGER.debug("Exiting->prepareContext");
			throw exp;
		}
	}

	/**
	 * This method uses models and convert them RAOss
	 *
	 * @param factContext
	 * @return set of RAOs
	 */
	private Set<Object> provideRAOs(final FactContext factContext)
	{
		final Set<Object> result = new HashSet<Object>();
		for (final Object fact : factContext.getFacts())
		{
			for (final RAOProvider raoProvider : factContext.getProviders(fact))
			{
				result.addAll(raoProvider.expandFactModel(fact));
			}
		}
		return result;
	}

	/**
	 * @param commerceRuleEngineService
	 *           the commerceRuleEngineService to set
	 */
	public void setCommerceRuleEngineService(final DefaultCommerceRuleEngineService commerceRuleEngineService)
	{
		this.commerceRuleEngineService = commerceRuleEngineService;
	}

	/**
	 * @param ruleEngineContextDao
	 *           the ruleEngineContextDao to set
	 */
	public void setRuleEngineContextDao(final RuleEngineContextDao ruleEngineContextDao)
	{
		this.ruleEngineContextDao = ruleEngineContextDao;
	}

	/**
	 * @param defaultRuleEngineContextName
	 *           the defaultRuleEngineContextName to set
	 */
	public void setDefaultRuleEngineContextName(final String defaultRuleEngineContextName)
	{
		this.defaultRuleEngineContextName = defaultRuleEngineContextName;
	}


	/**
	 * @param factContextFactory
	 *           the factContextFactory to set
	 */
	public void setFactContextFactory(final FactContextFactory factContextFactory)
	{
		this.factContextFactory = factContextFactory;
	}

}
