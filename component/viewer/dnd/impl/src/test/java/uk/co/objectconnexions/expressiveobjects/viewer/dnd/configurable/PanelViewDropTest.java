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

package uk.co.objectconnexions.expressiveobjects.viewer.dnd.configurable;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmock.auto.Mock;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.DummyContent;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.DummyView;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.DummyViewSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.TestToolkit;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.configurable.PanelView.Position;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Location;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Size;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Content;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewDrag;

public class PanelViewDropTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_ONLY);

    @Mock
    private ExpressiveObjectsConfiguration mockConfiguration;
    @Mock
    private ViewDrag viewDrag;
    
    private PanelView view;
    private DummyContent content;
    private Position position;
    private final DummyView sourceView = new DummyView();


    @Before
    public void setup() {
        Logger.getRootLogger().setLevel(Level.OFF);
        
        TestToolkit.createInstance();
        ExpressiveObjectsContext.setConfiguration(mockConfiguration);

        content = new DummyContent();

        view = new PanelView(content, new DummyViewSpecification()) {
            @Override
            public void addView(final Content content, final Position position) {
                PanelViewDropTest.this.position = position;
            }

            @Override
            public synchronized View[] getSubviews() {
                return new View[] { sourceView };
            }
        };
        view.setSize(new Size(200, 100));
        view.setLocation(new Location(400, 200));
    }

    @Test
    public void dropOnLeft() throws Exception {
        final ViewDrag drag = dragTo(new Location(405, 250));
        view.drop(drag);
        assertEquals(Position.West, position);
    }

    @Test
    public void dropOnRight() throws Exception {
        final ViewDrag drag = dragTo(new Location(595, 250));
        view.drop(drag);
        assertEquals(Position.East, position);
    }

    @Test
    public void dropOnTop() throws Exception {
        final ViewDrag drag = dragTo(new Location(500, 205));
        view.drop(drag);
        assertEquals(Position.North, position);
    }

    @Test
    public void dropOnBottom() throws Exception {
        final ViewDrag drag = dragTo(new Location(500, 295));
        view.drop(drag);
        assertEquals(Position.South, position);
    }

    @Test
    public void dropOnTopLeft() throws Exception {
        final ViewDrag drag = dragTo(new Location(405, 205));
        view.drop(drag);
        assertEquals(Position.North, position);
    }

    @Test
    public void dropOnTopRight() throws Exception {
        final ViewDrag drag = dragTo(new Location(595, 205));
        view.drop(drag);
        assertEquals(Position.North, position);
    }

    @Test
    public void dropOnBottomLeft() throws Exception {
        final ViewDrag drag = dragTo(new Location(405, 295));
        view.drop(drag);
        assertEquals(Position.South, position);
    }

    @Test
    public void dropOnBottomRight() throws Exception {
        final ViewDrag drag = dragTo(new Location(595, 295));
        view.drop(drag);
        assertEquals(Position.South, position);
    }

    private ViewDrag dragTo(final Location location) {
        context.checking(new Expectations() {
            {
                exactly(3).of(viewDrag).getSourceView();
                will(returnValue(sourceView));

                atLeast(1).of(viewDrag).getLocation();
                will(returnValue(location));
            }
        });
        return viewDrag;
    }

}
