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

package uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence;

import uk.co.objectconnexions.expressiveobjects.applib.annotation.Aggregated;
import uk.co.objectconnexions.expressiveobjects.core.commons.debug.DebugBuilder;
import uk.co.objectconnexions.expressiveobjects.core.commons.debug.DebuggableWithTitle;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.AggregatedOid;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.Oid;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.RootOid;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.RootOidDefault;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.TypedOid;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecId;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.SpecificationLoader;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;

public class OidGenerator implements DebuggableWithTitle {

    private final IdentifierGenerator identifierGenerator;
    
    public OidGenerator() {
        this(new IdentifierGeneratorDefault());
    }
    
    public OidGenerator(final IdentifierGenerator identifierGenerator) {
        this.identifierGenerator = identifierGenerator;
    }

    
    public IdentifierGenerator getIdentifierGenerator() {
        return identifierGenerator;
    }


    // //////////////////////////////////////////////////////////////
    // API and mandatory hooks
    // //////////////////////////////////////////////////////////////
    
    /**
     * Create a new {@link Oid#isTransient() transient} {@link Oid} for the
     * supplied pojo, uniquely distinguishable from any other {@link Oid}.
     */
    public final RootOid createTransientOid(final Object pojo) {
        final ObjectSpecId objectSpecId = objectSpecIdFor(pojo);
        final String transientIdentifier = identifierGenerator.createTransientIdentifierFor(objectSpecId, pojo);
        return createTransient(objectSpecId, transientIdentifier);
    }

    /**
     * Creates a new id, locally unique within an aggregate root, for the specified 
     * object for use in an {@link AggregatedOid} (as returned by {@link AggregatedOid#getLocalId()}).
     * 
     * <p>
     * This is used by {@link Aggregated} references (either referenced by properties or by
     * collections).
     */
    public AggregatedOid createAggregateOid(final Object pojo, final ObjectAdapter parentAdapter) {
        final ObjectSpecId objectSpecId = objectSpecIdFor(pojo);
        final String aggregateLocalId = identifierGenerator.createAggregateLocalId(objectSpecId, pojo, parentAdapter);
        return new AggregatedOid(objectSpecId, (TypedOid) parentAdapter.getOid(), aggregateLocalId);
    }

    /**
     * Return an equivalent {@link RootOid}, but being persistent.
     * 
     * <p>
     * It is the responsibility of the implementation to determine the new unique identifier.
     * For example, the generator may simply assign a new value from a sequence, or a GUID;
     * or, the generator may use the oid to look up the object and inspect the object in order
     * to obtain an application-defined value.  
     * 
     * @param pojo - being persisted
     * @param transientRootOid - the oid for the pojo when transient.
     */
    public final RootOid createPersistent(Object pojo, RootOid transientRootOid) {

        final ObjectSpecId objectSpecId = objectSpecIdFor(pojo);
        final String persistentIdentifier = identifierGenerator.createPersistentIdentifierFor(objectSpecId, pojo, transientRootOid);
        
        return RootOidDefault.create(objectSpecId, persistentIdentifier);
    }


    // //////////////////////////////////////////////////////////////
    // Helpers
    // //////////////////////////////////////////////////////////////

    private RootOid createTransient(final ObjectSpecId objectSpecId, final String transientIdentifier) {
        return RootOidDefault.createTransient(objectSpecId, transientIdentifier);
    }

    private ObjectSpecId objectSpecIdFor(final Object pojo) {
        final Class<? extends Object> cls = pojo.getClass();
        final ObjectSpecification objectSpec = getSpecificationLookup().loadSpecification(cls);
        return objectSpec.getSpecId();
    }



    // //////////////////////////////////////////////////////////////
    // debug
    // //////////////////////////////////////////////////////////////


    @Override
    public void debugData(final DebugBuilder debug) {
        getIdentifierGenerator().debugData(debug);
    }


    @Override
    public String debugTitle() {
        return getIdentifierGenerator().debugTitle();
    }

    
    //////////////////////////////////////////////////////////////////
    // context
    //////////////////////////////////////////////////////////////////

    protected SpecificationLoader getSpecificationLookup() {
        return ExpressiveObjectsContext.getSpecificationLoader();
    }

}
