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

import uk.co.objectconnexions.expressiveobjects.core.commons.lang.ToString;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.services.container.query.QueryFindByTitle;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;

/**
 * Corresponds to {@link QueryFindByTitle}.
 */
public class PersistenceQueryFindByTitle extends PersistenceQueryBuiltInAbstract {
    private final String title;

    public PersistenceQueryFindByTitle(final ObjectSpecification specification, final String title) {
        super(specification);
        this.title = title == null ? "" : title.toLowerCase();
    }

    public String getTitle() {
        return title;
    }

    @Override
    public boolean matches(final ObjectAdapter object) {
        final String titleString = object.titleString().toLowerCase();
        return matches(titleString);
    }

    public boolean matches(final String titleString) {
        final String objectTitle = titleString.toLowerCase();
        return objectTitle.indexOf(title) >= 0;
    }

    @Override
    public String toString() {
        final ToString str = ToString.createAnonymous(this);
        str.append("spec", getSpecification().getShortIdentifier());
        str.append("title", title);
        return str.toString();
    }

}
