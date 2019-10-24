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

import org.apache.log4j.Logger;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.Allow;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.Consent;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.Veto;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.Persistor;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Location;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ButtonAction;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Content;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Workspace;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.content.FieldContent;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.content.RootObject;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.control.AbstractButtonAction;

public class SaveTransientObjectBorder extends ButtonBorder {
    private static final Logger LOG = Logger.getLogger(SaveTransientObjectBorder.class);

    private static class CloseAction extends AbstractButtonAction {
        public CloseAction() {
            super("Discard");
        }

        @Override
        public void execute(final Workspace workspace, final View view, final Location at) {
            close(workspace, view);
        }
    }

    private static class SaveAction extends AbstractButtonAction {
        public SaveAction() {
            super("Save");
        }

        @Override
        public Consent disabled(final View view) {
            return canSave(view);
        }

        @Override
        public void execute(final Workspace workspace, final View view, final Location at) {
            save(view);
            // by recreating the view the transient border is removed
            final ViewSpecification spec = view.getSpecification();
            final View newView = spec.createView(view.getContent(), view.getViewAxes(), -1);
            workspace.replaceView(view, newView);
        }
    }

    private static Consent canSave(final View view) {

        final ObjectAdapter transientNO = view.getContent().getAdapter();

        // check each of the fields, and capture invalid state if known
        final SaveState saveState = new SaveState();
        checkFields(saveState, view, transientNO);
        final StringBuilder errorBuf = new StringBuilder(saveState.getMessage());

        final ObjectSpecification viewContentSpec = view.getContent().getSpecification();
        final Consent consent = viewContentSpec.isValid(transientNO);
        if (consent.isVetoed()) {
            if (errorBuf.length() > 0) {
                errorBuf.append("; ");
            }
            errorBuf.append(consent.getReason());
        }

        if (errorBuf.length() == 0) {
            return Allow.DEFAULT;
        } else {
            return new Veto(errorBuf.toString());
        }
    }

    private static void checkFields(final SaveState saveState, final View view, final ObjectAdapter forObject) {
        if (view.getContent().getAdapter() != forObject) {
            return;
        }

        final View[] subviews = view.getSubviews();
        for (final View fieldView : subviews) {
            final Content content = fieldView.getContent();
            if (content instanceof RootObject) {
                checkFields(saveState, fieldView, forObject);
            } else if (content instanceof FieldContent) {
                final boolean isMandatory = ((FieldContent) content).isMandatory();
                final boolean isEditable = ((FieldContent) content).isEditable().isAllowed();
                final ObjectAdapter field = content.getAdapter();
                final boolean isFieldEmpty = field == null;
                if (isMandatory && isEditable && isFieldEmpty) {
                    final String parameterName = ((FieldContent) content).getFieldName();
                    saveState.addMissingField(parameterName);

                } else if (fieldView.getState().isInvalid()) {
                    final String parameterName = ((FieldContent) content).getFieldName();
                    saveState.addInvalidField(parameterName);
                }
            }
        }
    }

    private static class SaveAndCloseAction extends AbstractButtonAction {
        public SaveAndCloseAction() {
            super("Save & Close");
        }

        @Override
        public Consent disabled(final View view) {
            return canSave(view);
        }

        @Override
        public void execute(final Workspace workspace, final View view, final Location at) {
            save(view);
            close(workspace, view);
        }
    }

    private static void close(final Workspace workspace, final View view) {
        view.dispose();
    }

    private static ObjectAdapter save(final View view) {
        final ObjectAdapter transientObject = view.getContent().getAdapter();
        try {
            getPersistenceSession().makePersistent(transientObject);
        } catch (final RuntimeException e) {
            LOG.info("exception saving " + transientObject + ", aborting transaction" + e.getMessage());
            throw e;
        }
        return transientObject;
    }

    // /////////////////////////////////////////////////////
    // Constructor
    // /////////////////////////////////////////////////////

    public SaveTransientObjectBorder(final View view) {
        super(new ButtonAction[] { new SaveAction(), new SaveAndCloseAction(), new CloseAction(), }, view);
    }

    // /////////////////////////////////////////////////////
    // Dependencies (from context)
    // /////////////////////////////////////////////////////

    private static Persistor getPersistenceSession() {
        return ExpressiveObjectsContext.getPersistenceSession();
    }

}
