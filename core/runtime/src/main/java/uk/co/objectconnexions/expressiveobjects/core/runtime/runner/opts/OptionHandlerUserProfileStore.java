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

package uk.co.objectconnexions.expressiveobjects.core.runtime.runner.opts;

import static uk.co.objectconnexions.expressiveobjects.core.runtime.runner.Constants.USER_PROFILE_STORE_LONG_OPT;
import static uk.co.objectconnexions.expressiveobjects.core.runtime.runner.Constants.USER_PROFILE_STORE_OPT;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfigurationBuilder;
import uk.co.objectconnexions.expressiveobjects.core.runtime.installerregistry.InstallerRepository;
import uk.co.objectconnexions.expressiveobjects.core.runtime.optionhandler.BootPrinter;
import uk.co.objectconnexions.expressiveobjects.core.runtime.optionhandler.OptionHandlerAbstract;
import uk.co.objectconnexions.expressiveobjects.core.runtime.runner.Constants;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.SystemConstants;
import uk.co.objectconnexions.expressiveobjects.core.runtime.userprofile.UserProfileStoreInstaller;

public class OptionHandlerUserProfileStore extends OptionHandlerAbstract {

    private final InstallerRepository installerRepository;
    private String userProfileStoreName;

    public OptionHandlerUserProfileStore(final InstallerRepository installerRepository) {
        this.installerRepository = installerRepository;
    }

    @Override
    @SuppressWarnings("static-access")
    public void addOption(final Options options) {
        final Object[] persistenceMechanisms = installerRepository.getInstallers(UserProfileStoreInstaller.class);
        final Option option = OptionBuilder.withArgName("name|class name").hasArg().withLongOpt(USER_PROFILE_STORE_LONG_OPT).withDescription("user profile store to use: " + availableInstallers(persistenceMechanisms) + "; or class name").create(USER_PROFILE_STORE_OPT);
        options.addOption(option);
    }

    @Override
    public boolean handle(final CommandLine commandLine, final BootPrinter bootPrinter, final Options options) {
        userProfileStoreName = commandLine.getOptionValue(Constants.USER_PROFILE_STORE_OPT);
        return true;
    }

    @Override
    public void primeConfigurationBuilder(final ExpressiveObjectsConfigurationBuilder expressiveObjectsConfigurationBuilder) {
        expressiveObjectsConfigurationBuilder.add(SystemConstants.PROFILE_PERSISTOR_INSTALLER_KEY, userProfileStoreName);
    }

    public String getUserProfileStoreName() {
        return userProfileStoreName;
    }

}
