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

package uk.co.objectconnexions.expressiveobjects.progmodels.dflt;

import org.junit.Assert;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.collections.modify.CollectionFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.describedas.DescribedAsFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.named.NamedFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.notpersistable.NotPersistableFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.plural.PluralFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.title.TitleFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.typeof.TypeOfFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.ObjectReflectorDefault;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.validprops.ObjectValidPropertiesFacet;

public class ObjectReflectorDefaultTest_object extends ObjectReflectorDefaultTestAbstract {

    public static class TestDomainObject {

    }

    @Override
    protected ObjectSpecification loadSpecification(final ObjectReflectorDefault reflector) {
        return reflector.loadSpecification(TestDomainObject.class);
    }

    @Test
    public void testType() throws Exception {
        Assert.assertTrue(specification.isNotCollection());
    }

    @Test
    public void testName() throws Exception {
        Assert.assertEquals(TestDomainObject.class.getName(), specification.getFullIdentifier());
    }

    @Test
    public void testStandardFacets() throws Exception {
        Assert.assertNotNull(specification.getFacet(NamedFacet.class));
        Assert.assertNotNull(specification.getFacet(DescribedAsFacet.class));
        Assert.assertNotNull(specification.getFacet(TitleFacet.class));
        Assert.assertNotNull(specification.getFacet(PluralFacet.class));
        Assert.assertNotNull(specification.getFacet(NotPersistableFacet.class));
        Assert.assertNotNull(specification.getFacet(ObjectValidPropertiesFacet.class));
    }

    @Test
    public void testNoCollectionFacet() throws Exception {
        final Facet facet = specification.getFacet(CollectionFacet.class);
        Assert.assertNull(facet);
    }

    @Test
    public void testNoTypeOfFacet() throws Exception {
        final TypeOfFacet facet = specification.getFacet(TypeOfFacet.class);
        Assert.assertNull(facet);
    }

}
