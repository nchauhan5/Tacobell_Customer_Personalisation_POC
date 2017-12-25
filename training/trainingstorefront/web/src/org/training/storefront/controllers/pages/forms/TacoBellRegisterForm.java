/**
 *
 */
package org.training.storefront.controllers.pages.forms;

import de.hybris.platform.acceleratorstorefrontcommons.forms.RegisterForm;

import java.util.Date;


/**
 * @author ksing5
 *
 */
public class TacoBellRegisterForm extends RegisterForm
{

	private Date dob;
	private String gender;

	/**
	 * @return the dob
	 */
	public Date getDob()
	{
		return dob;
	}

	/**
	 * @param dob
	 *           the dob to set
	 */
	public void setDob(final Date dob)
	{
		this.dob = dob;
	}

	/**
	 * @return the gender
	 */
	public String getGender()
	{
		return gender;
	}

	/**
	 * @param gender
	 *           the gender to set
	 */
	public void setGender(final String gender)
	{
		this.gender = gender;
	}

}
