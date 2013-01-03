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

import uk.co.objectconnexions.expressiveobjects.ExpressiveObjects;
import uk.co.objectconnexions.expressiveobjects.core.runtime.installerregistry.installerapi.ExpressiveObjectsViewerInstallerAbstract;
import uk.co.objectconnexions.expressiveobjects.core.runtime.viewer.ExpressiveObjectsViewer;
import uk.co.objectconnexions.expressiveobjects.core.runtime.viewer.web.WebAppSpecification;

/**
 * Convenience implementation of a {@link ExpressiveObjectsViewer} providing the ability to
 * configured for the JSON viewer from the {@link Expressive Objects command line} using
 * <tt>--viewer restful</tt> command line option.
 * 
 * <p>
 * In a production deployment the configuration represented by the
 * {@link WebAppSpecification} would be specified in the <tt>web.xml<tt> file.
 */
public class RestfulObjectsViewerInstaller extends ExpressiveObjectsViewerInstallerAbstract {

    static final String JAVAX_WS_RS_APPLICATION = "javax.ws.rs.Application";

    protected static final String EVERYTHING = "*";
    protected static final String ROOT = "/";
    protected static final String[] STATIC_CONTENT = new String[] { "*.js", "*.gif", "*.png", "*.html" };

    public RestfulObjectsViewerInstaller() {
        super("restfulobjects");
    }

    @Override
    protected ExpressiveObjectsViewer doCreateViewer() {
        return new EmbeddedWebViewerRestfulObjects();
    }

}
