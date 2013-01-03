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

package uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.adapterfactory.pojo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.Date;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.applib.profiles.Localization;
import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSession;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ResolveState;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.mgr.AdapterManager;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.RootOidDefault;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.version.ConcurrencyException;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.version.Version;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecId;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.SpecificationLoaderSpi;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.adapter.PojoAdapter;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.transaction.PojoAdapterBuilder;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmock.auto.Mock;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

public class PojoAdapterTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);
    
    private ObjectAdapter adapter;
    private RuntimeTestPojo domainObject;

    @Mock
    private Version mockVersion;
    @Mock
    private Version mockVersion2;
    @Mock
    private SpecificationLoaderSpi mockSpecificationLoader;
    @Mock
    private AuthenticationSession mockAuthenticationSession;
    @Mock
    private AdapterManager mockObjectAdapterLookup;
    @Mock
    private Localization mockLocalization;
    
    @Before
    public void setUp() throws Exception {
        domainObject = new RuntimeTestPojo();
        
        adapter = new PojoAdapter(domainObject, RootOidDefault.create(ObjectSpecId.of("CUS"), "1"), mockSpecificationLoader, mockObjectAdapterLookup, mockLocalization, mockAuthenticationSession);
        adapter.setVersion(mockVersion);
        
        allowUnimportantMethodCallsOn(mockVersion);
        allowUnimportantMethodCallsOn(mockVersion2);
    }

    private void allowUnimportantMethodCallsOn(final Version version) {
        context.checking(new Expectations() {
            {
                allowing(version).getSequence();
                allowing(version).getUtcTimestamp();
                allowing(version).sequence();
                allowing(version).getUser();
                
                allowing(version).getTime();
                will(returnValue(new Date()));
                
                allowing(mockAuthenticationSession).getUserName();
                will(returnValue("fredbloggs"));
            }
        });
    }

    @Test
    public void getOid_initially() {
        assertEquals(RootOidDefault.create(ObjectSpecId.of("CUS"), "1"), adapter.getOid());
    }

    @Test
    public void getObject_initially() {
        assertEquals(domainObject, adapter.getObject());
    }

    @Test
    public void getResolveState_initially() {
        assertEquals(ResolveState.NEW, adapter.getResolveState());
    }

    @Test
    public void changeState_newToTransient() {
        adapter.changeState(ResolveState.TRANSIENT);
        assertEquals(ResolveState.TRANSIENT, adapter.getResolveState());
    }

    @Test
    public void getVersion_initially() throws Exception {
        assertSame(mockVersion, adapter.getVersion());
    }

    @Test
    public void checkLock_whenVersionsSame() throws Exception {

        context.checking(new Expectations() {
            {
                one(mockVersion).different(mockVersion2);
                will(returnValue(false));
            }
        });
        
        adapter.checkLock(mockVersion2);
    }

    @Test(expected=ConcurrencyException.class)
    public void checkLock_whenVersionsDifferent() throws Exception {

        adapter = PojoAdapterBuilder.create().with(mockSpecificationLoader).withTitleString("some pojo").with(mockVersion).with(mockAuthenticationSession).build();
        
        context.checking(new Expectations() {
            {
                one(mockVersion).different(mockVersion2);
                will(returnValue(true));
            }
        });
        
        adapter.checkLock(mockVersion2);
    }

}
