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

package uk.co.objectconnexions.expressiveobjects.progmodel.wrapper.metamodel.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.objectconnexions.expressiveobjects.applib.events.ActionArgumentEvent;
import uk.co.objectconnexions.expressiveobjects.applib.events.ActionInvocationEvent;
import uk.co.objectconnexions.expressiveobjects.applib.events.ActionUsabilityEvent;
import uk.co.objectconnexions.expressiveobjects.applib.events.ActionVisibilityEvent;
import uk.co.objectconnexions.expressiveobjects.applib.events.CollectionAccessEvent;
import uk.co.objectconnexions.expressiveobjects.applib.events.CollectionAddToEvent;
import uk.co.objectconnexions.expressiveobjects.applib.events.CollectionMethodEvent;
import uk.co.objectconnexions.expressiveobjects.applib.events.CollectionRemoveFromEvent;
import uk.co.objectconnexions.expressiveobjects.applib.events.CollectionUsabilityEvent;
import uk.co.objectconnexions.expressiveobjects.applib.events.CollectionVisibilityEvent;
import uk.co.objectconnexions.expressiveobjects.applib.events.InteractionEvent;
import uk.co.objectconnexions.expressiveobjects.applib.events.ObjectTitleEvent;
import uk.co.objectconnexions.expressiveobjects.applib.events.ObjectValidityEvent;
import uk.co.objectconnexions.expressiveobjects.applib.events.PropertyAccessEvent;
import uk.co.objectconnexions.expressiveobjects.applib.events.PropertyModifyEvent;
import uk.co.objectconnexions.expressiveobjects.applib.events.PropertyUsabilityEvent;
import uk.co.objectconnexions.expressiveobjects.applib.events.PropertyVisibilityEvent;
import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSessionProvider;
import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSessionProviderAware;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectPersistor;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectPersistorAware;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.mgr.AdapterManager;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.mgr.AdapterManagerAware;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.SpecificationLoader;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.SpecificationLoaderAware;
import uk.co.objectconnexions.expressiveobjects.progmodel.wrapper.applib.WrapperFactory;
import uk.co.objectconnexions.expressiveobjects.progmodel.wrapper.applib.WrapperObject;
import uk.co.objectconnexions.expressiveobjects.progmodel.wrapper.applib.listeners.InteractionListener;

public class WrapperFactoryDefault implements WrapperFactory, AuthenticationSessionProviderAware, SpecificationLoaderAware, AdapterManagerAware, ObjectPersistorAware {

    private final List<InteractionListener> listeners = new ArrayList<InteractionListener>();
    private final Map<Class<? extends InteractionEvent>, InteractionEventDispatcher> dispatchersByEventClass = new HashMap<Class<? extends InteractionEvent>, InteractionEventDispatcher>();

    private AuthenticationSessionProvider authenticationSessionProvider;
    private SpecificationLoader specificationLookup;
    private AdapterManager adapterManager;
    private ObjectPersistor objectPersistor;

    public WrapperFactoryDefault() {
        dispatchersByEventClass.put(ObjectTitleEvent.class, new InteractionEventDispatcherTypeSafe<ObjectTitleEvent>() {
            @Override
            public void dispatchTypeSafe(final ObjectTitleEvent interactionEvent) {
                for (final InteractionListener l : getListeners()) {
                    l.objectTitleRead(interactionEvent);
                }
            }
        });
        dispatchersByEventClass.put(PropertyVisibilityEvent.class, new InteractionEventDispatcherTypeSafe<PropertyVisibilityEvent>() {
            @Override
            public void dispatchTypeSafe(final PropertyVisibilityEvent interactionEvent) {
                for (final InteractionListener l : getListeners()) {
                    l.propertyVisible(interactionEvent);
                }
            }
        });
        dispatchersByEventClass.put(PropertyUsabilityEvent.class, new InteractionEventDispatcherTypeSafe<PropertyUsabilityEvent>() {
            @Override
            public void dispatchTypeSafe(final PropertyUsabilityEvent interactionEvent) {
                for (final InteractionListener l : getListeners()) {
                    l.propertyUsable(interactionEvent);
                }
            }
        });
        dispatchersByEventClass.put(PropertyAccessEvent.class, new InteractionEventDispatcherTypeSafe<PropertyAccessEvent>() {
            @Override
            public void dispatchTypeSafe(final PropertyAccessEvent interactionEvent) {
                for (final InteractionListener l : getListeners()) {
                    l.propertyAccessed(interactionEvent);
                }
            }
        });
        dispatchersByEventClass.put(PropertyModifyEvent.class, new InteractionEventDispatcherTypeSafe<PropertyModifyEvent>() {
            @Override
            public void dispatchTypeSafe(final PropertyModifyEvent interactionEvent) {
                for (final InteractionListener l : getListeners()) {
                    l.propertyModified(interactionEvent);
                }
            }
        });
        dispatchersByEventClass.put(CollectionVisibilityEvent.class, new InteractionEventDispatcherTypeSafe<CollectionVisibilityEvent>() {
            @Override
            public void dispatchTypeSafe(final CollectionVisibilityEvent interactionEvent) {
                for (final InteractionListener l : getListeners()) {
                    l.collectionVisible(interactionEvent);
                }
            }
        });
        dispatchersByEventClass.put(CollectionUsabilityEvent.class, new InteractionEventDispatcherTypeSafe<CollectionUsabilityEvent>() {
            @Override
            public void dispatchTypeSafe(final CollectionUsabilityEvent interactionEvent) {
                for (final InteractionListener l : getListeners()) {
                    l.collectionUsable(interactionEvent);
                }
            }
        });
        dispatchersByEventClass.put(CollectionAccessEvent.class, new InteractionEventDispatcherTypeSafe<CollectionAccessEvent>() {
            @Override
            public void dispatchTypeSafe(final CollectionAccessEvent interactionEvent) {
                for (final InteractionListener l : getListeners()) {
                    l.collectionAccessed(interactionEvent);
                }
            }
        });
        dispatchersByEventClass.put(CollectionAddToEvent.class, new InteractionEventDispatcherTypeSafe<CollectionAddToEvent>() {
            @Override
            public void dispatchTypeSafe(final CollectionAddToEvent interactionEvent) {
                for (final InteractionListener l : getListeners()) {
                    l.collectionAddedTo(interactionEvent);
                }
            }
        });
        dispatchersByEventClass.put(CollectionRemoveFromEvent.class, new InteractionEventDispatcherTypeSafe<CollectionRemoveFromEvent>() {
            @Override
            public void dispatchTypeSafe(final CollectionRemoveFromEvent interactionEvent) {
                for (final InteractionListener l : getListeners()) {
                    l.collectionRemovedFrom(interactionEvent);
                }
            }
        });
        dispatchersByEventClass.put(ActionVisibilityEvent.class, new InteractionEventDispatcherTypeSafe<ActionVisibilityEvent>() {
            @Override
            public void dispatchTypeSafe(final ActionVisibilityEvent interactionEvent) {
                for (final InteractionListener l : getListeners()) {
                    l.actionVisible(interactionEvent);
                }
            }
        });
        dispatchersByEventClass.put(ActionUsabilityEvent.class, new InteractionEventDispatcherTypeSafe<ActionUsabilityEvent>() {
            @Override
            public void dispatchTypeSafe(final ActionUsabilityEvent interactionEvent) {
                for (final InteractionListener l : getListeners()) {
                    l.actionUsable(interactionEvent);
                }
            }
        });
        dispatchersByEventClass.put(ActionArgumentEvent.class, new InteractionEventDispatcherTypeSafe<ActionArgumentEvent>() {
            @Override
            public void dispatchTypeSafe(final ActionArgumentEvent interactionEvent) {
                for (final InteractionListener l : getListeners()) {
                    l.actionArgument(interactionEvent);
                }
            }
        });
        dispatchersByEventClass.put(ActionInvocationEvent.class, new InteractionEventDispatcherTypeSafe<ActionInvocationEvent>() {
            @Override
            public void dispatchTypeSafe(final ActionInvocationEvent interactionEvent) {
                for (final InteractionListener l : getListeners()) {
                    l.actionInvoked(interactionEvent);
                }
            }
        });
        dispatchersByEventClass.put(ObjectValidityEvent.class, new InteractionEventDispatcherTypeSafe<ObjectValidityEvent>() {
            @Override
            public void dispatchTypeSafe(final ObjectValidityEvent interactionEvent) {
                for (final InteractionListener l : getListeners()) {
                    l.objectPersisted(interactionEvent);
                }
            }
        });
        dispatchersByEventClass.put(CollectionMethodEvent.class, new InteractionEventDispatcherTypeSafe<CollectionMethodEvent>() {
            @Override
            public void dispatchTypeSafe(final CollectionMethodEvent interactionEvent) {
                for (final InteractionListener l : getListeners()) {
                    l.collectionMethodInvoked(interactionEvent);
                }
            }
        });
    }

    // /////////////////////////////////////////////////////////////
    // Views
    // /////////////////////////////////////////////////////////////

    @Override
    public <T> T wrap(final T domainObject) {
        return wrap(domainObject, ExecutionMode.EXECUTE);
    }

    @Override
    public <T> T wrap(final T domainObject, final ExecutionMode mode) {
        if (isWrapper(domainObject)) {
            return domainObject;
        }
        return Proxy.proxy(domainObject, this, mode, authenticationSessionProvider, specificationLookup, adapterManager, objectPersistor);
    }

    @Override
    public boolean isWrapper(final Object possibleWrapper) {
        return possibleWrapper instanceof WrapperObject;
    }

    // /////////////////////////////////////////////////////////////
    // Listeners
    // /////////////////////////////////////////////////////////////

    @Override
    public List<InteractionListener> getListeners() {
        return listeners;
    }

    @Override
    public boolean addInteractionListener(final InteractionListener listener) {
        return listeners.add(listener);
    }

    @Override
    public boolean removeInteractionListener(final InteractionListener listener) {
        return listeners.remove(listener);
    }

    @Override
    public void notifyListeners(final InteractionEvent interactionEvent) {
        final InteractionEventDispatcher dispatcher = dispatchersByEventClass.get(interactionEvent.getClass());
        if (dispatcher == null) {
            throw new RuntimeException("Unknown InteractionEvent - register into dispatchers map");
        }
        dispatcher.dispatch(interactionEvent);
    }

    // /////////////////////////////////////////////////////////////
    // Listeners
    // /////////////////////////////////////////////////////////////

    @Override
    public void setAuthenticationSessionProvider(final AuthenticationSessionProvider authenticationSessionProvider) {
        this.authenticationSessionProvider = authenticationSessionProvider;
    }

    @Override
    public void setAdapterManager(final AdapterManager adapterManager) {
        this.adapterManager = adapterManager;
    }

    @Override
    public void setSpecificationLookup(final SpecificationLoader specificationLookup) {
        this.specificationLookup = specificationLookup;
    }

    @Override
    public void setObjectPersistor(final ObjectPersistor objectPersistor) {
        this.objectPersistor = objectPersistor;
    }

}
