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

package uk.co.objectconnexions.expressiveobjects.core.runtime.context;

import java.util.Collections;
import java.util.List;

import org.jmock.Expectations;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSession;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfigurationDefault;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.OidMarshaller;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.SpecificationLoaderSpi;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.AuthenticationManager;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.standard.SimpleSession;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authorization.AuthorizationManager;
import uk.co.objectconnexions.expressiveobjects.core.runtime.imageloader.TemplateImageLoader;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.DeploymentType;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContextStatic;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.PersistenceSession;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.PersistenceSessionFactory;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.session.ExpressiveObjectsSessionFactory;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.session.ExpressiveObjectsSessionFactoryDefault;
import uk.co.objectconnexions.expressiveobjects.core.runtime.userprofile.UserProfileLoader;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmock.auto.Mock;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

public class ExpressiveObjectsContextTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);
    

    private ExpressiveObjectsConfiguration configuration;
    
    @Mock
    private PersistenceSession mockPersistenceSession;
    
    @Mock
    private SpecificationLoaderSpi mockSpecificationLoader;

    @Mock
    protected TemplateImageLoader mockTemplateImageLoader;
    @Mock
    protected PersistenceSessionFactory mockPersistenceSessionFactory;
    @Mock
    private UserProfileLoader mockUserProfileLoader;
    @Mock
    protected AuthenticationManager mockAuthenticationManager;
    @Mock
    protected AuthorizationManager mockAuthorizationManager;

    protected OidMarshaller oidMarshaller;

    private List<Object> servicesList;


    private AuthenticationSession authSession;


    private ExpressiveObjectsSessionFactory sessionFactory;

    @Before
    public void setUp() throws Exception {
        ExpressiveObjectsContext.testReset();

        servicesList = Collections.emptyList();

        configuration = new ExpressiveObjectsConfigurationDefault();
        
        oidMarshaller = new OidMarshaller();
        
        context.checking(new Expectations() {
            {
                allowing(mockPersistenceSessionFactory).createPersistenceSession();
                will(returnValue(mockPersistenceSession));
                
                ignoring(mockPersistenceSession);
                ignoring(mockSpecificationLoader);
                ignoring(mockPersistenceSessionFactory);
                ignoring(mockUserProfileLoader);
                ignoring(mockAuthenticationManager);
                ignoring(mockAuthorizationManager);
                ignoring(mockTemplateImageLoader);
            }
        });

        sessionFactory = new ExpressiveObjectsSessionFactoryDefault(DeploymentType.EXPLORATION, configuration, mockTemplateImageLoader, mockSpecificationLoader, mockAuthenticationManager, mockAuthorizationManager, mockUserProfileLoader, mockPersistenceSessionFactory, servicesList, oidMarshaller);
        authSession = new SimpleSession("tester", Collections.<String>emptyList());
        
        ExpressiveObjectsContext.setConfiguration(configuration);
    }
    
    @After
    public void tearDown() throws Exception {
        if(ExpressiveObjectsContext.inSession()) {
            ExpressiveObjectsContext.closeSession();
        }
    }
    
    @Test
    public void getConfiguration() {
        ExpressiveObjectsContextStatic.createRelaxedInstance(sessionFactory);
        Assert.assertEquals(configuration, ExpressiveObjectsContext.getConfiguration());
    }

    @Test
    public void openSession_getSpecificationLoader() {
        ExpressiveObjectsContextStatic.createRelaxedInstance(sessionFactory);
        ExpressiveObjectsContext.openSession(authSession);

        Assert.assertEquals(mockSpecificationLoader, ExpressiveObjectsContext.getSpecificationLoader());
    }

    @Test
    public void openSession_getAuthenticationLoader() {
        ExpressiveObjectsContextStatic.createRelaxedInstance(sessionFactory);
        ExpressiveObjectsContext.openSession(authSession);

        Assert.assertEquals(authSession, ExpressiveObjectsContext.getAuthenticationSession());
    }
    
    @Test
    public void openSession_getPersistenceSession() {
        ExpressiveObjectsContextStatic.createRelaxedInstance(sessionFactory);
        ExpressiveObjectsContext.openSession(authSession);

        Assert.assertSame(mockPersistenceSession, ExpressiveObjectsContext.getPersistenceSession());
    }


}
