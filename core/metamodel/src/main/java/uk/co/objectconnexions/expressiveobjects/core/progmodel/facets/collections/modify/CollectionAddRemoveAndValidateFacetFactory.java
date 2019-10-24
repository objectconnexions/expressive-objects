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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.collections.modify;

import java.lang.reflect.Method;

import uk.co.objectconnexions.expressiveobjects.core.commons.lang.NameUtils;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectDirtier;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectDirtierAware;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.exceptions.MetaModelException;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetUtil;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FeatureType;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.collections.modify.CollectionAddToFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.collections.modify.CollectionRemoveFromFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.methodutils.MethodScope;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.MethodFinderUtils;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.MethodPrefixBasedFacetFactoryAbstract;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.MethodPrefixConstants;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.collections.validate.CollectionValidateAddToFacetViaMethod;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.collections.validate.CollectionValidateRemoveFromFacetViaMethod;

/**
 * TODO: should probably split out into two {@link FacetFactory}s, one for
 * <tt>addTo()</tt>/<tt>removeFrom()</tt> and one for <tt>validateAddTo()</tt>/
 * <tt>validateRemoveFrom()</tt>.
 */
public class CollectionAddRemoveAndValidateFacetFactory extends MethodPrefixBasedFacetFactoryAbstract implements ObjectDirtierAware {

    private static final String[] PREFIXES = {};

    private ObjectDirtier objectDirtier;

    public CollectionAddRemoveAndValidateFacetFactory() {
        super(FeatureType.COLLECTIONS_ONLY, OrphanValidation.VALIDATE, PREFIXES);
    }

    @Override
    public void process(final ProcessMethodContext processMethodContext) {

        final Class<?> collectionType = attachAddToFacetAndRemoveFromFacet(processMethodContext);
        attachValidateAddToAndRemoveFromFacetIfMethodsFound(processMethodContext, collectionType);
    }

    private Class<?> attachAddToFacetAndRemoveFromFacet(final ProcessMethodContext processMethodContext) {

        final Method accessorMethod = processMethodContext.getMethod();
        final String capitalizedName = NameUtils.javaBaseName(accessorMethod.getName());

        final Class<?> cls = processMethodContext.getCls();

        // add
        final Method addToMethod = MethodFinderUtils.findMethod(cls, MethodScope.OBJECT, MethodPrefixConstants.ADD_TO_PREFIX + capitalizedName, void.class);
        processMethodContext.removeMethod(addToMethod);

        // remove
        final Method removeFromMethod = MethodFinderUtils.findMethod(cls, MethodScope.OBJECT, MethodPrefixConstants.REMOVE_FROM_PREFIX + capitalizedName, void.class);
        processMethodContext.removeMethod(removeFromMethod);

        // add facets
        final FacetHolder collection = processMethodContext.getFacetHolder();
        FacetUtil.addFacet(createAddToFacet(addToMethod, accessorMethod, collection));
        FacetUtil.addFacet(createRemoveFromFacet(removeFromMethod, accessorMethod, collection));

        // infer typ
        final Class<?> addToType = ((addToMethod == null || addToMethod.getParameterTypes().length != 1) ? null : addToMethod.getParameterTypes()[0]);
        final Class<?> removeFromType = ((removeFromMethod == null || removeFromMethod.getParameterTypes().length != 1) ? null : removeFromMethod.getParameterTypes()[0]);

        return inferTypeOfIfPossible(accessorMethod, addToType, removeFromType, collection);
    }

    /**
     * TODO need to distinguish between Java collections, arrays and other
     * collections!
     */
    private CollectionAddToFacet createAddToFacet(final Method addToMethodIfAny, final Method accessorMethod, final FacetHolder holder) {
        if (addToMethodIfAny != null) {
            return new CollectionAddToFacetViaMethod(addToMethodIfAny, holder);
        } else {
            return new CollectionAddToFacetViaAccessor(accessorMethod, holder, getObjectDirtier());
        }
    }

    /**
     * TODO need to distinguish between Java collections, arrays and other
     * collections!
     */
    private CollectionRemoveFromFacet createRemoveFromFacet(final Method removeFromMethodIfAny, final Method accessorMethod, final FacetHolder holder) {
        if (removeFromMethodIfAny != null) {
            return new CollectionRemoveFromFacetViaMethod(removeFromMethodIfAny, holder);
        } else {
            return new CollectionRemoveFromFacetViaAccessor(accessorMethod, holder, getObjectDirtier());
        }
    }

    private Class<?> inferTypeOfIfPossible(final Method getMethod, final Class<?> addType, final Class<?> removeType, final FacetHolder collection) {

        if (addType != null && removeType != null && addType != removeType) {
            throw new MetaModelException("The addTo/removeFrom methods for " + getMethod.getDeclaringClass() + " must " + "both deal with same type of object: " + addType + "; " + removeType);
        }

        final Class<?> type = addType != null ? addType : removeType;
        if (type != null) {
            FacetUtil.addFacet(new TypeOfFacetInferredFromSupportingMethods(type, collection, getSpecificationLoader()));
        }
        return type;
    }

    private void attachValidateAddToAndRemoveFromFacetIfMethodsFound(final ProcessMethodContext processMethodContext, final Class<?> collectionType) {
        attachValidateAddToFacetIfValidateAddToMethodIsFound(processMethodContext, collectionType);
        attachValidateRemoveFacetIfValidateRemoveFromMethodIsFound(processMethodContext, collectionType);
    }

    private void attachValidateAddToFacetIfValidateAddToMethodIsFound(final ProcessMethodContext processMethodContext, final Class<?> collectionType) {

        final Method getMethod = processMethodContext.getMethod();
        final String capitalizedName = NameUtils.javaBaseName(getMethod.getName());

        final Class<?> cls = processMethodContext.getCls();
        final Class<?>[] paramTypes = MethodFinderUtils.paramTypesOrNull(collectionType);
        Method validateAddToMethod = MethodFinderUtils.findMethod(cls, MethodScope.OBJECT, MethodPrefixConstants.VALIDATE_ADD_TO_PREFIX + capitalizedName, String.class, paramTypes);
        if (validateAddToMethod == null) {
            validateAddToMethod = MethodFinderUtils.findMethod(cls, MethodScope.OBJECT, MethodPrefixConstants.VALIDATE_ADD_TO_PREFIX_2 + capitalizedName, String.class, MethodFinderUtils.paramTypesOrNull(collectionType));
        }
        if (validateAddToMethod == null) {
            return;
        }
        processMethodContext.removeMethod(validateAddToMethod);

        final FacetHolder collection = processMethodContext.getFacetHolder();
        FacetUtil.addFacet(new CollectionValidateAddToFacetViaMethod(validateAddToMethod, collection));
    }

    private void attachValidateRemoveFacetIfValidateRemoveFromMethodIsFound(final ProcessMethodContext processMethodContext, final Class<?> collectionType) {

        final Method getMethod = processMethodContext.getMethod();
        final String capitalizedName = NameUtils.javaBaseName(getMethod.getName());

        final Class<?> cls = processMethodContext.getCls();
        final Class<?>[] paramTypes = MethodFinderUtils.paramTypesOrNull(collectionType);
        Method validateRemoveFromMethod = MethodFinderUtils.findMethod(cls, MethodScope.OBJECT, MethodPrefixConstants.VALIDATE_REMOVE_FROM_PREFIX + capitalizedName, String.class, paramTypes);
        if (validateRemoveFromMethod == null) {
            validateRemoveFromMethod = MethodFinderUtils.findMethod(cls, MethodScope.OBJECT, MethodPrefixConstants.VALIDATE_REMOVE_FROM_PREFIX_2 + capitalizedName, String.class, MethodFinderUtils.paramTypesOrNull(collectionType));
        }
        if (validateRemoveFromMethod == null) {
            return;
        }
        processMethodContext.removeMethod(validateRemoveFromMethod);

        final FacetHolder collection = processMethodContext.getFacetHolder();
        FacetUtil.addFacet(new CollectionValidateRemoveFromFacetViaMethod(validateRemoveFromMethod, collection));
    }

    // ///////////////////////////////////////////////////////
    // Dependencies (injected)
    // ///////////////////////////////////////////////////////

    protected ObjectDirtier getObjectDirtier() {
        return objectDirtier;
    }

    @Override
    public void setObjectDirtier(final ObjectDirtier objectDirtier) {
        this.objectDirtier = objectDirtier;
    }

}
