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

package uk.co.objectconnexions.expressiveobjects.core.runtime.progmodels;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetdecorator.FacetDecorator;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.FacetDecoratorInstaller;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.ReflectorConstants;
import uk.co.objectconnexions.expressiveobjects.core.runtime.installerregistry.InstallerLookup;
import uk.co.objectconnexions.expressiveobjects.core.runtime.installerregistry.InstallerLookupAware;
import uk.co.objectconnexions.expressiveobjects.progmodels.dflt.JavaReflectorInstallerNoDecorators;

public class JavaReflectorInstaller extends JavaReflectorInstallerNoDecorators implements InstallerLookupAware {

    private static final Logger LOG = Logger.getLogger(JavaReflectorInstaller.class);

    private InstallerLookup installerLookup;

    /**
     * Hook method to allow subclasses to specify a different sets of
     * {@link FacetDecorator}s.
     * 
     * <p>
     * By default, returns the {@link FacetDecorator}s that are specified in the
     * {@link ExpressiveObjectsConfiguration} (using
     * {@link ReflectorConstants#FACET_DECORATOR_CLASS_NAMES}) along with any
     * {@link FacetDecorator}s explicitly registered using
     * {@link #addFacetDecoratorInstaller(FacetDecoratorInstaller)}. created
     * using the {@link FacetDecoratorInstaller}s.
     */
    protected Set<FacetDecorator> createFacetDecorators(final ExpressiveObjectsConfiguration configuration) {
        addFacetDecoratorInstallers(configuration);
        return createFacetDecorators(decoratorInstallers);
    }

    private void addFacetDecoratorInstallers(final ExpressiveObjectsConfiguration configuration) {
        final String[] decoratorNames = configuration.getList(ReflectorConstants.FACET_DECORATOR_CLASS_NAMES);
        for (final String decoratorName : decoratorNames) {
            if (LOG.isInfoEnabled()) {
                LOG.info("adding reflector facet decorator from configuration " + decoratorName);
            }
            addFacetDecoratorInstaller(lookupFacetDecorator(decoratorName));
        }
    }

    private FacetDecoratorInstaller lookupFacetDecorator(final String decoratorClassName) {
        return installerLookup.getInstaller(FacetDecoratorInstaller.class, decoratorClassName);
    }

    private Set<FacetDecorator> createFacetDecorators(final Set<FacetDecoratorInstaller> decoratorInstallers) {
        final LinkedHashSet<FacetDecorator> decorators = new LinkedHashSet<FacetDecorator>();
        if (decoratorInstallers.size() == 0) {
            if (LOG.isInfoEnabled()) {
                LOG.info("No facet decorators installers added");
            }
        }
        for (final FacetDecoratorInstaller installer : decoratorInstallers) {
            decorators.addAll(installer.createDecorators());
        }
        return Collections.unmodifiableSet(decorators);
    }

    // /////////////////////////////////////////////////////
    // Optionally Injected: InstallerLookup
    // /////////////////////////////////////////////////////

    /**
     * Injected by virtue of being {@link InstallerLookupAware}.
     */
    @Override
    public void setInstallerLookup(final InstallerLookup installerLookup) {
        this.installerLookup = installerLookup;
    }

}
