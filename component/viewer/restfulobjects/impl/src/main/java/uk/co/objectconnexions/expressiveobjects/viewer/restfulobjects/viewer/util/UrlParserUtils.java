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
package uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.viewer.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.JsonRepresentation;

public final class UrlParserUtils {

    private final static Pattern OBJECT_OID = Pattern.compile(".*objects\\/(.+)");;
    private final static Pattern DOMAIN_TYPE = Pattern.compile(".*domainTypes\\/([^/]+).*");;

    public final static String oidFromLink(final JsonRepresentation link) {
        final String href = link.getString("href");
        final Matcher matcher = OBJECT_OID.matcher(href);
        if (!matcher.matches()) {
            return null;
        }
        return matcher.group(1);
    }

    public final static String domainTypeFrom(final JsonRepresentation link) {
        return domainTypeFrom(link.getString("href"));
    }

    public static String domainTypeFrom(final String href) {
        final Matcher matcher = DOMAIN_TYPE.matcher(href);
        if (!matcher.matches()) {
            return null;
        }
        return matcher.group(1);
    }

}
