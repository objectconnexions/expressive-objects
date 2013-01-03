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

package uk.co.objectconnexions.expressiveobjects.progmodels.dflt;

import uk.co.objectconnexions.expressiveobjects.core.metamodel.progmodel.ProgrammingModelAbstract;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.actions.debug.annotation.DebugAnnotationFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.actions.defaults.method.ActionDefaultsFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.actions.exploration.annotation.ExplorationAnnotationFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.actions.invoke.ActionInvocationFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.actions.notcontributed.annotation.NotContributedAnnotationFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.actions.notinservicemenu.annotation.NotInServiceMenuAnnotationFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.actions.notinservicemenu.method.NotInServiceMenuMethodFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.actions.prototype.annotation.PrototypeAnnotationFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.actions.semantics.ActionSemanticsAnnotationFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.actions.semantics.ActionSemanticsFallbackFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.actions.semantics.IdempotentAnnotationFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.actions.semantics.QueryOnlyAnnotationFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.actions.typeof.annotation.TypeOfAnnotationForActionsFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.actions.validate.method.ActionValidationFacetViaValidateMethodFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.collections.accessor.CollectionAccessorFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.collections.aggregated.ParentedSinceCollectionFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.collections.clear.CollectionClearFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.collections.collection.CollectionFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.collections.disabled.fromimmutable.DisabledFacetForCollectionDerivedFromImmutableTypeFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.collections.modify.CollectionAddRemoveAndValidateFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.collections.notpersisted.annotation.NotPersistedAnnotationForCollectionFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.collections.typeof.TypeOfAnnotationForCollectionsFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.fallback.FallbackFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.members.describedas.annotation.DescribedAsAnnotationOnMemberFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.members.describedas.staticmethod.DescribedAsFacetViaDescriptionMethodFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.members.disabled.annotation.DisabledAnnotationFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.members.disabled.forsession.DisabledFacetViaDisableForSessionMethodFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.members.disabled.method.DisabledFacetViaDisableMethodFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.members.disabled.staticmethod.DisabledFacetViaProtectMethodFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.members.hidden.annotation.HiddenAnnotationForMemberFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.members.hidden.forsession.HiddenFacetViaHideForSessionMethodFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.members.hidden.method.HiddenFacetViaHideMethodFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.members.hidden.staticmethod.HiddenFacetViaAlwaysHideMethodFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.members.named.annotation.NamedAnnotationOnMemberFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.members.named.staticmethod.NamedFacetViaNameMethodFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.members.order.MemberOrderAnnotationFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.members.resolve.ResolveAnnotationFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.aggregated.annotation.AggregatedAnnotationFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.autocomplete.annotation.AutoCompleteAnnotationFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.bounded.annotation.BoundedAnnotationFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.bounded.markerifc.BoundedMarkerInterfaceFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.callbacks.create.CreatedCallbackFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.callbacks.load.LoadCallbackFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.callbacks.persist.PersistCallbackFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.callbacks.persist.PersistCallbackViaSaveMethodFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.callbacks.remove.RemoveCallbackFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.callbacks.update.UpdateCallbackFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.choices.enums.EnumFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.defaults.annotation.DefaultedAnnotationFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.describedas.annotation.DescribedAsAnnotationOnTypeFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.dirty.method.DirtyMethodsFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.disabled.method.DisabledObjectViaDisabledMethodFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.encodeable.EncodableAnnotationFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.facets.annotation.FacetsAnnotationFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.hidden.HiddenAnnotationForTypeFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.hidden.method.HiddenObjectViaHiddenMethodFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.icon.method.IconMethodFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.ignore.annotation.RemoveProgrammaticOrIgnoreAnnotationMethodsFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.ignore.expressiveobjects.RemoveSetDomainObjectContainerMethodFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.ignore.expressiveobjects.RemoveStaticGettersAndSettersFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.ignore.javalang.IteratorFilteringFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.ignore.javalang.RemoveGetClassMethodFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.ignore.javalang.RemoveInitMethodFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.ignore.javalang.RemoveJavaLangComparableMethodsFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.ignore.javalang.RemoveJavaLangObjectMethodsFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.ignore.javalang.RemoveSuperclassMethodsFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.ignore.javalang.SyntheticMethodFilteringFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.ignore.jdo.RemoveJdoEnhancementTypesFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.immutable.annotation.ImmutableAnnotationFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.immutable.markerifc.ImmutableMarkerInterfaceFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.mask.annotation.MaskAnnotationForTypeFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.maxlen.annotation.MaxLengthAnnotationForTypeFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.membergroups.annotation.MemberGroupsAnnotationElseFallbackFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.multiline.annotation.MultiLineAnnotationOnTypeFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.named.annotation.NamedAnnotationOnTypeFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.named.staticmethod.NamedFacetViaSingularNameStaticMethodFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.notpersistable.NotPersistableAnnotationFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.notpersistable.NotPersistableMarkerInterfacesFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.objecttype.ObjectSpecIdAnnotationFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.objecttype.ObjectTypeDerivedFromClassNameFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.orderactions.ActionOrderAnnotationFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.orderfields.FieldOrderAnnotationFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.parseable.ParseableFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.plural.annotation.PluralAnnotationFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.plural.staticmethod.PluralMethodFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.regex.annotation.RegExFacetAnnotationForTypeFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.title.TitleMethodFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.title.annotation.TitleAnnotationFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.typicallen.annotation.TypicalLengthAnnotationOnTypeFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.validate.method.ValidateObjectViaValidateMethodFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.validperspec.MustSatisfySpecificationOnTypeFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.validprops.ObjectValidPropertiesFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.value.annotation.ValueFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.object.viewmodel.annotation.ViewModelAnnotationFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.paged.PagedAnnotationOnCollectionFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.paged.PagedAnnotationOnTypeFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.param.choices.enums.ParameterChoicesFacetDerivedFromChoicesFacetFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.param.choices.method.ActionChoicesFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.param.choices.methodnum.ActionParameterChoicesFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.param.defaults.fromtype.ParameterDefaultDerivedFromTypeFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.param.defaults.methodnum.ActionParameterDefaultsFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.param.describedas.annotation.DescribedAsAnnotationOnParameterFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.param.mandatory.annotation.OptionalAnnotationForParameterFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.param.mandatory.dflt.MandatoryDefaultForParametersFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.param.multiline.annotation.MultiLineAnnotationOnParameterFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.param.named.annotation.NamedAnnotationOnParameterFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.param.typicallen.annotation.TypicalLengthAnnotationOnParameterFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.param.typicallen.fromtype.TypicalLengthFacetForParameterDerivedFromTypeFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.param.validate.maskannot.MaskAnnotationForParameterFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.param.validate.maxlenannot.MaxLengthAnnotationForParameterFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.param.validate.perspec.MustSatisfySpecificationOnParameterFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.param.validate.regexannot.RegExFacetAnnotationForParameterFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.properties.accessor.PropertyAccessorFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.properties.choices.enums.PropertyChoicesFacetDerivedFromChoicesFacetFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.properties.choices.method.PropertyChoicesFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.properties.defaults.fromtype.PropertyDefaultDerivedFromTypeFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.properties.defaults.method.PropertyDefaultFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.properties.disabled.fromimmutable.DisabledFacetForPropertyDerivedFromImmutableTypeFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.properties.mandatory.annotation.OptionalAnnotationForPropertyFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.properties.mandatory.dflt.MandatoryDefaultForPropertiesFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.properties.mandatory.staticmethod.PropertyOptionalFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.properties.modify.PropertyModifyFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.properties.modify.PropertySetAndClearFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.properties.multiline.annotation.MultiLineAnnotationOnPropertyFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.properties.notpersisted.annotation.NotPersistedAnnotationForPropertyFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.properties.typicallen.annotation.TypicalLengthAnnotationOnPropertyFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.properties.typicallen.fromtype.TypicalLengthFacetForPropertyDerivedFromTypeFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.properties.validate.PropertyValidateDefaultFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.properties.validate.PropertyValidateFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.properties.validate.maskannot.MaskAnnotationForPropertyFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.properties.validate.maxlenannot.MaxLengthAnnotationForPropertyFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.properties.validate.perspec.MustSatisfySpecificationOnPropertyFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.properties.validate.regexannot.RegExFacetAnnotationForPropertyFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.bigdecimal.BigDecimalValueTypeFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.biginteger.BigIntegerValueTypeFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.blobs.BlobValueTypeFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.booleans.BooleanPrimitiveValueTypeFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.booleans.BooleanWrapperValueTypeFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.bytes.BytePrimitiveValueTypeFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.bytes.ByteWrapperValueTypeFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.chars.CharPrimitiveValueTypeFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.chars.CharWrapperValueTypeFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.clobs.ClobValueTypeFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.color.ColorValueTypeFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.date.DateValueTypeFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.datejodalocal.JodaLocalDateValueTypeFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.datesql.JavaSqlDateValueTypeFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.datetime.DateTimeValueTypeFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.datetimejoda.JodaDateTimeValueTypeFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.datetimejodalocal.JodaLocalDateTimeValueTypeFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.dateutil.JavaUtilDateValueTypeFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.floats.FloatPrimitiveValueTypeFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.floats.FloatWrapperValueTypeFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.image.ImageValueTypeFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.imageawt.JavaAwtImageValueTypeFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.integer.IntPrimitiveValueTypeFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.integer.IntWrapperValueTypeFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.longs.DoublePrimitiveValueTypeFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.longs.DoubleWrapperValueTypeFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.longs.LongPrimitiveValueTypeFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.longs.LongWrapperValueTypeFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.money.MoneyValueTypeFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.password.PasswordValueTypeFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.percentage.PercentageValueTypeFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.shortint.ShortPrimitiveValueTypeFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.shortint.ShortWrapperValueTypeFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.string.StringValueTypeFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.time.TimeValueTypeFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.timesql.JavaSqlTimeValueTypeFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.timestamp.TimeStampValueTypeFacetFactory;
import uk.co.objectconnexions.expressiveobjects.core.progmodel.facets.value.timestampsql.JavaSqlTimeStampValueTypeFacetFactory;

public final class ProgrammingModelFacetsJava5 extends ProgrammingModelAbstract {

    public ProgrammingModelFacetsJava5() {
        
        // must be first, so any Facets created can be replaced by other
        // FacetFactorys later.
        addFactory(FallbackFacetFactory.class);
        addFactory(ObjectTypeDerivedFromClassNameFacetFactory.class);
        
        addFactory(IteratorFilteringFacetFactory.class);
        addFactory(SyntheticMethodFilteringFacetFactory.class);
        addFactory(RemoveSuperclassMethodsFacetFactory.class);
        addFactory(RemoveJavaLangObjectMethodsFacetFactory.class);
        addFactory(RemoveJavaLangComparableMethodsFacetFactory.class);
        addFactory(RemoveSetDomainObjectContainerMethodFacetFactory.class);
        addFactory(RemoveInitMethodFacetFactory.class);
        addFactory(RemoveStaticGettersAndSettersFacetFactory.class);
        addFactory(RemoveGetClassMethodFacetFactory.class);
        addFactory(RemoveProgrammaticOrIgnoreAnnotationMethodsFacetFactory.class);
        addFactory(RemoveJdoEnhancementTypesFacetFactory.class);

        // must be before any other FacetFactories that install
        // MandatoryFacet.class facets
        addFactory(MandatoryDefaultForPropertiesFacetFactory.class);
        addFactory(MandatoryDefaultForParametersFacetFactory.class);

        addFactory(PropertyValidateDefaultFacetFactory.class);

        // enum support
        addFactory(EnumFacetFactory.class);
        addFactory(ParameterChoicesFacetDerivedFromChoicesFacetFacetFactory.class);
        addFactory(PropertyChoicesFacetDerivedFromChoicesFacetFacetFactory.class);


        // properties
        addFactory(PropertyAccessorFacetFactory.class);
        addFactory(PropertySetAndClearFacetFactory.class);
        // must come after PropertySetAndClearFacetFactory
        addFactory(PropertyModifyFacetFactory.class);
        
        addFactory(PropertyValidateFacetFactory.class);
        addFactory(PropertyChoicesFacetFactory.class);
        addFactory(PropertyDefaultFacetFactory.class);
        addFactory(PropertyOptionalFacetFactory.class);

        // collections
        addFactory(CollectionAccessorFacetFactory.class);
        addFactory(CollectionClearFacetFactory.class);
        addFactory(CollectionAddRemoveAndValidateFacetFactory.class);

        // actions
        addFactory(ActionInvocationFacetFactory.class);
        addFactory(ActionValidationFacetViaValidateMethodFacetFactory.class);
        addFactory(ActionChoicesFacetFactory.class);
        addFactory(ActionParameterChoicesFacetFactory.class);
        addFactory(ActionDefaultsFacetFactory.class);
        addFactory(ActionParameterDefaultsFacetFactory.class);
        addFactory(QueryOnlyAnnotationFacetFactory.class);
        addFactory(IdempotentAnnotationFacetFactory.class);
        addFactory(ActionSemanticsAnnotationFacetFactory.class);
        addFactory(ActionSemanticsFallbackFacetFactory.class);

        // members in general
        addFactory(NamedFacetViaNameMethodFacetFactory.class);
        addFactory(DescribedAsFacetViaDescriptionMethodFacetFactory.class);
        addFactory(DisabledFacetViaDisableForSessionMethodFacetFactory.class);
        addFactory(DisabledFacetViaDisableMethodFacetFactory.class);
        addFactory(DisabledFacetViaProtectMethodFacetFactory.class);
        addFactory(HiddenFacetViaHideForSessionMethodFacetFactory.class);
        addFactory(HiddenFacetViaAlwaysHideMethodFacetFactory.class);
        addFactory(HiddenFacetViaHideMethodFacetFactory.class);
        addFactory(ResolveAnnotationFacetFactory.class);

        // objects
        addFactory(ObjectSpecIdAnnotationFacetFactory.class);
        addFactory(IconMethodFacetFactory.class);

        addFactory(CreatedCallbackFacetFactory.class);
        addFactory(LoadCallbackFacetFactory.class);
        addFactory(PersistCallbackViaSaveMethodFacetFactory.class);
        addFactory(PersistCallbackFacetFactory.class);
        addFactory(UpdateCallbackFacetFactory.class);
        addFactory(RemoveCallbackFacetFactory.class);

        addFactory(DirtyMethodsFacetFactory.class);
        addFactory(ValidateObjectViaValidateMethodFacetFactory.class);
        addFactory(ObjectValidPropertiesFacetFactory.class);
        addFactory(PluralMethodFacetFactory.class);
        addFactory(NamedFacetViaSingularNameStaticMethodFacetFactory.class);
        addFactory(TitleAnnotationFacetFactory.class);
        addFactory(TitleMethodFacetFactory.class);

        addFactory(MemberOrderAnnotationFacetFactory.class);
        addFactory(ActionOrderAnnotationFacetFactory.class);
        addFactory(FieldOrderAnnotationFacetFactory.class);
        addFactory(MemberGroupsAnnotationElseFallbackFacetFactory.class);
        
        addFactory(AggregatedAnnotationFacetFactory.class);
        addFactory(BoundedAnnotationFacetFactory.class);
        addFactory(BoundedMarkerInterfaceFacetFactory.class);
        addFactory(DebugAnnotationFacetFactory.class);

        addFactory(DefaultedAnnotationFacetFactory.class);
        addFactory(PropertyDefaultDerivedFromTypeFacetFactory.class);
        addFactory(ParameterDefaultDerivedFromTypeFacetFactory.class);

        addFactory(DescribedAsAnnotationOnTypeFacetFactory.class);
        addFactory(DescribedAsAnnotationOnMemberFacetFactory.class);
        addFactory(DescribedAsAnnotationOnParameterFacetFactory.class);

        addFactory(DisabledAnnotationFacetFactory.class);
        addFactory(EncodableAnnotationFacetFactory.class);
        addFactory(ExplorationAnnotationFacetFactory.class);
        addFactory(PrototypeAnnotationFacetFactory.class);
        addFactory(NotContributedAnnotationFacetFactory.class);
        addFactory(NotInServiceMenuAnnotationFacetFactory.class);
        addFactory(NotInServiceMenuMethodFacetFactory.class);

        addFactory(HiddenAnnotationForTypeFacetFactory.class);
        // must come after the TitleAnnotationFacetFactory, because can act as an override
        addFactory(HiddenAnnotationForMemberFacetFactory.class);

        addFactory(HiddenObjectViaHiddenMethodFacetFactory.class);
        addFactory(DisabledObjectViaDisabledMethodFacetFactory.class);

        addFactory(ImmutableAnnotationFacetFactory.class);
        addFactory(DisabledFacetForPropertyDerivedFromImmutableTypeFacetFactory.class);
        addFactory(DisabledFacetForCollectionDerivedFromImmutableTypeFacetFactory.class);

        addFactory(ImmutableMarkerInterfaceFacetFactory.class);

        addFactory(ViewModelAnnotationFacetFactory.class);

        addFactory(MaxLengthAnnotationForTypeFacetFactory.class);
        addFactory(MaxLengthAnnotationForPropertyFacetFactory.class);
        addFactory(MaxLengthAnnotationForParameterFacetFactory.class);

        addFactory(MustSatisfySpecificationOnTypeFacetFactory.class);
        addFactory(MustSatisfySpecificationOnPropertyFacetFactory.class);
        addFactory(MustSatisfySpecificationOnParameterFacetFactory.class);

        addFactory(MultiLineAnnotationOnTypeFacetFactory.class);
        addFactory(MultiLineAnnotationOnPropertyFacetFactory.class);
        addFactory(MultiLineAnnotationOnParameterFacetFactory.class);

        addFactory(NamedAnnotationOnTypeFacetFactory.class);
        addFactory(NamedAnnotationOnMemberFacetFactory.class);
        addFactory(NamedAnnotationOnParameterFacetFactory.class);

        addFactory(NotPersistableAnnotationFacetFactory.class);
        addFactory(NotPersistableMarkerInterfacesFacetFactory.class);

        addFactory(NotPersistedAnnotationForCollectionFacetFactory.class);
        addFactory(NotPersistedAnnotationForPropertyFacetFactory.class);

        addFactory(OptionalAnnotationForPropertyFacetFactory.class);
        addFactory(OptionalAnnotationForParameterFacetFactory.class);

        addFactory(ParseableFacetFactory.class);
        addFactory(PluralAnnotationFacetFactory.class);
        addFactory(PagedAnnotationOnTypeFacetFactory.class);
        addFactory(PagedAnnotationOnCollectionFacetFactory.class);

        addFactory(AutoCompleteAnnotationFacetFactory.class);

        // must come after any facets that install titles
        addFactory(MaskAnnotationForTypeFacetFactory.class);
        addFactory(MaskAnnotationForPropertyFacetFactory.class);
        addFactory(MaskAnnotationForParameterFacetFactory.class);

        // must come after any facets that install titles, and after mask
        // if takes precedence over mask.
        addFactory(RegExFacetAnnotationForTypeFacetFactory.class);
        addFactory(RegExFacetAnnotationForPropertyFacetFactory.class);
        addFactory(RegExFacetAnnotationForParameterFacetFactory.class);

        addFactory(TypeOfAnnotationForCollectionsFacetFactory.class);
        addFactory(TypeOfAnnotationForActionsFacetFactory.class);

        addFactory(TypicalLengthFacetForPropertyDerivedFromTypeFacetFactory.class);
        addFactory(TypicalLengthFacetForParameterDerivedFromTypeFacetFactory.class);

        addFactory(TypicalLengthAnnotationOnTypeFacetFactory.class);
        addFactory(TypicalLengthAnnotationOnPropertyFacetFactory.class);
        addFactory(TypicalLengthAnnotationOnParameterFacetFactory.class);

        // built-in value types for Java language
        addFactory(BooleanPrimitiveValueTypeFacetFactory.class);
        addFactory(BooleanWrapperValueTypeFacetFactory.class);
        addFactory(BytePrimitiveValueTypeFacetFactory.class);
        addFactory(ByteWrapperValueTypeFacetFactory.class);
        addFactory(ShortPrimitiveValueTypeFacetFactory.class);
        addFactory(ShortWrapperValueTypeFacetFactory.class);
        addFactory(IntPrimitiveValueTypeFacetFactory.class);
        addFactory(IntWrapperValueTypeFacetFactory.class);
        addFactory(LongPrimitiveValueTypeFacetFactory.class);
        addFactory(LongWrapperValueTypeFacetFactory.class);
        addFactory(FloatPrimitiveValueTypeFacetFactory.class);
        addFactory(FloatWrapperValueTypeFacetFactory.class);
        addFactory(DoublePrimitiveValueTypeFacetFactory.class);
        addFactory(DoubleWrapperValueTypeFacetFactory.class);
        addFactory(CharPrimitiveValueTypeFacetFactory.class);
        addFactory(CharWrapperValueTypeFacetFactory.class);
        addFactory(BigIntegerValueTypeFacetFactory.class);
        addFactory(BigDecimalValueTypeFacetFactory.class);
        addFactory(JavaSqlDateValueTypeFacetFactory.class);
        addFactory(JavaSqlTimeValueTypeFacetFactory.class);
        addFactory(JavaUtilDateValueTypeFacetFactory.class);
        addFactory(JavaSqlTimeStampValueTypeFacetFactory.class);
        addFactory(StringValueTypeFacetFactory.class);

        addFactory(JavaAwtImageValueTypeFacetFactory.class);
        
        // applib values
        addFactory(BlobValueTypeFacetFactory.class);
        addFactory(ClobValueTypeFacetFactory.class);
        addFactory(DateValueTypeFacetFactory.class);
        addFactory(DateTimeValueTypeFacetFactory.class);
        addFactory(ColorValueTypeFacetFactory.class);
        addFactory(MoneyValueTypeFacetFactory.class);
        addFactory(PasswordValueTypeFacetFactory.class);
        addFactory(PercentageValueTypeFacetFactory.class);
        addFactory(TimeStampValueTypeFacetFactory.class);
        addFactory(TimeValueTypeFacetFactory.class);
        addFactory(ImageValueTypeFacetFactory.class);

        // jodatime values
        addFactory(JodaLocalDateValueTypeFacetFactory.class);
        addFactory(JodaLocalDateTimeValueTypeFacetFactory.class);
        addFactory(JodaDateTimeValueTypeFacetFactory.class);
        
        // written to not trample over TypeOf if already installed
        addFactory(CollectionFacetFactory.class);
        // must come after CollectionFacetFactory
        addFactory(ParentedSinceCollectionFacetFactory.class);

        // so we can dogfood the NO applib "value" types
        addFactory(ValueFacetFactory.class);

        addFactory(FacetsAnnotationFacetFactory.class);
    }




}
