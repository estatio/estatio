package org.estatio.module.budgetassignment.dom.calculationresult;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.VersionStrategy;

import com.google.api.client.util.Lists;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Auditing;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.utils.TitleBuilder;

import org.estatio.module.base.dom.UdoDomainObject2;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculation;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationRepository;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.occupancy.Occupancy;

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
@Unique(name = "BudgetCalculationResult_budget_occupancy_invoiceCharge_type_UNQ", members = { "budget", "occupancy", "invoiceCharge", "type"})
@javax.jdo.annotations.Queries({
        @Query(
                name = "findUnique", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationResult " +
                        "WHERE budget == :budget && "
                        + "occupancy == :occupancy && "
                        + "invoiceCharge == :invoiceCharge && "
                        + "type == :type"),
        @Query(
                name = "findByBudgetAndOccupancyAndType", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationResult " +
                        "WHERE budget == :budget && "
                        + "occupancy == :occupancy && "
                        + "type == :type "),
        @Query(
                name = "findByBudget", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationResult " +
                        "WHERE budget == :budget "),
})

@DomainObject(
        objectType = "org.estatio.dom.budgetassignment.calculationresult.BudgetCalculationResult",
        auditing = Auditing.DISABLED,
        publishing = Publishing.DISABLED
)
public class BudgetCalculationResult extends UdoDomainObject2<BudgetCalculationResult> {

    public BudgetCalculationResult() {
        super("budget, occupancy, invoiceCharge, type");
    }

    public BudgetCalculationResult(final Budget budget, final Occupancy occupancy, final Charge invoiceCharge, final BudgetCalculationType type, final BigDecimal value){
        this();
        this.budget = budget;
        this.occupancy = occupancy;
        this.invoiceCharge = invoiceCharge;
        this.type = type;
        this.value = value;
    }

    public String title(){
        return TitleBuilder.start()
                .withParent(getBudget())
                .withName(getInvoiceCharge())
                .withName(" ")
                .withName(getOccupancy().title())
                .toString();
    }

    @Getter @Setter
    @Column(name = "budgetId", allowsNull = "false")
    private Budget budget;

    @Getter @Setter
    @Column(name = "occupancyId", allowsNull = "false")
    private Occupancy occupancy;

    @Getter @Setter
    @Column(name = "chargeId", allowsNull = "false")
    private Charge invoiceCharge;

    @Getter @Setter
    @Column(allowsNull = "false")
    private BudgetCalculationType type;

    @Getter @Setter
    @Column(allowsNull = "false", scale = 2)
    private BigDecimal value;

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<BudgetCalculation> getBudgetCalculations(){
        return budgetCalculationRepository.findByBudgetAndUnitAndInvoiceChargeAndType(getBudget(), getOccupancy().getUnit(), getInvoiceCharge(), getType());
    }

    @Programmatic
    public boolean occupancyCoversLeaseEffectiveInterval(){
        return getOccupancy().getInterval().contains(getOccupancy().getLease().getEffectiveInterval());
    }

    @Programmatic
    public boolean leaseCoversBudgetInterval(){
        return getOccupancy().getLease().getEffectiveInterval().contains(getBudget().getInterval());
    }

    @Programmatic
    public boolean occupanciesOfleaseAndLeaseRenewalsCoverBudgetInterval(){
        if (this.leaseCoversBudgetInterval() && this.occupancyCoversLeaseEffectiveInterval()) return true;
        // find first linked lease for budget
        Lease firstLeaseIfAny = this.getOccupancy().getLease();
        while (firstLeaseIfAny!=null && !firstLeaseIfAny.getEffectiveInterval().contains(this.getBudget().getStartDate())){
            firstLeaseIfAny = (Lease) firstLeaseIfAny.getPrevious();
        }
        if (firstLeaseIfAny==null) return false;
        // find last linked lease for budget
        Lease lastLeaseIfAny = this.getOccupancy().getLease();
        while (lastLeaseIfAny!=null && !lastLeaseIfAny.getEffectiveInterval().contains(this.getBudget().getEndDate())){
            lastLeaseIfAny = (Lease) lastLeaseIfAny.getNext();
        }
        if (lastLeaseIfAny==null) return false;

        // at this point we have a first and last lease covering the budget period
        // we now check if the relevant occupancies are covering the lease effective interval for each
        Lease leaseToCheck = firstLeaseIfAny;
        if (!occupancyIntervalCoversLeaseInterval(leaseToCheck)) return false;
        while (!leaseToCheck.getNext().equals(lastLeaseIfAny)){
            leaseToCheck = (Lease) firstLeaseIfAny.getNext();
            if (!occupancyIntervalCoversLeaseInterval(leaseToCheck)) return false;
        }
        if (!occupancyIntervalCoversLeaseInterval(lastLeaseIfAny)) return false;

        return true;
    }

    private boolean occupancyIntervalCoversLeaseInterval(final Lease lease){
        final Occupancy firstOcc = Lists.newArrayList(lease.getOccupancies()).stream()
                .filter(o -> o.getUnit().equals(this.getOccupancy().getUnit())).findFirst().orElse(null);
        if (firstOcc==null || !firstOcc.getInterval().contains(lease.getEffectiveInterval())) return false;
        return true;
    }

    @Programmatic
    public void remove() {
        repositoryService.removeAndFlush(this);
    }

    @Override
    public ApplicationTenancy getApplicationTenancy() {
        return getOccupancy().getApplicationTenancy();
    }

    @Inject
    private BudgetCalculationRepository budgetCalculationRepository;

    @Inject
    private RepositoryService repositoryService;

}

