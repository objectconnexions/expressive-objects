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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.callbacks.persist;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetUtil;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FeatureType;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.callbacks.PersistedCallbackFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.callbacks.PersistingCallbackFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.methodutils.MethodScope;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.MethodFinderUtils;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.MethodPrefixBasedFacetFactoryAbstract;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.MethodPrefixConstants;

public class PersistCallbackFacetFactory extends MethodPrefixBasedFacetFactoryAbstract {

    private static final String[] PREFIXES = { MethodPrefixConstants.PERSISTED_PREFIX, MethodPrefixConstants.PERSISTING_PREFIX, };

    public PersistCallbackFacetFactory() {
        super(FeatureType.OBJECTS_ONLY, OrphanValidation.VALIDATE, PREFIXES);
    }

    @Override
    public void process(final ProcessClassContext processClassContext) {
        final Class<?> cls = processClassContext.getCls();
        final FacetHolder facetHolder = processClassContext.getFacetHolder();

        final List<Facet> facets = new ArrayList<Facet>();
        final List<Method> methods = new ArrayList<Method>();

        Method method = null;
        method = MethodFinderUtils.findMethod(cls, MethodScope.OBJECT, MethodPrefixConstants.PERSISTING_PREFIX, void.class, NO_PARAMETERS_TYPES);
        if (method != null) {
            methods.add(method);
            final PersistingCallbackFacet facet = facetHolder.getFacet(PersistingCallbackFacet.class);
            if (facet == null) {
                facets.add(new PersistingCallbackFacetViaMethod(method, facetHolder));
            } else {
                facet.addMethod(method);
            }
        }

        method = MethodFinderUtils.findMethod(cls, MethodScope.OBJECT, MethodPrefixConstants.PERSISTED_PREFIX, void.class, NO_PARAMETERS_TYPES);
        if (method != null) {
            methods.add(method);
            final PersistedCallbackFacet facet = facetHolder.getFacet(PersistedCallbackFacet.class);
            if (facet == null) {
                facets.add(new PersistedCallbackFacetViaMethod(method, facetHolder));
            } else {
                facet.addMethod(method);
            }
        }

        processClassContext.removeMethods(methods);
        FacetUtil.addFacets(facets);
    }

}
