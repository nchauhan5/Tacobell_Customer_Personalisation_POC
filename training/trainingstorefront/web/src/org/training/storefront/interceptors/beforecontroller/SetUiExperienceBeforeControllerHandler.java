/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package org.training.storefront.interceptors.beforecontroller;

import de.hybris.platform.acceleratorservices.uiexperience.UiExperienceService;
import de.hybris.platform.acceleratorstorefrontcommons.interceptors.BeforeControllerHandler;
import de.hybris.platform.commerceservices.enums.UiExperienceLevel;
import de.hybris.platform.commerceservices.util.ResponsiveUtils;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;


/**
 * Allows to overwrite the UiExperience level in the session via
 * de.hybris.platform.acceleratorservices.uiexperience.UiExperienceService#setOverrideUiExperienceLevel
 */
public class SetUiExperienceBeforeControllerHandler implements BeforeControllerHandler
{
	private static final Logger LOG = Logger.getLogger(SetLanguageBeforeControllerHandler.class);

	public static final String DEFAULT_UI_EXPERIENCE_LEVEL_PARAM = "uiel";

	@Resource(name = "uiExperienceService")
	private UiExperienceService uiExperienceService;

	@Resource(name = "enumerationService")
	private EnumerationService enumerationService;

	@Override
	public boolean beforeController(final HttpServletRequest request, final HttpServletResponse response,
			final HandlerMethod handler)
	{
		if (isGetMethod(request))
		{
			final String uiExperienceLevelParam = request.getParameter(DEFAULT_UI_EXPERIENCE_LEVEL_PARAM);
			if (StringUtils.isNotBlank(uiExperienceLevelParam) && !ResponsiveUtils.isResponsive())
			{
				try
				{
					final UiExperienceLevel uiExperienceLevel = enumerationService.getEnumerationValue(UiExperienceLevel.class,
							uiExperienceLevelParam);
					uiExperienceService.setOverrideUiExperienceLevel(uiExperienceLevel);

				}
				catch (final UnknownIdentifierException ile)
				{
					LOG.warn("Can not change uiExperienceLevel [" + uiExperienceLevelParam + "]. " + ile.getMessage());
					if (LOG.isDebugEnabled())
					{
						LOG.debug("Exception changing UiExperienceLevel", ile);
					}
				}
			}
		}
		return true;
	}

	protected boolean isGetMethod(final HttpServletRequest request)
	{
		return RequestMethod.GET.name().equalsIgnoreCase(request.getMethod());
	}
}
