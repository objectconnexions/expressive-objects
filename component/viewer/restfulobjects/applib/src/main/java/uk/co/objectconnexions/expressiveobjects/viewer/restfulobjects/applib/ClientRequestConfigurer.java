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
package uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib;

import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;

import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.specimpl.UriBuilderImpl;

import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.RestfulRequest.RequestParameter;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.links.LinkRepresentation;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.util.UrlEncodingUtils;

/**
 * Configures the body, query string etc of a {@link ClientRequest}.
 * 
 * <p>
 * Needed because, unfortunately, {@link ClientRequest} does not seem to allow
 * the query string to be set directly (only
 * {@link ClientRequest#getQueryParameters() query parameters}). Instead, it is
 * necessary to {@link UriBuilderImpl#replaceQuery(String) use} its underlying
 * {@link UriBuilderImpl}.
 */
public class ClientRequestConfigurer {

    public static ClientRequestConfigurer create(final ClientExecutor executor, final String uriTemplate) {
        final UriBuilder uriBuilder = new UriBuilderImpl().uriTemplate(uriTemplate);
        final ClientRequest clientRequest = executor.createRequest(uriBuilder);
        return new ClientRequestConfigurer(clientRequest, uriBuilder);
    }

    private final ClientRequest clientRequest;
    private final UriBuilder uriBuilder;

    ClientRequestConfigurer(final ClientRequest clientRequest, final UriBuilder uriBuilder) {
        this.clientRequest = clientRequest;
        this.uriBuilder = uriBuilder;
    }

    public ClientRequestConfigurer accept(final MediaType mediaType) {
        clientRequest.accept(mediaType);
        return this;
    }

    public ClientRequestConfigurer header(final String name, final String value) {
        clientRequest.header(name, value);
        return this;
    }

    /**
     * Prerequisite to {@link #configureArgs(JsonRepresentation)} or
     * {@link #configureArgs(Map)}.
     */
    public ClientRequestConfigurer setHttpMethod(final HttpMethod httpMethod) {
        clientRequest.setHttpMethod(httpMethod.getJavaxRsMethod());
        return this;
    }

    /**
     * Used when creating a request with arguments to execute.
     * 
     * <p>
     * Typical flow is:
     * <ul>
     * <li> {@link RestfulClient#createRequest(HttpMethod, String)}
     * <li> {@link RestfulRequest#withArg(RequestParameter, Object)} for each arg
     * <li> {@link RestfulRequest#execute()} - which calls this method.
     * </ul>
     */
    public ClientRequestConfigurer configureArgs(final Map<RequestParameter<?>, Object> args) {
        if (clientRequest.getHttpMethod() == null) {
            throw new IllegalStateException("Must set up http method first");
        }

        final JsonRepresentation argsAsMap = JsonRepresentation.newMap();
        for (final RequestParameter<?> requestParam : args.keySet()) {
            put(args, requestParam, argsAsMap);
        }
        getHttpMethod().setUpArgs(this, argsAsMap);
        return this;
    }

    private <P> void put(final Map<RequestParameter<?>, Object> args, final RequestParameter<P> requestParam, final JsonRepresentation argsAsMap) {
        @SuppressWarnings("unchecked")
        final P value = (P) args.get(requestParam);
        final String valueStr = requestParam.getParser().asString(value);
        argsAsMap.mapPut(requestParam.getName(), valueStr);
    }

    /**
     * Used when following links (
     * {@link RestfulClient#follow(LinkRepresentation)}).
     */
    public ClientRequestConfigurer configureArgs(final JsonRepresentation requestArgs) {
        if (clientRequest.getHttpMethod() == null) {
            throw new IllegalStateException("Must set up http method first");
        }

        getHttpMethod().setUpArgs(this, requestArgs);
        return this;
    }

    /**
     * Called back from
     * {@link HttpMethod#setUpArgs(ClientRequestConfigurer, JsonRepresentation)}
     */
    ClientRequestConfigurer body(final JsonRepresentation requestArgs) {
        clientRequest.body(MediaType.APPLICATION_JSON_TYPE, requestArgs.toString());
        return this;
    }

    /**
     * Called back from
     * {@link HttpMethod#setUpArgs(ClientRequestConfigurer, JsonRepresentation)}
     */
    ClientRequestConfigurer queryString(final JsonRepresentation requestArgs) {
        if (requestArgs.size() == 0) {
            return this;
        }
        final String queryString = UrlEncodingUtils.urlEncode(requestArgs.toString());
        uriBuilder.replaceQuery(queryString);
        return this;
    }

    /**
     * Called back from
     * {@link HttpMethod#setUpArgs(ClientRequestConfigurer, JsonRepresentation)}
     */
    ClientRequestConfigurer queryArgs(final JsonRepresentation requestArgs) {
        final MultivaluedMap<String, String> queryParameters = clientRequest.getQueryParameters();
        for (final Map.Entry<String, JsonRepresentation> entry : requestArgs.mapIterable()) {
            final String param = entry.getKey();
            final JsonRepresentation argRepr = entry.getValue();
            final String arg = UrlEncodingUtils.urlEncode(argRepr.asArg());
            queryParameters.add(param, arg);
        }
        return this;
    }

    /**
     * For testing.
     */
    ClientRequest getClientRequest() {
        return clientRequest;
    }

    HttpMethod getHttpMethod() {
        final String httpMethod = clientRequest.getHttpMethod();
        return HttpMethod.valueOf(httpMethod);
    }

}