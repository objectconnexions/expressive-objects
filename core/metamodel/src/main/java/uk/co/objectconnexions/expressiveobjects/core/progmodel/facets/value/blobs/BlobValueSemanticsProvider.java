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

package uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.blobs;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.apache.commons.codec.binary.Base64;
import uk.co.objectconnexions.expressiveobjects.applib.adapters.DefaultsProvider;
import uk.co.objectconnexions.expressiveobjects.applib.adapters.EncoderDecoder;
import uk.co.objectconnexions.expressiveobjects.applib.adapters.Parser;
import uk.co.objectconnexions.expressiveobjects.applib.profiles.Localization;
import uk.co.objectconnexions.expressiveobjects.applib.value.Blob;
import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolder;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.value.ValueSemanticsProviderAndFacetAbstract;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.value.ValueSemanticsProviderContext;

public class BlobValueSemanticsProvider extends ValueSemanticsProviderAndFacetAbstract<Blob> implements BlobValueFacet {

    private static final int TYPICAL_LENGTH = 0;

    private static Class<? extends Facet> type() {
        return BlobValueFacet.class;
    }

    private static final Blob DEFAULT_VALUE = null;

    /**
     * Required because implementation of {@link Parser} and
     * {@link EncoderDecoder}.
     */
    public BlobValueSemanticsProvider() {
        this(null, null, null);
    }

    public BlobValueSemanticsProvider(final FacetHolder holder, final ExpressiveObjectsConfiguration configuration, final ValueSemanticsProviderContext context) {
        super(type(), holder, Blob.class, TYPICAL_LENGTH, Immutability.IMMUTABLE, EqualByContent.NOT_HONOURED, DEFAULT_VALUE, configuration, context);
    }

    @Override
    public String titleString(final Object object, final Localization localization) {
        return object != null? ((Blob)object).getName(): "[null]";
    }

    @Override
    public String titleStringWithMask(final Object value, final String usingMask) {
        return titleString(value, null);
    }


    // //////////////////////////////////////////////////////////////////
    // Parser
    // //////////////////////////////////////////////////////////////////

    @Override
    public Parser<Blob> getParser() {
        return null;
    }

    // //////////////////////////////////////////////////////////////////
    // DefaultsProvider
    // //////////////////////////////////////////////////////////////////
    
    @Override
    public DefaultsProvider<Blob> getDefaultsProvider() {
        return null;
    }
    
    // //////////////////////////////////////////////////////////////////
    // EncoderDecoder
    // //////////////////////////////////////////////////////////////////

    @Override
    protected String doEncode(final Object object) {
        Blob blob = (Blob)object;
        return blob.getName() + ":" + blob.getMimeType().getBaseType() + ":" + Base64.encodeBase64String((blob.getBytes()));
    }

    @Override
    protected Blob doRestore(final String data) {
        final int colonIdx = data.indexOf(':');
        final String name  = data.substring(0, colonIdx);
        final int colon2Idx  = data.indexOf(":", colonIdx+1);
        final String mimeTypeBase = data.substring(colonIdx+1, colon2Idx);
        final byte[] bytes = Base64.decodeBase64(data.substring(colon2Idx+1));
        try {
            return new Blob(name, new MimeType(mimeTypeBase), bytes);
        } catch (MimeTypeParseException e) {
            throw new RuntimeException(e);
        }
    }

    // /////// toString ///////

    @Override
    public String toString() {
        return "BlobValueSemanticsProvider";
    }

}
