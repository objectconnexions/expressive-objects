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
import uk.co.objectconnexions.expressiveobjects.viewer.junit.sample.domain.Customer;
import uk.co.objectconnexions.expressiveobjects.viewer.junit.sample.domain.Product;

@Named("Products")
public class ProductRepository extends AbstractFactoryAndRepository {

    // use ctrl+space to bring up the NO templates.

    // also, use CoffeeBytes code folding with
    // user-defined regions of {{ and }}

    // {{ Logger
    @SuppressWarnings("unused")
    private final static Logger LOGGER = Logger.getLogger(ProductRepository.class);

    // }}

    /**
     * Lists all products in the repository.
     */
    public List<Product> showAll() {
        return allInstances(Product.class);
    }

    // {{ findByCode
    /**
     * Returns the Product with given code
     */
    public Product findByCode(@Named("Code") final String code) {
        return firstMatch(Product.class, new Filter<Product>() {
            @Override
            public boolean accept(final Product product) {
                return code.equals(product.getCode());
            }
        });
    }

    // }}

    /**
     * Creates a new product.
     * 
     * <p>
     * For use by fixtures only.
     * 
     * @return
     */
    @Hidden
    public Product newProduct(final String code, final String description, final int priceInPence) {
        final Product product = newTransientInstance(Product.class);
        product.setCode(code);
        product.setDescription(description);
        product.setPrice(new Double(priceInPence / 100));
        persist(product);
        return product;
    }

    /**
     * Creates a new still transient product.
     * 
     * <p>
     * For use by tests only. Using this rather than {@link Customer} since
     * {@link Product} has a {@link Product#validate()} method.
     * 
     * @return
     */
    @Hidden
    public Product newProduct() {
        return newTransientInstance(Product.class);
    }

    

}
