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

package uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FeatureType;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.Specification;

/**
 * A specification representing a non-{@link FeatureType#OBJECT object}, that
 * therefore has an underlying type (the type of the property, collection)
 * 
 * <p>
 * For a property or action parameter, is the type. For a collection is the
 * element type. For an action it is always <tt>null</tt>.
 */
public interface ObjectFeature extends Specification {

    /**
     * Returns the identifier of the member, which must not change. This should
     * be all camel-case with no spaces: so if the member is called 'Return
     * Date' then a suitable id would be 'returnDate'.
     */
    String getId();

    /**
     * Return the name for this member - the field or action. This is based on
     * the name of this member.
     * 
     * @see #getIdentifier()
     */
    String getName();

    /**
     * Returns a description of how the member is used - this complements the
     * help text.
     * 
     * @see #getHelp()
     */
    @Override
    String getDescription();

    /**
     * The specification of the underlying type.
     * 
     * <p>
     * For example:
     * <ul>
     * <li>for a {@link OneToOneAssociation property}, will return the
     * {@link ObjectSpecification} of the type that the accessor returns.
     * <li>for a {@link OneToManyAssociation collection} it will be the type of
     * element the collection holds (not the type of collection).
     * <li>for a {@link ObjectAction action}, will always return <tt>null</tt>.
     * See instead {@link ObjectAction#getReturnType()} and
     * {@link ObjectAction#getParameterTypes()}.
     * <li>for a {@link ObjectActionParameter action}, will return the type of
     * the parameter}.
     * </ul>
     */
    ObjectSpecification getSpecification();

}
