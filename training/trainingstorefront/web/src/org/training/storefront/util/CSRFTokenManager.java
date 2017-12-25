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
package org.training.storefront.util;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;


/**
 * This class manages the CSRF token for either a given session or request.
 */
public final class CSRFTokenManager
{
	private static final String CSRF_TOKEN_SESSION_ATTRIBUTE = CSRFTokenManager.class.getName() + ".tokenval";
	public static final String CSRF_PARAM_NAME = "CSRFToken";

	private CSRFTokenManager()
	{
		// The constructor is intentionally empty.
	}

	/**
	 * Returns the CSRF token from the httpServletRequest.
	 *
	 * @param httpServletRequest
	 *           the httpServletRequest to retrieve CSRF token from
	 * @return the CSRF token
	 */
	public static String getTokenFromRequest(final HttpServletRequest httpServletRequest)
	{
		final String requestCsrfToken = httpServletRequest.getParameter(CSRF_PARAM_NAME);

		if (requestCsrfToken == null)
		{
			return httpServletRequest.getHeader(CSRF_PARAM_NAME);
		}
		else
		{
			return requestCsrfToken;
		}
	}

	/**
	 * Generates a new token.
	 *
	 * @return The new token.
	 */
	protected static String generateToken()
	{
		return UUID.randomUUID().toString();
	}

	/**
	 * Returns the CSRF token for the provided httpSession.
	 *
	 * @param httpSession
	 * @return the CSRF token
	 */
	public static String getTokenForSession(final HttpSession httpSession)
	{
		String sessionCsrfToken = null;
		synchronized (httpSession)
		{
			sessionCsrfToken = (String) httpSession.getAttribute(CSRF_TOKEN_SESSION_ATTRIBUTE);
			if (StringUtils.isBlank(sessionCsrfToken))
			{
				sessionCsrfToken = generateToken();
				httpSession.setAttribute(CSRF_TOKEN_SESSION_ATTRIBUTE, sessionCsrfToken);
			}
		}
		return sessionCsrfToken;
	}
}
