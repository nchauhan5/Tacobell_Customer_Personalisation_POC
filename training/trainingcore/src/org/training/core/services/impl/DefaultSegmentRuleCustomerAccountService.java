/**
 *
 */
package org.training.core.services.impl;

import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.customer.impl.DefaultCustomerAccountService;
import de.hybris.platform.core.model.user.CustomerModel;

import org.training.core.services.SegmentRuleCustomerAccountService;

import com.smartedit.personalization.rules.services.SegmentRuleEngineService;


/**
 * @author ksing5
 *
 */
public class DefaultSegmentRuleCustomerAccountService extends DefaultCustomerAccountService
		implements SegmentRuleCustomerAccountService
{

	private SegmentRuleEngineService segmentRuleEngineService;

	@Override
	protected void registerCustomer(final CustomerModel customerModel, final String password) throws DuplicateUidException
	{
		super.registerCustomer(customerModel, password);
		segmentRuleEngineService.evaluate(customerModel);
	}

	@Deprecated
	@Override
	public void updateProfile(final CustomerModel customerModel, final String titleCode, final String name, final String login)
			throws DuplicateUidException
	{
		super.updateProfile(customerModel, titleCode, name, login);
		segmentRuleEngineService.evaluate(customerModel);
	}

	/**
	 * @return the segmentRuleEngineService
	 */
	public SegmentRuleEngineService getSegmentRuleEngineService()
	{
		return segmentRuleEngineService;
	}

	/**
	 * @param segmentRuleEngineService
	 *           the segmentRuleEngineService to set
	 */
	public void setSegmentRuleEngineService(final SegmentRuleEngineService segmentRuleEngineService)
	{
		this.segmentRuleEngineService = segmentRuleEngineService;
	}


}
