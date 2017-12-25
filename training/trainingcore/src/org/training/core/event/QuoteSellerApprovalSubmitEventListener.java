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
package org.training.core.event;

import static org.training.core.constants.TrainingCoreConstants.QUOTE_SELLER_APPROVER_PROCESS;

import de.hybris.platform.commerceservices.event.QuoteSellerApprovalSubmitEvent;
import de.hybris.platform.commerceservices.model.process.QuoteProcessModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.model.ModelService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Event listener that listens to {@link QuoteSellerApprovalSubmitEvent} which is used to trigger seller approval
 * process.
 */
public class QuoteSellerApprovalSubmitEventListener extends AbstractEventListener<QuoteSellerApprovalSubmitEvent>
{
	private ModelService modelService;
	private BusinessProcessService businessProcessService;
	private static final Logger LOG = Logger.getLogger(QuoteSellerApprovalSubmitEventListener.class);

	@Override
	protected void onEvent(final QuoteSellerApprovalSubmitEvent event)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Received QuoteSellerApprovalSubmitEvent..");
		}

		final QuoteProcessModel quoteSellerApprovalProcess = (QuoteProcessModel) getBusinessProcessService()
				.createProcess("quoteSellerApprovalProcess" + "-" + event.getQuote().getCode() + "-"
						+ event.getQuote().getStore().getUid() + "-" + System.currentTimeMillis(), QUOTE_SELLER_APPROVER_PROCESS);

		final QuoteModel quoteModel = event.getQuote();
		quoteSellerApprovalProcess.setQuoteCode(quoteModel.getCode());
		getModelService().save(quoteSellerApprovalProcess);
		//start the business process
		getBusinessProcessService().startProcess(quoteSellerApprovalProcess);
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}

	@Required
	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
	}
}
