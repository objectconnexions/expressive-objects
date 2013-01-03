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

package uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore;

import java.util.List;

import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfigurationException;
import uk.co.objectconnexions.expressiveobjects.core.commons.debug.DebugBuilder;
import uk.co.objectconnexions.expressiveobjects.core.commons.factory.InstanceCreationException;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.OidMarshaller;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.RootOid;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.TypedOid;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociation;
import uk.co.objectconnexions.expressiveobjects.core.runtime.logging.Logger;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.ObjectNotFoundException;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.ObjectPersistenceException;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.UnsupportedFindException;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.transaction.CreateObjectCommand;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.transaction.DestroyObjectCommand;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.transaction.PersistenceCommand;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.transaction.SaveObjectCommand;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.PersistenceQuery;

public class ExpressiveObjectsObjectStoreLogger extends Logger implements ObjectStoreSpi {
    private final ObjectStoreSpi underlying;

    public ExpressiveObjectsObjectStoreLogger(final ObjectStoreSpi decorated, final String level) {
        super(level);
        this.underlying = decorated;
    }

    public ExpressiveObjectsObjectStoreLogger(final ObjectStoreSpi decorated) {
        this.underlying = decorated;
    }

    @Override
    public CreateObjectCommand createCreateObjectCommand(final ObjectAdapter object) {
        log("create object " + object);
        return underlying.createCreateObjectCommand(object);
    }

    @Override
    public void registerService(final RootOid rootOid) {
        log("registering service: " + rootOid.enString(getOidMarshaller()));
        underlying.registerService(rootOid);
    }

    @Override
    public DestroyObjectCommand createDestroyObjectCommand(final ObjectAdapter object) {
        log("destroy object " + object);
        return underlying.createDestroyObjectCommand(object);
    }

    @Override
    public SaveObjectCommand createSaveObjectCommand(final ObjectAdapter object) {
        log("save object " + object);
        return underlying.createSaveObjectCommand(object);
    }

    @Override
    public void debugData(final DebugBuilder debug) {
        underlying.debugData(debug);
    }

    @Override
    public String debugTitle() {
        return underlying.debugTitle();
    }

    @Override
    protected Class<?> getDecoratedClass() {
        return underlying.getClass();
    }

    @Override
    public List<ObjectAdapter> loadInstancesAndAdapt(final PersistenceQuery criteria) throws ObjectPersistenceException, UnsupportedFindException {
        log("get instances matching " + criteria);
        return underlying.loadInstancesAndAdapt(criteria);
    }


    @Override
    public ObjectAdapter loadInstanceAndAdapt(final TypedOid oid) throws ObjectNotFoundException, ObjectPersistenceException {
        final ObjectAdapter adapter = underlying.loadInstanceAndAdapt(oid);
        log("get object for " + oid + " (of type '" + oid.getObjectSpecId() + "')", adapter.getObject());
        return adapter;
    }

    @Override
    public RootOid getOidForService(ObjectSpecification serviceSpec) {
        final RootOid serviceOid = underlying.getOidForService(serviceSpec);
        if(serviceOid != null) {
            log("get OID for service: " + serviceOid.enString(getOidMarshaller()));
        } else {
            log("get OID for service: null (presumably in the process of being registered for '" + serviceSpec.getSpecId() + "')");
        }
        return serviceOid;
    }

    @Override
    public boolean hasInstances(final ObjectSpecification specification) throws ObjectPersistenceException {
        final boolean hasInstances = underlying.hasInstances(specification);
        log("has instances of " + specification.getShortIdentifier(), "" + hasInstances);
        return hasInstances;
    }

    @Override
    public boolean isFixturesInstalled() {
        final boolean isInitialized = underlying.isFixturesInstalled();
        log("is initialized: " + isInitialized);
        return isInitialized;
    }

    @Override
    public void open() throws ExpressiveObjectsConfigurationException, InstanceCreationException, ObjectPersistenceException {
        log("opening " + name());
        underlying.open();
    }

    @Override
    public String name() {
        return underlying.name();
    }

    @Override
    public void reset() {
        log("reset");
        underlying.reset();
    }

    @Override
    public void resolveField(final ObjectAdapter object, final ObjectAssociation field) throws ObjectPersistenceException {
        log("resolve eagerly object in field " + field + " of " + object);
        underlying.resolveField(object, field);
    }

    @Override
    public void resolveImmediately(final ObjectAdapter object) throws ObjectPersistenceException {
        log("resolve immediately: " + object);
        underlying.resolveImmediately(object);
    }

    @Override
    public void execute(final List<PersistenceCommand> commands) throws ObjectPersistenceException {
        log("execute commands");
        int i = 0;
        for (final PersistenceCommand command : commands) {
            log("  " + (i++) + " " + command);
        }
        underlying.execute(commands);
    }

    @Override
    public void close() throws ObjectPersistenceException {
        log("closing " + underlying);
        underlying.close();
    }

    @Override
    public void startTransaction() {
        underlying.startTransaction();
    }

    @Override
    public void endTransaction() {
        underlying.endTransaction();
    }

    @Override
    public void abortTransaction() {
        underlying.abortTransaction();
    }
    
    
    /////////////////////////////////////////////
    // Dependencies (from context)
    /////////////////////////////////////////////
    
    protected OidMarshaller getOidMarshaller() {
        return ExpressiveObjectsContext.getOidMarshaller();
    }


}
