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

import static uk.co.objectconnexions.expressiveobjects.core.runtime.runner.Constants.DEBUG_OPT;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfigurationBuilder;
import uk.co.objectconnexions.expressiveobjects.core.runtime.optionhandler.BootPrinter;
import uk.co.objectconnexions.expressiveobjects.core.runtime.optionhandler.OptionHandlerAbstract;

public class OptionHandlerDebug extends OptionHandlerAbstract {

    public OptionHandlerDebug() {
        super();
    }

    @Override
    public void addOption(final Options options) {
        options.addOption(DEBUG_OPT, false, "print debugging messages");
    }

    @Override
    public boolean handle(final CommandLine commandLine, final BootPrinter bootPrinter, final Options options) {
        return true;
    }

    @Override
    public void primeConfigurationBuilder(final ExpressiveObjectsConfigurationBuilder expressiveObjectsConfigurationBuilder) {
        // TODO need to prime or otherwise set logging.
    }

}
