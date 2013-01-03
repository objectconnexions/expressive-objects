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


package uk.co.objectconnexions.expressiveobjects.extensions.htmlviewer.webapp;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import uk.co.objectconnexions.expressiveobjects.metamodel.authentication.AuthenticationSession;
import uk.co.objectconnexions.expressiveobjects.extensions.html.context.Context;
import uk.co.objectconnexions.expressiveobjects.extensions.html.servlet.internal.SessionAccess;


public class SessionList implements HttpSessionListener {
    private static final String NOF_SESSION_ATTRIBUTE = "nof-context";

    public void sessionCreated(final HttpSessionEvent event) {
        final HttpSession session = event.getSession();
        SessionAccess.addSession(session);
    }

    public void sessionDestroyed(final HttpSessionEvent event) {
        final HttpSession session = event.getSession();
        SessionAccess.removeSession(session);

        final Context context = (Context) session.getAttribute(NOF_SESSION_ATTRIBUTE);
        final AuthenticationSession nofSession = context.getSession();
        if (nofSession != null) {
            SessionAccess.logoffUser(nofSession);
        }

    }

}

