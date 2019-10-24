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

package uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.fixture;

import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.AuthenticationRequest;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.AuthenticatorAbstractForDfltRuntime;
import uk.co.objectconnexions.expressiveobjects.core.runtime.fixtures.authentication.AuthenticationRequestLogonFixture;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.DeploymentType;

public class LogonFixtureAuthenticator extends AuthenticatorAbstractForDfltRuntime {

    public LogonFixtureAuthenticator(final ExpressiveObjectsConfiguration configuration) {
        super(configuration);
    }

    /**
     * Can authenticate if a {@link AuthenticationRequestLogonFixture}.
     */
    @Override
    public final boolean canAuthenticate(final Class<? extends AuthenticationRequest> authenticationRequestClass) {
        return AuthenticationRequestLogonFixture.class.isAssignableFrom(authenticationRequestClass);
    }

    /**
     * Valid providing running in {@link DeploymentType#isExploring()
     * exploration} or {@link DeploymentType#isPrototyping() prototyping} mode.
     */
    @Override
    public final boolean isValid(final AuthenticationRequest request) {
        final DeploymentType deploymentType = getDeploymentType();
        return deploymentType.isExploring() || deploymentType.isPrototyping();
    }

}
