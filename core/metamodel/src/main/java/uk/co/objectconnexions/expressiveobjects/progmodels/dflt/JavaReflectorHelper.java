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

import java.util.Collection;
import java.util.Set;

import org.apache.log4j.Logger;

import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.ClassSubstitutorFactory;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.MetaModelRefiner;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetdecorator.FacetDecorator;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.layout.MemberLayoutArranger;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.progmodel.ProgrammingModel;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.SpecificationLoaderSpi;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.ObjectReflectorDefault;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.classsubstitutor.ClassSubstitutor;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.collectiontyperegistry.CollectionTypeRegistry;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.collectiontyperegistry.CollectionTypeRegistryDefault;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.traverser.SpecificationTraverser;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.traverser.SpecificationTraverserDefault;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.validator.MetaModelValidator;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.validator.MetaModelValidatorComposite;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.layout.dflt.MemberLayoutArrangerDefault;

public final class JavaReflectorHelper  {
    
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(JavaReflectorHelper.class);

    private JavaReflectorHelper(){}
    
    public static SpecificationLoaderSpi createObjectReflector(
                                                           final ProgrammingModel programmingModel,
                                                           final ClassSubstitutorFactory classSubstitutorFactory,
                                                           final Collection<MetaModelRefiner> metaModelRefiners,
                                                           final Set<FacetDecorator> facetDecorators,
                                                           final MetaModelValidator mmv, 
                                                           final ExpressiveObjectsConfiguration configuration) {
        final MemberLayoutArranger memberLayoutArranger = new MemberLayoutArrangerDefault();
        final SpecificationTraverser specificationTraverser = new SpecificationTraverserDefault();
        final CollectionTypeRegistry collectionTypeRegistry = new CollectionTypeRegistryDefault();
        final ClassSubstitutor classSubstitutor = classSubstitutorFactory.createClassSubstitutor(configuration);
        
        MetaModelValidatorComposite metaModelValidator = MetaModelValidatorComposite.asComposite(mmv);
        for (MetaModelRefiner metaModelRefiner : metaModelRefiners) {
            metaModelRefiner.refineProgrammingModel(programmingModel, configuration);
            metaModelRefiner.refineMetaModelValidator(metaModelValidator, configuration);
        }
        
        // the programming model is itself also a MetaModelValidatorRefiner
        if(!metaModelRefiners.contains(programmingModel)) {
            programmingModel.refineMetaModelValidator(metaModelValidator, configuration);
        }
        
        return new ObjectReflectorDefault(configuration, classSubstitutor, collectionTypeRegistry, specificationTraverser, memberLayoutArranger, programmingModel, facetDecorators, metaModelValidator);
    }

}
