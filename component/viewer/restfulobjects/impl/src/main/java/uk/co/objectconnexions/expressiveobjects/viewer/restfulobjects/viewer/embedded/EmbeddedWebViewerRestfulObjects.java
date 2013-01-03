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
package uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.embedded;

import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.jboss.resteasy.plugins.server.servlet.ResteasyBootstrap;

import uk.co.objectconnexions.expressiveobjects.core.commons.lang.MapUtils;
import uk.co.objectconnexions.expressiveobjects.core.runtime.viewer.web.WebAppSpecification;
import uk.co.objectconnexions.expressiveobjects.core.runtime.web.EmbeddedWebViewer;
import uk.co.objectconnexions.expressiveobjects.core.webapp.ExpressiveObjectsSessionFilter;
import uk.co.objectconnexions.expressiveobjects.core.webapp.ExpressiveObjectsWebAppBootstrapper;
import uk.co.objectconnexions.expressiveobjects.core.webapp.content.ResourceCachingFilter;
import uk.co.objectconnexions.expressiveobjects.core.webapp.content.ResourceServlet;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.RestfulObjectsApplication;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.authentication.AuthenticationSessionStrategyTrusted;

final class EmbeddedWebViewerRestfulObjects extends EmbeddedWebViewer {
    @Override
    public WebAppSpecification getWebAppSpecification() {
        final WebAppSpecification webAppSpec = new WebAppSpecification();

        webAppSpec.addServletContextListener(ExpressiveObjectsWebAppBootstrapper.class);
        
        webAppSpec.addContextParams("expressive-objects.viewers", "restfulobjects");

        webAppSpec.addContextParams(RestfulObjectsViewerInstaller.JAVAX_WS_RS_APPLICATION, RestfulObjectsApplication.class.getName());

        webAppSpec.addFilterSpecification(ExpressiveObjectsSessionFilter.class, 
                MapUtils.asMap(
                        ExpressiveObjectsSessionFilter.AUTHENTICATION_SESSION_STRATEGY_KEY, AuthenticationSessionStrategyTrusted.class.getName(),
                        ExpressiveObjectsSessionFilter.WHEN_NO_SESSION_KEY, ExpressiveObjectsSessionFilter.WhenNoSession.CONTINUE.name().toLowerCase()), 
                RestfulObjectsViewerInstaller.EVERYTHING);

        webAppSpec.addFilterSpecification(ResourceCachingFilter.class, RestfulObjectsViewerInstaller.STATIC_CONTENT);
        webAppSpec.addServletSpecification(ResourceServlet.class, RestfulObjectsViewerInstaller.STATIC_CONTENT);

        
        webAppSpec.addServletContextListener(ResteasyBootstrap.class);
        webAppSpec.addServletSpecification(HttpServletDispatcher.class, RestfulObjectsViewerInstaller.ROOT);


        return webAppSpec;
    }
}