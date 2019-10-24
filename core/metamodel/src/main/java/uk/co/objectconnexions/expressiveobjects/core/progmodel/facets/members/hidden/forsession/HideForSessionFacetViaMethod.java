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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.members.hidden.forsession;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSession;
import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSessionUtils;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.util.InvokeUtils;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.ImperativeFacet;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.members.hidden.HideForSessionFacetAbstract;

public class HideForSessionFacetViaMethod extends HideForSessionFacetAbstract implements ImperativeFacet {

    private final Method method;

    public HideForSessionFacetViaMethod(final Method method, final FacetHolder holder) {
        super(holder);
        this.method = method;
    }

    /**
     * Returns a singleton list of the {@link Method} provided in the
     * constructor.
     */
    @Override
    public List<Method> getMethods() {
        return Collections.singletonList(method);
    }

    @Override
    public boolean impliesResolve() {
        return true;
    }

    @Override
    public boolean impliesObjectChanged() {
        return false;
    }

    /**
     * Will only check provided that a {@link AuthenticationSession} has been
     * provided.
     */
    @Override
    public String hiddenReason(final AuthenticationSession session) {
        if (session == null) {
            return null;
        }
        final int len = method.getParameterTypes().length;
        final Object[] parameters = new Object[len];
        parameters[0] = AuthenticationSessionUtils.createUserMemento(session);
        // TODO: need to change to pick up as non-static rather than static
        final Boolean isHidden = (Boolean) InvokeUtils.invokeStatic(method, parameters);
        return isHidden.booleanValue() ? "Hidden" : null;
    }

    @Override
    protected String toStringValues() {
        return "method=" + method;
    }

}
