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

package uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher;

import org.apache.log4j.Logger;

import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AnonymousSession;
import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSession;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.AuthenticationManager;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.AuthenticationRequestPassword;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.context.RequestContext;

public class UserManager {

    private static final Logger LOG = Logger.getLogger(UserManager.class);
    private static UserManager instance;

    private static AuthenticationManager getAuthenticationManager() {
        if (instance == null) {
            throw new IllegalStateException("Server initialisation failed, or not defined as a context listener");
        }
        return instance.authenticationManager;
    }

    public static AuthenticationSession startRequest(final RequestContext context) {
        AuthenticationSession session = context.getSession();
        if (session == null) {
            session = new AnonymousSession();
            LOG.debug("start anonymous request: " + session);
        } else {
            LOG.debug("start request for: " + session.getUserName());
        }
        ExpressiveObjectsContext.closeSession();
        ExpressiveObjectsContext.openSession(session);
        return session;
    }

    public static AuthenticationSession authenticate(final AuthenticationRequestPassword passwordAuthenticationRequest) {
        final AuthenticationSession session = getAuthenticationManager().authenticate(passwordAuthenticationRequest);
        if (session != null) {
            LOG.info("log on user " + session.getUserName());
            ExpressiveObjectsContext.closeSession();
            ExpressiveObjectsContext.openSession(session);
        }
        return session;
    }

    public static void endRequest(final AuthenticationSession session) {
        if (session == null) {
            LOG.debug("end anonymous request");
        } else {
            LOG.debug("end request for: " + session.getUserName());
        }
        ExpressiveObjectsContext.closeSession();
    }

    public static void logoffUser(final AuthenticationSession session) {
        LOG.info("log off user " + session.getUserName());
        ExpressiveObjectsContext.closeSession();
        getAuthenticationManager().closeSession(session);

        final AnonymousSession replacementSession = new AnonymousSession();
        ExpressiveObjectsContext.openSession(replacementSession);
    }

    private final AuthenticationManager authenticationManager;

    public UserManager(final AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        UserManager.instance = this;
    }
}
