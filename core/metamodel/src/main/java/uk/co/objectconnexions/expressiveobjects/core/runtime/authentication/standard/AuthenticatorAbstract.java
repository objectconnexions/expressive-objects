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

package uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.standard;

import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSession;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.AuthenticationRequest;

public abstract class AuthenticatorAbstract implements Authenticator {

    private final ExpressiveObjectsConfiguration configuration;

    // //////////////////////////////////////////////////////
    // constructor
    // //////////////////////////////////////////////////////

    public AuthenticatorAbstract(final ExpressiveObjectsConfiguration configuration) {
        this.configuration = configuration;
    }

    // //////////////////////////////////////////////////////
    // init, shutdown
    // //////////////////////////////////////////////////////

    @Override
    public void init() {
        // does nothing.
    }

    @Override
    public void shutdown() {
        // does nothing.
    }

    // //////////////////////////////////////////////////////
    // API
    // //////////////////////////////////////////////////////

    /**
     * Default implementation returns a {@link SimpleSession}; can be overridden
     * if required.
     */
    @Override
    public AuthenticationSession authenticate(final AuthenticationRequest request, final String code) {
        if (!isValid(request)) {
            return null;
        }
        return new SimpleSession(request.getName(), request.getRoles(), code);
    }

    // //////////////////////////////////////////////////////
    // Injected (via constructor)
    // //////////////////////////////////////////////////////

    public ExpressiveObjectsConfiguration getConfiguration() {
        return configuration;
    }

}
