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

import uk.co.objectconnexions.expressiveobjects.applib.annotation.Hidden;
import uk.co.objectconnexions.expressiveobjects.applib.annotation.Named;
import uk.co.objectconnexions.expressiveobjects.core.commons.lang.ToString;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.Persistor;

public final class SimpleRepository<T> {

    private final Class<T> type;
    private ObjectSpecification spec;

    public SimpleRepository(final Class<T> cls) {
        this.type = cls;
    }

    public String getId() {
        return "repository#" + getClass().getName();
    }

    public String iconName() {
        return getSpec().getShortIdentifier();
    }

    public String title() {
        return getSpec().getPluralName();
    }

    @Override
    public String toString() {
        final ToString str = new ToString(this);
        str.append("class", type.getName());
        return str.toString();
    }

    // //////////////////////////////////////////////////////
    // allInstances
    // //////////////////////////////////////////////////////

    public T[] allInstances() {
        return RepositoryHelper.allInstances(getSpec(), type);
    }

    public String disableAllInstances() {
        return hasInstances() ? null : "No " + getSpec().getPluralName();
    }

    // //////////////////////////////////////////////////////
    // allInstances
    // //////////////////////////////////////////////////////

    public T[] findByTitle(final @Named("Title to find") String title) {
        return (T[]) RepositoryHelper.findByTitle(getSpec(), type, title).toArray();
    }

    public String disableFindByTitle(String title) {
        return disableAllInstances();
    }

    private boolean hasInstances() {
        return RepositoryHelper.hasInstances(getSpec());
    }

    // //////////////////////////////////////////////////////
    // newPersistentInstance
    // //////////////////////////////////////////////////////

    public Object newPersistentInstance() {
        final ObjectAdapter adapter = getPersistenceSession().createTransientInstance(getSpec());
        getPersistenceSession().makePersistent(adapter);
        return adapter.getObject();
    }

    // //////////////////////////////////////////////////////
    // newTransientInstance
    // //////////////////////////////////////////////////////

    public T newTransientInstance() {
        return (T) getPersistenceSession().createTransientInstance(getSpec()).getObject();
    }

    // //////////////////////////////////////////////////////
    // getSpec (hidden)
    // //////////////////////////////////////////////////////

    @Hidden
    protected ObjectSpecification getSpec() {
        if (spec == null) {
            spec = ExpressiveObjectsContext.getSpecificationLoader().loadSpecification(type);
        }
        return spec;
    }
/*
    public static boolean alwaysHideSpec() {
        return true;
    }
*/
    // //////////////////////////////////////////////////////
    // Dependencies (from singleton)
    // //////////////////////////////////////////////////////

    private Persistor getPersistenceSession() {
        return ExpressiveObjectsContext.getPersistenceSession();
    }

}
