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

package uk.co.objectconnexions.expressiveobjects.viewer.junit.sample.fixtures;

import org.apache.log4j.Logger;

import uk.co.objectconnexions.expressiveobjects.applib.fixtures.AbstractFixture;
import uk.co.objectconnexions.expressiveobjects.viewer.junit.sample.domain.Country;
import uk.co.objectconnexions.expressiveobjects.viewer.junit.sample.service.CountryRepository;
import uk.co.objectconnexions.expressiveobjects.viewer.junit.sample.service.CustomerRepository;

public class CustomersFixture extends AbstractFixture {

    // {{ Logger
    private final static Logger LOGGER = Logger.getLogger(CustomersFixture.class);

    public Logger getLOGGER() {
        return LOGGER;
    }

    // }}

    @Override
    public void install() {
        getLOGGER().debug("installing");
        final Country countryGBR = getCountryRepository().findByCode("GBR");
        getCustomerRepository().newCustomer("Richard", "Pawson", 1, countryGBR);
        getCustomerRepository().newCustomer("Robert", "Matthews", 2, countryGBR);
        getCustomerRepository().newCustomer("Dan", "Haywood", 3, countryGBR);
        getCustomerRepository().newCustomer("Stef", "Cascarini", 4, countryGBR);
        getCustomerRepository().newCustomer("Dave", "Slaughter", 5, countryGBR);
    }

    // {{ Injected: CustomerRepository
    private CustomerRepository customerRepository;

    /**
     * This field is not persisted, nor displayed to the user.
     */
    protected CustomerRepository getCustomerRepository() {
        return this.customerRepository;
    }

    /**
     * Injected by the application container.
     */
    public void setCustomerRepository(final CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    // }}

    // {{ Injected: CountryRepository
    private CountryRepository countryRepository;

    /**
     * This field is not persisted, nor displayed to the user.
     */
    protected CountryRepository getCountryRepository() {
        return this.countryRepository;
    }

    /**
     * Injected by the application container.
     */
    public void setCountryRepository(final CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }
    // }}

}
