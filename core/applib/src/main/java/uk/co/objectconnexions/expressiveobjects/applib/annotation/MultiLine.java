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

package uk.co.objectconnexions.expressiveobjects.applib.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a string property may have more than one line (ie may contain
 * carriage returns).
 * 
 * <p>
 * In addition you can specify the typical number of lines (defaults to 6) and
 * whether the lines should not be wrapped (by default they will not be
 * wrapped).
 * 
 * <p>
 * Can also be specified for types that are annotated as <tt>@Value</tt> types.
 * To apply, the value must have string semantics.
 * 
 * <p>
 * Note that if this annotation is applied, then any choices for the property
 * (ie as per a <tt>choicesXxx</tt> method) will be ignored.
 */
@Inherited
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface MultiLine {

    int numberOfLines() default 6;

    boolean preventWrapping() default true;
}
