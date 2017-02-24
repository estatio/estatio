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
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.dom.Importable;
import org.estatio.dom.budgetassignment.override.BudgetOverride;
import org.estatio.dom.budgetassignment.override.BudgetOverrideRepository;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationType;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.ChargeRepository;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseRepository;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.app.services.budget.BudgetOverrideImportExport"
)
public class BudgetOverrideImportExport implements Importable {

    public String title() {
        return "Budget Override Import / Export";
    }

    public BudgetOverrideImportExport(){
    }

    public BudgetOverrideImportExport(
            final String leaseReference,
            final LocalDate startDate,
            final LocalDate endDate,
            final String invoiceChargeReference,
            final String incomingChargeReference,
            final String type,
            final String reason,
            final String className,
            final BigDecimal maxValue,
            final BigDecimal fixedValue,
            final BigDecimal valuePerM2,
            final BigDecimal weightedArea
            ){
        this.leaseReference = leaseReference;
        this.startDate = startDate;
        this.endDate = endDate;
        this.invoiceChargeReference = invoiceChargeReference;
        this.incomingChargeReference = incomingChargeReference;
        this.type = type;
        this.reason = reason;
        this.className = className;
        this.maxValue = maxValue;
        this.fixedValue = fixedValue;
        this.valuePerM2 = valuePerM2;
        this.weightedArea = weightedArea;
    }

    @Getter @Setter
    private String leaseReference;
    @Getter @Setter
    private LocalDate startDate;
    @Getter @Setter
    private LocalDate endDate;
    @Getter @Setter
    private String invoiceChargeReference;
    @Getter @Setter
    private String incomingChargeReference;
    @Getter @Setter
    private String type;
    @Getter @Setter
    private String reason;
    @Getter @Setter
    private String className;
    @Getter @Setter
    private BigDecimal maxValue;
    @Getter @Setter
    private BigDecimal fixedValue;
    @Getter @Setter
    private BigDecimal valuePerM2;
    @Getter @Setter
    private BigDecimal weightedArea;

    @Override
    public List<Class> importAfter() {
        return Lists.newArrayList();
    }

    @Override
    @Programmatic
    public List<Object> importData(final Object previousRow) {

        if (getInvoiceChargeReference()==null){
            throw new ApplicationException("Incoming charge reference cannot be empty");
        }
        if (getReason()==null){
            throw new ApplicationException("Reason cannot be empty");
        }
        BudgetCalculationType budgetCalculationType;
        budgetCalculationType = getType()==null ? null : BudgetCalculationType.valueOf(getType());

        BudgetOverride budgetOverride;
        Charge incomingCharge = getIncomingChargeReference()==null ? null : fetchCharge(getIncomingChargeReference());

        switch (getClassName()) {

        case "BudgetOverrideForMax":
            validate();
            budgetOverride = budgetOverrideRepository
                    .newBudgetOverrideForMax(
                            getMaxValue(),
                            fetchLease(getLeaseReference()),
                            getStartDate(),
                            getEndDate(),
                            fetchCharge(getInvoiceChargeReference()),
                            incomingCharge,
                            budgetCalculationType,
                            getReason()
                    );
            break;

        case "BudgetOverrideForFixed":
            validate();
            budgetOverride = budgetOverrideRepository
                    .newBudgetOverrideForFixed(
                            getFixedValue(),
                            fetchLease(getLeaseReference()),
                            getStartDate(),
                            getEndDate(),
                            fetchCharge(getInvoiceChargeReference()),
                            incomingCharge,
                            budgetCalculationType,
                            getReason()
                    );
            break;

        case "BudgetOverrideForFlatRate":
            validate();
            budgetOverride = budgetOverrideRepository
                    .newBudgetOverrideForFlatRate(
                            getValuePerM2(),
                            getWeightedArea(),
                            fetchLease(getLeaseReference()),
                            getStartDate(),
                            getEndDate(),
                            fetchCharge(getInvoiceChargeReference()),
                            incomingCharge,
                            budgetCalculationType,
                            getReason()
                    );
            break;

        default:
            throw new ApplicationException(String.format("Classname %s is not valid.", getClassName()));
        }

        return Lists.newArrayList(budgetOverride);
    }

    private void validate(){
        Charge incomingCharge = getIncomingChargeReference()==null ? null : fetchCharge(getIncomingChargeReference());
        BudgetCalculationType budgetCalculationType;
        budgetCalculationType = getType()==null ? null : BudgetCalculationType.valueOf(getType());
        String invalidReason = budgetOverrideRepository.validateNewBudgetOverride(
                fetchLease(getLeaseReference()),
                getStartDate(),
                getEndDate(),
                fetchCharge(getInvoiceChargeReference()),
                incomingCharge,
                budgetCalculationType,
                getReason()
        );
        if (invalidReason!=null){
            throw new ApplicationException(String.format(invalidReason));
        }
    }

    private Lease fetchLease(final String leaseReference){
        final Lease lease = leaseRepository.findLeaseByReference(leaseReference);
        if (lease==null){
            throw new ApplicationException(String.format("Lease with reference %s not found.", leaseReference));
        }
        return lease;
    }

    private Charge fetchCharge(final String chargeReference) {
        final Charge charge = chargeRepository.findByReference(chargeReference);
        if (charge == null) {
            throw new ApplicationException(String.format("Charge with reference %s not found.", chargeReference));
        }
        return charge;
    }

    @Inject
    private BudgetOverrideRepository budgetOverrideRepository;
    @Inject
    private LeaseRepository leaseRepository;
    @Inject
    private ChargeRepository chargeRepository;
    @Inject
    private ServiceRegistry2 serviceRegistry2;

}
