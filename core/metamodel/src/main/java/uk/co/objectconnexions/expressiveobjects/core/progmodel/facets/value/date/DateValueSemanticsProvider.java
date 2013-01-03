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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.date;

import java.util.Date;

import uk.co.objectconnexions.expressiveobjects.applib.adapters.EncoderDecoder;
import uk.co.objectconnexions.expressiveobjects.applib.adapters.Parser;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.value.ValueSemanticsProviderContext;

public class DateValueSemanticsProvider extends DateValueSemanticsProviderAbstract<uk.co.objectconnexions.expressiveobjects.applib.value.Date> {

    // no default
    private static final uk.co.objectconnexions.expressiveobjects.applib.value.Date DEFAULT_VALUE = null; 


    /**
     * Required because implementation of {@link Parser} and
     * {@link EncoderDecoder}.
     */
    public DateValueSemanticsProvider() {
        this(null, null, null);
    }

    public DateValueSemanticsProvider(final FacetHolder holder, final ExpressiveObjectsConfiguration configuration, final ValueSemanticsProviderContext context) {
        super(holder, uk.co.objectconnexions.expressiveobjects.applib.value.Date.class, Immutability.NOT_IMMUTABLE, EqualByContent.NOT_HONOURED, DEFAULT_VALUE, configuration, context);
    }

    @Override
    protected uk.co.objectconnexions.expressiveobjects.applib.value.Date add(final uk.co.objectconnexions.expressiveobjects.applib.value.Date original, final int years, final int months, final int days, final int hours, final int minutes) {
        final uk.co.objectconnexions.expressiveobjects.applib.value.Date date = original;
        return date.add(years, months, days);
    }

    @Override
    protected uk.co.objectconnexions.expressiveobjects.applib.value.Date now() {
        return new uk.co.objectconnexions.expressiveobjects.applib.value.Date();
    }

    @Override
    protected Date dateValue(final Object value) {
        return ((uk.co.objectconnexions.expressiveobjects.applib.value.Date) value).dateValue();
    }

    @Override
    protected uk.co.objectconnexions.expressiveobjects.applib.value.Date setDate(final Date date) {
        return new uk.co.objectconnexions.expressiveobjects.applib.value.Date(date);
    }
}
