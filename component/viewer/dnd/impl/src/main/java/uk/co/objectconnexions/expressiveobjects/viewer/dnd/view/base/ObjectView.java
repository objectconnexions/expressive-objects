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

package uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.base;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.Consent;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ActionType;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.Persistor;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Location;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Offset;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.interaction.ViewDragImpl;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Click;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Content;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ContentDrag;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.DragEvent;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.DragStart;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Placement;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Toolkit;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.UserActionSet;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Workspace;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.option.UserActionAbstract;

public abstract class ObjectView extends AbstractView {

    public ObjectView(final Content content, final ViewSpecification specification) {
        super(content, specification);
    }

    @Override
    public void dragIn(final ContentDrag drag) {
        final Consent consent = getContent().canDrop(drag.getSourceContent());
        final String description = getContent().getDescription();
        if (consent.isAllowed()) {
            getFeedbackManager().setAction(consent.getDescription() + " " + description);
            getState().setCanDrop();
        } else {
            getFeedbackManager().setAction(consent.getReason() + " " + description);
            getState().setCantDrop();
        }
        markDamaged();
    }

    @Override
    public void dragOut(final ContentDrag drag) {
        getState().clearObjectIdentified();
        markDamaged();
    }

    @Override
    public DragEvent dragStart(final DragStart drag) {
        final View subview = subviewFor(drag.getLocation());
        if (subview != null) {
            drag.subtract(subview.getLocation());
            return subview.dragStart(drag);
        } else {
            if (drag.isCtrl()) {
                final View dragOverlay = new DragViewOutline(getView());
                return new ViewDragImpl(this, new Offset(drag.getLocation()), dragOverlay);
            } else {
                return Toolkit.getViewFactory().createDragContentOutline(this, drag.getLocation());
            }
        }
    }

    /**
     * Called when a dragged object is dropped onto this view. The default
     * behaviour implemented here calls the action method on the target, passing
     * the source object in as the only parameter.
     */
    @Override
    public void drop(final ContentDrag drag) {
        final ObjectAdapter result = getContent().drop(drag.getSourceContent());
        if (result != null) {
            objectActionResult(result, new Placement(this));
        }
        Workspace workspace = getWorkspace();
        workspace.notifyViewsFor(drag.getSourceContent().getAdapter());
        workspace.notifyViewsFor(drag.getTargetView().getContent().getAdapter());
        workspace.notifyViewsFor(result);
        getState().clearObjectIdentified();
        getFeedbackManager().showMessagesAndWarnings();

        markDamaged();
    }

    @Override
    public void firstClick(final Click click) {
        final View subview = subviewFor(click.getLocation());
        if (subview != null) {
            click.subtract(subview.getLocation());
            subview.firstClick(click);
        } else {
            if (click.button2()) {
                final Location location = new Location(click.getLocationWithinViewer());
                getViewManager().showInOverlay(getContent(), location);
            }
        }
    }

    @Override
    public void invalidateContent() {
        super.invalidateLayout();
    }

    @Override
    public void secondClick(final Click click) {
        final View subview = subviewFor(click.getLocation());
        if (subview != null) {
            click.subtract(subview.getLocation());
            subview.secondClick(click);
        } else {
            final Location location = getAbsoluteLocation();
            location.translate(click.getLocation());
            getWorkspace().addWindowFor(getContent().getAdapter(), new Placement(this));
        }
    }


    // ///////////////////////////////////////////////////////////////////////////
    // Dependencies (from context)
    // ///////////////////////////////////////////////////////////////////////////

    private static Persistor getPersistenceSession() {
        return ExpressiveObjectsContext.getPersistenceSession();
    }

}
