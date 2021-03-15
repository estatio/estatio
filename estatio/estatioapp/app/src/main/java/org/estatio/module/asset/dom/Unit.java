/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
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
package org.estatio.module.asset.dom;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;
import javax.jdo.annotations.InheritanceStrategy;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.schema.utils.jaxbadapters.PersistentEntityAdapter;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;
import org.incode.module.base.dom.with.WithIntervalMutable;

import org.estatio.module.asset.dom.erv.EstimatedRentalValue;
import org.estatio.module.asset.dom.erv.EstimatedRentalValueRepository;
import org.estatio.module.asset.dom.erv.Type;
import org.estatio.module.base.dom.apptenancy.WithApplicationTenancyProperty;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        schema = "dbo" // Isis' ObjectSpecId inferred from @Discriminator
)
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Discriminator("org.estatio.dom.asset.Unit")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByReferenceOrName", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.asset.dom.Unit "
                        + "WHERE (reference.matches(:referenceOrName) "
                        + "   || name.matches(:referenceOrName)) "
                        + "&& (:includeTerminated || endDate == null || endDate >= :date)"),
        @javax.jdo.annotations.Query(
                name = "findByReference", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.asset.dom.Unit "
                        + "WHERE reference.matches(:reference)"),
        @javax.jdo.annotations.Query(
                name = "findByActiveOnDate", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.asset.dom.Unit "
                        + "WHERE (startDate == null || startDate <= :startDate) "
                        + "&& (endDate == null || endDate >= :endDate)"),
        @javax.jdo.annotations.Query(
                name = "findByProperty", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.asset.dom.Unit "
                        + "WHERE (property == :property) "),
        @javax.jdo.annotations.Query(
                name = "findByPropertyAndActiveOnDate", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.asset.dom.Unit "
                        + "WHERE (property == :property) "
                        + "&& (startDate == null || startDate <= :date) "
                        + "&& (endDate == null || endDate >= :date)")
})
@DomainObject(autoCompleteRepository = UnitRepository.class, editing = Editing.DISABLED)
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_CHILD)
@XmlJavaTypeAdapter(PersistentEntityAdapter.class)
public class Unit
        extends FixedAsset<Unit>
        implements WithIntervalMutable<Unit>, WithApplicationTenancyProperty {

    // //////////////////////////////////////

    @PropertyLayout(
            named = "Application Level",
            describedAs = "Determines those users for whom this object is available to view and/or modify."
    )
    public ApplicationTenancy getApplicationTenancy() {
        return getProperty().getApplicationTenancy();
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull = "false", length = UnitType.Meta.MAX_LEN)
    @Getter @Setter
    private UnitType type;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(scale = 2, allowsNull = "true")
    @Getter @Setter
    private BigDecimal area;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(scale = 2, allowsNull = "true")
    @org.apache.isis.applib.annotation.Property(hidden = Where.PARENTED_TABLES)
    @Getter @Setter
    private BigDecimal storageArea;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(scale = 2, allowsNull = "true")
    @org.apache.isis.applib.annotation.Property(hidden = Where.PARENTED_TABLES)
    @Getter @Setter
    private BigDecimal salesArea;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(scale = 2, allowsNull = "true")
    @org.apache.isis.applib.annotation.Property(hidden = Where.PARENTED_TABLES)
    @Getter @Setter
    private BigDecimal mezzanineArea;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(scale = 2, allowsNull = "true")
    @org.apache.isis.applib.annotation.Property(hidden = Where.PARENTED_TABLES)
    @Getter @Setter
    private BigDecimal dehorsArea;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "propertyId", allowsNull = "false")
    @org.apache.isis.applib.annotation.Property(hidden = Where.PARENTED_TABLES, editing = Editing.DISABLED)
    @Getter @Setter
    private Property property;

    // //////////////////////////////////////

    @org.apache.isis.applib.annotation.Property(optionality = Optionality.OPTIONAL, editing = Editing.DISABLED)
    @javax.jdo.annotations.Persistent
    @Getter @Setter
    private LocalDate startDate;

    @org.apache.isis.applib.annotation.Property(optionality = Optionality.OPTIONAL, editing = Editing.DISABLED)
    @javax.jdo.annotations.Persistent
    @Getter @Setter
    private LocalDate endDate;

    // //////////////////////////////////////

    @Programmatic
    public LocalDateInterval getInterval() {
        return LocalDateInterval.including(getStartDate(), getEndDate());
    }

    @Programmatic
    public LocalDateInterval getEffectiveInterval() {
        return getInterval();
    }

    // //////////////////////////////////////

    public boolean isCurrent() {
        return isActiveOn(getClockService().now());
    }

    @Programmatic
    public boolean isActiveOn(final LocalDate date) {
        return LocalDateInterval.including(this.getStartDate(), this.getEndDate()).contains(date);
    }

    // //////////////////////////////////////

    private WithIntervalMutable.Helper<Unit> changeDates = new WithIntervalMutable.Helper<>(this);

    WithIntervalMutable.Helper<Unit> getChangeDates() {
        return changeDates;
    }

    @Override
    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public Unit changeDates(
            final @Parameter(optionality = Optionality.OPTIONAL) LocalDate startDate,
            final @Parameter(optionality = Optionality.OPTIONAL) LocalDate endDate) {
        return getChangeDates().changeDates(startDate, endDate);
    }

    public String disableChangeDates() {
        return null;
    }

    @Override
    public LocalDate default0ChangeDates() {
        return getChangeDates().default0ChangeDates();
    }

    @Override
    public LocalDate default1ChangeDates() {
        return getChangeDates().default1ChangeDates();
    }

    @Override
    public String validateChangeDates(
            final LocalDate startDate,
            final LocalDate endDate) {
        return getChangeDates().validateChangeDates(startDate, endDate);
    }



    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    public Unit resetDates() {
        setStartDate(null);
        setEndDate(null);
        return this;
    }

    public String disableResetDates() {
        return getStartDate() == null && getEndDate() == null ? "Dates are already clear": null;
    }



    // //////////////////////////////////////

    public Unit changeAsset(
            final String name,
            final UnitType type) {
        setName(name);
        setType(type);
        return this;
    }

    public String default0ChangeAsset() {
        return getName();
    }

    public UnitType default1ChangeAsset() {
        return getType();
    }

    // ///////////////////////////////////////

    public Unit changeAreas(
            final @Parameter(optionality = Optionality.OPTIONAL) BigDecimal gla,
            final @Parameter(optionality = Optionality.OPTIONAL) BigDecimal storageArea,
            final @Parameter(optionality = Optionality.OPTIONAL) BigDecimal salesArea,
            final @Parameter(optionality = Optionality.OPTIONAL) BigDecimal mezzanineArea,
            final @Parameter(optionality = Optionality.OPTIONAL) BigDecimal dehorsArea) {
        setArea(gla);
        setStorageArea(storageArea);
        setSalesArea(salesArea);
        setMezzanineArea(mezzanineArea);
        setDehorsArea(dehorsArea);
        return this;
    }

    public BigDecimal default0ChangeAreas() {
        return getArea();
    }

    public BigDecimal default1ChangeAreas() {
        return getStorageArea();
    }

    public BigDecimal default2ChangeAreas() {
        return getSalesArea();
    }

    public BigDecimal default3ChangeAreas() {
        return getMezzanineArea();
    }

    public BigDecimal default4ChangeAreas() {
        return getDehorsArea();
    }

    @Action(semantics = SemanticsOf.SAFE)
    public List<EstimatedRentalValue> getEstimatedRentalValuesByEcp(){
        return estimatedRentalValueRepository.findByUnitAndType(this, Type.VALUED_INTERNALLY);
    }

    public Unit newErvByEcp(final LocalDate date, final BigDecimal value){
        estimatedRentalValueRepository.upsert(this, date, Type.VALUED_INTERNALLY, value);
        return this;
    }

    @Action(semantics = SemanticsOf.SAFE)
    public List<EstimatedRentalValue> getEstimatedRentalValuesByValuer(){
        return estimatedRentalValueRepository.findByUnitAndType(this, Type.VALUED_BY_VALUER);
    }

    public Unit newErvByValuer(final LocalDate date, final BigDecimal value){
        estimatedRentalValueRepository.upsert(this, date, Type.VALUED_BY_VALUER, value);
        return this;
    }

    public static class ReferenceType {

        private ReferenceType() {}

        public static class Meta {

            public static final String REGEX = "(?=.{5,17})([A-Z]{1}-)?([A-Z]{2,4}-[A-Z,0-9,/,+,-]{1,11})";
            public static final String REGEX_DESCRIPTION = "Only letters and numbers devided by at least 1 and at most 3 dashes:\"-\" totalling between 5 and 15 characters. ";

            private Meta() {}

        }

    }

    @Inject
    private EstimatedRentalValueRepository estimatedRentalValueRepository;

}