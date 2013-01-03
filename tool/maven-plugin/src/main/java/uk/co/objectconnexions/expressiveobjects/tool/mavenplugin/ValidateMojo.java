package uk.co.objectconnexions.expressiveobjects.tool.mavenplugin;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.runtimecontext.noruntime.RuntimeContextNoRuntime;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.validator.ValidationFailures;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.app.ExpressiveObjectsMetaModel;
import uk.co.objectconnexions.expressiveobjects.progmodels.dflt.ProgrammingModelFacetsJava5;
import uk.co.objectconnexions.expressiveobjects.tool.mavenplugin.util.ClassRealms;
import uk.co.objectconnexions.expressiveobjects.tool.mavenplugin.util.ClassWorlds;
import uk.co.objectconnexions.expressiveobjects.tool.mavenplugin.util.ExpressiveObjectsMetaModels;
import uk.co.objectconnexions.expressiveobjects.tool.mavenplugin.util.Log4j;
import uk.co.objectconnexions.expressiveobjects.tool.mavenplugin.util.MavenProjects;
import uk.co.objectconnexions.expressiveobjects.tool.mavenplugin.util.Xpp3Doms;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.codehaus.classworlds.ClassRealm;
import org.codehaus.classworlds.ClassWorld;
import org.codehaus.classworlds.DuplicateRealmException;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * 
 * 
 *
 */
@Mojo(name = "validate", defaultPhase = LifecyclePhase.TEST, requiresProject = true, requiresDependencyResolution = ResolutionScope.COMPILE, requiresDependencyCollection = ResolutionScope.COMPILE)
public class ValidateMojo extends AbstractMojo {

    private static final String CURRENT_PLUGIN_KEY = "uk.co.objectconnexions.expressiveobjects.tools:expressive-objects-maven-plugin";
    private static final String EXPRESSIVE_OBJECTS_REALM = "expressive-objects";

    @Component
    protected MavenProject mavenProject;

    public void execute() throws MojoExecutionException, MojoFailureException {

        Log4j.configureIfRequired();

        ValidationFailures validationFailures = bootExpressiveObjectsThenShutdown();

        if (validationFailures.occurred()) {
            throwFailureException(validationFailures.getNumberOfMessages() + " problems found.", validationFailures.getMessages());
        }
    }

    private ValidationFailures bootExpressiveObjectsThenShutdown() throws MojoExecutionException, MojoFailureException {
        ClassWorld classWorld = null;
        ExpressiveObjectsMetaModel expressiveObjectsMetaModel = null;
        try {
            classWorld = new ClassWorld();
            final ClassRealm expressiveObjectsRealm = classWorld.newRealm(EXPRESSIVE_OBJECTS_REALM);

            addClassesToRealm(expressiveObjectsRealm);

            List<Object> serviceList = createServicesFromConfiguration(expressiveObjectsRealm);

            expressiveObjectsMetaModel = bootstrapExpressiveObjects(expressiveObjectsRealm, serviceList);
            return expressiveObjectsMetaModel.getValidationFailures();

        } catch (DuplicateRealmException e) {
            throwExecutionException("Error building classworld", e);
            return null; // never reached, since exception thrown above
        } finally {
            ClassWorlds.disposeSafely(classWorld, EXPRESSIVE_OBJECTS_REALM);
            ExpressiveObjectsMetaModels.disposeSafely(expressiveObjectsMetaModel);
        }
    }

    @SuppressWarnings("unchecked")
    private void addClassesToRealm(final ClassRealm expressiveObjectsRealm) throws MojoExecutionException {

        // first add all dependencies (including transitive)...
        Set<Artifact> artifacts = mavenProject.getArtifacts();
        for (Artifact artifact : artifacts) {
            File file = artifact.getFile();
            try {
                ClassRealms.addFileToRealm(expressiveObjectsRealm, file, getLog());
            } catch (MalformedURLException e) {
                throwExecutionException("Error adding classes for artifact '" + artifact + "' to class realm", e);
            } catch (IOException e) {
                throwExecutionException("Error adding classes for artifact '" + artifact + "' to class realm", e);
            }
        }

        // ... then all classpath elements
        // (there is substantial overlap with getArtifacts() here, but neither
        // appears to
        // provide the comprehensive set of class path elements).
        List<String> classpathElements;
        try {
            classpathElements = mavenProject.getRuntimeClasspathElements();
        } catch (DependencyResolutionRequiredException e) {
            throwExecutionException("Error obtaining runtime classpath", e);
            return;
        }
        for (String classpathElement : classpathElements) {
            final File file = new File(classpathElement);
            try {
                ClassRealms.addFileToRealm(expressiveObjectsRealm, file, getLog());
            } catch (MalformedURLException e) {
                throwExecutionException("Error adding classes for classpath element '" + classpathElement + "' to class realm", e);
            } catch (IOException e) {
                throwExecutionException("Error adding classes for classpath element '" + classpathElement + "' to class realm", e);
            }
        }
    }

    private List<Object> createServicesFromConfiguration(final ClassRealm expressiveObjectsRealm) throws MojoFailureException {
        final List<String> serviceEls = getServiceClassNamesFromConfiguration();
        return createServiceInstances(expressiveObjectsRealm, serviceEls);
    }

    private List<String> getServiceClassNamesFromConfiguration() throws MojoFailureException {
        final Xpp3Dom configuration = (Xpp3Dom) MavenProjects.lookupPlugin(mavenProject, CURRENT_PLUGIN_KEY).getConfiguration();
        if (configuration == null) {
            throwFailureException("Configuration error", "No <configuration> element found");
        }
        final Xpp3Dom servicesEl = configuration.getChild("services");
        if (servicesEl == null) {
            throwFailureException("Configuration error", "No <configuration>/<services> element found");
        }
        final Xpp3Dom[] serviceEls = servicesEl.getChildren("service");
        if (serviceEls == null || serviceEls.length == 0) {
            throwFailureException("Configuration error", "No <configuration>/<services>/<service> elements found");
        }
        return Lists.transform(Arrays.asList(serviceEls), Xpp3Doms.GET_VALUE);
    }

    private List<Object> createServiceInstances(final ClassRealm expressiveObjectsRealm, final List<String> serviceClassNames) throws MojoFailureException {
        final List<Object> serviceList = Lists.newArrayList();
        final Set<String> logMessages = Sets.newLinkedHashSet();
        for (String serviceClassName : serviceClassNames) {
            try {
                serviceList.add(expressiveObjectsRealm.loadClass(serviceClassName).newInstance());
            } catch (ClassNotFoundException e) {
                logMessages.add("Error loading class '" + serviceClassName + "' from classrealm");
            } catch (InstantiationException e) {
                logMessages.add("Error instantiating loaded class '" + serviceClassName + "'");
            } catch (IllegalAccessException e) {
                logMessages.add("Error instantiating loaded class '" + serviceClassName + "'");
            }
        }
        if (!logMessages.isEmpty()) {
            throwFailureException("Unable to load configured services", logMessages);
        }
        return serviceList;
    }

    private static ExpressiveObjectsMetaModel bootstrapExpressiveObjects(final ClassRealm expressiveObjectsRealm, List<Object> serviceList) {
        Thread.currentThread().setContextClassLoader(expressiveObjectsRealm.getClassLoader());

        ExpressiveObjectsMetaModel expressiveObjectsMetaModel = ExpressiveObjectsMetaModel.builder(new RuntimeContextNoRuntime(), new ProgrammingModelFacetsJava5()).withServices(serviceList).build();
        expressiveObjectsMetaModel.init();

        return expressiveObjectsMetaModel;
    }

    private void throwFailureException(String errorMessage, Set<String> logMessages) throws MojoFailureException {
        logErrors(logMessages);
        throw new MojoFailureException(errorMessage);
    }

    private void throwFailureException(String errorMessage, String... logMessages) throws MojoFailureException {
        logErrors(logMessages);
        throw new MojoFailureException(errorMessage);
    }

    private void throwExecutionException(String errorMessage, Exception e) throws MojoExecutionException {
        logErrors(errorMessage);
        throw new MojoExecutionException(errorMessage, e);
    }

    private void logErrors(String... logMessages) {
        getLog().error("");
        for (String logMessage : logMessages) {
            getLog().error(logMessage);
        }
        getLog().error("");
    }

    private void logErrors(Set<String> logMessages) {
        logErrors(logMessages.toArray(new String[] {}));
    }

}