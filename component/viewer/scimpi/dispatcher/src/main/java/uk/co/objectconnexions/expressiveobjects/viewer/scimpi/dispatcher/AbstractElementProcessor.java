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

package uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher;

import uk.co.objectconnexions.expressiveobjects.core.commons.config.ConfigurationConstants;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.viewer.scimpi.dispatcher.processor.Request;

public abstract class AbstractElementProcessor implements ElementProcessor, Names {

    private static final String SHOW_ICONS_BY_DEFAULT = ConfigurationConstants.ROOT + "scimpi.show-icons";

    private final boolean showIconByDefault;

    public AbstractElementProcessor() {
        showIconByDefault = ExpressiveObjectsContext.getConfiguration().getBoolean(SHOW_ICONS_BY_DEFAULT, false);
    }

    /**
     * Return the Class for the class specified in the type attribute.
     */
    protected Class<?> forClass(final Request request) {
        Class<?> cls = null;
        final String className = request.getOptionalProperty(TYPE);
        if (className != null) {
            try {
                cls = Class.forName(className);
            } catch (final ClassNotFoundException e) {
                throw new ScimpiException("No class for " + className, e);
            }
        }
        return cls;
    }

    protected boolean showIconByDefault() {
        return showIconByDefault;
    }
}
