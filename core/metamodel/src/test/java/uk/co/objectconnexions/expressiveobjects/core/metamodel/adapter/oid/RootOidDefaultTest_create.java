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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecId;

public class RootOidDefaultTest_create  {


    @Test
    public void create() throws Exception {
        ObjectSpecId objectSpecId = ObjectSpecId.of("CUS");
        RootOidDefault oid = RootOidDefault.create(objectSpecId, "123");
        assertThat(oid.getObjectSpecId(), is(objectSpecId));
        assertThat(oid.getIdentifier(), is("123"));
        assertThat(oid.getVersion(), is(nullValue()));
        
        assertThat(oid.isTransient(), is(false));
    }
    
    @Test
    public void createTransient() throws Exception {
        ObjectSpecId objectSpecId = ObjectSpecId.of("CUS");
        RootOidDefault oid = RootOidDefault.createTransient(objectSpecId, "123");
        assertThat(oid.getObjectSpecId(), is(objectSpecId));
        assertThat(oid.getIdentifier(), is("123"));
        assertThat(oid.getVersion(), is(nullValue()));
        
        assertThat(oid.isTransient(), is(true));
    }

    
    @Test
    public void createWithVersion() throws Exception {
        ObjectSpecId objectSpecId = ObjectSpecId.of("CUS");
        RootOidDefault oid = RootOidDefault.create(objectSpecId, "123", 456L);
        assertThat(oid.getObjectSpecId(), is(objectSpecId));
        assertThat(oid.getIdentifier(), is("123"));
        assertThat(oid.getVersion().getSequence(), is(456L));
        
        assertThat(oid.isTransient(), is(false));
    }
    
    @Test
    public void createTransientNoVersion() throws Exception {
        ObjectSpecId objectSpecId = ObjectSpecId.of("CUS");
        RootOidDefault oid = RootOidDefault.createTransient(objectSpecId, "123");
        assertThat(oid.getObjectSpecId(), is(objectSpecId));
        assertThat(oid.getIdentifier(), is("123"));
        assertThat(oid.getVersion(), is(nullValue()));
        
        assertThat(oid.isTransient(), is(true));
    }

}
