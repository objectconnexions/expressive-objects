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

package uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.standard;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

import uk.co.objectconnexions.expressiveobjects.core.commons.components.ApplicationScopedComponent;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authentication.RegistrationDetails;

public interface Registrar extends Authenticator, ApplicationScopedComponent {

    static Predicate<Registrar> NON_NULL = new Predicate<Registrar>() {
        @Override
        public boolean apply(final Registrar input) {
            return input != null;
        }
    };

    static Function<Authenticator, Registrar> AS_REGISTRAR_ELSE_NULL = new Function<Authenticator, Registrar>() {
        @Override
        public Registrar apply(final Authenticator input) {
            if (input instanceof Registrar) {
                return (Registrar) input;
            }
            return null;
        }
    };

    /**
     * Whether the provided {@link RegistrationDetails} is recognized by this
     * {@link Registrar}.
     */
    boolean canRegister(Class<? extends RegistrationDetails> registrationDetailsClass);

    boolean register(RegistrationDetails registrationDetails);

}
