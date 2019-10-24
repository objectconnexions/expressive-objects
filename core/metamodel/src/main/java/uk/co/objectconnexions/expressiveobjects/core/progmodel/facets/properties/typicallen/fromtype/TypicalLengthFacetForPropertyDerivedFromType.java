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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.properties.typicallen.fromtype;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.multiline.MultiLineFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.typicallen.TypicalLengthFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.typicallen.TypicalLengthFacetAbstract;

public class TypicalLengthFacetForPropertyDerivedFromType extends TypicalLengthFacetAbstract {

    private final TypicalLengthFacet typicalLengthFacet;

    public TypicalLengthFacetForPropertyDerivedFromType(final TypicalLengthFacet typicalLengthFacet, final FacetHolder holder) {
        super(holder, true);
        this.typicalLengthFacet = typicalLengthFacet;
    }

    @Override
    public int value() {
        final MultiLineFacet facet = getFacetHolder().getFacet(MultiLineFacet.class);
        return facet.numberOfLines() * typicalLengthFacet.value();
    }

}
