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

package uk.co.objectconnexions.expressiveobjects.core.webserver;

import java.net.URI;
import java.util.Formatter;

import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfigurationBuilderDefault;
import uk.co.objectconnexions.expressiveobjects.core.commons.lang.ArrayUtils;
import uk.co.objectconnexions.expressiveobjects.core.commons.lang.StringUtils;
import uk.co.objectconnexions.expressiveobjects.core.runtime.runner.Constants;
import uk.co.objectconnexions.expressiveobjects.core.runtime.runner.ExpressiveObjectsRunner;
import uk.co.objectconnexions.expressiveobjects.core.webserver.internal.OptionHandlerAddress;
import uk.co.objectconnexions.expressiveobjects.core.webserver.internal.OptionHandlerDeploymentTypeWebServer;
import uk.co.objectconnexions.expressiveobjects.core.webserver.internal.OptionHandlerPort;
import uk.co.objectconnexions.expressiveobjects.core.webserver.internal.OptionHandlerResourceBase;
import uk.co.objectconnexions.expressiveobjects.core.webserver.internal.OptionHandlerStartupMode;

import org.eclipse.jetty.server.NetworkConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class WebServer {

    public static enum StartupMode {
        FOREGROUND, BACKGROUND;

        public static StartupMode lookup(final String value) {
            if (value == null) {
                return null;
            }
            try {
                return valueOf(value.toUpperCase());
            } catch (final Exception e) {
                return null;
            }
        }

        public boolean isForeground() {
            return this == FOREGROUND;
        }

        public boolean isBackground() {
            return this == BACKGROUND;
        }

    }

    private Server jettyServer;

    public static void main(final String[] args) {
        new WebServer().run(ArrayUtils.append(args, "--" + Constants.NO_SPLASH_LONG_OPT));
    }

    /**
     * Originally introduced to allow the WebServer to be used by tests.
     */
    public void run(final int port) {
        String[] args = new String[0];
        args = OptionHandlerStartupMode.appendArg(args, StartupMode.BACKGROUND);
        args = OptionHandlerPort.appendArg(args, port);
        run(args);
    }

    public void run(final String[] args) {
        final ExpressiveObjectsRunner runner = new ExpressiveObjectsRunner(args, new OptionHandlerDeploymentTypeWebServer());
        addOptionHandlersAndValidators(runner);
        if (!runner.parseAndValidate()) {
            return;
        }
        runner.setConfigurationBuilder(new ExpressiveObjectsConfigurationBuilderDefault());
        runner.primeConfigurationWithCommandLineOptions();
        runner.loadInitialProperties();
        
        final WebServerBootstrapper bootstrapper = new WebServerBootstrapper(runner);
        bootstrapper.bootstrap(null);
        jettyServer = bootstrapper.getJettyServer();
    }

    private void addOptionHandlersAndValidators(ExpressiveObjectsRunner runner) {
        // adjustments
        runner.addOptionHandler(new OptionHandlerPort());
        runner.addOptionHandler(new OptionHandlerAddress());
        runner.addOptionHandler(new OptionHandlerResourceBase());
        runner.addOptionHandler(new OptionHandlerStartupMode());
    }

    public void stop() {
        if (jettyServer == null) {
            return;
        }
        try {
            jettyServer.stop();
        } catch (final Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public URI getBase() {
        return URI.create(baseFor(jettyServer));
    }

    private String baseFor(final Server jettyServer) {
        final NetworkConnector connector = (NetworkConnector) jettyServer.getConnectors()[0];
        final String scheme = "http";
        final String host = StringUtils.coalesce(connector.getHost(), "localhost");
        final int port = connector.getPort();

        final WebAppContext handler = (WebAppContext) jettyServer.getHandler();
        final String contextPath = handler.getContextPath();

        final StringBuilder buf = new StringBuilder();
        final Formatter formatter = new Formatter(buf);
        formatter.format("%s://%s:%d/%s", scheme, host, port, contextPath);
        return appendSlashIfRequired(buf).toString();
    }

    private static StringBuilder appendSlashIfRequired(final StringBuilder buf) {
        if (buf.charAt(buf.length() - 1) != '/') {
            buf.append('/');
        }
        return buf;
    }
}
