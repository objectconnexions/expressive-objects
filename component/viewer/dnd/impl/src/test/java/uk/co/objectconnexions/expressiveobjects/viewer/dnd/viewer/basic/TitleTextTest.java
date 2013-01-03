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

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2;
import uk.co.objectconnexions.expressiveobjects.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.DummyCanvas;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.DummyView;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.TestToolkit;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Color;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.ColorsAndFonts;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Text;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Toolkit;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.text.TitleText;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.viewer.drawing.DummyText;

public class TitleTextTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    private TitleText titleText;
    private String title;
    private TestCanvas canvas;
    private DummyView view;

    @Before
    public void setUp() throws Exception {
        
        TestToolkit.createInstance();

        view = new DummyView();
        final Text style = new DummyText();
        titleText = new TitleText(view, style, Toolkit.getColor(ColorsAndFonts.COLOR_BLACK)) {
            @Override
            protected String title() {
                return title;
            }
        };
        canvas = new TestCanvas();
    }

    // TODO these tests won't work on server that doesn't have graphics - eg a
    // Linux box without X
    /*
     * public void XXtestDrawCanvas() { title = "abcde"; titleText.draw(canvas,
     * 10, 20); }
     * 
     * public void testDrawCanvasDefaultColor() { titleText.draw(canvas, 10,
     * 20); assertEquals(Toolkit.getColor(ColorsAndFonts.COLOR_BLACK),
     * canvas.color); }
     * 
     * public void testDrawCanvasCanDrop() { view.getState().setCanDrop();
     * 
     * titleText.draw(canvas, 10, 20);
     * assertEquals(Toolkit.getColor(ColorsAndFonts.COLOR_VALID), canvas.color);
     * }
     * 
     * public void testDrawCanvasCantDrop() { view.getState().setCantDrop();
     * 
     * titleText.draw(canvas, 10, 20);
     * assertEquals(ColorsAndFonts.COLOR_INVALID, canvas.color); }
     * 
     * public void testDrawCanvasIdentifier() {
     * view.getState().setContentIdentified();
     * 
     * titleText.draw(canvas, 10, 20);
     * assertEquals(Toolkit.getColor("primary1"), canvas.color); }
     */

    @Test
    public void testDrawingLocation() {
        titleText.draw(canvas, 10, 20);
        assertEquals(10, canvas.x);
        assertEquals(20, canvas.y);
    }

    @Test
    public void testDrawingText() {
        title = "test string";
        titleText.draw(canvas, 10, 20);
        assertEquals("test string", canvas.text);
    }

    @Test
    public void testGetSize() {
        title = "abcde";

        assertEquals(10 * 5, titleText.getSize().getWidth());
        assertEquals(8, titleText.getSize().getHeight());
    }

}

class TestCanvas extends DummyCanvas {
    String text;
    int x;
    int y;
    Color color;

    @Override
    public void drawText(final String text, final int x, final int y, final Color color, final Text style) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.color = color;
    }
}
