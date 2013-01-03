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

import static uk.co.objectconnexions.expressiveobjects.core.commons.lang.CastUtils.enumerationOver;

import java.util.Enumeration;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.Allow;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.Consent;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.Veto;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ActionType;
import uk.co.objectconnexions.expressiveobjects.core.runtime.logging.SnapshotAppender;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.drawing.Location;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.View;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.Workspace;
import uk.co.objectconnexions.expressiveobjects.viewer.dnd.view.option.UserActionAbstract;

/**
 * Display debug window
 */
public class DebugDumpSnapshotOption extends UserActionAbstract {
    public DebugDumpSnapshotOption() {
        super("Dump log snapshot", ActionType.DEBUG);
    }

    @Override
    public Consent disabled(final View component) {
        final Enumeration<Logger> enumeration = enumerationOver(Logger.getRootLogger().getAllAppenders(), Logger.class);
        while (enumeration.hasMoreElements()) {
            final Appender appender = (Appender) enumeration.nextElement();
            if (appender instanceof SnapshotAppender) {
                return Allow.DEFAULT;
            }
        }
        // TODO: move logic into Facet
        return new Veto("No available snapshot appender");
    }

    @Override
    public void execute(final Workspace workspace, final View view, final Location at) {
        final Enumeration<Logger> enumeration = enumerationOver(Logger.getRootLogger().getAllAppenders(), Logger.class);
        while (enumeration.hasMoreElements()) {
            final Appender appender = (Appender) enumeration.nextElement();
            if (appender instanceof SnapshotAppender) {
                ((SnapshotAppender) appender).forceSnapshot();
            }
        }
    }

    @Override
    public String getDescription(final View view) {
        return "Force a snapshot of the log";
    }
}
