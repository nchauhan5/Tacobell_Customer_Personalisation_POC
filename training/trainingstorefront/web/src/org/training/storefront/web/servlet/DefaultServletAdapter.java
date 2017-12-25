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
package org.training.storefront.web.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;

public class DefaultServletAdapter extends HttpServlet
{
	private static final String COMMON_DEFAULT_SERVLET_NAME = "default";
	private static final String GAE_DEFAULT_SERVLET_NAME = "_ah_default";
	private static final String RESIN_DEFAULT_SERVLET_NAME = "resin-file";
	private static final String WEBLOGIC_DEFAULT_SERVLET_NAME = "FileServlet";
	private static final String WEBSPHERE_DEFAULT_SERVLET_NAME = "SimpleFileServlet";

	@Override
	public void service(final ServletRequest arg0, final ServletResponse arg1) throws ServletException, IOException
	{
		final RequestDispatcher requestDispatcher;

		if (getServletContext().getNamedDispatcher(COMMON_DEFAULT_SERVLET_NAME) != null)
		{
			requestDispatcher = getServletContext().getNamedDispatcher(COMMON_DEFAULT_SERVLET_NAME);
		}
		else if (getServletContext().getNamedDispatcher(GAE_DEFAULT_SERVLET_NAME) != null)
		{
			requestDispatcher = getServletContext().getNamedDispatcher(GAE_DEFAULT_SERVLET_NAME);
		}
		else if (getServletContext().getNamedDispatcher(RESIN_DEFAULT_SERVLET_NAME) != null)
		{
			requestDispatcher = getServletContext().getNamedDispatcher(RESIN_DEFAULT_SERVLET_NAME);
		}
		else if (getServletContext().getNamedDispatcher(WEBLOGIC_DEFAULT_SERVLET_NAME) != null)
		{
			requestDispatcher = getServletContext().getNamedDispatcher(WEBLOGIC_DEFAULT_SERVLET_NAME);
		}
		else if (getServletContext().getNamedDispatcher(WEBSPHERE_DEFAULT_SERVLET_NAME) != null)
		{
			requestDispatcher = getServletContext().getNamedDispatcher(WEBSPHERE_DEFAULT_SERVLET_NAME);
		}
		else
		{
			throw new IllegalStateException("Unable to locate the default servlet for serving static content.");
		}

		requestDispatcher.forward(arg0, arg1);
	}

}
