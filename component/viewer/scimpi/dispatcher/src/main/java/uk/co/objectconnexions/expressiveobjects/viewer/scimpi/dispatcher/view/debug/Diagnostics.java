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

import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSession;
import uk.co.objectconnexions.expressiveobjects.core.commons.debug.DebugHtmlString;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.AbstractElementProcessor;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.context.RequestContext;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.processor.Request;

public class Diagnostics extends AbstractElementProcessor {

    @Override
    public void process(final Request request) {
        if (request.getContext().isDebugDisabled()) {
            return;
        }

        final String type = request.getOptionalProperty(TYPE, "page");
        final boolean isForced = request.isRequested("force");
        if (isForced || request.getContext().showDebugData()) {
            request.appendHtml("<div class=\"debug\">");
            if ("page".equals(type)) {
                request.appendHtml("<pre>");
                final RequestContext context = request.getContext();
                request.appendHtml("URI:  " + context.getUri());
                request.appendHtml("\n");
                request.appendHtml("File: " + context.fullFilePath(context.getResourceFile()));
                final String result = (String) request.getContext().getVariable(RequestContext.RESULT);
                if (result != null) {
                    request.appendHtml("\n");
                    request.appendHtml("Object: " + result);
                }
                request.appendHtml("</pre>");
            } else if ("session".equals(type)) {
                request.appendHtml("<pre>");
                final AuthenticationSession session = ExpressiveObjectsContext.getAuthenticationSession();
                request.appendHtml("Session:  " + session.getUserName() + " " + session.getRoles());
                request.appendHtml("</pre>");
            } else if ("variables".equals(type)) {
                final RequestContext context = request.getContext();
                final DebugHtmlString debug = new DebugHtmlString();
                debug.appendln("", "");
                context.append(debug, "variables");
                debug.close();
                request.appendHtml(debug.toString());
            } else if ("processing".equals(type)) {
                request.appendHtml("<pre>");
                request.appendHtml(request.getContext().getDebugTrace());
                request.appendHtml("</pre>");
            } else {
                request.appendHtml("<i>No such type " + type + "</i>");
            }
            request.appendHtml("</div>");
        }
    }

    @Override
    public String getName() {
        return "diagnostics";
    }

}
