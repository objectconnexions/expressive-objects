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

package uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.callbacks;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.DomainModelException;

public final class CallbackUtils {

    private CallbackUtils() {
    }

    public static void callCallback(final ObjectAdapter object, final Class<? extends Facet> cls) {
        final CallbackFacet facet = (CallbackFacet) object.getSpecification().getFacet(cls);
        if (facet != null) {
            try {
                facet.invoke(object);
            } catch (final RuntimeException e) {
                throw new DomainModelException("Callback failed.  Calling " + facet + " on " + object, e);
            }
        }
    }

}
