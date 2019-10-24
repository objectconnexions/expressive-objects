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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.ordering.memberorder;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetedMethod;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.layout.memberorderfacet.MemberOrderComparator;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.members.order.MemberOrderFacetAnnotation;

public class MemberOrderComparatorTest extends TestCase {

    public static void main(final String[] args) {
        junit.textui.TestRunner.run(new TestSuite(MemberOrderComparatorTest.class));
    }

    private MemberOrderComparator comparator, laxComparator;

    public static class Customer {
        private String abc;

        public String getAbc() {
            return abc;
        }
    }

    private final FacetedMethod m1 = FacetedMethod.createForProperty(Customer.class, "abc");
    private final FacetedMethod m2 = FacetedMethod.createForProperty(Customer.class, "abc");

    @Override
    protected void setUp() {
        LogManager.getLoggerRepository().setThreshold(Level.OFF);

        comparator = new MemberOrderComparator(true);
        laxComparator = new MemberOrderComparator(false);
    }

    @Override
    protected void tearDown() throws Exception {
    }

    public void testDefaultGroupOneComponent() {
        m1.addFacet(new MemberOrderFacetAnnotation("", "1", m1));
        m2.addFacet(new MemberOrderFacetAnnotation("", "2", m2));
        assertEquals(-1, comparator.compare(m1, m2));
    }

    public void testDefaultGroupOneComponentOtherWay() {
        m1.addFacet(new MemberOrderFacetAnnotation("", "2", m1));
        m2.addFacet(new MemberOrderFacetAnnotation("", "1", m2));
        assertEquals(+1, comparator.compare(m1, m2));
    }

    public void testDefaultGroupOneComponentSame() {
        m1.addFacet(new MemberOrderFacetAnnotation("", "1", m1));
        m2.addFacet(new MemberOrderFacetAnnotation("", "1", m2));
        assertEquals(0, comparator.compare(m1, m2));
    }

    public void testDefaultGroupOneSideRunsOutOfComponentsFirst() {
        m1.addFacet(new MemberOrderFacetAnnotation("", "1", m1));
        m2.addFacet(new MemberOrderFacetAnnotation("", "1.1", m2));
        assertEquals(-1, comparator.compare(m1, m2));
    }

    public void testDefaultGroupOneSideRunsOutOfComponentsFirstOtherWay() {
        m1.addFacet(new MemberOrderFacetAnnotation("", "1.1", m1));
        m2.addFacet(new MemberOrderFacetAnnotation("", "1", m2));
        assertEquals(+1, comparator.compare(m1, m2));
    }

    public void testDefaultGroupOneSideRunsTwoComponents() {
        m1.addFacet(new MemberOrderFacetAnnotation("", "1.1", m1));
        m2.addFacet(new MemberOrderFacetAnnotation("", "1.2", m2));
        assertEquals(-1, comparator.compare(m1, m2));
    }

    public void testDefaultGroupOneSideRunsTwoComponentsOtherWay() {
        m1.addFacet(new MemberOrderFacetAnnotation("", "1.2", m1));
        m2.addFacet(new MemberOrderFacetAnnotation("", "1.1", m2));
        assertEquals(+1, comparator.compare(m1, m2));
    }

    public void testDefaultGroupOneSideRunsLotsOfComponents() {
        m1.addFacet(new MemberOrderFacetAnnotation("", "1.2.5.8.3.3", m1));
        m2.addFacet(new MemberOrderFacetAnnotation("", "1.2.5.8.3.4", m2));
        assertEquals(-1, comparator.compare(m1, m2));
    }

    public void testDefaultGroupOneSideRunsLotsOfComponentsOtherWay() {
        m1.addFacet(new MemberOrderFacetAnnotation("", "1.2.5.8.3.4", m1));
        m2.addFacet(new MemberOrderFacetAnnotation("", "1.2.5.8.3.3", m2));
        assertEquals(+1, comparator.compare(m1, m2));
    }

    public void testDefaultGroupOneSideRunsLotsOfComponentsSame() {
        m1.addFacet(new MemberOrderFacetAnnotation("", "1.2.5.8.3.3", m1));
        m2.addFacet(new MemberOrderFacetAnnotation("", "1.2.5.8.3.3", m2));
        assertEquals(0, comparator.compare(m1, m2));
    }

    public void testNamedGroupOneSideRunsLotsOfComponents() {
        m1.addFacet(new MemberOrderFacetAnnotation("abc", "1.2.5.8.3.3", m1));
        m2.addFacet(new MemberOrderFacetAnnotation("abc", "1.2.5.8.3.4", m2));
        assertEquals(-1, comparator.compare(m1, m2));
    }

    public void testEnsuresInSameGroup() {
        m1.addFacet(new MemberOrderFacetAnnotation("abc", "1", m1));
        m2.addFacet(new MemberOrderFacetAnnotation("def", "2", m2));
        try {
            assertEquals(-1, comparator.compare(m1, m2));
            fail("Exception should have been thrown");
        } catch (final IllegalArgumentException ex) {
            // expected
        }
    }

    public void testEnsuresInSameGroupCanBeDisabled() {
        m1.addFacet(new MemberOrderFacetAnnotation("abc", "1", m1));
        m2.addFacet(new MemberOrderFacetAnnotation("def", "2", m2));
        assertEquals(-1, laxComparator.compare(m1, m2));
    }

    public void testNonAnnotatedAfterAnnotated() {
        // don't annotate m1
        m2.addFacet(new MemberOrderFacetAnnotation("def", "2", m2));
        assertEquals(+1, comparator.compare(m1, m2));
    }

}
