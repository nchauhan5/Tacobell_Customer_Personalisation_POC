package com.smartedit.personalization.rules.services;

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.personalizationservices.model.CxUserToSegmentModel;

import java.util.List;



/**
 * The Interface UserToSegmentService.
 */
public interface UserToSegmentService
{

	/**
	 * Removes the segments associated with the provided user.
	 *
	 * @param user
	 *           the user
	 */
	public void removeSegmentsForUser(final UserModel user);

	/**
	 * Gets the segments for user that have been associated with that user for a particular duration of time (days).
	 *
	 * @return the segments for user based on duration
	 */
	public List<CxUserToSegmentModel> getSegmentsForUserBasedOnDuration();

}
