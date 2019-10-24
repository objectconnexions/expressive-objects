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

package uk.co.objectconnexions.expressiveobjects.objectstore.nosql.db;

import java.util.List;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.AggregatedOid;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.OneToOneAssociation;

public interface StateReader {

    /**
     * Aggregated objects are persisted &quot;under&quot; a key taken from the
     * association (property or collection).
     * 
     * <p>
     * This is not to be confused with the adapter's {@link AggregatedOid#getLocalId() localId},
     * which is a locally unique id for the adapter within the aggregate.
     * 
     * <p>
     * The parameter passed here is the {@link OneToOneAssociation#getId() property Id}.
     * 
     * @param associationId - under which the aggregate (property/collection) were persisted. 
     */
    StateReader readAggregate(String propertyId);

    List<StateReader> readCollection(String collectionId);

    long readLongField(String id);

    String readField(String id);

    String readEncrytionType();

//    String readObjectType();
//    String readId();
    
    String readOid();

    String readVersion();

    String readUser();

    String readTime();


}
