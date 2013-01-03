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

package uk.co.objectconnexions.expressiveobjects.core.commons.config;

import java.util.ArrayList;
import java.util.Arrays;

import com.google.common.collect.Lists;

import org.apache.log4j.Logger;

import uk.co.objectconnexions.expressiveobjects.core.commons.resource.ResourceStreamSource;
import uk.co.objectconnexions.expressiveobjects.core.commons.resource.ResourceStreamSourceContextLoaderClassPath;
import uk.co.objectconnexions.expressiveobjects.core.commons.resource.ResourceStreamSourceFileSystem;

/**
 * Convenience implementation of {@link ExpressiveObjectsConfigurationBuilder} that loads
 * configuration resource as per {@link ExpressiveObjectsConfigurationBuilderFileSystem} and
 * otherwise from the {@link ResourceStreamSourceContextLoaderClassPath
 * classpath}.
 * 
 * @see ResourceStreamSourceFileSystem
 */
public class ExpressiveObjectsConfigurationBuilderDefault extends ExpressiveObjectsConfigurationBuilderResourceStreams {

    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(ExpressiveObjectsConfigurationBuilderDefault.class);

    public ExpressiveObjectsConfigurationBuilderDefault() {
        super(resourceStreamSources(null));
    }

    public ExpressiveObjectsConfigurationBuilderDefault(final String firstDirectory) {
        super(resourceStreamSources(firstDirectory));
    }

    private static ResourceStreamSource[] resourceStreamSources(final String firstDirectory) {
        final ArrayList<ResourceStreamSource> rssList = Lists.newArrayList();
        if (firstDirectory != null) {
            rssList.add(fromFileSystem(firstDirectory));
        }
        rssList.addAll(Arrays.asList(fromFileSystem(ConfigurationConstants.DEFAULT_CONFIG_DIRECTORY), fromFileSystem(ConfigurationConstants.WEBINF_FULL_DIRECTORY), fromClassPath(), fromClassPath(ConfigurationConstants.WEBINF_DIRECTORY)));
        return rssList.toArray(new ResourceStreamSource[0]);
    }

    private static ResourceStreamSource fromFileSystem(final String directory) {
        return ResourceStreamSourceFileSystem.create(directory);
    }

    private static ResourceStreamSource fromClassPath() {
        return ResourceStreamSourceContextLoaderClassPath.create();
    }

    private static ResourceStreamSource fromClassPath(final String prefix) {
        return ResourceStreamSourceContextLoaderClassPath.create(prefix);
    }

}
