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

package uk.co.objectconnexions.expressiveobjects.core.objectstore.internal;

import java.util.Map;

import uk.co.objectconnexions.expressiveobjects.core.commons.exceptions.ExpressiveObjectsException;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.Oid;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecId;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.IdentifierGeneratorDefault;

import com.google.common.collect.Maps;

/**
 * Represents the persisted objects.
 * 
 * Attached and detached to each session.
 */
public class ObjectStorePersistedObjectsDefault implements ObjectStorePersistedObjects {

    private final Map<ObjectSpecification, ObjectStoreInstances> instancesBySpecMap = Maps.newHashMap();
    private final Map<ObjectSpecId, Oid> serviceOidByIdMap = Maps.newHashMap();

    private IdentifierGeneratorDefault.Memento oidGeneratorMemento;


    @Override
    public IdentifierGeneratorDefault.Memento getOidGeneratorMemento() {
        return oidGeneratorMemento;
    }

    @Override
    public void saveOidGeneratorMemento(final IdentifierGeneratorDefault.Memento memento) {
        this.oidGeneratorMemento = memento;
    }

    @Override
    public Oid getService(final ObjectSpecId objectSpecId) {
        return serviceOidByIdMap.get(objectSpecId);
    }

    @Override
    public void registerService(final ObjectSpecId objectSpecId, final Oid oid) {
        final Oid oidLookedUpByName = serviceOidByIdMap.get(objectSpecId);
        if (oidLookedUpByName != null) {
            if (oidLookedUpByName.equals(oid)) {
                throw new ExpressiveObjectsException("Already another service registered as name: " + objectSpecId + " (existing Oid: " + oidLookedUpByName + ", " + "intended: " + oid + ")");
            }
        } else {
            serviceOidByIdMap.put(objectSpecId, oid);
        }
    }

    // TODO: this is where the clever logic needs to go to determine how to save
    // into our custom Map.
    // also think we shouldn't surface the entire Map, just the API we require
    // (keySet, values etc).
    @Override
    public ObjectStoreInstances instancesFor(final ObjectSpecification spec) {
        ObjectStoreInstances ins = instancesBySpecMap.get(spec);
        if (ins == null) {
            ins = new ObjectStoreInstances(spec);
            instancesBySpecMap.put(spec, ins);
        }
        return ins;
    }

    @Override
    public Iterable<ObjectSpecification> specifications() {
        return instancesBySpecMap.keySet();
    }

    @Override
    public void clear() {
        instancesBySpecMap.clear();
    }

    @Override
    public Iterable<ObjectStoreInstances> instances() {
        return instancesBySpecMap.values();
    }

}
