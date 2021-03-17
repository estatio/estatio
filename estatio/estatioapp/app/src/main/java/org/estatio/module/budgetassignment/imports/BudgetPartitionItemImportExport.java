package org.estatio.module.budgetassignment.imports;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.isisaddons.module.excel.dom.ExcelFixture2;
import org.isisaddons.module.excel.dom.FixtureAwareRowHandler;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.base.dom.Importable;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budget.BudgetRepository;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.module.budget.dom.budgetitem.BudgetItem;
import org.estatio.module.budget.dom.keytable.DirectCostTable;
import org.estatio.module.budget.dom.keytable.DirectCostTableRepository;
import org.estatio.module.budget.dom.keytable.FoundationValueType;
import org.estatio.module.budget.dom.keytable.KeyTable;
import org.estatio.module.budget.dom.keytable.KeyTableRepository;
import org.estatio.module.budget.dom.keytable.KeyValueMethod;
import org.estatio.module.budget.dom.partioning.PartitionItem;
import org.estatio.module.budgetassignment.dom.BudgetService;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.app.services.budget.BudgetImportExport"
)
public class BudgetPartitionItemImportExport
        implements Importable, FixtureAwareRowHandler<BudgetPartitionItemImportExport> {

    public String title() {
        return "Budget Import / Export";
    }

    public BudgetPartitionItemImportExport(){
    }

    public BudgetPartitionItemImportExport(
            final String propertyReference,
            final LocalDate budgetStartDate,
            final LocalDate budgetEndDate,
            final String incomingChargeReference,
            final String partitioningTableName,
            final String foundationValueType,
            final String keyValueMethod,
            final String outgoingChargeReference,
            final BigDecimal percentage,
            final BigDecimal fixedBudgetedAmount,
            final BigDecimal fixedAuditedAmount,
            final String tableType,
            final BudgetCalculationType budgetCalculationType,
            final String calculationDescription){
        this.propertyReference = propertyReference;
        this.budgetStartDate = budgetStartDate;
        this.budgetEndDate = budgetEndDate;
        this.incomingChargeReference = incomingChargeReference;
        this.partitioningTableName = partitioningTableName;
        this.foundationValueType = foundationValueType;
        this.keyValueMethod = keyValueMethod;
        this.outgoingChargeReference = outgoingChargeReference;
        this.percentage = percentage;
        this.fixedBudgetedAmount = fixedBudgetedAmount;
        this.fixedAuditedAmount = fixedAuditedAmount;
        this.tableType = tableType;
        this.budgetCalculationType = budgetCalculationType.name();
        this.calculationDescription = calculationDescription;
    }
    @Getter @Setter
    @MemberOrder(sequence = "1")
    private String budgetCalculationType;
    @Getter @Setter
    @MemberOrder(sequence = "2")
    private String incomingChargeReference;
    @Getter @Setter
    @MemberOrder(sequence = "3")
    private String calculationDescription;
    @Getter @Setter
    @MemberOrder(sequence = "4")
    private BigDecimal fixedBudgetedAmount;
    @Getter @Setter
    @MemberOrder(sequence = "5")
    private BigDecimal fixedAuditedAmount;
    @Getter @Setter
    @MemberOrder(sequence = "6")
    private BigDecimal percentage;
    @Getter @Setter
    @MemberOrder(sequence = "7")
    private String partitioningTableName;
    @Getter @Setter
    @MemberOrder(sequence = "8")
    private String foundationValueType;
    @Getter @Setter
    @MemberOrder(sequence = "9")
    private String tableType;
    @Getter @Setter
    @MemberOrder(sequence = "10")
    private String outgoingChargeReference;
    @Getter @Setter
    @MemberOrder(sequence = "11")
    private String propertyReference;
    @Getter @Setter
    @MemberOrder(sequence = "12")
    private LocalDate budgetStartDate;
    @Getter @Setter
    @MemberOrder(sequence = "13")
    private LocalDate budgetEndDate;
    @Getter @Setter
    @MemberOrder(sequence = "14")
    private String keyValueMethod;

    @Override
    @Programmatic
    public List<Object> importData(final Object previousRow) {
        Property property = propertyRepository.findPropertyByReference(getPropertyReference());
        if (property == null) throw  new ApplicationException(String.format("Property with reference [%s] not found.", getPropertyReference()));
        Budget budget = budgetRepository.findByPropertyAndDate(property, getBudgetStartDate());
        if (budget == null) throw  new ApplicationException(String.format("Budget for property %s and date %s not found.", getPropertyReference(), getBudgetStartDate()));
        if (previousRow==null){
            budgetService.removeExistingPartitionItemsIfCanBeRemoved(budget);
        }
        Charge incomingCharge = fetchCharge(getIncomingChargeReference());
        BudgetItem budgetItem = fetchBudgetItemForCharge(budget, incomingCharge);
        budget.findOrCreatePartitioningForBudgeting();
        if (getOutgoingChargeReference()!=null && getPartitioningTableName()!=null) {
            final PartitioningTableType partitioningTableType = PartitioningTableType.valueOf(getTableType());
            if (partitioningTableType == PartitioningTableType.DIRECT_COST_TABLE || (partitioningTableType == PartitioningTableType.KEY_TABLE && getKeyValueMethod()!=null && getFoundationValueType()!=null)) {
                findOrCreatePartitionItem(budgetItem, partitioningTableType);
            }
        }
        return Lists.newArrayList(budgetItem.getBudget());
    }

    private PartitionItem findOrCreatePartitionItem(final BudgetItem budgetItem, final PartitioningTableType partitioningTableType) {
        Charge targetCharge = fetchCharge(getOutgoingChargeReference());
        if (partitioningTableType == PartitioningTableType.KEY_TABLE) {
            KeyTable keyTable = findOrCreateKeyTable(budgetItem.getBudget(), getPartitioningTableName(), getFoundationValueType(), getKeyValueMethod());
            return budgetItem.updateOrCreatePartitionItem(BudgetCalculationType.valueOf(getBudgetCalculationType()), targetCharge, keyTable, getPercentage() == null ? BigDecimal.ZERO : getPercentage(), getFixedBudgetedAmount() == null || getFixedBudgetedAmount().equals(BigDecimal.ZERO) ? null : getFixedBudgetedAmount(),
                    getFixedAuditedAmount() == null || getFixedAuditedAmount().equals(BigDecimal.ZERO) ? null : getFixedAuditedAmount());
        } else {
            DirectCostTable directCostTable = findOrCreateDirectCostTable(budgetItem.getBudget(), getPartitioningTableName());
            return budgetItem.updateOrCreatePartitionItem(BudgetCalculationType.valueOf(getBudgetCalculationType()),targetCharge, directCostTable, getPercentage() == null ? BigDecimal.ZERO : getPercentage(), getFixedBudgetedAmount() == null || getFixedBudgetedAmount().equals(BigDecimal.ZERO) ? null : getFixedBudgetedAmount(),
                    getFixedAuditedAmount() == null || getFixedAuditedAmount().equals(BigDecimal.ZERO) ? null : getFixedAuditedAmount());
        }
    }

    private BudgetItem fetchBudgetItemForCharge(final Budget budget, final Charge incomingCharge){
        final BudgetItem budgetItem = Lists.newArrayList(budget.getItems()).stream()
                .filter(i -> i.getCharge() == incomingCharge).findFirst().orElse(null);
        if (budgetItem == null) throw  new ApplicationException(String.format("BudgetItem with charge %s not found.", getIncomingChargeReference()));
        return budgetItem;
    }

    private Charge fetchCharge(final String chargeReference) {
        final Charge charge = chargeRepository
                .findByReference(chargeReference);
        if (charge == null) {
            throw new ApplicationException(String.format("Charge with reference %s not found.", chargeReference));
        }
        return charge;
    }

    private KeyTable findOrCreateKeyTable(final Budget budget, final String keyTableName, final String foundationValueType, final String keyValueMethod){
       return keyTableRepository.findOrCreateBudgetKeyTable(budget, keyTableName, FoundationValueType.valueOf(foundationValueType), KeyValueMethod.valueOf(keyValueMethod), 6);
    }

    private DirectCostTable findOrCreateDirectCostTable(final Budget budget, final String directCostTableName){
        return directCostTableRepository.findOrCreateDirectCostTable(budget, directCostTableName);
    }

    /**
     * To allow for usage within fixture scripts also.
     */
    @Setter
    private FixtureScript.ExecutionContext executionContext;

    /**
     * To allow for usage within fixture scripts also.
     */
    @Setter
    private ExcelFixture2 excelFixture2;

    @Override
    public void handleRow(final BudgetPartitionItemImportExport previousRow) {
        importData(previousRow);
    }

    @Inject
    private ChargeRepository chargeRepository;

    @Inject
    private BudgetRepository budgetRepository;

    @Inject
    private PropertyRepository propertyRepository;

    @Inject
    private KeyTableRepository keyTableRepository;

    @Inject
    private RepositoryService repositoryService;

    @Inject
    private DirectCostTableRepository directCostTableRepository;

    @Inject
    private BudgetService budgetService;

}
