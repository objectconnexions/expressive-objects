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

import java.util.HashSet;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jmock.Expectations;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetdecorator.FacetDecorator;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.collections.modify.CollectionFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.describedas.DescribedAsFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.named.NamedFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.plural.PluralFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.typeof.TypeOfFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.runtimecontext.RuntimeContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.runtimecontext.noruntime.RuntimeContextNoRuntime;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.ObjectReflectorDefault;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.classsubstitutor.ClassSubstitutorAbstract;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.collectiontyperegistry.CollectionTypeRegistryDefault;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.traverser.SpecificationTraverserDefault;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.layout.dflt.MemberLayoutArrangerDefault;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.metamodelvalidator.dflt.MetaModelValidatorDefault;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmock.auto.Mock;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.InjectIntoJMockAction;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

public abstract class ObjectReflectorDefaultTestAbstract {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_ONLY);
    
    private RuntimeContext runtimeContext;

    @Mock
    private ExpressiveObjectsConfiguration mockConfiguration;
    
    // is loaded by subclasses
    protected ObjectSpecification specification;

    
    @Before
    public void setUp() throws Exception {
        Logger.getRootLogger().setLevel(Level.OFF);

        context.checking(new Expectations() {
            {
                allowing(mockConfiguration).injectInto(with(any(Object.class)));
                will(InjectIntoJMockAction.injectInto());
                
                ignoring(mockConfiguration);
            }
        });

        runtimeContext = new RuntimeContextNoRuntime();
        final ObjectReflectorDefault reflector = new ObjectReflectorDefault(mockConfiguration, new ClassSubstitutorAbstract() {}, new CollectionTypeRegistryDefault(), new SpecificationTraverserDefault(), new MemberLayoutArrangerDefault(), new ProgrammingModelFacetsJava5(), new HashSet<FacetDecorator>(),
                new MetaModelValidatorDefault());
        reflector.setRuntimeContext(runtimeContext);
        reflector.init();
        
        specification = loadSpecification(reflector);
    }

    protected abstract ObjectSpecification loadSpecification(ObjectReflectorDefault reflector);

    @Test
    public void testCollectionFacet() throws Exception {
        final Facet facet = specification.getFacet(CollectionFacet.class);
        Assert.assertNull(facet);
    }

    @Test
    public void testTypeOfFacet() throws Exception {
        final TypeOfFacet facet = specification.getFacet(TypeOfFacet.class);
        Assert.assertNull(facet);
    }

    @Test
    public void testNamedFaced() throws Exception {
        final Facet facet = specification.getFacet(NamedFacet.class);
        Assert.assertNotNull(facet);
    }

    @Test
    public void testPluralFaced() throws Exception {
        final Facet facet = specification.getFacet(PluralFacet.class);
        Assert.assertNotNull(facet);
    }

    @Test
    public void testDescriptionFacet() throws Exception {
        final Facet facet = specification.getFacet(DescribedAsFacet.class);
        Assert.assertNotNull(facet);
    }

}
