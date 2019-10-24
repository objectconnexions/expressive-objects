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

package uk.co.objectconnexions.expressiveobjects.security.ldap.authentication;

import uk.co.objectconnexions.expressiveobjects.core.commons.config.ConfigurationConstants;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.AuthenticationManagerInstaller;

public final class LdapAuthenticationConstants {

    public static final String ROOT = ConfigurationConstants.ROOT + AuthenticationManagerInstaller.TYPE + "." + LdapAuthenticationManagerInstaller.NAME + ".";

    public static final String SERVER_KEY = ROOT + "server";
    public static final String SERVER_DEFAULT = "com.sun.jndi.ldap.LdapCtxFactory";

    public static final String LDAPDN_KEY = ROOT + "dn";

    public static String FILTER = "(objectclass=organizationalRole)";

    private LdapAuthenticationConstants() {
    }

}
