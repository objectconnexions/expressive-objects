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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.fallback;

import uk.co.objectconnexions.expressiveobjects.applib.events.ValidityEvent;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.maxlen.MaxLengthFacetAbstract;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.ValidityContext;

public class MaxLengthFacetUnlimited extends MaxLengthFacetAbstract {

    public MaxLengthFacetUnlimited(final FacetHolder holder) {
        super(Integer.MAX_VALUE, holder);
    }

    /**
     * No limit to maximum length.
     */
    @Override
    public String invalidates(final ValidityContext<? extends ValidityEvent> context) {
        return null;
    }

    @Override
    public boolean isNoop() {
        return true;
    }

}
