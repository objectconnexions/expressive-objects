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

import java.util.List;

import uk.co.objectconnexions.expressiveobjects.applib.annotation.Where;
import uk.co.objectconnexions.expressiveobjects.core.commons.debug.DebugBuilder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.collections.modify.CollectionFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.collections.modify.CollectionFacetUtils;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociation;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociationFilters;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.OneToOneAssociation;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Canvas;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.ColorsAndFonts;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Shape;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.icon.SubviewIconSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.list.InternalListSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Axes;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Click;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Content;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ObjectContent;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.SubviewDecorator;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Toolkit;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewAxis;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewRequirement;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.base.AbstractBorder;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.collection.CollectionContent;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.collection.CollectionElement;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.content.FieldContent;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.field.OneToOneFieldImpl;

public class ExpandableViewBorder extends AbstractBorder {
    public static final int CAN_OPEN = 1;
    public static final int CANT_OPEN = 2;
    public static final int UNKNOWN = 0;

    public static class Factory implements SubviewDecorator {
        private final ViewSpecification openObjectViewSpecification;
        private final ViewSpecification closedViewSpecification;
        private final ViewSpecification openCollectionViewSpecification;

        public Factory() {
            this.closedViewSpecification = new SubviewIconSpecification();
            this.openObjectViewSpecification = new InternalFormSpecification();
            this.openCollectionViewSpecification = new InternalListSpecification();
        }

        public Factory(final ViewSpecification closedViewSpecification, final ViewSpecification openObjectViewSpecification, final ViewSpecification openCollectionViewSpecification) {
            this.closedViewSpecification = closedViewSpecification;
            this.openObjectViewSpecification = openObjectViewSpecification;
            this.openCollectionViewSpecification = openCollectionViewSpecification;
        }

        @Override
        public ViewAxis createAxis(final Content content) {
            return null;
        }

        @Override
        public View decorate(final Axes axes, final View view) {
            if (view.getContent().isObject()) {
                return new ExpandableViewBorder(view, closedViewSpecification, openObjectViewSpecification);
            } else if (view.getContent().isCollection()) {
                return new ExpandableViewBorder(view, closedViewSpecification, openCollectionViewSpecification);
            } else {
                return view;
            }
        }
    }

    private boolean isOpen = false;
    private final ViewSpecification openViewSpecification;
    private final ViewSpecification closedViewSpecification;
    private int canOpen;

    // REVIEW: should provide this rendering context, rather than hardcoding.
    // the net effect currently is that class members annotated with 
    // @Hidden(where=Where.ANYWHERE) or @Disabled(where=Where.ANYWHERE) will indeed
    // be hidden/disabled, but will be visible/enabled (perhaps incorrectly) 
    // for any other value for Where
    private final Where where = Where.ANYWHERE;

    public ExpandableViewBorder(final View view, final ViewSpecification closedViewSpecification, final ViewSpecification openViewSpecification) {
        super(view);
        left = Toolkit.defaultFieldHeight();
        this.openViewSpecification = openViewSpecification;
        this.closedViewSpecification = closedViewSpecification;
        canOpen();
    }

    @Override
    protected void debugDetails(final DebugBuilder debug) {
        super.debugDetails(debug);
        debug.appendln("open spec", openViewSpecification);
        debug.appendln("closed spec", closedViewSpecification);
        debug.appendln("open", isOpen);
    }

    @Override
    public void draw(final Canvas canvas) {
        Shape pointer;
        if (isOpen) {
            pointer = new Shape(0, left / 2);
            pointer.addPoint(left - 2 - 2, left / 2);
            pointer.addPoint(left / 2 - 2, left - 2);
        } else {
            pointer = new Shape(2, 2);
            pointer.addPoint(2, left - 2);
            pointer.addPoint(left / 2, 2 + (left - 2) / 2);
        }
        if (canOpen == CAN_OPEN) {
            canvas.drawSolidShape(pointer, Toolkit.getColor(ColorsAndFonts.COLOR_PRIMARY1));
        } else if (canOpen == UNKNOWN) {
            canvas.drawShape(pointer, Toolkit.getColor(ColorsAndFonts.COLOR_PRIMARY1));
        } else {
            canvas.drawShape(pointer, Toolkit.getColor(ColorsAndFonts.COLOR_SECONDARY3));
        }

        super.draw(canvas);
    }

    @Override
    public void firstClick(final Click click) {
        if (click.getLocation().getX() < left) {
            if (canOpen == UNKNOWN) {
                resolveContent();
                markDamaged();
            }
            if (canOpen != CANT_OPEN) {
                isOpen = !isOpen;

                final View parent = wrappedView.getParent();

                getViewManager().removeFromNotificationList(wrappedView);
                if (isOpen) {
                    wrappedView = createOpenView();
                } else {
                    wrappedView = createClosedView();
                }
                setView(this);
                setParent(parent);
                getParent().invalidateLayout();
                canOpen();
            }
        } else {
            super.firstClick(click);
        }
    }

    private View createClosedView() {
        return closedViewSpecification.createView(getContent(), getViewAxes(), -1);
    }

    private View createOpenView() {
        return openViewSpecification.createView(getContent(), getViewAxes(), -1);
    }

    @Override
    public void update(final ObjectAdapter object) {
        super.update(object);
        canOpen();
    }

    private void canOpen() {
        final Content content = getContent();
        if (content.isCollection()) {
            canOpen = canOpenCollection(content);
        } else if (content.isObject()) {
            canOpen = canOpenObject(content);
        }
    }

    private int canOpenCollection(final Content content) {
        final ObjectAdapter collection = ((CollectionContent) content).getCollection();
        if (collection.isGhost()) {
            return UNKNOWN;
        } else {
            final CollectionFacet facet = CollectionFacetUtils.getCollectionFacetFromSpec(collection);
            return facet.size(collection) > 0 ? CAN_OPEN : CANT_OPEN;
        }
    }

    private int canOpenObject(final Content content) {
        final ObjectAdapter object = ((ObjectContent) content).getObject();
        if (object != null) {
            final List<ObjectAssociation> fields = object.getSpecification().getAssociations(ObjectAssociationFilters.dynamicallyVisible(ExpressiveObjectsContext.getAuthenticationSession(), object, where));
            for (int i = 0; i < fields.size(); i++) {
                if (fields.get(i).isOneToManyAssociation()) {
                    return CAN_OPEN;
                } else if (fields.get(i).isOneToOneAssociation() && !fields.get(i).getSpecification().isParseable() && fieldContainsReference(object, fields.get(i))) {
                    return CAN_OPEN;
                }
            }
        }
        final boolean openForObjectsWithOutReferences = true;
        return openForObjectsWithOutReferences ? CAN_OPEN : CANT_OPEN;
    }

    private boolean fieldContainsReference(final ObjectAdapter parent, final ObjectAssociation field) {
        final OneToOneAssociation association = (OneToOneAssociation) field;
        final OneToOneFieldImpl fieldContent = new OneToOneFieldImpl(parent, field.get(parent), association);
        if (openViewSpecification.canDisplay(new ViewRequirement(fieldContent, ViewRequirement.OPEN))) {
            return true;
        }
        return false;
    }

    private void resolveContent() {
        ObjectAdapter parent = getParent().getContent().getAdapter();
        if (!(parent instanceof ObjectAdapter)) {
            parent = getParent().getParent().getContent().getAdapter();
        }

        if (getContent() instanceof FieldContent) {
            final ObjectAssociation field = ((FieldContent) getContent()).getField();
            ExpressiveObjectsContext.getPersistenceSession().resolveField(parent, field);
        } else if (getContent() instanceof CollectionContent) {
            ExpressiveObjectsContext.getPersistenceSession().resolveImmediately(parent);
        } else if (getContent() instanceof CollectionElement) {
            ExpressiveObjectsContext.getPersistenceSession().resolveImmediately(getContent().getAdapter());
        }
    }

}
