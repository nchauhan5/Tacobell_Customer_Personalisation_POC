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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.support.RequestDataValueProcessor;


/**
 * This class is a RequestDataValueProcessor implementation. It provides an extra hidden field in forms. The extra
 * hidden field contains the CSRF token.
 */
public class CSRFRequestDataValueProcessor implements RequestDataValueProcessor
{
	@Override
	public Map<String, String> getExtraHiddenFields(final HttpServletRequest httpServletRequest)
	{
		final Map<String, String> extraHiddenFields = new HashMap<String, String>();
		String sessionCsrfToken = CSRFTokenManager.getTokenForSession(httpServletRequest.getSession());
		extraHiddenFields.put(CSRFTokenManager.CSRF_PARAM_NAME, sessionCsrfToken);
		return extraHiddenFields;
	}

	@Override
	public String processUrl(final HttpServletRequest request, final String url)
	{
		return url;
	}

	@Override
	public String processFormFieldValue(final HttpServletRequest request, final String name, final String value, final String type)
	{
		return value;
	}

	@Override
	public String processAction(final HttpServletRequest request, final String action, final String httpMethod)
	{
		return action;
	}

}
