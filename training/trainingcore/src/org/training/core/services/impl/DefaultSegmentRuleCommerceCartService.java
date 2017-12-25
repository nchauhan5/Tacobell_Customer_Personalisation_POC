/**
 *
 */
package org.training.core.services.impl;

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceCartService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;

import org.training.core.services.SegmentRuleCommerceCartService;

import com.smartedit.personalization.rules.services.SegmentRuleEngineService;


/**
 * @author ksing5
 *
 */
public class DefaultSegmentRuleCommerceCartService extends DefaultCommerceCartService implements SegmentRuleCommerceCartService
{
	private SegmentRuleEngineService segmentRuleEngineService;

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


	@Override
	public CommerceCartModification addToCart(final CommerceCartParameter parameter) throws CommerceCartModificationException
	{
		final CommerceCartModification commerceCartModification = this.getCommerceAddToCartStrategy().addToCart(parameter);
		this.getSegmentRuleEngineService().evaluate(parameter.getCart());
		return commerceCartModification;
	}
}
