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

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSession;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.exploration.AuthenticationRequestExploration;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.exploration.ExplorationAuthenticator;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.exploration.ExplorationAuthenticatorConstants;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.exploration.ExplorationSession;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.exploration.MultiUserExplorationSession;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.standard.SimpleSession;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.DeploymentType;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.SystemConstants;

@RunWith(JMock.class)
public class MultiUserExplorationSessionTest {

    private final Mockery mockery = new JUnit4Mockery();

    private MultiUserExplorationSession session;
    private ExpressiveObjectsConfiguration mockConfiguration;
    private ExplorationAuthenticator authenticator;

    @Before
    public void setUp() {
        mockConfiguration = mockery.mock(ExpressiveObjectsConfiguration.class);
        mockery.checking(new Expectations() {
            {
                allowing(mockConfiguration).getString(SystemConstants.DEPLOYMENT_TYPE_KEY);
                will(returnValue(DeploymentType.EXPLORATION.name()));
            }
        });
    }

    @Test
    public void testNameDefaultsToFirstUser() throws Exception {
        mockery.checking(new Expectations() {
            {
                allowing(mockConfiguration).getString(ExplorationAuthenticatorConstants.USERS);
                will(returnValue("fred, sven:admin|sales|marketing, bob:sales, dick"));
            }
        });
        authenticator = new ExplorationAuthenticator(mockConfiguration);
        final AuthenticationSession session = authenticator.authenticate(new AuthenticationRequestExploration(), "");

        Assert.assertEquals("fred", session.getUserName());
    }

    @Test
    public void testValidateCode() throws Exception {
        mockery.checking(new Expectations() {
            {
                allowing(mockConfiguration).getString(ExplorationAuthenticatorConstants.USERS);
                will(returnValue("fred, sven:admin|sales|marketing, bob:sales, dick"));
            }
        });
        authenticator = new ExplorationAuthenticator(mockConfiguration);
        final AuthenticationSession session = authenticator.authenticate(new AuthenticationRequestExploration(), "xxx");

        Assert.assertEquals("xxx", session.getValidationCode());
    }

    @Test
    public void testNoRolesSpecifiedForFirstUser() throws Exception {
        mockery.checking(new Expectations() {
            {
                allowing(mockConfiguration).getString(ExplorationAuthenticatorConstants.USERS);
                will(returnValue("fred, sven:admin|sales|marketing, bob:sales, dick"));
            }
        });
        authenticator = new ExplorationAuthenticator(mockConfiguration);
        final AuthenticationSession session = authenticator.authenticate(new AuthenticationRequestExploration(), "");

        Assert.assertEquals(0, session.getRoles().size());
    }

    @Test
    public void testForMultipleUser() throws Exception {
        mockery.checking(new Expectations() {
            {
                allowing(mockConfiguration).getString(ExplorationAuthenticatorConstants.USERS);
                will(returnValue("fred, sven:admin|sales|marketing, bob:sales, dick"));
            }
        });
        authenticator = new ExplorationAuthenticator(mockConfiguration);
        final AuthenticationSession authSession = authenticator.authenticate(new AuthenticationRequestExploration(), "");

        assertThat(authSession, is(instanceOf(MultiUserExplorationSession.class)));

        assertThat(authSession.getUserName(), is(equalTo("fred")));
    }

    @Test
    public void testForSingleUser() throws Exception {
        mockery.checking(new Expectations() {
            {
                allowing(mockConfiguration).getString(ExplorationAuthenticatorConstants.USERS);
                will(returnValue("sven"));
            }
        });
        authenticator = new ExplorationAuthenticator(mockConfiguration);
        final AuthenticationSession authSession = authenticator.authenticate(new AuthenticationRequestExploration(), "");
        assertThat(authSession, is(instanceOf(SimpleSession.class)));

        assertThat(authSession.getUserName(), is(equalTo("sven")));
    }

    @Test
    public void testNoUsersSpecified() throws Exception {
        mockery.checking(new Expectations() {
            {
                allowing(mockConfiguration).getString(ExplorationAuthenticatorConstants.USERS);
                will(returnValue(null));
            }
        });
        authenticator = new ExplorationAuthenticator(mockConfiguration);

        final AuthenticationSession authSession = authenticator.authenticate(new AuthenticationRequestExploration(), "");
        assertThat(authSession, is(instanceOf(ExplorationSession.class)));
    }

    @Test
    public void testOtherUsers() throws Exception {
        mockery.checking(new Expectations() {
            {
                allowing(mockConfiguration).getString(ExplorationAuthenticatorConstants.USERS);
                will(returnValue("fred, sven:admin|sales|marketing, bob:sales, dick"));
            }
        });
        authenticator = new ExplorationAuthenticator(mockConfiguration);
        this.session = (MultiUserExplorationSession) authenticator.authenticate(new AuthenticationRequestExploration(), "");

        final Set<String> availableSessions = session.getUserNames();
        Assert.assertEquals(4, availableSessions.size());
        Assert.assertTrue(availableSessions.contains("fred"));
        Assert.assertTrue(availableSessions.contains("sven"));
        Assert.assertTrue(availableSessions.contains("bob"));
        Assert.assertTrue(availableSessions.contains("dick"));
    }

    @Test
    public void testChangeUser() throws Exception {
        mockery.checking(new Expectations() {
            {
                allowing(mockConfiguration).getString(ExplorationAuthenticatorConstants.USERS);
                will(returnValue("fred, sven:admin|sales|marketing, bob:sales, dick"));
            }
        });
        authenticator = new ExplorationAuthenticator(mockConfiguration);
        this.session = (MultiUserExplorationSession) authenticator.authenticate(new AuthenticationRequestExploration(), "");

        session.setCurrentSession("bob");
        Assert.assertEquals("bob", session.getUserName());
    }

    @Test
    public void testRolesExist() throws Exception {
        mockery.checking(new Expectations() {
            {
                allowing(mockConfiguration).getString(ExplorationAuthenticatorConstants.USERS);
                will(returnValue("fred, sven:admin|sales|marketing, bob:sales, dick"));
            }
        });
        authenticator = new ExplorationAuthenticator(mockConfiguration);
        this.session = (MultiUserExplorationSession) authenticator.authenticate(new AuthenticationRequestExploration(), "");

        session.setCurrentSession("sven");
        final List<String> roles = session.getRoles();
        Assert.assertEquals(3, roles.size());
        Assert.assertEquals("admin", roles.get(0));
        Assert.assertEquals("sales", roles.get(1));
        Assert.assertEquals("marketing", roles.get(2));
    }
}
