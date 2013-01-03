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

package uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.debug;

import uk.co.objectconnexions.expressiveobjects.core.commons.debug.DebugBuilder;
import uk.co.objectconnexions.expressiveobjects.core.commons.debug.DebuggableWithTitle;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.util.Dump;

public class DebugObjectSpecification implements DebuggableWithTitle {
    private final ObjectSpecification specification;

    public DebugObjectSpecification(final ObjectAdapter object) {
        this.specification = object.getSpecification();
    }

    public DebugObjectSpecification(final ObjectSpecification object) {
        this.specification = object;
    }

    @Override
    public void debugData(final DebugBuilder debug) {
        if (specification == null) {
            debug.appendln("no specfication");
        } else {
            Dump.specification(specification, debug);
        }
    }

    @Override
    public String debugTitle() {
        return "Object Specification";
    }

}
