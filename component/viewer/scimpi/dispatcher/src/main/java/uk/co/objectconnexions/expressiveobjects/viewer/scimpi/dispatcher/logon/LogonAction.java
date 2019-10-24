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

package uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.logon;

import java.io.IOException;
import java.util.List;

import uk.co.objectconnexions.expressiveobjects.applib.profiles.Localization;
import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSession;
import uk.co.objectconnexions.expressiveobjects.core.commons.debug.DebugBuilder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.parseable.ParseableFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAction;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectActionParameter;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.AuthenticationRequestPassword;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.Action;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.Dispatcher;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.ScimpiException;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.UserManager;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.context.RequestContext;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.context.RequestContext.Scope;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.edit.FieldEditState;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.edit.FormState;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.util.MethodsUtils;


// TODO this should work like EditAction so that logon page is repopulated
public class LogonAction implements Action {

    @Override
    public void process(final RequestContext context) throws IOException {
        final String username = context.getParameter("username");
        final String password = context.getParameter("password");
        final String actualFormId = context.getParameter("_" + FORM_ID);
        final String expectedFormId = context.getParameter(LOGON_FORM_ID);
        boolean isDomainLogon = expectedFormId != null && expectedFormId.equals(actualFormId);
        boolean isValid;

        AuthenticationSession session = null;
        if (username.length() == 0 || password.length() == 0) {
            isValid = false;
        } else {
            if (isDomainLogon) {
                final String objectId = context.getParameter(LOGON_OBJECT);
                final String scope = context.getParameter(LOGON_SCOPE);
                final String methodName = context.getParameter(LOGON_METHOD);
                String resultName = context.getParameter(LOGON_RESULT_NAME);
                resultName = resultName == null ? "_" + USER : resultName;

                final ObjectAdapter object = MethodsUtils.findObject(context, objectId);
                final ObjectAction action = MethodsUtils.findAction(object, methodName);
                final int parameterCount = action.getParameterCount();
                final ObjectAdapter[] parameters = new ObjectAdapter[parameterCount];
                List<ObjectActionParameter> parameters2 = action.getParameters();
                if (parameters.length != 2) {
                    throw new ScimpiException("Expected two parameters for the log-on method: " + methodName);
                }

                Localization localization = ExpressiveObjectsContext.getLocalization(); 
                ParseableFacet facet = parameters2.get(0).getSpecification().getFacet(ParseableFacet.class);
                parameters[0] = facet.parseTextEntry(null, username, localization);
                facet = parameters2.get(1).getSpecification().getFacet(ParseableFacet.class);
                parameters[1] = facet.parseTextEntry(null, password, localization);
                final ObjectAdapter result = action.execute(object, parameters);
                isValid = result != null;
                if (isValid) {
                    Scope scope2 = scope == null ? Scope.SESSION : RequestContext.scope(scope);
                    final String resultId = context.mapObject(result, scope2);
                    context.addVariable(resultName, resultId, scope);
                    context.addVariable("_username", username, Scope.SESSION);
                    
                    context.clearVariable(LOGON_OBJECT, Scope.SESSION);
                    context.clearVariable(LOGON_METHOD, Scope.SESSION);
                    context.clearVariable(LOGON_RESULT_NAME, Scope.SESSION);
                    context.clearVariable(LOGON_SCOPE, Scope.SESSION);
                    context.clearVariable(PREFIX + "expressive-objects-user", Scope.SESSION);
                    context.clearVariable(LOGON_FORM_ID, Scope.SESSION);
                }
                session = context.getSession();
            } else {
                session = UserManager.authenticate(new AuthenticationRequestPassword(username, password));
                isValid = session != null;
            }
        }

        String view;
        if (!isValid) {
            final FormState formState = new FormState();
            formState.setForm(actualFormId);
            formState.setError("Failed to login. Check the username and ensure that your password was entered correctly");
            FieldEditState fieldState = formState.createField("username", username);
            if (username.length() == 0) {
                fieldState.setError("User Name required");
            }
            fieldState = formState.createField("password", password);
            if (password.length() == 0) {
                fieldState.setError("Password required");
            }
            if (username.length() == 0 || password.length() == 0) {
                formState.setError("Both the user name and password must be entered");
            }
            context.addVariable(ENTRY_FIELDS, formState, Scope.REQUEST);

            view = context.getParameter(ERROR);
            context.setRequestPath("/" + view, Dispatcher.ACTION);
        } else {
            context.setSession(session);
            context.startHttpSession();
            context.setUserAuthenticated(true);
            view = context.getParameter(VIEW);
            if (view == null) {
                // REVIEW this is duplicated in Logon.java
                view = "start." + Dispatcher.EXTENSION;
            }
            context.redirectTo(view);
        }
    }

    @Override
    public String getName() {
        return "logon";
    }

    @Override
    public void init() {}

    @Override
    public void debug(final DebugBuilder debug) {}
}
