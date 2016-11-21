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
package org.estatio.dom.budgeting.budget;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.utils.TitleBuilder;
import org.incode.module.base.dom.valuetypes.LocalDateInterval;
import org.incode.module.base.dom.with.WithIntervalMutable;

import org.estatio.dom.UdoDomainObject2;
import org.estatio.dom.apptenancy.WithApplicationTenancyProperty;
import org.estatio.dom.asset.Property;
import org.estatio.dom.budgeting.api.BudgetItemCreator;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationRepository;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationService;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationType;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.budgeting.budgetitem.BudgetItemRepository;
import org.estatio.dom.budgeting.keytable.FoundationValueType;
import org.estatio.dom.budgeting.keytable.KeyTable;
import org.estatio.dom.budgeting.keytable.KeyTableRepository;
import org.estatio.dom.budgeting.keytable.KeyValueMethod;
import org.estatio.dom.budgeting.partioning.PartitionItem;
import org.estatio.dom.budgeting.partioning.PartitionItemRepository;
import org.estatio.dom.budgeting.partioning.Partitioning;
import org.estatio.dom.budgeting.partioning.PartitioningRepository;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.lease.OccupancyRepository;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE
        ,schema = "dbo" // Isis' ObjectSpecId inferred from @DomainObject#objectType
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Queries({
        @Query(
                name = "findByProperty", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.budgeting.budget.Budget " +
                        "WHERE property == :property " +
                        "ORDER BY startDate DESC"),
        @Query(
                name = "findByPropertyAndStartDate", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.budgeting.budget.Budget " +
                        "WHERE property == :property && startDate == :startDate")
})
@Unique(name = "Budget_property_startDate_UNQ", members = { "property", "startDate" })
@DomainObject(
        objectType = "org.estatio.dom.budgeting.budget.Budget"
)
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_ROOT)
public class Budget extends UdoDomainObject2<Budget>
        implements WithIntervalMutable<Budget>, WithApplicationTenancyProperty, BudgetItemCreator {

    public Budget() {
        super("property, startDate");
    }

    public String title() {
        return TitleBuilder.start()
                .withParent(getProperty())
                .withName(getBudgetYear())
                .toString();
    }

    @Column(name = "propertyId", allowsNull = "false")
    @PropertyLayout(hidden = Where.PARENTED_TABLES)
    @Getter @Setter
    private Property property;

    @Column(allowsNull = "true") // done because of inherited implementation WithStartDate
    @Getter @Setter
    @PropertyLayout(hidden = Where.EVERYWHERE)
    private LocalDate startDate;

    @Column(allowsNull = "true")
    @Getter @Setter
    @PropertyLayout(hidden = Where.EVERYWHERE)
    private LocalDate endDate;

    @Persistent(mappedBy = "budget", dependentElement = "true")
    @Getter @Setter
    private SortedSet<BudgetItem> items = new TreeSet<>();

    @Persistent(mappedBy = "budget", dependentElement = "true")
    @Getter @Setter
    private SortedSet<Partitioning> partitionings = new TreeSet<>();

    @Persistent(mappedBy = "budget", dependentElement = "true")
    @Getter @Setter
    private SortedSet<KeyTable> keyTables = new TreeSet<>();

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    @MemberOrder(name = "items", sequence = "1")
    public BudgetItem newBudgetItem(
            final BigDecimal budgetedValue,
            final Charge charge) {
        return budgetItemRepository.newBudgetItem(this, budgetedValue, charge);
    }

    public String validateNewBudgetItem(
            final BigDecimal budgetedValue,
            final Charge charge) {
        return budgetItemRepository.validateNewBudgetItem(this, charge);
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    @MemberOrder(name = "partitionings", sequence = "1")
    public Budget newPartitioning(){
        partitioningRepository.newPartitioning(this, getStartDate(), getEndDate(), BudgetCalculationType.ACTUAL);
        return this;
    }

    public String validateNewPartitioning(){
        return partitioningRepository.validateNewPartitioning(this, getStartDate(), getEndDate(), BudgetCalculationType.ACTUAL);
    }

    public String disableNewPartitioning(){
        return partitioningRepository.findByBudgetAndType(this, BudgetCalculationType.ACTUAL).size()>0 ? "Partitioning for reconciliation already exists" : null;
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public KeyTable createKeyTable(
            final String name,
            final FoundationValueType foundationValueType,
            final KeyValueMethod keyValueMethod) {
        return keyTableRepository.newKeyTable(this, name, foundationValueType, keyValueMethod, 6);
    }

    public String validateCreateKeyTable(
            final String name,
            final FoundationValueType foundationValueType,
            final KeyValueMethod keyValueMethod) {
        return keyTableRepository.validateNewKeyTable(this, name, foundationValueType, keyValueMethod, 6);
    }

    public Budget createNextBudget() {
        LocalDate start = new LocalDate(getBudgetYear()+1, 01, 01);
        LocalDate end = new LocalDate(getBudgetYear()+1, 12, 31);
        Budget newBudget = budgetRepository.newBudget(getProperty(),start, end);
        newBudget.findOrCreatePartitioningForBudgeting();
        return copyCurrentTo(newBudget);
    }

    public String validateCreateNextBudget() {
        if (budgetRepository.findByPropertyAndStartDate(getProperty(), new LocalDate(getBudgetYear()+1, 01, 01)) != null){
            return "This budget already exists";
        }
        return null;
    }

    private Budget copyCurrentTo(final Budget newBudget) {
        for (KeyTable keyTable : getKeyTables()){
            keyTable.createCopyOn(newBudget);
        }
        for (BudgetItem item : getItems()){
            item.createCopyOn(newBudget);
        }
        return newBudget;
    }

    @Programmatic
    public List<Charge> getInvoiceCharges() {
        List<Charge> charges = new ArrayList<>();
        for (BudgetItem budgetItem : getItems()) {
            for (PartitionItem partitionItem : budgetItem.getPartitionItems()) {
                if (!charges.contains(partitionItem.getCharge())) {
                    charges.add(partitionItem.getCharge());
                }
            }
        }
        return charges;
    }

    @Action(semantics = SemanticsOf.SAFE)
    public int getBudgetYear() {
        return getStartDate().getYear();
    }

    @Programmatic
    public LocalDateInterval getBudgetYearInterval() {
        return new LocalDateInterval(new LocalDate(getBudgetYear(),01,01), new LocalDate(new LocalDate(getBudgetYear(),12,31)));
    }

    @Programmatic
    public LocalDateInterval getInterval() {
        return LocalDateInterval.including(getStartDate(), getEndDate());
    }

    @Programmatic
    public LocalDateInterval getEffectiveInterval() {
        return getInterval();
    }

    public boolean isCurrent() {
        return isActiveOn(getClockService().now());
    }

    private boolean isActiveOn(final LocalDate date) {
        return LocalDateInterval.including(this.getStartDate(), this.getEndDate()).contains(date);
    }

    private WithIntervalMutable.Helper<Budget> changeDates = new WithIntervalMutable.Helper<>(this);

    WithIntervalMutable.Helper<Budget> getChangeDates() {
        return changeDates;
    }

    @Override
    @Action(semantics = SemanticsOf.IDEMPOTENT, hidden = Where.EVERYWHERE)
    public Budget changeDates(
            final LocalDate startDate,
            final @Parameter(optionality = Optionality.OPTIONAL) LocalDate endDate) {
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

        return "Dates should not be changed.";
    }

    @PropertyLayout(hidden = Where.EVERYWHERE)
    @Override public ApplicationTenancy getApplicationTenancy() {
        return getProperty().getApplicationTenancy();
    }

    @Programmatic
    public Partitioning getPartitioningForBudgeting(){
        return partitioningRepository.findUnique(this, BudgetCalculationType.BUDGETED, getStartDate());
    }

    @Programmatic
    public void remove(){
        remove(this);
    }

    @Action(restrictTo = RestrictTo.PROTOTYPING, semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    @ActionLayout()
    public Budget removeAllBudgetItems() {
        for (BudgetItem budgetItem : this.getItems()) {
            for (PartitionItem pItem : budgetItem.getPartitionItems()){
                pItem.remove();
            }
            getContainer().remove(budgetItem);
            getContainer().flush();
        }

        return this;
    }

    @Programmatic
    public Budget findOrCreatePartitioningForBudgeting(){
        Partitioning partitioningForBudgeting = getPartitioningForBudgeting();
        if (partitioningForBudgeting==null){
            partitioningRepository.newPartitioning(this, getStartDate(), getEndDate(), BudgetCalculationType.BUDGETED);
        }
        return this;
    }

    @Override
    @Programmatic
    public BudgetItem findOrCreateBudgetItem(
            final Charge budgetItemCharge) {
        return budgetItemRepository.findOrCreateBudgetItem(this, budgetItemCharge);
    }

    @Inject
    private PartitioningRepository partitioningRepository;

    @Inject
    private PartitionItemRepository partitionItemRepository;

    @Inject
    private BudgetItemRepository budgetItemRepository;

    @Inject
    private BudgetRepository budgetRepository;

    @Inject
    private OccupancyRepository occupancyRepository;

    @Inject
    private BudgetCalculationRepository budgetCalculationRepository;

    @Inject
    private KeyTableRepository keyTableRepository;

    @Inject
    private BudgetCalculationService budgetCalculationService;

}
