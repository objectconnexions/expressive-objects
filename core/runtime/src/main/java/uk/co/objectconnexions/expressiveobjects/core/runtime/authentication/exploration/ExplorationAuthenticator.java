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

package uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.exploration;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSession;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.AuthenticationRequest;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.AuthenticatorAbstractForDfltRuntime;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.standard.SimpleSession;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.DeploymentType;

/**
 * Creates a session suitable for {@link DeploymentType#EXPLORATION exploration}
 * mode.
 * 
 * <p>
 * If the {@link ExpressiveObjectsConfiguration} contains the key
 * {@value ExplorationAuthenticatorConstants#USERS} then returns a
 * {@link MultiUserExplorationSession} which encapsulates the details of several
 * users (and their roles). Viewers that are aware of this capability can offer
 * the convenient ability to switch between these users. For viewers that are
 * not aware, the {@link MultiUserExplorationSession} appears as a regular
 * {@link SimpleSession session}, with the Id of the first user listed.
 * 
 * <p>
 * The format of the {@value ExplorationAuthenticatorConstants#USERS} key should
 * be:
 * 
 * <pre>
 * &amp;lt:userName&gt; [:&lt;role&gt;[|&lt;role&gt;]...], &lt;userName&gt;...
 * </pre>
 */
public class ExplorationAuthenticator extends AuthenticatorAbstractForDfltRuntime {

    private final Set<SimpleSession> registeredSessions = new LinkedHashSet<SimpleSession>();;
    private final String users;

    // //////////////////////////////////////////////////////////////////
    // Constructor
    // //////////////////////////////////////////////////////////////////

    public ExplorationAuthenticator(final ExpressiveObjectsConfiguration configuration) {
        super(configuration);
        users = getConfiguration().getString(ExplorationAuthenticatorConstants.USERS);
        if (users != null) {
            registeredSessions.addAll(parseUsers(users));
        }
    }

    private List<SimpleSession> parseUsers(final String users) {
        final List<SimpleSession> registeredUsers = new ArrayList<SimpleSession>();

        final StringTokenizer st = new StringTokenizer(users, ",");
        while (st.hasMoreTokens()) {
            final String token = st.nextToken();
            final int end = token.indexOf(':');
            final List<String> roles = new ArrayList<String>();
            final String userName;
            if (end == -1) {
                userName = token.trim();
            } else {
                userName = token.substring(0, end).trim();
                final String roleList = token.substring(end + 1);
                final StringTokenizer st2 = new StringTokenizer(roleList, "|");
                while (st2.hasMoreTokens()) {
                    final String role = st2.nextToken().trim();
                    roles.add(role);
                }
            }
            registeredUsers.add(createSimpleSession(userName, roles));
        }
        return registeredUsers;
    }

    private SimpleSession createSimpleSession(final String userName, final List<String> roles) {
        return new SimpleSession(userName, roles.toArray(new String[roles.size()]));
    }

    // //////////////////////////////////////////////////////////////////
    // API
    // //////////////////////////////////////////////////////////////////

    /**
     * Can authenticate if a {@link AuthenticationRequestExploration}.
     */
    @Override
    public final boolean canAuthenticate(final Class<? extends AuthenticationRequest> authenticationRequestClass) {
        return AuthenticationRequestExploration.class.isAssignableFrom(authenticationRequestClass);
    }

    /**
     * Valid providing running in {@link DeploymentType#isExploring() 
     * exploration} mode.
     */
    @Override
    public final boolean isValid(final AuthenticationRequest request) {
        return getDeploymentType().isExploring();
    }

    @Override
    public AuthenticationSession authenticate(final AuthenticationRequest request, final String code) {
        final AuthenticationRequestExploration authenticationRequestExploration = (AuthenticationRequestExploration) request;
        if (!authenticationRequestExploration.isDefaultUser()) {
            registeredSessions.add(createSimpleSession(authenticationRequestExploration.getName(), authenticationRequestExploration.getRoles()));
        }
        if (registeredSessions.size() > 1) {
            return new MultiUserExplorationSession(registeredSessions, code);
        } else if (registeredSessions.size() == 1) {
            return registeredSessions.iterator().next();
        } else {
            return new ExplorationSession(code);
        }
    }

}
