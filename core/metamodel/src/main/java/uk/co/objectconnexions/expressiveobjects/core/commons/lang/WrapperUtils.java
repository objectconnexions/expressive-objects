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

package uk.co.objectconnexions.expressiveobjects.core.commons.lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class WrapperUtils {

    private WrapperUtils() {
    }

    private static Map<Class<?>, Class<?>> wrapperClasses = new HashMap<Class<?>, Class<?>>();

    static {
        wrapperClasses.put(boolean.class, Boolean.class);
        wrapperClasses.put(byte.class, Byte.class);
        wrapperClasses.put(char.class, Character.class);
        wrapperClasses.put(short.class, Short.class);
        wrapperClasses.put(int.class, Integer.class);
        wrapperClasses.put(long.class, Long.class);
        wrapperClasses.put(float.class, Float.class);
        wrapperClasses.put(double.class, Double.class);
    }

    public static Class<?> wrap(final Class<?> primitiveClass) {
        return wrapperClasses.get(primitiveClass);
    }

    public static Class<?>[] wrapAsNecessary(final Class<?>[] classes) {
        final List<Class<?>> wrappedClasses = new ArrayList<Class<?>>();
        for (final Class<?> cls : classes) {
            if (cls.isPrimitive()) {
                wrappedClasses.add(wrap(cls));
            } else {
                wrappedClasses.add(cls);
            }
        }
        return wrappedClasses.toArray(new Class[] {});
    }

}
