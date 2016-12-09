package org.estatio.dom.budgetassignment.calculationresult;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Auditing;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.utils.TitleBuilder;

import org.estatio.dom.UdoDomainObject2;
import org.estatio.dom.budgetassignment.override.BudgetOverride;
import org.estatio.dom.budgetassignment.override.BudgetOverrideRepository;
import org.estatio.dom.budgetassignment.override.BudgetOverrideValue;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculation;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationRepository;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.lease.Occupancy;

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
@Unique(name = "BudgetCalculationResult_budgetCalculationRun_invoiceCharge_UNQ", members = { "budgetCalculationRun", "invoiceCharge" })
@javax.jdo.annotations.Queries({
        @Query(
                name = "findUnique", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.budgetassignment.calculationresult.BudgetCalculationResult " +
                        "WHERE budgetCalculationRun == :budgetCalculationRun && "
                        + "invoiceCharge == :invoiceCharge")
})

@DomainObject(
        objectType = "org.estatio.dom.budgetassignment.calculationresult.BudgetCalculationResult",
        auditing = Auditing.DISABLED,
        publishing = Publishing.DISABLED
)
public class BudgetCalculationResult extends UdoDomainObject2<BudgetCalculationResult> {

    public BudgetCalculationResult() {
        super("budgetCalculationRun, invoiceCharge");
    }

    public String title(){
        return TitleBuilder.start()
                .withParent(getBudgetCalculationRun())
                .withName(" ")
                .withName(getInvoiceCharge())
                .toString();
    }

    @Getter @Setter
    @Column(name = "budgetCalculationRunId", allowsNull = "false")
    @PropertyLayout(hidden = Where.REFERENCES_PARENT)
    private BudgetCalculationRun budgetCalculationRun;

    @Getter @Setter
    @Column(name = "chargeId", allowsNull = "false")
    private Charge invoiceCharge;

    @Getter @Setter
    @Column(allowsNull = "true", scale = 2)
    private BigDecimal value;

    @Getter @Setter
    @Column(allowsNull = "true", scale = 2)
    private BigDecimal shortfall;

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<BudgetOverrideValue> getOverrideValues(){
        List<BudgetOverrideValue> results = new ArrayList<>();
        for (BudgetOverride override : budgetOverrideRepository
                .findByLeaseAndInvoiceCharge(
                        getBudgetCalculationRun().getLease(),
                        getInvoiceCharge())){
            for (BudgetOverrideValue value : override.getValues()){
                if (value.getType() == getBudgetCalculationRun().getType()) {
                    results.add(value);
                }
            }
        }
        return results;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<BudgetCalculation> getBudgetCalculations(){
        List<BudgetCalculation> results = new ArrayList<>();
        for (Occupancy occupancy : getBudgetCalculationRun().getLease().getOccupancies()) {
            results.addAll(budgetCalculationRepository.findByBudgetAndUnitAndInvoiceChargeAndType(getBudgetCalculationRun().getBudget(), occupancy.getUnit(), getInvoiceCharge(), getBudgetCalculationRun().getType()));
        }
        return results;
    }

    @Programmatic
    public void calculate() throws IllegalArgumentException {

        validateOverrides();

        BigDecimal valueCalculatedByBudget = BigDecimal.ZERO;
        BigDecimal valueUsingOverrides = BigDecimal.ZERO;
        List<Charge> incomingChargesOnOverrides = new ArrayList<>();

        for (BudgetCalculation calculation : getBudgetCalculations()){
            valueCalculatedByBudget = valueCalculatedByBudget.add(calculation.getValueForPartitionPeriod());
        }

        if (getOverrideValues().size()==1 && getOverrideValues().get(0).getBudgetOverride().getIncomingCharge()==null){
            // SCENARIO: one override for all
            valueUsingOverrides = valueUsingOverrides.add(getOverrideValues().get(0).getValue());
        } else {
            // SCENARIO: overrides on incoming charge level
            BigDecimal valueToSubtract = BigDecimal.ZERO;
            for (BudgetOverrideValue value : getOverrideValues()) {
                incomingChargesOnOverrides.add(value.getBudgetOverride().getIncomingCharge());
                valueUsingOverrides = valueUsingOverrides.add(value.getValue());
            }
            for (Charge charge : incomingChargesOnOverrides){
                for (BudgetCalculation calculation : getBudgetCalculations().stream().filter(x->x.getIncomingCharge().equals(charge)).collect(Collectors.toList())){
                    valueToSubtract = valueToSubtract.add(calculation.getValueForPartitionPeriod());
                }
            }
            valueUsingOverrides = valueUsingOverrides.add(valueCalculatedByBudget).subtract(valueToSubtract);
        }

        setValue(valueUsingOverrides.setScale(2, BigDecimal.ROUND_HALF_UP));
        setShortfall(valueCalculatedByBudget.subtract(valueUsingOverrides).setScale(2, BigDecimal.ROUND_HALF_UP));
    }

    void validateOverrides() throws IllegalArgumentException {
        budgetOverrideRepository.validateBudgetOverridesForLease(getBudgetCalculationRun().getLease(), getInvoiceCharge());
    }

    @Programmatic
    public void finalizeCalculationResult() {
        for (BudgetCalculation calculation : getBudgetCalculations()){
            calculation.finalizeCalculation();
        }
        for (BudgetOverrideValue overrideValue : getOverrideValues()){
            overrideValue.finalizeOverrideValue();
        }
    }

    @Programmatic
    public void remove() {
        repositoryService.removeAndFlush(this);
    }

    @Override
    public ApplicationTenancy getApplicationTenancy() {
        return getBudgetCalculationRun().getApplicationTenancy();
    }

    @Inject
    private BudgetOverrideRepository budgetOverrideRepository;

    @Inject
    private BudgetCalculationRepository budgetCalculationRepository;

    @Inject
    private RepositoryService repositoryService;

}

