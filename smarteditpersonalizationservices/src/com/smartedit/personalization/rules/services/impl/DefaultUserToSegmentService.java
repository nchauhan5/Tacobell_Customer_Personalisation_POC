package com.smartedit.personalization.rules.services.impl;

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.personalizationservices.model.CxUserToSegmentModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.smartedit.personalization.rules.services.UserToSegmentService;


/**
 * The Class DefaultUserToSegmentService.
 */
public class DefaultUserToSegmentService implements UserToSegmentService
{

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultUserToSegmentService.class);

	@Autowired
	private ModelService modelService;

	@Autowired
	private FlexibleSearchService flexibleSearchService;

	/**
	 * Removes the segments associated with the provided user.
	 *
	 * @param user
	 *           the user
	 */
	@Override
	public void removeSegmentsForUser(final UserModel user)
	{
		final Set<CxUserToSegmentModel> userToSegments = new HashSet<>(user.getUserToSegments());
		final Set<CxUserToSegmentModel> userSegmentRelationsToRemove = new HashSet<>();

		for (final CxUserToSegmentModel cxUserToSegmentModel : userToSegments)
		{
			if (Boolean.TRUE.equals(cxUserToSegmentModel.getIsSegmentAttachedViaSegmentSourceRule()))
			{
				userSegmentRelationsToRemove.add(cxUserToSegmentModel);
			}
		}
		modelService.removeAll(userSegmentRelationsToRemove);
	}

	/**
	 * Gets the segments for user that have been associated with that user for a particular duration of time (days).
	 *
	 * @return the segments for user based on duration
	 */
	@Override
	public List<CxUserToSegmentModel> getSegmentsForUserBasedOnDuration()
	{
		final String query = "select distinct{cxus.pk} from {CxUserToSegment as cxus join CxSegment as cxs on {cxus.segment} = {cxs.pk}} where DATEDIFF(curdate(),{cxus.creationtime}) > {cxs.durationForAssociation} and {cxus.isSegmentAttachedViaSegmentSourceRule} = 1";

		LOGGER.info(query);

		final SearchResult<CxUserToSegmentModel> cxUserToSegmentModels = flexibleSearchService.<CxUserToSegmentModel> search(query);
		return cxUserToSegmentModels.getResult();
	}

}
