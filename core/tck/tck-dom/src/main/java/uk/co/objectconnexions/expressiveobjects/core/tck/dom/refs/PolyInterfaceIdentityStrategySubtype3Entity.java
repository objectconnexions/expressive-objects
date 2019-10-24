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

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;

import uk.co.objectconnexions.expressiveobjects.applib.annotation.MemberOrder;
import uk.co.objectconnexions.expressiveobjects.applib.annotation.ObjectType;
import uk.co.objectconnexions.expressiveobjects.applib.annotation.Optional;
import uk.co.objectconnexions.expressiveobjects.applib.annotation.Title;

@javax.jdo.annotations.PersistenceCapable(identityType=IdentityType.DATASTORE)
@javax.jdo.annotations.Discriminator("PIS3")
@javax.jdo.annotations.DatastoreIdentity(strategy=javax.jdo.annotations.IdGeneratorStrategy.IDENTITY)
@Inheritance(strategy=InheritanceStrategy.NEW_TABLE)
@ObjectType("PIS3")
public class PolyInterfaceIdentityStrategySubtype3Entity extends BaseEntity implements PolyInterfaceIdentityStrategy {

    

    // {{ Parent (title #1)
    private PolyInterfaceIdentityStrategyParentEntity parent;

    @Title(sequence="1", append="-")
    @MemberOrder(sequence = "1")
    @Optional
    public PolyInterfaceIdentityStrategyParentEntity getParent() {
        return parent;
    }

    public void setParent(final PolyInterfaceIdentityStrategyParentEntity parent) {
        this.parent = parent;
    }

    // }}

    // {{ Name  (title #2)
    private String name;

    @Title(sequence="2")
    @MemberOrder(sequence = "1")
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    // }}

    
    // {{ Baz (property)
    private java.math.BigDecimal boz;

    @MemberOrder(sequence = "1")
    public java.math.BigDecimal getBoz() {
        return boz;
    }

    public void setBoz(final java.math.BigDecimal baz) {
        this.boz = baz;
    }
    // }}



}
