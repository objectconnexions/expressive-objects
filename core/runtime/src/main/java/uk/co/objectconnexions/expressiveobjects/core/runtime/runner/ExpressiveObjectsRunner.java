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

package uk.co.objectconnexions.expressiveobjects.core.runtime.runner;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ConfigurationConstants;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfigurationBuilder;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfigurationBuilderDefault;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfigurationBuilderPrimer;
import uk.co.objectconnexions.expressiveobjects.core.runtime.installerregistry.InstallerLookup;
import uk.co.objectconnexions.expressiveobjects.core.runtime.installers.InstallerLookupDefault;
import uk.co.objectconnexions.expressiveobjects.core.runtime.logging.ExpressiveObjectsLoggingConfigurer;
import uk.co.objectconnexions.expressiveobjects.core.runtime.optionhandler.BootPrinter;
import uk.co.objectconnexions.expressiveobjects.core.runtime.optionhandler.OptionHandler;
import uk.co.objectconnexions.expressiveobjects.core.runtime.runner.opts.OptionHandlerAdditionalProperty;
import uk.co.objectconnexions.expressiveobjects.core.runtime.runner.opts.OptionHandlerConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.runtime.runner.opts.OptionHandlerDebug;
import uk.co.objectconnexions.expressiveobjects.core.runtime.runner.opts.OptionHandlerDeploymentType;
import uk.co.objectconnexions.expressiveobjects.core.runtime.runner.opts.OptionHandlerDiagnostics;
import uk.co.objectconnexions.expressiveobjects.core.runtime.runner.opts.OptionHandlerFixture;
import uk.co.objectconnexions.expressiveobjects.core.runtime.runner.opts.OptionHandlerHelp;
import uk.co.objectconnexions.expressiveobjects.core.runtime.runner.opts.OptionHandlerNoSplash;
import uk.co.objectconnexions.expressiveobjects.core.runtime.runner.opts.OptionHandlerPersistor;
import uk.co.objectconnexions.expressiveobjects.core.runtime.runner.opts.OptionHandlerQuiet;
import uk.co.objectconnexions.expressiveobjects.core.runtime.runner.opts.OptionHandlerReflector;
import uk.co.objectconnexions.expressiveobjects.core.runtime.runner.opts.OptionHandlerUserProfileStore;
import uk.co.objectconnexions.expressiveobjects.core.runtime.runner.opts.OptionHandlerVerbose;
import uk.co.objectconnexions.expressiveobjects.core.runtime.runner.opts.OptionHandlerVersion;
import uk.co.objectconnexions.expressiveobjects.core.runtime.runner.opts.OptionHandlerViewer;
import uk.co.objectconnexions.expressiveobjects.core.runtime.runner.opts.OptionValidator;
import uk.co.objectconnexions.expressiveobjects.core.runtime.runner.opts.OptionValidatorForPersistor;
import uk.co.objectconnexions.expressiveobjects.core.runtime.runner.opts.OptionValidatorForViewers;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.DeploymentType;
import org.apache.log4j.Logger;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class ExpressiveObjectsRunner {

    private static final Logger LOG = Logger.getLogger(ExpressiveObjectsRunner.class);

    private final ExpressiveObjectsLoggingConfigurer loggingConfigurer = new ExpressiveObjectsLoggingConfigurer();

    private final String[] args;
    private final OptionHandlerDeploymentType optionHandlerDeploymentType;
    private final InstallerLookup installerLookup;

    private final OptionHandlerViewer optionHandlerViewer;

    private final List<OptionHandler> optionHandlers = Lists.newArrayList();
    private final List<OptionValidator> validators = Lists.newArrayList();
    private ExpressiveObjectsConfigurationBuilder expressiveObjectsConfigurationBuilder;

    private Injector globalInjector;;

    // ///////////////////////////////////////////////////////////////////////////////////////
    // Construction and adjustments
    // ///////////////////////////////////////////////////////////////////////////////////////

    public ExpressiveObjectsRunner(final String[] args, final OptionHandlerDeploymentType optionHandlerDeploymentType) {
        this.args = args;
        this.optionHandlerDeploymentType = optionHandlerDeploymentType;

        // setup logging immediately
        loggingConfigurer.configureLogging(determineConfigDirectory(), args);
        this.installerLookup = new InstallerLookupDefault();

        this.optionHandlerViewer = addStandardOptionHandlersAndValidators(this.installerLookup);
    }

    // REVIEW is this something that ExpressiveObjectsConfigBuilder should know about?
    private String determineConfigDirectory() {
        if (new File(ConfigurationConstants.WEBINF_FULL_DIRECTORY).exists()) {
            return ConfigurationConstants.WEBINF_FULL_DIRECTORY;
        } else {
            return ConfigurationConstants.DEFAULT_CONFIG_DIRECTORY;
        }
    }

    /**
     * Adds additional option handlers; may also require additional
     * {@link OptionValidator validator}s to be
     * {@link #addValidator(OptionValidator) add}ed.
     * <p>
     * An adjustment (as per GOOS book).
     */
    public final boolean addOptionHandler(final OptionHandler optionHandler) {
        return optionHandlers.add(optionHandler);
    }

    /**
     * Adds additional validators; typically goes hand-in-hand will calls to
     * {@link #addOptionHandler(OptionHandler)}.
     * <p>
     * An adjustment (as per GOOS book).
     */
    public void addValidator(final OptionValidator validator) {
        validators.add(validator);
    }

    /**
     * The default implementation is a {@link ExpressiveObjectsConfigurationBuilderDefault},
     * which looks to the <tt>config/</tt> directory, the
     * <tt>src/main/webapp/WEB-INF</tt> directory, and then finally to the
     * classpath. However, this could be a security concern in a production
     * environment; a user could edit the <tt>expressive-objects.properties</tt> config files
     * to disable security, for example.
     * <p>
     * This method therefore allows this system to be configured using a
     * different {@link ExpressiveObjectsConfigurationBuilder}. For example, a
     * security-conscious subclass could return a
     * {@link ExpressiveObjectsConfigurationBuilder} that only reads from the classpath. This
     * would allow the application to be deployed as a single sealed JAR that
     * could not be tampered with.
     * <p>
     * An adjustment (as per GOOS book).
     */
    public void setConfigurationBuilder(final ExpressiveObjectsConfigurationBuilder expressiveObjectsConfigurationBuilder) {
        this.expressiveObjectsConfigurationBuilder = expressiveObjectsConfigurationBuilder;
    }

    // ///////////////////////////////////////////////////////////////////////////////////////
    // parse and validate
    // ///////////////////////////////////////////////////////////////////////////////////////

    public final boolean parseAndValidate() {

        // add options (ie cmd line flags)
        final Options options = createOptions();

        // parse & validate options from the cmd line
        final BootPrinter printer = new BootPrinter(getClass());
        return parseOptions(options, printer) && validateOptions(options, printer);
    }

    private Options createOptions() {
        final Options options = new Options();
        for (final OptionHandler optionHandler : optionHandlers) {
            optionHandler.addOption(options);
        }
        return options;
    }

    private boolean parseOptions(final Options options, final BootPrinter printer) {
        final CommandLineParser parser = new BasicParser();
        try {
            final CommandLine commandLine = parser.parse(options, args);
            for (final OptionHandler optionHandler : optionHandlers) {
                if (!optionHandler.handle(commandLine, printer, options)) {
                    return false;
                }
            }
        } catch (final ParseException e) {
            printer.printErrorMessage(e.getMessage());
            printer.printHelp(options);
            return false;
        }
        return true;
    }

    private boolean validateOptions(final Options options, final BootPrinter printer) {
        final DeploymentType deploymentType = optionHandlerDeploymentType.getDeploymentType();

        for (final OptionValidator validator : validators) {
            final Optional<String> errorMessage = validator.validate(deploymentType);
            if (errorMessage.isPresent()) {
                printer.printErrorAndHelp(options, errorMessage.get());
                return false;
            }
        }
        return true;
    }

    public ExpressiveObjectsConfigurationBuilder getStartupConfiguration() {
        return expressiveObjectsConfigurationBuilder;
    }

    public void primeConfigurationWithCommandLineOptions() {
        for (final ExpressiveObjectsConfigurationBuilderPrimer expressiveObjectsConfigurationBuilderPrimer : optionHandlers) {
            LOG.debug("priming configurations for " + expressiveObjectsConfigurationBuilderPrimer);
            expressiveObjectsConfigurationBuilderPrimer.primeConfigurationBuilder(expressiveObjectsConfigurationBuilder);
        }
    }

    public void loadInitialProperties() {
        expressiveObjectsConfigurationBuilder.addDefaultConfigurationResources();
    }


    // ///////////////////////////////////////////////////////////////////////////////////////
    // Bootstrapping
    // ///////////////////////////////////////////////////////////////////////////////////////

    // TODO remove and use is desktop runner

    public final void bootstrap(final ExpressiveObjectsBootstrapper bootstrapper) {

        final DeploymentType deploymentType = optionHandlerDeploymentType.getDeploymentType();

        this.globalInjector = createGuiceInjector(deploymentType, expressiveObjectsConfigurationBuilder, installerLookup, optionHandlers);

        bootstrapper.bootstrap(globalInjector);
        expressiveObjectsConfigurationBuilder.lockConfiguration();
        expressiveObjectsConfigurationBuilder.dumpResourcesToLog();
    }

    private Injector createGuiceInjector(final DeploymentType deploymentType, final ExpressiveObjectsConfigurationBuilder expressiveObjectsConfigurationBuilder, final InstallerLookup installerLookup, final List<OptionHandler> optionHandlers) {
        final ExpressiveObjectsInjectModule expressiveObjectsModule = new ExpressiveObjectsInjectModule(deploymentType, expressiveObjectsConfigurationBuilder, installerLookup);
        expressiveObjectsModule.addViewerNames(optionHandlerViewer.getViewerNames());
        return Guice.createInjector(expressiveObjectsModule);
    }

    // ///////////////////////////////////////////////////////////////////////////////////////
    // Handlers & Validators
    // ///////////////////////////////////////////////////////////////////////////////////////

    public final List<OptionHandler> getOptionHandlers() {
        return Collections.unmodifiableList(optionHandlers);
    }

    private OptionHandlerViewer addStandardOptionHandlersAndValidators(final InstallerLookup installerLookup) {
        addOptionHandler(optionHandlerDeploymentType);
        addOptionHandler(new OptionHandlerConfiguration());

        OptionHandlerPersistor optionHandlerPersistor;
        OptionHandlerViewer optionHandlerViewer;

        addOptionHandler(optionHandlerPersistor = new OptionHandlerPersistor(installerLookup));
        addOptionHandler(optionHandlerViewer = new OptionHandlerViewer(installerLookup));

        addOptionHandler(new OptionHandlerReflector(installerLookup));
        addOptionHandler(new OptionHandlerUserProfileStore(installerLookup));

        addOptionHandler(new OptionHandlerFixture());
        addOptionHandler(new OptionHandlerNoSplash());
        addOptionHandler(new OptionHandlerAdditionalProperty());

        addOptionHandler(new OptionHandlerDebug());
        addOptionHandler(new OptionHandlerDiagnostics());
        addOptionHandler(new OptionHandlerQuiet());
        addOptionHandler(new OptionHandlerVerbose());

        addOptionHandler(new OptionHandlerHelp());
        addOptionHandler(new OptionHandlerVersion());

        // validators
        addValidator(new OptionValidatorForViewers(optionHandlerViewer));
        addValidator(new OptionValidatorForPersistor(optionHandlerPersistor));

        return optionHandlerViewer;
    }

}
