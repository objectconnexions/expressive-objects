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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.time;

import java.text.DateFormat;
import java.util.Date;
import java.util.Map;

import com.google.common.collect.Maps;

import uk.co.objectconnexions.expressiveobjects.applib.adapters.EncoderDecoder;
import uk.co.objectconnexions.expressiveobjects.applib.adapters.Parser;
import uk.co.objectconnexions.expressiveobjects.applib.value.Time;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.value.ValueSemanticsProviderContext;

public class TimeValueSemanticsProvider extends TimeValueSemanticsProviderAbstract<uk.co.objectconnexions.expressiveobjects.applib.value.Time> {

    private static final Map<String, DateFormat> formats = Maps.newHashMap();

    static {
        initFormats(formats);
    }

    /**
     * Required because implementation of {@link Parser} and
     * {@link EncoderDecoder}.
     */
    public TimeValueSemanticsProvider() {
        this(null, null, null);
    }

    public TimeValueSemanticsProvider(final FacetHolder holder, final ExpressiveObjectsConfiguration configuration, final ValueSemanticsProviderContext context) {
        super(holder, uk.co.objectconnexions.expressiveobjects.applib.value.Time.class, configuration, context);
    }

    @Override
    protected Map<String, DateFormat> formats() {
        return formats;
    }

    @Override
    protected boolean ignoreTimeZone() {
        return true;
    }

    @Override
    protected Time add(final Time original, final int years, final int months, final int days, final int hours, final int minutes) {
        Time time = original;
        time = time.add(hours, minutes);
        return time;
    }

    @Override
    protected Date dateValue(final Object object) {
        final Time time = (Time) object;
        return time == null ? null : time.asJavaDate();
    }

    @Override
    protected Time now() {
        return new Time();
    }

    @Override
    protected Time setDate(final Date date) {
        return new Time(date.getTime());
    }

}
