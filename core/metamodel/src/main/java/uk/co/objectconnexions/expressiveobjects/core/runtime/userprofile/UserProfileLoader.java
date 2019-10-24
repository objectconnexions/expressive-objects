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

package uk.co.objectconnexions.expressiveobjects.core.runtime.userprofile;

import java.util.List;

import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSession;
import uk.co.objectconnexions.expressiveobjects.core.commons.components.ApplicationScopedComponent;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;

/**
 * Intended to acts like a bridge, loading the profile from the underlying
 * store.
 * 
 * <p>
 * This is an interface only to make it easy to mock in tests. In practice there
 * is only a single implementation, {@link UserProfileLoaderDefault}.
 */
public interface UserProfileLoader extends ApplicationScopedComponent {

    // //////////////////////////////////////////////////////
    // Fixtures
    // //////////////////////////////////////////////////////

    /**
     * @see PersistenceSession#isFixturesInstalled()
     */
    public boolean isFixturesInstalled();

    // //////////////////////////////////////////////////////
    // saveAs...
    // //////////////////////////////////////////////////////

    public void saveAsDefault(UserProfile userProfile);

    public void saveForUser(String userName, UserProfile userProfile);

    // //////////////////////////////////////////////////////
    // saveSession
    // //////////////////////////////////////////////////////

    public void saveSession(List<ObjectAdapter> objects);

    // //////////////////////////////////////////////////////
    // getProfile
    // //////////////////////////////////////////////////////

    public UserProfile getProfile(AuthenticationSession session);

    @Deprecated
    public UserProfile getProfile();

    // //////////////////////////////////////////////////////
    // Services
    // //////////////////////////////////////////////////////

    public void setServices(List<Object> servicesList);

    public List<Object> getServices();

}
