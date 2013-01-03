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

import uk.co.objectconnexions.expressiveobjects.core.runtime.userprofile.Options;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.util.Properties;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Axes;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Content;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewRequirement;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.ViewSpecification;

public class UserViewSpecification implements ViewSpecification {

    private final ViewSpecification specification;
    private final String name;

    public UserViewSpecification(final ViewSpecification specification, final String name) {
        this.specification = specification;
        this.name = name;
    }

    /*
     * public UserViewSpecification(View view) { specification =
     * view.getSpecification(); Options copyOptions = new Options();
     * view.saveOptions(copyOptions); name = specification.getName() + " " + new
     * Date().getSeconds();
     * 
     * // view.setSpecification(this); // view.loadOptions(copyOptions); }
     */
    @Override
    public boolean canDisplay(final ViewRequirement requirement) {
        return specification.canDisplay(requirement);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isAligned() {
        return specification.isAligned();
    }

    @Override
    public boolean isOpen() {
        return specification.isOpen();
    }

    @Override
    public boolean isReplaceable() {
        return specification.isReplaceable();
    }

    @Override
    public boolean isResizeable() {
        return specification.isResizeable();
    }

    @Override
    public boolean isSubView() {
        return specification.isSubView();
    }

    @Override
    public View createView(final Content content, final Axes axes, final int sequence) {
        final View createView = specification.createView(content, axes, sequence);

        final Options viewOptions = Properties.getViewConfigurationOptions(this);
        createView.loadOptions(viewOptions);
        return createView;
    }

    public ViewSpecification getWrappedSpecification() {
        return specification;
    }

}
