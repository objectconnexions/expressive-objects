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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.ignore.javalang;

import java.lang.reflect.Method;
import java.util.ArrayList;

import uk.co.objectconnexions.expressiveobjects.core.commons.exceptions.ExpressiveObjectsException;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FeatureType;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetFactoryAbstract;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.MethodFilteringFacetFactory;

/**
 * Designed to simply filter out any synthetic methods.
 * 
 * <p>
 * Does not add any {@link Facet}s.
 */
public class SyntheticMethodFilteringFacetFactory extends FacetFactoryAbstract implements MethodFilteringFacetFactory {

    public SyntheticMethodFilteringFacetFactory() {
        super(new ArrayList<FeatureType>());
    }

    @Override
    public boolean recognizes(final Method method) {
        return isSynthetic(method);
    }

    private boolean isSynthetic(final Method method) {
        try {
            final Class<?> type = method.getClass();
            try {
                return ((Boolean) type.getMethod("isSynthetic", (Class[]) null).invoke(method, (Object[]) null)).booleanValue();
            } catch (final NoSuchMethodException nsm) {
                // pre java 5
                return false;
            }
        } catch (final Exception e) {
            throw new ExpressiveObjectsException(e);
        }
    }

}
