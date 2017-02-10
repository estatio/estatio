package org.estatio.app.services.budget;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.dom.Importable;
import org.estatio.asset.dom.Property;
import org.estatio.asset.dom.PropertyRepository;
import org.estatio.budget.dom.budget.Budget;
import org.estatio.budget.dom.budget.BudgetRepository;
import org.estatio.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.budget.dom.budgetitem.BudgetItemRepository;
import org.estatio.budget.dom.keytable.FoundationValueType;
import org.estatio.budget.dom.keytable.KeyTable;
import org.estatio.budget.dom.keytable.KeyTableRepository;
import org.estatio.budget.dom.keytable.KeyValueMethod;
import org.estatio.budget.dom.partioning.PartitionItem;
import org.estatio.budget.dom.partioning.PartitionItemRepository;
import org.estatio.charge.dom.Charge;
import org.estatio.charge.dom.ChargeRepository;

import lombok.Getter;
import lombok.Setter;

@DomainObject(nature = Nature.VIEW_MODEL)
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
            final String budgetChargeReference,
            final BigDecimal budgetedValue,
            final BigDecimal auditedValue,
            final String keyTableName,
            final String foundationValueType,
            final String keyValueMethod,
            final String invoiceChargeReference,
            final BigDecimal percentage
            ){
        this.propertyReference = propertyReference;
        this.budgetStartDate = budgetStartDate;
        this.budgetEndDate = budgetEndDate;
        this.budgetChargeReference = budgetChargeReference;
        this.budgetedValue = budgetedValue;
        this.auditedValue = auditedValue;
        this.keyTableName = keyTableName;
        this.foundationValueType = foundationValueType;
        this.keyValueMethod = keyValueMethod;
        this.invoiceChargeReference = invoiceChargeReference;
        this.percentage = percentage;
    }

    @Getter @Setter
    private String propertyReference;
    @Getter @Setter
    private LocalDate budgetStartDate;
    @Getter @Setter
    private LocalDate budgetEndDate;
    @Getter @Setter
    private String budgetChargeReference;
    @Getter @Setter
    private BigDecimal budgetedValue;
    @Getter @Setter
    private BigDecimal auditedValue;
    @Getter @Setter
    private String keyTableName;
    @Getter @Setter
    private String foundationValueType;
    @Getter @Setter
    private String keyValueMethod;
    @Getter @Setter
    private String invoiceChargeReference;
    @Getter @Setter
    private BigDecimal percentage;


    @Override
    public List<Class> importAfter() {
        return Lists.newArrayList();
    }

    @Override
    @Programmatic
    public List<Object> importData(final Object previousRow) {

        Property property = propertyRepository.findPropertyByReference(getPropertyReference());
        if (property == null) throw  new ApplicationException(String.format("Property with reference [%s] not found.", getPropertyReference()));
        Charge sourceCharge = fetchCharge(getBudgetChargeReference());
        Charge targetCharge = fetchCharge(getInvoiceChargeReference());
        Budget budget = budgetRepository.findOrCreateBudget(property, getBudgetStartDate(), getBudgetEndDate());
        KeyTable keyTable = findOrCreateKeyTable(budget, getKeyTableName(), getFoundationValueType(), getKeyValueMethod());
        PartitionItem partitionItem =
                budget
                        .findOrCreatePartitioningForBudgeting()
                        .findOrCreateBudgetItem(sourceCharge)
                        .updateOrCreateBudgetItemValue(budgetedValue, budgetStartDate, BudgetCalculationType.BUDGETED)
                        .updateOrCreateBudgetItemValue(auditedValue, budgetEndDate, BudgetCalculationType.ACTUAL)
                        .updateOrCreatePartitionItem(targetCharge, keyTable, getPercentage());

        return Lists.newArrayList(budget);
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

}
