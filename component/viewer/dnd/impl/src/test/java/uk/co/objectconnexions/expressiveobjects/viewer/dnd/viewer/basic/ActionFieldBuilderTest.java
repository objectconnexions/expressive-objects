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

package uk.co.objectconnexions.expressiveobjects.viewer.dnd.viewer.basic;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.easymock.MockControl;
import org.jmock.Expectations;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSession;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfigurationDefault;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.oid.OidMarshaller;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.SpecificationLoaderSpi;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.AuthenticationManager;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.exploration.ExplorationSession;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authorization.AuthorizationManager;
import uk.co.objectconnexions.expressiveobjects.core.runtime.imageloader.TemplateImageLoader;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.DeploymentType;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContextStatic;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.PersistenceSessionFactory;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.session.ExpressiveObjectsSessionFactoryDefault;
import uk.co.objectconnexions.expressiveobjects.core.runtime.userprofile.UserProfile;
import uk.co.objectconnexions.expressiveobjects.core.runtime.userprofile.UserProfileLoader;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmock.auto.Mock;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.DummyView;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.dialog.ActionFieldBuilder;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Axes;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Content;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewFactory;

public class ActionFieldBuilderTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    protected TemplateImageLoader mockTemplateImageLoader;
    @Mock
    protected SpecificationLoaderSpi mockSpecificationLoader;
    @Mock
    protected PersistenceSessionFactory mockPersistenceSessionFactory;
    @Mock
    private UserProfileLoader mockUserProfileLoader;
    @Mock
    protected AuthenticationManager mockAuthenticationManager;
    @Mock
    protected AuthorizationManager mockAuthorizationManager;


    private ExpressiveObjectsConfiguration configuration;
    private List<Object> servicesList;
    private OidMarshaller oidMarshaller;

    private ActionFieldBuilder builder;

    @Before
    public void setUp() throws Exception {
        Logger.getRootLogger().setLevel(Level.OFF);

        configuration = new ExpressiveObjectsConfigurationDefault();
        servicesList = Collections.emptyList();
        
        oidMarshaller = new OidMarshaller();

        context.checking(new Expectations() {
            {
                ignoring(mockSpecificationLoader);
                ignoring(mockPersistenceSessionFactory);

                one(mockUserProfileLoader).getProfile(with(any(AuthenticationSession.class)));
                will(returnValue(new UserProfile()));

                ignoring(mockTemplateImageLoader);
                ignoring(mockAuthenticationManager);
                ignoring(mockAuthorizationManager);
            }
        });

        final ViewFactory subviewSpec = new ViewFactory() {
            @Override
            public View createView(final Content content, final Axes axes, final int fieldNumber) {
                return new DummyView();
            }
        };

        final ExpressiveObjectsSessionFactoryDefault sessionFactory = new ExpressiveObjectsSessionFactoryDefault(DeploymentType.EXPLORATION, configuration, mockTemplateImageLoader, mockSpecificationLoader, mockAuthenticationManager, mockAuthorizationManager, mockUserProfileLoader, mockPersistenceSessionFactory, servicesList, oidMarshaller);

        ExpressiveObjectsContext.setConfiguration(sessionFactory.getConfiguration());
        ExpressiveObjectsContextStatic.createRelaxedInstance(sessionFactory);
        ExpressiveObjectsContextStatic.openSession(new ExplorationSession());

        builder = new ActionFieldBuilder(subviewSpec);

    }

    @After
    public void tearDown() {
        ExpressiveObjectsContext.closeSession();
    }

    @Test
    public void testUpdateBuild() {
        final MockControl control = MockControl.createControl(View.class);
        final View view = (View) control.getMock();

        control.expectAndDefaultReturn(view.getView(), view);
        control.expectAndDefaultReturn(view.getContent(), null);

        /*
         * DummyView[] views = new DummyView[2]; views[1] = new DummyView();
         * views[1].setupContent(new ObjectParameter("name", null, null, false,
         * 1, actionContent)); view.setupSubviews(views);
         */

        control.replay();

        // builder.build(view);

        control.verify();
    }

    /*
     * // TODO fails on server as cant load X11 for Text class public void
     * xxxtestNewBuild() { view.setupSubviews(new View[0]);
     * 
     * view.addAction("add TextView0 null");
     * view.addAction("add MockView1/LabelBorder"); view.addAction("add
     * MockView2/LabelBorder");
     * 
     * builder.build(view);
     * 
     * view.verify(); } public void
     * xxxtestUpdateBuildWhereParameterHasChangedFromNullToAnObject() {
     * DummyView[] views = new DummyView[2]; views[1] = new DummyView();
     * ObjectParameter objectParameter = new ObjectParameter("name", null, null,
     * false, 1, actionContent); views[1].setupContent(objectParameter);
     * view.setupSubviews(views);
     * 
     * actionContent.setParameter(0, new DummyObjectAdapter());
     * 
     * view.addAction("replace MockView1 with MockView2/LabelBorder");
     * 
     * builder.build(view);
     * 
     * view.verify(); }
     * 
     * public void
     * xxxtestUpdateBuildWhereParameterHasChangedFromAnObjectToNull() {
     * DummyView[] views = new DummyView[2]; views[1] = new DummyView();
     * ObjectParameter objectParameter = new ObjectParameter("name", new
     * DummyObjectAdapter(), null, false, 1, actionContent);
     * views[1].setupContent(objectParameter); view.setupSubviews(views);
     * 
     * objectParameter.setObject(null);
     * 
     * view.addAction("replace MockView1 with MockView2/LabelBorder");
     * 
     * builder.build(view);
     * 
     * view.verify(); }
     * 
     * public void
     * xxxtestUpdateBuildWhereParameterHasChangedFromOneObjectToAnother() {
     * DummyView[] views = new DummyView[2]; views[1] = new DummyView();
     * ObjectParameter objectParameter = new ObjectParameter("name", new
     * DummyObjectAdapter(), null, false, 1, actionContent);
     * views[1].setupContent(objectParameter); view.setupSubviews(views);
     * 
     * objectParameter.setObject(new DummyObjectAdapter());
     * 
     * view.addAction("replace MockView1 with MockView2/LabelBorder");
     * 
     * builder.build(view);
     * 
     * view.verify(); }
     * 
     * public void xxtestUpdateBuildWhereParameterObjectSetButToSameObject() {
     * DummyView[] views = new DummyView[2]; views[1] = new DummyView();
     * DummyObjectAdapter dummyObjectAdapter = new DummyObjectAdapter();
     * ObjectParameter objectParameter = new ObjectParameter("name",
     * dummyObjectAdapter, null, false, 1, actionContent);
     * views[1].setupContent(objectParameter); view.setupSubviews(views);
     * 
     * actionContent.setParameter(0, dummyObjectAdapter); //
     * objectParameter.setObject(dummyObjectAdapter);
     * 
     * builder.build(view);
     * 
     * view.verify(); } }
     * 
     * class MockActionHelper extends ActionHelper {
     * 
     * protected MockActionHelper( ObjectAdapter target, Action action, String[]
     * labels, ObjectAdapter[] parameters, ObjectSpecification[] parameterTypes,
     * boolean[] required) { super(target, action, labels, parameters,
     * parameterTypes, required); }
     */
}
