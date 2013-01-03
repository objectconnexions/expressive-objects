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

package uk.co.objectconnexions.expressiveobjects.viewer.dnd.dialog;

import org.apache.log4j.Logger;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.Consent;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.Veto;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.ColorsAndFonts;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Location;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Axes;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.BackgroundTask;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ButtonAction;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Content;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.GlobalViewFactory;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Placement;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Toolkit;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewFactory;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewRequirement;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Workspace;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.action.ActionContent;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.action.BackgroundWork;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.action.ObjectParameter;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.action.ParameterContent;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.action.TextParseableParameter;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.base.Layout;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.border.ButtonBorder;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.border.IconBorder;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.composite.CompositeViewDecorator;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.composite.CompositeViewSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.composite.StackLayout;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.control.AbstractButtonAction;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.control.CancelAction;

public class ActionDialogSpecification extends CompositeViewSpecification {
    private static final Logger LOG = Logger.getLogger(ActionDialogSpecification.class);

    private static class DialogFormSubviews implements ViewFactory {

        @Override
        public View createView(final Content content, final Axes axes, final int fieldNumber) {
            if (content instanceof TextParseableParameter) {
                final GlobalViewFactory factory = Toolkit.getViewFactory();
                return factory.createView(new ViewRequirement(content, ViewRequirement.CLOSED | ViewRequirement.SUBVIEW));
            } else if (content instanceof ObjectParameter) {
                final GlobalViewFactory factory = Toolkit.getViewFactory();
                return factory.createView(new ViewRequirement(content, ViewRequirement.CLOSED | ViewRequirement.SUBVIEW));
            }

            return null;
        }
    }

    private static class ExecuteAction extends AbstractButtonAction {
        public ExecuteAction() {
            this("Apply");
        }

        public ExecuteAction(final String name) {
            super(name, true);
        }

        @Override
        public Consent disabled(final View view) {
            final View[] subviews = view.getSubviews();
            final StringBuffer missingFields = new StringBuffer();
            final StringBuffer invalidFields = new StringBuffer();
            for (final View field : subviews) {
                final ParameterContent content = ((ParameterContent) field.getContent());
                final boolean isEmpty = content.getAdapter() == null;
                if (content.isRequired() && isEmpty) {
                    final String parameterName = content.getParameterName();
                    if (missingFields.length() > 0) {
                        missingFields.append(", ");
                    }
                    missingFields.append(parameterName);

                } else if (field.getState().isInvalid()) {
                    final String parameterName = content.getParameterName();
                    if (invalidFields.length() > 0) {
                        invalidFields.append(", ");
                    }
                    invalidFields.append(parameterName);
                }
            }
            if (missingFields.length() > 0) {
                // TODO: move logic into Facet
                return new Veto(String.format("Fields needed: %s", missingFields));
            }
            if (invalidFields.length() > 0) {
                // TODO: move logic into Facet
                return new Veto(String.format("Invalid fields: %s", invalidFields));
            }

            final ActionContent actionContent = ((ActionContent) view.getContent());
            return actionContent.disabled();
        }

        @Override
        public void execute(final Workspace workspace, final View view, final Location at) {
            final BackgroundTask task = new BackgroundTask() {
                @Override
                public void execute() {
                    final ActionContent actionContent = ((ActionContent) view.getContent());
                    final ObjectAdapter result = actionContent.execute();
                    LOG.debug("action invoked with result " + result);
                    if (result != null) {
                        view.objectActionResult(result, new Placement(view.getAbsoluteLocation()));
                    }
                    view.getViewManager().disposeUnneededViews();
                    view.getFeedbackManager().showMessagesAndWarnings();
                }

                @Override
                public String getName() {
                    return ((ActionContent) view.getContent()).getActionName();
                }

                @Override
                public String getDescription() {
                    return "Running action " + getName() + " on  " + view.getContent().getAdapter();
                }
            };
            LOG.debug("  ... created task " + task);

            BackgroundWork.runTaskInBackground(view, task);
        }

        protected void move(final Location at) {
            at.move(30, 60);
        }
    }

    private static class ExecuteAndCloseAction extends ExecuteAction {
        public ExecuteAndCloseAction() {
            super("OK");
        }

        @Override
        public void execute(final Workspace workspace, final View view, final Location at) {
            LOG.debug("executing action " + this);
            view.dispose();
            LOG.debug("  ... disposed view, now executing");
            super.execute(workspace, view, at);
            view.getViewManager().setKeyboardFocus(workspace);
            // view.getViewManager().clearKeyboardFocus();
        }

        @Override
        protected void move(final Location at) {
        }
    }

    public ActionDialogSpecification() {
        builder = new ActionFieldBuilder(new DialogFormSubviews());
        addSubviewDecorator(new ParametersLabelDecorator());
        addViewDecorator(new CompositeViewDecorator() {
            @Override
            public View decorate(final View view, final Axes axes) {
                // TODO reintroduce the 'Apply' notion, but under control from
                // the method declaration
                final ButtonAction[] actions = new ButtonAction[] { new ExecuteAndCloseAction(), new CancelAction() };
                final ButtonBorder buttonBorder = new ButtonBorder(actions, new IconBorder(view, Toolkit.getText(ColorsAndFonts.TEXT_TITLE_SMALL)));
                buttonBorder.setFocusManager(new ActionDialogFocusManager(buttonBorder));
                return buttonBorder;
            }
        });
    }

    @Override
    public Layout createLayout(final Content content, final Axes axes) {
        return new StackLayout();
    }

    @Override
    public boolean canDisplay(final ViewRequirement requirement) {
        return requirement.getContent() instanceof ActionContent;
    }

    @Override
    public String getName() {
        return "Action Dialog";
    }

}
