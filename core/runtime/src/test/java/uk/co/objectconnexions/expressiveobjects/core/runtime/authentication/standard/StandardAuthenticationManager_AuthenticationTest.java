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

package uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.standard;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSession;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.commons.matchers.ExpressiveObjectsMatchers;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.AuthenticationRequest;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.AuthenticationRequestPassword;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.standard.AuthenticationManagerStandard;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.standard.Authenticator;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.standard.RandomCodeGenerator;

@RunWith(JMock.class)
public class StandardAuthenticationManager_AuthenticationTest {

    private final Mockery mockery = new JUnit4Mockery();

    private AuthenticationManagerStandard authenticationManager;

    private ExpressiveObjectsConfiguration mockConfiguration;
    private RandomCodeGenerator mockRandomCodeGenerator;
    private Authenticator mockAuthenticator;
    private AuthenticationSession mockAuthSession;

    @Before
    public void setUp() throws Exception {
        mockConfiguration = mockery.mock(ExpressiveObjectsConfiguration.class);
        mockRandomCodeGenerator = mockery.mock(RandomCodeGenerator.class);
        mockAuthenticator = mockery.mock(Authenticator.class);
        mockAuthSession = mockery.mock(AuthenticationSession.class);

        authenticationManager = new AuthenticationManagerStandard(mockConfiguration);
        authenticationManager.addAuthenticator(mockAuthenticator);
        authenticationManager.setRandomCodeGenerator(mockRandomCodeGenerator);

        mockery.checking(new Expectations() {
            {
                allowing(mockAuthenticator).canAuthenticate(with(ExpressiveObjectsMatchers.anySubclassOf(AuthenticationRequest.class)));
                will(returnValue(true));

                allowing(mockAuthenticator).authenticate(with(any(AuthenticationRequest.class)), with(any(String.class)));
                will(returnValue(mockAuthSession));

                allowing(mockRandomCodeGenerator).generateRandomCode();
                will(returnValue("123456"));

                allowing(mockAuthSession).getValidationCode();
                will(returnValue("123456"));

                allowing(mockAuthSession).hasUserNameOf("foo");
                will(returnValue(true));

                allowing(mockAuthSession).getUserName();
                will(returnValue("foo"));
            }
        });
    }

    @Test
    public void newlyCreatedAuthenticationSessionShouldBeValid() throws Exception {
        final AuthenticationRequestPassword request = new AuthenticationRequestPassword("foo", "bar");
        final AuthenticationSession session = authenticationManager.authenticate(request);

        assertThat(authenticationManager.isSessionValid(session), is(true));
    }

}
