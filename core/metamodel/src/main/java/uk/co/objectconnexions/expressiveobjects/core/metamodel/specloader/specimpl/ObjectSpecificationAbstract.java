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

package uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.specimpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.apache.log4j.Logger;

import uk.co.objectconnexions.expressiveobjects.applib.Identifier;
import uk.co.objectconnexions.expressiveobjects.applib.annotation.NotPersistable;
import uk.co.objectconnexions.expressiveobjects.applib.filter.Filter;
import uk.co.objectconnexions.expressiveobjects.applib.filter.Filters;
import uk.co.objectconnexions.expressiveobjects.applib.profiles.Localization;
import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSession;
import uk.co.objectconnexions.expressiveobjects.core.commons.authentication.AuthenticationSessionProvider;
import uk.co.objectconnexions.expressiveobjects.core.commons.lang.JavaClassUtils;
import uk.co.objectconnexions.expressiveobjects.core.commons.lang.ToString;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ObjectAdapter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.adapter.ServicesProvider;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.Consent;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.InteractionInvocationMethod;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.consent.InteractionResult;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.deployment.DeploymentCategory;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.Facet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FacetHolderImpl;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facetapi.FeatureType;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.collections.modify.CollectionFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.describedas.DescribedAsFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.help.HelpFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.hide.HiddenFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.named.NamedFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.aggregated.ParentedFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.dirty.ClearDirtyObjectFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.dirty.IsDirtyObjectFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.dirty.MarkDirtyObjectFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.encodeable.EncodableFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.icon.IconFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.immutable.ImmutableFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.notpersistable.NotPersistableFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.objecttype.ObjectSpecIdFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.parseable.ParseableFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.plural.PluralFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.title.TitleFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.object.value.ValueFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.facets.typeof.TypeOfFacet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.InteractionContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.InteractionUtils;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.ObjectTitleContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.interactions.ObjectValidityContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ActionType;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.Instance;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectActionSet;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectInstantiator;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecId;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecification;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.ObjectSpecificationException;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.Persistability;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.SpecificationContext;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.SpecificationLoader;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAction;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectActionFilters;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectActionParameter;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectActions;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociation;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.ObjectAssociationFilters;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.OneToManyAssociation;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.spec.feature.OneToOneAssociation;
import uk.co.objectconnexions.expressiveobjects.core.metamodel.specloader.specimpl.objectlist.ObjectSpecificationForFreeStandingList;

public abstract class ObjectSpecificationAbstract extends FacetHolderImpl implements ObjectSpecification {

    private final static Logger LOG = Logger.getLogger(ObjectSpecificationAbstract.class);

    private static class SubclassList {
        private final List<ObjectSpecification> classes = Lists.newArrayList();

        public void addSubclass(final ObjectSpecification subclass) {
            if(classes.contains(subclass)) { 
                return;
            }
            classes.add(subclass);
        }

        public boolean hasSubclasses() {
            return !classes.isEmpty();
        }

        public List<ObjectSpecification> toList() {
            return Collections.unmodifiableList(classes);
        }
    }

    private final DeploymentCategory deploymentCategory;
    private final AuthenticationSessionProvider authenticationSessionProvider;
    private final ServicesProvider servicesProvider;
    private final ObjectInstantiator objectInstantiator;
    private final SpecificationLoader specificationLookup;

    private final List<ObjectAction> objectActions = Lists.newArrayList();
    private final List<ObjectAssociation> associations = Lists.newArrayList();
    private final List<ObjectSpecification> interfaces = Lists.newArrayList();
    private final SubclassList subclasses = new SubclassList();

    /**
     * Lazily populated.
     */
    private final Map<ActionType, List<ObjectAction>> contributedActionSetsByType = Maps.newLinkedHashMap();

    private final Class<?> correspondingClass;
    private final String fullName;
    private final String shortName;
    private final Identifier identifier;
    private final boolean isAbstract;
    // derived lazily, cached since immutable
    private ObjectSpecId specId;

    private ObjectSpecification superclassSpec;

    private Persistability persistability = Persistability.USER_PERSISTABLE;

    private MarkDirtyObjectFacet markDirtyObjectFacet;
    private ClearDirtyObjectFacet clearDirtyObjectFacet;
    private IsDirtyObjectFacet isDirtyObjectFacet;

    private TitleFacet titleFacet;
    private IconFacet iconFacet;

    private IntrospectionState introspected = IntrospectionState.NOT_INTROSPECTED;

    // //////////////////////////////////////////////////////////////////////
    // Constructor
    // //////////////////////////////////////////////////////////////////////

    public ObjectSpecificationAbstract(final Class<?> introspectedClass, final String shortName, final SpecificationContext specificationContext) {

        this.correspondingClass = introspectedClass;
        this.fullName = introspectedClass.getName();
        this.shortName = shortName;
        this.isAbstract = JavaClassUtils.isAbstract(introspectedClass);
        this.identifier = Identifier.classIdentifier(introspectedClass);

        this.deploymentCategory = specificationContext.getDeploymentCategory();
        this.authenticationSessionProvider = specificationContext.getAuthenticationSessionProvider();
        this.servicesProvider = specificationContext.getServicesProvider();
        this.objectInstantiator = specificationContext.getObjectInstantiator();
        this.specificationLookup = specificationContext.getSpecificationLookup();
    }

    
    protected DeploymentCategory getDeploymentCategory() {
        return deploymentCategory;
    }

    // //////////////////////////////////////////////////////////////////////
    // Stuff immediately derivable from class
    // //////////////////////////////////////////////////////////////////////

    @Override
    public FeatureType getFeatureType() {
        return FeatureType.OBJECT;
    }

    @Override
    public ObjectSpecId getSpecId() {
        if(specId == null) {
            final ObjectSpecIdFacet facet = getFacet(ObjectSpecIdFacet.class);
            if(facet == null) {
                throw new IllegalStateException("could not find an ObjectSpecIdFacet for " + this.getFullIdentifier());
            }
            if(facet != null) {
                specId = facet.value();
            }
        }
        return specId;
    }
    
    /**
     * As provided explicitly within the
     * {@link #IntrospectableSpecificationAbstract(Class, String, SpecificationContext)
     * constructor}.
     * 
     * <p>
     * Not API, but <tt>public</tt> so that {@link FacetedMethodsBuilder} can
     * call it.
     */
    @Override
    public Class<?> getCorrespondingClass() {
        return correspondingClass;
    }

    /**
     * As provided explicitly within the
     * {@link #IntrospectableSpecificationAbstract(Class, String, SpecificationContext)
     * constructor}.
     */
    @Override
    public String getShortIdentifier() {
        return shortName;
    }

    /**
     * The {@link Class#getName() (full) name} of the
     * {@link #getCorrespondingClass() class}.
     */
    @Override
    public String getFullIdentifier() {
        return fullName;
    }

    
    public enum IntrospectionState {
        NOT_INTROSPECTED,
        BEING_INTROSPECTED,
        INTROSPECTED,
    }

    /**
     * Only if {@link #setIntrospected(boolean)} has been called (should be
     * called within {@link #updateFromFacetValues()}.
     */
    public IntrospectionState getIntrospectionState() {
        return introspected;
    }

    public void setIntrospectionState(IntrospectionState introspectationState) {
        this.introspected = introspectationState;
    }

    // //////////////////////////////////////////////////////////////////////
    // Introspection (part 1)
    // //////////////////////////////////////////////////////////////////////

    public abstract void introspectTypeHierarchyAndMembers();

    /**
     * Intended to be called within {@link #introspectTypeHierarchyAndMembers()}
     * .
     */
    protected void updateSuperclass(final Class<?> superclass) {
        if (superclass == null) {
            return;
        }
        superclassSpec = getSpecificationLookup().loadSpecification(superclass);
        if (superclassSpec != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("  Superclass " + superclass.getName());
            }
            updateAsSubclassTo(superclassSpec);
        }
    }

    /**
     * Intended to be called within {@link #introspectTypeHierarchyAndMembers()}
     * .
     */
    protected void updateInterfaces(final List<ObjectSpecification> interfaces) {
        this.interfaces.clear();
        this.interfaces.addAll(interfaces);
    }

    /**
     * Intended to be called within {@link #introspectTypeHierarchyAndMembers()}
     * .
     */
    protected void updateAsSubclassTo(final ObjectSpecification supertypeSpec) {
        if (!(supertypeSpec instanceof ObjectSpecificationAbstract)) {
            return;
        }
        // downcast required because addSubclass is (deliberately) not public
        // API
        final ObjectSpecificationAbstract introspectableSpec = (ObjectSpecificationAbstract) supertypeSpec;
        introspectableSpec.updateSubclasses(this);
    }

    /**
     * Intended to be called within {@link #introspectTypeHierarchyAndMembers()}
     * .
     */
    protected void updateAsSubclassTo(final List<ObjectSpecification> supertypeSpecs) {
        for (final ObjectSpecification supertypeSpec : supertypeSpecs) {
            updateAsSubclassTo(supertypeSpec);
        }
    }

    private void updateSubclasses(final ObjectSpecification subclass) {
        this.subclasses.addSubclass(subclass);
    }

    /**
     * Intended to be called within {@link #introspectTypeHierarchyAndMembers()}
     * .
     */
    protected void updateAssociations(final List<ObjectAssociation> associations) {
        if (associations == null) {
            return;
        }
        this.associations.clear();
        this.associations.addAll(associations);
    }

    /**
     * Intended to be called within {@link #introspectTypeHierarchyAndMembers()}
     * .
     */
    protected void updateObjectActions(final List<ObjectAction> objectActions) {
        if (objectActions == null) {
            return;
        }
        this.objectActions.clear();
        this.objectActions.addAll(objectActions);
    }

    // //////////////////////////////////////////////////////////////////////
    // Introspection (part 2)
    // //////////////////////////////////////////////////////////////////////

    public void updateFromFacetValues() {
        clearDirtyObjectFacet = getFacet(ClearDirtyObjectFacet.class);
        markDirtyObjectFacet = getFacet(MarkDirtyObjectFacet.class);
        isDirtyObjectFacet = getFacet(IsDirtyObjectFacet.class);

        titleFacet = getFacet(TitleFacet.class);
        iconFacet = getFacet(IconFacet.class);

        this.persistability = determinePersistability();
    }

    private Persistability determinePersistability() {
        final NotPersistableFacet notPersistableFacet = getFacet(NotPersistableFacet.class);
        if (notPersistableFacet == null) {
            return Persistability.USER_PERSISTABLE;
        }
        final NotPersistable.By initiatedBy = notPersistableFacet.value();
        if (initiatedBy == NotPersistable.By.USER_OR_PROGRAM) {
            return Persistability.TRANSIENT;
        } else if (initiatedBy == NotPersistable.By.USER) {
            return Persistability.PROGRAM_PERSISTABLE;
        } else {
            return Persistability.USER_PERSISTABLE;
        }
    }

    /**
     * Intended to be called (if at all) within {@link #updateFromFacetValues()}
     * .
     */
    protected void setClearDirtyObjectFacet(final ClearDirtyObjectFacet clearDirtyObjectFacet) {
        this.clearDirtyObjectFacet = clearDirtyObjectFacet;
    }


    // //////////////////////////////////////////////////////////////////////
    // Title, Icon
    // //////////////////////////////////////////////////////////////////////

    @Override
    public String getTitle(final ObjectAdapter adapter, final Localization localization) {
        if (titleFacet != null) {
            final String titleString = titleFacet.title(adapter, localization);
            if (titleString != null && !titleString.equals("")) {
                return titleString;
            }
        }
        return (this.isService() ? "" : "Untitled ") + getSingularName();
    }

    @Override
    public String getIconName(final ObjectAdapter reference) {
        return iconFacet == null ? null : iconFacet.iconName(reference);
    }

    // //////////////////////////////////////////////////////////////////////
    // Specification
    // //////////////////////////////////////////////////////////////////////

    @Override
    public Instance getInstance(final ObjectAdapter adapter) {
        return adapter;
    }

    // //////////////////////////////////////////////////////////////////////
    // Hierarchical
    // //////////////////////////////////////////////////////////////////////

    /**
     * Determines if this class represents the same class, or a subclass, of the
     * specified class.
     * 
     * <p>
     * cf {@link Class#isAssignableFrom(Class)}, though target and parameter are
     * the opposite way around, ie:
     * 
     * <pre>
     * cls1.isAssignableFrom(cls2);
     * </pre>
     * <p>
     * is equivalent to:
     * 
     * <pre>
     * spec2.isOfType(spec1);
     * </pre>
     * 
     * <p>
     * Callable after {@link #introspectTypeHierarchyAndMembers()} has been
     * called.
     */
    @Override
    public boolean isOfType(final ObjectSpecification specification) {
        if (specification == this) {
            return true;
        }
        for (final ObjectSpecification interfaceSpec : interfaces()) {
            if (interfaceSpec.isOfType(specification)) {
                return true;
            }
        }
        final ObjectSpecification superclassSpec = superclass();
        return superclassSpec != null ? superclassSpec.isOfType(specification) : false;
    }

    // //////////////////////////////////////////////////////////////////////
    // Name, Description, Persistability
    // //////////////////////////////////////////////////////////////////////

    /**
     * Expect to be populated using {@link #setSingularName(String)}, but has
     * default name as well.
     */
    @Override
    public String getSingularName() {
        final NamedFacet namedFacet = getFacet(NamedFacet.class);
        return namedFacet != null? namedFacet.value() : this.getFullIdentifier();
    }

    /**
     * Expect to be populated using {@link #setPluralName(String)} but has
     * default name as well.
     */
    @Override
    public String getPluralName() {
        final PluralFacet pluralFacet = getFacet(PluralFacet.class);
        return pluralFacet.value();
    }

    /**
     * Expect to be populated using {@link #setDescribedAs(String)} but has
     * default name as well.
     */
    @Override
    public String getDescription() {
        final DescribedAsFacet describedAsFacet = getFacet(DescribedAsFacet.class);
        final String describedAs = describedAsFacet.value();
        return describedAs == null ? "" : describedAs;
    }

    /*
     * help is typically a reference (eg a URL) and so should not default to a
     * textual value if not set up
     */
    @Override
    public String getHelp() {
        final HelpFacet helpFacet = getFacet(HelpFacet.class);
        return helpFacet == null ? null : helpFacet.value();
    }

    @Override
    public Persistability persistability() {
        return persistability;
    }

    // //////////////////////////////////////////////////////////////////////
    // Dirty object support
    // //////////////////////////////////////////////////////////////////////

    @Override
    public boolean isDirty(final ObjectAdapter object) {
        return isDirtyObjectFacet == null ? false : isDirtyObjectFacet.invoke(object);
    }

    @Override
    public void clearDirty(final ObjectAdapter object) {
        if (clearDirtyObjectFacet != null) {
            clearDirtyObjectFacet.invoke(object);
        }
    }

    @Override
    public void markDirty(final ObjectAdapter object) {
        if (markDirtyObjectFacet != null) {
            markDirtyObjectFacet.invoke(object);
        }
    }

    // //////////////////////////////////////////////////////////////////////
    // Facet Handling
    // //////////////////////////////////////////////////////////////////////

    @Override
    public <Q extends Facet> Q getFacet(final Class<Q> facetType) {
        final Q facet = super.getFacet(facetType);
        Q noopFacet = null;
        if (isNotANoopFacet(facet)) {
            return facet;
        } else {
            noopFacet = facet;
        }
        if (interfaces() != null) {
            final List<ObjectSpecification> interfaces = interfaces();
            for (int i = 0; i < interfaces.size(); i++) {
                final ObjectSpecification interfaceSpec = interfaces.get(i);
                if (interfaceSpec == null) {
                    // HACK: shouldn't happen, but occurring on occasion when
                    // running
                    // XATs under JUnit4. Some sort of race condition?
                    continue;
                }
                final Q interfaceFacet = interfaceSpec.getFacet(facetType);
                if (isNotANoopFacet(interfaceFacet)) {
                    return interfaceFacet;
                } else {
                    if (noopFacet == null) {
                        noopFacet = interfaceFacet;
                    }
                }
            }
        }
        // search up the inheritance hierarchy
        final ObjectSpecification superSpec = superclass();
        if (superSpec != null) {
            final Q superClassFacet = superSpec.getFacet(facetType);
            if (isNotANoopFacet(superClassFacet)) {
                return superClassFacet;
            }
        }
        return noopFacet;
    }

    private boolean isNotANoopFacet(final Facet facet) {
        return facet != null && !facet.isNoop();
    }

    // //////////////////////////////////////////////////////////////////////
    // DefaultValue
    // //////////////////////////////////////////////////////////////////////

    @Override
    public Object getDefaultValue() {
        return null;
    }

    // //////////////////////////////////////////////////////////////////////
    // Identifier
    // //////////////////////////////////////////////////////////////////////

    @Override
    public Identifier getIdentifier() {
        return identifier;
    }

    // //////////////////////////////////////////////////////////////////
    // create InteractionContext
    // //////////////////////////////////////////////////////////////////

    @Override
    public ObjectTitleContext createTitleInteractionContext(final AuthenticationSession session, final InteractionInvocationMethod interactionMethod, final ObjectAdapter targetObjectAdapter) {
        return new ObjectTitleContext(getDeploymentCategory(), session, interactionMethod, targetObjectAdapter, getIdentifier(), targetObjectAdapter.titleString());
    }

    // //////////////////////////////////////////////////////////////////////
    // Superclass, Interfaces, Subclasses, isAbstract
    // //////////////////////////////////////////////////////////////////////

    @Override
    public ObjectSpecification superclass() {
        return superclassSpec;
    }

    @Override
    public List<ObjectSpecification> interfaces() {
        return Collections.unmodifiableList(interfaces);
    }

    @Override
    public List<ObjectSpecification> subclasses() {
        return subclasses.toList();
    }

    @Override
    public boolean hasSubclasses() {
        return subclasses.hasSubclasses();
    }

    @Override
    public final boolean isAbstract() {
        return isAbstract;
    }

    // //////////////////////////////////////////////////////////////////////
    // Associations
    // //////////////////////////////////////////////////////////////////////

    @Override
    public List<ObjectAssociation> getAssociations() {
        return Collections.unmodifiableList(associations);
    }

    /**
     * The association with the given {@link ObjectAssociation#getId() id}.
     * 
     * <p>
     * This is overridable because {@link ObjectSpecificationForFreeStandingList}
     * simply returns <tt>null</tt>.
     * 
     * <p>
     * TODO put fields into hash.
     * 
     * <p>
     * TODO: could this be made final? (ie does the framework ever call this
     * method for an {@link ObjectSpecificationForFreeStandingList})
     */
    @Override
    public ObjectAssociation getAssociation(final String id) {
        for (final ObjectAssociation objectAssociation : getAssociations()) {
            if (objectAssociation.getId().equals(id)) {
                return objectAssociation;
            }
        }
        throw new ObjectSpecificationException("No association called '" + id + "' in '" + getSingularName() + "'");
    }

    @Override
    public List<ObjectAssociation> getAssociations(final Filter<ObjectAssociation> filter) {
        final List<ObjectAssociation> allFields = getAssociations();

        final List<ObjectAssociation> selectedFields = Lists.newArrayList();
        for (int i = 0; i < allFields.size(); i++) {
            if (filter.accept(allFields.get(i))) {
                selectedFields.add(allFields.get(i));
            }
        }

        return selectedFields;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<OneToOneAssociation> getProperties() {
        final List<OneToOneAssociation> list = new ArrayList<OneToOneAssociation>();
        @SuppressWarnings("rawtypes")
        final List associationList = getAssociations(ObjectAssociationFilters.PROPERTIES);
        list.addAll(associationList);
        return list;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<OneToManyAssociation> getCollections() {
        final List<OneToManyAssociation> list = Lists.newArrayList();
        @SuppressWarnings("rawtypes")
        final List associationList = getAssociations(ObjectAssociationFilters.COLLECTIONS);
        list.addAll(associationList);
        return list;
    }

    // //////////////////////////////////////////////////////////////////////
    // getObjectAction, getAction, getActions
    // //////////////////////////////////////////////////////////////////////

    @Override
    public List<ObjectAction> getObjectActions(final Contributed contributed) {
        if (contributed.isExcluded()) {
            // REVIEW: this special case almost certainly isn't required
            return Collections.unmodifiableList(objectActions);
        } else {
            return getObjectActions(ActionType.ALL_EXCEPT_SET, Contributed.INCLUDED);
        }
    }

    @Override
    public List<ObjectAction> getObjectActions(final List<ActionType> requestedTypes, final Contributed contributed) {
        return getObjectActions(requestedTypes, contributed, Filters.<ObjectAction>any());
    }

    @Override
    public List<ObjectAction> getObjectActions(final List<ActionType> requestedTypes, final Contributed contributed, Filter<ObjectAction> filter) {
        final List<ObjectAction> actions = Lists.newArrayList();
        for (final ActionType type : requestedTypes) {
            addActions(type, contributed, filter, actions);
        }
        return actions;
    }

    @Override
    public List<ObjectAction> getObjectActions(final ActionType type, final Contributed contributed) {
        return getObjectActions(type, contributed, Filters.<ObjectAction>any());
    }

    @Override
    public List<ObjectAction> getObjectActions(final ActionType type, final Contributed contributed, Filter<ObjectAction> filter) {
        final List<ObjectAction> actions = Lists.newArrayList();
        return addActions(type, contributed, filter, actions);
    }

    private List<ObjectAction> addActions(final ActionType type, final Contributed contributed, final Filter<ObjectAction> filter, final List<ObjectAction> actionListToAppendTo) {
        if (!isService() && contributed.isIncluded()) {
            actionListToAppendTo.addAll(getContributedActions(type, filter));
        }
        actionListToAppendTo.addAll(getFlattenedActions(objectActions, type, filter));
        return actionListToAppendTo;
    }

    @SuppressWarnings("unchecked")
    private static List<ObjectAction> getFlattenedActions(final List<ObjectAction> objectActions, final ActionType type, final Filter<ObjectAction> filter) {
        final List<ObjectAction> actions = ObjectActions.flattenedActions(objectActions);
        return Lists.newArrayList(Iterables.filter(actions, Filters.asPredicate(Filters.and(ObjectActionFilters.filterOfType(type), filter))));
    }

    @Override
    public List<ObjectAction> getServiceActionsReturning(final ActionType type) {
        return getServiceActionsReturning(Collections.singletonList(type));
    }

    @Override
    public List<ObjectAction> getServiceActionsReturning(final List<ActionType> types) {
        final List<ObjectAction> serviceActions = Lists.newArrayList();
        final List<ObjectAdapter> services = getServicesProvider().getServices();
        for (final ObjectAdapter serviceAdapter : services) {
            appendServiceActionsReturning(serviceAdapter, types, serviceActions);
        }
        return serviceActions;
    }

    private void appendServiceActionsReturning(final ObjectAdapter serviceAdapter, final List<ActionType> types, final List<ObjectAction> relatedActionsToAppendTo) {
        final List<ObjectAction> matchingActionsToAppendTo = Lists.newArrayList();
        for (final ActionType type : types) {
            final List<ObjectAction> serviceActions = serviceAdapter.getSpecification().getObjectActions(type, Contributed.INCLUDED);
            for (final ObjectAction serviceAction : serviceActions) {
                addIfReturnsSubtype(serviceAction, matchingActionsToAppendTo);
            }
        }
        if (matchingActionsToAppendTo.size() > 0) {
            final ObjectActionSet set = new ObjectActionSet("id", serviceAdapter.titleString(), matchingActionsToAppendTo);
            relatedActionsToAppendTo.add(set);
        }
    }

    private void addIfReturnsSubtype(final ObjectAction serviceAction, final List<ObjectAction> matchingActionsToAppendTo) {
        final ObjectSpecification returnType = serviceAction.getReturnType();
        if (returnType == null) {
            return;
        }
        if (returnType.isParentedOrFreeCollection()) {
            final TypeOfFacet facet = serviceAction.getFacet(TypeOfFacet.class);
            if (facet != null) {
                final ObjectSpecification elementType = facet.valueSpec();
                addIfReturnsSubtype(serviceAction, elementType, matchingActionsToAppendTo);
            }
        } else {
            addIfReturnsSubtype(serviceAction, returnType, matchingActionsToAppendTo);
        }
    }

    private void addIfReturnsSubtype(final ObjectAction serviceAction, final ObjectSpecification actionType, final List<ObjectAction> matchingActionsToAppendTo) {
        if (actionType.isOfType(this)) {
            matchingActionsToAppendTo.add(serviceAction);
        }
    }

    // //////////////////////////////////////////////////////////////////////
    // contributed actions
    // //////////////////////////////////////////////////////////////////////

    /**
     * Finds all service actions that contribute to this spec, if any.
     * 
     * <p>
     * If this specification {@link #isService() is actually for} a service,
     * then returns an empty array.
     * @param filter TODO
     * 
     * @return an array of {@link ObjectActionSet}s (!!), each of which contains
     *         {@link ObjectAction}s of the requested type.
     * 
     */
    protected List<ObjectAction> getContributedActions(final ActionType actionType, Filter<ObjectAction> filter) {
        List<ObjectAction> contributedActionSets = getContributedActions(actionType);
        return Lists.newArrayList(Iterables.filter(contributedActionSets, Filters.asPredicate(filter)));
    }

    private List<ObjectAction> getContributedActions(final ActionType actionType) {
        if (isService()) {
            return Collections.emptyList();
        }
        List<ObjectAction> contributedActionSets = contributedActionSetsByType.get(actionType);
        if (contributedActionSets == null) {
            // populate an ActionSet with all actions contributed by each
            // service
            contributedActionSets = Lists.newArrayList();
            final List<ObjectAdapter> services = getServicesProvider().getServices();
            for (final ObjectAdapter serviceAdapter : services) {
                addContributedActionsIfAny(serviceAdapter, actionType, contributedActionSets);
            }
            contributedActionSetsByType.put(actionType, contributedActionSets);
        }
        return contributedActionSets;
    }

    private void addContributedActionsIfAny(final ObjectAdapter serviceAdapter, final ActionType actionType, final List<ObjectAction> contributedActionSetsToAppendTo) {
        final ObjectSpecification specification = serviceAdapter.getSpecification();
        if (specification == this) {
            return;
        }
        final List<ObjectAction> contributedActions = findContributedActions(specification, actionType);
        // only add if there are matching subactions.
        if (contributedActions.size() == 0) {
            return;
        }
        final ObjectActionSet contributedActionSet = new ObjectActionSet("id", serviceAdapter.titleString(), contributedActions);
        contributedActionSetsToAppendTo.add(contributedActionSet);
    }

    private List<ObjectAction> findContributedActions(final ObjectSpecification specification, final ActionType actionType) {
        final List<ObjectAction> contributedActions = Lists.newArrayList();
        final List<ObjectAction> serviceActions = specification.getObjectActions(actionType, Contributed.INCLUDED, Filters.<ObjectAction>any());
        for (final ObjectAction serviceAction : serviceActions) {
            if (serviceAction.isAlwaysHidden()) {
                continue;
            }
            // see if qualifies by inspecting all parameters
            if (matchesParameterOf(serviceAction)) {
                contributedActions.add(serviceAction);
            }
        }
        return contributedActions;
    }

    private boolean matchesParameterOf(final ObjectAction serviceAction) {
        final List<ObjectActionParameter> params = serviceAction.getParameters();
        for (final ObjectActionParameter param : params) {
            if (isOfType(param.getSpecification())) {
                return true;
            }
        }
        return false;
    }

    // //////////////////////////////////////////////////////////////////////
    // validity
    // //////////////////////////////////////////////////////////////////////

    @Override
    public Consent isValid(final ObjectAdapter inObject) {
        return isValidResult(inObject).createConsent();
    }

    /**
     * TODO: currently this method is hard-coded to assume all interactions are
     * initiated {@link InteractionInvocationMethod#BY_USER by user}.
     */
    @Override
    public InteractionResult isValidResult(final ObjectAdapter targetObjectAdapter) {
        final ObjectValidityContext validityContext = createValidityInteractionContext(deploymentCategory, getAuthenticationSession(), InteractionInvocationMethod.BY_USER, targetObjectAdapter);
        return InteractionUtils.isValidResult(this, validityContext);
    }

    /**
     * Create an {@link InteractionContext} representing an attempt to save the
     * object.
     */
    @Override
    public ObjectValidityContext createValidityInteractionContext(DeploymentCategory deploymentCategory, final AuthenticationSession session, final InteractionInvocationMethod interactionMethod, final ObjectAdapter targetObjectAdapter) {
        return new ObjectValidityContext(deploymentCategory, session, interactionMethod, targetObjectAdapter, getIdentifier());
    }

    // //////////////////////////////////////////////////////////////////////
    // convenience isXxx (looked up from facets)
    // //////////////////////////////////////////////////////////////////////

    @Override
    public boolean isImmutable() {
        return containsFacet(ImmutableFacet.class);
    }

    @Override
    public boolean isHidden() {
        return containsFacet(HiddenFacet.class);
    }

    @Override
    public boolean isParseable() {
        return containsFacet(ParseableFacet.class);
    }

    @Override
    public boolean isEncodeable() {
        return containsFacet(EncodableFacet.class);
    }

    @Override
    public boolean isValue() {
        return containsFacet(ValueFacet.class);
    }

    @Override
    public boolean isParented() {
        return containsFacet(ParentedFacet.class);
    }

    @Override
    public boolean isParentedOrFreeCollection() {
        return containsFacet(CollectionFacet.class);
    }

    @Override
    public boolean isNotCollection() {
        return !isParentedOrFreeCollection();
    }

    @Override
    public boolean isValueOrIsParented() {
        return isValue() || isParented();
    }

    // //////////////////////////////////////////////////////////////////////
    // misc
    // //////////////////////////////////////////////////////////////////////

    @Override
    public Object createObject() {
        throw new UnsupportedOperationException(getFullIdentifier());
    }

    @Override
    public ObjectAdapter initialize(ObjectAdapter objectAdapter) {
        return objectAdapter;
    }

    // //////////////////////////////////////////////////////////////////////
    // toString
    // //////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        final ToString str = new ToString(this);
        str.append("class", getFullIdentifier());
        return str.toString();
    }

    // //////////////////////////////////////////////////////////////////////
    // Convenience
    // //////////////////////////////////////////////////////////////////////

    /**
     * convenience method to return the current {@link AuthenticationSession}
     * from the {@link #getAuthenticationSessionProvider() injected}
     * {@link AuthenticationSessionProvider}.
     */
    protected final AuthenticationSession getAuthenticationSession() {
        return getAuthenticationSessionProvider().getAuthenticationSession();
    }

    // //////////////////////////////////////////////////////////////////////
    // Dependencies (injected in constructor)
    // //////////////////////////////////////////////////////////////////////

    protected AuthenticationSessionProvider getAuthenticationSessionProvider() {
        return authenticationSessionProvider;
    }

    public ServicesProvider getServicesProvider() {
        return servicesProvider;
    }

    public ObjectInstantiator getObjectInstantiator() {
        return objectInstantiator;
    }

    public SpecificationLoader getSpecificationLookup() {
        return specificationLookup;
    }

}
