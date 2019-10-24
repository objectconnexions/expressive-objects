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

package uk.co.objectconnexions.expressiveobjects.core.runtime.userprofile;

import uk.co.objectconnexions.expressiveobjects.core.commons.debug.DebugBuilder;
import uk.co.objectconnexions.expressiveobjects.core.commons.debug.DebuggableWithTitle;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.mgr.AdapterManager;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.services.ServiceUtil;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.PersistenceSession;
import uk.co.objectconnexions.expressiveobjects.core.runtime.userprofile.PerspectiveEntry;
import uk.co.objectconnexions.expressiveobjects.core.runtime.userprofile.UserProfile;

/**
 * Introduced in order to remove the dependency between {@link UserProfile} and
 * {@link PerspectiveEntry} on <tt>runtimes:dflt</tt>.
 */
public final class UserProfilesDebugUtil {

    public static DebuggableWithTitle asDebuggableWithTitle(final UserProfile userProfile) {
        return new DebuggableWithTitle() {

            @Override
            public void debugData(final DebugBuilder debug) {
                debug.appendTitle("Options");
                debug.indent();
                debug.append(userProfile.getOptions());
                debug.unindent();

                debug.appendTitle("Perspectives");
                for (final PerspectiveEntry entry : userProfile.getEntries()) {
                    asDebuggableWithTitle(entry).debugData(debug);
                }
            }

            @Override
            public String debugTitle() {
                return toString();
            }
        };
    }

    public static DebuggableWithTitle asDebuggableWithTitle(final PerspectiveEntry perspectiveEntry) {
        return new DebuggableWithTitle() {

            @Override
            public void debugData(final DebugBuilder debug) {
                debug.appendln("Name", perspectiveEntry.getName());
                debug.blankLine();
                debug.appendTitle("Services (Ids)");
                debug.indent();
                for (final Object service : perspectiveEntry.getServices()) {
                    debug.appendln(ServiceUtil.id(service));
                }
                debug.unindent();

                debug.blankLine();
                debug.appendTitle("Objects");
                debug.indent();
                final AdapterManager adapterManager = getAdapterManager();
                for (final Object obj : perspectiveEntry.getObjects()) {
                    debug.appendln(adapterManager.adapterFor(obj).toString());
                }
                debug.unindent();
            }

            @Override
            public String debugTitle() {
                return "Perspective";
            }
        };
    }

    // ///////////////////////////////////////////////////////
    // Dependencies (from Context)
    // ///////////////////////////////////////////////////////

    protected static AdapterManager getAdapterManager() {
        return getPersistenceSession().getAdapterManager();
    }

    protected static PersistenceSession getPersistenceSession() {
        return ExpressiveObjectsContext.getPersistenceSession();
    }

}
