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
import org.estatio.module.budgetassignment.dom.BudgetService;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.app.services.budget.BudgetItemImportExport"
)
public class BudgetItemImportExport implements Importable, FixtureAwareRowHandler<BudgetItemImportExport> {

    public String title() {
        return "Budget Item Import / Export";
    }

    public BudgetItemImportExport(){
    }

    public BudgetItemImportExport(
            final String propertyReference,
            final LocalDate budgetStartDate,
            final LocalDate budgetEndDate,
            final String incomingChargeReference,
            final BigDecimal budgetedValue,
            final BigDecimal auditedValue,
            final String calculationDescription){
        this.propertyReference = propertyReference;
        this.budgetStartDate = budgetStartDate;
        this.budgetEndDate = budgetEndDate;
        this.incomingChargeReference = incomingChargeReference;
        this.budgetedValue = budgetedValue;
        this.auditedValue = auditedValue;
        this.calculationDescription = calculationDescription;
    }

    @Getter @Setter
    @MemberOrder(sequence = "7")
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
    @MemberOrder(sequence = "5")
    private BigDecimal auditedValue;
    @Getter @Setter
    @MemberOrder(sequence = "6")
    private String calculationDescription;

    @Override
    @Programmatic
    public List<Object> importData(final Object previousRow) {
        Property property = propertyRepository.findPropertyByReference(getPropertyReference());
        if (property == null) throw  new ApplicationException(String.format("Property with reference [%s] not found.", getPropertyReference()));
        Budget budget = budgetRepository.findOrCreateBudget(property, getBudgetStartDate(), getBudgetEndDate());
        if (previousRow==null){
            budgetService.removeExistingPartitionItemsAndBudgetItemsIfCanBeRemoved(budget);
        }
        Charge incomingCharge = fetchCharge(getIncomingChargeReference());
        BudgetItem budgetItem = findOrCreateBudgetAndBudgetItem(budget, incomingCharge);
        return Lists.newArrayList(budgetItem.getBudget());
    }

    private BudgetItem findOrCreateBudgetAndBudgetItem(final Budget budget, final Charge incomingCharge){
        BudgetItem budgetItem = budget
                .findOrCreateBudgetItem(incomingCharge)
                .upsertValue(getBudgetedValue(), getBudgetStartDate(), BudgetCalculationType.BUDGETED)
                .upsertValue(getAuditedValue(), getBudgetEndDate(), BudgetCalculationType.AUDITED);
        budgetItem.setCalculationDescription(getCalculationDescription());
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
    public void handleRow(final BudgetItemImportExport previousRow) {
        importData(previousRow);
    }

    @Inject
    private ChargeRepository chargeRepository;

    @Inject
    private BudgetRepository budgetRepository;

    @Inject
    private PropertyRepository propertyRepository;

    @Inject
    private RepositoryService repositoryService;

    @Inject
    private BudgetService budgetService;

}
