/**
 *
 */
package org.training.rules.cronjobs;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.personalizationservices.model.CxUserToSegmentModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.training.rules.services.UserToSegmentService;


/**
 *
 */
public class RemoveUserFromSegmentsJob extends AbstractJobPerformable<CronJobModel>
{
	private static final Logger LOGGER = LoggerFactory.getLogger(RemoveUserFromSegmentsJob.class);

	@Autowired
	private UserToSegmentService removeUserFromSegmentService;

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable#perform(de.hybris.platform.cronjob.model.
	 * CronJobModel)
	 */
	@Override
	public PerformResult perform(final CronJobModel arg0)
	{
		try
		{
			final List<CxUserToSegmentModel> cxUserToSegmentModels = removeUserFromSegmentService
					.getSegmentsForUserBasedOnDuration();
			modelService.removeAll(cxUserToSegmentModels);
			LOGGER.info("Cronjob finished successfully.......");
			return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
		}
		catch (final Exception e)
		{
			LOGGER.error("Cronjob failed. Could not remove users from segments..........", e);
			return new PerformResult(CronJobResult.FAILURE, CronJobStatus.ABORTED);
		}
	}

}
