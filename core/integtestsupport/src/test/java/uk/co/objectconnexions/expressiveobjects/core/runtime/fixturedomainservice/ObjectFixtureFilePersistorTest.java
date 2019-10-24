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
import static org.junit.Assert.*;
import static org.junit.Assume.assumeThat;

import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import junit.framework.Assert;

import com.google.common.collect.Sets;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfigurationDefault;
import uk.co.objectconnexions.expressiveobjects.core.commons.matchers.ExpressiveObjectsMatchers;
import uk.co.objectconnexions.expressiveobjects.core.integtestsupport.ExpressiveObjectsSystemWithFixtures;
import uk.co.objectconnexions.expressiveobjects.core.runtime.fixturedomainservice.FixtureException;
import uk.co.objectconnexions.expressiveobjects.core.runtime.fixturedomainservice.ObjectFixtureFilePersistor;
import uk.co.objectconnexions.expressiveobjects.core.tck.dom.refs.ParentEntity;
import uk.co.objectconnexions.expressiveobjects.core.tck.dom.refs.ReferencingEntity;
import uk.co.objectconnexions.expressiveobjects.core.tck.dom.refs.SimpleEntity;

public class ObjectFixtureFilePersistorTest {

    private static final String DATEFORMAT_PATTERN = "dd-MMM-yyyy HH:mm z";
    
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT_PATTERN);
    
    @Rule
    public ExpressiveObjectsSystemWithFixtures iswf = ExpressiveObjectsSystemWithFixtures.builder()
        .with(configuration()).build();
    
    private static ExpressiveObjectsConfiguration configuration() {
        final ExpressiveObjectsConfigurationDefault config = new ExpressiveObjectsConfigurationDefault();
        config.add("expressive-objects.value.format.datetime", DATEFORMAT_PATTERN);
        return config;
    }

    private ObjectFixtureFilePersistor persistor;

    @Before
    public void setup() throws Exception {
        Logger.getRootLogger().setLevel(Level.OFF);

        Locale.setDefault(Locale.UK);

        persistor = new ObjectFixtureFilePersistor();

        iswf.fixtures.smpl1.setName("Fred Smith");
        iswf.fixtures.smpl1.setDate(dateFormat.parse("08-Mar-2010 01:00 UTC"));

        iswf.fixtures.smpl2.setName("Joe Bloggs");
        iswf.fixtures.smpl2.setDate(dateFormat.parse("09-Apr-2011 02:10 UTC"));

        assumeThat(TimeZone.getDefault().getDisplayName(), is("Greenwich Mean Time"));
    }
    

    @Test
    public void loadInstance() throws Exception {
        
        final StringReader reader = new StringReader(SimpleEntity.class.getName() + "#1\n  name: Fred Smith\n  date: 08-Mar-2010 01:00 UTC");
        final Set<Object> objects = persistor.loadData(reader);

        assertEquals(1, objects.size());
        final Object object = objects.toArray()[0];
        assertThat(object instanceof SimpleEntity, is(true));
        final SimpleEntity epv = (SimpleEntity) object;
        assertEquals("Fred Smith", epv.getName());
        
        assertEquals(dateFormat.parse("08-Mar-2010 01:00 GMT"), epv.getDate());
    }

    @Test
    public void invalidFieldLine() throws Exception {
        try {
            final StringReader reader = new StringReader(SimpleEntity.class.getName() + "#1\n  name Fred Smith");
            persistor.loadData(reader);
            Assert.fail();
        } catch (final FixtureException e) {
            Assert.assertEquals("failed to load data at line 2", e.getMessage());
            Assert.assertEquals("no colon (:) in: name Fred Smith", e.getCause().getMessage());
        }
    }

    @Test
    public void oldFieldNameSkipped() throws Exception {
        final StringReader reader = new StringReader(SimpleEntity.class.getName() + "#1\n  xname: Fred Smith");
        final Set<Object> objects = persistor.loadData(reader);
        final Object object = objects.toArray()[0];
        Assert.assertNull(((SimpleEntity) object).getName());

    }

    @Test
    public void saveNoObjects() throws Exception {
        // Person person = new Person();
        final Set<Object> objects = new HashSet<Object>();
        final StringWriter out = new StringWriter();
        persistor.save(objects, out);
        Assert.assertEquals("", out.toString());
    }

    @Test
    public void saveOneObject() throws Exception {
        final Set<Object> objects = Sets.newLinkedHashSet();
        objects.add(iswf.fixtures.smpl1);

        final StringWriter out = new StringWriter();
        persistor.save(objects, out);
        final String actual = out.toString().replaceAll("\r\n", "\n");
        
        final String expected = SimpleEntity.class.getName() + "#2\n  date: 08-Mar-2010 01:00 UTC\n  name: Fred Smith\n";
        
        assertThat(actual, ExpressiveObjectsMatchers.startsWith(expected));
    }

    @Test
    public void saveTwoObjects() throws Exception {
        final Set<Object> objects = Sets.newLinkedHashSet();
        objects.add(iswf.fixtures.smpl1);
        objects.add(iswf.fixtures.smpl3);

        final StringWriter out = new StringWriter();
        persistor.save(objects, out);
        final String actual = out.toString().replaceAll("\r\n", "\n");

        final String expected1 = SimpleEntity.class.getName() + "#2\n  date: 08-Mar-2010 01:00 UTC\n  name: Fred Smith\n";
        final String expected2 = SimpleEntity.class.getName() + "#3\n  date: \n  name: \n";
        assertThat(actual, ExpressiveObjectsMatchers.contains(expected1));
        assertThat(actual, ExpressiveObjectsMatchers.contains(expected2));
    }

    @Test
    public void saveReferencedObject() throws Exception {

        final Set<Object> objects = Sets.newLinkedHashSet();
        
        iswf.fixtures.rfcg1.setReference(iswf.fixtures.smpl1);
        objects.add(iswf.fixtures.rfcg1);
        objects.add(iswf.fixtures.smpl1);

        final StringWriter out = new StringWriter();
        persistor.save(objects, out);
        final String actual = out.toString().replaceAll("\r\n", "\n");

        final String expected1 = ReferencingEntity.class.getName() + "#2\n  aggregatedEntities: \n  aggregatedReference: \n  reference: " + SimpleEntity.class.getName() + "#3";
        final String expected2 = SimpleEntity.class.getName() + "#3\n  date: 08-Mar-2010 01:00 UTC\n  name: Fred Smith\n";
        assertThat(actual, ExpressiveObjectsMatchers.contains(expected1));
        assertThat(actual, ExpressiveObjectsMatchers.contains(expected2));
    }

    
    @Test
    public void saveObjectAndAssociatedCollection() throws Exception {

        final Set<Object> objects = Sets.newLinkedHashSet();
        
        iswf.fixtures.prnt1.getHomogeneousCollection().add(iswf.fixtures.smpl1);
        iswf.fixtures.prnt1.getHomogeneousCollection().add(iswf.fixtures.smpl2);
        objects.add(iswf.fixtures.prnt1);

        objects.add(iswf.fixtures.smpl1);
        objects.add(iswf.fixtures.smpl2);

        final StringWriter out = new StringWriter();
        persistor.save(objects, out);
        final String actual = out.toString().replaceAll("\r\n", "\n");
        
        final String expected1a = ParentEntity.class.getName() + "#2\n";
        final String expected1b = "heterogeneousCollection: \n  homogeneousCollection: " + SimpleEntity.class.getName() + "#3 " + SimpleEntity.class.getName() + "#4 " + "\n";
        final String expected2 = SimpleEntity.class.getName() + "#3\n  date: 08-Mar-2010 01:00 UTC\n  name: Fred Smith\n";
        final String expected3 = SimpleEntity.class.getName() + "#4\n  date: 09-Apr-2011 02:10 UTC\n  name: Joe Bloggs\n";
        assertThat(actual.replaceAll("\n", "###"), ExpressiveObjectsMatchers.contains(expected1a.replaceAll("\n", "###")));
        assertThat(actual.replaceAll("\n", "###"), ExpressiveObjectsMatchers.contains(expected1b.replaceAll("\n", "###")));
        assertThat(actual, ExpressiveObjectsMatchers.contains(expected2));
        assertThat(actual, ExpressiveObjectsMatchers.contains(expected3));
    }

}

