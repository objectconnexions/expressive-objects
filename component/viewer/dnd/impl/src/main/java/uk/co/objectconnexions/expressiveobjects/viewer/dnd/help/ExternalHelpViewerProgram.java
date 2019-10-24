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

package uk.co.objectconnexions.expressiveobjects.viewer.dnd.help;

import java.io.IOException;

import org.apache.log4j.Logger;

import uk.co.objectconnexions.expressiveobjects.core.commons.exceptions.ExpressiveObjectsException;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Location;

public class ExternalHelpViewerProgram implements HelpViewer {
    private static final Logger LOG = Logger.getLogger(ExternalHelpViewerProgram.class);
    private final String program;

    public ExternalHelpViewerProgram(final String program) {
        this.program = program;
    }

    @Override
    public void open(final Location location, final String name, final String description, final String help) {
        final String exec = program + " " + help;
        try {
            Runtime.getRuntime().exec(exec);
            LOG.debug("executing '" + exec + "'");
        } catch (final IOException e) {
            throw new ExpressiveObjectsException("faile to execute '" + exec + "'", e);
        }
    }

}
