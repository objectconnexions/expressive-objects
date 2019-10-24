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

package uk.co.objectconnexions.expressiveobjects.core.bytecode.cglib;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import uk.co.objectconnexions.expressiveobjects.core.commons.lang.ArrayUtils;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.ImperativeFacetUtils;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.ImperativeFacetUtils.ImperativeFacetFlags;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.SpecificationLoaderSpi;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectMember;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.classsubstitutor.CglibEnhanced;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.specimpl.dflt.ObjectSpecificationDefault;
import uk.co.objectconnexions.expressiveobjects.core.runtime.bytecode.ObjectResolveAndObjectChangedEnhancerAbstract;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectfactory.ObjectChanger;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectfactory.ObjectResolver;

public class ObjectResolveAndObjectChangedEnhancer extends ObjectResolveAndObjectChangedEnhancerAbstract {

    private Callback callback;

    /**
     * Cache of Enhancers, lazy populated.
     */
    private final Map<Class<?>, Enhancer> enhancerByClass = new HashMap<Class<?>, Enhancer>();

    public ObjectResolveAndObjectChangedEnhancer(final ObjectResolver objectResolver, final ObjectChanger objectChanger, final SpecificationLoaderSpi specificationLoader) {
        super(objectResolver, objectChanger, specificationLoader);

        createCallback();
    }

    @Override
    protected void createCallback() {
        this.callback = new MethodInterceptor() {

            @Override
            public Object intercept(final Object proxied, final Method proxiedMethod, final Object[] args, final MethodProxy proxyMethod) throws Throwable {

                final boolean ignore = proxiedMethod.getDeclaringClass().equals(Object.class);
                ImperativeFacetFlags flags = null;

                if (!ignore) {
                    final ObjectSpecificationDefault targetObjSpec = getJavaSpecificationOfOwningClass(proxiedMethod);
                    final ObjectMember member = targetObjSpec.getMember(proxiedMethod);

                    flags = ImperativeFacetUtils.getImperativeFacetFlags(member, proxiedMethod);

                    if (flags.impliesResolve()) {
                        objectResolver.resolve(proxied, member.getName());
                    }
                }

                final Object proxiedReturn = proxyMethod.invokeSuper(proxied, args);

                if (!ignore && flags.impliesObjectChanged()) {
                    objectChanger.objectChanged(proxied);
                }

                return proxiedReturn;
            }

        };
    }

    @SuppressWarnings("unchecked")
    public <T> T newInstance(final Class<T> cls) {
        final Enhancer enhancer = lookupOrCreateEnhancerFor(cls);
        return (T) enhancer.create();
    }

    private Enhancer lookupOrCreateEnhancerFor(final Class<?> cls) {
        Enhancer enhancer = enhancerByClass.get(cls);
        if (enhancer == null) {
            enhancer = new Enhancer();
            enhancer.setSuperclass(cls);
            enhancer.setInterfaces(ArrayUtils.combine(cls.getInterfaces(), new Class<?>[] { CglibEnhanced.class }));
            enhancer.setCallback(callback);
            enhancerByClass.put(cls, enhancer);
        }
        return enhancer;
    }

}
