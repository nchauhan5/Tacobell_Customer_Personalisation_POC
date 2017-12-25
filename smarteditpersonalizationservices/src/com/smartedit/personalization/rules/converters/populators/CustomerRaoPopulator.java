package com.smartedit.personalization.rules.converters.populators;

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.ruleengineservices.converters.populator.UserRaoPopulator;
import de.hybris.platform.ruleengineservices.rao.UserRAO;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;


/**
 *
 */
public class CustomerRaoPopulator extends UserRaoPopulator
{

	private Converter<UserModel, UserRAO> userConverter;

	@Override
	public void populate(final UserModel source, final UserRAO target) throws ConversionException
	{
		if (source != null)
		{
			super.populate(source, target);
			//target.setAge(source.getCustomerAge() != null ? source.getCustomerAge().intValue() : 0);
			//target.setDateOfBirth(source.getBirthday());
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
