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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.jmock.Expectations;
import org.junit.Rule;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectAdapterUtils;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmock.auto.Mock;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

public class ObjectAdapterUtilsTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_ONLY);

    @Mock
    private ObjectAdapter mockObjectAdapter;

    private Object underlyingDomainObject;

    @Test
    public void testUnwrapObjectWhenNull() {
        assertNull(ObjectAdapterUtils.unwrapObject(null));
    }

    @Test
    public void testUnwrapObjectWhenNotNull() {
        underlyingDomainObject = new Object(); 
        expectAdapterWillReturn(underlyingDomainObject);
        assertEquals(underlyingDomainObject, ObjectAdapterUtils.unwrapObject(mockObjectAdapter));
    }

    @Test
    public void testUnwrapStringWhenNull() {
        assertNull(ObjectAdapterUtils.unwrapObjectAsString(null));
    }

    @Test
    public void testUnwrapStringWhenNotNullButNotString() {
        underlyingDomainObject = new Object(); 
        expectAdapterWillReturn(underlyingDomainObject);
        assertNull(ObjectAdapterUtils.unwrapObjectAsString(mockObjectAdapter));
    }

    @Test
    public void testUnwrapStringWhenNotNullAndString() {
        underlyingDomainObject = "huzzah";
        expectAdapterWillReturn(underlyingDomainObject);
        assertEquals("huzzah", ObjectAdapterUtils.unwrapObjectAsString(mockObjectAdapter));
    }

    private void expectAdapterWillReturn(final Object domainObject) {
        context.checking(new Expectations() {
            {
                allowing(mockObjectAdapter).getObject();
                will(returnValue(domainObject));
            }
        });
    }
    

}
