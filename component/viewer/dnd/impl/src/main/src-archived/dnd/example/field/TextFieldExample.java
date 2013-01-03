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


package uk.co.objectconnexions.expressiveobjects.viewer.dnd.example.field;

import uk.co.objectconnexions.expressiveobjects.noa.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.noa.adapter.Oid;
import uk.co.objectconnexions.expressiveobjects.noa.adapter.ResolveState;
import uk.co.objectconnexions.expressiveobjects.noa.adapter.Version;
import uk.co.objectconnexions.expressiveobjects.metamodel.facets.actcoll.typeof.TypeOfFacet;
import uk.co.objectconnexions.expressiveobjects.noa.reflect.Allow;
import uk.co.objectconnexions.expressiveobjects.noa.reflect.Consent;
import uk.co.objectconnexions.expressiveobjects.noa.reflect.ObjectAction;
import uk.co.objectconnexions.expressiveobjects.noa.reflect.ObjectActionInstance;
import uk.co.objectconnexions.expressiveobjects.noa.reflect.ObjectActionType;
import uk.co.objectconnexions.expressiveobjects.noa.reflect.OneToManyAssociation;
import uk.co.objectconnexions.expressiveobjects.noa.reflect.OneToManyAssociationInstance;
import uk.co.objectconnexions.expressiveobjects.noa.reflect.OneToOneAssociation;
import uk.co.objectconnexions.expressiveobjects.noa.reflect.OneToOneAssociationInstance;
import uk.co.objectconnexions.expressiveobjects.noa.reflect.listeners.ObjectListener;
import uk.co.objectconnexions.expressiveobjects.noa.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.noa.util.DebugString;
import uk.co.objectconnexions.expressiveobjects.noa.util.NotImplementedException;
import uk.co.objectconnexions.expressiveobjects.nof.core.util.AsString;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.Content;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.TextParseableContent;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.UserActionSet;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.ViewAxis;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.ViewSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.Workspace;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Image;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Location;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Size;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.example.ExampleViewSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.example.view.TestViews;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.field.SingleLineTextField;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.field.WrappedTextField;


class DummyTextParseableField implements TextParseableContent {
    private ObjectAdapter object = new ObjectAdapter() {

        public String asEncodedString(Object object) {
            return null;
        }

        public String getIconName() {
            return null;
        }

        public Object getObject() {
            return null;
        }

        public ObjectSpecification getSpecification() {
            return null;
        }

        public void parseTextEntry(Object original, final String text) {
            DummyTextParseableField.this.text = text;
        }

        public Object restoreFromEncodedString(final String data) {
            return null;
        }

        public String titleString() {
            return text;
        }

        public String toString() {
            AsString str = new AsString(this);
            str.append("text", text);
            return str.toString();
        }

        public void setMask(String mask) {}

        public int defaultTypicalLength() {
            return 0;
        }

        public void addObjectListener(ObjectListener listener) {}

        public void fireChangedEvent() {}

        public ObjectActionInstance getActionInstance(ObjectAction action) {
            return null;
        }

        public ObjectActionInstance[] getActionInstances(ObjectActionType type) {
            return null;
        }

        public OneToManyAssociationInstance getOneToManyAssociation(OneToManyAssociation field) {
            return null;
        }

        public OneToManyAssociationInstance[] getOneToManyAssociationInstances() {
            return null;
        }

        public OneToOneAssociationInstance getOneToOneAssociation(OneToOneAssociation field) {
            return null;
        }

        public OneToOneAssociationInstance[] getOneToOneAssociationInstances() {
            return null;
        }

        public void removeObjectListener(ObjectListener listener) {}

        public void changeState(ResolveState newState) {}

        public void checkLock(Version version) {}

        public Oid getOid() {
            return null;
        }

        public ResolveState getResolveState() {
            return null;
        }

        public Version getVersion() {
            return null;
        }

        public void setOptimisticLock(Version version) {}

        public void replacePojo(Object pojo) {
            throw new NotImplementedException();
        }
        
        public TypeOfFacet getTypeOfFacet() {
            return null;
        }
        
        public void setTypeOfFacet(TypeOfFacet typeOfFacet) {}

    };
    private String text;

    public DummyTextParseableField(final String text) {
        this.text = text;
    }

    public Consent canDrop(final Content sourceContent) {
        return null;
    }

    public void clear() {}

    public void contentMenuOptions(UserActionSet options) {}

    public void debugDetails(final DebugString debug) {}

    public ObjectAdapter drop(final Content sourceContent) {
        return null;
    }

    public void entryComplete() {}

    public String getDescription() {
        return null;
    }

    public String getHelp() {
        return null;
    }

    public String getIconName() {
        return null;
    }

    public Image getIconPicture(int iconHeight) {
        return null;
    }

    public String getId() {
        return null;
    }

    public ObjectAdapter getAdapter() {
        return getObject();
    }

    public ObjectAdapter getObject() {
        return object;
    }

    public int getMaximumLength() {
        return 0;
    }

    public int getTypicalLineLength() {
        return 0;
    }

    public ObjectSpecification getSpecification() {
        return null;
    }

    public boolean isCollection() {
        return false;
    }

    public Consent isEditable() {
        return Allow.DEFAULT;
    }

    public boolean isEmpty() {
        return false;
    }

    public boolean isObject() {
        return false;
    }

    public boolean isPersistable() {
        return false;
    }

    public boolean isTransient() {
        return false;
    }

    public boolean isTextParseable() {
        return true;
    }

    public void parseTextEntry(final String entryText) {}

    public String title() {
        return null;
    }

    public void viewMenuOptions(UserActionSet options) {}

    public String windowTitle() {
        return null;
    }

    public ObjectAdapter[] getOptions() {
        return null;
    }

    public boolean isOptionEnabled() {
        return false;
    }

    public int getNoLines() {
        return 1;
    }

    public boolean canClear() {
        return false;
    }

    public boolean canWrap() {
        return false;
    }

    public String titleString(ObjectAdapter value) {
        return null;
    }

}

public class TextFieldExample extends TestViews {
    private static final String LONG_TEXT = "Expressive Objects - a framework that exposes behaviourally complete business\n"
            + "objects directly to the user. Copyright (C) 2010 Apache Software Foundation\n";

    private static final String SHORT_TEXT = "Short length of text for small field";

    public static void main(final String[] args) {
        new TextFieldExample();
    }

    protected void views(final Workspace workspace) {
        View parent = new ParentView();

        TextParseableContent content = new DummyTextParseableField(SHORT_TEXT);
        ViewSpecification specification = new ExampleViewSpecification();
        ViewAxis axis = null;

        SingleLineTextField textField = new SingleLineTextField(content, specification, axis, true);
        textField.setParent(parent);
        textField.setWidth(200);
        textField.setLocation(new Location(50, 20));
        textField.setSize(textField.getRequiredSize(new Size()));
        workspace.addView(textField);

        textField = new SingleLineTextField(content, specification, axis, false);
        textField.setParent(parent);
        textField.setWidth(80);
        textField.setLocation(new Location(50, 80));
        textField.setSize(textField.getRequiredSize(new Size()));
        workspace.addView(textField);

        content = new DummyTextParseableField(LONG_TEXT);
        WrappedTextField view = new WrappedTextField(content, specification, axis, false);
        view.setParent(parent);
        view.setNoLines(5);
        view.setWidth(200);
        view.setWrapping(false);
        view.setLocation(new Location(50, 140));
        view.setSize(view.getRequiredSize(new Size()));
        workspace.addView(view);

        view = new WrappedTextField(content, specification, axis, true);
        view.setParent(parent);
        view.setNoLines(8);
        view.setWidth(500);
        view.setWrapping(false);
        view.setLocation(new Location(50, 250));
        view.setSize(view.getRequiredSize(new Size()));
        workspace.addView(view);
    }
}

