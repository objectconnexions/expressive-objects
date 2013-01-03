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

package uk.co.objectconnexions.expressiveobjects.progmodel.wrapper.metamodel;

import java.util.List;

import uk.co.objectconnexions.expressiveobjects.applib.DomainObjectContainer;
import uk.co.objectconnexions.expressiveobjects.applib.events.InteractionEvent;
import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSessionProvider;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectPersistor;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.mgr.AdapterManager;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.services.container.DomainObjectContainerDefault;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.SpecificationLoader;
import uk.co.objectconnexions.expressiveobjects.progmodel.wrapper.applib.WrapperFactory;
import uk.co.objectconnexions.expressiveobjects.progmodel.wrapper.applib.listeners.InteractionListener;
import uk.co.objectconnexions.expressiveobjects.progmodel.wrapper.metamodel.internal.WrapperFactoryDefault;

/**
 * A combined {@link DomainObjectContainer} and {@link WrapperFactory}.
 */
public class DomainObjectContainerWrapperFactory extends DomainObjectContainerDefault implements WrapperFactory {

    private final WrapperFactoryDefault wrapperFactoryDelegate;

    public DomainObjectContainerWrapperFactory() {
        this.wrapperFactoryDelegate = new WrapperFactoryDefault();
    }

    // /////////////////////////////////////////////////////////////
    // Views
    // /////////////////////////////////////////////////////////////

    @Override
    public <T> T wrap(final T domainObject) {
        return wrapperFactoryDelegate.wrap(domainObject);
    }

    @Override
    public <T> T wrap(final T domainObject, final ExecutionMode mode) {
        return wrapperFactoryDelegate.wrap(domainObject, mode);
    }

    @Override
    public boolean isWrapper(final Object possibleView) {
        return wrapperFactoryDelegate.isWrapper(possibleView);
    }

    // /////////////////////////////////////////////////////////////
    // Listeners
    // /////////////////////////////////////////////////////////////

    @Override
    public List<InteractionListener> getListeners() {
        return wrapperFactoryDelegate.getListeners();
    }

    @Override
    public boolean addInteractionListener(final InteractionListener listener) {
        return wrapperFactoryDelegate.addInteractionListener(listener);
    }

    @Override
    public boolean removeInteractionListener(final InteractionListener listener) {
        return wrapperFactoryDelegate.removeInteractionListener(listener);
    }

    @Override
    public void notifyListeners(final InteractionEvent interactionEvent) {
        wrapperFactoryDelegate.notifyListeners(interactionEvent);
    }

    // /////////////////////////////////////////////////////////////
    // Dependencies
    // /////////////////////////////////////////////////////////////

    @Override
    public void setSpecificationLookup(final SpecificationLoader specificationLookup) {
        super.setSpecificationLookup(specificationLookup);
        wrapperFactoryDelegate.setSpecificationLookup(specificationLookup);
    }

    @Override
    public void setAuthenticationSessionProvider(final AuthenticationSessionProvider authenticationSessionProvider) {
        super.setAuthenticationSessionProvider(authenticationSessionProvider);
        wrapperFactoryDelegate.setAuthenticationSessionProvider(authenticationSessionProvider);
    }

    @Override
    public void setAdapterManager(final AdapterManager adapterManager) {
        super.setAdapterManager(adapterManager);
        wrapperFactoryDelegate.setAdapterManager(adapterManager);
    }

    @Override
    public void setObjectPersistor(final ObjectPersistor objectPersistor) {
        super.setObjectPersistor(objectPersistor);
        wrapperFactoryDelegate.setObjectPersistor(objectPersistor);
    }
}
