package org.estatio.fixture.budget.spreadsheets;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.estatio.dom.Importable;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budget.BudgetRepository;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.budgeting.budgetitem.BudgetItemRepository;
import org.estatio.dom.budgeting.keytable.KeyTable;
import org.estatio.dom.budgeting.keytable.KeyTableRepository;
import org.estatio.dom.budgeting.allocation.BudgetItemAllocation;
import org.estatio.dom.budgeting.allocation.BudgetItemAllocationRepository;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.Charges;
import org.isisaddons.module.excel.dom.ExcelFixture;
import org.isisaddons.module.excel.dom.ExcelFixtureRowHandler;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;
import org.joda.time.LocalDate;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@DomainObject(nature = Nature.VIEW_MODEL)
public class BudgetImport implements ExcelFixtureRowHandler, Importable {

    private static int numberOfRecords = 0;
    private static int numberOfBudgetItemsCreated = 0;
    private static int[] counter = new int[10];

    private String propertyReference;
    private LocalDate startDate;
    private LocalDate endDate;
    private String chargeReference;
    private String targetChargeReference;
    private BigDecimal budgetedValue;
    private BigDecimal keytableAPercentage;
    private BigDecimal keytableBPercentage;
    private BigDecimal keytableCPercentage;
    private BigDecimal keytableDPercentage;
    private BigDecimal keytableEPercentage;
    private BigDecimal keytableFPercentage;
    private BigDecimal keytableGPercentage;
    private BigDecimal keytableHPercentage;
    private BigDecimal keytableIPercentage;
    private BigDecimal keytableJPercentage;

    @Override
    public List<Object> handleRow(FixtureScript.ExecutionContext executionContext, ExcelFixture excelFixture, Object o) {
        return importData();
    }

    private BudgetItem findOrCreateBudgetItem(
            final Property property,
            final LocalDate startDate,
            final Budget budget,
            final BigDecimal budgetedValue,
            final Charge charge){
        BudgetItem budgetItem = budgetItemRepository.findByPropertyAndChargeAndStartDate(property, charge, startDate);
        if (budgetItem==null) {
            budgetItem = budgetItemRepository.newBudgetItem(budget, budgetedValue, charge);
            numberOfBudgetItemsCreated ++;
        }
        return budgetItem;
    }

    private BudgetItemAllocation findOrCreateBudgetItemAllocation(
            final Charge charge,
            final KeyTable keyTable,
            final BudgetItem budgetItem,
            final BigDecimal percentage){

        BudgetItemAllocation budgetItemAllocation = budgetItemAllocationRepository.findByChargeAndBudgetItemAndKeyTable(charge, budgetItem, keyTable);
        if (budgetItemAllocation == null) {
            budgetItemAllocation = budgetItemAllocationRepository.newBudgetItemAllocation(charge, keyTable, budgetItem, percentage);
        }
        return budgetItemAllocation;
    }

    @Override
    @Action(invokeOn= InvokeOn.OBJECT_AND_COLLECTION)
    public List<Object> importData() {

        final Property property = propertyRepository.findPropertyByReference(getPropertyReference());

        //find or create budget
        Budget budget = budgetRepository.findOrCreateBudget(property, startDate, endDate);

        //Hack to be able to import existing charge for Italy
        String targetChargeRef = getTargetChargeReference().replace("ITA","");

        final Charge targetCharge = charges.findByReference(targetChargeRef);
        final LocalDate startDate = getStartDate();
        final LocalDate endDate = getEndDate();

        List<BigDecimal> percentages = new ArrayList<>();
        percentages.add(getKeytableAPercentage());
        percentages.add(getKeytableBPercentage());
        percentages.add(getKeytableCPercentage());
        percentages.add(getKeytableDPercentage());
        percentages.add(getKeytableEPercentage());
        percentages.add(getKeytableFPercentage());
        percentages.add(getKeytableGPercentage());
        percentages.add(getKeytableHPercentage());
        percentages.add(getKeytableIPercentage());
        percentages.add(getKeytableJPercentage());

        String[] names = {
                "KeyTable A",
                "KeyTable B",
                "KeyTable C",
                "KeyTable D",
                "KeyTable E",
                "KeyTable F",
                "KeyTable G",
                "KeyTable H",
                "KeyTable I",
                "KeyTable J"
        };

        List<KeyTable> tables = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            KeyTable keyTable = keyTableRepository.findByBudgetAndName(budget, names[i]);
            tables.add(keyTable);
        }

        numberOfRecords ++;

        try {

            Charge sourceCharge = charges.findByReference(getChargeReference());

            //find or create budget item
            BudgetItem budgetItem = findOrCreateBudgetItem(property, startDate, budget, getBudgetedValue(), sourceCharge);

            //find or create schedule items
            for (int i = 0; i < 10; i++) {
                if (percentages.get(i) != null) {
                    //find or create schedule item
                    findOrCreateBudgetItemAllocation(targetCharge, tables.get(i), budgetItem, percentages.get(i));
                    counter[i]++;
                }
            }

            // console feedback
            System.out.println("Number of records read: " + numberOfRecords);
            System.out.println("Number of budgetItems created: " + numberOfBudgetItemsCreated);
            for (int i = 0; i<10; i++){

                System.out.println("Number of scheduleItems for " + tables.get(i).getName() + " : " + counter[i]);

            }

        } catch (Exception e) {
            // REVIEW: ignore any garbage
            System.out.println("ERROR OR GARBAGE");
        }

        return Lists.newArrayList();
    }

    @MemberOrder(sequence = "1")
    public String getPropertyReference() {
        return propertyReference;
    }

    public void setPropertyReference(String propertyReference) {
        this.propertyReference = propertyReference;
    }

    @MemberOrder(sequence = "2")
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    @MemberOrder(sequence = "3")
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @MemberOrder(sequence = "4")
    public String getChargeReference() {
        return chargeReference;
    }

    public void setChargeReference(String chargeReference) {
        this.chargeReference = chargeReference;
    }

    @MemberOrder(sequence = "5")
    public String getTargetChargeReference() {
        return targetChargeReference;
    }

    public void setTargetChargeReference(String targetChargeReference) {
        this.targetChargeReference = targetChargeReference;
    }

    @MemberOrder(sequence = "6")
    public BigDecimal getBudgetedValue() {
        return budgetedValue;
    }

    public void setBudgetedValue(BigDecimal budgetedValue) {
        this.budgetedValue = budgetedValue;
    }

    @MemberOrder(sequence = "7")
    public BigDecimal getKeytableAPercentage() {
        return keytableAPercentage;
    }

    public void setKeytableAPercentage(BigDecimal keytableAPercentage) {
        this.keytableAPercentage = keytableAPercentage;
    }

    @MemberOrder(sequence = "8")
    public BigDecimal getKeytableBPercentage() {
        return keytableBPercentage;
    }

    public void setKeytableBPercentage(BigDecimal keytableBPercentage) {
        this.keytableBPercentage = keytableBPercentage;
    }

    @MemberOrder(sequence = "9")
    public BigDecimal getKeytableCPercentage() {
        return keytableCPercentage;
    }

    public void setKeytableCPercentage(BigDecimal keytableCPercentage) {
        this.keytableCPercentage = keytableCPercentage;
    }

    @MemberOrder(sequence = "10")
    public BigDecimal getKeytableDPercentage() {
        return keytableDPercentage;
    }

    public void setKeytableDPercentage(BigDecimal keytableDPercentage) {
        this.keytableDPercentage = keytableDPercentage;
    }

    @MemberOrder(sequence = "11")
    public BigDecimal getKeytableEPercentage() {
        return keytableEPercentage;
    }

    public void setKeytableEPercentage(BigDecimal keytableEPercentage) {
        this.keytableEPercentage = keytableEPercentage;
    }

    @MemberOrder(sequence = "12")
    public BigDecimal getKeytableFPercentage() {
        return keytableFPercentage;
    }

    public void setKeytableFPercentage(BigDecimal keytableFPercentage) {
        this.keytableFPercentage = keytableFPercentage;
    }

    @MemberOrder(sequence = "13")
    public BigDecimal getKeytableGPercentage() {
        return keytableGPercentage;
    }

    public void setKeytableGPercentage(BigDecimal keytableGPercentage) {
        this.keytableGPercentage = keytableGPercentage;
    }

    @MemberOrder(sequence = "14")
    public BigDecimal getKeytableHPercentage() {
        return keytableHPercentage;
    }

    public void setKeytableHPercentage(BigDecimal keytableHPercentage) {
        this.keytableHPercentage = keytableHPercentage;
    }

    @MemberOrder(sequence = "15")
    public BigDecimal getKeytableIPercentage() {
        return keytableIPercentage;
    }

    public void setKeytableIPercentage(BigDecimal keytableIPercentage) {
        this.keytableIPercentage = keytableIPercentage;
    }

    @MemberOrder(sequence = "16")
    public BigDecimal getKeytableJPercentage() {
        return keytableJPercentage;
    }

    public void setKeytableJPercentage(BigDecimal keytableJPercentage) {
        this.keytableJPercentage = keytableJPercentage;
    }

    private static String pretty(final String str) {
        return str == null? null : StringUtils.capitalize(str.toLowerCase());
    }

    @Inject
    DomainObjectContainer container;

    @Inject
    private Charges charges;

    @Inject
    private BudgetRepository budgetRepository;

    @Inject
    private BudgetItemRepository budgetItemRepository;

    @Inject
    private BudgetItemAllocationRepository budgetItemAllocationRepository;

    @Inject
    private PropertyRepository propertyRepository;

    @Inject
    private KeyTableRepository keyTableRepository;

    @Inject
    private ApplicationTenancyRepository applicationTenancyRepository;
}
