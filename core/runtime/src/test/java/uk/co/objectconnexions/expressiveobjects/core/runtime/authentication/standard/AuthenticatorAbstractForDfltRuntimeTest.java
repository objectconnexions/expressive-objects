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

import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.AuthenticationRequest;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.AuthenticatorAbstractForDfltRuntime;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.DeploymentType;

@RunWith(JMock.class)
public class AuthenticatorAbstractForDfltRuntimeTest {

    private final Mockery mockery = new JUnit4Mockery();

    private ExpressiveObjectsConfiguration mockConfiguration;
    private AuthenticatorAbstractForDfltRuntime authenticator;

    @Before
    public void setUp() {
        mockConfiguration = mockery.mock(ExpressiveObjectsConfiguration.class);

        authenticator = new AuthenticatorAbstractForDfltRuntime(mockConfiguration) {

            @Override
            public boolean isValid(final AuthenticationRequest request) {
                return false;
            }

            @Override
            public boolean canAuthenticate(final Class<? extends AuthenticationRequest> authenticationRequestClass) {
                return false;
            }
        };
    }

    @Test
    public void getConfiguration() throws Exception {
        assertThat(authenticator.getConfiguration(), is(mockConfiguration));
    }

    @Test
    public void getDeploymentTypeForExploration() throws Exception {
        final DeploymentType deploymentType = DeploymentType.EXPLORATION;
        mockery.checking(new Expectations() {
            {
                allowing(mockConfiguration).getString("expressive-objects.deploymentType");
                will(returnValue(deploymentType.name()));
            }
        });
        assertThat(authenticator.getDeploymentType(), is(deploymentType));
    }

    @Test
    public void getDeploymentTypeForPrototype() throws Exception {
        final DeploymentType deploymentType = DeploymentType.PROTOTYPE;
        mockery.checking(new Expectations() {
            {
                allowing(mockConfiguration).getString("expressive-objects.deploymentType");
                will(returnValue(deploymentType.name()));
            }
        });
        assertThat(authenticator.getDeploymentType(), is(deploymentType));
    }

    @Test
    public void getDeploymentTypeForServer() throws Exception {
        final DeploymentType deploymentType = DeploymentType.SERVER;
        mockery.checking(new Expectations() {
            {
                allowing(mockConfiguration).getString("expressive-objects.deploymentType");
                will(returnValue(deploymentType.name()));
            }
        });
        assertThat(authenticator.getDeploymentType(), is(deploymentType));
    }

    @Test(expected = IllegalStateException.class)
    public void expectsThereToBeADeploymentTypeInExpressiveObjectsConfiguration() throws Exception {
        mockery.checking(new Expectations() {
            {
                allowing(mockConfiguration).getString("expressive-objects.deploymentType");
                will(returnValue(null));
            }
        });
        authenticator.getDeploymentType();
    }

    @Test(expected = IllegalArgumentException.class)
    public void expectsThereToBeAValidDeploymentTypeInExpressiveObjectsConfiguration() throws Exception {
        mockery.checking(new Expectations() {
            {
                allowing(mockConfiguration).getString("expressive-objects.deploymentType");
                will(returnValue("GARBAGE"));
            }
        });
        authenticator.getDeploymentType();
    }

}
