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
package org.training.setup;

import static org.training.constants.TrainingrulesConstants.PLATFORM_LOGO_CODE;

import de.hybris.platform.core.initialization.SystemSetup;

import java.io.InputStream;

import org.training.constants.TrainingrulesConstants;
import org.training.service.TrainingrulesService;


@SystemSetup(extension = TrainingrulesConstants.EXTENSIONNAME)
public class TrainingrulesSystemSetup
{
	private final TrainingrulesService trainingrulesService;

	public TrainingrulesSystemSetup(final TrainingrulesService trainingrulesService)
	{
		this.trainingrulesService = trainingrulesService;
	}

	@SystemSetup(process = SystemSetup.Process.INIT, type = SystemSetup.Type.ESSENTIAL)
	public void createEssentialData()
	{
		trainingrulesService.createLogo(PLATFORM_LOGO_CODE);
	}

	private InputStream getImageStream()
	{
		return TrainingrulesSystemSetup.class.getResourceAsStream("/trainingrules/sap-hybris-platform.png");
	}
}
