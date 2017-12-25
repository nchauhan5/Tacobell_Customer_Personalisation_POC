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
package org.training.core.search.solrfacetsearch.provider.impl;

import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractPropertyFieldValueProvider;
import org.training.core.enums.SwatchColorEnum;
import org.training.core.model.ApparelSizeVariantProductModel;
import org.training.core.model.ApparelStyleVariantProductModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;


public class ColorFacetValueProvider extends AbstractPropertyFieldValueProvider implements FieldValueProvider, Serializable
{
	private FieldNameProvider fieldNameProvider;

	@Override
	public Collection<FieldValue> getFieldValues(final IndexConfig indexConfig, final IndexedProperty indexedProperty,
			final Object model) throws FieldValueProviderException
	{
		final ApparelStyleVariantProductModel apparelStyleModel = getApparelStyleProductModel(model);
		if (apparelStyleModel == null)
		{
			return Collections.emptyList();
		}

		final Set<SwatchColorEnum> colors = apparelStyleModel.getSwatchColors();

		if (colors != null && !colors.isEmpty())
		{
			final Collection<FieldValue> fieldValues = new ArrayList<FieldValue>();
			for (final SwatchColorEnum color : colors)
			{
				fieldValues.addAll(createFieldValue(color, indexedProperty));
			}
			return fieldValues;
		}
		else
		{
			return Collections.emptyList();
		}
	}

	protected List<FieldValue> createFieldValue(final SwatchColorEnum color, final IndexedProperty indexedProperty)
	{
		final List<FieldValue> fieldValues = new ArrayList<FieldValue>();
		final Object value = color.getCode();
		final Collection<String> fieldNames = fieldNameProvider.getFieldNames(indexedProperty, null);
		for (final String fieldName : fieldNames)
		{
			fieldValues.add(new FieldValue(fieldName, value));
		}
		return fieldValues;
	}

	protected ApparelStyleVariantProductModel getApparelStyleProductModel(final Object model)
	{
		Object finalModel = model;
		if (model instanceof ApparelSizeVariantProductModel)
		{
			finalModel = ((ApparelSizeVariantProductModel) finalModel).getBaseProduct();
		}

		if (finalModel instanceof ApparelStyleVariantProductModel)
		{
			return (ApparelStyleVariantProductModel) finalModel;
		}
		else
		{
			return null;
		}
	}

	@Required
	public void setFieldNameProvider(final FieldNameProvider fieldNameProvider)
	{
		this.fieldNameProvider = fieldNameProvider;
	}

}
