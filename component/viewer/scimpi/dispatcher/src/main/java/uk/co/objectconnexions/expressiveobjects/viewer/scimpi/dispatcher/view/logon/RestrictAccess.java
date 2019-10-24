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
package uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.view.logon;

import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.AbstractElementProcessor;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.Dispatcher;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.processor.Request;

public class RestrictAccess extends AbstractElementProcessor {
    private static final String LOGIN_VIEW = "login-view";
    private static final String DEFAULT_LOGIN_VIEW = "login." + Dispatcher.EXTENSION;

    public String getName() {
        return "restrict-access";
    }

    public void process(Request request) {
        if (!request.getContext().isUserAuthenticated()) { 
            final String view = request.getOptionalProperty(LOGIN_VIEW, DEFAULT_LOGIN_VIEW);
            request.getContext().redirectTo(view);
        }
    }

}

