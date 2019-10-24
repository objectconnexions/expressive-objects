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

import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.RootOid;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.RootOidDefault;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.version.Version;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecId;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmock.auto.Mock;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;
import uk.co.objectconnexions.expressiveobjects.objectstore.nosql.NoSqlCommandContext;
import uk.co.objectconnexions.expressiveobjects.objectstore.nosql.NoSqlDestroyObjectCommand;
import uk.co.objectconnexions.expressiveobjects.objectstore.nosql.versions.VersionCreatorDefault;
import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class DestroyObjectCommandImplementationTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);
    
    @Mock
    private NoSqlCommandContext commandContext;
    @Mock
    private ObjectSpecification specification;
    @Mock
    private ObjectAdapter adapter;
    
    @Mock
    private VersionCreatorDefault versionCreator;
    @Mock
    private Version version;

    //private KeyCreatorDefault keyCreator;

    private final ObjectSpecId specId = ObjectSpecId.of("com.foo.bar.SomeClass");

    private long id = 123;
    private String keyStr = Long.toString(id, 16);

    private RootOid rootOid = RootOidDefault.create(specId, keyStr);

    private NoSqlDestroyObjectCommand command;

    @Before
    public void setup() {
        //keyCreator = new KeyCreatorDefault();
        
        context.checking(new Expectations(){{

            allowing(specification).getFullIdentifier();
            will(returnValue("com.foo.bar.SomeClass"));

            allowing(specification).getSpecId();
            will(returnValue(specId));

            allowing(adapter).getSpecification();
            will(returnValue(specification));
            
            allowing(adapter).getOid();
            will(returnValue(rootOid));

            allowing(adapter).getVersion();
            will(returnValue(version));

        }});
    }

    @Test
    public void execute() throws Exception {
        
        final String versionStr = "3";

        context.checking(new Expectations() {
            {
                one(versionCreator).versionString(version);
                will(returnValue(versionStr));

                one(commandContext).delete(specification.getSpecId(), keyStr, versionStr, rootOid);
            }
        });

        command = new NoSqlDestroyObjectCommand(versionCreator, adapter);
        command.execute(commandContext);
    }
}
