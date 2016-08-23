package org.estatio.fixture.budget.spreadsheets;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.InvokeOn;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.excel.dom.ExcelFixture;
import org.isisaddons.module.excel.dom.ExcelFixtureRowHandler;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.estatio.dom.Importable;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.budgeting.allocation.BudgetItemAllocation;
import org.estatio.dom.budgeting.allocation.BudgetItemAllocationRepository;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budget.BudgetRepository;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.budgeting.budgetitem.BudgetItemRepository;
import org.estatio.dom.budgeting.keytable.KeyTable;
import org.estatio.dom.budgeting.keytable.KeyTableRepository;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.ChargeRepository;

import lombok.Getter;
import lombok.Setter;

@DomainObject(nature = Nature.VIEW_MODEL)
public class BudgetImport implements ExcelFixtureRowHandler, Importable {

    private static int numberOfRecords = 0;
    private static int numberOfBudgetItemsCreated = 0;
    private static int[] counter = new int[10];

    @Getter @Setter
    private String propertyReference;

    @Getter @Setter
    private LocalDate startDate;

    @Getter @Setter
    private LocalDate endDate;

    @Getter @Setter
    private String chargeReference;

    @Getter @Setter
    private String targetChargeReference;

    @Getter @Setter
    private BigDecimal budgetedValue;

    @Getter @Setter
    private BigDecimal keytableAPercentage;

    @Getter @Setter
    private BigDecimal keytableBPercentage;

    @Getter @Setter
    private BigDecimal keytableCPercentage;

    @Getter @Setter
    private BigDecimal keytableDPercentage;

    @Getter @Setter
    private BigDecimal keytableEPercentage;

    @Getter @Setter
    private BigDecimal keytableFPercentage;

    @Getter @Setter
    private BigDecimal keytableGPercentage;

    @Getter @Setter
    private BigDecimal keytableHPercentage;

    @Getter @Setter
    private BigDecimal keytableIPercentage;

    @Getter @Setter
    private BigDecimal keytableJPercentage;


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


    @Programmatic
    @Override
    public List<Object> handleRow(FixtureScript.ExecutionContext executionContext, ExcelFixture excelFixture, Object previousRow) {
        return importData(previousRow);
    }

    // REVIEW: is this view model actually ever surfaced in the UI?
    @Action(invokeOn= InvokeOn.OBJECT_AND_COLLECTION)
    public List<Object> importData() {
        return importData(null);
    }

    @Programmatic
    @Override
    public List<Object> importData(final Object previousRow) {

        final Property property = propertyRepository.findPropertyByReference(getPropertyReference());

        //find or create budget
        Budget budget = budgetRepository.findOrCreateBudget(property, startDate, endDate);

        //Hack to be able to import existing charge for Italy
        String targetChargeRef = getTargetChargeReference().replace("ITA","");

        final Charge targetCharge = chargeRepository.findByReference(targetChargeRef);
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

            Charge sourceCharge = chargeRepository.findByReference(getChargeReference());

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



    @Inject
    DomainObjectContainer container;

    @Inject
    private ChargeRepository chargeRepository;

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
