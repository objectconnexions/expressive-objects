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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.booleans;

import uk.co.objectconnexions.expressiveobjects.applib.adapters.EncoderDecoder;
import uk.co.objectconnexions.expressiveobjects.applib.adapters.Parser;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.properties.defaults.PropertyDefaultFacet;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.value.ValueSemanticsProviderContext;

public class BooleanPrimitiveValueSemanticsProvider extends BooleanValueSemanticsProviderAbstract implements PropertyDefaultFacet {

    private static final Boolean DEFAULT_VALUE = Boolean.FALSE;

    /**
     * Required because implementation of {@link Parser} and
     * {@link EncoderDecoder}.
     */
    public BooleanPrimitiveValueSemanticsProvider() {
        this(null, null, null);
    }

    public BooleanPrimitiveValueSemanticsProvider(final FacetHolder holder, final ExpressiveObjectsConfiguration configuration, final ValueSemanticsProviderContext context) {
        super(holder, boolean.class, DEFAULT_VALUE, configuration, context);
    }

    // //////////////////////////////////////////////////////////////////
    // PropertyDefault
    // //////////////////////////////////////////////////////////////////

    @Override
    public ObjectAdapter getDefault(final ObjectAdapter inObject) {
        return createAdapter(boolean.class, Boolean.FALSE);
    }

    // //////////////////////////////////////////////////////////////////
    // BooleanValueFacet impl
    // //////////////////////////////////////////////////////////////////

    @Override
    public void reset(final ObjectAdapter object) {
        object.replacePojo(Boolean.FALSE);
    }

    @Override
    public void set(final ObjectAdapter object) {
        object.replacePojo(Boolean.TRUE);
    }

    @Override
    public void toggle(final ObjectAdapter object) {
        final boolean current = ((Boolean) object.getObject()).booleanValue();
        final boolean toggled = !current;
        object.replacePojo(Boolean.valueOf(toggled));
    }

}
