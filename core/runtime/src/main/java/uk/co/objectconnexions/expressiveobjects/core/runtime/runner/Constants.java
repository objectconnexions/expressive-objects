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

package uk.co.objectconnexions.expressiveobjects.core.runtime.runner;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.ObjectReflectorInstaller;
import uk.co.objectconnexions.expressiveobjects.core.runtime.installerregistry.installerapi.ExpressiveObjectsViewerInstaller;
import uk.co.objectconnexions.expressiveobjects.core.runtime.installerregistry.installerapi.PersistenceMechanismInstaller;
import uk.co.objectconnexions.expressiveobjects.core.runtime.logging.LoggingConstants;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.DeploymentType;
import uk.co.objectconnexions.expressiveobjects.core.runtime.userprofile.UserProfileStoreInstaller;

public final class Constants {

    private Constants() {
    }

    public static final String TYPE_OPT = "t";
    public static final String TYPE_LONG_OPT = "type";
    public static final String TYPE_EXPLORATION = DeploymentType.EXPLORATION.friendlyName();
    public static final String TYPE_PROTOTYPE = DeploymentType.PROTOTYPE.friendlyName();
    public static final String TYPE_SINGLE_USER = DeploymentType.SINGLE_USER.friendlyName();
    public static final String TYPE_CLIENT = DeploymentType.CLIENT.friendlyName();
    public static final String TYPE_SERVER_EXPLORATION = DeploymentType.SERVER_EXPLORATION.friendlyName();
    public static final String TYPE_SERVER_PROTOTYPE = DeploymentType.SERVER_PROTOTYPE.friendlyName();
    public static final String TYPE_SERVER = DeploymentType.SERVER.friendlyName();

    public static final String REFLECTOR_OPT = "l";
    public static final String REFLECTOR_LONG_OPT = ObjectReflectorInstaller.TYPE;

    public static final String OBJECT_PERSISTENCE_OPT = "r";
    public static final String OBJECT_PERSISTENCE_LONG_OPT = PersistenceMechanismInstaller.TYPE;

    public static final String USER_PROFILE_STORE_OPT = "e";
    public static final String USER_PROFILE_STORE_LONG_OPT = UserProfileStoreInstaller.TYPE;

    public static final String VIEWER_OPT = "v";
    public static final String VIEWER_LONG_OPT = ExpressiveObjectsViewerInstaller.TYPE;

    public static final String CONFIGURATION_OPT = "c";
    public static final String CONFIGURATION_LONG_OPT = "config";

    public static final String FIXTURE_OPT = "f";
    public static final String FIXTURE_LONG_OPT = "fixture";

    public static final String HELP_OPT = "h";
    public static final String HELP_LONG_OPT = "help";

    public static final String NO_SPLASH_OPT = "s";
    public static final String NO_SPLASH_LONG_OPT = "nosplash";

    public static final String USER_OPT = "u";
    public static final String USER_LONG_OPT = "user";

    public static final String PASSWORD_OPT = "p";
    public static final String PASSWORD_LONG_OPT = "password";

    public static final String DIAGNOSTICS_OPT = "diagnostics";
    public static final String VERSION_OPT = "version";

    public static final String DEBUG_OPT = LoggingConstants.DEBUG_OPT;
    public static final String VERBOSE_OPT = LoggingConstants.VERBOSE_OPT;
    public static final String QUIET_OPT = LoggingConstants.QUIET_OPT;

    public static final String ADDITIONAL_PROPERTY = "D";

}
