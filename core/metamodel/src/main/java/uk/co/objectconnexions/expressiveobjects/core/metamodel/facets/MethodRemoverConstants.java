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

package uk.co.objectconnexions.expressiveobjects.core.metamodel.facets;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.MethodRemover;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.methodutils.MethodScope;

public class MethodRemoverConstants {

    public static MethodRemover NULL = new MethodRemover() {
        @Override
        public List<Method> removeMethods(final MethodScope methodScope, final String prefix, final Class<?> returnType, final boolean canBeVoid, final int paramCount) {
            return new ArrayList<Method>();
        }

        @Override
        public void removeMethod(final MethodScope methodScope, final String methodName, final Class<?> returnType, final Class<?>[] parameterTypes) {
        }

        @Override
        public void removeMethod(final Method method) {
        }

        @Override
        public void removeMethods(final List<Method> methods) {
        }
    };
}
