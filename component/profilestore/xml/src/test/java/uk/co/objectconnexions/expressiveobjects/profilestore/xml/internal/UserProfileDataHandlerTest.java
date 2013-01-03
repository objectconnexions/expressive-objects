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

package uk.co.objectconnexions.expressiveobjects.profilestore.xml.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfigurationDefault;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.OidMarshaller;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.SpecificationLoaderSpi;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.AuthenticationManager;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authorization.AuthorizationManager;
import uk.co.objectconnexions.expressiveobjects.core.runtime.imageloader.TemplateImageLoader;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.DeploymentType;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContextStatic;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.PersistenceSessionFactory;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.session.ExpressiveObjectsSessionFactory;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.session.ExpressiveObjectsSessionFactoryDefault;
import uk.co.objectconnexions.expressiveobjects.core.runtime.userprofile.Options;
import uk.co.objectconnexions.expressiveobjects.core.runtime.userprofile.UserProfile;
import uk.co.objectconnexions.expressiveobjects.core.runtime.userprofile.UserProfileLoader;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmock.auto.Mock;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;
import uk.co.objectconnexions.expressiveobjects.profilestore.xml.internal.UserProfileDataHandler;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class UserProfileDataHandlerTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);
    
    @Mock
    private TemplateImageLoader mockTemplateImageLoader;
    @Mock
    private SpecificationLoaderSpi mockSpecificationLoader;
    @Mock
    private AuthenticationManager mockAuthenticationManager;
    @Mock
    private AuthorizationManager mockAuthorizationManager;
    @Mock
    private UserProfileLoader mockUserProfileLoader;
    @Mock
    private PersistenceSessionFactory mockPersistenceSessionFactory;

    private TestServiceObject1 service;
    private UserProfile profile;

    
    @Before
    public void setup() throws Exception {
        Logger.getRootLogger().setLevel(Level.OFF);
        service = new TestServiceObject1();
        final ExpressiveObjectsSessionFactory executionContextFactory = new ExpressiveObjectsSessionFactoryDefault(DeploymentType.EXPLORATION, new ExpressiveObjectsConfigurationDefault(), mockTemplateImageLoader, mockSpecificationLoader, mockAuthenticationManager,
                mockAuthorizationManager, mockUserProfileLoader, mockPersistenceSessionFactory, Arrays.<Object>asList(service), new OidMarshaller());

        ExpressiveObjectsContextStatic.createRelaxedInstance(executionContextFactory);

        final XMLReader parser = XMLReaderFactory.createXMLReader();
        final UserProfileDataHandler handler = new UserProfileDataHandler();
        parser.setContentHandler(handler);
        parser.parse(new InputSource(new InputStreamReader(new FileInputStream("test.xml"))));

        profile = handler.getUserProfile();
    }

    @Test
    public void stringOption() throws Exception {
        assertEquals("on", profile.getOptions().getString("power"));
    }

    @Test
    public void unknownOptionReturnsNull() throws Exception {
        assertEquals(null, profile.getOptions().getString("device"));
    }

    @Test
    public void integerOption() throws Exception {
        assertEquals(50, profile.getOptions().getInteger("height", 10));
    }

    @Test
    public void unknownIntegerReturnsDefault() throws Exception {
        assertEquals(30, profile.getOptions().getInteger("width", 30));
    }

    @Test
    public void unknownOptionsCreated() throws Exception {
        final Options options = profile.getOptions().getOptions("");
        assertNotNull(options);
        assertEquals(false, options.names().hasNext());
    }

    @Test
    public void containedOptions() throws Exception {
        final Options options = profile.getOptions().getOptions("opts");
        assertNotNull(options);
        assertEquals("value1", options.getString("option1"));
        assertEquals("value2", options.getString("option2"));
    }

    @Test
    public void recursiveOptions() throws Exception {
        Options options = profile.getOptions().getOptions("opts");
        options = options.getOptions("options3");
        assertEquals("value4", options.getString("option4"));
        assertEquals("value5", options.getString("option5"));
    }

    @Test
    public void profileNames() throws Exception {
        final List<String> list = profile.list();
        assertEquals(2, list.size());
        assertEquals("Library", list.get(0));
        assertEquals("Admin", list.get(1));
    }

    @Test
    public void perspective() throws Exception {
        assertEquals("Admin", profile.getPerspective().getName());
        assertEquals(1, profile.getPerspective().getServices().size());
        assertEquals(service, profile.getPerspective().getServices().get(0));
    }
}
