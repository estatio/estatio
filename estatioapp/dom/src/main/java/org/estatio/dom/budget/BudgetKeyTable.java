/*
 *
 *  Copyright 2012-2015 Eurocommercial Properties NV
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
package org.estatio.dom.budget;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.EstatioDomainObject;
import org.estatio.dom.WithIntervalMutable;
import org.estatio.dom.apptenancy.WithApplicationTenancyProperty;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.Unit;
import org.estatio.dom.asset.Units;
import org.estatio.dom.valuetypes.LocalDateInterval;

@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(strategy = IdGeneratorStrategy.NATIVE, column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Queries({
        @Query(
                name = "findBudgetKeyTableByName", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.budget.BudgetKeyTable " +
                        "WHERE name == :name "),
        @Query(
                name = "findBudgetKeyTableByNameMatches", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.budget.BudgetKeyTable " +
                        "WHERE name.toLowerCase().indexOf(:name) >= 0 "),
        @Query(
                name = "findByProperty", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.budget.BudgetKeyTable " +
                        "WHERE property == :property ")
})
@DomainObject(editing = Editing.DISABLED,
        autoCompleteRepository = BudgetKeyTables.class,
        autoCompleteAction = "autoComplete")
public class BudgetKeyTable extends EstatioDomainObject<Budget> implements WithIntervalMutable<BudgetKeyTable>, WithApplicationTenancyProperty {

    public BudgetKeyTable() {
        super("property, name, startDate, endDate");
    }

    public String title() {
        return this.getName();
    }

    public String toString() {
        return this.getName();
    }

    private Property property;

    @javax.jdo.annotations.Column(allowsNull = "false")
    @MemberOrder(sequence = "2")
    @PropertyLayout(hidden = Where.PARENTED_TABLES)
    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    // //////////////////////////////////////

    private String name;

    @javax.jdo.annotations.Column(allowsNull = "false")
    @MemberOrder(sequence = "1")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BudgetKeyTable changeName(final String name) {
        setName(name);
        return this;
    }

    public String validateChangeName(final String name) {
        if (name.equals(null)) {
            return "Name can't be empty";
        }
        return null;
    }

    public String default0ChangeName(final String name) {
        return getName();
    }

    // //////////////////////////////////////

    private LocalDate startDate;

    @javax.jdo.annotations.Column(allowsNull = "true")
    @MemberOrder(sequence = "3")
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    // //////////////////////////////////////

    private LocalDate endDate;

    @javax.jdo.annotations.Column(allowsNull = "true")
    @MemberOrder(sequence = "4")
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

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

    private boolean isActiveOn(final LocalDate date) {
        return LocalDateInterval.including(this.getStartDate(), this.getEndDate()).contains(date);
    }

    // //////////////////////////////////////

    private WithIntervalMutable.Helper<BudgetKeyTable> changeDates = new WithIntervalMutable.Helper<BudgetKeyTable>(this);

    WithIntervalMutable.Helper<BudgetKeyTable> getChangeDates() {
        return changeDates;
    }

    @Override
    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public BudgetKeyTable changeDates(
            final @ParameterLayout(named = "Start date") @Parameter(optionality = Optionality.OPTIONAL) LocalDate startDate,
            final @ParameterLayout(named = "End date") @Parameter(optionality = Optionality.OPTIONAL) LocalDate endDate) {
        return getChangeDates().changeDates(startDate, endDate);
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

    // //////////////////////////////////////


    private BudgetFoundationValueType foundationValueType;

    @javax.jdo.annotations.Column(allowsNull = "false")
    @MemberOrder(sequence = "5")
    public BudgetFoundationValueType getFoundationValueType() {
        return foundationValueType;
    }

    public void setFoundationValueType(BudgetFoundationValueType foundationValueType) {
        this.foundationValueType = foundationValueType;
    }

    public BudgetKeyTable changeFoundationValueType(
            final @ParameterLayout(named = "Foundation value type") BudgetFoundationValueType foundationValueType) {
        setFoundationValueType(foundationValueType);
        return this;
    }

    public BudgetFoundationValueType default0ChangeFoundationValueType(final BudgetFoundationValueType foundationValueType) {
        return getFoundationValueType();
    }

    public String validateChangeFoundationValueType(final BudgetFoundationValueType foundationValueType) {
        if (foundationValueType.equals(null)) {
            return "Foundation value type can't be empty";
        }
        return null;
    }

    // //////////////////////////////////////

    private BudgetKeyValueMethod keyValueMethod;

    @javax.jdo.annotations.Column(allowsNull = "false")
    @MemberOrder(sequence = "6")
    public BudgetKeyValueMethod getKeyValueMethod() {
        return keyValueMethod;
    }

    public void setKeyValueMethod(BudgetKeyValueMethod keyValueMethod) {
        this.keyValueMethod = keyValueMethod;
    }

    public BudgetKeyTable changeKeyValueMethod(
            final @ParameterLayout(named = "Key value method") BudgetKeyValueMethod keyValueMethod) {
        setKeyValueMethod(keyValueMethod);
        return this;
    }

    public BudgetKeyValueMethod default0ChangeKeyValueMethod(final BudgetKeyValueMethod keyValueMethod) {
        return getKeyValueMethod();
    }

    public String validateChangeKeyValueMethod(final BudgetKeyValueMethod keyValueMethod) {
        if (keyValueMethod.equals(null)) {
            return "Key value method can't be empty";
        }
        return null;
    }

    // //////////////////////////////////////

    private SortedSet<BudgetKeyItem> budgetKeyItems = new TreeSet<BudgetKeyItem>();

    @CollectionLayout(render = RenderType.EAGERLY)
    @Persistent(mappedBy = "budgetKeyTable", dependentElement = "true")
    public SortedSet<BudgetKeyItem> getBudgetKeyItems() {
        return budgetKeyItems;
    }

    public void setBudgetKeyItems(final SortedSet<BudgetKeyItem> budgetKeyItems) {
        this.budgetKeyItems = budgetKeyItems;
    }

    // //////////////////////////////////////

    public BudgetKeyTable generateBudgetKeyItems(
            @ParameterLayout(named="Are you sure you want to delete all Budget Key Items and generate new ones?")
            @Parameter(optionality= Optionality.OPTIONAL)
            boolean confirmGenerate){

        //delete old items
        for (Iterator<BudgetKeyItem> it = this.getBudgetKeyItems().iterator(); it.hasNext();) {
            it.next().deleteBudgetKeyItem();
        }

        for (Unit unit :  units.findByProperty(this.getProperty())){

            BigDecimal denominator = BigDecimal.ZERO;
            for (Unit u :  units.findByProperty(this.getProperty())) {
                denominator = denominator.add(getFoundationValueType().valueOf(u));
            }

            BigDecimal numerator = BigDecimal.ZERO;
            numerator = numerator.add(getFoundationValueType().valueOf(unit));
            BigDecimal keyValue = getKeyValueMethod().calculate(numerator, denominator);
            budgetKeyItemsRepo.newBudgetKeyItem(
                    this,
                    unit,
                    getFoundationValueType().valueOf(unit),
                    keyValue);

        }

        return this;

    }

    public String validateGenerateBudgetKeyItems(boolean confirmGenerate){
        return confirmGenerate? null:"Please confirm";
    }

    // //////////////////////////////////////

    @Override
    @MemberOrder(sequence = "7")
    @PropertyLayout(hidden = Where.EVERYWHERE)
    public ApplicationTenancy getApplicationTenancy() {
        return getProperty().getApplicationTenancy();
    }

    // //////////////////////////////////////

    public boolean isValid(){

        return getKeyValueMethod().isValid(this);
    }

    @Inject
    Units units;

    @Inject
    BudgetKeyItems budgetKeyItemsRepo;
}
