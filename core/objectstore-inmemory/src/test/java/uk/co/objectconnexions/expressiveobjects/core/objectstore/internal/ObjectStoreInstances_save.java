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
import static org.junit.Assert.assertThat;

import java.util.Map;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSession;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.Oid;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.version.Version;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.objectstore.internal.ObjectStoreInstances;

/**
 * Tested in style of <i>Working Effectively with Legacy Code</i> (Feathers) and
 * <i>Growing Object-Oriented Software</i> (Freeman &amp; Pryce).
 */
@RunWith(JMock.class)
public class ObjectStoreInstances_save {

    private ObjectStoreInstances instances;

    private final Mockery context = new JUnit4Mockery();

    private ObjectSpecification mockSpec;
    private ObjectAdapter mockAdapter;
    private AuthenticationSession mockAuthSession;

    @Before
    public void setUp() throws Exception {
        mockSpec = context.mock(ObjectSpecification.class);
        mockAdapter = context.mock(ObjectAdapter.class);
        mockAuthSession = context.mock(AuthenticationSession.class);
        instances = new ObjectStoreInstances(mockSpec) {
            @Override
            protected AuthenticationSession getAuthenticationSession() {
                return mockAuthSession;
            }
        };
        ignoreAuthenticationSession();
    }

    private void ignoreAuthenticationSession() {
        context.checking(new Expectations() {
            {
                ignoring(mockAuthSession);
            }
        });
    }

    @Test
    public void saveUpdatesTheOptimisticLock() throws Exception {
        allowingGetOidAndGetObjectAndTitleStringFromAdapter();
        context.checking(new Expectations() {
            {
                one(mockAdapter).setVersion(with(any(Version.class)));
            }
        });
        instances.save(mockAdapter);

    }

    @Test
    public void saveStoresObject() throws Exception {
        allowingGetOidAndGetObjectAndTitleStringFromAdapter();
        ignoringInteractionsWithAdapter();
        instances.save(mockAdapter);

        final Map<Oid, Object> objectInstances = instances.getObjectInstances();
        assertThat(objectInstances.size(), is(1));

        final Set<Oid> oids = instances.getOids();
        assertThat(oids.size(), is(1));

        assertThat(instances.hasInstances(), is(true));

    }

    private void ignoringInteractionsWithAdapter() {
        context.checking(new Expectations() {
            {
                ignoring(mockAdapter);
            }
        });
    }

    private void allowingGetOidAndGetObjectAndTitleStringFromAdapter() {
        context.checking(new Expectations() {
            {
                allowing(mockAdapter).getOid();
                allowing(mockAdapter).getObject();
                allowing(mockAdapter).titleString();
            }
        });
    }

}
