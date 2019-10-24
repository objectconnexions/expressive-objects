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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.actions.exploration;

import uk.co.objectconnexions.expressiveobjects.applib.events.VisibilityEvent;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.MarkerFacetAbstract;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.actions.exploration.ExplorationFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.VisibilityContext;

public abstract class ExplorationFacetAbstract extends MarkerFacetAbstract implements ExplorationFacet {

    public static Class<? extends Facet> type() {
        return ExplorationFacet.class;
    }

    public ExplorationFacetAbstract(final FacetHolder holder) {
        super(type(), holder);
    }

    @Override
    public String hides(
        VisibilityContext<? extends VisibilityEvent> ic) {
        return ic.getDeploymentCategory().isExploring()? null: "Not visible";
    }


}
