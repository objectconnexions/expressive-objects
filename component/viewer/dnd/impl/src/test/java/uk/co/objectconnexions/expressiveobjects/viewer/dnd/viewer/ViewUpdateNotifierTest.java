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

package uk.co.objectconnexions.expressiveobjects.viewer.dnd.viewer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Vector;

import junit.framework.Assert;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.core.commons.debug.DebugString;
import uk.co.objectconnexions.expressiveobjects.core.commons.exceptions.ExpressiveObjectsException;
import uk.co.objectconnexions.expressiveobjects.core.integtestsupport.ExpressiveObjectsSystemWithFixtures;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.SpecificationLoaderSpi;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.AuthenticationManager;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authorization.AuthorizationManager;
import uk.co.objectconnexions.expressiveobjects.core.runtime.imageloader.TemplateImageLoader;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.PersistenceSession;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.PersistenceSessionFactory;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.transaction.ExpressiveObjectsTransaction;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.transaction.ExpressiveObjectsTransactionManager;
import uk.co.objectconnexions.expressiveobjects.core.runtime.userprofile.UserProfileLoader;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmock.auto.Mock;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.DummyView;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.DummyWorkspaceView;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.base.ViewUpdateNotifierImpl;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.content.RootObject;

public class ViewUpdateNotifierTest {

    @Rule
    public ExpressiveObjectsSystemWithFixtures iswf = ExpressiveObjectsSystemWithFixtures.builder().build();

    private ExposedViewUpdateNotifier notifier;
    
    private ObjectAdapter adapter;

    @Before
    public void setUp() throws Exception {
        Logger.getRootLogger().setLevel(Level.OFF);

        iswf.persist(iswf.fixtures.smpl1);
        
        adapter = iswf.adapterFor(iswf.fixtures.smpl1);
        
        notifier = new ExposedViewUpdateNotifier();
    }
    
    @After
    public void tearDown() {
        ExpressiveObjectsContext.closeSession();
    }

    private DummyView createView(final ObjectAdapter object) {
        final DummyView view = new DummyView();
        view.setupContent(new RootObject(object));
        return view;
    }

    @Test
    public void testAddViewWithNonObjectContent() {
        final DummyView view = createView(null);
        notifier.add(view);
        notifier.assertEmpty();
    }

    @Test
    public void testAddViewWithObjectContent() {
        final DummyView view = addViewForObject();
        notifier.assertContainsViewForObject(view, adapter);
    }

    private DummyView addViewForObject() {
        final DummyView view = createView(adapter);
        notifier.add(view);
        return view;
    }

    @Test
    public void testAddViewIsIgnoredAfterFirstCall() {
        final DummyView view = addViewForObject();
        try {
            notifier.add(view);
            fail();
        } catch (final ExpressiveObjectsException expected) {
        }
    }

    @Test
    public void testDebug() throws Exception {
        addViewForObject();
        final DebugString debugString = new DebugString();
        notifier.debugData(debugString);
        assertNotNull(debugString.toString());
    }

    @Test
    public void testRemoveView() {
        final Vector vector = new Vector();
        final DummyView view = createView(adapter);
        vector.addElement(view);
        notifier.setupViewsForObject(adapter, vector);

        notifier.remove(view);
        notifier.assertEmpty();
    }

    @Test
    public void testViewDirty() {

        //adapter.setupResolveState(ResolveState.RESOLVED);

        final Vector<View> vector = new Vector<View>();
        final DummyView view1 = createView(adapter);
        vector.addElement(view1);

        final DummyView view2 = createView(adapter);
        vector.addElement(view2);

        notifier.setupViewsForObject(adapter, vector);

        notifier.invalidateViewsForChangedObjects();
        assertEquals(0, view1.invalidateContent);
        assertEquals(0, view2.invalidateContent);

        ExpressiveObjectsContext.getUpdateNotifier().addChangedObject(adapter);
        notifier.invalidateViewsForChangedObjects();

        assertEquals(1, view1.invalidateContent);
        assertEquals(1, view2.invalidateContent);
    }

    
    @Test
    public void testDisposedViewsRemoved() {
        final DummyWorkspaceView workspace = new DummyWorkspaceView();

        final Vector<View> vector = new Vector<View>();
        final DummyView view1 = createView(adapter);
        view1.setParent(workspace);
        workspace.addView(view1);
        vector.addElement(view1);

        final DummyView view2 = createView(adapter);
        view2.setParent(workspace);
        workspace.addView(view2);
        vector.addElement(view2);

        notifier.setupViewsForObject(adapter, vector);

        notifier.invalidateViewsForChangedObjects();
        assertEquals(0, view1.invalidateContent);
        assertEquals(0, view2.invalidateContent);

        ExpressiveObjectsContext.getUpdateNotifier().addDisposedObject(adapter);
        notifier.removeViewsForDisposedObjects();
        assertEquals(0, workspace.getSubviews().length);

    }
}

class ExposedViewUpdateNotifier extends ViewUpdateNotifierImpl {

    public void assertContainsViewForObject(final View view, final ObjectAdapter object) {
        Assert.assertTrue(viewListByAdapter.containsKey(object));
        final List<View> viewsForObject = viewListByAdapter.get(object);
        Assert.assertTrue(viewsForObject.contains(view));
    }

    public void setupViewsForObject(final ObjectAdapter object, final Vector<View> vector) {
        viewListByAdapter.put(object, vector);
    }

    public void assertEmpty() {
        Assert.assertTrue("Not empty", viewListByAdapter.isEmpty());
    }
}
