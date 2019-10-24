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

import uk.co.objectconnexions.expressiveobjects.applib.marker.AlwaysImmutable;
import uk.co.objectconnexions.expressiveobjects.applib.marker.ImmutableOncePersisted;
import uk.co.objectconnexions.expressiveobjects.applib.marker.ImmutableUntilPersisted;
import uk.co.objectconnexions.expressiveobjects.applib.marker.NeverImmutable;
import uk.co.objectconnexions.expressiveobjects.applib.util.Enums;

public enum When {
    /**
     * If annotated on a member, then that member should be disabled/hidden for persisted objects, but should be
     * visible for transient objects.
     * 
     * <p>
     * If annotated on an class, then applies to all members of that class.
     */
    ONCE_PERSISTED,
    /**
     * If annotated on a member, then that member should be disabled/hidden for transient objects, but should be
     * visible for persisted objects.
     * 
     * <p>
     * If annotated on an class, then applies to all members of that class.
     */
    UNTIL_PERSISTED,
    /**
     * If annotated on a member, then that member should be disabled/hidden both for objects that are persisted
     * and for objects that are not persisted.
     * 
     * <p>
     * Combines {@link #ONCE_PERSISTED} and {@link #UNTIL_PERSISTED}. 
     * 
     * <p>
     * If annotated on an class, then applies to all members of that class.
     */
    ALWAYS,
    /**
     * If annotated on a member, then that member should not be disabled/hidden.
     * 
     * <p>
     * If annotated on an class, then applies to all members of that class.
     */
    NEVER;

    public String getFriendlyName() {
        return Enums.getFriendlyNameOf(this);
    }
    
    /**
     * As an alternative to annotating an object with {@link Disabled}, can instead have the
     * class implement a marker interface.   
     */
    public static When lookupForMarkerInterface(final Class<?> cls) {
        if (AlwaysImmutable.class.isAssignableFrom(cls)) {
            return ALWAYS;
        } else if (ImmutableOncePersisted.class.isAssignableFrom(cls)) {
            return ONCE_PERSISTED;
        } else if (ImmutableUntilPersisted.class.isAssignableFrom(cls)) {
            return UNTIL_PERSISTED;
        } else if (NeverImmutable.class.isAssignableFrom(cls)) {
            return NEVER;
        }
        return null;
    }

    public interface Persistable {
        public boolean isTransient();
    }
    
    public boolean appliesTo(final Persistable persistable) {
        final boolean isTransient = persistable.isTransient();
        return this == When.ALWAYS || 
               this == When.ONCE_PERSISTED && !isTransient || 
               this == When.UNTIL_PERSISTED && isTransient;
    }
}
