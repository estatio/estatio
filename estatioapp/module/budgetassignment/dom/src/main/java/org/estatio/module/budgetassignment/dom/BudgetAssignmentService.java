package org.estatio.module.budgetassignment.dom;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationResult;
import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationResultLinkRepository;
import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationResultRepository;
import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationRun;
import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationRunRepository;
import org.estatio.dom.budgetassignment.override.BudgetOverride;
import org.estatio.dom.budgetassignment.override.BudgetOverrideRepository;
import org.estatio.dom.budgetassignment.override.BudgetOverrideValue;
import org.estatio.dom.budgetassignment.viewmodels.CalculationResultViewModel;
import org.estatio.dom.budgetassignment.viewmodels.DetailedCalculationResultViewmodel;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculation;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationType;
import org.estatio.dom.budgeting.budgetcalculation.Status;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.budgeting.partioning.PartitionItem;
import org.estatio.dom.budgeting.partioning.Partitioning;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;
import org.estatio.module.lease.dom.InvoicingFrequency;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemStatus;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.LeaseStatus;
import org.estatio.module.lease.dom.LeaseTermForServiceCharge;
import org.estatio.module.lease.dom.LeaseTermRepository;
import org.estatio.module.lease.dom.occupancy.Occupancy;

@DomainService(nature = NatureOfService.DOMAIN)
public class BudgetAssignmentService {

    public List<BudgetCalculationRun> calculateResultsForLeases(final Budget budget, final BudgetCalculationType type){
        List<BudgetCalculationRun> results = new ArrayList<>();

        for (Lease lease : leasesWithActiveOccupations(budget)) {
            removeNewOverrideValues(lease);
            calculateOverrideValues(lease, budget);
            results.add(executeCalculationRun(lease, budget, type));
        }

        return results;
    }

    List<Lease> leasesWithActiveOccupations(final Budget budget){
        List<Lease> result = new ArrayList<>();
        for (Lease lease : leaseRepository.findLeasesByProperty(budget.getProperty())){
            // TODO: this is an extra filter because currently occupancies can outrun terminated leases
            if (lease.getStatus()!=LeaseStatus.TERMINATED) {
                for (Occupancy occupancy : lease.getOccupancies()) {
                    if (occupancy.getInterval().overlaps(budget.getInterval())) {
                        result.add(lease);
                        break;
                    }
                }
            }
        }
        return result;
    }

    public BudgetCalculationRun executeCalculationRun(final Lease lease, final Budget budget, final BudgetCalculationType type){
        BudgetCalculationRun run = budgetCalculationRunRepository.findOrCreateNewBudgetCalculationRun(lease, budget, type);
        if (run.getStatus()==Status.NEW) {
            createBudgetCalculationResults(run);
        }
        return run;
    }

    public void createBudgetCalculationResults(final BudgetCalculationRun run){

        run.removeCalculationResults();
        for (Partitioning partitioning : run.getBudget().getPartitionings()){
            for (Charge invoiceCharge : partitioning.getDistinctInvoiceCharges()){
                BudgetCalculationResult result = run.createCalculationResult(invoiceCharge);
                result.calculate();
            }
        }

    }

    public List<BudgetOverrideValue> calculateOverrideValues(final Lease lease, final Budget budget){
        List<BudgetOverrideValue> results = new ArrayList<>();
        for (BudgetOverride override : budgetOverrideRepository.findByLease(lease)) {
            results.addAll(override.findOrCreateValues(budget.getStartDate()));
        }
        return results;
    }

    public void removeNewOverrideValues(final Lease lease){
        for (BudgetOverride override : budgetOverrideRepository.findByLease(lease)) {
            for (BudgetOverrideValue value : override.getValues()){
                value.removeWithStatusNew();
            }
        }
    }

    public void assign(final Budget budget){
        for (BudgetCalculationRun run : budgetCalculationRunRepository.findByBudgetAndTypeAndStatus(budget, BudgetCalculationType.BUDGETED, Status.NEW)){
            for (BudgetCalculationResult resultForLease : run.getBudgetCalculationResults()){

                LocalDate itemStartDate = run.getLease().getStartDate().isAfter(budget.getStartDate()) ?
                        run.getLease().getStartDate() :
                        budget.getStartDate();

                LeaseItem leaseItem = findOrCreateLeaseItemForServiceChargeBudgeted(run.getLease(), resultForLease, itemStartDate);

                LeaseTermForServiceCharge leaseTerm = (LeaseTermForServiceCharge) leaseTermRepository.findOrCreateLeaseTermForInterval(leaseItem, new LocalDateInterval(itemStartDate, budget.getEndDate()));

                budgetCalculationResultLinkRepository.findOrCreateLink(resultForLease, leaseTerm);

                leaseTerm.setBudgetedValue(resultForLease.getValue());
            }

            run.finalizeRun();
        }
    }

    private LeaseItem findOrCreateLeaseItemForServiceChargeBudgeted(final Lease lease, final BudgetCalculationResult calculationResult, final LocalDate startDate){
        InvoicingFrequency frequency;
        PaymentMethod paymentMethod;
        LeaseItem itemToCopyFrom;

        // try to copy invoice frequency and payment method from a lease item
        itemToCopyFrom = lease.findFirstItemOfTypeAndCharge(LeaseItemType.SERVICE_CHARGE, calculationResult.getInvoiceCharge());
        if (itemToCopyFrom==null){
            itemToCopyFrom = lease.findFirstItemOfType(LeaseItemType.SERVICE_CHARGE);
        }
        if (itemToCopyFrom==null){
            if (lease.getItems().size()>0) {
                itemToCopyFrom = lease.getItems().first();
            }
        }
        if (itemToCopyFrom!=null){
            frequency = itemToCopyFrom.getInvoicingFrequency();
            paymentMethod = itemToCopyFrom.getPaymentMethod();
        } else {
            // this is the first item on the lease: so make some guess
            frequency = InvoicingFrequency.QUARTERLY_IN_ADVANCE;
            paymentMethod = PaymentMethod.DIRECT_DEBIT;
        }

        LeaseItem leaseItem = lease.findFirstItemOfTypeAndCharge(LeaseItemType.SERVICE_CHARGE_BUDGETED, calculationResult.getInvoiceCharge());
        if (leaseItem==null){
            leaseItem = lease.newItem(
                    LeaseItemType.SERVICE_CHARGE_BUDGETED,
                    LeaseAgreementRoleTypeEnum.LANDLORD, calculationResult.getInvoiceCharge(),
                    frequency,
                    paymentMethod,
                    startDate);
            leaseItem.setStatus(LeaseItemStatus.SUSPENDED);
        }
        return leaseItem;
    }

    public List<CalculationResultViewModel> getCalculationResults(final Budget budget){
        List<CalculationResultViewModel> results = new ArrayList<>();
        for (BudgetCalculationRun run : budgetCalculationRunRepository.findByBudgetAndType(budget, BudgetCalculationType.BUDGETED)){
            for (BudgetCalculationResult result : run.getBudgetCalculationResults()){
                CalculationResultViewModel vm = new CalculationResultViewModel(
                        run.getLease(),
                        result.getInvoiceCharge(),
                        run.getType()==BudgetCalculationType.BUDGETED ? result.getValue().add(result.getShortfall()) : BigDecimal.ZERO,
                        run.getType()==BudgetCalculationType.BUDGETED ? result.getValue() :BigDecimal.ZERO ,
                        run.getType()==BudgetCalculationType.BUDGETED ? result.getShortfall() : BigDecimal.ZERO,
                        run.getType()==BudgetCalculationType.ACTUAL ? result.getValue().add(result.getShortfall()) : BigDecimal.ZERO,
                        run.getType()==BudgetCalculationType.ACTUAL ? result.getValue() :BigDecimal.ZERO ,
                        run.getType()==BudgetCalculationType.ACTUAL ? result.getShortfall(): BigDecimal.ZERO
                );
                String unitString = run.getLease().getOccupancies().first().getUnit().getReference();
                if (run.getLease().getOccupancies().size()>1) {
                    boolean skip = true;
                    for (Occupancy occupancy : run.getLease().getOccupancies()){
                        if (skip){
                            skip = false;
                        } else {
                            unitString = unitString.concat(" | ").concat(occupancy.getUnit().getReference());
                        }
                    }
                }
                vm.setUnit(unitString);
                results.add(vm);
            }
        }
        return results;
    }

    public List<DetailedCalculationResultViewmodel> getDetailedCalculationResults(final Lease lease, final Budget budget, final BudgetCalculationType type){

        List<DetailedCalculationResultViewmodel> results = new ArrayList<>();

        BudgetCalculationRun runForLease = budgetCalculationRunRepository.findUnique(lease, budget, type);
        if (runForLease==null){return results;}

        for (BudgetCalculationResult result : runForLease.getBudgetCalculationResults()){

            // scenario: one override for incoming charge
            if (result.overrideValueForInvoiceCharge() != null){

                results.add(new DetailedCalculationResultViewmodel(
                        lease.primaryOccupancy().get().getUnit(),
                        "Override for total " + result.getInvoiceCharge().getDescription(),
                        result.getValue().add(result.getShortfall()),
                        result.getValue(),
                        result.getShortfall(),
                        null,
                        result.getInvoiceCharge()
                ));

            } else {

                for (BudgetCalculation calculation : result.getBudgetCalculations()) {

                    BigDecimal effectiveValueForIncomingCharge = calculation.getEffectiveValue();
                    BigDecimal shortFallForIncomingCharge = BigDecimal.ZERO;
                    BigDecimal valueInBudget = BigDecimal.ZERO;

                    DetailedCalculationResultViewmodel vm = new DetailedCalculationResultViewmodel(
                            calculation.getUnit(),
                            calculation.getIncomingCharge().getDescription(),
                            calculation.getEffectiveValue(),
                            valueInBudget,
                            effectiveValueForIncomingCharge,
                            shortFallForIncomingCharge,
                            calculation.getInvoiceCharge()
                    );

                    // set value in Budget
                    PartitionItem partitionItem = calculation.getPartitionItem();
                    BudgetItem budgetItem = calculation.getBudgetItem();
                    BigDecimal valueForBudgetItem = type == BudgetCalculationType.BUDGETED ? budgetItem.getBudgetedValue() : budgetItem.getAuditedValue();
                    valueInBudget = valueForBudgetItem;
                    vm.setTotalValueInBudget(valueInBudget);

                    // set possible overrides for incoming charge
                    for (BudgetOverrideValue overrideValue : result.getOverrideValues()) {
                        if (overrideValue.getBudgetOverride().getIncomingCharge() == calculation.getIncomingCharge() && overrideValue.getType() == type) {
                            effectiveValueForIncomingCharge = overrideValue.getValue().multiply(calculation.getPartitionItem().getPartitioning().getFractionOfYear());
                            shortFallForIncomingCharge = calculation.getEffectiveValue().subtract(effectiveValueForIncomingCharge);
                        }
                    }
                    if (effectiveValueForIncomingCharge != BigDecimal.ZERO) {
                        vm.setEffectiveValueForLease(effectiveValueForIncomingCharge);
                        vm.setShortfall(shortFallForIncomingCharge);
                    }
                    results.add(vm);
                }

            }

        }
        return results;
    }

    @Inject
    LeaseRepository leaseRepository;

    @Inject
    private LeaseTermRepository leaseTermRepository;

    @Inject
    private BudgetOverrideRepository budgetOverrideRepository;

    @Inject
    private BudgetCalculationRunRepository budgetCalculationRunRepository;

    @Inject
    private BudgetCalculationResultRepository budgetCalculationResultRepository;

    @Inject
    private BudgetCalculationResultLinkRepository budgetCalculationResultLinkRepository;

}
