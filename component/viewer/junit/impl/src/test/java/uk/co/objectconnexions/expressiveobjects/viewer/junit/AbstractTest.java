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

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

import uk.co.objectconnexions.expressiveobjects.applib.DomainObjectContainer;
import uk.co.objectconnexions.expressiveobjects.progmodel.wrapper.applib.WrapperFactory;
import uk.co.objectconnexions.expressiveobjects.viewer.junit.sample.domain.Country;
import uk.co.objectconnexions.expressiveobjects.viewer.junit.sample.domain.Customer;
import uk.co.objectconnexions.expressiveobjects.viewer.junit.sample.domain.Product;
import uk.co.objectconnexions.expressiveobjects.viewer.junit.sample.fixtures.CountriesFixture;
import uk.co.objectconnexions.expressiveobjects.viewer.junit.sample.fixtures.CustomerOrdersFixture;
import uk.co.objectconnexions.expressiveobjects.viewer.junit.sample.fixtures.CustomersFixture;
import uk.co.objectconnexions.expressiveobjects.viewer.junit.sample.fixtures.ProductsFixture;
import uk.co.objectconnexions.expressiveobjects.viewer.junit.sample.service.CountryRepository;
import uk.co.objectconnexions.expressiveobjects.viewer.junit.sample.service.CustomerRepository;
import uk.co.objectconnexions.expressiveobjects.viewer.junit.sample.service.OrderRepository;
import uk.co.objectconnexions.expressiveobjects.viewer.junit.sample.service.ProductRepository;

@RunWith(ExpressiveObjectsTestRunner.class)
@Fixtures({ @Fixture(CountriesFixture.class), @Fixture(ProductsFixture.class), @Fixture(CustomersFixture.class), @Fixture(CustomerOrdersFixture.class) })
@Services({ @Service(CountryRepository.class), @Service(ProductRepository.class), @Service(CustomerRepository.class), @Service(OrderRepository.class) })
public abstract class AbstractTest {

    protected Customer custJsDO;
    protected Customer custJsWO;

    protected Product product355DO;
    protected Product product355VO;

    protected Product product850DO;

    protected Country countryGbrDO;
    protected Country countryGbrVO;

    protected Country countryUsaDO;
    protected Country countryAusDO;

    private ProductRepository productRepository;
    private CustomerRepository customerRepository;
    private CountryRepository countryRepository;

    private DomainObjectContainer domainObjectContainer;
    private WrapperFactory wrapperFactory;

    @Before
    public void setUp() {

        product355DO = productRepository.findByCode("355-40311");
        product355VO = wrapperFactory.wrap(product355DO);
        product850DO = productRepository.findByCode("850-18003");

        countryGbrDO = countryRepository.findByCode("GBR");
        countryGbrVO = wrapperFactory.wrap(countryGbrDO);

        countryUsaDO = countryRepository.findByCode("USA");
        countryAusDO = countryRepository.findByCode("AUS");

        custJsDO = customerRepository.findByName("Pawson");
        custJsWO = wrapperFactory.wrap(custJsDO);
    }

    @After
    public void tearDown() {
    }

    // //////////////////////////////////////////////////////
    // Injected.
    // //////////////////////////////////////////////////////

    protected WrapperFactory getWrapperFactory() {
        return wrapperFactory;
    }

    public void setWrapperFactory(final WrapperFactory headlessViewer) {
        this.wrapperFactory = headlessViewer;
    }

    protected DomainObjectContainer getDomainObjectContainer() {
        return domainObjectContainer;
    }

    public void setDomainObjectContainer(final DomainObjectContainer domainObjectContainer) {
        this.domainObjectContainer = domainObjectContainer;
    }

    protected ProductRepository getProductRepository() {
        return productRepository;
    }

    public void setProductRepository(final ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    protected CustomerRepository getCustomerRepository() {
        return customerRepository;
    }

    public void setCustomerRepository(final CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    protected CountryRepository getCountryRepository() {
        return countryRepository;
    }

    public void setCountryRepository(final CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

}
