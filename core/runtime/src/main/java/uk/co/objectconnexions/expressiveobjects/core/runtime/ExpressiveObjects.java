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

package uk.co.objectconnexions.expressiveobjects.core.runtime;

import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfigurationBuilderDefault;
import uk.co.objectconnexions.expressiveobjects.core.runtime.runner.ExpressiveObjectsRunner;
import uk.co.objectconnexions.expressiveobjects.core.runtime.runner.opts.OptionHandlerDeploymentTypeExpressiveObjects;
import uk.co.objectconnexions.expressiveobjects.core.runtime.runner.opts.OptionHandlerPassword;
import uk.co.objectconnexions.expressiveobjects.core.runtime.runner.opts.OptionHandlerUser;
import uk.co.objectconnexions.expressiveobjects.core.runtime.runner.opts.OptionValidatorUserAndPasswordCombo;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.SystemConstants;

public class ExpressiveObjects {

    static final String DEFAULT_EMBEDDED_WEBSERVER = SystemConstants.WEBSERVER_DEFAULT;

    public static void main(final String[] args) {
        new ExpressiveObjects().run(args);
    }

    private void run(final String[] args) {
        final ExpressiveObjectsRunner runner = new ExpressiveObjectsRunner(args, new OptionHandlerDeploymentTypeExpressiveObjects());
        addOptionHandlersAndValidators(runner);
        if (!runner.parseAndValidate()) {
            return;
        }
        runner.setConfigurationBuilder(new ExpressiveObjectsConfigurationBuilderDefault());
        runner.primeConfigurationWithCommandLineOptions();
        runner.loadInitialProperties();
        runner.bootstrap(new RuntimeBootstrapper());
    }

    private void addOptionHandlersAndValidators(final ExpressiveObjectsRunner runner) {
        final OptionHandlerUser optionHandlerUser = new OptionHandlerUser();
        final OptionHandlerPassword optionHandlerPassword = new OptionHandlerPassword();

        runner.addOptionHandler(optionHandlerUser);
        runner.addOptionHandler(optionHandlerPassword);

        runner.addValidator(new OptionValidatorUserAndPasswordCombo(optionHandlerUser, optionHandlerPassword));
    }

}
