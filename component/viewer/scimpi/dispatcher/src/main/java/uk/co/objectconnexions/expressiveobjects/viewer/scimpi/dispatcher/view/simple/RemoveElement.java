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

package uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.simple;

import java.util.List;

import uk.co.objectconnexions.expressiveobjects.applib.annotation.Where;
import uk.co.objectconnexions.expressiveobjects.applib.filter.Filter;
import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSession;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.Consent;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociation;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociationFilters;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.OneToManyAssociation;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.AbstractElementProcessor;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.Dispatcher;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.ForbiddenException;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.ScimpiException;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.context.RequestContext;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.context.RequestContext.Scope;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.edit.RemoveAction;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.processor.Request;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.util.MethodsUtils;

public class RemoveElement extends AbstractElementProcessor {

    // REVIEW: should provide this rendering context, rather than hardcoding.
    // the net effect currently is that class members annotated with 
    // @Hidden(where=Where.ANYWHERE) or @Disabled(where=Where.ANYWHERE) will indeed
    // be hidden/disabled, but will be visible/enabled (perhaps incorrectly) 
    // for any other value for Where
    private final static Where where = Where.ANYWHERE;

    @Override
    public void process(final Request request) {
        final String title = request.getOptionalProperty(BUTTON_TITLE, "Remove From List");
        final String cls = request.getOptionalProperty(CLASS, "action in-line delete confirm");
        final String object = request.getOptionalProperty(OBJECT);
        final String resultOverride = request.getOptionalProperty(RESULT_OVERRIDE);
        final RequestContext context = request.getContext();
        final String objectId = object != null ? object : (String) context.getVariable(RequestContext.RESULT);
        final ObjectAdapter adapter = MethodsUtils.findObject(context, objectId);

        final String element = request.getOptionalProperty(ELEMENT, (String) context.getVariable(ELEMENT));
        final ObjectAdapter elementId = MethodsUtils.findObject(context, element);

        final String fieldName = request.getRequiredProperty(FIELD);

        String view = request.getOptionalProperty(VIEW);
        view = context.fullFilePath(view == null ? context.getResourceFile() : view);
        String error = request.getOptionalProperty(ERROR);
        error = context.fullFilePath(error == null ? context.getResourceFile() : error);

        request.processUtilCloseTag();

        write(request, adapter, fieldName, elementId, resultOverride, view, error, title, cls);
    }

    @Override
    public String getName() {
        return "remove-element";
    }

    public static void write(final Request request, final ObjectAdapter adapter, final String fieldName, final ObjectAdapter element, final String resultOverride, final String view, final String error, final String title, final String cssClass) {
        final ObjectAssociation field = adapter.getSpecification().getAssociation(fieldName);
        if (field == null) {
            throw new ScimpiException("No field " + fieldName + " in " + adapter.getSpecification().getFullIdentifier());
        }
        if (!field.isOneToManyAssociation()) {
            throw new ScimpiException("Field " + fieldName + " not a collection, in " + adapter.getSpecification().getFullIdentifier());
        }
        if (field.isVisible(ExpressiveObjectsContext.getAuthenticationSession(), adapter, where).isVetoed()) {
            throw new ForbiddenException(field, ForbiddenException.VISIBLE);
        }
        ExpressiveObjectsContext.getPersistenceSession().resolveField(adapter, field);

        Consent usable = field.isUsable(ExpressiveObjectsContext.getAuthenticationSession(), adapter, where);
        if (usable.isAllowed()) {
            usable = ((OneToManyAssociation) field).isValidToRemove(adapter, element);
        }

        if (usable.isVetoed()) {
            request.appendHtml("<div class=\"" + cssClass + " disabled-form\">"); 
            request.appendHtml("<div class=\"button disabled\" title=\""); 
            request.appendAsHtmlEncoded(usable.getReason());
            request.appendHtml("\" >" + title);
            request.appendHtml("</div>");
            request.appendHtml("</div>");
        } else {
            if (valid(request, adapter)) {
                final String classSegment = " class=\"" + cssClass + "\"";

                final String objectId = request.getContext().mapObject(adapter, Scope.INTERACTION);
                final String elementId = request.getContext().mapObject(element, Scope.INTERACTION);
                final String action = RemoveAction.ACTION + Dispatcher.COMMAND_ROOT;
                request.appendHtml("<form" + classSegment + " method=\"post\" action=\"" + action + "\" >");
                request.appendHtml("<input type=\"hidden\" name=\"" + OBJECT + "\" value=\"" + objectId + "\" />");
                request.appendHtml("<input type=\"hidden\" name=\"" + FIELD + "\" value=\"" + fieldName + "\" />");
                request.appendHtml("<input type=\"hidden\" name=\"" + ELEMENT + "\" value=\"" + elementId + "\" />");
                if (resultOverride != null) {
                    request.appendHtml("<input type=\"hidden\" name=\"" + RESULT_OVERRIDE + "\" value=\"" + resultOverride + "\" />");
                }
                request.appendHtml("<input type=\"hidden\" name=\"" + VIEW + "\" value=\"" + view + "\" />");
                request.appendHtml("<input type=\"hidden\" name=\"" + ERROR + "\" value=\"" + error + "\" />");
                request.appendHtml(request.getContext().interactionFields());
                request.appendHtml("<input class=\"button\" type=\"submit\" value=\"" + title + "\" />");
                request.appendHtml("</form>");
            }
        }
    }

    private static boolean valid(final Request request, final ObjectAdapter adapter) {
        // TODO is this check valid/necessary?

        // TODO check is valid to remove element
        final AuthenticationSession session = ExpressiveObjectsContext.getAuthenticationSession();
        final Filter<ObjectAssociation> filter = ObjectAssociationFilters.dynamicallyVisible(session, adapter, where);
        final List<ObjectAssociation> visibleFields = adapter.getSpecification().getAssociations(filter);
        if (visibleFields.size() == 0) {
            return false;
        }

        return true;
    }
}
