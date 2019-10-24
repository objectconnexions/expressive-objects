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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.param.choices.method;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import uk.co.objectconnexions.expressiveobjects.core.commons.lang.ArrayUtil;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.mgr.AdapterManager;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.util.AdapterInvokeUtils;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.ImperativeFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.actions.choices.ActionChoicesFacetAbstract;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.DomainModelException;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.SpecificationLoader;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.CollectionUtils;

public class ActionChoicesFacetViaMethod extends ActionChoicesFacetAbstract implements ImperativeFacet {

    private final Method method;
    private final Class<?> choicesType;
    private final SpecificationLoader specificationLookup;
    private final AdapterManager adapterMap;

    public ActionChoicesFacetViaMethod(final Method method, final Class<?> choicesType, final FacetHolder holder, final SpecificationLoader specificationLookup, final AdapterManager adapterManager) {
        super(holder);
        this.method = method;
        this.choicesType = choicesType;
        this.specificationLookup = specificationLookup;
        this.adapterMap = adapterManager;
    }

    /**
     * Returns a singleton list of the {@link Method} provided in the
     * constructor.
     */
    @Override
    public List<Method> getMethods() {
        return Collections.singletonList(method);
    }

    @Override
    public boolean impliesResolve() {
        return true;
    }

    @Override
    public boolean impliesObjectChanged() {
        return false;
    }

    @Override
    public Object[][] getChoices(final ObjectAdapter owningAdapter) {
        final Object invoke = AdapterInvokeUtils.invoke(method, owningAdapter);
        if (!(invoke instanceof Object[])) {
            throw new DomainModelException("Expected an array of collections (Object[]) containing choices for all parameters, but got " + invoke + " instead. Perhaps the parameter number is missing!");
        }
        final Object[] options = (Object[]) invoke;
        final Object[][] results = new Object[options.length][];
        for (int i = 0; i < results.length; i++) {
            if (options[i] == null) {
                results[i] = null;
            } else if (options[i].getClass().isArray()) {
                results[i] = ArrayUtil.getObjectAsObjectArray(options[i]);
            } else {
                final ObjectSpecification specification = getSpecificationLookup().loadSpecification(choicesType);
                results[i] = CollectionUtils.getCollectionAsObjectArray(options[i], specification, getAdapterMap());
            }
        }
        return results;
    }

    @Override
    protected String toStringValues() {
        return "method=" + method + ",type=" + choicesType;
    }

    // ///////////////////////////////////////////////////////
    // Dependencies
    // ///////////////////////////////////////////////////////

    private SpecificationLoader getSpecificationLookup() {
        return specificationLookup;
    }

    private AdapterManager getAdapterMap() {
        return adapterMap;
    }

}
