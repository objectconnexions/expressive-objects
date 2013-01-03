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

package uk.co.objectconnexions.expressiveobjects.core.runtime.fixtures;

import java.util.List;

import uk.co.objectconnexions.expressiveobjects.applib.fixtures.LogonFixture;
import uk.co.objectconnexions.expressiveobjects.applib.fixtures.switchuser.SwitchUserService;
import uk.co.objectconnexions.expressiveobjects.applib.fixtures.switchuser.SwitchUserServiceAware;
import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSession;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.AuthenticationManager;
import uk.co.objectconnexions.expressiveobjects.core.runtime.fixtures.authentication.AuthenticationRequestLogonFixture;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.transaction.ExpressiveObjectsTransactionManager;

public class SwitchUserServiceImpl implements SwitchUserService {

    public SwitchUserServiceImpl() {
    }

    @Override
    public void switchUser(final String username, final List<String> roles) {
        switchUser(new LogonFixture(username, roles));
    }

    @Override
    public void switchUser(final String username, final String... roles) {
        switchUser(new LogonFixture(username, roles));
    }

    private void switchUser(final LogonFixture logonFixture) {
        getTransactionManager().endTransaction();
        ExpressiveObjectsContext.closeSession();
        final AuthenticationRequestLogonFixture authRequest = new AuthenticationRequestLogonFixture(logonFixture);
        final AuthenticationSession session = getAuthenticationManager().authenticate(authRequest);
        ExpressiveObjectsContext.openSession(session);
        getTransactionManager().startTransaction();
    }

    public void injectInto(final Object fixture) {
        if (fixture instanceof SwitchUserServiceAware) {
            final SwitchUserServiceAware serviceAware = (SwitchUserServiceAware) fixture;
            serviceAware.setService(this);
        }
    }

    protected AuthenticationManager getAuthenticationManager() {
        return ExpressiveObjectsContext.getAuthenticationManager();
    }

    protected ExpressiveObjectsTransactionManager getTransactionManager() {
        return ExpressiveObjectsContext.getTransactionManager();
    }

}
