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

import de.hybris.platform.util.Config;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;


/**
 * A Spring MVC HandlerInterceptor which will enforce CSRF token validity on posts requests.
 */
public class CSRFHandlerInterceptor extends HandlerInterceptorAdapter
{
	private static final String CSRF_ALLOWED_URLS = "csrf.allowed.url.patterns";

	/**
	 * Set of servlet paths that should be allowed to go through without CSRF validation. Usually that means those paths
	 * are already trusted (e.g. a rest call that is already validated by another token).
	 */
	private List<String> csrfAllowedUrlPatterns;

	protected boolean shouldCheckCSRFTokenForRequest(final HttpServletRequest request)
	{
		return ("POST").equalsIgnoreCase(request.getMethod()) && !isCSRFExemptUrl(request.getServletPath());
	}

	protected boolean isCSRFExemptUrl(final String servletPath)
	{
		if (servletPath != null)
		{
			final String allowedUrlPatterns = Config.getParameter(CSRF_ALLOWED_URLS);
			final Set<String> allowedUrls = StringUtils.commaDelimitedListToSet(allowedUrlPatterns);
			if (CollectionUtils.isNotEmpty(csrfAllowedUrlPatterns))
			{
				allowedUrls.addAll(csrfAllowedUrlPatterns);
			}
			for (final String pattern : allowedUrls)
			{
				if (servletPath.matches(pattern))
				{
					return true;
				}
			}
		}
		return false;
	}


	@Override
	public boolean preHandle(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse,
			final Object handler) throws Exception
	{
		if (shouldCheckCSRFTokenForRequest(httpServletRequest))
		{
			// httpServletRequest is POST and CSRF token validation is need for the given servelet path
			final String sessionCsrfToken = CSRFTokenManager.getTokenForSession(httpServletRequest.getSession());
			final String requestCsrfToken = CSRFTokenManager.getTokenFromRequest(httpServletRequest);
			if (sessionCsrfToken.equals(requestCsrfToken))
			{
				return true;
			}
			else
			{
				httpServletResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "CSRF token validation failure");
				return false;
			}
		}
		else
		{
			// httpServletRequest doesn't need CSRF token validation
			return true;
		}
	}

	public List<String> getCsrfAllowedUrlPatterns()
	{
		return csrfAllowedUrlPatterns;
	}

	public void setCsrfAllowedUrlPatterns(final List<String> csrfAllowedUrlPatterns)
	{
		this.csrfAllowedUrlPatterns = csrfAllowedUrlPatterns;
	}
}
