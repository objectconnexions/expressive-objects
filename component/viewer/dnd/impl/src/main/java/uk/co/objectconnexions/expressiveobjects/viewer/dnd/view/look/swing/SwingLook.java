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

package uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.look.swing;

import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Look;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.border.ScrollBorder;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.border.ViewResizeBorder;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.control.Button;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.window.AbstractWindowBorder;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.window.CloseWindowControl;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.window.IconizeWindowControl;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.window.ResizeWindowControl;

public class SwingLook implements Look {

    @Override
    public void install() {
        Button.setButtonRender(new Button3DStyleRender());
        AbstractWindowBorder.setBorderRenderer(new SwingStyleWindowBorder());
        CloseWindowControl.setRender(new CloseWindow3DRender());
        ResizeWindowControl.setRender(new ResizeWindow3DRender());
        IconizeWindowControl.setRender(new IconizeWindow3DRender());
        ViewResizeBorder.setRender(new ResizeView3DRender());
        ScrollBorder.setRender(new ScrollBar3DRender());
    }

    @Override
    public String getName() {
        return "Swing";
    }

}
