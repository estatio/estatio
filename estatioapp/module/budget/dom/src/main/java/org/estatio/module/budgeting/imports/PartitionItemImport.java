package org.estatio.module.budgeting.imports;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.excel.dom.ExcelFixture;
import org.isisaddons.module.excel.dom.ExcelFixtureRowHandler;

import org.estatio.dom.Importable;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.asset.Unit;
import org.estatio.dom.asset.UnitRepository;
import org.estatio.module.budgeting.dom.partioning.PartitionItemRepository;
import org.estatio.module.budgeting.dom.partioning.PartitionItem;
import org.estatio.module.budgeting.dom.budget.Budget;
import org.estatio.module.budgeting.dom.budget.BudgetRepository;
import org.estatio.module.budgeting.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.module.budgeting.dom.budgetitem.BudgetItem;
import org.estatio.module.budgeting.dom.budgetitem.BudgetItemRepository;
import org.estatio.module.budgeting.dom.keyitem.KeyItem;
import org.estatio.module.budgeting.dom.keyitem.KeyItemRepository;
import org.estatio.module.budgeting.dom.keytable.FoundationValueType;
import org.estatio.module.budgeting.dom.keytable.KeyTable;
import org.estatio.module.budgeting.dom.keytable.KeyTableRepository;
import org.estatio.module.budgeting.dom.keytable.KeyValueMethod;
import org.estatio.module.budgeting.dom.partioning.Partitioning;
import org.estatio.module.budgeting.dom.partioning.PartitioningRepository;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.dom.viewmodels.PartitionItemImport"
)
public class PartitionItemImport implements ExcelFixtureRowHandler, Importable {

    @Getter @Setter
    private String propertyReference;

    @Getter @Setter
    private LocalDate startDate;

    @Getter @Setter
    private LocalDate endDate;

    @Getter @Setter
    private String keyTableName;

    @Getter @Setter
    private BigDecimal sourceValue;

    @Getter @Setter
    private BigDecimal keyValue;

    @Getter @Setter
    private String unitReference;

    @Getter @Setter
    private String budgetChargeReference;

    @Getter @Setter
    private BigDecimal budgetValue;

    @Getter @Setter
    private String invoiceChargeReference;

    @Getter @Setter
    private BigDecimal percentage;

//    @Override
//    public List<Class> importAfter() {
//        return Lists.newArrayList(PropertyImport.class);
//    }

    @Programmatic
    @Override
    public List<Object> handleRow(FixtureScript.ExecutionContext executionContext, ExcelFixture excelFixture, Object previousRow) {
        return importData(previousRow);
    }

    // REVIEW: other import view models have @Action annotation here...  but in any case, is this view model actually ever surfaced in the UI?
    public List<Object> importData() {
        return importData(null);
    }

    @Programmatic
    @Override
    public List<Object> importData(final Object previousRow) {

        Property property = propertyRepository.findPropertyByReference(propertyReference);
        if (property == null)
            throw new ApplicationException(String.format("Property with reference [%s] not found.", propertyReference));
        final Budget budget = budgetRepository.findOrCreateBudget(property, startDate, endDate);
        final KeyTable keyTable = keyTableRepository.findOrCreateBudgetKeyTable(budget, keyTableName, FoundationValueType.MANUAL, KeyValueMethod.PERCENT, 6);
        findOrCreateBudgetKeyItem(keyTable, unitRepository.findUnitByReference(unitReference), keyValue, sourceValue);

        final Charge charge = fetchCharge(budgetChargeReference);
        final BudgetItem butgetItem = findOrCreateBudgetItem(budget, charge, budgetValue);
        final Charge scheduleCharge = fetchCharge(invoiceChargeReference);

        final Partitioning partitioning = findOrCreatePartitioning(budget);
        findOrCreatePartitionItem(partitioning, scheduleCharge, butgetItem, keyTable, percentage);

        return Lists.newArrayList();
    }

    private Charge fetchCharge(final String chargeReference) {
        final Charge charge = chargeRepository
                .findByReference(chargeReference);
        if (charge == null) {
            throw new ApplicationException(String.format("Charge with reference %s not found.", chargeReference));
        }
        return charge;
    }

    private Partitioning findOrCreatePartitioning(final Budget budget){
        final Partitioning partitioning = partitioningRepository.findUnique(budget, BudgetCalculationType.BUDGETED, budget.getStartDate());
        if (partitioning == null) {
            return partitioningRepository.newPartitioning(budget, budget.getStartDate(), budget.getEndDate(), BudgetCalculationType.BUDGETED);
        }
        return partitioning;
    }

    private PartitionItem findOrCreatePartitionItem(final Partitioning partitioning, final Charge charge, final BudgetItem butgetItem, final KeyTable keyTable, final BigDecimal percentage) {
        final PartitionItem partitionItem = partitionItemRepository.findUnique(partitioning, charge, butgetItem, keyTable);
        if (partitionItem == null) {
            return partitionItemRepository.newPartitionItem(partitioning, charge, keyTable, butgetItem, percentage);
        }
        return partitionItem;
    }

    private BudgetItem findOrCreateBudgetItem(final Budget budget, final Charge charge, final BigDecimal value) {
        BudgetItem budgetItem = budgetItemRepository.findByBudgetAndCharge(budget, charge);
        if (budgetItem == null) {
            budgetItem = budgetItemRepository.newBudgetItem(budget, charge);
            budgetItem.newValue(value, budget.getStartDate(), BudgetCalculationType.BUDGETED);
            return budgetItem;
        }
        return budgetItem;
    }

    private KeyItem findOrCreateBudgetKeyItem(KeyTable keyTable, Unit unit, BigDecimal keyValue, BigDecimal sourceValue) {
        final KeyItem item = budgetKeyItemRepository.findByKeyTableAndUnit(keyTable, unit);
        if (item == null) {
            return budgetKeyItemRepository.newItem(keyTable, unit, sourceValue, keyValue);
        }
        return item;
    }

    //region > injected services

    @Inject
    private PropertyRepository propertyRepository;

    @Inject
    private UnitRepository unitRepository;

    @Inject
    private ChargeRepository chargeRepository;

    @Inject
    private BudgetRepository budgetRepository;

    @Inject
    private BudgetItemRepository budgetItemRepository;

    @Inject
    private KeyTableRepository keyTableRepository;

    @Inject
    private KeyItemRepository budgetKeyItemRepository;

    @Inject
    private PartitioningRepository partitioningRepository;

    @Inject
    private PartitionItemRepository partitionItemRepository;

    //endregion
}
