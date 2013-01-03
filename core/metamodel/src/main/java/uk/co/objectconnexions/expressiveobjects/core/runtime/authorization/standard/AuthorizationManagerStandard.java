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

package uk.co.objectconnexions.expressiveobjects.core.runtime.authorization.standard;

import uk.co.objectconnexions.expressiveobjects.applib.Identifier;
import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSession;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.commons.debug.DebugBuilder;
import uk.co.objectconnexions.expressiveobjects.core.commons.debug.DebuggableWithTitle;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.progmodel.ProgrammingModel;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.validator.MetaModelValidatorComposite;
import uk.co.objectconnexions.expressiveobjects.core.runtime.authorization.AuthorizationManagerAbstract;

public class AuthorizationManagerStandard extends AuthorizationManagerAbstract implements DebuggableWithTitle {

    private Authorizor authorizor;

    // /////////////////////////////////////////////////////////
    // Constructor
    // /////////////////////////////////////////////////////////

    public AuthorizationManagerStandard(final ExpressiveObjectsConfiguration configuration) {
        super(configuration);
        // avoid null pointers
        authorizor = new Authorizor() {

            @Override
            public void init() {
            }

            @Override
            public void shutdown() {
            }

            @Override
            public boolean isVisibleInRole(final String user, final Identifier identifier) {
                return true;
            }

            @Override
            public boolean isUsableInRole(final String role, final Identifier identifier) {
                return true;
            }

            @Override
            public boolean isVisibleInAnyRole(Identifier identifier) {
                return true;
            }

            @Override
            public boolean isUsableInAnyRole(Identifier identifier) {
                return true;
            }
        };
    }

    // /////////////////////////////////////////////////////////
    // init, shutddown
    // /////////////////////////////////////////////////////////

    @Override
    public void init() {
        authorizor.init();
    }

    @Override
    public void shutdown() {
        authorizor.shutdown();
    }

    // /////////////////////////////////////////////////////////
    // API
    // /////////////////////////////////////////////////////////

    @Override
    public boolean isUsable(final AuthenticationSession session, final ObjectAdapter target, final Identifier identifier) {
        if (isPerspectiveMember(identifier)) {
            return true;
        }
        if (authorizor.isUsableInAnyRole(identifier)) {
            return true;
        }
        for (final String roleName : session.getRoles()) {
            if (authorizor.isUsableInRole(roleName, identifier)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isVisible(final AuthenticationSession session, final ObjectAdapter target, final Identifier identifier) {
        if (isPerspectiveMember(identifier)) {
            return true;
        }
        if (authorizor.isVisibleInAnyRole(identifier)) {
            return true;
        }
        for (final String roleName : session.getRoles()) {
            if (authorizor.isVisibleInRole(roleName, identifier)) {
                return true;
            }
        }
        return false;
    }

    private boolean isPerspectiveMember(final Identifier identifier) {
        return (identifier.getClassName().equals(""));
    }


    // //////////////////////////////////////////////////
    // MetaModelRefiner impl
    // //////////////////////////////////////////////////

    @Override
    public void refineMetaModelValidator(MetaModelValidatorComposite baseMetaModelValidator, ExpressiveObjectsConfiguration configuration) {
        // no-op
    }

    @Override
    public void refineProgrammingModel(ProgrammingModel baseProgrammingModel, ExpressiveObjectsConfiguration configuration) {
        final AuthorizationFacetFactory facetFactory = new AuthorizationFacetFactory(this);
        baseProgrammingModel.addFactory(facetFactory);
    }

    // //////////////////////////////////////////////////////////
    // Debugging
    // //////////////////////////////////////////////////////////

    @Override
    public String debugTitle() {
        return "Authorization Manager";
    }

    @Override
    public void debugData(final DebugBuilder debug) {
        debug.appendTitle("Authorizor Manager");
        debug.appendln("Authorizer", authorizor);
    }

    // //////////////////////////////////////////////////
    // Dependencies (injected)
    // //////////////////////////////////////////////////

    protected void setAuthorizor(final Authorizor authorisor) {
        this.authorizor = authorisor;
    }

}
