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

import static org.hamcrest.CoreMatchers.equalTo;

import java.util.ArrayList;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.commons.exceptions.UnexpectedCallException;
import uk.co.objectconnexions.expressiveobjects.core.integtestsupport.ExpressiveObjectsSystemWithFixtures;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.OidMarshaller;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.RootOidDefault;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.version.SerialNumberVersion;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.version.Version;
import uk.co.objectconnexions.expressiveobjects.core.tck.dom.refs.ParentEntity;
import uk.co.objectconnexions.expressiveobjects.core.tck.dom.refs.ReferencingEntity;
import uk.co.objectconnexions.expressiveobjects.core.tck.dom.refs.SimpleEntity;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmock.auto.Mock;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;
import uk.co.objectconnexions.expressiveobjects.objectstore.nosql.NoSqlCommandContext;
import uk.co.objectconnexions.expressiveobjects.objectstore.nosql.WriteObjectCommand;
import uk.co.objectconnexions.expressiveobjects.objectstore.nosql.db.StateWriter;
import uk.co.objectconnexions.expressiveobjects.objectstore.nosql.encryption.DataEncryption;
import uk.co.objectconnexions.expressiveobjects.objectstore.nosql.versions.VersionCreator;

public class WriteObjectCommandTest {
    
    @Rule
    public ExpressiveObjectsSystemWithFixtures iswf = ExpressiveObjectsSystemWithFixtures.builder().build();

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    private StateWriter writer;
    @Mock
    private VersionCreator versionCreator;
    
    @Mock
    private NoSqlCommandContext commandContext;

    private DataEncryption dataEncrypter;

    private ObjectAdapter smpl1Adapter;
    private ObjectAdapter rfcg1Adapter;
    private ObjectAdapter prnt1Adapter;

    private SimpleEntity smpl1;
    private SimpleEntity smpl2;

    private ReferencingEntity rfcg1;

    private ParentEntity prnt1;

    @Before
    public void setup() {

        smpl1 = iswf.fixtures.smpl1;
        smpl1.setName("Fred Smith");
        smpl1.setSize(108);
        smpl1Adapter = iswf.remapAsPersistent(smpl1, RootOidDefault.deString("SMPL:1", new OidMarshaller()));

        smpl2 = iswf.fixtures.smpl2;
        smpl2.setName("John Brown");
        iswf.remapAsPersistent(smpl2, RootOidDefault.deString("SMPL:2", new OidMarshaller()));

        rfcg1 = iswf.fixtures.rfcg1;
        rfcg1.setReference(smpl1);
        rfcg1Adapter = iswf.remapAsPersistent(rfcg1, RootOidDefault.deString("RFCG:1", new OidMarshaller()));

        prnt1 = iswf.fixtures.prnt1;
        prnt1.getHomogeneousCollection().add(smpl1);
        prnt1.getHomogeneousCollection().add(smpl2);
        
        prnt1Adapter = iswf.remapAsPersistent(prnt1, RootOidDefault.deString("PRNT:1", new OidMarshaller()));

        final Version version = SerialNumberVersion.create(2, "username", null);

        context.checking(new Expectations() {
            {
                one(versionCreator).newVersion("tester");
                will(returnValue(version));
                one(versionCreator).versionString(version);
                will(returnValue("2"));
                one(versionCreator).timeString(version);
                will(returnValue("1057"));
            }
        });

        dataEncrypter = new DataEncryption() {
            @Override
            public String getType() {
                return "etc1";
            }

            @Override
            public void init(final ExpressiveObjectsConfiguration configuration) {
            }

            @Override
            public String encrypt(final String plainText) {
                return "ENC" + plainText;
            }

            @Override
            public String decrypt(final String encryptedText) {
                throw new UnexpectedCallException();
            }
        };
    }


    @Test
    public void objectWithValues() throws Exception {

        context.checking(new Expectations() {

            {
                one(commandContext).createStateWriter(smpl1Adapter.getSpecification().getSpecId());
                will(returnValue(writer));

                final RootOidDefault oid = RootOidDefault.create(smpl1Adapter.getSpecification().getSpecId(), "1");
                exactly(2).of(writer).writeOid(oid); // once for the id, once for the type

                one(writer).writeField("name", "ENCFred Smith");
                one(writer).writeField("size", "ENC108");
                one(writer).writeField("date", null);
                one(writer).writeField("nullable", null);
                one(writer).writeVersion(null, "2");
                one(writer).writeUser("username");
                one(writer).writeTime("1057");
                one(writer).writeEncryptionType("etc1");

                one(commandContext).insert(writer);

            }
        });

        final WriteObjectCommand command = new WriteObjectCommand(WriteObjectCommand.Mode.NON_UPDATE, versionCreator, dataEncrypter, smpl1Adapter);
        command.execute(commandContext);
    }

    @Test
    public void objectWithReferences() throws Exception {

        context.checking(new Expectations() {
            {
                one(commandContext).createStateWriter(rfcg1Adapter.getSpecification().getSpecId());
                will(returnValue(writer));

                final RootOidDefault oid = RootOidDefault.create(rfcg1Adapter.getSpecification().getSpecId(), "1");
                exactly(2).of(writer).writeOid(oid); // once for the id, once for the type
                
                one(writer).writeField("reference", "SMPL:1");
                one(writer).writeField("aggregatedReference", null);
                one(writer).writeCollection(with(equalTo("aggregatedEntities")), with(equalTo(new ArrayList<StateWriter>())));
                
                one(writer).writeVersion(null, "2");
                one(writer).writeUser("username");
                one(writer).writeTime("1057");
                one(writer).writeEncryptionType("etc1");

                one(commandContext).insert(writer);
            }
        });

        final WriteObjectCommand command = new WriteObjectCommand(WriteObjectCommand.Mode.NON_UPDATE, versionCreator, dataEncrypter, rfcg1Adapter);
        command.execute(commandContext);
    }

    @Test
    public void objectWithCollections() throws Exception {

        context.checking(new Expectations() {
            {
                one(commandContext).createStateWriter(prnt1Adapter.getSpecification().getSpecId());
                will(returnValue(writer));

                final RootOidDefault oid = RootOidDefault.create(prnt1Adapter.getSpecification().getSpecId(), "1");
                exactly(2).of(writer).writeOid(oid); // once for the id, once for the type

                one(writer).writeField("name", null);
                one(writer).writeField("homogeneousCollection", "SMPL:1|SMPL:2|");

                one(writer).writeVersion(null, "2");
                one(writer).writeUser("username");
                one(writer).writeTime("1057");
                one(writer).writeEncryptionType("etc1");

                one(commandContext).insert(writer);
            }
        });

        final WriteObjectCommand command = new WriteObjectCommand(WriteObjectCommand.Mode.NON_UPDATE, versionCreator, dataEncrypter, prnt1Adapter);
        command.execute(commandContext);
    }
}
