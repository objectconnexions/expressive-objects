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

package uk.co.objectconnexions.expressiveobjects.core.metamodel.facets;

import uk.co.objectconnexions.expressiveobjects.applib.annotation.When;
import uk.co.objectconnexions.expressiveobjects.applib.annotation.Where;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;

public abstract class WhenAndWhereValueFacetAbstract extends WhenValueFacetAbstract implements WhenAndWhereValueFacet {
    private final Where where;

    public WhenAndWhereValueFacetAbstract(final Class<? extends Facet> facetType, final FacetHolder holder, final When when, final Where where) {
        super(facetType, holder, when);
        this.where = where;
    }

    @Override
    public Where where() {
        return where;
    }

    @Override
    protected String toStringValues() {
        return super.toStringValues() + "; where =" + where.getFriendlyName();
    }
}
