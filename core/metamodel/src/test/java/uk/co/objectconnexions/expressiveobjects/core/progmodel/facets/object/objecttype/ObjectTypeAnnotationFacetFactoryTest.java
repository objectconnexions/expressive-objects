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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.objecttype;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.applib.annotation.ObjectType;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetFactory.ProcessClassContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.objecttype.ObjectSpecIdFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecId;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.AbstractFacetFactoryJUnit4TestCase;

public class ObjectTypeAnnotationFacetFactoryTest extends AbstractFacetFactoryJUnit4TestCase {

    private ObjectSpecIdAnnotationFacetFactory facetFactory;

    @Before
    public void setUp() throws Exception {
        facetFactory = new ObjectSpecIdAnnotationFacetFactory();
    }

    @Test
    public void objectTypeAnnotationPickedUpOnClass() {

        @ObjectType("CUS")
        class Customer {
        }
        
        expectNoMethodsRemoved();
        
        facetFactory.process(new ProcessClassContext(Customer.class, methodRemover, facetHolderImpl));

        final ObjectSpecIdFacet facet = facetHolderImpl.getFacet(ObjectSpecIdFacet.class);
        
        assertThat(facet, is(not(nullValue())));
        assertThat(facet instanceof ObjectSpecIdFacetForObjectTypeAnnotation, is(true));
        assertThat(facet.value(), is(ObjectSpecId.of("CUS")));

    }

}
