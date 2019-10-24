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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.datetimejoda;

import java.util.Date;

import uk.co.objectconnexions.expressiveobjects.applib.adapters.EncoderDecoder;
import uk.co.objectconnexions.expressiveobjects.applib.adapters.Parser;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.value.ValueSemanticsProviderContext;
import org.joda.time.DateTime;

public class JodaDateTimeValueSemanticsProvider extends JodaDateTimeValueSemanticsProviderAbstract<DateTime> {

    // no default
    private static final DateTime DEFAULT_VALUE = null;


    /**
     * Required because implementation of {@link Parser} and
     * {@link EncoderDecoder}.
     */
    public JodaDateTimeValueSemanticsProvider() {
        this(null, null, null);
    }

    public JodaDateTimeValueSemanticsProvider(final FacetHolder holder, final ExpressiveObjectsConfiguration configuration, final ValueSemanticsProviderContext context) {
        super(holder, DateTime.class, DEFAULT_VALUE, configuration, context);
    }

    @Override
    protected DateTime add(final DateTime original, final int years, final int months, final int days, final int hours, final int minutes) {
        if(hours != 0 || minutes != 0) {
            throw new IllegalArgumentException("cannot add non-zero hours or minutes to a DateTime");
        }
        return original.plusYears(years).plusMonths(months).plusDays(days);
    }

    @Override
    protected DateTime now() {
        return new DateTime();
    }

    @Override
    protected Date dateValue(final Object value) {
        return ((DateTime) value).toDateTime().toDate();
    }

    @Override
    protected DateTime setDate(final Date date) {
        return new DateTime(date.getTime());
    }
}
