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

package uk.co.objectconnexions.expressiveobjects.core.bytecode.javassist;

import java.lang.reflect.Method;

import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;

import uk.co.objectconnexions.expressiveobjects.core.commons.exceptions.ExpressiveObjectsException;
import uk.co.objectconnexions.expressiveobjects.core.commons.lang.ArrayUtils;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.ImperativeFacetUtils;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.ImperativeFacetUtils.ImperativeFacetFlags;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.SpecificationLoaderSpi;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectMember;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.specimpl.dflt.ObjectSpecificationDefault;
import uk.co.objectconnexions.expressiveobjects.core.runtime.bytecode.ObjectResolveAndObjectChangedEnhancerAbstract;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectfactory.ObjectChanger;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectfactory.ObjectResolver;

public class ObjectResolveAndObjectChangedEnhancer extends ObjectResolveAndObjectChangedEnhancerAbstract {

    private MethodHandler methodHandler;

    public ObjectResolveAndObjectChangedEnhancer(final ObjectResolver objectResolver, final ObjectChanger objectChanger, final SpecificationLoaderSpi specificationLoader) {
        super(objectResolver, objectChanger, specificationLoader);

        createCallback();
    }

    @Override
    protected void createCallback() {
        this.methodHandler = new MethodHandler() {
            @Override
            public Object invoke(final Object proxied, final Method proxyMethod, final Method proxiedMethod, final Object[] args) throws Throwable {

                final boolean ignore = proxyMethod.getDeclaringClass().equals(Object.class);
                ImperativeFacetFlags flags = null;

                if (!ignore) {
                    final ObjectSpecificationDefault targetObjSpec = getJavaSpecificationOfOwningClass(proxiedMethod);

                    final ObjectMember member = targetObjSpec.getMember(proxiedMethod);
                    flags = ImperativeFacetUtils.getImperativeFacetFlags(member, proxiedMethod);

                    if (flags.impliesResolve()) {
                        objectResolver.resolve(proxied, member.getName());
                    }
                }

                final Object proxiedReturn = proxiedMethod.invoke(proxied, args); // execute
                                                                                  // the
                                                                                  // original
                                                                                  // method.

                if (!ignore && flags.impliesObjectChanged()) {
                    objectChanger.objectChanged(proxied);
                }

                return proxiedReturn;
            }
        };
    }

    @SuppressWarnings("unchecked")
    public <T> T newInstance(final Class<T> cls) {

        final ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setSuperclass(cls);
        proxyFactory.setInterfaces(ArrayUtils.combine(cls.getInterfaces(), new Class<?>[] { JavassistEnhanced.class }));

        proxyFactory.setFilter(new MethodFilter() {
            @Override
            public boolean isHandled(final Method m) {
                // ignore finalize()
                return !m.getName().equals("finalize");
            }
        });

        final Class<T> proxySubclass = proxyFactory.createClass();
        try {
            final T newInstance = proxySubclass.newInstance();
            final ProxyObject proxyObject = (ProxyObject) newInstance;
            proxyObject.setHandler(methodHandler);

            return newInstance;
        } catch (final InstantiationException e) {
            throw new ExpressiveObjectsException(e);
        } catch (final IllegalAccessException e) {
            throw new ExpressiveObjectsException(e);
        }
    }

}
