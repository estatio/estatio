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
package org.estatio.module.budget.dom.budget;

import java.math.BigDecimal;
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
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

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
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.schema.utils.jaxbadapters.PersistentEntityAdapter;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.utils.TitleBuilder;
import org.incode.module.base.dom.valuetypes.LocalDateInterval;
import org.incode.module.base.dom.with.WithIntervalMutable;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.base.dom.UdoDomainObject2;
import org.estatio.module.base.dom.apptenancy.WithApplicationTenancyProperty;
import org.estatio.module.budget.dom.api.BudgetItemCreator;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculation;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationRepository;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationService;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.module.budget.dom.budgetitem.BudgetItem;
import org.estatio.module.budget.dom.budgetitem.BudgetItemRepository;
import org.estatio.module.budget.dom.keytable.DirectCostTable;
import org.estatio.module.budget.dom.keytable.DirectCostTableRepository;
import org.estatio.module.budget.dom.keytable.FoundationValueType;
import org.estatio.module.budget.dom.keytable.KeyTable;
import org.estatio.module.budget.dom.keytable.KeyTableRepository;
import org.estatio.module.budget.dom.keytable.KeyValueMethod;
import org.estatio.module.budget.dom.partioning.PartitionItem;
import org.estatio.module.budget.dom.partioning.PartitionItemRepository;
import org.estatio.module.budget.dom.partioning.Partitioning;
import org.estatio.module.budget.dom.partioning.PartitioningRepository;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;

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
                        "FROM org.estatio.module.budget.dom.budget.Budget " +
                        "WHERE property == :property " +
                        "ORDER BY startDate DESC"),
        @Query(
                name = "findByPropertyAndStartDate", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.module.budget.dom.budget.Budget " +
                        "WHERE property == :property && startDate == :startDate")
})
@Unique(name = "Budget_property_startDate_UNQ", members = { "property", "startDate" })
@DomainObject(
        objectType = "org.estatio.dom.budgeting.budget.Budget"
)
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_ROOT)
@XmlJavaTypeAdapter(PersistentEntityAdapter.class)
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

    @Programmatic
    public BudgetItem findByCharge(final Charge charge) {
        return budgetItemRepository.findByBudgetAndCharge(this, charge);
    }

    @Persistent(mappedBy = "budget", dependentElement = "true")
    @Getter @Setter
    private SortedSet<Partitioning> partitionings = new TreeSet<>();

    @Persistent(mappedBy = "budget", dependentElement = "true")
    @Getter @Setter
    private SortedSet<KeyTable> keyTables = new TreeSet<>();

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public Status getStatus(){
        if (!budgetCalculationRepository.findByBudgetAndTypeAndStatus(this, BudgetCalculationType.ACTUAL, org.estatio.module.budget.dom.budgetcalculation.Status.ASSIGNED).isEmpty()) return Status.RECONCILED;
        if (!budgetCalculationRepository.findByBudgetAndTypeAndStatus(this, BudgetCalculationType.BUDGETED, org.estatio.module.budget.dom.budgetcalculation.Status.ASSIGNED).isEmpty()) return Status.ASSIGNED;
        return Status.NEW;
    }

    @Persistent(mappedBy = "budget", dependentElement = "true")
    @Getter @Setter
    private SortedSet<DirectCostTable> directCostTables = new TreeSet<>();

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    @MemberOrder(name = "items", sequence = "1")
    public BudgetItem newBudgetItem(
            final BigDecimal budgetedValue,
            final Charge charge) {
        return budgetItemRepository.newBudgetItem(this, budgetedValue, charge);
    }

    public List<Charge> choices1NewBudgetItem() {
        return chargeRepository.allIncoming();
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

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public DirectCostTable createDirectCostTable(
            final String name) {
        return directCostTableRepository.newDirectCostTable(this, name);
    }

    public String validateCreateDirectCostTable(
            final String name) {
        return directCostTableRepository.validateNewKeyTable(this, name);
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
            keyTable.createCopyFor(newBudget);
        }
        for (DirectCostTable directCostTable : getDirectCostTables()){
            directCostTable.createCopyFor(newBudget);
        }
        for (BudgetItem item : getItems()){
            item.createCopyFor(newBudget);
        }
        return newBudget;
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
        return getInterval().contains(date);
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

    @Programmatic
    public Budget removeAllBudgetItems() {
        for (BudgetItem budgetItem : this.getItems()) {
            for (PartitionItem pItem : budgetItem.getPartitionItems()){
                pItem.remove();
            }
            repositoryService.removeAndFlush(budgetItem);
        }

        return this;
    }

    @Programmatic
    public Budget removeNewCalculations(){
        for (BudgetCalculation calculation : budgetCalculationRepository.findByBudget(this)) {
            calculation.removeWithStatusNew();
        }
        return this;
    }

    @Programmatic
    public Budget removeAllPartitioningTables(){
        for (KeyTable keyTable : getKeyTables()){
            keyTable.deleteItems();
            keyTable.remove();
        }
        for (DirectCostTable directCostTable : getDirectCostTables()){
            directCostTable.deleteItems();
            directCostTable.remove();
        }
        return this;
    }

    @Programmatic
    public String noUnassignedItemsForTypeReason(final BudgetCalculationType type){
        for (BudgetItem item : getItems()){
            if (!item.isAssignedForType(type)){
                return null;
            }
        }
        return type==BudgetCalculationType.BUDGETED ? "All items are calculated and assigned already" : "All items are reconciled and assigned already";
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
    BudgetCalculationRepository budgetCalculationRepository;

    @Inject
    private KeyTableRepository keyTableRepository;

    @Inject
    private BudgetCalculationService budgetCalculationService;

    @Inject
    private ChargeRepository chargeRepository;

    @Inject
    private DirectCostTableRepository directCostTableRepository;

    @Inject
    private RepositoryService repositoryService;

}
