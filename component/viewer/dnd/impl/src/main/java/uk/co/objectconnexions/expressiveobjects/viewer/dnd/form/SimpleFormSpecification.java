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

package uk.co.objectconnexions.expressiveobjects.viewer.dnd.form;

import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Color;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.ColorsAndFonts;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Axes;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Toolkit;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewFactory;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewRequirement;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.border.BackgroundBorder;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.border.EmptyBorder;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.border.IconBorder;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.composite.CompositeViewDecorator;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.composite.StandardFields;

public class SimpleFormSpecification extends AbstractFormSpecification {

    @Override
    public boolean canDisplay(final ViewRequirement requirement) {
        return requirement.isObject() && !requirement.isTextParseable() && requirement.hasReference()
        	 && requirement.isOpen() && requirement.isSubview() && requirement.isFixed();
    }

    @Override
    protected void init() {
        addViewDecorator(new IconBorder.Factory());
        addViewDecorator(new CompositeViewDecorator() {
            @Override
            public View decorate(final View view, final Axes axes) {
                Color color = Toolkit.getColor(ColorsAndFonts.COLOR_ACTIVE);
				return new BackgroundBorder(color, new EmptyBorder(3, new EmptyBorder(6, view)));
            }
        });
    }

    @Override
    protected ViewFactory createFieldFactory() {
        return new StandardFields();
    }

    @Override
    public String getName() {
        return "Summary Form";
    }

}
