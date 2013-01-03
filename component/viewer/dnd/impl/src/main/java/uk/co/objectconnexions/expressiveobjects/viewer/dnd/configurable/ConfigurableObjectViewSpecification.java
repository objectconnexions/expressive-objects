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

import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Axes;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Content;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewRequirement;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.base.Layout;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.border.IconBorder;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.composite.CompositeViewSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.composite.FieldLabelsDecorator;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.composite.GridLayout;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.composite.GridLayoutControlBorder;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.composite.ObjectFieldBuilder;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.composite.StandardFields;

public class ConfigurableObjectViewSpecification extends CompositeViewSpecification {

    public ConfigurableObjectViewSpecification() {
        builder = new ObjectFieldBuilder(new StandardFields());
        addSubviewDecorator(new FieldLabelsDecorator());
        addSubviewDecorator(new ConfigurableFieldBorder.Factory());
        addViewDecorator(new GridLayoutControlBorder.Factory());
        addViewDecorator(new IconBorder.Factory());
    }

    @Override
    public boolean canDisplay(final ViewRequirement requirement) {
        return requirement.isObject() && requirement.isOpen() && requirement.isExpandable() && requirement.isDesign();
    }

    @Override
    public String getName() {
        return "Configurable (experimental)";
    }

    /*
     * protected View decorateView(View view) { return new IconBorder(view); }
     */
    @Override
    public Layout createLayout(final Content content, final Axes axes) {
        return new GridLayout();
    }
}
