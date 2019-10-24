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

import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.ObjectNotFoundException;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.AbstractElementProcessor;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.context.RequestContext;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.context.RequestContext.Scope;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.processor.Request;

public class InitializeFromCookie extends AbstractElementProcessor {
    private static final String SEVEN_DAYS = Integer.toString(60 * 24 * 7);

    @Override
    public void process(final Request request) {
        final String name = request.getRequiredProperty(NAME);

        final RequestContext context = request.getContext();
        if (context.getVariable(name) != null) {
            request.skipUntilClose();
        } else {
            final String scopeName = request.getOptionalProperty(SCOPE);
            final Scope scope = RequestContext.scope(scopeName, Scope.SESSION);

            final String cookieName = request.getOptionalProperty("cookie", name);
            final String cookieValue = context.getCookie(cookieName);
            boolean hasObject;
            if (cookieValue != null) {
                try {
                    context.getMappedObject(cookieValue);
                    hasObject = true;
                } catch (final ObjectNotFoundException e) {
                    hasObject = false;
                }
            } else {
                hasObject = false;
            }

            if (hasObject) {
                request.skipUntilClose();
                context.addVariable(name, cookieValue, scope);
            } else {
                final String expiresString = request.getOptionalProperty("expires", SEVEN_DAYS);
                request.pushNewBuffer();
                request.processUtilCloseTag();
                request.popBuffer();
                final String id = (String) context.getVariable(RequestContext.RESULT);
                final ObjectAdapter variable = context.getMappedObject(id);
                if (variable != null) {
                    context.addCookie(cookieName, id, Integer.valueOf(expiresString));
                    context.addVariable(name, id, scope);
                }
            }
        }
    }

    @Override
    public String getName() {
        return "initialize-from-cookie";
    }

}
