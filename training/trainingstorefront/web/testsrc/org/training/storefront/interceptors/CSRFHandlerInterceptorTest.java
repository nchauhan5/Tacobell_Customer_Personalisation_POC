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
package org.training.storefront.interceptors;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.util.Config;
import org.training.storefront.util.CSRFHandlerInterceptor;
import org.training.storefront.util.CSRFTokenManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import junit.framework.Assert;


@IntegrationTest
public class CSRFHandlerInterceptorTest extends ServicelayerTest
{
	private static final String CSRF_URL_PROPERTY = "csrf.allowed.url.patterns";
	private static final String SESSION_CSRF_ATTRIBUTE = "org.training.storefront.util.CSRFTokenManager.tokenval";

	@InjectMocks
	private final CSRFHandlerInterceptor csrfHandlerInterceptor = new CSRFHandlerInterceptor();

	@Mock
	private HttpServletRequest request;
	@Mock
	private HttpServletResponse response;

	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);
	}


	@Test
	public void shouldNotCheckWithNonPostRequest() throws Exception
	{
		BDDMockito.given(request.getMethod()).willReturn("GET");
		boolean verified = csrfHandlerInterceptor.preHandle(request, response, null);
		Assert.assertEquals(true, verified);

		BDDMockito.given(request.getMethod()).willReturn("PUT");
		verified = csrfHandlerInterceptor.preHandle(request, response, null);
		Assert.assertEquals(true, verified);

		BDDMockito.given(request.getMethod()).willReturn("DELETE");
		verified = csrfHandlerInterceptor.preHandle(request, response, null);

		Assert.assertEquals(true, verified);
	}

	@Test
	public void shouldCheckWithPostRequest() throws Exception
	{
		final HttpSession session = Mockito.mock(HttpSession.class);
		BDDMockito.given(session.getAttribute(SESSION_CSRF_ATTRIBUTE)).willReturn("123");
		BDDMockito.given(request.getMethod()).willReturn("POST");
		BDDMockito.given(request.getSession()).willReturn(session);
		BDDMockito.given(request.getParameter(CSRFTokenManager.CSRF_PARAM_NAME)).willReturn("123");
		final boolean verified = csrfHandlerInterceptor.preHandle(request, response, null);

		Assert.assertEquals(true, verified);
	}

	@Test
	public void shouldErrorOnMismatchTokens() throws Exception
	{
		final HttpSession session = Mockito.mock(HttpSession.class);
		BDDMockito.given(session.getAttribute(SESSION_CSRF_ATTRIBUTE)).willReturn("1234");
		BDDMockito.given(request.getMethod()).willReturn("POST");
		BDDMockito.given(request.getSession()).willReturn(session);
		BDDMockito.given(request.getParameter(CSRFTokenManager.CSRF_PARAM_NAME)).willReturn("123");
		final boolean verified = csrfHandlerInterceptor.preHandle(request, response, null);

		verify(response, times(1)).sendError(HttpServletResponse.SC_FORBIDDEN, "CSRF token validation failure");
		Assert.assertEquals(false, verified);
	}

	@Test
	public void shouldPassOnExemptUrl() throws Exception
	{
		final String originalValues = Config.getParameter(CSRF_URL_PROPERTY);

		try
		{
			Config.setParameter(CSRF_URL_PROPERTY, "/[^/]+(/[^?]*)+(sop-response)$");
			final HttpSession session = Mockito.mock(HttpSession.class);
			BDDMockito.given(session.getAttribute(SESSION_CSRF_ATTRIBUTE)).willReturn("1234");
			// Mismatch tokens
			BDDMockito.given(request.getParameter(CSRFTokenManager.CSRF_PARAM_NAME)).willReturn("123");
			BDDMockito.given(request.getMethod()).willReturn("POST");
			BDDMockito.given(request.getSession()).willReturn(session);

			BDDMockito.given(request.getServletPath()).willReturn("/checkout/multi/sop-response");
			final boolean verified = csrfHandlerInterceptor.preHandle(request, response, null);

			Assert.assertEquals(true, verified);
		}
		finally
		{
			Config.setParameter(CSRF_URL_PROPERTY, originalValues);
		}
	}

}
