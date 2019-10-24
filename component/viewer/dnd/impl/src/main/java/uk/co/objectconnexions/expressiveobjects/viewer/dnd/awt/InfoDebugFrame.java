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

package uk.co.objectconnexions.expressiveobjects.viewer.dnd.awt;

import uk.co.objectconnexions.expressiveobjects.core.commons.debug.DebuggableWithTitle;

public class InfoDebugFrame extends DebugFrame {
    private static final long serialVersionUID = 1L;
    private DebuggableWithTitle[] info;

    @Override
    protected DebuggableWithTitle[] getInfo() {
        return info;
    }

    /**
     * set the display strategy to use to display the information.
     */
    public void setInfo(final DebuggableWithTitle info) {
        this.info = new DebuggableWithTitle[] { info };
    }

    /**
     * set the display strategies to use to display the information.
     */
    public void setInfo(final DebuggableWithTitle[] info) {
        this.info = info;
    }

}
