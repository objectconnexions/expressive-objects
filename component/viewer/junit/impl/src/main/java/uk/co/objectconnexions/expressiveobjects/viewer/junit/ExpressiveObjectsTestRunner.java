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

package uk.co.objectconnexions.expressiveobjects.viewer.junit;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.jmock.Mockery;
import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.internal.runners.MethodRoadie;
import org.junit.internal.runners.TestClass;
import org.junit.internal.runners.TestMethod;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

import uk.co.objectconnexions.expressiveobjects.applib.fixtures.LogonFixture;
import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSession;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfigurationBuilder;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfigurationBuilderDefault;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.services.ServicesInjectorSpi;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.AuthenticationRequest;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.exploration.AuthenticationRequestExploration;
import uk.co.objectconnexions.expressiveobjects.core.runtime.fixtures.authentication.AuthenticationRequestLogonFixture;
import uk.co.objectconnexions.expressiveobjects.core.runtime.installers.InstallerLookupDefault;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.DeploymentType;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.SystemConstants;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.PersistenceSession;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.transaction.ExpressiveObjectsTransactionManager;
import uk.co.objectconnexions.expressiveobjects.viewer.junit.internal.ExpressiveObjectsSystemUsingInstallersWithinJunit;

/**
 * Copied from JMock, and with the same support.
 * 
 */
public class ExpressiveObjectsTestRunner extends JUnit4ClassRunner {

    private final Field mockeryField;

    /**
     * Only used during object construction.
     */
    public ExpressiveObjectsTestRunner(final Class<?> testClass) throws InitializationError {
        super(testClass);

        // JMock initialization, adapted to allow for no mockery field.
        mockeryField = findFieldAndMakeAccessible(testClass, Mockery.class);
    }

    private static String getConfigDir(final Class<?> javaClass) {
        final ConfigDir fixturesAnnotation = javaClass.getAnnotation(ConfigDir.class);
        if (fixturesAnnotation != null) {
            return fixturesAnnotation.value();
        }
        return null;
    }

    @Override
    protected void invokeTestMethod(final Method method, final RunNotifier notifier) {

        final TestClass testClass = getTestClass();
        final String configDirIfAny = getConfigDir(testClass.getJavaClass());

        final Description description = methodDescription(method);

        final ExpressiveObjectsConfigurationBuilder expressiveObjectsConfigurationBuilder = new ExpressiveObjectsConfigurationBuilderDefault(configDirIfAny);
        expressiveObjectsConfigurationBuilder.add(SystemConstants.NOSPLASH_KEY, "" + true); // switch
                                                                               // off
                                                                               // splash

        final InstallerLookupDefault installerLookup = new InstallerLookupDefault();
        expressiveObjectsConfigurationBuilder.injectInto(installerLookup);
        installerLookup.init();

        ExpressiveObjectsSystemUsingInstallersWithinJunit system = null;
        AuthenticationSession session = null;
        try {
            // init the system; cf similar code in Expressive Objects and
            // ExpressiveObjectsServletContextInitializer
            final DeploymentType deploymentType = DeploymentType.PROTOTYPE;

            // TODO: replace with regular ExpressiveObjectsSystem and remove this subclass.
            system = new ExpressiveObjectsSystemUsingInstallersWithinJunit(deploymentType, installerLookup, testClass);

            system.init();

            // specific to this bootstrap mechanism
            AuthenticationRequest request;
            final LogonFixture logonFixture = system.getFixturesInstaller().getLogonFixture();
            if (logonFixture != null) {
                request = new AuthenticationRequestLogonFixture(logonFixture);
            } else {
                request = new AuthenticationRequestExploration(logonFixture);
            }
            session = ExpressiveObjectsContext.getAuthenticationManager().authenticate(request);

            ExpressiveObjectsContext.openSession(session);
            getTransactionManager().startTransaction();

            final Object test = createTest();
            getServicesInjector().injectServicesInto(test);

            final TestMethod testMethod = wrapMethod(method);
            new MethodRoadie(test, testMethod, notifier, description).run();

            getTransactionManager().endTransaction();

        } catch (final InvocationTargetException e) {
            testAborted(notifier, description, e.getCause());
            getTransactionManager().abortTransaction();
            return;
        } catch (final Exception e) {
            testAborted(notifier, description, e);
            return;
        } finally {
            if (system != null) {
                if (session != null) {
                    ExpressiveObjectsContext.closeSession();
                }
                system.shutdown();
            }
        }
    }

    private void testAborted(final RunNotifier notifier, final Description description, final Throwable e) {
        notifier.fireTestStarted(description);
        notifier.fireTestFailure(new Failure(description, e));
        notifier.fireTestFinished(description);
    }

    /**
     * Taken from JMock's runner.
     */
    @Override
    protected TestMethod wrapMethod(final Method method) {
        return new TestMethod(method, getTestClass()) {
            @Override
            public void invoke(final Object testFixture) throws IllegalAccessException, InvocationTargetException {

                super.invoke(testFixture);

                if (mockeryField != null) {
                    mockeryOf(testFixture).assertIsSatisfied();
                }
            }
        };
    }

    /**
     * JMock code.
     * 
     * @param test
     * @return
     */
    protected Mockery mockeryOf(final Object test) {
        if (mockeryField == null) {
            return null;
        }
        try {
            final Mockery mockery = (Mockery) mockeryField.get(test);
            if (mockery == null) {
                throw new IllegalStateException(String.format("Mockery named '%s' is null", mockeryField.getName()));
            }
            return mockery;
        } catch (final IllegalAccessException e) {
            throw new IllegalStateException(String.format("cannot get value of field %s", mockeryField.getName()), e);
        }
    }

    /**
     * Adapted from JMock code.
     */
    static Field findFieldAndMakeAccessible(final Class<?> testClass, final Class<?> clazz) throws InitializationError {
        for (Class<?> c = testClass; c != Object.class; c = c.getSuperclass()) {
            for (final Field field : c.getDeclaredFields()) {
                if (clazz.isAssignableFrom(field.getType())) {
                    field.setAccessible(true);
                    return field;
                }
            }
        }
        return null;
    }

    // /////////////////////////////////////////////////////
    // Dependencies (from context)
    // /////////////////////////////////////////////////////

    private static PersistenceSession getPersistenceSession() {
        return ExpressiveObjectsContext.getPersistenceSession();
    }

    private static ServicesInjectorSpi getServicesInjector() {
        return getPersistenceSession().getServicesInjector();
    }

    private static ExpressiveObjectsTransactionManager getTransactionManager() {
        return getPersistenceSession().getTransactionManager();
    }

}
