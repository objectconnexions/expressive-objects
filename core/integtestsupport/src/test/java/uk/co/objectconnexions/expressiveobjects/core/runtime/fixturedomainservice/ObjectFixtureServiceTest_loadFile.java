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

package uk.co.objectconnexions.expressiveobjects.core.runtime.fixturedomainservice;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.Locale;
import java.util.Set;

import junit.framework.Assert;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.core.commons.config.ConfigurationConstants;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfigurationDefault;
import uk.co.objectconnexions.expressiveobjects.core.integtestsupport.ExpressiveObjectsSystemWithFixtures;
import uk.co.objectconnexions.expressiveobjects.core.runtime.fixturedomainservice.ObjectFixtureService;
import uk.co.objectconnexions.expressiveobjects.core.tck.dom.movies.Person;
import uk.co.objectconnexions.expressiveobjects.core.tck.dom.refs.SimpleEntity;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

public class ObjectFixtureServiceTest_loadFile {

    private static ExpressiveObjectsConfiguration configuration() {
        ExpressiveObjectsConfigurationDefault configuration = new ExpressiveObjectsConfigurationDefault();
        configuration.add(ConfigurationConstants.ROOT + "exploration-objects.file", "test.data");
        return configuration;
    }

    @Rule
    public ExpressiveObjectsSystemWithFixtures iswf = ExpressiveObjectsSystemWithFixtures.builder().with(configuration()).build();
    
    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    private ObjectFixtureService service;

    
    @Before
    public void setup() {
        Logger.getRootLogger().setLevel(Level.OFF);
        Locale.setDefault(Locale.UK);

        service = new ObjectFixtureService();
    }


    @Test
    public void loadFile() throws Exception {

        // when
        service.loadFile();

        // then
        final Set<Object> objects = service.allSavedObjects();
        Assert.assertEquals(1, objects.size());
        final Object object = objects.toArray()[0];
        assertThat(object instanceof SimpleEntity, is(true));
        Assert.assertEquals("Fred Smith", ((SimpleEntity) object).getName());
        Assert.assertEquals(new Date(110, 2, 8, 13, 32), ((SimpleEntity) object).getDate());
    }

}
