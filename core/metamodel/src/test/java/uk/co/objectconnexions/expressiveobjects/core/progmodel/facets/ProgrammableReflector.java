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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets;

import java.util.Collection;
import java.util.List;

import uk.co.objectconnexions.expressiveobjects.core.commons.components.ApplicationScopedComponent;
import uk.co.objectconnexions.expressiveobjects.core.commons.debug.DebugBuilder;
import uk.co.objectconnexions.expressiveobjects.core.commons.exceptions.NotYetImplementedException;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.runtimecontext.RuntimeContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.runtimecontext.RuntimeContextAware;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecId;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.SpecificationLoaderSpi;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.collectiontyperegistry.CollectionTypeRegistry;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.validator.ValidationFailures;

public class ProgrammableReflector implements SpecificationLoaderSpi, ApplicationScopedComponent, RuntimeContextAware {

    @Override
    public void init() {
    }

    public void installServiceSpecification(final Class<?> cls) {
    }

    private Collection<ObjectSpecification> allSpecificationsReturn;

    public void setAllSpecificationsReturn(final Collection<ObjectSpecification> allSpecificationsReturn) {
        this.allSpecificationsReturn = allSpecificationsReturn;
    }

    @Override
    public Collection<ObjectSpecification> allSpecifications() {
        return allSpecificationsReturn;
    }

    private CollectionTypeRegistry getCollectionTypeRegistryReturn;

    public void setGetCollectionTypeRegistryReturn(final CollectionTypeRegistry getCollectionTypeRegistryReturn) {
        this.getCollectionTypeRegistryReturn = getCollectionTypeRegistryReturn;
    }

    public CollectionTypeRegistry getCollectionTypeRegistry() {
        return getCollectionTypeRegistryReturn;
    }

    @Override
    public ObjectSpecification loadSpecification(final Class<?> type) {
        return loadSpecification(type.getName());
    }

    private ObjectSpecification loadSpecificationStringReturn;

    public void setLoadSpecificationStringReturn(final ObjectSpecification loadSpecificationStringReturn) {
        this.loadSpecificationStringReturn = loadSpecificationStringReturn;
    }

    @Override
    public ObjectSpecification loadSpecification(final String name) {
        return loadSpecificationStringReturn;
    }

    @Override
    public void shutdown() {
    }

    @Override
    public boolean loaded(final Class<?> cls) {
        throw new NotYetImplementedException();
    }

    @Override
    public boolean loaded(final String fullyQualifiedClassName) {
        throw new NotYetImplementedException();
    }

    @Override
    public void injectInto(final Object candidate) {
    }

    @Override
    public void setRuntimeContext(final RuntimeContext runtimeContext) {
        // ignored
    }

    @Override
    public void debugData(final DebugBuilder debug) {
    }

    @Override
    public String debugTitle() {
        return null;
    }

    @Override
    public boolean loadSpecifications(final List<Class<?>> typesToLoad, final Class<?> typeToIgnore) {
        return false;
    }

    @Override
    public boolean loadSpecifications(final List<Class<?>> typesToLoad) {
        return false;
    }

    @Override
    public ObjectSpecification lookupBySpecId(ObjectSpecId objectSpecId) {
        return null;
    }

    @Override
    public void setServiceClasses(final List<Class<?>> serviceClasses) {
        throw new NotYetImplementedException();
    }

    @Override
    public void validateSpecifications(ValidationFailures validationFailures) {
        throw new NotYetImplementedException();
    }

    @Override
    public ObjectSpecification introspectIfRequired(ObjectSpecification spec) {
        throw new NotYetImplementedException();
    }

}
