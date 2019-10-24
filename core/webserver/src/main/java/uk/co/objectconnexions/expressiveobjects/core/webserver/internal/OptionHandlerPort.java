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

/**
 * 
 */
package uk.co.objectconnexions.expressiveobjects.core.webserver.internal;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfigurationBuilder;
import uk.co.objectconnexions.expressiveobjects.core.commons.lang.ArrayUtils;
import uk.co.objectconnexions.expressiveobjects.core.runtime.optionhandler.BootPrinter;
import uk.co.objectconnexions.expressiveobjects.core.runtime.optionhandler.OptionHandler;
import uk.co.objectconnexions.expressiveobjects.core.webserver.WebServerConstants;

public final class OptionHandlerPort implements OptionHandler {
    private Integer port;
    static final String PORT_LONG_OPT = "port";
    static final String PORT_OPT = "p";

    public static String[] appendArg(final String[] args, final int port) {
        return ArrayUtils.append(args, "--" + PORT_LONG_OPT, "" + port);
    }

    @Override
    @SuppressWarnings("static-access")
    public void addOption(final Options options) {
        OptionBuilder.withArgName("port");
        final Option option = OptionBuilder.hasArg().withLongOpt(OptionHandlerPort.PORT_LONG_OPT).withDescription("port to listen on").create(OptionHandlerPort.PORT_OPT);
        options.addOption(option);
    }

    @Override
    public boolean handle(final CommandLine commandLine, final BootPrinter bootPrinter, final Options options) {
        final String portStr = commandLine.getOptionValue(OptionHandlerPort.PORT_OPT);
        if (portStr != null) {
            port = Integer.parseInt(portStr);
        }
        return true;
    }

    @Override
    public void primeConfigurationBuilder(final ExpressiveObjectsConfigurationBuilder expressiveObjectsConfigurationBuilder) {
        if (port == null) {
            return;
        }
        expressiveObjectsConfigurationBuilder.add(WebServerConstants.EMBEDDED_WEB_SERVER_PORT_KEY, "" + port);
    }
}