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

package uk.co.objectconnexions.expressiveobjects.viewer.dnd;

import junit.framework.TestCase;

import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Location;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.interaction.SimpleInternalDrag;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.InternalDrag;

public class InternalDragTest extends TestCase {
    public static void main(final String[] args) {
        junit.textui.TestRunner.run(InternalDragTest.class);
    }

    public void testDragStart() {
        final DummyView view = new DummyView();
        view.setupAbsoluteLocation(new Location(30, 60));

        final InternalDrag id = new SimpleInternalDrag(view, new Location(100, 110));
        assertEquals(new Location(70, 50), id.getLocation());

        // id.drag(null, new Location(110, 130), 0);
        // assertEquals(new Location(80, 70), id.getLocation());
    }
}
