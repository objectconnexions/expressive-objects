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

package uk.co.objectconnexions.expressiveobjects.viewer.html.servlet;

import javax.servlet.http.HttpServlet;

import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSession;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.AuthenticationManager;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.DeploymentType;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.viewer.html.PathBuilder;
import uk.co.objectconnexions.expressiveobjects.viewer.html.PathBuilderDefault;
import uk.co.objectconnexions.expressiveobjects.viewer.html.component.html.HtmlComponentFactory;

public abstract class AbstractHtmlViewerServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private PathBuilder pathBuilder;
    private HtmlComponentFactory componentFactory;

    protected HtmlComponentFactory getHtmlComponentFactory() {
        if(componentFactory == null) {
            componentFactory = new HtmlComponentFactory(getPathBuilder());
        }
        return componentFactory;
    }

    protected PathBuilder getPathBuilder() {
        if (pathBuilder != null) {
            return pathBuilder;
        }
        return pathBuilder = new PathBuilderDefault(getServletContext());
    }


    /**
     * Convenience.
     */
    protected String pathTo(final String prefix) {
        return getPathBuilder().pathTo(prefix);
    }

    // //////////////////////////////////////////////////////////////
    // Dependencies (from context)
    // //////////////////////////////////////////////////////////////

    protected AuthenticationSession getAuthenticationSession() {
        return ExpressiveObjectsContext.getAuthenticationSession();
    }

    protected ExpressiveObjectsConfiguration getConfiguration() {
        return ExpressiveObjectsContext.getConfiguration();
    }

    protected AuthenticationManager getAuthenticationManager() {
        return ExpressiveObjectsContext.getAuthenticationManager();
    }

    protected DeploymentType getDeploymentType() {
        return ExpressiveObjectsContext.getDeploymentType();
    }

}
