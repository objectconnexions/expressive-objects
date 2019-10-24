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

package uk.co.objectconnexions.expressiveobjects.core.tck.dom.refs;

import uk.co.objectconnexions.expressiveobjects.applib.annotation.Hidden;
import uk.co.objectconnexions.expressiveobjects.applib.annotation.MemberOrder;
import uk.co.objectconnexions.expressiveobjects.applib.annotation.Named;
import uk.co.objectconnexions.expressiveobjects.applib.annotation.ObjectType;
import uk.co.objectconnexions.expressiveobjects.core.tck.dom.AbstractEntityRepository;

@Named("PolyInterfaceIdentityStrategyParentEntities")
@ObjectType("PolyInterfaceIdentityStrategyParentEntities")
public class PolyInterfaceIdentityStrategyParentEntityRepository extends AbstractEntityRepository<PolyInterfaceIdentityStrategyParentEntity> {

    public PolyInterfaceIdentityStrategyParentEntityRepository() {
        super(PolyInterfaceIdentityStrategyParentEntity.class, "PolyInterfaceIdentityStrategyParentEntities");
    }

    @MemberOrder(sequence = "2")
    public PolyInterfaceIdentityStrategyParentEntity newEntity(final String name) {
        final PolyInterfaceIdentityStrategyParentEntity entity = newTransientInstance(PolyInterfaceIdentityStrategyParentEntity.class);
        entity.setName(name);
        persist(entity);
        return entity;
    }

    
    @Hidden
    public void registerType(PolyInterfaceIdentityStrategySubtype1Entity e) { }

    @Hidden
    public void registerType(PolyInterfaceIdentityStrategySubtype2Entity e) { }

    @Hidden
    public void registerType(PolyInterfaceIdentityStrategySubtype3Entity e) { }

}
