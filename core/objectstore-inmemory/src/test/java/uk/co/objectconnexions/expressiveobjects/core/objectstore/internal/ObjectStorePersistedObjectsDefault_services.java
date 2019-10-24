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

package uk.co.objectconnexions.expressiveobjects.core.objectstore.internal;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.Oid;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecId;
import uk.co.objectconnexions.expressiveobjects.core.objectstore.internal.ObjectStorePersistedObjectsDefault;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmock.auto.Mock;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

public class ObjectStorePersistedObjectsDefault_services {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    private Oid mockOidForFooService;
    @Mock
    private Oid mockOidForBarService;
    
    private ObjectStorePersistedObjectsDefault persistedObjects;

    @Before
    public void setUp() throws Exception {
        persistedObjects = new ObjectStorePersistedObjectsDefault();
    }

    @Test
    public void noServicesInitially() throws Exception {
        final Oid service = persistedObjects.getService(ObjectSpecId.of("fooService"));
        assertThat(service, is(nullValue()));
    }

    @Test
    public void registerServicesMakesAvailable() throws Exception {
        persistedObjects.registerService(ObjectSpecId.of("fooService"), mockOidForFooService);

        final Oid service = persistedObjects.getService(ObjectSpecId.of("fooService"));
        assertThat(service, is(mockOidForFooService));
    }

    @Test
    public void registerServicesWhenMoreThanOnePullsOutTheCorrectOne() throws Exception {
        persistedObjects.registerService(ObjectSpecId.of("fooService"), mockOidForFooService);
        persistedObjects.registerService(ObjectSpecId.of("barService"), mockOidForBarService);

        final Oid service = persistedObjects.getService(ObjectSpecId.of("fooService"));
        assertThat(service, is(mockOidForFooService));
    }

}
