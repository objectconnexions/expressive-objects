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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.datetime;

import java.util.Date;

import uk.co.objectconnexions.expressiveobjects.applib.adapters.EncoderDecoder;
import uk.co.objectconnexions.expressiveobjects.applib.adapters.Parser;
import uk.co.objectconnexions.expressiveobjects.applib.value.DateTime;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.value.ValueSemanticsProviderContext;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.DateAndTimeValueSemanticsProviderAbstract;

public class DateTimeValueSemanticsProvider extends DateAndTimeValueSemanticsProviderAbstract<DateTime> {

    /**
     * Required because implementation of {@link Parser} and
     * {@link EncoderDecoder}.
     */
    public DateTimeValueSemanticsProvider() {
        this(null, null, null);
    }

    public DateTimeValueSemanticsProvider(final FacetHolder holder, final ExpressiveObjectsConfiguration configuration, final ValueSemanticsProviderContext context) {
        super(holder, DateTime.class, Immutability.NOT_IMMUTABLE, EqualByContent.NOT_HONOURED, configuration, context);
    }

    @Override
    protected Date dateValue(final Object value) {
        final DateTime date = (DateTime) value;
        return date == null ? null : date.dateValue();
    }

    @Override
    protected DateTime add(final DateTime original, final int years, final int months, final int days, final int hours, final int minutes) {
        DateTime date = original;
        date = date.add(years, months, days, hours, minutes);
        return date;
    }

    @Override
    protected DateTime now() {
        return new DateTime();
    }

    @Override
    protected DateTime setDate(final Date date) {
        return new DateTime(date);
    }

}
