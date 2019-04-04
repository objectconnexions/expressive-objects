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

import java.util.Date;

import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.AbstractElementProcessor;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.context.RequestContext;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.context.RequestContext.Scope;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.processor.Request;

public class UniqueId extends AbstractElementProcessor {

    private static long next = new Date().getTime() % 100000;

    @Override
    public String getName() {
        return "unique-id";
    }

    @Override
    public void process(final Request request) {
        final String variableName = request.getOptionalProperty(NAME, "id");
        final String prefix = request.getOptionalProperty(TYPE);
        final String scopeName = request.getOptionalProperty(SCOPE);
        final Scope scope = RequestContext.scope(scopeName, Scope.REQUEST);

        String id;
        synchronized(this) {
            id = String.valueOf(next++);
        }
        if (prefix != null) {
            id = prefix + id;
        }
        
        request.appendDebug("    Unique ID " + variableName + " set to " + id);
        request.getContext().addVariable(variableName, id, scope);
    }
}
