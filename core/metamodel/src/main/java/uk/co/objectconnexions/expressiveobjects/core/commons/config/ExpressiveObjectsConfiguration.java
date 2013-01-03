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

import java.awt.Color;
import java.awt.Font;
import java.util.Iterator;
import java.util.Map;

import uk.co.objectconnexions.expressiveobjects.core.commons.components.Injectable;
import uk.co.objectconnexions.expressiveobjects.core.commons.debug.DebuggableWithTitle;
import uk.co.objectconnexions.expressiveobjects.core.commons.resource.ResourceStreamSource;

/**
 * Immutable set of properties representing the configuration of the running
 * system.
 * 
 * <p>
 * The {@link ExpressiveObjectsConfiguration} is one part of a mutable/immutable pair pattern
 * (cf {@link String} and {@link StringBuilder}). What this means is, as
 * components are loaded they can discover their own configuration resources.
 * These are added to {@link ExpressiveObjectsConfigurationBuilder}.
 * 
 * <p>
 * Thus the {@link ExpressiveObjectsConfiguration} held by different components may vary, but
 * with each being a possible superset of the previous.
 */
public interface ExpressiveObjectsConfiguration extends DebuggableWithTitle, Injectable, Iterable<String> {

    /**
     * Creates a new ExpressiveObjectsConfiguration containing the properties starting with
     * the specified prefix. The names of the new properties will have the
     * prefixed stripped. This is similar to the {@link #getProperties(String)}
     * method, except the property names have their prefixes removed.
     * 
     * @see #getProperties(String)
     */
    ExpressiveObjectsConfiguration createSubset(String prefix);

    /**
     * Gets the boolean value for the specified name where no value or 'on' will
     * result in true being returned; anything gives false. If no boolean
     * property is specified with this name then false is returned.
     * 
     * @param name
     *            the property name
     */
    boolean getBoolean(String name);

    /**
     * Gets the boolean value for the specified name. If no property is
     * specified with this name then the specified default boolean value is
     * returned.
     * 
     * @param name
     *            the property name
     * @param defaultValue
     *            the value to use as a default
     */
    boolean getBoolean(String name, boolean defaultValue);

    /**
     * Gets the color for the specified name. If no color property is specified
     * with this name then null is returned.
     * 
     * @param name
     *            the property name
     */
    Color getColor(String name);

    /**
     * Gets the color for the specified name. If no color property is specified
     * with this name then the specified default color is returned.
     * 
     * @param name
     *            the property name
     * @param defaultValue
     *            the value to use as a default
     */
    Color getColor(String name, Color defaultValue);

    /**
     * Gets the font for the specified name. If no font property is specified
     * with this name then null is returned.
     * 
     * @param name
     *            the property name
     */
    Font getFont(String name);

    /**
     * Gets the font for the specified name. If no font property is specified
     * with this name then the specified default font is returned.
     * 
     * @param name
     *            the property name
     * @param defaultValue
     *            the color to use as a default
     */
    Font getFont(String name, Font defaultValue);

    /**
     * Returns a list of entries for the single configuration property with the
     * specified name.
     * 
     * <p>
     * If there is no matching property then returns an empty array.
     */
    String[] getList(String name);

    /**
     * Gets the number value for the specified name. If no property is specified
     * with this name then 0 is returned.
     * 
     * @param name
     *            the property name
     */
    int getInteger(String name);

    /**
     * Gets the number value for the specified name. If no property is specified
     * with this name then the specified default number value is returned.
     * 
     * @param name
     *            the property name
     * @param defaultValue
     *            the value to use as a default
     */
    int getInteger(String name, int defaultValue);

    /**
     * Creates a new ExpressiveObjectsConfiguration containing the properties starting with
     * the specified prefix. The names of the properties in the copy are the
     * same as in the original, ie the prefix is not removed. This is similar to
     * the {@link #createSubset(String)} method except the names of the
     * properties are not altered when copied.
     * 
     * @see #createSubset(String)
     */
    ExpressiveObjectsConfiguration getProperties(String withPrefix);

    /**
     * Returns the configuration property with the specified name. If there is
     * no matching property then null is returned.
     */
    String getString(String name);

    String getString(String name, String defaultValue);

    boolean hasProperty(String name);

    boolean isEmpty();

    /**
     * Iterates over the property names of this configuration.
     */
    @Override
    Iterator<String> iterator();

    int size();

    /**
     * The {@link ResourceStreamSource} that was used to build this
     * configuration.
     * 
     * @see ExpressiveObjectsConfigurationBuilder#getResourceStreamSource()
     */
    ResourceStreamSource getResourceStreamSource();

    /**
     * A mutable copy of the current set of properties (name/values) held in this configuration.
     */
    Map<String, String> asMap();

}
