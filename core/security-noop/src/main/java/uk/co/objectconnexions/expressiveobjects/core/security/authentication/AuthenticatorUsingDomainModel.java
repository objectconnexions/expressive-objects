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

package uk.co.objectconnexions.expressiveobjects.core.security.authentication;

import uk.co.objectconnexions.expressiveobjects.applib.ApplicationException;
import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSession;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.mgr.AdapterManager;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAction;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.AuthenticationRequest;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.AuthenticationRequestPassword;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.standard.AuthenticatorAbstract;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.internal.InitialisationSession;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.PersistenceSession;

/**
 * Implementation that bypasses authentication.
 * 
 * <p>
 * Intended for testing use only.
 */
public class AuthenticatorUsingDomainModel extends AuthenticatorAbstract {

    private String loginService;

    public AuthenticatorUsingDomainModel(final ExpressiveObjectsConfiguration configuration) {
        super(configuration);
        
        loginService = "users";
    }

    @Override
    public boolean isValid(final AuthenticationRequest request) {
            AuthenticationSession authenticationSession = new InitialisationSession(); // new SimpleSession(name, new String[0]);
            ExpressiveObjectsContext.openSession(authenticationSession);
            
            PersistenceSession persistenceSession = ExpressiveObjectsContext.getPersistenceSession();
            ObjectAdapter service = persistenceSession.getService(loginService);
            ObjectAction loginAction = service.getSpecification().getObjectAction("login");
            if (loginAction == null) {
                throw new ApplicationException("No login method in service " + service.titleString());
            }
            ObjectAdapter[] parameters = new ObjectAdapter[2];
            AdapterManager adapterManager = persistenceSession.getAdapterManager();
            parameters[0] = adapterManager.adapterFor(request.getName());
            parameters[1] = adapterManager.adapterFor(((AuthenticationRequestPassword) request).getPassword());;
            ObjectAdapter result = loginAction.execute(service, parameters);
            
            ExpressiveObjectsContext.closeSession();
            return result != null;
    }

    @Override
    public boolean canAuthenticate(final Class<? extends AuthenticationRequest> authenticationRequestClass) {
        return AuthenticationRequestPassword.class.isAssignableFrom(authenticationRequestClass );
    }

}
