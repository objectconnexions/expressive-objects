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

import static uk.co.objectconnexions.expressiveobjects.core.webserver.WebServerConstants.EMBEDDED_WEB_SERVER_PORT_DEFAULT;
import static uk.co.objectconnexions.expressiveobjects.core.webserver.WebServerConstants.EMBEDDED_WEB_SERVER_PORT_KEY;
import static uk.co.objectconnexions.expressiveobjects.core.webserver.WebServerConstants.EMBEDDED_WEB_SERVER_RESOURCE_BASE_DEFAULT;
import static uk.co.objectconnexions.expressiveobjects.core.webserver.WebServerConstants.EMBEDDED_WEB_SERVER_RESOURCE_BASE_KEY;
import static uk.co.objectconnexions.expressiveobjects.core.webserver.WebServerConstants.EMBEDDED_WEB_SERVER_STARTUP_MODE_DEFAULT;
import static uk.co.objectconnexions.expressiveobjects.core.webserver.WebServerConstants.EMBEDDED_WEB_SERVER_STARTUP_MODE_KEY;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Injector;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfigurationBuilder;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfigurationBuilderPrimer;
import uk.co.objectconnexions.expressiveobjects.core.commons.exceptions.ExpressiveObjectsException;
import uk.co.objectconnexions.expressiveobjects.core.commons.lang.CastUtils;
import uk.co.objectconnexions.expressiveobjects.core.runtime.runner.ExpressiveObjectsBootstrapper;
import uk.co.objectconnexions.expressiveobjects.core.runtime.runner.ExpressiveObjectsRunner;
import uk.co.objectconnexions.expressiveobjects.core.webapp.WebAppConstants;
import uk.co.objectconnexions.expressiveobjects.core.webserver.WebServer.StartupMode;
import org.apache.log4j.Logger;

final class WebServerBootstrapper implements ExpressiveObjectsBootstrapper {

    private static final Logger LOG = Logger.getLogger(WebServerBootstrapper.class);
    private static final String SRC_MAIN_WEBAPP = "src/main/webapp";

    private final ExpressiveObjectsRunner runner;

    private Server jettyServer;

    WebServerBootstrapper(final ExpressiveObjectsRunner runner) {
        this.runner = runner;
    }

    /**
     * ignores the arguments and just bootstraps JettyViewer, come what may.
     */
    @Override
    public void bootstrap(final Injector injector) {

        final ExpressiveObjectsConfigurationBuilder expressiveObjectsConfigurationBuilder = runner.getStartupConfiguration();

        // we don't actually bootstrap the system here; instead we expect it to
        // be bootstrapped
        // from the ServletContextInitializer in the web.xml
        final ExpressiveObjectsConfiguration configuration = expressiveObjectsConfigurationBuilder.getConfiguration();
        final int port = configuration.getInteger(EMBEDDED_WEB_SERVER_PORT_KEY, EMBEDDED_WEB_SERVER_PORT_DEFAULT);
        final String webappContextPath = configuration.getString(EMBEDDED_WEB_SERVER_RESOURCE_BASE_KEY, EMBEDDED_WEB_SERVER_RESOURCE_BASE_DEFAULT);
        final StartupMode startupMode = StartupMode.lookup(configuration.getString(EMBEDDED_WEB_SERVER_STARTUP_MODE_KEY, EMBEDDED_WEB_SERVER_STARTUP_MODE_DEFAULT));
        // TODO get and use the address

        jettyServer = new Server(port);
        final WebAppContext context = new WebAppContext(SRC_MAIN_WEBAPP, webappContextPath);

        copyConfigurationPrimersIntoServletContext(context);

        jettyServer.setHandler(context);

        LOG.info("starting Jetty on port " + port + " to serve webapp");
        try {
            jettyServer.start();
            if (startupMode.isForeground()) {
                jettyServer.join();
            }
        } catch (final Exception ex) {
             throw new ExpressiveObjectsException("Unable to start Jetty server", ex);
        }
    }

    public Server getJettyServer() {
        return jettyServer;
    }

    /**
     * Bound to the {@link WebAppContext} so that they can be used when
     * bootstrapping.
     *
     * @param context
     */
    @SuppressWarnings("unchecked")
    private void copyConfigurationPrimersIntoServletContext(final WebAppContext context) {
        final List<ExpressiveObjectsConfigurationBuilderPrimer> expressiveObjectsConfigurationBuilderPrimers = (List<ExpressiveObjectsConfigurationBuilderPrimer>) (List<?>) runner.getOptionHandlers();
        context.setAttribute(WebAppConstants.CONFIGURATION_PRIMERS_KEY, expressiveObjectsConfigurationBuilderPrimers);
    }

    @SuppressWarnings("unused")
    private void copyDeploymentTypeIntoInitParams(final WebAppContext context) {
        Map<String, String> initParams = CastUtils.cast(context.getInitParams());
        initParams = new HashMap<String, String>(initParams);
        context.setInitParams(initParams);
    }
}