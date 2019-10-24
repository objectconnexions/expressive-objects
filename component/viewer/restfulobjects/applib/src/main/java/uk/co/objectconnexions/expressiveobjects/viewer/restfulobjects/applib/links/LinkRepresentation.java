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
package uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.links;

import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.jboss.resteasy.client.ClientExecutor;

import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.ClientRequestConfigurer;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.HttpMethod;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.JsonRepresentation;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.RestfulRequest;
import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.RestfulResponse;

public final class LinkRepresentation extends JsonRepresentation {

    public LinkRepresentation() {
        this(new ObjectNode(JsonNodeFactory.instance));
        withMethod(HttpMethod.GET);
    }

    public LinkRepresentation(final JsonNode jsonNode) {
        super(jsonNode);
    }

    public String getRel() {
        return asObjectNode().path("rel").getTextValue();
    }

    public LinkRepresentation withRel(final String rel) {
        asObjectNode().put("rel", rel);
        return this;
    }

    public String getHref() {
        return asObjectNode().path("href").getTextValue();
    }

    public LinkRepresentation withHref(final String href) {
        asObjectNode().put("href", href);
        return this;
    }

    public JsonRepresentation getValue() {
        return getRepresentation("value");
    }

    public String getTitle() {
        return getString("title");
    }

    public LinkRepresentation withTitle(final String title) {
        asObjectNode().put("title", title);
        return this;
    }

    public HttpMethod getHttpMethod() {
        final String methodStr = asObjectNode().path("method").getTextValue();
        return HttpMethod.valueOf(methodStr);
    }

    public MediaType getType() {
        final String typeStr = asObjectNode().path("type").getTextValue();
        if (typeStr == null) {
            return MediaType.APPLICATION_JSON_TYPE;
        }
        return MediaType.valueOf(typeStr);
    }

    public LinkRepresentation withMethod(final HttpMethod httpMethod) {
        asObjectNode().put("method", httpMethod.name());
        return this;
    }

    /**
     * Returns the &quot;arguments&quot; json-property of the link (a map).
     * 
     * <p>
     * If there is no &quot;arguments&quot; node, then as a convenience will
     * return an empty map.
     * 
     * @return
     */
    public JsonRepresentation getArguments() {
        final JsonNode arguments = asObjectNode().get("arguments");
        if (arguments.isNull()) {
            return JsonRepresentation.newMap();
        }
        return new JsonRepresentation(arguments);
    }

    public <T> RestfulResponse<JsonRepresentation> follow(final ClientExecutor executor) throws Exception {
        return follow(executor, null);
    }

    public <T extends JsonRepresentation> RestfulResponse<T> follow(final ClientExecutor executor, final JsonRepresentation requestArgs) throws Exception {

        final ClientRequestConfigurer clientRequestConfigurer = ClientRequestConfigurer.create(executor, getHref());

        clientRequestConfigurer.accept(MediaType.APPLICATION_JSON_TYPE);
        clientRequestConfigurer.setHttpMethod(getHttpMethod());

        clientRequestConfigurer.configureArgs(requestArgs);

        final RestfulRequest restfulRequest = new RestfulRequest(clientRequestConfigurer);
        return restfulRequest.executeT();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getHref() == null) ? 0 : getHref().hashCode());
        result = prime * result + ((getHttpMethod() == null) ? 0 : getHttpMethod().hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LinkRepresentation other = (LinkRepresentation) obj;
        if (getHref() == null) {
            if (other.getHref() != null) {
                return false;
            }
        } else if (!getHref().equals(other.getHref())) {
            return false;
        }
        if (getHttpMethod() != other.getHttpMethod()) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Link [rel=" + getRel() + ", href=" + getHref() + ", method=" + getHttpMethod() + ", type=" + getType() + "]";
    }

}
