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
package uk.co.objectconnexions.expressiveobjects.viewer.junit.internal;

import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.AuthenticationManagerInstaller;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authorization.AuthorizationManagerInstaller;
import uk.co.objectconnexions.expressiveobjects.core.runtime.installerregistry.installerapi.PersistenceMechanismInstaller;
import uk.co.objectconnexions.expressiveobjects.core.security.authentication.BypassAuthenticationManagerInstaller;
import uk.co.objectconnexions.expressiveobjects.core.security.authorization.BypassAuthorizationManagerInstaller;
import uk.co.objectconnexions.expressiveobjects.viewer.junit.Authenticator;
import uk.co.objectconnexions.expressiveobjects.viewer.junit.Authorizor;
import uk.co.objectconnexions.expressiveobjects.viewer.junit.Persistor;

public class AnnotationInstaller {

    /**
     * Should be called prior to installing; typically called immediately after
     * instantiation.
     * 
     * <p>
     * Note: an alternative design would be to have a 1-arg constructor, but the
     * convention for installers is to make them no-arg.
     */
    // {{ AuthenticationManagerInstaller
    public AuthenticationManagerInstaller addAuthenticatorAnnotatedOn(final Class<?> javaClass) throws InstantiationException, IllegalAccessException {
        final Authenticator authenticatorAnnotation = javaClass.getAnnotation(Authenticator.class);
        if (authenticatorAnnotation != null) {
            return addAuthenticatorRepresentedBy(authenticatorAnnotation);
        } else {
            return new BypassAuthenticationManagerInstaller();
        }

    }

    private AuthenticationManagerInstaller addAuthenticatorRepresentedBy(final Authenticator authenticatorAnnotation) throws InstantiationException, IllegalAccessException {
        final Class<?> fixtureClass = authenticatorAnnotation.value();
        return (AuthenticationManagerInstaller) fixtureClass.newInstance();
    }

    // }}

    // {{ AuthorizationManagerInstaller
    public AuthorizationManagerInstaller addAuthorizerAnnotatedOn(final Class<?> javaClass) throws InstantiationException, IllegalAccessException {
        final Authorizor authorizorAnnotation = javaClass.getAnnotation(Authorizor.class);
        if (authorizorAnnotation != null) {
            return addAuthorizerRepresentedBy(authorizorAnnotation);
        } else {
            return new BypassAuthorizationManagerInstaller();
        }

    }

    private AuthorizationManagerInstaller addAuthorizerRepresentedBy(final Authorizor authorizorAnnotation) throws InstantiationException, IllegalAccessException {
        final Class<?> fixtureClass = authorizorAnnotation.value();
        return (AuthorizationManagerInstaller) fixtureClass.newInstance();
    }

    // }}

    // {{ PersistenceMechanismInstaller
    public PersistenceMechanismInstaller addPersistorAnnotatedOn(final Class<?> javaClass) throws InstantiationException, IllegalAccessException {
        final Persistor annotation = javaClass.getAnnotation(Persistor.class);
        if (annotation != null) {
            return addPersistorRepresentedBy(annotation);
        } else {
            return new InMemoryPersistenceMechanismInstallerWithinJunit();
        }

    }

    private PersistenceMechanismInstaller addPersistorRepresentedBy(final Persistor annotation) throws InstantiationException, IllegalAccessException {
        final Class<?> fixtureClass = annotation.value();
        return (PersistenceMechanismInstaller) fixtureClass.newInstance();
    }
    // }}

    // new InMemoryUserProfileStoreInstaller();

}
