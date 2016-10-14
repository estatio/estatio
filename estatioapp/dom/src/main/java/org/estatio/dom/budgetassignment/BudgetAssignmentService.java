package org.estatio.dom.budgetassignment;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.dom.asset.Unit;
import org.estatio.dom.budgetassignment.viewmodels.BudgetAssignmentResult;
import org.estatio.dom.budgeting.allocation.BudgetItemAllocation;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculation;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationRepository;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationResult;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationService;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationStatus;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseRepository;
import org.estatio.dom.lease.LeaseStatus;
import org.estatio.dom.lease.Occupancy;
import org.estatio.dom.lease.OccupancyRepository;
import org.incode.module.base.dom.valuetypes.LocalDateInterval;

@DomainService(nature = NatureOfService.DOMAIN)
public class BudgetAssignmentService {


    public List<BudgetAssignmentResult> getAssignmentResults(final Budget budget){
        List<BudgetAssignmentResult> results = new ArrayList<>();
        for (Lease lease : leaseRepository.findLeasesByProperty(budget.getProperty())){
            // TODO: this is an extra filter because currently occupancies can outrun terminated leases
            if (lease.getStatus() != LeaseStatus.TERMINATED) {

                List<BudgetCalculationResult> calculationResultsForLease = new ArrayList<>();
                for (Occupancy occupancy : lease.getOccupancies()) {
                    if (occupancy.getInterval().overlaps(budget.getInterval())) {
                        calculationResultsForLease.addAll(calculationResults(budget, occupancy.getUnit()));
                    }
                    results.addAll(createFromCalculationResults(lease, occupancy.getUnit(), calculationResultsForLease));
                }

            }
        }
        return results;
    }

    private List<BudgetCalculationResult> calculationResults(final Budget budget, final Unit u){
        return Lists.newArrayList(
                budgetCalculationService.getCalculatedResults(budget).stream().filter(x -> x.getKeyItem().getUnit().equals(u)).collect(Collectors.toList())
        );
    }

    private List<BudgetAssignmentResult> createFromCalculationResults(final Lease lease, final Unit unit, final List<BudgetCalculationResult> calculationResultsForLease){
        List<BudgetAssignmentResult> assignmentResults = new ArrayList<>();
        for (BudgetCalculationResult calculationResult : calculationResultsForLease){
            List<BudgetAssignmentResult> filteredByCharge = assignmentResults.stream().filter(x -> x.getInvoiceCharge().equals(calculationResult.getBudgetItemAllocation().getCharge().getReference())).collect(Collectors.toList());
            if (filteredByCharge.size()>0){
                filteredByCharge.get(0).add(calculationResult);
            } else {
                assignmentResults.add(new BudgetAssignmentResult(
                    lease,
                    unit,
                    calculationResult.getBudgetItemAllocation().getCharge(),
                    calculationResult.getValue()
                ));
            }
        }
        return assignmentResults;
    }


    public List<BudgetCalculationLink> assignBudgetCalculations(final Budget budget) {

        removeCurrentlyAssignedCalculations(budget);

        List<BudgetCalculationLink> result = new ArrayList<>();

        for (Charge invoiceCharge : budget.getInvoiceCharges()) {

            List<BudgetCalculation> calculationsForCharge = budgetCalculationRepository.findByBudgetAndCharge(budget, invoiceCharge);

            for (Occupancy occupancy : occupancyRepository.occupanciesByPropertyAndInterval(budget.getProperty(), budget.getInterval())) {

                List<BudgetCalculation> budgetCalculationsForOccupancy = calculationsForOccupancy(calculationsForCharge, occupancy);

                // find or create service charge item
                if (budgetCalculationsForOccupancy.size()>0){

                    ServiceChargeItem serviceChargeItem = serviceChargeItemRepository.findOrCreateServiceChargeItem(occupancy, invoiceCharge);

                }


            }

        }

        return result;
    }

    private void removeCurrentlyAssignedCalculations(final Budget budget) {
        for (BudgetCalculation calculation : budgetCalculationRepository.findByBudgetAndStatus(budget, BudgetCalculationStatus.ASSIGNED)){

            for (BudgetCalculationLink link : budgetCalculationLinkRepository.findByBudgetCalculation(calculation)){
                link.remove();
            }

            calculation.remove();
        }
    }

    private List<BudgetCalculation> calculationsForOccupancy(final List<BudgetCalculation> calculationList, final Occupancy occupancy){
        List<BudgetCalculation> result = new ArrayList<>();

        for (BudgetCalculation budgetCalculation : calculationList){

            if (budgetCalculation.getKeyItem().getUnit().equals(occupancy.getUnit())){
                result.add(budgetCalculation);
            }
        }

        return result;
    }

    public BigDecimal getShortFallAmountBudgeted(final Budget budget){
        return getShortFall(budget).getBudgetedShortFall().setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal getShortFallAmountAudited(final Budget budget){
        return getShortFall(budget).getAuditedShortFall().setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal getShortFallAmount(final BudgetCalculation budgetCalculation){
        return getShortFall(budgetCalculation).getShortFall(budgetCalculation.getCalculationType());
    }

    private ShortFall getShortFall(final Budget budget){
        ShortFall shortFall = new ShortFall();
        for (BudgetItem item : budget.getItems()){
            shortFall = shortFall.add(getShortFall(item));
        }
        return shortFall;
    }

    private ShortFall getShortFall(final BudgetItem budgetItem){
        ShortFall shortFall = new ShortFall();
        for (BudgetItemAllocation allocation : budgetItem.getBudgetItemAllocations()){
            shortFall = shortFall.add(getShortFallForTemporaryCalculations(allocation));
        }
        return shortFall;
    }

    private ShortFall getShortFallForTemporaryCalculations(final BudgetItemAllocation allocation){
        ShortFall shortFall = new ShortFall();
        List<BudgetCalculation> calculationsForAllocation = budgetCalculationRepository.findByBudgetItemAllocationAndStatus(allocation, BudgetCalculationStatus.TEMPORARY);
        for (BudgetCalculation calculation : calculationsForAllocation){
            shortFall = shortFall.add(getShortFall(calculation));
        }
        return shortFall;
    }

    ShortFall getShortFall(final BudgetCalculation budgetCalculation){

        BigDecimal shortFallAmount = BigDecimal.ZERO;
        ShortFall shortFall = new ShortFall();

        List<Occupancy> associatedOccupancies = associatedOccupancies(budgetCalculation);
        if (associatedOccupancies.size()>0){

            BigDecimal recoverableAmountForCalculation = BigDecimal.ZERO;
            for (Occupancy occupancy : associatedOccupancies){

                recoverableAmountForCalculation = recoverableAmountForCalculation.add(recoverableAmountForOccupancy(occupancy, budgetCalculation));

            }
            shortFallAmount = shortFallAmount.add(budgetCalculation.getValueForBudgetPeriod().subtract(recoverableAmountForCalculation));

        } else {

            shortFallAmount = shortFallAmount.add(budgetCalculation.getValueForBudgetPeriod());

        }

        return shortFall.add(shortFallAmount, budgetCalculation.getCalculationType());
    }

    BigDecimal recoverableAmountForOccupancy(final Occupancy occupancy, final BudgetCalculation calculation){
        LocalDateInterval budgetInterval = calculation.getBudget().getInterval();
        BigDecimal numberOfDaysInBudgetInterval = BigDecimal.valueOf(budgetInterval.days());
        BigDecimal numberOfDaysInOccupancyIntervalOverlap = BigDecimal.valueOf(occupancy.getInterval().overlap(budgetInterval).days());
        BigDecimal factor = numberOfDaysInOccupancyIntervalOverlap.divide(numberOfDaysInBudgetInterval, MathContext.DECIMAL64);
        return calculation.getValueForBudgetPeriod().multiply(factor);
    }

    List<Occupancy> associatedOccupancies(final BudgetCalculation calculation){
        return occupancyRepository.occupanciesByUnitAndInterval(calculation.getKeyItem().getUnit(), calculation.getBudget().getInterval());
    }


    @Inject
    BudgetCalculationRepository budgetCalculationRepository;

    @Inject
    private BudgetCalculationService budgetCalculationService;

    @Inject
    private BudgetCalculationLinkRepository budgetCalculationLinkRepository;

    @Inject
    private OccupancyRepository occupancyRepository;

    @Inject
    private ServiceChargeItemRepository serviceChargeItemRepository;

    @Inject
    private LeaseRepository leaseRepository;

}
