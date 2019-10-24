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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.parseable;

import uk.co.objectconnexions.expressiveobjects.applib.adapters.Parser;
import uk.co.objectconnexions.expressiveobjects.applib.profiles.Localization;
import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSessionProvider;
import uk.co.objectconnexions.expressiveobjects.core.commons.lang.ClassUtil;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.mgr.AdapterManager;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.deployment.DeploymentCategory;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetAbstract;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.parseable.ParseableFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.runtimecontext.ServicesInjector;

public abstract class ParseableFacetAbstract extends FacetAbstract implements ParseableFacet {

    private final Class<?> parserClass;

    // to delegate to
    private final ParseableFacetUsingParser parseableFacetUsingParser;

    public ParseableFacetAbstract(final String candidateParserName, final Class<?> candidateParserClass, final FacetHolder holder, DeploymentCategory deploymentCategory, final AuthenticationSessionProvider authenticationSessionProvider, final ServicesInjector dependencyInjector, final AdapterManager adapterManager) {
        super(ParseableFacet.class, holder, Derivation.NOT_DERIVED);

        this.parserClass = ParserUtil.parserOrNull(candidateParserClass, candidateParserName);
        this.parseableFacetUsingParser = isValid()?
                createParser(holder, deploymentCategory, authenticationSessionProvider, dependencyInjector, adapterManager):null;
    }

    private ParseableFacetUsingParser createParser(final FacetHolder holder, DeploymentCategory deploymentCategory, final AuthenticationSessionProvider authenticationSessionProvider, final ServicesInjector dependencyInjector, final AdapterManager adapterManager) {
        final Parser<?> parser = (Parser<?>) ClassUtil.newInstance(parserClass, FacetHolder.class, holder);
        return new ParseableFacetUsingParser(parser, holder, deploymentCategory, authenticationSessionProvider, dependencyInjector, adapterManager);
    }

    /**
     * Discover whether either of the candidate parser name or class is valid.
     */
    public boolean isValid() {
        return parserClass != null;
    }

    /**
     * Guaranteed to implement the {@link Parser} class, thanks to generics in
     * the applib.
     */
    public Class<?> getParserClass() {
        return parserClass;
    }

    @Override
    protected String toStringValues() {
        return parserClass.getName();
    }

    @Override
    public ObjectAdapter parseTextEntry(final ObjectAdapter original, final String entryText, final Localization localization) {
        return parseableFacetUsingParser.parseTextEntry(original, entryText, localization);
    }

    @Override
    public String parseableTitle(final ObjectAdapter existing) {
        return parseableFacetUsingParser.parseableTitle(existing);
    }
}
