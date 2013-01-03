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

package uk.co.objectconnexions.expressiveobjects.viewer.html.image;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import uk.co.objectconnexions.expressiveobjects.core.commons.lang.Resources;

public class ImageProviderResourceBased extends ImageProviderAbstract {

    /**
     * Is an array since easy to maintain
     */
    public final static String[] DEFAULT_LOCATIONS = { "images", "src/main/resources", "src/main/java" };

    /**
     * Is a list since easy to inject.
     */
    private List<String> locations = new ArrayList<String>();

    /**
     * Initializes {@link #locations} with {@link #DEFAULT_LOCATIONS}, but can
     * be overridden using {@link #setLocations(List)}.
     */
    public ImageProviderResourceBased() {
        locations.addAll(Arrays.asList(DEFAULT_LOCATIONS));
    }

    @Override
    protected String findImage(final String className, final String[] extensions) {

        for (final String location : locations) {
            for (final String extension : extensions) {
                final String candidate = location + "/" + className + "." + extension;
                if (Resources.getResourceAsFile(candidate) != null) {
                    return candidate;
                }
            }
        }
        return null;
    }

    /**
     * Optionally inject the locations where the provider will search.
     * 
     * <p>
     * If not specified, will use the locations in {@link #DEFAULT_LOCATIONS}.
     */
    public void setLocations(final List<String> locations) {
        this.locations = locations;
    }

}
