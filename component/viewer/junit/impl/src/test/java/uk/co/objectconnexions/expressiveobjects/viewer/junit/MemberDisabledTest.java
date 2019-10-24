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

package uk.co.objectconnexions.expressiveobjects.viewer.junit;

import static uk.co.objectconnexions.expressiveobjects.core.commons.matchers.ExpressiveObjectsMatchers.classEqualTo;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.members.disabled.annotation.DisabledFacetAnnotation;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.members.disabled.method.DisableForContextFacetViaMethod;
import uk.co.objectconnexions.expressiveobjects.progmodel.wrapper.applib.DisabledException;
import uk.co.objectconnexions.expressiveobjects.viewer.junit.sample.domain.Order;

public class MemberDisabledTest extends AbstractTest {

    @Test
    public void whenValueDisabledForValueThenThrowsException() {
        custJsDO.disableFirstName = "cannot alter";
        try {
            custJsWO.setFirstName("Dick");
            fail("Should have thrown exception");
        } catch (final DisabledException ex) {
            assertThat(ex.getAdvisorClass(), classEqualTo(DisableForContextFacetViaMethod.class));
            assertThat(ex.getIdentifier().getMemberNaturalName(), equalTo("First Name"));
            assertThat(ex.getMessage(), equalTo("cannot alter"));
        }
    }

    @Test
    public void whenValueDisabledForNullThenThrowsException() {
        custJsDO.disableFirstName = "cannot alter";
        try {
            custJsWO.setFirstName(null);
            fail("Should have thrown exception");
        } catch (final DisabledException ex) {
            assertThat(ex.getAdvisorClass(), classEqualTo(DisableForContextFacetViaMethod.class));
            assertThat(ex.getIdentifier().getMemberNaturalName(), equalTo("First Name"));
            assertThat(ex.getMessage(), equalTo("cannot alter"));
        }
    }

    @Test
    public void whenAssociationDisabledForReferenceThenThrowsException() {
        custJsDO.disableCountryOfBirth = "cannot alter";
        try {
            custJsWO.setCountryOfBirth(countryUsaDO);
            fail("Should have thrown exception");
        } catch (final DisabledException ex) {
            assertThat(ex.getAdvisorClass(), classEqualTo(DisableForContextFacetViaMethod.class));
            assertThat(ex.getIdentifier().getMemberNaturalName(), equalTo("Country Of Birth"));
            assertThat(ex.getMessage(), equalTo("cannot alter"));
        }
    }

    @Test
    public void whenAssociationDisabledForNullThenThrowsException() {
        custJsDO.disableCountryOfBirth = "cannot alter";
        try {
            custJsWO.setCountryOfBirth(null);
            fail("Should have thrown exception");
        } catch (final DisabledException ex) {
            assertThat(ex.getAdvisorClass(), classEqualTo(DisableForContextFacetViaMethod.class));
            assertThat(ex.getIdentifier().getMemberNaturalName(), equalTo("Country Of Birth"));
            assertThat(ex.getMessage(), equalTo("cannot alter"));
        }
    }

    @Test
    public void whenCollectionDisabledThenAddToThrowsException() {
        final List<Order> orders = custJsWO.getOrders();
        final Order order = orders.get(0);
        try {
            custJsWO.addToMoreOrders(order);
            fail("Should have thrown exception");
        } catch (final DisabledException ex) {
            assertThat(ex.getAdvisorClass(), classEqualTo(DisabledFacetAnnotation.class));
            assertThat(ex.getIdentifier().getMemberNaturalName(), equalTo("More Orders"));
            assertThat(ex.getMessage(), equalTo("Always disabled"));
        }
    }

    @Test
    public void whenCollectionDisabledThenRemovefromThrowsException() {
        custJsDO.addToVisitedCountries(countryUsaDO);
        custJsDO.disableVisitedCountries = "cannot alter";
        try {
            custJsWO.removeFromVisitedCountries(countryUsaDO);
            fail("Should have thrown exception");
        } catch (final DisabledException ex) {
            assertThat(ex.getAdvisorClass(), classEqualTo(DisableForContextFacetViaMethod.class));
            assertThat(ex.getIdentifier().getMemberNaturalName(), equalTo("Visited Countries"));
            assertThat(ex.getMessage(), equalTo("cannot alter"));
        }
    }

    @Test
    public void whenActionDisabledThenThrowsException() {
        custJsDO.disablePlaceOrder = "cannot invoke";
        try {
            custJsWO.placeOrder(product355DO, 3);
            fail("Should have thrown exception");
        } catch (final DisabledException ex) {
            assertThat(ex.getAdvisorClass(), classEqualTo(DisableForContextFacetViaMethod.class));
            assertThat(ex.getIdentifier().getMemberNaturalName(), equalTo("Place Order"));
            assertThat(ex.getMessage(), equalTo("cannot invoke"));
        }
    }

}
