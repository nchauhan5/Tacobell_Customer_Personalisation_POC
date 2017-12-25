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
package com.smartedit.personalization.rules.services.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.personalizationservices.model.CxUserToSegmentModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 *
 */
@UnitTest
public class DefaultUserToSegmentServiceTest
{
	@InjectMocks
	private DefaultUserToSegmentService defaultUserToSegmentService;

	@Mock
	private ModelService modelService;

	@Mock
	private FlexibleSearchService flexibleSearchService;

	@Mock
	private SearchResult<CxUserToSegmentModel> searchResult;

	@Mock
	private CxUserToSegmentModel cxUserToSegmentModel;

	private List<CxUserToSegmentModel> cxUserToSegments;

	private UserModel currentUser;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		cxUserToSegments = new ArrayList<>();
		cxUserToSegments.add(cxUserToSegmentModel);
		currentUser = new UserModel();
		currentUser.setUserToSegments(cxUserToSegments);
	}

	@Test
	public void testRemoveSegmentsForUser_IfSegmentAttachedVia_SegmentSourceRule()
	{
		Mockito.when(cxUserToSegmentModel.getIsSegmentAttachedViaSegmentSourceRule()).thenReturn(Boolean.TRUE);
		Mockito.doNothing().when(modelService).removeAll(Mockito.anySet());
		Mockito.doNothing().when(modelService).removeAll(Mockito.anySet());
		defaultUserToSegmentService.removeSegmentsForUser(currentUser);
		Mockito.verify(cxUserToSegmentModel, Mockito.times(1)).getIsSegmentAttachedViaSegmentSourceRule();
	}

	@Test
	public void testRemoveSegmentsForUser_IfSegmentNotAttachedVia_SegmentSourceRule()
	{
		Mockito.when(cxUserToSegmentModel.getIsSegmentAttachedViaSegmentSourceRule()).thenReturn(Boolean.FALSE);
		Mockito.doNothing().when(modelService).removeAll(Mockito.anySet());
		Mockito.doNothing().when(modelService).removeAll(Mockito.anySet());
		defaultUserToSegmentService.removeSegmentsForUser(currentUser);
		Mockito.verify(cxUserToSegmentModel, Mockito.times(1)).getIsSegmentAttachedViaSegmentSourceRule();
	}

	@Test
	public void testGetSegmentsForUserBasedOnDuration()
	{
		Mockito.when(flexibleSearchService.<CxUserToSegmentModel> search(Mockito.anyString())).thenReturn(searchResult);
		Mockito.when(searchResult.getResult()).thenReturn(cxUserToSegments);
		Assert.assertNotNull(cxUserToSegments);
		Assert.assertEquals(1, cxUserToSegments.size());
		Assert.assertEquals(cxUserToSegmentModel, cxUserToSegments.get(0));
	}
}
