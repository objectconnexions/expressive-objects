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

package uk.co.objectconnexions.expressiveobjects.core.metamodel.facetdecorator;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;

public abstract class FacetDecoratorAbstract implements FacetDecorator {

    @Override
    public String getFacetTypeNames() {
        final Class<? extends Facet>[] decoratorFacetTypes = getFacetTypes();
        final StringBuilder buf = new StringBuilder();
        for (int i = 0; i < decoratorFacetTypes.length; i++) {
            if (i > 0) {
                buf.append(", ");
            }
            buf.append(decoratorFacetTypes[i].getName());
        }
        return buf.toString();
    }

}
