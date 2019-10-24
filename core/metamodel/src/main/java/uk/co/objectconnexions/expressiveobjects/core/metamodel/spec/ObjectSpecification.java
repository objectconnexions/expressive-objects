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

package uk.co.objectconnexions.expressiveobjects.core.metamodel.spec;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.common.base.Function;

import uk.co.objectconnexions.expressiveobjects.applib.annotation.ObjectType;
import uk.co.objectconnexions.expressiveobjects.applib.filter.Filter;
import uk.co.objectconnexions.expressiveobjects.applib.profiles.Localization;
import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSession;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.Consent;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.InteractionInvocationMethod;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.InteractionResult;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.deployment.DeploymentCategory;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.collections.modify.CollectionFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.describedas.DescribedAsFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.help.HelpFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.hide.HiddenFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.named.NamedFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.aggregated.ParentedFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.encodeable.EncodableFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.icon.IconFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.immutable.ImmutableFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.objecttype.ObjectSpecIdFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.parseable.ParseableFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.plural.PluralFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.title.TitleFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.value.ValueFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.InteractionContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.ObjectTitleContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.ObjectValidityContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAction;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectActionContainer;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectActionContainer.Contributed;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociationContainer;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.classsubstitutor.ClassSubstitutor;

/**
 * Represents an entity or value (cf {@link java.lang.Class}) within the
 * metamodel.
 * 
 * <p>
 * As specifications are cyclic (specifically a class will reference its
 * subclasses, which in turn reference their superclass) they need be created
 * first, and then later work out its internals. Hence we create
 * {@link ObjectSpecification}s as we need them, and then introspect them later.
 * 
 * <p>
 * REVIEW: why is there no Help method for classes?
 */
public interface ObjectSpecification extends Specification, ObjectActionContainer, ObjectAssociationContainer, Hierarchical, Dirtiable, DefaultProvider {

    public final static List<ObjectSpecification> EMPTY_LIST = Collections.emptyList();

    public final static Function<ObjectSpecification, String> FUNCTION_FULLY_QUALIFIED_CLASS_NAME = new Function<ObjectSpecification, String>() {
        @Override
        public String apply(final ObjectSpecification from) {
            return from.getFullIdentifier();
        }
    };
    public final static Comparator<ObjectSpecification> COMPARATOR_FULLY_QUALIFIED_CLASS_NAME = new Comparator<ObjectSpecification>() {
        @Override
        public int compare(final ObjectSpecification o1, final ObjectSpecification o2) {
            return o1.getFullIdentifier().compareTo(o2.getFullIdentifier());
        }
    };
    public final static Comparator<ObjectSpecification> COMPARATOR_SHORT_IDENTIFIER_IGNORE_CASE = new Comparator<ObjectSpecification>() {
        @Override
        public int compare(final ObjectSpecification s1, final ObjectSpecification s2) {
            return s1.getShortIdentifier().compareToIgnoreCase(s2.getShortIdentifier());
        }
    };

    /**
     * @return
     */
    Class<?> getCorrespondingClass();

    /**
     * Returns the (unique) spec Id, as per the {@link ObjectSpecIdFacet}.
     * 
     * <p>
     * This will typically be the value of the {@link ObjectType} annotation (or equivalent);
     * if non has been specified then will default to the fully qualified class name (with
     * {@link ClassSubstitutor class name substituted} if necessary to allow for runtime bytecode enhancement.
     * 
     * <p>
     * The {@link ObjectSpecification} can be retrieved using {@link SpecificationLoader#lookupBySpecId(String)}.
     */
    ObjectSpecId getSpecId();
    
    /**
     * Returns an (immutable) "full" identifier for this specification.
     * 
     * <p>
     * This will be the fully qualified name of the Class object that this
     * object represents (i.e. it includes the package name).
     */
    String getFullIdentifier();
    
    /**
     * Returns an (immutable) "short" identifier for this specification.
     * 
     * <p>
     * This will be the class name without the package; any text up to and
     * including the last period is removed.
     */
    String getShortIdentifier();

    /**
     * Returns the (singular) name for objects of this specification.
     * 
     * <p>
     * Corresponds to the {@link NamedFacet#value()} of {@link NamedFacet}; is
     * not necessarily immutable.
     */
    String getSingularName();

    /**
     * Returns the plural name for objects of this specification.
     * 
     * <p>
     * Corresponds to the {@link PluralFacet#value() value} of
     * {@link PluralFacet}; is not necessarily immutable.
     */
    String getPluralName();

    /**
     * Returns the description, if any, of the specification.
     * 
     * <p>
     * Corresponds to the {@link DescribedAsFacet#value()) value} of
     * {@link DescribedAsFacet}; is not necessarily immutable.
     */
    @Override
    String getDescription();

    /**
     * Returns a help string or lookup reference, if any, of the specification.
     * 
     * <p>
     * Corresponds to the {@link HelpFacet#value()) value} of {@link HelpFacet};
     * is not necessarily immutable.
     */
    String getHelp();

    /**
     * Returns the title string for the specified object.
     * 
     * <p>
     * Corresponds to the {@link TitleFacet#value()) value} of
     * {@link TitleFacet}; is not necessarily immutable.
     */
    String getTitle(ObjectAdapter adapter, Localization localization);

    /**
     * Returns the name of an icon to use for the specified object.
     * 
     * <p>
     * Corresponds to the {@link IconFacet#iconName(ObjectAdapter)) icon name}
     * returned by the {@link IconFacet}; is not necessarily immutable.
     */
    String getIconName(ObjectAdapter object);

    boolean isAbstract();

    // //////////////////////////////////////////////////////////////
    // TitleContext
    // //////////////////////////////////////////////////////////////

    /**
     * Create an {@link InteractionContext} representing an attempt to read the
     * object's title.
     */
    ObjectTitleContext createTitleInteractionContext(AuthenticationSession session, InteractionInvocationMethod invocationMethod, ObjectAdapter targetObjectAdapter);

    // //////////////////////////////////////////////////////////////
    // ValidityContext, Validity
    // //////////////////////////////////////////////////////////////

    /**
     * Create an {@link InteractionContext} representing an attempt to save the
     * object.
     * @param deploymentCategory TODO
     */
    ObjectValidityContext createValidityInteractionContext(DeploymentCategory deploymentCategory, AuthenticationSession session, InteractionInvocationMethod invocationMethod, ObjectAdapter targetObjectAdapter);

    /**
     * Determines whether the specified object is in a valid state (for example,
     * so can be persisted); represented as a {@link Consent}.
     */
    Consent isValid(ObjectAdapter adapter);

    /**
     * Determines whether the specified object is in a valid state (for example,
     * so can be persisted); represented as a {@link InteractionResult}.
     */
    InteractionResult isValidResult(ObjectAdapter adapter);

    // //////////////////////////////////////////////////////////////
    // Facets
    // //////////////////////////////////////////////////////////////

    /**
     * Determines if objects of this specification can be persisted or not. If
     * it can be persisted (i.e. it return something other than
     * {@link Persistability}.TRANSIENT ObjectAdapter.isPersistent() will
     * indicated whether the object is persistent or not. If they cannot be
     * persisted then {@link ObjectAdapter}. {@link #persistability()} should be
     * ignored.
     */
    Persistability persistability();

    /**
     * Determines if the object represents an value or object.
     * 
     * <p>
     * In effect, means that it doesn't have the {@link CollectionFacet}, and
     * therefore will return NOT {@link #isParentedOrFreeCollection()}
     * 
     * @see #isCollection().
     */
    boolean isNotCollection();

    /**
     * Determines if objects of this type are a parented (internal) or free-standing (external) collection.
     * 
     * <p>
     * In effect, means has got {@link CollectionFacet}, and therefore will
     * return NOT {@link #isNotCollection()}.
     * 
     * @see #isNotCollection()
     */
    boolean isParentedOrFreeCollection();

    /**
     * Determines if objects of this type are values.
     * 
     * <p>
     * In effect, means has got {@link ValueFacet}.
     */
    boolean isValue();

    /**
     * Determines if objects of this type are parented (a parented collection, or an aggregated entity).
     * 
     * <p>
     * In effect, means has got {@link ParentedFacet}.
     */
    boolean isParented();

    /**
     * Determines if objects of this type are either values or aggregated.
     * 
     * @see #isValue()
     * @see #isParented()
     */
    boolean isValueOrIsParented();

    /**
     * Determines if objects of this type can be set up from a text entry
     * string.
     * 
     * <p>
     * In effect, means has got a {@link ParseableFacet}.
     */
    boolean isParseable();

    /**
     * Determines if objects of this type can be converted to a data-stream.
     * 
     * <p>
     * In effect, means has got {@link EncodableFacet}.
     */
    boolean isEncodeable();

    /**
     * Whether has the {@link ImmutableFacet}.
     */
    boolean isImmutable();

    /**
     * Whether has the {@link HiddenFacet}
     */
    boolean isHidden();

    // //////////////////////////////////////////////////////////////
    // Creation
    // //////////////////////////////////////////////////////////////

    Object createObject();

    /**
     * REVIEW: should this behaviour move, eg onto ObjectAdapter?
     */
    ObjectAdapter initialize(ObjectAdapter object);



    // //////////////////////////////////////////////////////////////
    // Service
    // //////////////////////////////////////////////////////////////

    boolean isService();

    public void markAsService();




}
