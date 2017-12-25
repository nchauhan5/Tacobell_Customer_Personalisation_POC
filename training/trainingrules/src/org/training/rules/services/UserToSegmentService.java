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
package org.training.rules.services;

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.personalizationservices.model.CxUserToSegmentModel;

import java.util.List;


/**
 *
 */
public interface UserToSegmentService
{

	/**
	 * Removes the segments for user.
	 *
	 * @param user
	 *           the user
	 */
	public void removeSegmentsForUser(final UserModel user);



	/**
	 * Gets the segments for user based on duration.
	 *
	 * @return the segments for user based on duration
	 */
	public List<CxUserToSegmentModel> getSegmentsForUserBasedOnDuration();

}
