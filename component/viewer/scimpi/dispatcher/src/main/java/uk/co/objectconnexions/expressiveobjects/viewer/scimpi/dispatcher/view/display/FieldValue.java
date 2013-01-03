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

package uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.display;

import uk.co.objectconnexions.expressiveobjects.applib.annotation.Where;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.parseable.ParseableFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociation;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.booleans.BooleanValueFacet;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.ObjectNotFoundException;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.AbstractElementProcessor;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.ForbiddenException;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.ScimpiException;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.context.RequestContext.Scope;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.processor.Request;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.field.LinkedObject;

public class FieldValue extends AbstractElementProcessor {

    // REVIEW: should provide this rendering context, rather than hardcoding.
    // the net effect currently is that class members annotated with 
    // @Hidden(where=Where.ANYWHERE) or @Disabled(where=Where.ANYWHERE) will indeed
    // be hidden/disabled, but will be visible/enabled (perhaps incorrectly) 
    // for any other value for Where
    private final Where where = Where.ANYWHERE;

    @Override
    public void process(final Request request) {
        final String className = request.getOptionalProperty(CLASS);
        final String id = request.getOptionalProperty(OBJECT);
        final String fieldName = request.getRequiredProperty(FIELD);
        final ObjectAdapter object = request.getContext().getMappedObjectOrResult(id);
        final ObjectAssociation field = object.getSpecification().getAssociation(fieldName);
        if (field == null) {
            throw new ScimpiException("No field " + fieldName + " in " + object.getSpecification().getFullIdentifier());
        }
        if (field.isVisible(ExpressiveObjectsContext.getAuthenticationSession(), object, where).isVetoed()) {
            throw new ForbiddenException(field, ForbiddenException.VISIBLE);
        }
        final boolean isIconShowing = request.isRequested(SHOW_ICON, showIconByDefault());
        final int truncateTo = Integer.valueOf(request.getOptionalProperty(TRUNCATE, "0")).intValue();

        write(request, object, field, null, className, isIconShowing, truncateTo);
    }

    @Override
    public String getName() {
        return "field";
    }

    public static void write(final Request request, final ObjectAdapter object, final ObjectAssociation field, final LinkedObject linkedField, final String className, final boolean showIcon, final int truncateTo) {

        final ObjectAdapter fieldReference = field.get(object);

        if (fieldReference != null) {
            final String classSection = "class=\"" + (className == null ? "value" : className) + "\"";
            request.appendHtml("<span " + classSection + ">");
            if (field.isOneToOneAssociation()) {
                try {
                    ExpressiveObjectsContext.getPersistenceSession().resolveImmediately(fieldReference);
                } catch (final ObjectNotFoundException e) {
                    request.appendHtml(e.getMessage() + "</span>");
                }
            }

            if (!field.getSpecification().containsFacet(ParseableFacet.class) && showIcon) {
                request.appendHtml("<img class=\"small-icon\" src=\"" + request.getContext().imagePath(fieldReference) + "\" alt=\"" + field.getSpecification().getShortIdentifier() + "\"/>");
            }

            if (linkedField != null) {
                final String id = request.getContext().mapObject(fieldReference, linkedField.getScope(), Scope.INTERACTION);
                request.appendHtml("<a href=\"" + linkedField.getForwardView() + "?" + linkedField.getVariable() + "=" + id + request.getContext().encodedInteractionParameters() + "\">");
            }
            String value = fieldReference == null ? "" : fieldReference.titleString();
            if (truncateTo > 0 && value.length() > truncateTo) {
                value = value.substring(0, truncateTo) + "...";
            }

            // TODO figure out a better way to determine if boolean or a
            // password
            final ObjectSpecification spec = field.getSpecification();
            final BooleanValueFacet facet = spec.getFacet(BooleanValueFacet.class);
            if (facet != null) {
                final boolean flag = facet.isSet(fieldReference);
                final String valueSegment = flag ? " checked=\"checked\"" : "";
                final String disabled = " disabled=\"disabled\"";
                request.appendHtml("<input type=\"checkbox\"" + valueSegment + disabled + " />");
            } else {
                request.appendAsHtmlEncoded(value);
            }

            if (linkedField != null) {
                request.appendHtml("</a>");
            }
            request.appendHtml("</span>");
        }
    }

}
