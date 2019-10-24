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
package uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.util;

import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;

import uk.co.objectconnexions.expressiveobjects.viewer.restfulobjects.applib.JsonRepresentation;

public class PathNode {
    private static final Pattern NODE = Pattern.compile("^([^\\[]*)(\\[(.+)\\])?$");
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");
    private static final Pattern KEY_VALUE = Pattern.compile("^([^=]+)=(.+)$");

    public static final PathNode NULL = new PathNode("", Collections.<String, String> emptyMap());

    public static PathNode parse(final String path) {
        final Matcher nodeMatcher = NODE.matcher(path);
        if (!nodeMatcher.matches()) {
            return null;
        }
        final int groupCount = nodeMatcher.groupCount();
        if (groupCount < 1) {
            return null;
        }
        final String key = nodeMatcher.group(1);
        final Map<String, String> criteria = Maps.newHashMap();
        final String criteriaStr = nodeMatcher.group(3);
        if (criteriaStr != null) {
            for (final String criterium : Splitter.on(WHITESPACE).split(criteriaStr)) {
                final Matcher keyValueMatcher = KEY_VALUE.matcher(criterium);
                if (keyValueMatcher.matches()) {
                    criteria.put(keyValueMatcher.group(1), keyValueMatcher.group(2));
                }
            }
        }

        return new PathNode(key, criteria);
    }

    private final String key;
    private final Map<String, String> criteria;

    private PathNode(final String key, final Map<String, String> criteria) {
        this.key = key;
        this.criteria = Collections.unmodifiableMap(criteria);
    }

    public String getKey() {
        return key;
    }

    public Map<String, String> getCriteria() {
        return criteria;
    }

    public boolean hasCriteria() {
        return !getCriteria().isEmpty();
    }

    public boolean matches(final JsonRepresentation repr) {
        if (!repr.isMap()) {
            return false;
        }
        for (final Map.Entry<String, String> criterium : getCriteria().entrySet()) {
            final String requiredValue = criterium.getValue();
            final String actualValue = repr.getString(criterium.getKey());
            if (!Objects.equal(requiredValue, actualValue)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
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
        final PathNode other = (PathNode) obj;
        if (key == null) {
            if (other.key != null) {
                return false;
            }
        } else if (!key.equals(other.key)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return key + (criteria.isEmpty() ? "" : criteria);
    }

}