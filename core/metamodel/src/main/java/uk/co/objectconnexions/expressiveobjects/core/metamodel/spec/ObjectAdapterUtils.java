/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package uk.co.objectconnexions.expressiveobjects.core.metamodel.spec;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;

public final class ObjectAdapterUtils {

    private ObjectAdapterUtils() {
    }

    public static Object unwrapObject(final ObjectAdapter adapter) {
        if (adapter == null) {
            return null;
        }
        return adapter.getObject();
    }

    public static String unwrapObjectAsString(final ObjectAdapter adapter) {
        final Object obj = unwrapObject(adapter);
        if (obj == null) {
            return null;
        }
        if (!(obj instanceof String)) {
            return null;
        }
        return (String) obj;
    }

}
