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

package uk.co.objectconnexions.expressiveobjects.core.runtime.services;

import java.util.ArrayList;
import java.util.List;

import uk.co.objectconnexions.expressiveobjects.applib.query.QueryFindAllInstances;
import uk.co.objectconnexions.expressiveobjects.core.commons.exceptions.ExpressiveObjectsException;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.collections.modify.CollectionFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.collections.modify.CollectionFacetUtils;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.services.container.query.QueryCardinality;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.SpecificationLoaderSpi;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.query.PersistenceQueryFindByTitle;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.PersistenceQuery;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.Persistor;

public final class RepositoryHelper {

    private RepositoryHelper() {
    }

    public static <T> Object[] allInstances(final Class<T> cls) {
        return allInstances(getSpecificationLoader().loadSpecification(cls), cls);
    }

    public static <T> T[] allInstances(final ObjectSpecification spec, final Class<T> cls) {
        final QueryFindAllInstances<T> query = new QueryFindAllInstances<T>(spec.getFullIdentifier());
        final ObjectAdapter instances = getPersistenceSession().findInstances(query, QueryCardinality.MULTIPLE);
        final T[] array = convertToArray(instances, cls);
        return array;
    }

    public static <T> List<T> findByPersistenceQuery(final PersistenceQuery persistenceQuery, final Class<T> cls) {
        final ObjectAdapter instances = getPersistenceSession().findInstances(persistenceQuery);
        return convertToList(instances, cls);
    }

    public static <T> List<T> findByTitle(final Class<T> type, final String title) {
        final ObjectSpecification spec = getSpecificationLoader().loadSpecification(type);
        return findByTitle(spec, type, title);
    }

    public static <T> List<T> findByTitle(final ObjectSpecification spec, final Class<T> cls, final String title) {
        final PersistenceQuery criteria = new PersistenceQueryFindByTitle(spec, title);
        return findByPersistenceQuery(criteria, cls);
    }

    public static <T> boolean hasInstances(final Class<T> type) {
        final ObjectSpecification spec = getSpecificationLoader().loadSpecification(type);
        return hasInstances(spec);
    }

    public static boolean hasInstances(final ObjectSpecification spec) {
        return getPersistenceSession().hasInstances(spec);
    }

    // /////////////////////////////////////////////////////////////////////
    // Helpers
    // /////////////////////////////////////////////////////////////////////

    private static <T> List<T> convertToList(final ObjectAdapter instances, final Class<T> cls) {
        final CollectionFacet facet = CollectionFacetUtils.getCollectionFacetFromSpec(instances);
        final List<T> list = new ArrayList<T>();
        for (final ObjectAdapter adapter : facet.iterable(instances)) {
        	Object object = adapter.getObject();
        	if (!object.getClass().isInstance(cls)) {
        		throw new ExpressiveObjectsException(object + " should be instance of " + cls.getName());
        	}
			list.add((T) object);
        }
        return list;
    }

    private static <T>  T[] convertToArray(final ObjectAdapter instances, final Class<T> cls) {
        return (T[]) convertToList(instances, cls).toArray();
    }

    // /////////////////////////////////////////////////////////////////////
    // Context lookup
    // /////////////////////////////////////////////////////////////////////

    private static Persistor getPersistenceSession() {
        return ExpressiveObjectsContext.getPersistenceSession();
    }

    private static SpecificationLoaderSpi getSpecificationLoader() {
        return ExpressiveObjectsContext.getSpecificationLoader();
    }

}
