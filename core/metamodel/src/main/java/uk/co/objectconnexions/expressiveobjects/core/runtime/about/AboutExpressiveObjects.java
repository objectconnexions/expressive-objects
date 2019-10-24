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

package uk.co.objectconnexions.expressiveobjects.core.runtime.about;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import uk.co.objectconnexions.expressiveobjects.core.commons.exceptions.ExpressiveObjectsException;

public class AboutExpressiveObjects {
    private static String applicationCopyrightNotice;
    private static String applicationName;
    private static String applicationVersion;
    private static String frameworkName;
    private static String frameworkVersion;
    private static String logo;
    private static String frameworkCopyright;
    private static String frameworkCompileDate;
    private static List<ComponentDetails> componentDetails;

    static {
        try {
            final ResourceBundle bundle = ResourceBundle.getBundle("expressive-objects-version");
            logo = bundle.getString("framework.logo");
            frameworkVersion = bundle.getString("framework.version");
            frameworkName = bundle.getString("framework.name");
            frameworkCopyright = bundle.getString("framework.copyright");
            frameworkCompileDate = bundle.getString("framework.compile.date");
        } catch (final MissingResourceException ex) {
            logo = "splash-logo";
            frameworkVersion = "${project.version}-r${buildNumber}";
            frameworkCopyright = "Copyright (c) 2010~2012 Apache Software Foundation";
            frameworkName = "${project.parent.name}";
        }

        // NOT in use yet: frameworkVersion = findVersion();
    }

    public static String findVersion() {
        try {
            final String moduleId = "uk.co.objectconnexions.expressiveobjects.plugins:dndviewer";
            final String module = moduleId.replace(":", "/");
            final InputStream resourceAsStream = AboutExpressiveObjects.class.getClassLoader().getResourceAsStream("META-INF/maven/" + module + "/pom.properties");
            if (resourceAsStream == null) {
                return "no version";
            }
            final Properties p = new Properties();
            p.load(resourceAsStream);
            final String version = p.getProperty("version");
            return version;
        } catch (final IOException e) {
            throw new ExpressiveObjectsException(e);
        }

    }

    public static String getApplicationCopyrightNotice() {
        return applicationCopyrightNotice;
    }

    public static String getApplicationName() {
        return applicationName;
    }

    public static String getApplicationVersion() {
        return applicationVersion;
    }

    public static String getFrameworkCopyrightNotice() {
        return select(frameworkCopyright, "Copyright Apache Software Foundation");
    }

    public static String getFrameworkCompileDate() {
        return frameworkCompileDate;
    }

    public static String getFrameworkName() {
        return select(frameworkName, "Expressive Objects (incubating)");
    }

    public static String getImageName() {
        return select(logo, "splash-logo");
    }

    public static String getFrameworkVersion() {
        final String version = "Version " + select(frameworkVersion, "unreleased");
        /*
         * NOT in use yet: for (ComponentDetails details : componentDetails) {
         * version += "\n" + details.getName() + " " + details.getModule() + " "
         * + details.getVersion(); }
         */
        return version;
    }

    public static void main(final String[] args) {
        System.out.println(getFrameworkName() + ", " + getFrameworkVersion());
        System.out.println(getFrameworkCopyrightNotice());
    }

    private static String select(final String value, final String defaultValue) {
        return value == null || value.startsWith("${") ? defaultValue : value;
    }

    public static void setApplicationCopyrightNotice(final String applicationCopyrightNotice) {
        AboutExpressiveObjects.applicationCopyrightNotice = applicationCopyrightNotice;
    }

    public static void setApplicationName(final String applicationName) {
        AboutExpressiveObjects.applicationName = applicationName;
    }

    public static void setApplicationVersion(final String applicationVersion) {
        AboutExpressiveObjects.applicationVersion = applicationVersion;
    }

    public static void setComponentDetails(final List<ComponentDetails> componentDetails) {
        AboutExpressiveObjects.componentDetails = componentDetails;
    }

}
