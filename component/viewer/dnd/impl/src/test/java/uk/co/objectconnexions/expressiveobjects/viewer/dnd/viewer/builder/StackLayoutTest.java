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

package uk.co.objectconnexions.expressiveobjects.viewer.dnd.viewer.builder;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.viewer.dnd.DummyView;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.DummyViewSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.DummyWorkspaceView;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Size;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.composite.StackLayout;

public class StackLayoutTest {
    private StackLayout layout;
    private DummyView subview1;
    private DummyView subview2;

    @Before
    public void setUp() throws Exception {
        layout = new StackLayout();

        subview1 = new DummyView();
        subview1.setupSpecification(new DummyViewSpecification());
        subview1.setupRequiredSize(new Size(100, 20));

        subview2 = new DummyView();
        subview2.setupSpecification(new DummyViewSpecification());
        subview2.setupRequiredSize(new Size(120, 20));

    }

    @Test
    public void noContentNoSize() throws Exception {
        final View view = new DummyView();
        Assert.assertEquals(new Size(), layout.getRequiredSize(view));
    }

    @Test
    public void sameSizeAsOnlyComponent() throws Exception {
        final View view = new DummyWorkspaceView();
        final DummyView subview = new DummyView();
        subview.setupRequiredSize(new Size(100, 20));
        view.addView(subview);

        Assert.assertEquals(new Size(100, 20), layout.getRequiredSize(view));
    }

    @Test
    public void sameWidthAsWidestComponentAndHeightTotalOfAll() throws Exception {
        final View view = new DummyWorkspaceView();
        view.addView(subview1);
        view.addView(subview2);

        Assert.assertEquals(new Size(120, 40), layout.getRequiredSize(view));
    }

    @Test
    public void layoutToMaxWidth() throws Exception {
        final View view = new DummyWorkspaceView();
        view.addView(subview1);
        view.addView(subview2);

        layout.layout(view, new Size(110, 60));

        Assert.assertEquals(new Size(100, 20), subview1.getSize());
        Assert.assertEquals(new Size(110, 20), subview2.getSize());
    }

    @Test
    public void layoutFixedWidth() throws Exception {
        final View view = new DummyWorkspaceView();
        view.addView(subview1);
        view.addView(subview2);

        layout = new StackLayout(true);
        layout.layout(view, Size.createMax());

        Assert.assertEquals(new Size(120, 20), subview1.getSize());
        Assert.assertEquals(new Size(120, 20), subview2.getSize());
    }

}
