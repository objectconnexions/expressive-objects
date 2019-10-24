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
package uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import javax.servlet.http.HttpServletRequest;

import org.jmock.Expectations;
import org.junit.Rule;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmock.auto.Mock;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.JsonRepresentation;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.RepresentationType;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.RestfulRequest.RequestParameter;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.util.UrlEncodingUtils;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.ResourceContext;

public class ResourceContextTest_getArg {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private ResourceContext resourceContext;

    private String queryString;

    @Test
    public void whenArgExists() throws Exception {
        queryString = UrlEncodingUtils.urlEncode(JsonRepresentation.newMap("x-ro-page", "123").asJsonNode());

        context.checking(new Expectations() {
            {
                one(httpServletRequest).getQueryString();
                will(returnValue(queryString));
            }
        });
        resourceContext = new ResourceContext(null, null, null, null, httpServletRequest, null, null, null, null, null, null, null, null, null) {
            @Override
            void init(final RepresentationType representationType) {
                //
            }
        };
        final Integer arg = resourceContext.getArg(RequestParameter.PAGE);
        assertThat(arg, equalTo(123));
    }

    @Test
    public void whenArgDoesNotExist() throws Exception {
        queryString = UrlEncodingUtils.urlEncode(JsonRepresentation.newMap("xxx", "123").asJsonNode());

        context.checking(new Expectations() {
            {
                one(httpServletRequest).getQueryString();
                will(returnValue(queryString));
            }
        });
        resourceContext = new ResourceContext(null, null, null, null, httpServletRequest, null, null, null, null, null, null, null, null, null) {
            @Override
            void init(final RepresentationType representationType) {
                //
            }
        };
        final Integer arg = resourceContext.getArg(RequestParameter.PAGE);
        assertThat(arg, equalTo(RequestParameter.PAGE.getDefault()));
    }

}
