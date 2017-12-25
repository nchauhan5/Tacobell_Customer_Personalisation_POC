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
package org.training.rules.strategies.impl.mapper;

import de.hybris.platform.personalizationservices.model.CxSegmentModel;
import de.hybris.platform.personalizationservices.segment.CxSegmentService;
import de.hybris.platform.ruleengineservices.rule.strategies.RuleParameterValueMapper;
import de.hybris.platform.ruleengineservices.rule.strategies.RuleParameterValueMapperException;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;


/**
 *
 */
public class SegmentRuleParameterValueMapper implements RuleParameterValueMapper<CxSegmentModel>
{

	@Autowired
	private CxSegmentService cxSegmentService;

	@Override
	public CxSegmentModel fromString(final String value) throws RuleParameterValueMapperException
	{
		ServicesUtil.validateParameterNotNull(value, "String value cannot be null");
		final Optional<CxSegmentModel> segmentModel = cxSegmentService.getSegment(value);
		if (!segmentModel.isPresent())
		{
			throw new RuleParameterValueMapperException("Cannot find Segment with the code: " + value);
		}
		else
		{
			return segmentModel.get();
		}
	}

	@Override
	public String toString(final CxSegmentModel cxSegmentModel) throws RuleParameterValueMapperException
	{
		ServicesUtil.validateParameterNotNull(cxSegmentModel, "Object cannot be null");
		return cxSegmentModel.getCode();
	}

}
