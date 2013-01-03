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

import org.apache.log4j.Logger;

import uk.co.objectconnexions.expressiveobjects.core.commons.resource.ResourceStreamSource;
import uk.co.objectconnexions.expressiveobjects.core.commons.resource.ResourceStreamSourceChainOfResponsibility;
import uk.co.objectconnexions.expressiveobjects.core.commons.resource.ResourceStreamSourceFileSystem;

/**
 * Convenience implementation of {@link ExpressiveObjectsConfigurationBuilder} that loads
 * configuration resource from a specified directory (or directories) on the
 * filesystem.
 * 
 * @see ResourceStreamSourceFileSystem
 */
public class ExpressiveObjectsConfigurationBuilderFileSystem extends ExpressiveObjectsConfigurationBuilderResourceStreams {

    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(ExpressiveObjectsConfigurationBuilderFileSystem.class);

    private static ResourceStreamSource createResourceStreamSource(final String... directories) {
        final ResourceStreamSourceChainOfResponsibility composite = new ResourceStreamSourceChainOfResponsibility();
        for (final String directory : directories) {
            composite.addResourceStreamSource(new ResourceStreamSourceFileSystem(directory));
        }
        return composite;
    }

    public ExpressiveObjectsConfigurationBuilderFileSystem(final String... directories) {
        super(createResourceStreamSource(directories));
    }

    public ExpressiveObjectsConfigurationBuilderFileSystem() {
        super(ResourceStreamSourceFileSystem.create(ConfigurationConstants.DEFAULT_CONFIG_DIRECTORY));
    }

}
