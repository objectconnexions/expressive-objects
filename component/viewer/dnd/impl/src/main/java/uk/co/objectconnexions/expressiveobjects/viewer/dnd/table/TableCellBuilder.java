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

package uk.co.objectconnexions.expressiveobjects.viewer.dnd.table;

import org.apache.log4j.Logger;

import uk.co.objectconnexions.expressiveobjects.applib.annotation.Where;
import uk.co.objectconnexions.expressiveobjects.core.commons.ensure.Assert;
import uk.co.objectconnexions.expressiveobjects.core.commons.exceptions.UnexpectedCallException;
import uk.co.objectconnexions.expressiveobjects.core.commons.exceptions.UnknownTypeException;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociation;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.OneToManyAssociation;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.OneToOneAssociation;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.booleans.BooleanValueFacet;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.image.ImageValueFacet;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.field.CheckboxField;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Axes;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Content;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.GlobalViewFactory;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ObjectContent;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Toolkit;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewRequirement;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewSpecification;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.base.BlankView;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.base.FieldErrorView;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.composite.AbstractViewBuilder;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.content.TextParseableContent;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.field.OneToOneFieldImpl;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.field.TextParseableFieldImpl;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.viewer.basic.UnlinedTextFieldSpecification;

class TableCellBuilder extends AbstractViewBuilder {
    private static final Logger LOG = Logger.getLogger(TableCellBuilder.class);

    // REVIEW: should provide this rendering context, rather than hardcoding.
    // the net effect currently is that class members annotated with
    // @Hidden(where=Where.ALL_TABLES) will indeed be hidden from all tables
    // but will be shown (perhaps incorrectly) if annotated with Where.PARENTED_TABLE
    // or Where.STANDALONE_TABLE
    private final Where where = Where.ALL_TABLES;
    

    private void addField(final View view, final Axes axes, final ObjectAdapter object, final ObjectAssociation field) {
        final ObjectAdapter value = field.get(object);
        View fieldView;
        fieldView = createFieldView(view, axes, object, field, value);
        if (fieldView != null) {
            view.addView(decorateSubview(axes, fieldView));
        } else {
            view.addView(new FieldErrorView("No field for " + value));
        }
    }

    @Override
    public void build(final View view, final Axes axes) {
        Assert.assertEquals("ensure the view is complete decorated view", view.getView(), view);

        final Content content = view.getContent();
        final ObjectAdapter object = ((ObjectContent) content).getObject();

        if (view.getSubviews().length == 0) {
            initialBuild(object, view, axes);
        } else {
            updateBuild(object, view, axes);
        }
    }

    private void updateBuild(final ObjectAdapter object, final View view, final Axes axes) {
        final TableAxis viewAxis = axes.getAxis(TableAxis.class);

        LOG.debug("update view " + view + " for " + object);
        final View[] subviews = view.getSubviews();
        final ObjectSpecification spec = object.getSpecification();
        for (int i = 0; i < subviews.length; i++) {
            final ObjectAssociation field = fieldFromActualSpec(spec, viewAxis.getFieldForColumn(i));
            final View subview = subviews[i];
            final ObjectAdapter value = field.get(object);

            // if the field is parseable then it may have been modified; we need
            // to replace what was
            // typed in with the actual title.
            if (field.getSpecification().isParseable()) {
                final boolean visiblityChange = !field.isVisible(ExpressiveObjectsContext.getAuthenticationSession(), object, where).isAllowed() ^ (subview instanceof BlankView);
                final ObjectAdapter adapter = subview.getContent().getAdapter();
                final boolean valueChange = value != null && value.getObject() != null && !value.getObject().equals(adapter.getObject());

                if (visiblityChange || valueChange) {
                    final View fieldView = createFieldView(view, axes, object, field, value);
                    view.replaceView(subview, decorateSubview(axes, fieldView));
                }
                subview.refresh();
            } else if (field.isOneToOneAssociation()) {
                final ObjectAdapter existing = ((ObjectContent) subviews[i].getContent()).getObject();
                final boolean changedValue = value != existing;
                if (changedValue) {
                    View fieldView;
                    fieldView = createFieldView(view, axes, object, field, value);
                    if (fieldView != null) {
                        view.replaceView(subview, decorateSubview(axes, fieldView));
                    } else {
                        view.addView(new FieldErrorView("No field for " + value));
                    }
                }
            }
        }
    }

    private ObjectAssociation fieldFromActualSpec(final ObjectSpecification spec, final ObjectAssociation field) {
        final String fieldName = field.getId();
        return spec.getAssociation(fieldName);
    }

    private void initialBuild(final ObjectAdapter object, final View view, final Axes axes) {
        final TableAxis viewAxis = axes.getAxis(TableAxis.class);
        LOG.debug("build view " + view + " for " + object);
        final int len = viewAxis.getColumnCount();
        final ObjectSpecification spec = object.getSpecification();
        for (int f = 0; f < len; f++) {
            if (f > 3) {
                continue;
            }
            final ObjectAssociation field = fieldFromActualSpec(spec, viewAxis.getFieldForColumn(f));
            addField(view, axes, object, field);
        }
    }

    private View createFieldView(final View view, final Axes axes, final ObjectAdapter object, final ObjectAssociation field, final ObjectAdapter value) {
        if (field == null) {
            throw new NullPointerException();
        }
        final GlobalViewFactory factory = Toolkit.getViewFactory();
        ViewSpecification cellSpec;
        Content content;
        if (field instanceof OneToManyAssociation) {
            throw new UnexpectedCallException("no collections allowed");
        } else if (field instanceof OneToOneAssociation) {

            final ObjectSpecification fieldSpecification = field.getSpecification();
            if (fieldSpecification.isParseable()) {
                content = new TextParseableFieldImpl(object, value, (OneToOneAssociation) field);
                // REVIEW how do we deal with IMAGES?
                if (content.getAdapter() instanceof ImageValueFacet) {
                    return new BlankView(content);
                }

                if (!field.isVisible(ExpressiveObjectsContext.getAuthenticationSession(), object, where).isAllowed()) {
                    return new BlankView(content);
                }
                if (((TextParseableContent) content).getNoLines() > 0) {
                    /*
                     * TODO remove this after introducing constraints into view
                     * specs that allow the parent view to specify what kind of
                     * subviews it can deal
                     */

                    if (fieldSpecification.containsFacet(BooleanValueFacet.class)) {
                        cellSpec = new CheckboxField.Specification();
                    } else {
                        cellSpec = new UnlinedTextFieldSpecification();
                    }
                } else {
                    return factory.createView(new ViewRequirement(content, ViewRequirement.CLOSED));
                }
            } else {
                content = new OneToOneFieldImpl(object, value, (OneToOneAssociation) field);
                
                if (!field.isVisible(ExpressiveObjectsContext.getAuthenticationSession(), object, where).isAllowed()) {
                    return new BlankView(content);
                }
                return factory.createView(new ViewRequirement(content, ViewRequirement.CLOSED | ViewRequirement.SUBVIEW));

            }

        } else {
            throw new UnknownTypeException(field);
        }

        return cellSpec.createView(content, axes, -1);
    }

}
