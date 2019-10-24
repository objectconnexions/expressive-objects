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
package uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecId;

public class OidMarshallerTest_marshal {

    private OidMarshaller oidMarshaller;
    
    @Before
    public void setUp() throws Exception {
        oidMarshaller = new OidMarshaller();
    }
    
    @Test
    public void rootOid() {
        final String marshal = oidMarshaller.marshal(RootOidDefault.create(ObjectSpecId.of("CUS"),  "123"));
        assertThat(marshal, equalTo("CUS:123"));
    }

    @Test
    public void rootOid_transient() {
        final String marshal = oidMarshaller.marshal(RootOidDefault.createTransient(ObjectSpecId.of("CUS"),  "123"));
        assertThat(marshal, equalTo("!CUS:123"));
    }
    
    @Test
    public void rootOid_versionSequence() {
        final String marshal = oidMarshaller.marshal(RootOidDefault.create(ObjectSpecId.of("CUS"),  "123", 90807L));
        assertThat(marshal, equalTo("CUS:123^90807::"));
    }

    @Test
    public void rootOid_versionSequenceAndUser() {
        final String marshal = oidMarshaller.marshal(RootOidDefault.create(ObjectSpecId.of("CUS"),  "123", 90807L, "joebloggs"));
        assertThat(marshal, equalTo("CUS:123^90807:joebloggs:"));
    }

    @Test
    public void rootOid_versionSequenceAndUtc() {
        final String marshal = oidMarshaller.marshal(RootOidDefault.create(ObjectSpecId.of("CUS"),  "123", 90807L, 3453452141L));
        assertThat(marshal, equalTo("CUS:123^90807::3453452141"));
    }

    @Test
    public void rootOid_versionSequenceAndUserAndUtc() {
        final String marshal = oidMarshaller.marshal(RootOidDefault.create(ObjectSpecId.of("CUS"),  "123", 90807L, "joebloggs", 3453452141L));
        assertThat(marshal, equalTo("CUS:123^90807:joebloggs:3453452141"));
    }


}
