/**
 *
 */
package org.training.rules.dynamic.attribute.handlers;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler;

import org.joda.time.LocalDate;
import org.joda.time.Years;


/**
 *
 */
public class CustomerAgeHandler implements DynamicAttributeHandler<Integer, CustomerModel>
{

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler#get(de.hybris.platform.servicelayer.model.
	 * AbstractItemModel)
	 */
	@Override
	public Integer get(final CustomerModel customer)
	{
		final LocalDate birthdate = new LocalDate(customer.getDateOfBirth());
		final LocalDate now = new LocalDate();
		final Years age = Years.yearsBetween(birthdate, now);
		return new Integer(age.getYears());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler#set(de.hybris.platform.servicelayer.model.
	 * AbstractItemModel, java.lang.Object)
	 */
	@Override
	public void set(final CustomerModel customer, final Integer age)
	{
		// YTODO Auto-generated method stub

	}

}
