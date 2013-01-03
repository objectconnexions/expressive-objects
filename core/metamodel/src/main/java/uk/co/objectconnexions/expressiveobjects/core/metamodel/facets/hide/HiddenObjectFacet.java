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

package uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.hide;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.HidingInteractionAdvisor;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectMember;

/**
 * Mechanism for determining whether this object is should be hidden.
 * 
 * <p>
 * Even though all the properties of an object may themselves be visible, there
 * could be reasons to hide the object.
 * </p>
 * 
 * <p>
 * In the standard Expressive Objects Programming Model, typically corresponds to the
 * <tt>hidden</tt> method.
 * </p>
 * 
 * @see HiddenFacet
 */
public interface HiddenObjectFacet extends Facet, HidingInteractionAdvisor {

    /**
     * Clone this facet onto another {@link FacetHolder}.
     * 
     * <p>
     * Introduced to allow this facet to be installed onto the
     * {@link ObjectSpecification}, and then copied down onto each of the spec's
     * {@link ObjectMember}s.
     */
    public void copyOnto(FacetHolder holder);

}
