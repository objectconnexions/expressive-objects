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

package uk.co.objectconnexions.expressiveobjects.core.metamodel.progmodel;

import java.util.Collections;
import java.util.List;

import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.commons.factory.InstanceUtil;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.MetaModelRefiner;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.MetaModelValidatorRefiner;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.validator.MetaModelValidatorComposite;

import com.google.common.collect.Lists;

public abstract class ProgrammingModelAbstract implements ProgrammingModel {

    private final List<FacetFactory> facetFactories = Lists.newArrayList();
    private final List<Object> facetFactoryInstancesOrClasses = Lists.newArrayList();

    @Override
    public void init() {
        initializeIfRequired();
    }

    private void initializeIfRequired() {
        if(!facetFactories.isEmpty()) {
            return;
        }
        initialize();
    }

    private void initialize() {
        for (final Object factoryInstanceOrClass : facetFactoryInstancesOrClasses) {
            final FacetFactory facetFactory = asFacetFactory(factoryInstanceOrClass);
            facetFactories.add(facetFactory);
        }
    }

    private static FacetFactory asFacetFactory(final Object factoryInstanceOrClass) {
        if(factoryInstanceOrClass instanceof FacetFactory) {
            return (FacetFactory) factoryInstanceOrClass;
        } else {
            @SuppressWarnings("unchecked")
            Class<? extends FacetFactory> factoryClass = (Class<? extends FacetFactory>) factoryInstanceOrClass;
            return (FacetFactory) InstanceUtil.createInstance(factoryClass);
        }
    }

    private void assertNotInitialized() {
        if(!facetFactories.isEmpty()) {
            throw new IllegalStateException("Programming model already initialized");
        }
    }


    @Override
    public final List<FacetFactory> getList() {
        initializeIfRequired();
        return Collections.unmodifiableList(facetFactories);
    }

    @Override
    public final void addFactory(final Class<? extends FacetFactory> factoryClass) {
        assertNotInitialized();
        facetFactoryInstancesOrClasses.add(factoryClass);
    }

    @Override
    public final void removeFactory(final Class<? extends FacetFactory> factoryClass) {
        assertNotInitialized();
        facetFactoryInstancesOrClasses.remove(factoryClass);
    }

    @Override
    public void addFactory(FacetFactory facetFactory) {
        assertNotInitialized();
        facetFactoryInstancesOrClasses.add(facetFactory);
    }

    @Override
    public void refineMetaModelValidator(MetaModelValidatorComposite metaModelValidator, ExpressiveObjectsConfiguration configuration) {
        for (FacetFactory facetFactory : getList()) {
            if(facetFactory instanceof MetaModelValidatorRefiner) {
                MetaModelValidatorRefiner metaModelValidatorRefiner = (MetaModelValidatorRefiner) facetFactory;
                metaModelValidatorRefiner.refineMetaModelValidator(metaModelValidator, configuration);
            }
        }
    }
}
