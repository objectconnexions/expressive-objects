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

package uk.co.objectconnexions.expressiveobjects.viewer.dnd.field;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.Consent;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.Veto;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Location;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Workspace;

public class ClearValueOption extends AbstractValueOption {

    public ClearValueOption(final AbstractField field) {
        super(field, "Clear");
    }

    @Override
    public String getDescription(final View view) {
        return "Clear field";
    }

    @Override
    public Consent disabled(final View view) {
        final ObjectAdapter value = getValue(view);
        final Consent consent = view.canChangeValue();
        if (consent.isVetoed()) {
            return consent;
        }
        final Consent canClear = field.canClear();
        if (canClear.isVetoed()) {
            // TODO: move logic into Facets.
            return new Veto(String.format("Can't clear %s values", value.getSpecification().getShortIdentifier()));
        }
        if (value == null || isEmpty(view)) {
            // TODO: move logic into Facets.
            return new Veto("Field is already empty");
        }
        // TODO: move logic into Facets.
        return consent.setDescription(String.format("Clear value ", value.titleString()));
    }

    @Override
    public void execute(final Workspace frame, final View view, final Location at) {
        field.clear();
    }

    @Override
    public String toString() {
        return "ClearValueOption";
    }
}
