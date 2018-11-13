package org.estatio.module.application.imports;

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
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.module.base.dom.Importable;
import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budget.BudgetRepository;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.module.budget.dom.budgetitem.BudgetItem;
import org.estatio.module.budget.dom.budgetitem.BudgetItemRepository;
import org.estatio.module.budget.dom.keytable.FoundationValueType;
import org.estatio.module.budget.dom.keytable.KeyTable;
import org.estatio.module.budget.dom.keytable.KeyTableRepository;
import org.estatio.module.budget.dom.keytable.KeyValueMethod;
import org.estatio.module.budget.dom.partioning.PartitionItem;
import org.estatio.module.budget.dom.partioning.PartitionItemRepository;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItemRepository;
import org.estatio.module.capex.dom.order.OrderItemRepository;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;

import lombok.Getter;
import lombok.Setter;

// TODO: need to untangle this and push back down to budget module
@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.app.services.budget.BudgetImportExport"
)
public class BudgetImportExport implements Importable {

    public String title() {
        return "Budget Import / Export";
    }

    public BudgetImportExport(){
    }

    public BudgetImportExport(
            final String propertyReference,
            final LocalDate budgetStartDate,
            final LocalDate budgetEndDate,
            final String incomingChargeReference,
            final BigDecimal budgetedValue,
            final BigDecimal auditedValue,
            final String keyTableName,
            final String foundationValueType,
            final String keyValueMethod,
            final String outgoingChargeReference,
            final BigDecimal percentage,
            final BigDecimal fixedBudgetedAmount,
            final BigDecimal fixedAuditedAmount,
            final String calculationDescription){
        this.propertyReference = propertyReference;
        this.budgetStartDate = budgetStartDate;
        this.budgetEndDate = budgetEndDate;
        this.incomingChargeReference = incomingChargeReference;
        this.budgetedValue = budgetedValue;
        this.auditedValue = auditedValue;
        this.keyTableName = keyTableName;
        this.foundationValueType = foundationValueType;
        this.keyValueMethod = keyValueMethod;
        this.outgoingChargeReference = outgoingChargeReference;
        this.percentage = percentage;
        this.fixedBudgetedAmount = fixedBudgetedAmount;
        this.fixedAuditedAmount = fixedAuditedAmount;
        this.calculationDescription = calculationDescription;
    }

    @Getter @Setter
    @MemberOrder(sequence = "14")
    private String propertyReference;
    @Getter @Setter
    @MemberOrder(sequence = "1")
    private LocalDate budgetStartDate;
    @Getter @Setter
    @MemberOrder(sequence = "2")
    private LocalDate budgetEndDate;
    @Getter @Setter
    @MemberOrder(sequence = "3")
    private String incomingChargeReference;
    @Getter @Setter
    @MemberOrder(sequence = "4")
    private BigDecimal budgetedValue;
    @Getter @Setter
    @MemberOrder(sequence = "6")
    private BigDecimal auditedValue;
    @Getter @Setter
    @MemberOrder(sequence = "9")
    private String keyTableName;
    @Getter @Setter
    @MemberOrder(sequence = "10")
    private String foundationValueType;
    @Getter @Setter
    @MemberOrder(sequence = "11")
    private String keyValueMethod;
    @Getter @Setter
    @MemberOrder(sequence = "12")
    private String outgoingChargeReference;
    @Getter @Setter
    @MemberOrder(sequence = "13")
    private BigDecimal percentage;
    @Getter @Setter
    @MemberOrder(sequence = "5")
    private BigDecimal fixedBudgetedAmount;
    @Getter @Setter
    @MemberOrder(sequence = "7")
    private BigDecimal fixedAuditedAmount;
    @Getter @Setter
    @MemberOrder(sequence = "8")
    private String calculationDescription;


    @Override
    @Programmatic
    public List<Object> importData(final Object previousRow) {
        if (previousRow==null){
            removeExistingBudgetItemsIfNotLinked();
        }
        Charge incomingCharge = fetchCharge(getIncomingChargeReference());
        BudgetItem budgetItem = findOrCreateBudgetAndBudgetItem(incomingCharge);
        if (getOutgoingChargeReference()!=null && getKeyTableName()!=null && getFoundationValueType()!=null && getKeyValueMethod()!=null && percentage!=null) {
           findOrCreatePartitionItem(budgetItem);
        }
        return Lists.newArrayList(budgetItem.getBudget());
    }

    private void removeExistingBudgetItemsIfNotLinked(){
        Property property = propertyRepository.findPropertyByReference(getPropertyReference());
        Budget budget = budgetRepository.findOrCreateBudget(property, getBudgetStartDate(), getBudgetEndDate());
        for (BudgetItem item : budget.getItems()){
            if (orderItemRepository.findByBudgetItem(item).size() == 0 && incomingInvoiceItemRepository.findByBudgetItem(item).size() == 0) {
                for (PartitionItem pItem : item.getPartitionItems()) {
                    pItem.remove();
                }
                repositoryService.removeAndFlush(item);
            }
        }
    }

    private BudgetItem findOrCreateBudgetAndBudgetItem(final Charge incomingCharge){
        Property property = propertyRepository.findPropertyByReference(getPropertyReference());
        if (property == null) throw  new ApplicationException(String.format("Property with reference [%s] not found.", getPropertyReference()));
        Budget budget = budgetRepository.findOrCreateBudget(property, getBudgetStartDate(), getBudgetEndDate());
        BudgetItem budgetItem = budget
                .findOrCreateBudgetItem(incomingCharge)
                .updateOrCreateBudgetItemValue(getBudgetedValue(), getBudgetStartDate(), BudgetCalculationType.BUDGETED)
                .updateOrCreateBudgetItemValue(getAuditedValue(), getBudgetEndDate(), BudgetCalculationType.ACTUAL);
        budgetItem.setCalculationDescription(getCalculationDescription());
        return budgetItem;
    }

    private PartitionItem findOrCreatePartitionItem(final BudgetItem budgetItem){
        Charge targetCharge = fetchCharge(getOutgoingChargeReference());
        KeyTable keyTable = findOrCreateKeyTable(budgetItem.getBudget(), getKeyTableName(), getFoundationValueType(), getKeyValueMethod());
        return budgetItem.updateOrCreatePartitionItem(targetCharge, keyTable, getPercentage(), getFixedBudgetedAmount(), getFixedAuditedAmount());
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

    @Inject
    private ChargeRepository chargeRepository;

    @Inject
    private BudgetRepository budgetRepository;

    @Inject
    private BudgetItemRepository budgetItemRepository;

    @Inject
    private PartitionItemRepository partitionItemRepository;

    @Inject
    private PropertyRepository propertyRepository;

    @Inject
    private KeyTableRepository keyTableRepository;

    @Inject
    private RepositoryService repositoryService;

    @Inject
    private OrderItemRepository orderItemRepository;

    @Inject
    private IncomingInvoiceItemRepository incomingInvoiceItemRepository;

}
