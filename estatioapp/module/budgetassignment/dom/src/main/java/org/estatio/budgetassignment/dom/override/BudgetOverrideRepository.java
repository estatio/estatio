package org.estatio.budgetassignment.dom.override;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.lease.Lease;

@DomainService(repositoryFor = BudgetOverride.class, nature = NatureOfService.DOMAIN)
public class BudgetOverrideRepository extends UdoDomainRepositoryAndFactory<BudgetOverride> {

    public BudgetOverrideRepository() {
        super(BudgetOverrideRepository.class, BudgetOverride.class);
    }

    public BudgetOverrideForFixed newBudgetOverrideForFixed(
            final BigDecimal fixedValue,
            final Lease lease,
            @Parameter(optionality = Optionality.OPTIONAL)
            final LocalDate startDate,
            @Parameter(optionality = Optionality.OPTIONAL)
            final LocalDate endDate,
            final Charge invoiceCharge,
            @Parameter(optionality = Optionality.OPTIONAL)
            final Charge incomingCharge,
            @Parameter(optionality = Optionality.OPTIONAL)
            final BudgetCalculationType type,
            final String reason){
        BudgetOverrideForFixed newOverride = newTransientInstance(BudgetOverrideForFixed.class);
        newOverride = (BudgetOverrideForFixed) setValues(newOverride, lease, startDate, endDate, invoiceCharge, incomingCharge, type, reason);
        newOverride.setFixedValue(fixedValue);
        persistIfNotAlready(newOverride);
        return newOverride;
    }

    public String validateNewBudgetOverrideForFixed(
            final BigDecimal fixedValue,
            final Lease lease,
            final LocalDate startDate,
            final LocalDate endDate,
            final Charge invoiceCharge,
            final Charge incomingCharge,
            final BudgetCalculationType type,
            final String reason){
        return validateNewBudgetOverride(lease, startDate, endDate, invoiceCharge, incomingCharge, type, reason);
    }

    public BudgetOverrideForFlatRate newBudgetOverrideForFlatRate(
            final BigDecimal valuePerM2,
            final BigDecimal weightedArea,
            final Lease lease,
            @Parameter(optionality = Optionality.OPTIONAL)
            final LocalDate startDate,
            @Parameter(optionality = Optionality.OPTIONAL)
            final LocalDate endDate,
            final Charge invoiceCharge,
            @Parameter(optionality = Optionality.OPTIONAL)
            final Charge incomingCharge,
            @Parameter(optionality = Optionality.OPTIONAL)
            final BudgetCalculationType type,
            final String reason){
        BudgetOverrideForFlatRate newOverride = newTransientInstance(BudgetOverrideForFlatRate.class);
        newOverride = (BudgetOverrideForFlatRate) setValues(newOverride, lease, startDate, endDate, invoiceCharge, incomingCharge, type, reason);
        newOverride.setValuePerM2(valuePerM2);
        newOverride.setWeightedArea(weightedArea);
        persistIfNotAlready(newOverride);
        return newOverride;
    }

    public String validateNewBudgetOverrideForFlatRate(
            final BigDecimal valuePerM2,
            final BigDecimal weightedArea,
            final Lease lease,
            final LocalDate startDate,
            final LocalDate endDate,
            final Charge invoiceCharge,
            final Charge incomingCharge,
            final BudgetCalculationType type,
            final String reason){
        return validateNewBudgetOverride(lease, startDate, endDate, invoiceCharge, incomingCharge, type, reason);
    }

    public BudgetOverrideForMax newBudgetOverrideForMax(
            final BigDecimal maxValue,
            final Lease lease,
            @Parameter(optionality = Optionality.OPTIONAL)
            final LocalDate startDate,
            @Parameter(optionality = Optionality.OPTIONAL)
            final LocalDate endDate,
            final Charge invoiceCharge,
            @Parameter(optionality = Optionality.OPTIONAL)
            final Charge incomingCharge,
            @Parameter(optionality = Optionality.OPTIONAL)
            final BudgetCalculationType type,
            final String reason){
        BudgetOverrideForMax newOverride = newTransientInstance(BudgetOverrideForMax.class);
        newOverride = (BudgetOverrideForMax) setValues(newOverride, lease, startDate, endDate, invoiceCharge, incomingCharge, type, reason);
        newOverride.setMaxValue(maxValue);
        persistIfNotAlready(newOverride);
        return newOverride;
    }

    public String validateNewBudgetOverrideForMax(
            final BigDecimal maxValue,
            final Lease lease,
            final LocalDate startDate,
            final LocalDate endDate,
            final Charge invoiceCharge,
            final Charge incomingCharge,
            final BudgetCalculationType type,
            final String reason){
        return validateNewBudgetOverride(lease, startDate, endDate, invoiceCharge, incomingCharge, type, reason);
    }

    private BudgetOverride setValues(
            final BudgetOverride budgetOverride,
            final Lease lease,
            final LocalDate startDate,
            final LocalDate endDate,
            final Charge invoiceCharge,
            final Charge incomingCharge,
            final BudgetCalculationType type,
            final String reason
            ){
        budgetOverride.setLease(lease);
        budgetOverride.setStartDate(startDate);
        budgetOverride.setEndDate(endDate);
        budgetOverride.setInvoiceCharge(invoiceCharge);
        budgetOverride.setIncomingCharge(incomingCharge);
        budgetOverride.setType(type);
        budgetOverride.setReason(reason);
        return budgetOverride;
    }

    public List<BudgetOverride> allBudgetOverrides(){
        List<BudgetOverrideForMax> allMax = allInstances(BudgetOverrideForMax.class);
        List<BudgetOverrideForFixed> allFixed = allInstances(BudgetOverrideForFixed.class);
        List<BudgetOverride> result = new ArrayList<>();
        result.addAll(allMax);
        result.addAll(allFixed);
        return result;
    }

    public List<BudgetOverride> findByLease(final Lease lease) {
        return allMatches("findByLease", "lease", lease);
    }

    public List<BudgetOverride> findByLeaseAndInvoiceCharge(final Lease lease, final Charge invoiceCharge) {
        return allMatches("findByLeaseAndInvoiceCharge", "lease", lease, "invoiceCharge", invoiceCharge);
    }

    @Programmatic
    public String validateNewBudgetOverride(final Lease lease, final LocalDate startDate, final LocalDate endDate, final Charge invoiceCharge, final Charge incomingCharge, final BudgetCalculationType type, final String reason){
        BudgetOverride tempOverride = new BudgetOverrideDummy();
        tempOverride.setStartDate(startDate);
        tempOverride.setEndDate(endDate);
        tempOverride.setInvoiceCharge(invoiceCharge);
        tempOverride.setIncomingCharge(incomingCharge);
        tempOverride.setType(type);
        try {
            validateWithSameLeaseAndInvoiceCharge(tempOverride, findByLeaseAndInvoiceCharge(lease, invoiceCharge));
        } catch (Exception e){
            return "Conflicting budget overrides found";
        }
        if (reason==null){
            return "Reason cannot be empty";
        }
        if (invoiceCharge==null){
            return "Invoice charge cannnot be empty";
        }
        return null;
    }

    @Programmatic
    public void validateBudgetOverridesForLease(final Lease lease, final Charge invoiceCharge) throws IllegalArgumentException {
        List<BudgetOverride> overridesForLeaseAndInvoiceCharge = new ArrayList<>();
        for (BudgetOverride override : findByLeaseAndInvoiceCharge(lease, invoiceCharge)){
            validateWithSameLeaseAndInvoiceCharge(override, overridesForLeaseAndInvoiceCharge);
            overridesForLeaseAndInvoiceCharge.add(override);
        }
    }

    @Programmatic
    public void validateWithSameLeaseAndInvoiceCharge(final BudgetOverride override, final List<BudgetOverride> overrideListWithSameLeaseAndInvoiceCharge) throws IllegalArgumentException {
        for (BudgetOverride overrideToCompare : overrideListWithSameLeaseAndInvoiceCharge){
            // test overlapping period
            if (override.getInterval().overlaps(overrideToCompare.getInterval())){
                // test type
                if (override.getType()==null || overrideToCompare.getType()==null || override.getType()==overrideToCompare.getType()){
                    // test incoming charge
                    if (override.getIncomingCharge()==null || overrideToCompare.getIncomingCharge()==null || override.getIncomingCharge()==overrideToCompare.getIncomingCharge()){
                        throw new IllegalArgumentException("Conflicting budget overrides found");
                    }
                }
            }
        }
    }

}
