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

package uk.co.objectconnexions.expressiveobjects.viewer.junit.sample.service;

import java.util.List;

import org.apache.log4j.Logger;

import uk.co.objectconnexions.expressiveobjects.applib.AbstractFactoryAndRepository;
import uk.co.objectconnexions.expressiveobjects.applib.annotation.Hidden;
import uk.co.objectconnexions.expressiveobjects.applib.annotation.Named;
import uk.co.objectconnexions.expressiveobjects.applib.filter.Filter;
import uk.co.objectconnexions.expressiveobjects.viewer.junit.sample.domain.Country;

@Named("Countries")
public class CountryRepository extends AbstractFactoryAndRepository {

    // {{ Logger
    @SuppressWarnings("unused")
    private final static Logger LOGGER = Logger.getLogger(CountryRepository.class);

    // }}

    /**
     * Lists all countries in the repository.
     */
    public List<Country> showAll() {
        return allInstances(Country.class);
    }

    // {{ findByCode
    /**
     * Returns the Country with given code
     */
    public Country findByCode(@Named("Code") final String code) {
        return firstMatch(Country.class, new Filter<Country>() {
            @Override
            public boolean accept(final Country country) {
                return code.equals(country.getCode());
            }
        });
    }

    // }}

    /**
     * Creates a new countryGBR.
     * 
     * <p>
     * For use by fixtures only.
     * 
     * @return
     */
    @Hidden
    public Country newCountry(final String code, final String name) {
        final Country country = newTransientInstance(Country.class);
        country.setCode(code);
        country.setName(name);
        persist(country);
        return country;
    }

}
