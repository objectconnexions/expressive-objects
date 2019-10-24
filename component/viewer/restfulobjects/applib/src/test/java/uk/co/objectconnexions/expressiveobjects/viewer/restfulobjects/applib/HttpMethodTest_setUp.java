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

import java.io.UnsupportedEncodingException;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmock.auto.Mock;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.ClientRequestConfigurer;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.HttpMethod;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.JsonRepresentation;

public class HttpMethodTest_setUp {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    private ClientRequestConfigurer requestConfigurer;

    private JsonRepresentation repr;

    @Before
    public void setUp() throws Exception {
        repr = JsonRepresentation.newMap();
        repr.mapPut("aString", "bar");
        repr.mapPut("anInt", 3);
        repr.mapPut("aLong", 31231231L);
        repr.mapPut("aBoolean", true);
        repr.mapPut("aStringRequiringEncoding", "http://localhost:8080/somewhere");
    }

    @Test
    public void get() throws Exception {
        setsUpQueryString(HttpMethod.GET);
    }

    @Test
    public void delete() throws Exception {
        setsUpQueryString(HttpMethod.DELETE);
    }

    @Test
    public void post() throws Exception {
        setsUpBody(HttpMethod.POST);
    }

    @Test
    public void put() throws Exception {
        setsUpBody(HttpMethod.PUT);
    }

    private void setsUpQueryString(final HttpMethod httpMethod) throws UnsupportedEncodingException {
        context.checking(new Expectations() {
            {
                one(requestConfigurer).setHttpMethod(httpMethod);
                one(requestConfigurer).queryString(repr);
            }
        });

        httpMethod.setUpArgs(requestConfigurer, repr);
    }

    private void setsUpBody(final HttpMethod httpMethod) throws UnsupportedEncodingException {
        context.checking(new Expectations() {
            {
                one(requestConfigurer).setHttpMethod(httpMethod);
                one(requestConfigurer).body(repr);
            }
        });

        httpMethod.setUpArgs(requestConfigurer, repr);
    }

}
