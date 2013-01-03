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


package uk.co.objectconnexions.expressiveobjects.viewer.dnd.value;

import uk.co.objectconnexions.expressiveobjects.object.InvalidEntryException;
import uk.co.objectconnexions.expressiveobjects.object.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.Click;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.Content;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.ObjectContent;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.ViewAxis;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.ViewSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.basic.SimpleIdentifier;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.core.AbstractFieldSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.special.OpenOptionFieldBorder;

import javax.swing.text.html.Option;

public class OptionSelectionField extends TextField {

    private String selected;

    public static class Specification extends AbstractFieldSpecification {
        public boolean canDisplay(ObjectAdapter object) {
            return object.getObject() instanceof Option;
        }
        
        public View createView(Content content, ViewAxis axis) {
            return new SimpleIdentifier(new OptionSelectionFieldBorder(new OptionSelectionField(content, this, axis)));
        }

        public String getName() {
            return "Drop down list";
        }
    }

    public OptionSelectionField(Content content, ViewSpecification specification, ViewAxis axis) {
        super(content, specification, axis, false);
    }
    
    Option getOption() {
        ObjectContent content = ((ObjectContent) getContent());
        Option value = (Option) content.getObject().getObject();

        return value;
    }
    
    void set(String selected) {
        this.selected = selected;
        initiateSave();
    }
    
    protected void save() {
        try {
            parseEntry(selected);
        } catch (InvalidEntryException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

class OptionSelectionFieldBorder extends OpenOptionFieldBorder {

    public OptionSelectionFieldBorder(OptionSelectionField wrappedView) {
        super(wrappedView);
    }

    protected View createOverlay() {
            return new OptionSelectionFieldOverlay((OptionSelectionField) wrappedView);
    }
    
    public void firstClick(Click click) {
        if (canChangeValue()) {
            super.firstClick(click);
        }
    }
}
