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

import uk.co.objectconnexions.expressiveobjects.applib.marker.NonPersistable;
import uk.co.objectconnexions.expressiveobjects.applib.marker.ProgramPersistable;
import uk.co.objectconnexions.expressiveobjects.applib.util.Enums;

/**
 * Indicates that an instance cannot be persisted by a user, but only
 * programmatically.
 */
@Inherited
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface NotPersistable {

    public enum By {
        USER, 
        USER_OR_PROGRAM;
        
        public static By lookupForMarkerInterface(final Class<?> cls) {
            if (ProgramPersistable.class.isAssignableFrom(cls)) {
                return USER;
            } else if (NonPersistable.class.isAssignableFrom(cls)) {
                return USER_OR_PROGRAM;
            }
            return null;
        }
        
        public String getFriendlyName() {
            return Enums.getFriendlyNameOf(this);
        }
    }

    By value() default By.USER_OR_PROGRAM;

}
