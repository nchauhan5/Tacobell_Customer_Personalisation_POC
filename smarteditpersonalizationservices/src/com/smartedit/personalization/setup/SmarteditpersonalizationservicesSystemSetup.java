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
package com.smartedit.personalization.setup;

import static com.smartedit.personalization.constants.SmarteditpersonalizationservicesConstants.PLATFORM_LOGO_CODE;

import de.hybris.platform.core.initialization.SystemSetup;

import java.io.InputStream;

import com.smartedit.personalization.constants.SmarteditpersonalizationservicesConstants;
import com.smartedit.personalization.service.SmarteditpersonalizationservicesService;


@SystemSetup(extension = SmarteditpersonalizationservicesConstants.EXTENSIONNAME)
public class SmarteditpersonalizationservicesSystemSetup
{
	private final SmarteditpersonalizationservicesService smarteditpersonalizationservicesService;

	public SmarteditpersonalizationservicesSystemSetup(final SmarteditpersonalizationservicesService smarteditpersonalizationservicesService)
	{
		this.smarteditpersonalizationservicesService = smarteditpersonalizationservicesService;
	}

	@SystemSetup(process = SystemSetup.Process.INIT, type = SystemSetup.Type.ESSENTIAL)
	public void createEssentialData()
	{
		smarteditpersonalizationservicesService.createLogo(PLATFORM_LOGO_CODE);
	}

	private InputStream getImageStream()
	{
		return SmarteditpersonalizationservicesSystemSetup.class.getResourceAsStream("/smarteditpersonalizationservices/sap-hybris-platform.png");
	}
}
