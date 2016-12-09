package org.estatio.dom.budgetassignment.override;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;

import org.incode.module.base.dom.utils.TitleBuilder;
import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.dom.UdoDomainObject2;
import org.estatio.dom.asset.Unit;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budget.BudgetRepository;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculation;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationRepository;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationType;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.Occupancy;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE
        ,schema = "dbo"     // Isis' ObjectSpecId inferred from @Discriminator
)
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Discriminator(
        strategy = DiscriminatorStrategy.VALUE_MAP,
        column = "discriminator",
        value = "org.estatio.dom.budgetassignment.override.BudgetOverride"
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.IDENTITY,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Queries({
        @Query(
                name = "findByLease", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.budgetassignment.override.BudgetOverride " +
                        "WHERE lease == :lease"),
        @Query(
                name = "findByLeaseAndInvoiceCharge", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.budgetassignment.override.BudgetOverride " +
                        "WHERE lease == :lease && "
                        + "invoiceCharge == :invoiceCharge" )
})

@DomainObject()
public abstract class BudgetOverride extends UdoDomainObject2<BudgetOverride> {

    public BudgetOverride() {
        super("lease, invoiceCharge, type, incomingCharge, startDate, endDate, reason");
    }

    public String title(){
        return TitleBuilder.start()
                .withName(getLease())
                .withName(" - ")
                .withName(getReason())
                .toString();
    }

    @Getter @Setter
    @Column(name = "leaseId", allowsNull = "false")
    @PropertyLayout(hidden = Where.PARENTED_TABLES)
    private Lease lease;

    @Getter @Setter
    @Column(allowsNull = "true")
    private LocalDate startDate;

    @Getter @Setter
    @Column(allowsNull = "true")
    private LocalDate endDate;

    @Getter @Setter
    @Column(name = "chargeId", allowsNull = "false")
    private Charge invoiceCharge;

    @Getter @Setter
    @Column(name = "incomingChargeId", allowsNull = "true")
    private Charge incomingCharge;

    @Getter @Setter
    @Column(allowsNull = "true")
    private BudgetCalculationType type;

    @Getter @Setter
    @Column(allowsNull = "false")
    private String reason;

    @Getter @Setter
    @Persistent(mappedBy = "budgetOverride", dependentElement = "true")
    private SortedSet<BudgetOverrideValue> values = new TreeSet<>();

    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    public BudgetOverride terminate(final LocalDate endDate){
        setEndDate(endDate);
        return this;
    }

    public String validateTerminate(final LocalDate endDate){
        if (getStartDate()!=null && endDate.isBefore(getStartDate())){
            return "The date of termination cannot be before the start date";
        }
        return null;
    }

    @Programmatic
    public List<BudgetOverrideValue> findOrCreateValues(final LocalDate startDate){
        List<BudgetOverrideValue> results = new ArrayList<>();
        if (isActiveOnCalculationDate(startDate)) {
            BudgetOverrideValue resultBudgeted;
            BudgetOverrideValue resultActual;
            if (getType() == null) {
                resultBudgeted = valueFor(startDate, BudgetCalculationType.BUDGETED);
                if (resultBudgeted!=null) {
                    results.add(resultBudgeted);
                }
                resultActual = valueFor(startDate, BudgetCalculationType.ACTUAL);
                if (resultActual!=null) {
                    results.add(resultActual);
                }
            } else {
                switch (getType()) {
                case BUDGETED:
                    resultBudgeted = valueFor(startDate, BudgetCalculationType.BUDGETED);
                    if (resultBudgeted!=null) {
                        results.add(resultBudgeted);
                    }
                    break;

                case ACTUAL:
                    resultActual = valueFor(startDate, BudgetCalculationType.ACTUAL);
                    if (resultActual!=null) {
                        results.add(resultActual);
                    }
                    break;
                }
            }
        }
        return results;
    }

    @Programmatic
    abstract BudgetOverrideValue valueFor(final LocalDate date, final BudgetCalculationType type);

    @Programmatic
    public boolean isActiveOnCalculationDate(final LocalDate calculationDate) {
        if (getStartDate()!=null && calculationDate.isBefore(getStartDate())){
            return false;
        }
        if (getEndDate()!=null && (calculationDate.equals(getEndDate()) || calculationDate.isAfter(getEndDate()))){
            return false;
        }
        return true;
    }

    @Programmatic
    BigDecimal getCalculatedValueByBudget(final LocalDate budgetStartDate, final BudgetCalculationType type){
        BigDecimal value = BigDecimal.ZERO;
        List<Unit> unitsForLease = new ArrayList<>();
        List<BudgetCalculation> calculationsForLeaseAndCharges = new ArrayList<>();
        for (Occupancy occupancy : getLease().getOccupancies()){
            unitsForLease.add(occupancy.getUnit());
        }
        Budget budget = budgetRepository.findByPropertyAndDate(getLease().getProperty(), budgetStartDate);
        if (getIncomingCharge() == null) {
            for (Unit unit : unitsForLease){
                calculationsForLeaseAndCharges.addAll(budgetCalculationRepository.findByBudgetAndUnitAndInvoiceChargeAndType(budget, unit, getInvoiceCharge(), type));
            }
        } else {
            for (Unit unit : unitsForLease){
                calculationsForLeaseAndCharges.addAll(budgetCalculationRepository.findByBudgetAndUnitAndInvoiceChargeAndIncomingChargeAndType(budget, unit, getInvoiceCharge(), getIncomingCharge(), type));
            }
        }
        for (BudgetCalculation calculation : calculationsForLeaseAndCharges){
            //TODO - consolidate in test: NOTE!! the pro rata calculation is used !!
            value = value.add(calculation.getValueForPartitionPeriod());
        }
        return value;
    }

    @Programmatic
    public BudgetOverrideValue findOrCreateCalculation(final BigDecimal value, final BudgetCalculationType type){
        return budgetOverrideValueRepository.findOrCreateOverrideValue(value, this, type);
    }

    @Programmatic
    public LocalDateInterval getInterval() {
        return LocalDateInterval.excluding(getStartDate(), getEndDate());
    }

    // TODO: for prototyping purposes only; should be removed when in production
    @Programmatic
    @Deprecated
    public void remove() {
        remove(this);
    }

    @Inject
    BudgetRepository budgetRepository;

    @Inject
    BudgetCalculationRepository budgetCalculationRepository;

    @Inject
    BudgetOverrideValueRepository budgetOverrideValueRepository;

}
