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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.encodeable;

import uk.co.objectconnexions.expressiveobjects.applib.adapters.EncoderDecoder;
import uk.co.objectconnexions.expressiveobjects.core.commons.lang.ClassUtil;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.mgr.AdapterManager;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetAbstract;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.encodeable.EncodableFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.runtimecontext.ServicesInjector;

public abstract class EncodableFacetAbstract extends FacetAbstract implements EncodableFacet {

    private final Class<?> encoderDecoderClass;

    // to delegate to
    private final EncodableFacetUsingEncoderDecoder encodeableFacetUsingEncoderDecoder;

    private final AdapterManager adapterManager;
    private final ServicesInjector dependencyInjector;

    public EncodableFacetAbstract(final String candidateEncoderDecoderName, final Class<?> candidateEncoderDecoderClass, final FacetHolder holder, final AdapterManager adapterManager, final ServicesInjector dependencyInjector) {
        super(EncodableFacet.class, holder, Derivation.NOT_DERIVED);
        this.adapterManager = adapterManager;
        this.dependencyInjector = dependencyInjector;

        this.encoderDecoderClass = EncoderDecoderUtil.encoderDecoderOrNull(candidateEncoderDecoderClass, candidateEncoderDecoderName);
        if (isValid()) {
            final EncoderDecoder<?> encoderDecoder = (EncoderDecoder<?>) ClassUtil.newInstance(encoderDecoderClass, FacetHolder.class, holder);
            this.encodeableFacetUsingEncoderDecoder = new EncodableFacetUsingEncoderDecoder(encoderDecoder, holder, getAdapterManager(), getDependencyInjector());
        } else {
            this.encodeableFacetUsingEncoderDecoder = null;
        }
    }

    /**
     * Discover whether either of the candidate encoder/decoder name or class is
     * valid.
     */
    public boolean isValid() {
        return encoderDecoderClass != null;
    }

    /**
     * Guaranteed to implement the {@link EncoderDecoder} class, thanks to
     * generics in the applib.
     */
    public Class<?> getEncoderDecoderClass() {
        return encoderDecoderClass;
    }

    @Override
    protected String toStringValues() {
        return encoderDecoderClass.getName();
    }

    @Override
    public ObjectAdapter fromEncodedString(final String encodedData) {
        return encodeableFacetUsingEncoderDecoder.fromEncodedString(encodedData);
    }

    @Override
    public String toEncodedString(final ObjectAdapter object) {
        return encodeableFacetUsingEncoderDecoder.toEncodedString(object);
    }

    public AdapterManager getAdapterManager() {
        return adapterManager;
    }

    public ServicesInjector getDependencyInjector() {
        return dependencyInjector;
    }

}
