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

package uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.query;

import uk.co.objectconnexions.expressiveobjects.applib.query.QueryFindAllInstances;
import uk.co.objectconnexions.expressiveobjects.core.commons.lang.ToString;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;

/**
 * Corresponds to {@link QueryFindAllInstances}
 */
public class PersistenceQueryFindAllInstances extends PersistenceQueryBuiltInAbstract {
    public PersistenceQueryFindAllInstances(final ObjectSpecification specification) {
        super(specification);
    }

    /**
     * Returns true so it matches all instances.
     */
    @Override
    public boolean matches(final ObjectAdapter object) {
        return true;
    }

    @Override
    public String toString() {
        final ToString str = ToString.createAnonymous(this);
        str.append("spec", getSpecification().getShortIdentifier());
        return str.toString();
    }
}
