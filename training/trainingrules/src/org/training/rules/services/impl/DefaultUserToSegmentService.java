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
package org.training.rules.services.impl;

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
import org.training.rules.services.UserToSegmentService;


/**
 *
 */
public class DefaultUserToSegmentService implements UserToSegmentService
{

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultUserToSegmentService.class);

	@Autowired
	private ModelService modelService;

	@Autowired
	private FlexibleSearchService flexibleSearchService;

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
		// modelService.refresh(user);
		//		userToSegments.removeAll(userSegmentRelationsToRemove);
		//		user.setUserToSegments(userToSegments);
		//		modelService.save(user);
	}

	@Override
	public List<CxUserToSegmentModel> getSegmentsForUserBasedOnDuration()
	{
		final String query = "select distinct{cxus.pk} from {CxUserToSegment as cxus join CxSegment as cxs on {cxus.segment} = {cxs.pk}} where DATEDIFF(curdate(),{cxus.creationtime}) > {cxs.durationForAssociation} and {cxus.isSegmentAttachedViaSegmentSourceRule} = 1";

		LOGGER.info(query);

		final SearchResult<CxUserToSegmentModel> cxUserToSegmentModels = flexibleSearchService.<CxUserToSegmentModel> search(query);
		return cxUserToSegmentModels.getResult();
	}

}
