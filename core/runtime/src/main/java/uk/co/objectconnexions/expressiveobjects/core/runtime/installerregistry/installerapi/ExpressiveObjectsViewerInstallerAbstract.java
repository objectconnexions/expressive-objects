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

package uk.co.objectconnexions.expressiveobjects.core.runtime.installerregistry.installerapi;

import java.util.List;

import uk.co.objectconnexions.expressiveobjects.core.commons.config.InstallerAbstract;
import uk.co.objectconnexions.expressiveobjects.core.runtime.installerregistry.InstallerLookup;
import uk.co.objectconnexions.expressiveobjects.core.runtime.systemdependencyinjector.SystemDependencyInjector;
import uk.co.objectconnexions.expressiveobjects.core.runtime.viewer.ExpressiveObjectsViewer;

public abstract class ExpressiveObjectsViewerInstallerAbstract extends InstallerAbstract implements ExpressiveObjectsViewerInstaller {

    private SystemDependencyInjector installerLookup;

    public ExpressiveObjectsViewerInstallerAbstract(final String name) {
        super(ExpressiveObjectsViewerInstaller.TYPE, name);
    }

    @Override
    public ExpressiveObjectsViewer createViewer() {
        return injectDependenciesInto(doCreateViewer());
    }

    /**
     * Subclasses should override (or else override {@link #createViewer()} if
     * they need to do anything more elaborate.
     */
    protected ExpressiveObjectsViewer doCreateViewer() {
        return null;
    }

    protected <T> T injectDependenciesInto(final T candidate) {
        return installerLookup.injectDependenciesInto(candidate);
    }

    @Override
    public void setInstallerLookup(final InstallerLookup installerLookup) {
        this.installerLookup = installerLookup;
    }

    @Override
    public List<Class<?>> getTypes() {
        return listOf(ExpressiveObjectsViewer.class);
    }
}
