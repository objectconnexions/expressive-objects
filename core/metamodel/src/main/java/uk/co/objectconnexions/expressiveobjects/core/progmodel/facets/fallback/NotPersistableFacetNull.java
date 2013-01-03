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

import uk.co.objectconnexions.expressiveobjects.applib.events.UsabilityEvent;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.notpersistable.NotPersistableFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.notpersistable.NotPersistableFacetAbstract;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.UsabilityContext;

/**
 * Installed by the {@link FallbackFacetFactory}, and means that this class
 * <i>is</i> persistable (ie not {@link NotPersistableFacet not persistable}).
 */
public class NotPersistableFacetNull extends NotPersistableFacetAbstract {

    public NotPersistableFacetNull(final FacetHolder holder) {
        super(null, holder);
    }

    /**
     * Always returns <tt>null</tt> (that is, does <i>not</i> disable).
     */
    @Override
    public String disables(final UsabilityContext<? extends UsabilityEvent> ic) {
        return null;
    }

}
