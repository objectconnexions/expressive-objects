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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class JavaClassUtilsTest_getBuiltIn {

    @Test
    public void voidBuiltIns() throws ClassNotFoundException {
        assertEquals(JavaClassUtils.getBuiltIn("void"), void.class);
        assertEquals(JavaClassUtils.getBuiltIn("boolean"), boolean.class);
        assertEquals(JavaClassUtils.getBuiltIn("byte"), byte.class);
        assertEquals(JavaClassUtils.getBuiltIn("short"), short.class);
        assertEquals(JavaClassUtils.getBuiltIn("int"), int.class);
        assertEquals(JavaClassUtils.getBuiltIn("long"), long.class);
        assertEquals(JavaClassUtils.getBuiltIn("char"), char.class);
        assertEquals(JavaClassUtils.getBuiltIn("float"), float.class);
        assertEquals(JavaClassUtils.getBuiltIn("double"), double.class);
    }

}
