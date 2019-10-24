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
package uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.RepresentationType;

public class RepresentationTypeTest_getName_lookup {

    @Test
    public void selectedValues() {
        assertThat(RepresentationType.VERSION.getName(), is("version"));
        assertThat(RepresentationType.HOME_PAGE.getName(), is("homePage"));
        assertThat(RepresentationType.ACTION_PARAMETER_DESCRIPTION.getName(), is("actionParameterDescription"));
    }

    @Test
    public void roundtrip() {
        for (final RepresentationType repType : RepresentationType.values()) {
            final String name = repType.getName();
            final RepresentationType lookup = RepresentationType.lookup(name);
            assertSame(repType, lookup);
        }
    }

    @Test
    public void lookup_whenUnknown() {
        assertThat(RepresentationType.lookup("foobar"), is(RepresentationType.GENERIC));
    }

    @Test
    public void lookup_whenNull() {
        assertThat(RepresentationType.lookup((String) null), is(RepresentationType.GENERIC));
    }

}
