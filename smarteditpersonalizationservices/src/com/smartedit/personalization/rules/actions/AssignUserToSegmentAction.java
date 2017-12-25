package com.smartedit.personalization.rules.actions;

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.personalizationservices.model.CxSegmentModel;
import de.hybris.platform.personalizationservices.model.CxUserToSegmentModel;
import de.hybris.platform.personalizationservices.segment.CxSegmentService;
import de.hybris.platform.ruleengineservices.compiler.RuleActionValidator;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerContext;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerException;
import de.hybris.platform.ruleengineservices.rao.UserRAO;
import de.hybris.platform.ruleengineservices.rule.data.RuleActionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleActionDefinitionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleParameterData;
import de.hybris.platform.ruleengineservices.rule.data.RuleParameterDefinitionData;
import de.hybris.platform.ruleengineservices.rule.evaluation.RuleActionContext;
import de.hybris.platform.ruleengineservices.rule.evaluation.RuleEvaluationException;
import de.hybris.platform.ruleengineservices.rule.evaluation.RuleExecutableAction;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Action class for GCR Custom Promotion applying the action as per 5GPOS response
 *
 */
public class AssignUserToSegmentAction implements RuleExecutableAction, RuleActionValidator
{

	private static final int _1 = 1;

	private static final Logger LOG = Logger.getLogger(AssignUserToSegmentAction.class);

	private static final String SEGMENT_PARAM = "segment";

	@Autowired
	private SessionService sessionService;

	@Autowired
	private UserService userService;

	@Autowired
	private ModelService modelService;

	@Autowired
	private CxSegmentService cxSegmentService;

	/**
	 * This method evaluates if the action defined for a segment source rule is valid and throws
	 * {@link de.hybris.platform.ruleengineservices.compiler.RuleCompilerException}.
	 *
	 * @param context
	 *           the context
	 * @param action
	 *           the action
	 * @param actionDefinition
	 *           the action definition
	 * @throws RuleCompilerException
	 *            the rule compiler exception
	 */
	@Override
	public void validate(final RuleCompilerContext context, final RuleActionData action,
			final RuleActionDefinitionData actionDefinition) throws RuleCompilerException
	{

		final RuleParameterData segmentParameter = action.getParameters().get(SEGMENT_PARAM);
		final RuleParameterDefinitionData segmentParameterDefinition = actionDefinition.getParameters().get(SEGMENT_PARAM);

		if (null == segmentParameter && null == segmentParameterDefinition)
		{
			LOG.error("Action must have some criteria. Segment cannot be empty");
			throw new RuleCompilerException("Action must have some criteria. Segment cannot be empty");
		}
	}

	/**
	 * Execute action method is called every time a particular condition associated to a SegmentSourceRule is evaluated
	 * to true {@link de.hybris.platform.ruleengineservices.rule.evaluation.RuleEvaluationException}.
	 *
	 * @param context
	 *           the context
	 * @param parameters
	 *           the parameters
	 * @throws RuleEvaluationException
	 *            the rule evaluation exception
	 */
	@Override
	public void executeAction(final RuleActionContext context, final Map<String, Object> parameters) throws RuleEvaluationException
	{
		final UserRAO user = context.getValue(UserRAO.class);

		final String segmentCode = (String) parameters.get("segment");
		final Optional<CxSegmentModel> cxSegmentModelOption = this.cxSegmentService.getSegment(segmentCode);
		final UserModel currentuser = user != null ? userService.getUserForUID(user.getId()) : this.userService.getCurrentUser();

		final CxSegmentModel cxSegmentModel = cxSegmentModelOption.isPresent() ? cxSegmentModelOption.get() : null;

		final Collection<CxSegmentModel> assignedSegments = this.cxSegmentService.getSegmentsFromUser(currentuser);

		if (cxSegmentModel != null && !assignedSegments.contains(cxSegmentModel))
		{
			removeUserFromMutuallyExclusiveSegments(currentuser, cxSegmentModel);
			final CxUserToSegmentModel userToSegment = modelService.create(CxUserToSegmentModel.class);
			userToSegment.setSegment(cxSegmentModel);
			userToSegment.setUser(currentuser);
			userToSegment.setAffinity(new BigDecimal(_1));
			userToSegment.setIsSegmentAttachedViaSegmentSourceRule(Boolean.TRUE);
			modelService.save(userToSegment);
		}
	}

	private void removeUserFromMutuallyExclusiveSegments(final UserModel currentuser, final CxSegmentModel cxSegmentModel)
	{
		final Set<CxSegmentModel> mutuallyExlusiveSegments = cxSegmentModel.getMutuallyExlusiveSegments();
		if (CollectionUtils.isNotEmpty(mutuallyExlusiveSegments))
		{
			final Set<CxUserToSegmentModel> userToSegments = new HashSet<>(currentuser.getUserToSegments());
			final Set<CxUserToSegmentModel> userSegmentRelationsToRemove = new HashSet<>();

			for (final CxUserToSegmentModel cxUserToSegmentModel : userToSegments)
			{
				if (mutuallyExlusiveSegments.contains(cxUserToSegmentModel.getSegment()))
				{
					userSegmentRelationsToRemove.add(cxUserToSegmentModel);
				}
			}

			modelService.removeAll(userSegmentRelationsToRemove);
			modelService.refresh(currentuser);
		}
	}

	/**
	 * @return the sessionService
	 */
	public SessionService getSessionService()
	{
		return sessionService;
	}

	/**
	 * @param sessionService
	 *           the sessionService to set
	 */
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	/**
	 * @return the userService
	 */
	public UserService getUserService()
	{
		return userService;
	}

	/**
	 * @param userService
	 *           the userService to set
	 */
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

}
