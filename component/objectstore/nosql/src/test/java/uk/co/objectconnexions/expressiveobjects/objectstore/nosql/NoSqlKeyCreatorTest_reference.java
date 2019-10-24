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

package uk.co.objectconnexions.expressiveobjects.objectstore.nosql;

import static org.junit.Assert.assertEquals;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.RootOidDefault;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecId;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmock.auto.Mock;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;
import uk.co.objectconnexions.expressiveobjects.objectstore.nosql.keys.KeyCreatorDefault;

public class NoSqlKeyCreatorTest_reference {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_ONLY);

    @Mock
    private ObjectSpecification specification;
    @Mock
    private ObjectAdapter adapter;
    
    private final String className = "com.foo.bar.SomeClass";
    private final String objectType = "SCL";
    
    private final RootOidDefault rootOidDefault = RootOidDefault.create(ObjectSpecId.of(objectType), ""+123);
    
    private KeyCreatorDefault keyCreatorDefault;

    @Before
    public void setup() {
        keyCreatorDefault = new KeyCreatorDefault();
        
        context.checking(new Expectations() {
            {
                allowing(adapter).getSpecification();
                will(returnValue(specification));

                allowing(adapter).getOid();
                will(returnValue(rootOidDefault));

                allowing(specification).getFullIdentifier();
                will(returnValue(className));
            }
        });
    }

    @Test
    public void reference() throws Exception {
        final String expectedReference = objectType + ":" + rootOidDefault.getIdentifier();
        assertEquals(expectedReference, keyCreatorDefault.oidStrFor(adapter));
    }
}
