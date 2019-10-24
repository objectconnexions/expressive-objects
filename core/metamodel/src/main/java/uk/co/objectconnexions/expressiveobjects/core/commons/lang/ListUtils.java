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

package uk.co.objectconnexions.expressiveobjects.core.commons.lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public final class ListUtils {
    private static final String DEFAULT_DELIMITER = ",";

    private ListUtils() {
    }

    public static <T> List<T> combine(final List<T> list, final List<T> list2) {
        final List<T> combinedList = new ArrayList<T>();
        combinedList.addAll(list);
        combinedList.addAll(list2);
        return combinedList;
    }

    /**
     * Returns list1 with everything in list2, ignoring duplicates.
     */
    public static <T> List<T> merge(final List<T> list1, final List<T> list2) {
        for (final T obj : list2) {
            if (!(list1.contains(obj))) {
                list1.add(obj);
            }
        }
        return list1;
    }

    public static List<String> merge(final String[] array1, final String[] array2) {
        final List<String> prefixes = new ArrayList<String>();
        addNoDuplicates(array1, prefixes);
        addNoDuplicates(array2, prefixes);
        return prefixes;
    }

    private static void addNoDuplicates(final String[] array, final List<String> list) {
        for (int i = 0; i < array.length; i++) {
            if (!list.contains(array[i])) {
                list.add(array[i]);
            }
        }
    }

    public static List<Object> asList(final Object[] objectArray) {
        final List<Object> list = new ArrayList<Object>();
        for (final Object element : objectArray) {
            if (Collection.class.isAssignableFrom(element.getClass())) {
                @SuppressWarnings("rawtypes")
                final Collection collection = (Collection) element;
                list.addAll(asList(collection.toArray()));
            } else {
                list.add(element);
            }
        }
        return list;
    }

    /**
     * @see #listToString(List, String)
     * @see #stringToList(String)
     */
    public static String listToString(final List<String> list) {
        return listToString(list, DEFAULT_DELIMITER);
    }

    /**
     * @see #listToString(List, String)
     * @see #stringToList(String)
     */
    public static String listToString(final List<String> list, final String delimiter) {
        if (list.size() == 0) {
            return null;
        }
        final StringBuilder buf = new StringBuilder();
        boolean first = true;
        for (final String str : list) {
            if (first) {
                first = false;
            } else {
                buf.append(delimiter);
            }
            buf.append(str);
        }
        return buf.toString();
    }

    /**
     * @see #stringToList(String, String)
     * @see #listToString(List)
     */
    public static List<String> stringToList(final String commaSeparated) {
        return appendDelimitedStringToList(commaSeparated, new ArrayList<String>());
    }

    /**
     * @see #stringToList(String)
     * @see #listToString(List, String)
     */
    public static List<String> stringToList(final String delimited, final String delimiter) {
        return appendDelimitedStringToList(delimited, delimiter, new ArrayList<String>());
    }

    /**
     * @see #appendDelimitedStringToList(String, String, List)
     */
    public static List<String> appendDelimitedStringToList(final String commaSeparated, final List<String> list) {
        return appendDelimitedStringToList(commaSeparated, DEFAULT_DELIMITER, list);
    }

    public static List<String> appendDelimitedStringToList(final String delimited, final String delimiter, final List<String> list) {
        if (delimited == null) {
            return list;
        }
        final String[] optionValues = delimited.split(delimiter);
        list.addAll(Arrays.asList(optionValues));
        return list;
    }

}
