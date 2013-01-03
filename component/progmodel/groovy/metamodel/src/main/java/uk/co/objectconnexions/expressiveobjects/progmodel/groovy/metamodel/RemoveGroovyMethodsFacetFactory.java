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
package uk.co.objectconnexions.expressiveobjects.progmodel.groovy.metamodel;

import java.lang.reflect.Method;

import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfigurationAware;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FeatureType;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.MethodRemover;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetFactoryAbstract;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.methodutils.MethodScope;

public class RemoveGroovyMethodsFacetFactory extends FacetFactoryAbstract implements ExpressiveObjectsConfigurationAware {

    private static final String DEPTH_KEY = "expressive-objects.groovy.depth";
    private static final int DEPTH_DEFAULT = 5;

    private ExpressiveObjectsConfiguration configuration;

    public RemoveGroovyMethodsFacetFactory() {
        super(FeatureType.OBJECTS_ONLY);
    }

    static class MethodSpec {
        static class Builder {

            private final MethodSpec methodSpec = new MethodSpec();

            public Builder param(final Class<?>... paramTypes) {
                methodSpec.parameterTypes = paramTypes;
                return this;
            }

            public Builder ret(final Class<?> returnType) {
                methodSpec.returnType = returnType;
                return this;
            }

            public MethodSpec build() {
                return methodSpec;
            }

            public void remove(final MethodRemover remover) {
                build().removeMethod(remover);
            }
        }

        static Builder specFor(final String methodName) {
            final Builder builder = new Builder();
            builder.methodSpec.methodName = methodName;
            return builder;
        }

        static Builder specFor(final String formatStr, final Object... args) {
            return specFor(String.format(formatStr, args));
        }

        private String methodName;
        private Class<?> returnType = void.class;
        private Class<?>[] parameterTypes = new Class[0];

        void removeMethod(final MethodRemover methodRemover) {
            methodRemover.removeMethod(MethodScope.OBJECT, methodName, returnType, parameterTypes);
        }
    }

    @Override
    public void process(final ProcessClassContext processClassContext) {
        MethodSpec.specFor("invokeMethod").param(String.class, Object.class).ret(Object.class).remove(processClassContext);
        MethodSpec.specFor("getMetaClass").ret(groovy.lang.MetaClass.class).remove(processClassContext);
        MethodSpec.specFor("setMetaClass").param(groovy.lang.MetaClass.class).remove(processClassContext);
        MethodSpec.specFor("getProperty").param(String.class).ret(Object.class).remove(processClassContext);

        final int depth = determineDepth();
        for (int i = 1; i < depth; i++) {
            MethodSpec.specFor("this$dist$invoke$%d", i).param(String.class, Object.class).ret(Object.class).remove(processClassContext);
            MethodSpec.specFor("this$dist$set$%d", i).param(String.class, Object.class).remove(processClassContext);
            MethodSpec.specFor("this$dist$get$%d", i).param(String.class).ret(Object.class).remove(processClassContext);
        }
        final Method[] methods = processClassContext.getCls().getMethods();
        for (final Method method : methods) {
            if (method.getName().startsWith("super$")) {
                processClassContext.removeMethod(method);
            }
        }
    }

    private int determineDepth() {
        final int depth = configuration.getInteger(DEPTH_KEY, DEPTH_DEFAULT);
        return depth;
    }

    @Override
    public void setConfiguration(final ExpressiveObjectsConfiguration configuration) {
        this.configuration = configuration;
    }

}
