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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.members.order;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.MultipleValueFacetAbstract;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.members.order.MemberOrderFacet;

public abstract class MemberOrderFacetAbstract extends MultipleValueFacetAbstract implements MemberOrderFacet {

    public static Class<? extends Facet> type() {
        return MemberOrderFacet.class;
    }

    private final String name;
    private final String sequence;

    public MemberOrderFacetAbstract(final String name, final String sequence, final FacetHolder holder) {
        super(type(), holder);
        this.name = name;
        this.sequence = sequence;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String sequence() {
        return sequence;
    }

}
