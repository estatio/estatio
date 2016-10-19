package org.estatio.dom.budgetassignment;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.dom.asset.Unit;
import org.estatio.dom.budgetassignment.viewmodels.BudgetAssignmentResult;
import org.estatio.dom.budgetassignment.viewmodels.DetailedBudgetAssignmentResult;
import org.estatio.dom.budgeting.allocation.BudgetItemAllocation;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculation;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationRepository;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationService;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationStatus;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationType;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationViewmodel;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.budgeting.keytable.KeyTable;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseRepository;
import org.estatio.dom.lease.LeaseStatus;
import org.estatio.dom.lease.Occupancy;
import org.estatio.dom.lease.OccupancyRepository;

@DomainService(nature = NatureOfService.DOMAIN)
public class BudgetAssignmentService {

    public List<DetailedBudgetAssignmentResult> getDetailedBudgetAssignmentResults(final Budget budget, final Lease lease){
        List<DetailedBudgetAssignmentResult> results = new ArrayList<>();

        for (Occupancy occupancy : lease.getOccupancies()) {
            if (occupancy.getInterval().overlaps(budget.getInterval())) {

                for (BudgetCalculationViewmodel calculationResult : calculationResults(budget, occupancy.getUnit())){

                    results.add(
                            new DetailedBudgetAssignmentResult(
                                    occupancy.getUnit(),
                                    calculationResult.getBudgetItemAllocation().getBudgetItem().getCharge(),
                                    getRowLabelLastPart(calculationResult.getBudgetItemAllocation().getBudgetItem()),
                                    calculationResult.getValue(),
                                    calculationResult.getKeyItem().getKeyTable(),
                                    calculationResult.getBudgetItemAllocation().getCharge()
                            )
                    );

                }

            }
        }

        return results;
    }

    private BigDecimal getTotalBudgetedValue(final BudgetItem budgetItem){
        BigDecimal returnValue = BigDecimal.ZERO;
        List<BudgetCalculationViewmodel> resultsForItem =
                budgetCalculationService.getCalculations(budgetItem.getBudget()).stream().filter(x -> x.getBudgetItemAllocation().getBudgetItem().equals(budgetItem)).collect(Collectors.toList()
        );
        for (BudgetCalculationViewmodel bcResult : resultsForItem){
            if (bcResult.getValue() != null && bcResult.getCalculationType() == BudgetCalculationType.BUDGETED) {
                returnValue = returnValue.add(bcResult.getValue());
            }
        }
        return returnValue.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private String getRowLabelLastPart(final BudgetItem budgetItem){

        String concatString = new String();

        concatString = concatString
                .concat(" | budgeted ")
                .concat(budgetItem.getBudgetedValue().toString());

        List<BudgetItemAllocation> sortedAllocations = new ArrayList<>(budgetItem.getBudgetItemAllocations());

        List<RowLabelHelper> helpers = new ArrayList<>();
        for (BudgetItemAllocation allocation : budgetItem.getBudgetItemAllocations()){
            helpers.add(new RowLabelHelper(allocation.getPercentage(), allocation.getKeyTable()));
        }
        Collections.sort(helpers);
        for (RowLabelHelper helper : helpers){
            concatString =
                    concatString
                    .concat(" | ")
                    .concat(helper.percentage)
                    .concat(helper.keyTableName);
        }
        return concatString;

    }

    public List<BudgetAssignmentResult> getAssignmentResults(final Budget budget){
        List<BudgetAssignmentResult> results = new ArrayList<>();
        for (Lease lease : leaseRepository.findLeasesByProperty(budget.getProperty())){
           results.addAll(getAssignmentResults(budget, lease));
        }
        return results;
    }

    private List<BudgetAssignmentResult> getAssignmentResults(final Budget budget, final Lease lease){
        List<BudgetAssignmentResult> results = new ArrayList<>();
        // TODO: this is an extra filter because currently occupancies can outrun terminated leases
        if (lease.getStatus() != LeaseStatus.TERMINATED) {
            for (Occupancy occupancy : lease.getOccupancies()) {
                List<BudgetCalculationViewmodel> calculationResultsForOccupancy = new ArrayList<>();
                if (occupancy.getInterval().overlaps(budget.getInterval())) {
                    calculationResultsForOccupancy.addAll(calculationResults(budget, occupancy.getUnit()));
                }
                results.addAll(createFromCalculationResults(lease, occupancy.getUnit(), calculationResultsForOccupancy));
            }
        }
        return results;
    }

    private List<BudgetCalculationViewmodel> calculationResults(final Budget budget, final Unit u){
        return Lists.newArrayList(
                budgetCalculationService.getCalculations(budget).stream().filter(x -> x.getKeyItem().getUnit().equals(u)).collect(Collectors.toList())
        );
    }

    private List<BudgetAssignmentResult> createFromCalculationResults(final Lease lease, final Unit unit, final List<BudgetCalculationViewmodel> calculationResultsForLease){
        List<BudgetAssignmentResult> assignmentResults = new ArrayList<>();
        for (BudgetCalculationViewmodel calculationResult : calculationResultsForLease){
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


    private class RowLabelHelper implements Comparable<RowLabelHelper> {

        RowLabelHelper(
                final BigDecimal percentage,
                final KeyTable keyTable){
            this.percentage = percentage.setScale(2, BigDecimal.ROUND_HALF_UP).toString().concat(" % ");
            this.keyTableName = keyTable.getName();
        }

        String percentage;

        String keyTableName;

        @Override public int compareTo(final RowLabelHelper o) {
            return this.keyTableName.compareTo(o.keyTableName);
        }

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
