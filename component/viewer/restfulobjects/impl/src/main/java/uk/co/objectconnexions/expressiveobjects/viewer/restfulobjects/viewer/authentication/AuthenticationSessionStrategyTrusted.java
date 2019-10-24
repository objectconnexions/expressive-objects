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
package uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.authentication;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSession;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.exploration.AuthenticationRequestExploration;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.core.webapp.auth.AuthenticationSessionStrategyDefault;

public class AuthenticationSessionStrategyTrusted extends AuthenticationSessionStrategyDefault {

    @Override
    public AuthenticationSession lookupValid(final ServletRequest servletRequest, final ServletResponse servletResponse) {
        final AuthenticationSession session = super.lookupValid(servletRequest, servletResponse);
        if (session != null) {
            return session;
        }

        // will always succeed.
        final AuthenticationRequestExploration request = new AuthenticationRequestExploration();
        return ExpressiveObjectsContext.getAuthenticationManager().authenticate(request);
    }
}
