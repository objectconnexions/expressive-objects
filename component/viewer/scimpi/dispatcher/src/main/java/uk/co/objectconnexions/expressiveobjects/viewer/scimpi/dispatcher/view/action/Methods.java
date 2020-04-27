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

package uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.action;

import java.util.List;

import uk.co.objectconnexions.expressiveobjects.applib.annotation.Where;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ActionType;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectActionSet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAction;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectActionContainer.Contributed;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.AbstractElementProcessor;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.Dispatcher;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.context.RequestContext;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.context.RequestContext.Scope;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.processor.Request;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.util.MethodsUtils;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.field.InclusionList;

public class Methods extends AbstractElementProcessor {

    // REVIEW: should provide this rendering context, rather than hardcoding.
    // the net effect currently is that class members annotated with 
    // @Hidden(where=Where.ANYWHERE) or @Disabled(where=Where.ANYWHERE) will indeed
    // be hidden/disabled, but will be visible/enabled (perhaps incorrectly) 
    // for any other value for Where
    private final static Where where = Where.ANYWHERE;

    @Override
    public void process(final Request request) {
        String objectId = request.getOptionalProperty(OBJECT);
        final String view = request.getOptionalProperty(VIEW, "_generic_action." + Dispatcher.EXTENSION);
        final String cancelTo = request.getOptionalProperty(CANCEL_TO);
        final boolean showForms = request.isRequested(FORMS, false);
        final String labelDelimiter = request.getOptionalProperty(LABEL_DELIMITER, "");

        final ObjectAdapter object = MethodsUtils.findObject(request.getContext(), objectId);
        if (objectId == null) {
            objectId = request.getContext().mapObject(object, null);
        }

        final InclusionList inclusionList = new InclusionList();
        request.setBlockContent(inclusionList);
        request.processUtilCloseTag();

        request.appendHtml("<div class=\"methods\">");
        if (inclusionList.includes("edit") && !object.getSpecification().isService()) {
            request.appendHtml("<div class=\"action\">");
            request.appendHtml("<a class=\"button\" href=\"_generic_edit." + Dispatcher.EXTENSION + "?" + RequestContext.RESULT
                    + "=" + objectId + "\">Edit...</a>");
            request.appendHtml("</div>");
        }
        writeMethods(request, objectId, object, showForms, inclusionList, view, "_generic.shtml?" + RequestContext.RESULT
                + "=" + objectId, labelDelimiter);
        request.popBlockContent();
        request.appendHtml("</div>");
    }

    public static void writeMethods(
            final Request request,
            final String objectId,
            final ObjectAdapter adapter,
            final boolean showForms,
            final InclusionList inclusionList,
            final String view,
            final String cancelTo,
            final String labelDelimeter) {
        List<ObjectAction> actions = adapter.getSpecification().getObjectActions(ActionType.USER, Contributed.INCLUDED);
        writeMethods(request, adapter, actions, objectId, showForms, inclusionList, view, cancelTo, labelDelimeter);
        // TODO determine if system is set up to display exploration methods
        if (true) {
            actions = adapter.getSpecification().getObjectActions(ActionType.EXPLORATION, Contributed.INCLUDED);
            writeMethods(request, adapter, actions, objectId, showForms, inclusionList, view, cancelTo, labelDelimeter);
        }
        // TODO determine if system is set up to display debug methods
        if (true) {
            actions = adapter.getSpecification().getObjectActions(ActionType.DEBUG, Contributed.INCLUDED);
            writeMethods(request, adapter, actions, objectId, showForms, inclusionList, view, cancelTo, labelDelimeter);
        }
    }

    private static void writeMethods(
            final Request request,
            final ObjectAdapter adapter,
            List<ObjectAction> actions,
            final String objectId,
            final boolean showForms,
            final InclusionList inclusionList,
            final String view,
            final String cancelTo,
            final String labelDelimeter) {
        actions = inclusionList.includedActions(actions);
        for (int j = 0; j < actions.size(); j++) {
            final ObjectAction action = actions.get(j);
            if (action instanceof ObjectActionSet) {
                request.appendHtml("<div class=\"subset\">");
                writeMethods(request, adapter, action.getActions(), objectId, showForms, inclusionList, view, cancelTo, labelDelimeter);
                request.appendHtml("</div>");
            } else if (action.isContributed()) {
                if (action.getParameterCount() == 1 && adapter.getSpecification().isOfType(action.getParameters().get(0).getSpecification())) {
                    if (objectId != null) {
                        final ObjectAdapter target = request.getContext().getMappedObject(objectId);
                        final ObjectAdapter realTarget = action.realTarget(target);
                        final String realTargetId = request.getContext().mapObject(realTarget, Scope.INTERACTION);
                        writeMethod(request, adapter, new String[] { objectId }, action, realTargetId, showForms, view, cancelTo, labelDelimeter);
                    } else {
                        request.appendHtml("<div class=\"button\">");
                        request.appendAsHtmlEncoded(action.getName());
                        request.appendHtml("???</div>");
                    }
                } else if (!adapter.getSpecification().isService()) {
                    writeMethod(request, adapter, new String[0], action, objectId, showForms, view, cancelTo, labelDelimeter);
                }
            } else {
                writeMethod(request, adapter, new String[0], action, objectId, showForms, view, cancelTo, labelDelimeter);
            }
        }
    }

    private static void writeMethod(
            final Request request,
            final ObjectAdapter adapter,
            final String[] parameters,
            final ObjectAction action,
            final String objectId,
            final boolean showForms,
            final String view,
            final String cancelTo,
            final String labelDelimeter) {
        // if (action.isVisible(ExpressiveObjectsContext.getSession(), null) &&
        // action.isVisible(ExpressiveObjectsContext.getSession(), adapter))
        // {
        if (action.isVisible(ExpressiveObjectsContext.getAuthenticationSession(), adapter, where).isAllowed()) {
            request.appendHtml("<div class=\"action\">");
            if (ExpressiveObjectsContext.getSession() == null) {
                request.appendHtml("<span class=\"disabled\" title=\"no user logged in\">");
                request.appendAsHtmlEncoded(action.getName());
                request.appendHtml("</span>");
                /*
                 * } else if (action.isUsable(ExpressiveObjectsContext.getSession(),
                 * null).isVetoed()) {
                 * request.appendHtml("<span class=\"disabled\" title=\"" +
                 * action.isUsable(ExpressiveObjectsContext.getSession(), null).getReason() +
                 * "\">"); request.appendHtml(action.getName());
                 * request.appendHtml("</span>");
                 */
            } else if (action.isUsable(ExpressiveObjectsContext.getAuthenticationSession(), adapter, where).isVetoed()) {
                request.appendHtml("<span class=\"disabled\" title=\"Disabled: " + action.isUsable(ExpressiveObjectsContext.getAuthenticationSession(), adapter, where).getReason() + "\">");
                request.appendAsHtmlEncoded(action.getName());
                request.appendHtml("</span>");
            } else {
                final String version = request.getContext().mapVersion(adapter);
                if (action.getParameterCount() == 0 || (action.isContributed() && action.getParameterCount() == 1)) {
                    ActionButton.write(request, adapter, action, parameters, version, "_generic." + Dispatcher.EXTENSION, null, null, null, null, null, null, null, null, null);
                } else if (showForms) {
                    final CreateFormParameter params = new CreateFormParameter();
                    params.objectId = objectId;
                    params.methodName = action.getId();
                    params.forwardResultTo = "_generic." + Dispatcher.EXTENSION;
                    params.buttonTitle = "OK";
                    params.formTitle = action.getName();
                    params.labelDelimiter = labelDelimeter;
                    ActionForm.createForm(request, params, true);
                } else {
                    request.appendHtml("<a class=\"button\" href=\"" + view + "?" + RequestContext.RESULT
                            + "=" + objectId + "&amp;_" + VERSION + "=" + version + "&amp;_" + METHOD + "=" + action.getId());
                    if (cancelTo != null) {
                        request.appendHtml("&amp;_cancel-to=");
                        request.appendAsHtmlEncoded("cancel-to=\"" + cancelTo + "\"");
                    }
                    request.appendHtml("\" title=\"" + action.getDescription() + "\">");
                    request.appendAsHtmlEncoded(action.getName() + "...");
                    request.appendHtml("</a>");
                }
            }
            request.appendHtml("</div>");
        }
    }

    @Override
    public String getName() {
        return "methods";
    }

}
