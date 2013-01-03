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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.validprops;

import uk.co.objectconnexions.expressiveobjects.applib.annotation.Where;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.ObjectValidityContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociation;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociationFilters;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.OneToOneAssociation;

public class ObjectValidPropertiesFacetImpl extends ObjectValidPropertiesFacetAbstract {

    // REVIEW: should provide this rendering context, rather than hardcoding.
    // the net effect currently is that class members annotated with 
    // @Hidden(where=Where.ANYWHERE) or @Disabled(where=Where.ANYWHERE) will indeed
    // be hidden/disabled, but will be visible/enabled (perhaps incorrectly) 
    // for any other value for Where
    private final Where where = Where.ANYWHERE;

    public ObjectValidPropertiesFacetImpl(final FacetHolder holder) {
        super(holder);
    }

    @Override
    public String invalidReason(final ObjectValidityContext context) {
        final StringBuilder buf = new StringBuilder();
        final ObjectAdapter adapter = context.getTarget();
        for (final ObjectAssociation property : adapter.getSpecification().getAssociations(ObjectAssociationFilters.PROPERTIES)) {
            // ignore hidden properties
            if (property.isVisible(context.getSession(), adapter, where).isVetoed()) {
                continue;
            }
            // ignore disabled properties
            if (property.isUsable(context.getSession(), adapter, where).isVetoed()) {
                continue;
            }
            final OneToOneAssociation otoa = (OneToOneAssociation) property;
            final ObjectAdapter value = otoa.get(adapter);
            if (otoa.isAssociationValid(adapter, value).isVetoed()) {
                if (buf.length() > 0) {
                    buf.append(", ");
                }
                buf.append(property.getName());
            }
        }
        if (buf.length() > 0) {
            return "Invalid properties: " + buf.toString();
        }
        return null;
    }

}
