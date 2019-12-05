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

package uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing;

/**
 * A look-up for font and color details.
 * 
 */
public interface ColorsAndFonts {
    public final static String COLOR_BLACK = "black";
    public final static String COLOR_WHITE = "white";
    public final static String COLOR_PRIMARY1 = "primary1";
    public final static String COLOR_PRIMARY2 = "primary2";
    public final static String COLOR_PRIMARY3 = "primary3";
    public final static String COLOR_SECONDARY1 = "secondary1";
    public final static String COLOR_SECONDARY2 = "secondary2";
    public final static String COLOR_SECONDARY3 = "secondary3";

    // background colors
    public final static String COLOR_APPLICATION = "background.application";
    public final static String COLOR_WINDOW = "background.window";
    public final static String COLOR_MENU_VALUE = "background.menu.value";
    public final static String COLOR_MENU_CONTENT = "background.menu.content";
    public final static String COLOR_MENU_VIEW = "background.menu.view";
    public final static String COLOR_MENU_WORKSPACE = "background.menu.workspace";

    // menu colors
    public final static String COLOR_MENU = "menu.normal";
    public final static String COLOR_MENU_DISABLED = "menu.disabled";
    public final static String COLOR_MENU_REVERSED = "menu.reversed";

    // label colors
    public final static String COLOR_LABEL = "label.normal";
    public final static String COLOR_LABEL_DISABLED = "label.disabled";
    public final static String COLOR_LABEL_MANDATORY = "label.mandatory";

    // state colors
    public final static String COLOR_IDENTIFIED = "identified";
    public final static String COLOR_VALID = "valid";
    public final static String COLOR_INVALID = "invalid";
    public final static String COLOR_ERROR = "error";
    public final static String COLOR_ACTIVE = "active";
    public final static String COLOR_OUT_OF_SYNC = "out-of-sync";

    // text colors
    public final static String COLOR_TEXT_SAVED = "text.saved";
    public final static String COLOR_TEXT_EDIT = "text.edit";
    public final static String COLOR_TEXT_CURSOR = "text.cursor";
    public final static String COLOR_TEXT_HIGHLIGHT = "text.highlight";

    // debug outline colors
    public final static String COLOR_DEBUG_BASELINE = "debug.baseline";
    public final static String COLOR_DEBUG_BOUNDS_BORDER = "debug.bounds.border";
    public final static String COLOR_DEBUG_BOUNDS_DRAW = "debug.bounds.draw";
    public final static String COLOR_DEBUG_BOUNDS_REPAINT = "debug.bounds.repaint";
    public final static String COLOR_DEBUG_BOUNDS_VIEW = "debug.bounds.view";

    // fonts
    public final static String TEXT_DEFAULT = "text.default";
    public final static String TEXT_CONTROL = "text.control";
    public final static String TEXT_TITLE = "text.title";
    public final static String TEXT_TITLE_SMALL = "text.title.small";
    public final static String TEXT_DEBUG = "text.debug";
    public final static String TEXT_STATUS = "text.status";
    public final static String TEXT_ICON = "text.icon";
    public final static String TEXT_LABEL = "text.label";
    public final static String TEXT_LABEL_MANDATORY = "text.label.mandatory";
    public final static String TEXT_LABEL_DISABLED = "text.label.disabled";
    public final static String TEXT_MENU = "text.menu";
    public final static String TEXT_NORMAL = "text.normal";

    int defaultBaseline();

    int defaultFieldHeight();

    Color getColor(int rgbColor);

    Color getColor(String name);

    Text getText(String name);

    void init();

	Object getAntiAliasing();

}
