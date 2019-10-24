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

package uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.algorithm;

import java.util.List;

import org.apache.log4j.Logger;

import uk.co.objectconnexions.expressiveobjects.core.commons.lang.ToString;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ResolveState;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.collections.modify.CollectionFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.collections.modify.CollectionFacetUtils;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.callbacks.CallbackUtils;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.callbacks.PersistedCallbackFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.callbacks.PersistingCallbackFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociation;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.ObjectPersistenceException;

public class PersistAlgorithmDefault extends PersistAlgorithmAbstract {
    private static final Logger LOG = Logger.getLogger(PersistAlgorithmDefault.class);

    @Override
    public String name() {
        return "Simple Bottom Up Persistence Walker";
    }

    @Override
    public void makePersistent(final ObjectAdapter adapter, final ToPersistObjectSet toPersistObjectSet) {
        if (adapter.getSpecification().isParentedOrFreeCollection()) {
            if(LOG.isDebugEnabled()) {
                LOG.debug("persist " + adapter);
            }
            if (adapter.isGhost()) {
                adapter.changeState(ResolveState.RESOLVING);
                adapter.changeState(ResolveState.RESOLVED);
            } else if (adapter.isTransient()) {
                adapter.changeState(ResolveState.RESOLVED);
            }
            final CollectionFacet facet = CollectionFacetUtils.getCollectionFacetFromSpec(adapter);
            for (final ObjectAdapter element : facet.iterable(adapter)) {
                persist(element, toPersistObjectSet);
            }
        } else {
            assertObjectNotPersistentAndPersistable(adapter);
            persist(adapter, toPersistObjectSet);
        }
    }

    protected void persist(final ObjectAdapter adapter, final ToPersistObjectSet toPersistObjectSet) {
        if (alreadyPersistedOrNotPersistableOrServiceOrStandalone(adapter)) {
            return;
        }

        final List<ObjectAssociation> associations = adapter.getSpecification().getAssociations();
        if (!adapter.getSpecification().isEncodeable() && associations.size() > 0) {
            if(LOG.isDebugEnabled()) {
                LOG.debug("make persistent " + adapter);
            }
            CallbackUtils.callCallback(adapter, PersistingCallbackFacet.class);
            toPersistObjectSet.remapAsPersistent(adapter);
            
            // was previously to SERIALIZING_RESOLVED, but 
            // after refactoring simplifications this is now equivalent to UPDATING
            final ResolveState stateWhilePersisting = ResolveState.UPDATING;
            
            adapter.changeState(stateWhilePersisting);  

            for (int i = 0; i < associations.size(); i++) {
                final ObjectAssociation objectAssoc = associations.get(i);
                if (objectAssoc.isNotPersisted()) {
                    continue;
                }
                if (objectAssoc.isOneToManyAssociation()) {
                    final ObjectAdapter collection = objectAssoc.get(adapter);
                    if (collection == null) {
                        throw new ObjectPersistenceException("Collection " + objectAssoc.getName() + " does not exist in " + adapter.getSpecification().getFullIdentifier());
                    }
                    makePersistent(collection, toPersistObjectSet);
                } else {
                    final ObjectAdapter fieldValue = objectAssoc.get(adapter);
                    if (fieldValue == null) {
                        continue;
                    }
                    persist(fieldValue, toPersistObjectSet);
                }
            }
            toPersistObjectSet.addCreateObjectCommand(adapter);
            CallbackUtils.callCallback(adapter, PersistedCallbackFacet.class);
            adapter.changeState(stateWhilePersisting.getEndState());
        }

    }

    @Override
    public String toString() {
        final ToString toString = new ToString(this);
        return toString.toString();
    }

}
