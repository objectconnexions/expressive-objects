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

package uk.co.objectconnexions.expressiveobjects.core.runtime.memento;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.core.commons.encoding.DataOutputStreamExtended;
import uk.co.objectconnexions.expressiveobjects.core.integtestsupport.ExpressiveObjectsSystemWithFixtures;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.runtime.memento.Memento;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmock.auto.Mock;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

public class MementoTest_encodedData {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Rule
    public ExpressiveObjectsSystemWithFixtures iswf = ExpressiveObjectsSystemWithFixtures.builder().build();

    private ObjectAdapter rootAdapter;

    private Memento memento;

    @Mock
    private DataOutputStreamExtended mockOutputImpl;

    @Before
    public void setUpSystem() throws Exception {
        iswf.fixtures.smpl1.setName("Fred");
        iswf.fixtures.smpl2.setName("Harry");
        
        iswf.fixtures.rfcg1.setReference(iswf.fixtures.smpl1);
        
        iswf.fixtures.prnt1.getHomogeneousCollection().add(iswf.fixtures.smpl1);
        iswf.fixtures.prnt1.getHomogeneousCollection().add(iswf.fixtures.smpl2);
        
        iswf.fixtures.prnt1.getHeterogeneousCollection().add(iswf.fixtures.smpl1);
        iswf.fixtures.prnt1.getHeterogeneousCollection().add(iswf.fixtures.rfcg1);

        
        rootAdapter = ExpressiveObjectsContext.getPersistenceSession().getAdapterManager().adapterFor(iswf.fixtures.smpl1);

        memento = new Memento(rootAdapter);
    }

    
    @Test
    public void encodedData() throws Exception {
        context.checking(new Expectations() {
            {
                one(mockOutputImpl).writeEncodable(memento.getData());
            }
        });
        memento.encodedData(mockOutputImpl);
    }

}
