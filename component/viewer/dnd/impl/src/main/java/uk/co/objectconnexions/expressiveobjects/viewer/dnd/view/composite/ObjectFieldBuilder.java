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

package uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.composite;

import java.util.List;

import org.apache.log4j.Logger;

import uk.co.objectconnexions.expressiveobjects.applib.annotation.Where;
import uk.co.objectconnexions.expressiveobjects.applib.filter.Filter;
import uk.co.objectconnexions.expressiveobjects.core.commons.ensure.Assert;
import uk.co.objectconnexions.expressiveobjects.core.commons.exceptions.UnknownTypeException;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.util.AdapterUtils;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociation;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociationFilters;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Axes;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Content;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ObjectContent;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.SubviewDecorator;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Toolkit;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewFactory;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.base.FieldErrorView;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.content.FieldContent;

public class ObjectFieldBuilder extends AbstractViewBuilder {
    private static final Logger LOG = Logger.getLogger(ObjectFieldBuilder.class);

    // REVIEW: confirm this rendering context
    private final Where where = Where.OBJECT_FORMS;

    private final ViewFactory subviewDesign;

    // TODO remove - transitional
    public ObjectFieldBuilder(final ViewFactory subviewDesign) {
        this.subviewDesign = subviewDesign;
    }

    public ObjectFieldBuilder(final ViewFactory subviewDesign, final SubviewDecorator subviewDecorator) {
        this.subviewDesign = subviewDesign;
        addSubviewDecorator(subviewDecorator);
    }

    @Override
    public void build(final View view, final Axes axes) {
        Assert.assertEquals("ensure the view is the complete decorated view", view.getView(), view);

        final Content content = view.getContent();
        final ObjectAdapter object = ((ObjectContent) content).getObject();

        LOG.debug("build view " + view + " for " + object);

        final ObjectSpecification spec = object.getSpecification();
        final Filter<ObjectAssociation> filter = ObjectAssociationFilters.dynamicallyVisible(ExpressiveObjectsContext.getAuthenticationSession(), object, where);
        final List<ObjectAssociation> flds = spec.getAssociations(filter);

        if (view.getSubviews().length == 0) {
            initialBuild(view, axes, object, flds);
        } else {
            updateBuild(view, axes, object, flds);
        }
    }

    private void initialBuild(final View view, final Axes axes, final ObjectAdapter object, final List<ObjectAssociation> flds) {
        LOG.debug("  as new build");
        // addViewAxes(view);
        for (int f = 0; f < flds.size(); f++) {
            final ObjectAssociation field = flds.get(f);
            addField(view, axes, object, field, f);
        }
    }

    private void addField(final View view, final Axes axes, final ObjectAdapter object, final ObjectAssociation field, final int fieldNumber) {
        final View fieldView = createFieldView(view, axes, object, fieldNumber, field);
        if (fieldView != null) {
            view.addView(decorateSubview(axes, fieldView));
        }
    }

    private void updateBuild(final View view, final Axes axes, final ObjectAdapter object, final List<ObjectAssociation> flds) {
        LOG.debug("  as update build");
        /*
         * 1/ To remove fields: look through views and remove any that don't
         * exists in visible fields
         * 
         * 2/ From remaining views, check for changes as already being done, and
         * replace if needed
         * 
         * 3/ Finally look through fields to see if there is no existing
         * subview; and add one
         */

        View[] subviews = view.getSubviews();

        // remove views for fields that no longer exist
        outer: for (int i = 0; i < subviews.length; i++) {
            final FieldContent fieldContent = ((FieldContent) subviews[i].getContent());

            for (int j = 0; j < flds.size(); j++) {
                final ObjectAssociation field = flds.get(j);
                if (fieldContent.getField() == field) {
                    continue outer;
                }
            }
            view.removeView(subviews[i]);
        }

        // update existing fields if needed
        subviews = view.getSubviews();
        for (int i = 0; i < subviews.length; i++) {
            final View subview = subviews[i];
            final ObjectAssociation field = ((FieldContent) subview.getContent()).getField();
            final ObjectAdapter value = field.get(object);

            if (field.isOneToManyAssociation()) {
                subview.update(value);
            } else if (field.isOneToOneAssociation()) {
                final ObjectAdapter existing = subview.getContent().getAdapter();

                // if the field is parseable then it may have been modified; we
                // need to replace what was
                // typed in with the actual title.
                if (!field.getSpecification().isParseable()) {
                    final boolean changedValue = value != existing;
                    final boolean isDestroyed = existing != null && existing.isDestroyed();
                    if (changedValue || isDestroyed) {
                        View fieldView;
                        fieldView = createFieldView(view, axes, object, i, field);
                        if (fieldView != null) {
                            view.replaceView(subview, decorateSubview(axes, fieldView));
                        } else {
                            view.addView(new FieldErrorView("No field for " + value));
                        }
                    }
                } else {
                    if (AdapterUtils.exists(value) && !AdapterUtils.wrappedEqual(value, existing)) {
                        final View fieldView = createFieldView(view, axes, object, i, field);
                        view.replaceView(subview, decorateSubview(axes, fieldView));
                    } else {
                        subview.refresh();
                    }
                }
            } else {
                throw new UnknownTypeException(field.getName());
            }
        }

        // add new fields
        outer2: for (int j = 0; j < flds.size(); j++) {
            final ObjectAssociation field = flds.get(j);
            for (int i = 0; i < subviews.length; i++) {
                final FieldContent fieldContent = ((FieldContent) subviews[i].getContent());
                if (fieldContent.getField() == field) {
                    continue outer2;
                }
            }
            addField(view, axes, object, field, j);
        }
    }

    private View createFieldView(final View view, final Axes axes, final ObjectAdapter object, final int fieldNumber, final ObjectAssociation field) {
        if (field == null) {
            throw new NullPointerException();
        }

        if (field.isOneToOneAssociation()) {
            ExpressiveObjectsContext.getPersistenceSession().resolveField(object, field);
        }

        final Content content1 = Toolkit.getContentFactory().createFieldContent(field, object);
        final View fieldView = subviewDesign.createView(content1, axes, fieldNumber);
        return fieldView;
    }

}
