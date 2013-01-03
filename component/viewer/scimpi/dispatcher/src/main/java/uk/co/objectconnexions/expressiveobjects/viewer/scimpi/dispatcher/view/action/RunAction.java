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
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAction;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectActionParameter;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.AbstractElementProcessor;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.ForbiddenException;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.context.RequestContext;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.context.RequestContext.Scope;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.processor.Request;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.util.MethodsUtils;

public class RunAction extends AbstractElementProcessor {

    // REVIEW: should provide this rendering context, rather than hardcoding.
    // the net effect currently is that class members annotated with 
    // @Hidden(where=Where.ANYWHERE) or @Disabled(where=Where.ANYWHERE) will indeed
    // be hidden/disabled, but will be visible/enabled (perhaps incorrectly) 
    // for any other value for Where
    private final Where where = Where.ANYWHERE;

    @Override
    public void process(final Request request) {
        final RequestContext context = request.getContext();

        final String objectId = request.getOptionalProperty(OBJECT);
        final ObjectAdapter object = MethodsUtils.findObject(context, objectId);

        final String methodName = request.getRequiredProperty(METHOD);
        final ObjectAction action = MethodsUtils.findAction(object, methodName);

        final String variableName = request.getOptionalProperty(RESULT_NAME);
        final String scopeName = request.getOptionalProperty(SCOPE);

        final ActionContent parameterBlock = new ActionContent(action);
        request.setBlockContent(parameterBlock);
        request.processUtilCloseTag();
        final ObjectAdapter[] parameters = parameterBlock.getParameters(request);

        if (!MethodsUtils.isVisibleAndUsable(object, action, where)) {
            throw new ForbiddenException(action, ForbiddenException.VISIBLE_AND_USABLE);
        }

        // swap null parameter of the object's type to run a contributed method
        if (action.isContributed()) {
            final List<ObjectActionParameter> parameterSpecs = action.getParameters();
            for (int i = 0; i < parameters.length; i++) {
                if (parameters[i] == null && object.getSpecification().isOfType(parameterSpecs.get(i).getSpecification())) {
                    parameters[i] = object;
                    break;
                }
            }
        }

        final Scope scope = RequestContext.scope(scopeName, Scope.REQUEST);
        MethodsUtils.runMethod(context, action, object, parameters, variableName, scope);
        request.popBlockContent();
    }

    @Override
    public String getName() {
        return "run-action";
    }

}
