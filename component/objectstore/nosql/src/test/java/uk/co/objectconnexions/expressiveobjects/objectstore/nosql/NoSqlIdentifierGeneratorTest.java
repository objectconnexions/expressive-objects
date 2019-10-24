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

package uk.co.objectconnexions.expressiveobjects.objectstore.nosql;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.RootOid;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.RootOidDefault;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecId;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.SpecificationLoaderSpi;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.IdentifierGenerator;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmock.auto.Mock;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;
import uk.co.objectconnexions.expressiveobjects.objectstore.nosql.NoSqlIdentifierGenerator;
import uk.co.objectconnexions.expressiveobjects.objectstore.nosql.db.NoSqlDataDatabase;

public class NoSqlIdentifierGeneratorTest {

    public static class ExamplePojo {
    }

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_ONLY);

    @Mock
    private NoSqlDataDatabase db;
    @Mock
    private SpecificationLoaderSpi mockSpecificationLoader;
    @Mock
    private ObjectSpecification mockSpecification;

    private final ObjectSpecId sequenceNumbersSpecId = ObjectSpecId.of("_id");
    private IdentifierGenerator identifierGenerator;

    @Before
    public void setup() {
        Logger.getRootLogger().setLevel(Level.OFF);

        context.checking(new Expectations() {
            {
                allowing(mockSpecificationLoader).loadSpecification(with(ExamplePojo.class));
                will(returnValue(mockSpecification));

                allowing(mockSpecification).getCorrespondingClass();
                will(returnValue(ExamplePojo.class));

                allowing(mockSpecification).getCorrespondingClass();
                will(returnValue(sequenceNumbersSpecId));
            }
        });

        identifierGenerator = new NoSqlIdentifierGenerator(db, -999, 4);
    }

    @Test
    public void transientIdentifier() throws Exception {
        String identifier = identifierGenerator.createTransientIdentifierFor(sequenceNumbersSpecId, new ExamplePojo());
        assertEquals("-999", identifier);
        
        identifier = identifierGenerator.createTransientIdentifierFor(sequenceNumbersSpecId, new ExamplePojo());
        assertEquals("-998", identifier);
    }

    @Test
    public void batchCreatedAndReused() throws Exception {
        context.checking(new Expectations() {
            {
                one(db).nextSerialNumberBatch(sequenceNumbersSpecId, 4);
                will(returnValue(1L));
            }
        });

        RootOid transientRootOid = RootOidDefault.createTransient(sequenceNumbersSpecId, "-998");
        String identifier = identifierGenerator.createPersistentIdentifierFor(sequenceNumbersSpecId, new ExamplePojo(), transientRootOid);
        assertEquals("1", identifier);

        transientRootOid = RootOidDefault.createTransient(sequenceNumbersSpecId, "-997");
        identifier = identifierGenerator.createPersistentIdentifierFor(sequenceNumbersSpecId, new ExamplePojo(), transientRootOid);
        assertEquals("2", identifier);
    }

    @Test
    public void secondBatchCreated() throws Exception {
        context.checking(new Expectations() {
            {
                one(db).nextSerialNumberBatch(sequenceNumbersSpecId, 4);
                will(returnValue(1L));
            }
        });

        RootOid transientRootOid = RootOidDefault.createTransient(sequenceNumbersSpecId, "-998");
        String identifier = identifierGenerator.createPersistentIdentifierFor(sequenceNumbersSpecId, new ExamplePojo(), transientRootOid);
        assertEquals("1", identifier);

        transientRootOid = RootOidDefault.createTransient(sequenceNumbersSpecId, "-997");
        identifier = identifierGenerator.createPersistentIdentifierFor(sequenceNumbersSpecId, new ExamplePojo(), transientRootOid);
        assertEquals("2", identifier);

        transientRootOid = RootOidDefault.createTransient(sequenceNumbersSpecId, "-996");
        identifier = identifierGenerator.createPersistentIdentifierFor(sequenceNumbersSpecId, new ExamplePojo(), transientRootOid);
        assertEquals("3", identifier);

        transientRootOid = RootOidDefault.createTransient(sequenceNumbersSpecId, "-995");
        identifier = identifierGenerator.createPersistentIdentifierFor(sequenceNumbersSpecId, new ExamplePojo(), transientRootOid);
        assertEquals("4", identifier);

        context.checking(new Expectations() {
            {
                one(db).nextSerialNumberBatch(sequenceNumbersSpecId, 4);
                will(returnValue(5L));
            }
        });

        transientRootOid = RootOidDefault.createTransient(sequenceNumbersSpecId, "-994");
        identifier = identifierGenerator.createPersistentIdentifierFor(sequenceNumbersSpecId, new ExamplePojo(), transientRootOid);
        assertEquals("5", identifier);
    }

}
