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
import uk.co.objectconnexions.expressiveobjects.core.metamodel.util.Dump;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;

public class DebugObjectGraph implements DebuggableWithTitle {
    private final ObjectAdapter object;

    public DebugObjectGraph(final ObjectAdapter object) {
        this.object = object;
    }

    @Override
    public void debugData(final DebugBuilder debug) {
        dumpGraph(object, debug);
    }

    @Override
    public String debugTitle() {
        return "Object Graph";
    }

    private void dumpGraph(final ObjectAdapter object, final DebugBuilder info) {
        if (object != null) {
            Dump.graph(object, ExpressiveObjectsContext.getAuthenticationSession(), info);
        }
    }
}
