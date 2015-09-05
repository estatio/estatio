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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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

import org.estatio.app.budget.DistributionService;
import org.estatio.dom.EstatioDomainObject;
import org.estatio.dom.WithIntervalMutable;
import org.estatio.dom.apptenancy.WithApplicationTenancyProperty;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.Unit;
import org.estatio.dom.asset.UnitMenu;
import org.estatio.dom.lease.Occupancies;
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

    @javax.jdo.annotations.Column(name="propertyId", allowsNull = "false")
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

    @javax.jdo.annotations.Column(name="foundationValueTypeId", allowsNull = "false")
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

    @javax.jdo.annotations.Column(name="keyValueMethodId", allowsNull = "false")
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

    //region > numberOfDigits (property)
    private Integer numberOfDigits;

    @javax.jdo.annotations.Column(allowsNull = "false")
    @MemberOrder(sequence = "7")
    public Integer getNumberOfDigits() {
        return numberOfDigits;
    }

    public void setNumberOfDigits(final Integer numberOfDigits) {
        this.numberOfDigits = numberOfDigits;
    }

    public BudgetKeyTable changeNumberOfDigits(
            final @ParameterLayout(named = "Number Of Digits") Integer numberOfDigits) {
        setNumberOfDigits(numberOfDigits);
        return this;
    }

    public Integer default0ChangeNumberOfDigits(final Integer numberOfDigits) {
        return getNumberOfDigits();
    }

    public String validateChangeNumberOfDigits(final Integer numberOfDigits) {
        if (numberOfDigits< 0 || numberOfDigits > 6) {
            return "Number Of Digits must have a value between 0 and 6";
        }
        return null;
    }
    //endregion

    private SortedSet<BudgetKeyItem> budgetKeyItems = new TreeSet<BudgetKeyItem>();

    @CollectionLayout(render = RenderType.EAGERLY)
    @Persistent(mappedBy = "budgetKeyTable", dependentElement = "true")
    public SortedSet<BudgetKeyItem> getBudgetKeyItems() {
        return budgetKeyItems;
    }

    public void setBudgetKeyItems(final SortedSet<BudgetKeyItem> budgetKeyItems) {
        this.budgetKeyItems = budgetKeyItems;
    }

    // /////////////////////////////////////

    public BudgetKeyTable generateBudgetKeyItems(
            @ParameterLayout(named="Are you sure? (All current Budget Key Items will be deleted.)")
            @Parameter(optionality= Optionality.OPTIONAL)
            boolean confirmGenerate){

        //delete old items
        for (Iterator<BudgetKeyItem> it = this.getBudgetKeyItems().iterator(); it.hasNext();) {
            it.next().deleteBudgetKeyItem();
        }

        /*
        create list of input pairs: identifier - sourcevalue
        sourcevalue is determined by BudgetFoundationValueType
        */
        List<Distributable> input = new ArrayList<>();

        for (Unit unit :  unitMenu.findByProperty(this.getProperty())){

            if (unitIntervalValidForThisKeyTable(unit)) {
                BigDecimal sourceValue;
                if (getFoundationValueType().valueOf(unit) != null) {
                    sourceValue = getFoundationValueType().valueOf(unit);
                } else {
                    sourceValue = BigDecimal.ZERO;
                }
                BudgetKeyItem newItem = new BudgetKeyItem();
                newItem.setSourceValue(sourceValue);
                newItem.setValue(BigDecimal.ZERO);
                newItem.setUnit(unit);
                newItem.setBudgetKeyTable(this);
                persistIfNotAlready(newItem);
                input.add(newItem);
            }
        }

        /*
        call distribute method
         */
        DistributionService distributionService = new DistributionService();
        distributionService.distribute(input, getKeyValueMethod().targetTotal(), getNumberOfDigits());

        return this;
    }

    public String validateGenerateBudgetKeyItems(boolean confirmGenerate){
        return confirmGenerate? null:"Please confirm";
    }

    public boolean hideGenerateBudgetKeyItems(boolean confirmGenerate){
        if (getFoundationValueType() == BudgetFoundationValueType.MANUAL) {
            return true;
        }
        return false;
    }

    // //////////////////////////////////////

    public BudgetKeyTable distributeSourceValues(
            @ParameterLayout(named="Are you sure? (All current Target Values will be overwritten.)")
            @Parameter(optionality= Optionality.OPTIONAL)
            boolean confirmDistribute){


        DistributionService distributionService = new DistributionService();
        distributionService.distribute(new ArrayList(getBudgetKeyItems()), getKeyValueMethod().targetTotal(), getNumberOfDigits());


        return this;
    }

    public String validateDistributeSourceValues(boolean confirmDistribute){
        return confirmDistribute? null:"Please confirm";
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
        return (this.isValidForKeyValues() && this.isValidForUnits());
    }

    public boolean isValidForKeyValues(){
        return getKeyValueMethod().isValid(this);
    }

    public boolean isValidForUnits(){
        for (BudgetKeyItem item : this.getBudgetKeyItems()) {
            if (!this.unitIntervalValidForThisKeyTable(item.getUnit())) {
                return false;
            }
            if (!item.getUnit().hasOccupancyOverlappingInterval(this.getInterval())){
                return false;
            }
        }
        return true;
    }

    @Programmatic
    private boolean unitIntervalValidForThisKeyTable(final Unit unit) {
        return unit.getInterval().contains(this.getInterval());
    }

    // //////////////////////////////////////


    public BudgetKeyTable deleteBudgetKeyItems(@ParameterLayout(named = "Are you sure?") final boolean confirmDelete) {

        for (BudgetKeyItem budgetKeyItem : getBudgetKeyItems()){
            removeIfNotAlready(budgetKeyItem);
        }

        return this;
    }

    public String validateDeleteBudgetKeyItems(final boolean confirmDelete) {
        return confirmDelete? null:"Please confirm";
    }


    // //////////////////////////////////////

    @Inject
    UnitMenu unitMenu;

    @Inject
    Occupancies occupancies;

}
