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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.dateutil;

import java.util.Calendar;
import java.util.Date;

import uk.co.objectconnexions.expressiveobjects.applib.adapters.EncoderDecoder;
import uk.co.objectconnexions.expressiveobjects.applib.adapters.Parser;
import uk.co.objectconnexions.expressiveobjects.applib.clock.Clock;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.value.ValueSemanticsProviderContext;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.DateAndTimeValueSemanticsProviderAbstract;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.datesql.JavaSqlDateValueSemanticsProvider;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.timesql.JavaSqlTimeValueSemanticsProvider;

/**
 * An adapter that handles {@link java.util.Date} as both a date AND time
 * component.
 * 
 * @see JavaSqlDateValueSemanticsProvider
 * @see JavaSqlTimeValueSemanticsProvider
 */
public class JavaUtilDateValueSemanticsProvider extends DateAndTimeValueSemanticsProviderAbstract<java.util.Date> {

    /**
     * Required because implementation of {@link Parser} and
     * {@link EncoderDecoder}.
     */
    public JavaUtilDateValueSemanticsProvider() {
        this(null, null, null);
    }

    public JavaUtilDateValueSemanticsProvider(final FacetHolder holder, final ExpressiveObjectsConfiguration configuration, final ValueSemanticsProviderContext context) {
        super(holder, Date.class, Immutability.NOT_IMMUTABLE, EqualByContent.NOT_HONOURED, configuration, context);
    }

    @Override
    protected Date dateValue(final Object value) {
        return value == null ? null : (Date) value;
    }

    @Override
    protected Date add(final Date original, final int years, final int months, final int days, final int hours, final int minutes) {
        final Date date = original;
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        cal.add(Calendar.YEAR, years);
        cal.add(Calendar.MONTH, months);
        cal.add(Calendar.DAY_OF_MONTH, days);
        cal.add(Calendar.HOUR, hours);
        cal.add(Calendar.MINUTE, minutes);

        return setDate(cal.getTime());
    }

    @Override
    protected Date now() {
        return new Date(Clock.getTime());
    }

    @Override
    protected Date setDate(final Date date) {
        return date;
    }
}
