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

package uk.co.objectconnexions.expressiveobjects.core.runtime.fixtures;

import uk.co.objectconnexions.expressiveobjects.applib.fixtures.userprofile.UserProfileService;
import uk.co.objectconnexions.expressiveobjects.applib.fixtures.userprofile.UserProfileServiceAware;
import uk.co.objectconnexions.expressiveobjects.applib.profiles.Perspective;
import uk.co.objectconnexions.expressiveobjects.applib.profiles.Profile;
import uk.co.objectconnexions.expressiveobjects.core.commons.exceptions.ExpressiveObjectsException;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.context.ExpressiveObjectsContext;
import uk.co.objectconnexions.expressiveobjects.core.runtime.system.persistence.PersistenceSession;
import uk.co.objectconnexions.expressiveobjects.core.runtime.userprofile.PerspectiveEntry;
import uk.co.objectconnexions.expressiveobjects.core.runtime.userprofile.UserProfile;
import uk.co.objectconnexions.expressiveobjects.core.runtime.userprofile.UserProfileLoader;

public class ProfileServiceImpl implements UserProfileService {

    @Override
    public Profile newUserProfile() {
        return new ProfileImpl();
    }

    @Override
    public Profile newUserProfile(final Profile profileTemplate) {
        return new ProfileImpl((ProfileImpl) profileTemplate);
    }

    @Override
    public void saveAsDefault(final Profile profile) {
        getUserProfileLoader().saveAsDefault(createUserProfile(profile));
    }

    @Override
    public void saveForUser(final String name, final Profile profile) {
        getUserProfileLoader().saveForUser(name, createUserProfile(profile));
    }

    private UserProfile createUserProfile(final Profile profile) {
        return ((ProfileImpl) profile).getUserProfile();
    }

    public void injectInto(final Object fixture) {
        if (fixture instanceof UserProfileServiceAware) {
            final UserProfileServiceAware serviceAware = (UserProfileServiceAware) fixture;
            serviceAware.setService(this);
        }
    }

    private static UserProfileLoader getUserProfileLoader() {
        return ExpressiveObjectsContext.getUserProfileLoader();
    }

}

class ProfileImpl implements Profile {
    private final UserProfile userProfile;

    public ProfileImpl(final ProfileImpl profileTemplate) {
        this();
        userProfile.copy(profileTemplate.userProfile);
    }

    public ProfileImpl() {
        userProfile = new UserProfile();
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    @Override
    public void addToOptions(final String name, final String value) {
        userProfile.addToOptions(name, value);
    }

    @Override
    public void addToPerspectives(final Perspective perspective) {
        userProfile.addToPerspectives(((PerspectiveImpl) perspective).getPerspectiveEntry());
    }

    @Override
    public Perspective getPerspective(final String name) {
        final PerspectiveEntry perspectiveEntry = userProfile.getPerspective(name);
        if (perspectiveEntry == null) {
            throw new ExpressiveObjectsException("No perspective found for " + name);
        }
        return new PerspectiveImpl(perspectiveEntry);
    }

    @Override
    public Perspective newPerspective(final String name) {
        final PerspectiveEntry perspectiveEntry = userProfile.newPerspective(name);
        return new PerspectiveImpl(perspectiveEntry);
    }

}

class PerspectiveImpl implements Perspective {
    private final PerspectiveEntry entry;

    public PerspectiveImpl(final PerspectiveEntry perspectiveEntry) {
        entry = perspectiveEntry;
    }

    public PerspectiveEntry getPerspectiveEntry() {
        return entry;
    }

    @Override
    public void addGenericRepository(final Class<?>... classes) {
        for (final Class<?> cls : classes) {
            final Object service = getPersistenceSession().getService("repository#" + cls.getName()).getObject();
            entry.addToServices(service);
        }
    }

    @Override
    public void addToObjects(final Object... objects) {
        for (final Object object : objects) {
            entry.addToObjects(object);
        }
    }

    @Override
    public Object addToServices(final Class<?> serviceType) {
        final Object service = findService(serviceType);
        entry.addToServices(service);
        return service;
    }

    @Override
    public void removeFromServices(final Class<?> serviceType) {
        final Object service = findService(serviceType);
        entry.removeFromServices(service);
    }

    private Object findService(final Class<?> serviceType) {
        for (final Object service : ExpressiveObjectsContext.getServices()) {
            if (service.getClass().isAssignableFrom(serviceType)) {
                return service;
            }
        }
        throw new ExpressiveObjectsException("No service of type " + serviceType.getName());
    }

    @Override
    public void addToServices(final Class<?>... classes) {
        for (final Class<?> cls : classes) {
            addToServices(cls);
        }
    }

    @Override
    public void removeFromServices(final Class<?>... classes) {
        for (final Class<?> cls : classes) {
            removeFromServices(cls);
        }
    }

    protected PersistenceSession getPersistenceSession() {
        return ExpressiveObjectsContext.getPersistenceSession();
    }

}
