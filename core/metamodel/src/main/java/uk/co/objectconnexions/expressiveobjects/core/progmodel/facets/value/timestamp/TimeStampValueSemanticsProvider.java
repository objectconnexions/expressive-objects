/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.timestamp;

import java.text.DateFormat;
import java.util.Date;
import java.util.Map;

import com.google.common.collect.Maps;

import uk.co.objectconnexions.expressiveobjects.applib.adapters.EncoderDecoder;
import uk.co.objectconnexions.expressiveobjects.applib.adapters.Parser;
import uk.co.objectconnexions.expressiveobjects.applib.value.TimeStamp;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.parseable.InvalidEntryException;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.properties.defaults.PropertyDefaultFacet;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.value.ValueSemanticsProviderContext;

public class TimeStampValueSemanticsProvider extends TimeStampValueSemanticsProviderAbstract<TimeStamp> {

    public static final boolean isAPropertyDefaultFacet() {
        return PropertyDefaultFacet.class.isAssignableFrom(TimeStampValueSemanticsProvider.class);
    }

    private static Map<String, DateFormat> formats = Maps.newHashMap();

    static {
        initFormats(formats);
    }

    /**
     * Required because implementation of {@link Parser} and
     * {@link EncoderDecoder}.
     */
    public TimeStampValueSemanticsProvider() {
        this(null, null, null);
    }

    public TimeStampValueSemanticsProvider(final FacetHolder holder, final ExpressiveObjectsConfiguration configuration, final ValueSemanticsProviderContext context) {
        super(holder, TimeStamp.class, configuration, context);
    }

    // //////////////////////////////////////////////////////////////////
    // temporal-specific stuff
    // //////////////////////////////////////////////////////////////////

    @Override
    protected Date dateValue(final Object value) {
        return new Date(((TimeStamp) value).longValue());
    }

    @Override
    protected Map<String, DateFormat> formats() {
        return formats;
    }

    @Override
    protected TimeStamp now() {
        throw new InvalidEntryException("Can't change a timestamp.");
    }

    @Override
    protected TimeStamp setDate(final Date date) {
        return new TimeStamp(date.getTime());
    }

}
