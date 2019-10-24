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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.common.collect.Maps;

import org.jboss.resteasy.client.core.BaseClientResponse;

import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.util.Parser;

public final class RestfulRequest {

    public enum DomainModel {
        NONE, SIMPLE, FORMAL, SELECTABLE;

        public static Parser<DomainModel> parser() {
            return new Parser<RestfulRequest.DomainModel>() {

                @Override
                public DomainModel valueOf(final String str) {
                    return DomainModel.valueOf(str.toUpperCase());
                }

                @Override
                public String asString(final DomainModel t) {
                    return t.name().toLowerCase();
                }
            };
        }

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    public static class RequestParameter<Q> {

        public static RequestParameter<List<List<String>>> FOLLOW_LINKS = new RequestParameter<List<List<String>>>("x-ro-follow-links", Parser.forListOfListOfStrings(), Collections.<List<String>> emptyList());
        public static RequestParameter<Integer> PAGE = new RequestParameter<Integer>("x-ro-page", Parser.forInteger(), 1);
        public static RequestParameter<Integer> PAGE_SIZE = new RequestParameter<Integer>("x-ro-page-size", Parser.forInteger(), 25);
        public static RequestParameter<List<String>> SORT_BY = new RequestParameter<List<String>>("x-ro-sort-by", Parser.forListOfStrings(), Collections.<String> emptyList());
        public static RequestParameter<DomainModel> DOMAIN_MODEL = new RequestParameter<DomainModel>("x-ro-domain-model", DomainModel.parser(), DomainModel.SIMPLE);
        public static RequestParameter<Boolean> VALIDATE_ONLY = new RequestParameter<Boolean>("x-ro-validate-only", Parser.forBoolean(), false);

        private final String name;
        private final Parser<Q> parser;
        private final Q defaultValue;

        private RequestParameter(final String name, final Parser<Q> parser, final Q defaultValue) {
            this.name = name;
            this.parser = parser;
            this.defaultValue = defaultValue;
        }

        public String getName() {
            return name;
        }

        public Parser<Q> getParser() {
            return parser;
        }

        public Q valueOf(final JsonRepresentation parameterRepresentation) {
            if (parameterRepresentation == null) {
                return defaultValue;
            }
            if (!parameterRepresentation.isMap()) {
                return defaultValue;
            }
            final Q parsedValue = getParser().valueOf(parameterRepresentation.getRepresentation(getName()));
            return parsedValue != null ? parsedValue : defaultValue;
        }

        public Q getDefault() {
            return defaultValue;
        }

        @Override
        public String toString() {
            return getName();
        }
    }

    public static class Header<X> {
        public static Header<String> IF_MATCH = new Header<String>("If-Match", Parser.forString());
        public static Header<List<MediaType>> ACCEPT = new Header<List<MediaType>>("Accept", Parser.forListOfMediaTypes());

        private final String name;
        private final Parser<X> parser;

        /**
         * public visibility for testing purposes only.
         */
        public Header(final String name, final Parser<X> parser) {
            this.name = name;
            this.parser = parser;
        }

        public String getName() {
            return name;
        }

        public Parser<X> getParser() {
            return parser;
        }

        void setHeader(final ClientRequestConfigurer clientRequestConfigurer, final X t) {
            clientRequestConfigurer.header(getName(), parser.asString(t));
        }

        @Override
        public String toString() {
            return getName();
        }
    }

    private final ClientRequestConfigurer clientRequestConfigurer;
    private final Map<RequestParameter<?>, Object> args = Maps.newLinkedHashMap();

    public RestfulRequest(final ClientRequestConfigurer clientRequestConfigurer) {
        this.clientRequestConfigurer = clientRequestConfigurer;
    }

    public <T> RestfulRequest withHeader(final Header<T> header, final T t) {
        header.setHeader(clientRequestConfigurer, t);
        return this;
    }

    public <T> RestfulRequest withHeader(final Header<List<T>> header, final T... ts) {
        header.setHeader(clientRequestConfigurer, Arrays.asList(ts));
        return this;
    }

    public <Q> RestfulRequest withArg(final RestfulRequest.RequestParameter<Q> queryParam, final String argStrFormat, final Object... args) {
        final String argStr = String.format(argStrFormat, args);
        final Q arg = queryParam.getParser().valueOf(argStr);
        return withArg(queryParam, arg);
    }

    public <Q> RestfulRequest withArg(final RestfulRequest.RequestParameter<Q> queryParam, final Q arg) {
        args.put(queryParam, arg);
        return this;
    }

    public RestfulResponse<JsonRepresentation> execute() {
        try {
            if (!args.isEmpty()) {
                clientRequestConfigurer.configureArgs(args);
            }
            final Response response = clientRequestConfigurer.getClientRequest().execute();

            // this is a bit hacky
            @SuppressWarnings("unchecked")
            final BaseClientResponse<String> restEasyResponse = (BaseClientResponse<String>) response;
            restEasyResponse.setReturnType(String.class);

            return RestfulResponse.ofT(response);
        } catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends JsonRepresentation> RestfulResponse<T> executeT() {
        final RestfulResponse<JsonRepresentation> restfulResponse = execute();
        return (RestfulResponse<T>) restfulResponse;
    }

    /**
     * For testing only.
     */
    ClientRequestConfigurer getClientRequestConfigurer() {
        return clientRequestConfigurer;
    }

}
