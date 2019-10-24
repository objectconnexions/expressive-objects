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

package uk.co.objectconnexions.expressiveobjects.objectstore.nosql.keys;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.Oid;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.OidMarshaller;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.RootOid;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.RootOidDefault;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.TypedOid;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecId;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.SpecificationLoaderSpi;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.objectstore.nosql.NoSqlStoreException;

public class KeyCreatorDefault {

    /**
     * returns {@link RootOid#getIdentifier()} (oid must be {@link RootOid}, and must be persistent). 
     */
    public String getIdentifierForPersistentRoot(final Oid oid) {
        if (!(oid instanceof RootOid)) {
            throw new NoSqlStoreException("Oid is not a RootOid: " + oid);
        } 
        RootOid rootOid = (RootOid) oid;
        if (rootOid.isTransient()) {
            throw new NoSqlStoreException("Oid is not for a persistent object: " + oid);
        }
        return rootOid.getIdentifier();
    }

    /**
     * Equivalent to the {@link Oid#enStringNoVersion(OidMarshaller)} for the adapter's Oid.
     */
    public String oidStrFor(final ObjectAdapter adapter) {
        if(adapter == null) {
            return null;
        }
        try {
            //return adapter.getSpecification().getFullIdentifier() + "@" + key(adapter.getOid());
            return adapter.getOid().enStringNoVersion(getOidMarshaller());
        } catch (final NoSqlStoreException e) {
            throw new NoSqlStoreException("Failed to create refence for " + adapter, e);
        }
    }

    public RootOid createRootOid(ObjectSpecification objectSpecification, final String identifier) {
        final ObjectSpecId objectSpecId = objectSpecification.getSpecId();
        return RootOidDefault.create(objectSpecId, identifier);
    }

    public RootOid unmarshal(final String oidStr) {
//        final ObjectSpecification objectSpecification = specificationFromReference(ref);
//        final String id = ref.split("@")[1];
//        return oid(objectSpecification, id);
        return getOidMarshaller().unmarshal(oidStr, RootOid.class);
    }

    public ObjectSpecification specificationFromOidStr(final String oidStr) {
//        final String name = ref.split("@")[0];
//        return getSpecificationLoader().loadSpecification(name);
        final TypedOid oid = getOidMarshaller().unmarshal(oidStr, TypedOid.class);
        return getSpecificationLoader().lookupBySpecId(oid.getObjectSpecId());
    }

    
    /////////////////////////////////////////////////
    // dependencies (from context)
    /////////////////////////////////////////////////
    
    
    protected SpecificationLoaderSpi getSpecificationLoader() {
        return ExpressiveObjectsContext.getSpecificationLoader();
    }

    protected OidMarshaller getOidMarshaller() {
        return ExpressiveObjectsContext.getOidMarshaller();
    }


}
