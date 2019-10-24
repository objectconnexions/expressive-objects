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

package uk.co.objectconnexions.expressiveobjects.core.webapp.config;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;

import uk.co.objectconnexions.expressiveobjects.core.commons.lang.PathUtils;
import uk.co.objectconnexions.expressiveobjects.core.commons.resource.ResourceStreamSourceAbstract;

public class ResourceStreamSourceForWebInf extends ResourceStreamSourceAbstract {

    public static final String DEFAULT_WEBINF_DIRECTORY = "/WEB-INF";

    private final ServletContext servletContext;
    private final String configurationDirectory;

    public ResourceStreamSourceForWebInf(final ServletContext servletContext) {
        this(servletContext, DEFAULT_WEBINF_DIRECTORY);
    }

    public ResourceStreamSourceForWebInf(final ServletContext servletContext, final String configurationDirectory) {
        this.servletContext = servletContext;
        this.configurationDirectory = configurationDirectory;
    }

    @Override
    public String getName() {
        return "servlet context ('" + configurationDirectory + "')";
    }

    @Override
    public InputStream doReadResource(final String resourcePath) throws IOException {
        final String fullyQualifiedResourcePath = PathUtils.combine(configurationDirectory, resourcePath);
        return servletContext.getResourceAsStream(fullyQualifiedResourcePath);
    }

}
