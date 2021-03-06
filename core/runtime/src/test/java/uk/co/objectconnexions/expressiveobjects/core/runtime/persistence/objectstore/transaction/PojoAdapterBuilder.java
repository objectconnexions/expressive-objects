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

package uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.transaction;

import java.util.Iterator;

import com.google.common.base.Splitter;

import uk.co.objectconnexions.expressiveobjects.applib.profiles.Localization;
import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSession;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ResolveState;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.mgr.AdapterManager;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.AggregatedOid;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.CollectionOid;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.Oid;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.RootOid;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.RootOidDefault;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.version.Version;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecId;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.SpecificationLoader;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.SpecificationLoaderSpi;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.adapter.PojoAdapter;

public class PojoAdapterBuilder {

    private PojoAdapterBuilder(){
    }
    
    private Object pojo = new Object();

    // override; else will delegate to SpecificationLoader
    private ObjectSpecification objectSpec;
    
    private SpecificationLoader specificationLoader;
    
    private AdapterManager objectAdapterLookup;
    
    private ObjectSpecId objectSpecId = ObjectSpecId.of("CUS");
    private String identifier = "1";
    // only used if type is AGGREGATED
    private String aggregatedId = "firstName";
    
    private Type type = Type.ROOT;
    private Persistence persistence = Persistence.PERSISTENT;

    private String titleString;

    private Version version;

    private Localization localization;

    private AuthenticationSession authenticationSession;

    
    public static enum Persistence {
        TRANSIENT {
            @Override
            RootOid createOid(ObjectSpecId objectSpecId, String identifier) {
                return RootOidDefault.createTransient(objectSpecId, identifier);
            }

            @Override
            void changeStateOn(PojoAdapter pojoAdapter) {
                pojoAdapter.changeState(ResolveState.TRANSIENT);
            }
        },
        PERSISTENT {
            @Override
            RootOid createOid(ObjectSpecId objectSpecId, String identifier) {
                return RootOidDefault.create(objectSpecId, identifier);
            }

            @Override
            void changeStateOn(PojoAdapter pojoAdapter) {
                pojoAdapter.changeState(ResolveState.TRANSIENT);
                pojoAdapter.changeState(ResolveState.RESOLVED);
            }
        }, 
        VALUE {
            @Override
            RootOid createOid(ObjectSpecId objectSpecId, String identifier) {
                return null;
            }

            @Override
            void changeStateOn(PojoAdapter pojoAdapter) {
                pojoAdapter.changeState(ResolveState.VALUE);
            }
        };
        abstract RootOid createOid(ObjectSpecId objectSpecId, String identifier);

        abstract void changeStateOn(PojoAdapter pojoAdapter);
    }

    public static enum Type {
        ROOT {
            @Override
            Oid oidFor(RootOid rootOid, ObjectSpecId objectSpecId, String unused) {
                return rootOid;
            }
        }, AGGREGATED {
            @Override
            Oid oidFor(RootOid rootOid, ObjectSpecId objectSpecId, String aggregateLocalId) {
                return new AggregatedOid(objectSpecId, rootOid, aggregateLocalId);
            }
        }, COLLECTION {
            @Override
            Oid oidFor(RootOid rootOid, ObjectSpecId objectSpecId, String collectionId) {
                return new CollectionOid(rootOid, collectionId);
            }
        }, VALUE {
            @Override
            Oid oidFor(RootOid rootOid, ObjectSpecId objectSpecId, String unused) {
                return null;
            }
        };

        abstract Oid oidFor(RootOid rootOid, ObjectSpecId objectSpecId, String supplementalId);
    }

    public static PojoAdapterBuilder create() {
        return new PojoAdapterBuilder();
    }

    public PojoAdapterBuilder withAggregatedId(String aggregatedId) {
        this.aggregatedId = aggregatedId;
        return this;
    }
    
    public PojoAdapterBuilder withIdentifier(String identifier) {
        this.identifier = identifier;
        return this;
    }
    
    public PojoAdapterBuilder withObjectType(String objectType) {
        this.objectSpecId = ObjectSpecId.of(objectType);
        return this;
    }
    
    public PojoAdapterBuilder withPojo(Object pojo) {
        this.pojo = pojo;
        return this;
    }

    public PojoAdapterBuilder withOid(String oidAndTitle) {
        final Iterator<String> iterator = Splitter.on("|").split(oidAndTitle).iterator();
        if(!iterator.hasNext()) { return this; }
        withObjectType(iterator.next());
        if(!iterator.hasNext()) { return this; }
        withIdentifier(iterator.next());
        if(!iterator.hasNext()) { return this; }
        withTitleString(iterator.next());
        return this;
    }
    
    /**
     * A Persistence of VALUE implies a Type of VALUE also
     */
    public PojoAdapterBuilder with(Persistence persistence) {
        this.persistence = persistence;
        if(persistence == Persistence.VALUE) {
            this.type = Type.VALUE;
        }
        return this;
    }
    
    /**
     * A Type of VALUE implies a Persistence of VALUE also.
     */
    public PojoAdapterBuilder with(Type type) {
        this.type = type;
        if(type == Type.VALUE) {
            this.persistence = Persistence.VALUE;
        }
        return this;
    }
    
    public PojoAdapterBuilder with(ObjectSpecification objectSpec) {
        this.objectSpec = objectSpec;
        return this;
    }

    public PojoAdapterBuilder with(AdapterManager objectAdapterLookup) {
        this.objectAdapterLookup = objectAdapterLookup;
        return this;
    }
    
    public PojoAdapterBuilder with(SpecificationLoader specificationLoader) {
        this.specificationLoader = specificationLoader;
        return this;
    }
    
    public PojoAdapterBuilder with(AuthenticationSession authenticationSession) {
        this.authenticationSession = authenticationSession;
        return this;
    }
    
    public PojoAdapterBuilder with(Localization localization) {
        this.localization = localization;
        return this;
    }

    public PojoAdapterBuilder with(Version version) {
        this.version = version;
        return this;
    }

    public PojoAdapterBuilder withTitleString(String titleString) {
        this.titleString = titleString;
        return this;
    }

    public PojoAdapter build() {
        final RootOid rootOid = persistence.createOid(objectSpecId, identifier);
        final Oid oid = type.oidFor(rootOid, objectSpecId, aggregatedId);
        final PojoAdapter pojoAdapter = new PojoAdapter(pojo, oid, specificationLoader, objectAdapterLookup, localization, authenticationSession) {
            @Override
            public ObjectSpecification getSpecification() { return objectSpec != null? objectSpec: super.getSpecification(); }
            @Override
            public String titleString() {
                return titleString != null? titleString: super.titleString();
            }
        };
        persistence.changeStateOn(pojoAdapter);
        if(persistence == Persistence.PERSISTENT && version != null) {
            pojoAdapter.setVersion(version);
        }
        return pojoAdapter;
    }



}
