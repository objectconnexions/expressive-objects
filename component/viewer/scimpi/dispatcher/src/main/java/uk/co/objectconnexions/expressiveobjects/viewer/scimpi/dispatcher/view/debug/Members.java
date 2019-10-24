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

package uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.debug;

import java.util.List;

import uk.co.objectconnexions.expressiveobjects.applib.annotation.Where;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ActionType;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAction;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectActionContainer.Contributed;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectActionParameter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociation;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.AbstractElementProcessor;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.ForbiddenException;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.ScimpiException;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.processor.Request;

public class Members extends AbstractElementProcessor {

    // REVIEW: should provide this rendering context, rather than hardcoding.
    // the net effect currently is that class members annotated with 
    // @Hidden(where=Where.ANYWHERE) or @Disabled(where=Where.ANYWHERE) will indeed
    // be hidden/disabled, but will be visible/enabled (perhaps incorrectly) 
    // for any other value for Where
    private final Where where = Where.ANYWHERE;

    @Override
    public String getName() {
        return "members";
    }

    @Override
    public void process(final Request request) {
        if (request.getContext().isDebugDisabled()) {
            return;
        }

        final String id = request.getOptionalProperty(OBJECT);
        final String fieldName = request.getOptionalProperty(FIELD);
        request.appendHtml("<pre class=\"debug\">");
        try {
            ObjectAdapter object = request.getContext().getMappedObjectOrResult(id);
            ObjectAssociation field = null;
            if (fieldName != null) {
                field = object.getSpecification().getAssociation(fieldName);
                if (field.isVisible(ExpressiveObjectsContext.getAuthenticationSession(), object, where).isVetoed()) {
                    throw new ForbiddenException(field, ForbiddenException.VISIBLE);
                }
                object = field.get(object);
            }
            request.processUtilCloseTag();

            final ObjectSpecification specification = field == null ? object.getSpecification() : field.getSpecification();

            request.appendHtml(specification.getSingularName() + " (" + specification.getFullIdentifier() + ") \n");
            final List<ObjectAssociation> fields = specification.getAssociations();
            for (final ObjectAssociation fld : fields) {
                if (!fld.isAlwaysHidden()) {
                    request.appendHtml("   " + fld.getId() + " - '" + fld.getName() + "' -> " + fld.getSpecification().getSingularName() + (fld.isOneToManyAssociation() ? " (collection of)" : "") + "\n");
                }
            }
            request.appendHtml("   --------------\n");
            final List<ObjectAction> actions = specification.getObjectActions(ActionType.USER, Contributed.INCLUDED);
            ;
            for (final ObjectAction action : actions) {
                request.appendHtml("   " + action.getId() + " (");
                boolean first = true;
                for (final ObjectActionParameter parameter : action.getParameters()) {
                    if (!first) {
                        request.appendHtml(", ");
                    }
                    request.appendHtml(parameter.getSpecification().getSingularName());
                    first = false;
                }
                request.appendHtml(")" + " - '" + action.getName() + "'");
                if (action.getSpecification() != null) {
                    request.appendHtml(" -> " + action.getSpecification().getSingularName() + ")");
                }
                request.appendHtml("\n");
            }
        } catch (final ScimpiException e) {
            request.appendHtml("Debug failed: " + e.getMessage());
        }
        request.appendHtml("</pre>");
    }

}
