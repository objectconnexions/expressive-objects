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

package uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.adaptermanager;

import uk.co.objectconnexions.expressiveobjects.core.commons.exceptions.UnknownTypeException;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.collections.modify.CollectionFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.collections.modify.CollectionFacetUtils;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.SpecificationLoaderSpi;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.AdapterManagerSpi;

public final class AdapterUtils {
    private AdapterUtils() {
    }

    public static ObjectAdapter createAdapter(final Class<?> type, final Object object, final AdapterManagerSpi adapterManager, final SpecificationLoaderSpi specificationLoader) {
        final ObjectSpecification specification = specificationLoader.loadSpecification(type);
        if (specification.isNotCollection()) {
            return adapterManager.adapterFor(object);
        } else {
            throw new UnknownTypeException("not an object, is this a collection?");
        }
    }

    public static Object[] getCollectionAsObjectArray(final Object option, final ObjectSpecification spec, final AdapterManagerSpi adapterManager) {
        final ObjectAdapter collection = adapterManager.adapterFor(option);
        final CollectionFacet facet = CollectionFacetUtils.getCollectionFacetFromSpec(collection);
        final Object[] optionArray = new Object[facet.size(collection)];
        int j = 0;
        for (final ObjectAdapter adapter : facet.iterable(collection)) {
            optionArray[j++] = adapter.getObject();
        }
        return optionArray;
    }

    public static Object domainObject(final ObjectAdapter inObject) {
        return inObject == null ? null : inObject.getObject();
    }

}
