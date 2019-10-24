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

package uk.co.objectconnexions.expressiveobjects.security.file.authorization;

import uk.co.objectconnexions.expressiveobjects.core.commons.config.ConfigurationConstants;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authorization.AuthorizationManagerInstaller;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authorization.standard.AuthorizationConstants;

public final class FileAuthorizationConstants {

    private static final String ROOT = ConfigurationConstants.ROOT + AuthorizationManagerInstaller.TYPE + "."
        + FileAuthorizationManagerInstaller.NAME + ".";

    public static final String WHITELIST_RESOURCE_KEY = ROOT + "whitelist";
    public static final String WHITELIST_RESOURCE_DEFAULT = "authorization_file.allow";

    public static final String BLACKLIST_RESOURCE_KEY = ROOT + "blacklist";
    public static final String BLACKLIST_RESOURCE_DEFAULT = "";

    public static final String LEARN = AuthorizationConstants.LEARN;
    public static final boolean LEARN_DEFAULT = AuthorizationConstants.LEARN_DEFAULT;

    public static final String WHITELIST_EMPTY = ROOT + "whitelist.empty.isallowed";
    public static final boolean WHITELIST_EMPTY_DEFAULT = false;

    private FileAuthorizationConstants() {
    }

}
