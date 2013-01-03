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

package uk.co.objectconnexions.expressiveobjects.core.commons.config;

import java.util.Properties;

import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;

public class ExpressiveObjectsConfigurationDefault_PropertNamesTest extends TestCase {

    private ExpressiveObjectsConfigurationDefault configuration;

    public ExpressiveObjectsConfigurationDefault_PropertNamesTest(final String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        BasicConfigurator.configure();
        LogManager.getRootLogger().setLevel(Level.OFF);

        configuration = new ExpressiveObjectsConfigurationDefault();

        final Properties p = new Properties();
        p.put("expressive-objects.bool", "on");
        p.put("expressive-objects.str", "original");
        configuration.add(p);

        final Properties p1 = new Properties();
        p1.put("expressive-objects.int", "1");
        p1.put("expressive-objects.str", "replacement");
        configuration.add(p1);
    }

    public void testDuplicatedPropertyIsNotReplaced() {
        assertEquals("original", configuration.getString("expressive-objects.str"));
    }

    public void testUniqueEntries() {
        assertEquals(1, configuration.getInteger("expressive-objects.int"));
        assertEquals(true, configuration.getBoolean("expressive-objects.bool"));
    }

}
