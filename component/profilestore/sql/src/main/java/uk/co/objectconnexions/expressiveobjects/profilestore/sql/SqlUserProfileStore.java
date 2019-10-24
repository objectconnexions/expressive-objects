/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package uk.co.objectconnexions.expressiveobjects.profilestore.sql;

import com.google.inject.Inject;

import uk.co.objectconnexions.expressiveobjects.core.commons.config.ExpressiveObjectsConfiguration;
import uk.co.objectconnexions.expressiveobjects.core.runtime.userprofile.UserProfile;
import uk.co.objectconnexions.expressiveobjects.core.runtime.userprofile.UserProfileStore;

/**
 * 
 * 
 * @version $Rev$ $Date$
 */
public class SqlUserProfileStore implements UserProfileStore {

    @Inject
    public SqlUserProfileStore(final ExpressiveObjectsConfiguration configuration) {

    }

    @Override
    public boolean isFixturesInstalled() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void save(final String userName, final UserProfile userProfile) {
        // TODO Auto-generated method stub

    }

    @Override
    public UserProfile getUserProfile(final String userName) {
        // TODO Auto-generated method stub
        return null;
    }

}
