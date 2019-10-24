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

package uk.co.objectconnexions.expressiveobjects.objectstore.nosql.db.mongo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hamcrest.CoreMatchers;
import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.commons.exceptions.UnexpectedCallException;
import uk.co.objectconnexions.expressiveobjects.core.integtestsupport.ExpressiveObjectsSystemWithFixtures;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ResolveState;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.mgr.AdapterManager;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.OidMarshaller;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.RootOidDefault;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecId;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.SpecificationLoaderSpi;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.AdapterManagerSpi;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.PersistenceSession;
import uk.co.objectconnexions.expressiveobjects.core.tck.dom.refs.ParentEntity;
import uk.co.objectconnexions.expressiveobjects.core.tck.dom.refs.ReferencingEntity;
import uk.co.objectconnexions.expressiveobjects.core.tck.dom.refs.SimpleEntity;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmock.auto.Mock;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;
import uk.co.objectconnexions.expressiveobjects.objectstore.nosql.ObjectReader;
import uk.co.objectconnexions.expressiveobjects.objectstore.nosql.db.StateReader;
import uk.co.objectconnexions.expressiveobjects.objectstore.nosql.db.mongo.MongoPersistorMechanismInstaller;
import uk.co.objectconnexions.expressiveobjects.objectstore.nosql.encryption.DataEncryption;
import uk.co.objectconnexions.expressiveobjects.objectstore.nosql.versions.VersionCreator;

public class ObjectReaderMongoIntegrationTest {
    
    @Rule
    public ExpressiveObjectsSystemWithFixtures iswf = ExpressiveObjectsSystemWithFixtures.builder().with(new MongoPersistorMechanismInstaller()).build();

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_ONLY);

    @Mock
    private VersionCreator versionCreator;

    @Mock
    private StateReader reader1;
    @Mock
    private StateReader reader2;

    private ObjectReader objectReader;
    
    private Map<String, DataEncryption> dataEncrypter;

	private OidMarshaller oidMarshaller = new OidMarshaller();

    private final RootOidDefault oid3 = RootOidDefault.deString("SMPL:3", oidMarshaller );
    private final RootOidDefault oid4 = RootOidDefault.deString("RFCG:4", oidMarshaller); 
    private final RootOidDefault oid5 = RootOidDefault.deString("PRNT:5", oidMarshaller); 


    @Before
    public void setup() {
        objectReader = new ObjectReader();

        dataEncrypter = new HashMap<String, DataEncryption>();
        final DataEncryption etcEncryption = new DataEncryption() {
            @Override
            public String getType() {
                return "etc1";
            }

            @Override
            public void init(final ExpressiveObjectsConfiguration configuration) {
            }

            @Override
            public String encrypt(final String plainText) {
                throw new UnexpectedCallException();
            }

            @Override
            public String decrypt(final String encryptedText) {
                return encryptedText.substring(3);
            }
        };
        dataEncrypter.put(etcEncryption.getType(), etcEncryption);
    }

    @Test
    public void testReadingValues() throws Exception {
        setupObject1();

        context.checking(new Expectations() {
            {
                one(reader1).readOid();
                will(returnValue("SMPL:3"));

                one(reader1).readEncrytionType();
                will(returnValue("etc1"));
                one(reader1).readVersion();
                will(returnValue("3"));
                one(reader1).readUser();
                will(returnValue("username"));
                one(reader1).readTime();
                will(returnValue("1020"));
                one(versionCreator).version("3", "username", "1020");
            }
        });

        final ObjectAdapter readObject = objectReader.load(reader1, versionCreator, dataEncrypter);
        assertEquals(oid3, readObject.getOid());
        assertEquals(ResolveState.RESOLVED, readObject.getResolveState());

        final SimpleEntity pojo = (SimpleEntity) readObject.getObject();
        assertEquals("Fred Smith", pojo.getName());
        assertEquals(34, pojo.getSize());

        context.assertIsSatisfied();
    }

    @Test
    public void testReadingReference() throws Exception {
        context.checking(new Expectations() {
            {
                one(reader2).readOid();
                will(returnValue("RFCG:4"));

                one(reader2).readEncrytionType();
                will(returnValue("etc1"));
                one(reader2).readVersion();
                will(returnValue("3"));
                one(reader2).readUser();
                will(returnValue("username"));
                one(reader2).readTime();
                will(returnValue("1020"));
                one(versionCreator).version("3", "username", "1020");

                one(reader2).readField("reference");
                will(returnValue("SMPL:3"));

                one(reader2).readCollection("aggregatedEntities");
                will(returnValue(new ArrayList<StateReader>()));

                one(reader2).readAggregate("aggregatedReference");
                will(returnValue(null));
            }
        });

        final ObjectAdapter readObject = objectReader.load(reader2, versionCreator, dataEncrypter);
        assertEquals(oid4, readObject.getOid());
        assertEquals(ResolveState.RESOLVED, readObject.getResolveState());

        final ReferencingEntity pojo = (ReferencingEntity) readObject.getObject();
        assertEquals(null, pojo.getAggregatedReference());
        assertThat(pojo.getReference(), CoreMatchers.instanceOf(SimpleEntity.class));

        context.assertIsSatisfied();
    }

    @Test
    public void testReadingCollection() throws Exception {
        //final ObjectSpecification specification = ExpressiveObjectsContext.getSpecificationLoader().loadSpecification(ExamplePojoWithValues.class);
        context.checking(new Expectations() {
            {
                one(reader2).readOid();
                will(returnValue("PRNT:5"));

                one(reader2).readEncrytionType();
                will(returnValue("etc1"));
                one(reader2).readVersion();
                will(returnValue("3"));
                one(reader2).readUser();
                will(returnValue("username"));
                one(reader2).readTime();
                will(returnValue("1020"));
                one(versionCreator).version("3", "username", "1020");

                one(reader2).readField("name");
                will(returnValue(null));
                one(reader2).readField("children");
                will(returnValue(null));
                one(reader2).readField("heterogeneousCollection");
                will(returnValue(null));
                one(reader2).readField("homogeneousCollection");
                will(returnValue("SMPL:3|SMPL:4|"));
            }
        });

        final ObjectAdapter readObject = objectReader.load(reader2, versionCreator, dataEncrypter);
        assertEquals(oid5, readObject.getOid());
        assertEquals(ResolveState.RESOLVED, readObject.getResolveState());

        final ParentEntity pojo = (ParentEntity) readObject.getObject();
        final List<SimpleEntity> collection2 = pojo.getHomogeneousCollection();
        assertEquals(2, collection2.size());

        assertThat(collection2.get(0), CoreMatchers.instanceOf(SimpleEntity.class));
        assertThat(collection2.get(1), CoreMatchers.instanceOf(SimpleEntity.class));

        context.assertIsSatisfied();
    }

    @Test
    public void updateObjectsState() throws Exception {
        setupObject1();
        context.checking(new Expectations() {
            {

                one(reader1).readEncrytionType();
                will(returnValue("etc1"));
                one(reader1).readVersion();
                will(returnValue("3"));
                one(reader1).readUser();
                will(returnValue("username"));
                one(reader1).readTime();
                will(returnValue("1020"));
                one(versionCreator).version("3", "username", "1020");
            }
        });

        final ObjectAdapter readObject = getAdapterManager().adapterFor(RootOidDefault.create(ObjectSpecId.of("SMPL"), ""+4));

        objectReader.update(reader1, versionCreator, dataEncrypter, readObject);

        final SimpleEntity pojo = (SimpleEntity) readObject.getObject();
        assertEquals("Fred Smith", pojo.getName());
        assertEquals(34, pojo.getSize());

        context.assertIsSatisfied();
    }

    private void setupObject1() {
        context.checking(new Expectations() {
            {
                one(reader1).readField("date");
                will(returnValue("null"));

                one(reader1).readField("name");
                will(returnValue("ENCFred Smith"));

                one(reader1).readField("size");
                will(returnValue("ENC34"));

                one(reader1).readField("nullable");
                will(returnValue("null"));
            }
        });
    }

    protected SpecificationLoaderSpi getSpecificationLoader() {
        return ExpressiveObjectsContext.getSpecificationLoader();
    }

    protected PersistenceSession getPersistenceSession() {
        return ExpressiveObjectsContext.getPersistenceSession();
    }

    protected AdapterManager getAdapterManager() {
        return getPersistenceSession().getAdapterManager();
    }


}
