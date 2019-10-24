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

package uk.co.objectconnexions.expressiveobjects.applib.fixtures;

import uk.co.objectconnexions.expressiveobjects.applib.fixtures.userprofile.UserProfileService;
import uk.co.objectconnexions.expressiveobjects.applib.fixtures.userprofile.UserProfileServiceAware;
import uk.co.objectconnexions.expressiveobjects.applib.profiles.Profile;

public abstract class UserProfileFixture extends BaseFixture implements UserProfileServiceAware {

    private UserProfileService profileService;

    public UserProfileFixture() {
        super(FixtureType.USER_PROFILES);
    }

    @Override
    public void setService(final UserProfileService profileService) {
        this.profileService = profileService;
    }

    @Override
    public final void install() {
        installProfiles();
    }

    protected abstract void installProfiles();

    protected Profile newUserProfile() {
        return profileService.newUserProfile();
    }

    protected Profile newUserProfile(final Profile profile) {
        return profileService.newUserProfile(profile);
    }

    protected void saveForUser(final String name, final Profile profile) {
        profileService.saveForUser(name, profile);
    }

    protected void saveAsDefault(final Profile profile) {
        profileService.saveAsDefault(profile);
    }

}
