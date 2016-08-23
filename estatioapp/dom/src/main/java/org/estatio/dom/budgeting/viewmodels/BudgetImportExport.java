package org.estatio.dom.budgeting.viewmodels;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.InvokeOn;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.Importable;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.budgeting.allocation.BudgetItemAllocation;
import org.estatio.dom.budgeting.allocation.BudgetItemAllocationRepository;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budget.BudgetRepository;
import org.estatio.dom.budgeting.budgetitem.BudgetItemRepository;
import org.estatio.dom.budgeting.keytable.FoundationValueType;
import org.estatio.dom.budgeting.keytable.KeyTable;
import org.estatio.dom.budgeting.keytable.KeyTableRepository;
import org.estatio.dom.budgeting.keytable.KeyValueMethod;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.ChargeRepository;

import lombok.Getter;
import lombok.Setter;

@DomainObject(nature = Nature.VIEW_MODEL)
public class BudgetImportExport implements Importable {

    //region > constructors, title
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
            final String allocationChargeReference,
            final BigDecimal percentage
            ){
        this.propertyReference = propertyReference;
        this.budgetStartDate = budgetStartDate;
        this.budgetEndDate = budgetEndDate;
        this.budgetChargeReference = budgetChargeReference;
        this.budgetedValue = budgetedValue;
        this.auditedValue = auditedValue;
        this.keyTableName = keyTableName;
        this.allocationChargeReference = allocationChargeReference;
        this.percentage = percentage;
    }
    //endregion


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
    private String allocationChargeReference;
    @Getter @Setter
    private BigDecimal percentage;


    @Override
    public List<Class> importAfter() {
        return Lists.newArrayList();
    }

    // REVIEW: is this view model actually ever surfaced in the UI?
    @Action(invokeOn= InvokeOn.OBJECT_AND_COLLECTION, publishing = Publishing.DISABLED, semantics = SemanticsOf.IDEMPOTENT)
    public List<Object> importData() {
        return importData(null);
    }

    @Override
    @Programmatic
    public List<Object> importData(final Object previousRow) {

        Property property = propertyRepository.findPropertyByReference(getPropertyReference());
        if (property == null) throw  new ApplicationException(String.format("Property with reference [%s] not found.", getPropertyReference()));
        Charge sourceCharge = fetchCharge(getBudgetChargeReference());
        Charge targetCharge = fetchCharge(getAllocationChargeReference());
        Budget budget = budgetRepository.findOrCreateBudget(property, getBudgetStartDate(), getBudgetEndDate());
        KeyTable keyTable = findOrCreateKeyTable(budget, getKeyTableName());
        BudgetItemAllocation budgetItemAllocation =
                budget
                        .updateOrCreateBudgetItem(sourceCharge, getBudgetedValue(), getAuditedValue())
                        .findOrCreateBudgetItemAllocation(targetCharge, keyTable, getPercentage());

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

    private KeyTable findOrCreateKeyTable(final Budget budget, final String keyTableName){
       return keyTableRepository.findOrCreateBudgetKeyTable(budget, keyTableName, FoundationValueType.MANUAL, KeyValueMethod.DEFAULT, 6);
    }

    //region > injected services
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
    //endregion

}
