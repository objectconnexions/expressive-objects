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

package uk.co.objectconnexions.expressiveobjects.core.runtime.authorization.standard;

import java.util.List;

import uk.co.objectconnexions.expressiveobjects.core.commons.config.InstallerAbstract;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authorization.AuthorizationManager;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authorization.AuthorizationManagerInstaller;

public abstract class AuthorizationManagerStandardInstallerAbstract extends InstallerAbstract implements AuthorizationManagerInstaller {

    public AuthorizationManagerStandardInstallerAbstract(final String name) {
        super(AuthorizationManagerInstaller.TYPE, name);
    }

    @Override
    public AuthorizationManager createAuthorizationManager() {
        final AuthorizationManagerStandard authorizationManager = new AuthorizationManagerStandard(getConfiguration());
        final Authorizor authorizor = createAuthorizor(getConfiguration());
        authorizationManager.setAuthorizor(authorizor);
        return authorizationManager;
    }

    /**
     * Hook method
     * 
     * @return
     */
    protected abstract Authorizor createAuthorizor(final ExpressiveObjectsConfiguration configuration);

    @Override
    public List<Class<?>> getTypes() {
        return listOf(AuthorizationManager.class);
    }

}
