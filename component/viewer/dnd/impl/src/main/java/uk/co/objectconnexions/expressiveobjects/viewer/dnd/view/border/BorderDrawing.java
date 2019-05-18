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

package uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.border;

import uk.co.objectconnexions.expressiveobjects.core.commons.debug.DebugBuilder;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Canvas;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Size;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewState;

public interface BorderDrawing {

    public abstract void debugDetails(final DebugBuilder debug);

    public abstract void layoutControls(final Size size, View[] controls);

    public abstract void draw(final Canvas canvas, Size s, boolean hasFocus, final ViewState state, View[] controls, String title);

    public abstract void drawTransientMarker(Canvas canvas, Size size);

    public abstract void getRequiredSize(Size size, String title, View[] controls);

    /**
     * Return the width required to accommodate the left border.
     */
    public abstract int getLeft();

    /**
     * Return the width required to accommodate the right border.
     */
    public abstract int getRight();

    /**
     * Return the width required to accommodate the top border.
     */
    public abstract int getTop();

    /**
     * Return the width required to accommodate the bottom border.
     */
    public abstract int getBottom();

}
