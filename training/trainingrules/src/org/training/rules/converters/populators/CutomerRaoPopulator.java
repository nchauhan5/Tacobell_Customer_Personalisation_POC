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
package org.training.rules.converters.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.ruleengineservices.rao.UserRAO;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.training.data.CustomerRAO;


/**
 *
 */
public class CutomerRaoPopulator implements Populator<CustomerModel, CustomerRAO>
{

	private Converter<UserModel, UserRAO> userConverter;

	@Override
	public void populate(final CustomerModel source, final CustomerRAO target) throws ConversionException
	{
		// YTODO Auto-generated method stub
		if (source != null)
		{
			final UserRAO userRAO = this.getUserConverter().convert(source);
			target.setOrders(userRAO.getOrders());
			target.setCxPromotionActionResults(userRAO.getCxPromotionActionResults());
			target.setGroups(userRAO.getGroups());
			try
			{
				//some issue with dynamic handler
				target.setAge(source.getCustomerAge() != null ? source.getCustomerAge().intValue() : 0);
			}
			catch (final Exception ex)
			{
				final LocalDate birthdate = new LocalDate(source.getDateOfBirth());
				final LocalDate now = new LocalDate();
				target.setAge(Years.yearsBetween(birthdate, now).getYears());
			}
			target.setDateOfBirth(source.getDateOfBirth());
			target.setGender(source.getGender());
			target.setId(source.getUid());
			target.setCreationDate(source.getCreationtime());
		}
	}

	/**
	 * @return the userConverter
	 */
	public Converter<UserModel, UserRAO> getUserConverter()
	{
		return userConverter;
	}

	/**
	 * @param userConverter
	 *           the userConverter to set
	 */
	public void setUserConverter(final Converter<UserModel, UserRAO> userConverter)
	{
		this.userConverter = userConverter;
	}

}
