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

import java.util.List;

import uk.co.objectconnexions.expressiveobjects.core.commons.config.InstallerAbstract;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.AuthenticationManager;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.AuthenticationManagerInstaller;

public abstract class AuthenticationManagerStandardInstallerAbstract extends InstallerAbstract implements AuthenticationManagerInstaller {

    public AuthenticationManagerStandardInstallerAbstract(final String name) {
        super(AuthenticationManagerInstaller.TYPE, name);
    }

    @Override
    public final AuthenticationManager createAuthenticationManager() {
        final AuthenticationManagerStandard authenticationManager = createAuthenticationManagerStandard();
        for (final Authenticator authenticator : createAuthenticators(getConfiguration())) {
            authenticationManager.addAuthenticator(authenticator);
        }
        return authenticationManager;
    }

    protected AuthenticationManagerStandard createAuthenticationManagerStandard() {
        return new AuthenticationManagerStandard(getConfiguration());
    }

    /**
     * Hook method
     * 
     * @return
     */
    protected abstract List<Authenticator> createAuthenticators(final ExpressiveObjectsConfiguration configuration);

    @Override
    public List<Class<?>> getTypes() {
        return listOf(AuthenticationManager.class);
    }

}
