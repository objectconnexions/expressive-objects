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

package uk.co.objectconnexions.expressiveobjects.core.runtime.system;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.applib.annotation.When;
import uk.co.objectconnexions.expressiveobjects.applib.annotation.Where;
import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSession;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.Consent;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.InteractionInvocationMethod;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.deployment.DeploymentCategory;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FeatureType;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.FacetedMethod;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.describedas.DescribedAsFacetAbstract;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.named.NamedFacetAbstract;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.PropertyUsabilityContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.PropertyVisibilityContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.UsabilityContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.VisibilityContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.Instance;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectMemberContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.specimpl.ObjectMemberAbstract;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.members.disabled.DisableForSessionFacetAbstract;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.members.hidden.HiddenFacetAbstract;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.members.hidden.HiddenFacetImpl;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.members.hidden.HiddenFacetNever;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.members.hidden.HideForContextFacetNone;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.members.hidden.HideForSessionFacetAbstract;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.members.hidden.staticmethod.HiddenFacetAlwaysEverywhere;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.transaction.PojoAdapterBuilder;
import uk.co.objectconnexions.expressiveobjects.core.runtime.persistence.objectstore.transaction.PojoAdapterBuilder.Persistence;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

public class ObjectMemberAbstractTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);
    
    private ObjectMemberAbstractImpl testMember;
    
    private ObjectAdapter persistentAdapter;
    private ObjectAdapter transientAdapter;
    
    @Before
    public void setUp() throws Exception {
        Logger.getRootLogger().setLevel(Level.OFF);

        persistentAdapter = PojoAdapterBuilder.create().build();
        transientAdapter = PojoAdapterBuilder.create().with(Persistence.TRANSIENT).build();

        testMember = new ObjectMemberAbstractImpl("id");
    }


    @Test
    public void testToString() throws Exception {
        testMember.addFacet(new NamedFacetAbstract("", testMember) {});
        assertTrue(testMember.toString().length() > 0);
    }

    @Test
    public void testAvailableForUser() throws Exception {
        testMember.addFacet(new DisableForSessionFacetAbstract(testMember) {
            @Override
            public String disabledReason(final AuthenticationSession session) {
                return null;
            }
        });
        final Consent usable = testMember.isUsable(null, persistentAdapter, Where.ANYWHERE);
        final boolean allowed = usable.isAllowed();
        assertTrue(allowed);
    }

    @Test
    public void testVisibleWhenHiddenFacetSetToAlways() {
        testMember.addFacet(new HideForContextFacetNone(testMember));
        testMember.addFacet(new HiddenFacetAbstract(When.ALWAYS, Where.ANYWHERE, testMember) {
            @Override
            public String hiddenReason(final ObjectAdapter target, final Where whereContext) {
                return null;
            }
        });
        final Consent visible = testMember.isVisible(null, persistentAdapter, Where.ANYWHERE);
        assertTrue(visible.isAllowed());
    }

    @Test
    public void testVisibleWhenTargetPersistentAndHiddenFacetSetToOncePersisted() {
        testMember.addFacet(new HideForContextFacetNone(testMember));
        testMember.addFacet(new HiddenFacetImpl(When.ONCE_PERSISTED, Where.ANYWHERE, testMember));
        assertFalse(testMember.isVisible(null, persistentAdapter, Where.ANYWHERE).isAllowed());
    }

    @Test
    public void testVisibleWhenTargetPersistentAndHiddenFacetSetToUntilPersisted() {
        testMember.addFacet(new HideForContextFacetNone(testMember));
        testMember.addFacet(new HiddenFacetImpl(When.UNTIL_PERSISTED, Where.ANYWHERE, testMember));
        final Consent visible = testMember.isVisible(null, persistentAdapter, Where.ANYWHERE);
        assertTrue(visible.isAllowed());
    }

    @Test
    public void testVisibleWhenTargetTransientAndHiddenFacetSetToUntilPersisted() {
        testMember.addFacet(new HideForContextFacetNone(testMember));
        testMember.addFacet(new HiddenFacetImpl(When.UNTIL_PERSISTED, Where.ANYWHERE, testMember));
        
        final Consent visible = testMember.isVisible(null, transientAdapter, Where.ANYWHERE);
        assertFalse(visible.isAllowed());
    }

    @Test
    public void testVisibleDeclarativelyByDefault() {
        testMember.addFacet(new HiddenFacetNever(testMember) {
        });
        assertTrue(testMember.isVisible(null, persistentAdapter, Where.ANYWHERE).isAllowed());
    }

    @Test
    public void testVisibleDeclaratively() {
        testMember.addFacet(new HiddenFacetAlwaysEverywhere(testMember) {
        });
        assertFalse(testMember.isVisible(null, persistentAdapter, Where.ANYWHERE).isAllowed());
    }

    @Test
    public void testVisibleForSessionByDefault() {
        final Consent visible = testMember.isVisible(null, persistentAdapter, Where.ANYWHERE);
        assertTrue(visible.isAllowed());
    }

    @Test
    public void testVisibleForSession() {
        testMember.addFacet(new HideForSessionFacetAbstract(testMember) {
            @Override
            public String hiddenReason(final AuthenticationSession session) {
                return "Hidden";
            }
        });
        assertFalse(testMember.isVisible(null, persistentAdapter, Where.ANYWHERE).isAllowed());
    }

    @Test
    public void testVisibleForSessionFails() {
        testMember.addFacet(new HideForSessionFacetAbstract(testMember) {
            @Override
            public String hiddenReason(final AuthenticationSession session) {
                return "hidden";
            }
        });
        assertFalse(testMember.isVisible(null, persistentAdapter, Where.ANYWHERE).isAllowed());
    }

    @Test
    public void testName() throws Exception {
        final String name = "action name";
        testMember.addFacet(new NamedFacetAbstract(name, testMember) {
        });
        assertEquals(name, testMember.getName());
    }

    @Test
    public void testDescription() throws Exception {
        final String name = "description text";
        testMember.addFacet(new DescribedAsFacetAbstract(name, testMember) {
        });
        assertEquals(name, testMember.getDescription());
    }
}

class ObjectMemberAbstractImpl extends ObjectMemberAbstract {

    public static class Customer {
        private String firstName;

        public String getFirstName() {
            return firstName;
        }
    }

    protected ObjectMemberAbstractImpl(final String id) {
        super(FacetedMethod.createForProperty(Customer.class, "firstName"), FeatureType.PROPERTY, new ObjectMemberContext(DeploymentCategory.PRODUCTION, null, null, null, null, null));
    }

    @Override
    public String debugData() {
        return null;
    }

    public Consent isUsable(final ObjectAdapter target) {
        return null;
    }

    @Override
    public ObjectSpecification getSpecification() {
        return null;
    }

    @Override
    public UsabilityContext<?> createUsableInteractionContext(final AuthenticationSession session, final InteractionInvocationMethod invocationMethod, final ObjectAdapter target, Where where) {
        return new PropertyUsabilityContext(DeploymentCategory.PRODUCTION, session, invocationMethod, target, getIdentifier(), where);
    }

    @Override
    public VisibilityContext<?> createVisibleInteractionContext(final AuthenticationSession session, final InteractionInvocationMethod invocationMethod, final ObjectAdapter targetObjectAdapter, Where where) {
        return new PropertyVisibilityContext(DeploymentCategory.PRODUCTION, session, invocationMethod, targetObjectAdapter, getIdentifier(), where);
    }

    // /////////////////////////////////////////////////////////////
    // getInstance
    // /////////////////////////////////////////////////////////////

    @Override
    public Instance getInstance(final ObjectAdapter adapter) {
        return null;
    }

}
