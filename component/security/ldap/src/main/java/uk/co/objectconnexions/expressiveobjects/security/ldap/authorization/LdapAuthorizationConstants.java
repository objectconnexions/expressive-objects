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

package uk.co.objectconnexions.expressiveobjects.security.ldap.authorization;

import uk.co.objectconnexions.expressiveobjects.core.commons.config.ConfigurationConstants;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authorization.AuthorizationManagerInstaller;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authorization.standard.AuthorizationConstants;
import uk.co.objectconnexions.expressiveobjects.security.ldap.authentication.LdapAuthenticationConstants;

public final class LdapAuthorizationConstants {

    private static final String ROOT = ConfigurationConstants.ROOT + AuthorizationManagerInstaller.TYPE + "." + LdapAuthorizationManagerInstaller.NAME + ".";

    public static final String SERVER_KEY = ROOT + "server";
    public static final String SERVER_DEFAULT = LdapAuthenticationConstants.SERVER_DEFAULT;

    public static final String LDAPDN_KEY = ROOT + "dn";

    public static final String APP_DN_KEY = ROOT + "application.dn";

    public static final String LEARN_KEY = AuthorizationConstants.LEARN;
    public static final boolean LEARN_DEFAULT = AuthorizationConstants.LEARN_DEFAULT;

    private LdapAuthorizationConstants() {
    }

}
