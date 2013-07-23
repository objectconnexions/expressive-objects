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

import java.net.URLEncoder;

import org.apache.commons.lang.StringEscapeUtils;
import uk.co.objectconnexions.expressiveobjects.applib.annotation.Where;
import uk.co.objectconnexions.expressiveobjects.applib.profiles.Localization;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.parseable.ParseableFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAction;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectActionParameter;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.AbstractElementProcessor;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.context.RequestContext;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.context.RequestContext.Scope;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.processor.Request;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.util.MethodsUtils;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.HelpLink;

public class ActionLink extends AbstractElementProcessor {

    // REVIEW: confirm this rendering context
    private final Where where = Where.OBJECT_FORMS;

    @Override
    public void process(final Request request) {
        String objectId = request.getOptionalProperty(OBJECT);
        final String method = request.getOptionalProperty(METHOD);
        final String forwardResultTo = request.getOptionalProperty(VIEW);
        final String forwardVoidTo = request.getOptionalProperty(VOID);
        String resultOverride = request.getOptionalProperty(RESULT_OVERRIDE);
        
        final String resultName = request.getOptionalProperty(RESULT_NAME);
        final String resultNameSegment = resultName == null ? "" : "&amp;" + RESULT_NAME + "=" + resultName;
        final String scope = request.getOptionalProperty(SCOPE);
        final String scopeSegment = scope == null ? "" : "&amp;" + SCOPE + "=" + scope;
        final String confirm = request.getOptionalProperty(CONFIRM);
        final String completionMessage = request.getOptionalProperty(MESSAGE);
        final String idName = request.getOptionalProperty(ID, method);
        final String className = request.getOptionalProperty(CLASS);

        
        // TODO need a mechanism for globally dealing with encoding; then use
        // the new encode method
        final String confirmSegment = confirm == null ? "" : "&amp;" + "_" + CONFIRM + "=" + URLEncoder.encode(confirm);
        final String messageSegment = completionMessage == null ? "" : "&amp;" + "_" + MESSAGE + "=" + URLEncoder.encode(completionMessage);

        final RequestContext context = request.getContext();
        final ObjectAdapter object = MethodsUtils.findObject(context, objectId);
        final String version = context.mapVersion(object);
        final ObjectAction action = MethodsUtils.findAction(object, method);

        final ActionContent parameterBlock = new ActionContent(action);
        request.setBlockContent(parameterBlock);
        request.pushNewBuffer();
        request.processUtilCloseTag();
        final String text = request.popBuffer();
        
        final String[] parameters = parameterBlock.getParameters();
        final String target;
        /*
        if (action.isContributed()) {
            System.arraycopy(parameters, 0, parameters, 1, parameters.length - 1);
            parameters[0] = request.getContext().mapObject(object, Scope.REQUEST);
            target =  request.getContext().mapObject(action.realTarget(object), Scope.REQUEST);
            if (!action.hasReturn() && resultOverride == null) {
                resultOverride = parameters[0];
            }
        } else {
            target =  StringEscapeUtils.escapeHtml(request.getContext().mapObject(object, Scope.INTERACTION));
        }
         */
        
        final ObjectAdapter[] objectParameters;
        
        // TODO copied from ActionButton
        //final ObjectAdapter target;
        if (action.isContributed()) {
            objectParameters= null;
            System.arraycopy(parameters, 0, parameters, 1, parameters.length - 1);
            parameters[0] = request.getContext().mapObject(object, Scope.REQUEST);
         //   target =  action.realTarget(object);
            target =  request.getContext().mapObject(action.realTarget(object), Scope.REQUEST);
            if (!action.hasReturn() && resultOverride == null) {
                resultOverride = parameters[0];
            }
        } else {
            objectParameters = new ObjectAdapter[parameters.length];
            // target = object;
            target =  StringEscapeUtils.escapeHtml(request.getContext().mapObject(object, Scope.INTERACTION));
            int i = 0;
            for (final ObjectActionParameter spec : action.getParameters()) {
                final ObjectSpecification type = spec.getSpecification();
                if (parameters[i] == null) {
                    objectParameters[i] = null;
                } else if (type.getFacet(ParseableFacet.class) != null) {
                    final ParseableFacet facet = type.getFacet(ParseableFacet.class);
                    Localization localization = ExpressiveObjectsContext.getLocalization(); 
                    objectParameters[i] = facet.parseTextEntry(null, parameters[i], localization); 
                } else {
                    objectParameters[i] = MethodsUtils.findObject(request.getContext(), parameters[i]);
                }
                i++;
            }
        }

        if (MethodsUtils.isVisibleAndUsable(object, action, where)  && MethodsUtils.canRunMethod(object, action, objectParameters).isAllowed()) {
            writeLink(request, idName, className, target, version, method, forwardResultTo, forwardVoidTo, resultNameSegment, resultOverride, scopeSegment,
                    confirmSegment, messageSegment, context, action, parameters, text);
        }
        request.popBlockContent();
    }

    public static void writeLink(
            final Request request,
            final String idName,
            final String className,
            final String objectId,
            final String version,
            final String method,
            final String forwardResultTo,
            final String forwardVoidTo,
            final String resultNameSegment,
            final String resultOverride,
            final String scopeSegment,
            final String confirmSegment,
            final String messageSegment,
            final RequestContext context,
            final ObjectAction action,
            final String[] parameters,
            String text) {
        text = text == null || text.trim().equals("") ? action.getName() : text;

        String parameterSegment = "";
        for (int i = 0; i < parameters.length; i++) {
            parameterSegment += "&param" + (i + 1) + "=" + parameters[i];
        }

        final String idSegment = idName == null ? "" : ("id=\"" + idName + "\" ");
        final String classSegment = "class=\"" + (className == null ? "action in-line" : className) + "\"";
        final String interactionParamters = context.encodedInteractionParameters();
        final String forwardResultSegment = forwardResultTo == null ? "" : "&amp;" + "_" + VIEW + "=" + context.fullFilePath(forwardResultTo);
        final String resultOverrideSegment = resultOverride == null ? "" : "&amp;" + "_" + RESULT_OVERRIDE + "=" + resultOverride;
        final String voidView = context.fullFilePath(forwardVoidTo == null ? context.getResourceFile() : forwardVoidTo);
        final String forwardVoidSegment = "&amp;" + "_" + VOID + "=" + voidView;
        request.appendHtml("<a " + idSegment + classSegment + " href=\"action.app?" + "_" + OBJECT + "=" + objectId + "&amp;" + "_" + VERSION + "=" + version
                + "&amp;" + "_" + METHOD + "=" + method + resultOverrideSegment + forwardResultSegment + forwardVoidSegment + resultNameSegment
                + parameterSegment + scopeSegment + confirmSegment + messageSegment + interactionParamters + "\">");
        request.appendHtml(text);
        request.appendHtml("</a>");
        HelpLink.append(request, action.getDescription(), action.getHelp());
    }

    @Override
    public String getName() {
        return "action-link";
    }

}
