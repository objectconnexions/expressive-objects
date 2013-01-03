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

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Injector;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import uk.co.objectconnexions.expressiveobjects.core.commons.lang.Threads;
import uk.co.objectconnexions.expressiveobjects.core.runtime.installerregistry.InstallerLookup;
import uk.co.objectconnexions.expressiveobjects.core.runtime.installerregistry.installerapi.EmbeddedWebServerInstaller;
import uk.co.objectconnexions.expressiveobjects.core.runtime.runner.ExpressiveObjectsBootstrapper;
import uk.co.objectconnexions.expressiveobjects.core.runtime.runner.ExpressiveObjectsInjectModule.ViewerList;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.ExpressiveObjectsSystem;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.core.runtime.viewer.ExpressiveObjectsViewer;
import uk.co.objectconnexions.expressiveobjects.core.runtime.viewer.web.WebAppSpecification;
import uk.co.objectconnexions.expressiveobjects.core.runtime.web.EmbeddedWebServer;

final class RuntimeBootstrapper implements ExpressiveObjectsBootstrapper {

    @Override
    public void bootstrap(final Injector injector) {

        bootstrapSystem(injector);
        bootstrapViewers(injector);
    }

    private void bootstrapSystem(final Injector injector) {

        // sufficient just to look it up
        @SuppressWarnings("unused")
        final ExpressiveObjectsSystem system = injector.getInstance(ExpressiveObjectsSystem.class);
    }

    private void bootstrapViewers(final Injector injector) {
        final List<ExpressiveObjectsViewer> viewers = lookupViewers(injector);

        // split viewers into web viewers and non-web viewers
        final List<ExpressiveObjectsViewer> webViewers = findWebViewers(viewers);
        final List<ExpressiveObjectsViewer> nonWebViewers = findNonWebViewers(viewers, webViewers);

        startNonWebViewers(nonWebViewers);
        startWebViewers(injector, webViewers);
    }

    private List<ExpressiveObjectsViewer> lookupViewers(final Injector injector) {
        final List<ExpressiveObjectsViewer> viewers = injector.getInstance(ViewerList.class).getViewers();

        // looking up viewers may have merged in some further config files,
        // so update the NOContext global
        // REVIEW: would rather inject this
        final InstallerLookup installerLookup = injector.getInstance(InstallerLookup.class);
        ExpressiveObjectsContext.setConfiguration(installerLookup.getConfiguration());

        return viewers;
    }

    private List<ExpressiveObjectsViewer> findWebViewers(final List<ExpressiveObjectsViewer> viewers) {
        final List<ExpressiveObjectsViewer> webViewers = new ArrayList<ExpressiveObjectsViewer>(viewers);
        CollectionUtils.filter(webViewers, new Predicate() {
            @Override
            public boolean evaluate(final Object object) {
                final ExpressiveObjectsViewer viewer = (ExpressiveObjectsViewer) object;
                return viewer.getWebAppSpecification() != null;
            }
        });
        return webViewers;
    }

    private List<ExpressiveObjectsViewer> findNonWebViewers(final List<ExpressiveObjectsViewer> viewers, final List<ExpressiveObjectsViewer> webViewers) {
        final List<ExpressiveObjectsViewer> nonWebViewers = new ArrayList<ExpressiveObjectsViewer>(viewers);
        nonWebViewers.removeAll(webViewers);
        return nonWebViewers;
    }

    /**
     * Starts each (non web) {@link ExpressiveObjectsViewer viewer} in its own thread.
     */
    private void startNonWebViewers(final List<ExpressiveObjectsViewer> viewers) {
        for (final ExpressiveObjectsViewer viewer : viewers) {
            final Runnable target = new Runnable() {
                @Override
                public void run() {
                    viewer.init();
                }
            };
            Threads.startThread(target, "Viewer");
        }
    }

    /**
     * Starts all the web {@link ExpressiveObjectsViewer viewer}s in an instance of an
     * {@link EmbeddedWebServer}.
     */
    private void startWebViewers(final Injector injector, final List<ExpressiveObjectsViewer> webViewers) {
        if (webViewers.size() == 0) {
            return;
        }

        final InstallerLookup installerLookup = injector.getInstance(InstallerLookup.class);

        // TODO: we could potentially offer pluggability here
        final EmbeddedWebServerInstaller webServerInstaller = installerLookup.embeddedWebServerInstaller(ExpressiveObjects.DEFAULT_EMBEDDED_WEBSERVER);
        final EmbeddedWebServer embeddedWebServer = webServerInstaller.createEmbeddedWebServer();
        for (final ExpressiveObjectsViewer viewer : webViewers) {
            final WebAppSpecification webContainerRequirements = viewer.getWebAppSpecification();
            embeddedWebServer.addWebAppSpecification(webContainerRequirements);
        }
        embeddedWebServer.init();
    }
}