package org.estatio.dom.budgetassignment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.dom.asset.Unit;
import org.estatio.dom.budgetassignment.calculationresult.BudgetCalculationResult;
import org.estatio.dom.budgetassignment.calculationresult.BudgetCalculationResultRepository;
import org.estatio.dom.budgetassignment.calculationresult.BudgetCalculationRun;
import org.estatio.dom.budgetassignment.calculationresult.BudgetCalculationRunRepository;
import org.estatio.dom.budgetassignment.override.BudgetOverride;
import org.estatio.dom.budgetassignment.override.BudgetOverrideRepository;
import org.estatio.dom.budgetassignment.override.BudgetOverrideValue;
import org.estatio.dom.budgetassignment.viewmodels.BudgetCalculationResultViewModel;
import org.estatio.dom.budgetassignment.viewmodels.DetailedBudgetCalculationResultViewmodel;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationRepository;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationService;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationType;
import org.estatio.dom.budgeting.budgetcalculation.BudgetCalculationViewmodel;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.budgeting.keytable.KeyTable;
import org.estatio.dom.budgeting.partioning.PartitionItem;
import org.estatio.dom.budgeting.partioning.Partitioning;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseRepository;
import org.estatio.dom.lease.LeaseStatus;
import org.estatio.dom.lease.Occupancy;

@DomainService(nature = NatureOfService.DOMAIN)
public class BudgetAssignmentService {

    public List<BudgetCalculationRun> calculateResultsForLeases(final Budget budget, final BudgetCalculationType type){
        List<BudgetCalculationRun> results = new ArrayList<>();

        for (Lease lease : leaseRepository.findByAssetAndActiveOnDate(budget.getProperty(), budget.getStartDate())) {
            // TODO: this is an extra filter because currently occupancies can outrun terminated leases
            if (lease.getStatus() != LeaseStatus.TERMINATED) {

                removeNewOverrideValues(lease);
                calculateOverrideValues(lease, budget);
                results.add(executeCalculationRun(lease, budget, type));

            }
        }

        return results;
    }

    public BudgetCalculationRun executeCalculationRun(final Lease lease, final Budget budget, final BudgetCalculationType type){
        BudgetCalculationRun run = budgetCalculationRunRepository.findOrCreateNewBudgetCalculationRun(lease, budget, type);
        createBudgetCalculationResults(run);
        return run;
    }

    public void createBudgetCalculationResults(final BudgetCalculationRun run){

        for (Partitioning partitioning : run.getBudget().getPartitionings()){
            for (Charge invoiceCharge : partitioning.getDistinctInvoiceCharges()){
                BudgetCalculationResult result = run.findOrCreateResult(invoiceCharge);
                result.calculate();
            }
        }

    }

    public List<BudgetOverrideValue> calculateOverrideValues(final Lease lease, final Budget budget){
        List<BudgetOverrideValue> results = new ArrayList<>();
        for (BudgetOverride override : budgetOverrideRepository.findByLease(lease)) {
            results.addAll(override.calculate(budget.getStartDate()));
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
        // TODO: implement
    }

    public List<DetailedBudgetCalculationResultViewmodel> getDetailedBudgetAssignmentResults(final Budget budget, final Lease lease){
        List<DetailedBudgetCalculationResultViewmodel> results = new ArrayList<>();

        for (Occupancy occupancy : lease.getOccupancies()) {
            if (occupancy.getInterval().overlaps(budget.getInterval())) {

                for (BudgetCalculationViewmodel calculationResult : calculationResults(budget, occupancy.getUnit())){

                    if (calculationResult.getCalculationType() == BudgetCalculationType.BUDGETED) {
                        results.add(
                                new DetailedBudgetCalculationResultViewmodel(
                                        occupancy.getUnit(),
                                        calculationResult.getPartitionItem().getBudgetItem().getCharge(),
                                        getRowLabelLastPart(calculationResult.getPartitionItem().getBudgetItem()),
                                        calculationResult.getValue(),
                                        calculationResult.getKeyItem().getKeyTable(),
                                        calculationResult.getPartitionItem().getCharge()
                                )
                        );
                    }

                }

            }
        }

        return results;
    }

    private String getRowLabelLastPart(final BudgetItem budgetItem){

        String concatString = new String();

        concatString = concatString
                .concat(" | budgeted ")
                .concat(budgetItem.getBudgetedValue().toString());

        List<RowLabelHelper> helpers = new ArrayList<>();
        for (PartitionItem partitionItem : budgetItem.getPartitionItems()){
            helpers.add(new RowLabelHelper(partitionItem.getPercentage(), partitionItem.getKeyTable()));
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

    public List<BudgetCalculationResultViewModel> getAssignmentResults(final Budget budget){
        List<BudgetCalculationResultViewModel> results = new ArrayList<>();
        for (Lease lease : leaseRepository.findLeasesByProperty(budget.getProperty())){
           results.addAll(getAssignmentResults(budget, lease));
        }
        return results;
    }

    private List<BudgetCalculationResultViewModel> getAssignmentResults(final Budget budget, final Lease lease){
        List<BudgetCalculationResultViewModel> results = new ArrayList<>();
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
                budgetCalculationService.getAllCalculations(budget).stream().filter(x -> x.getKeyItem().getUnit().equals(u)).collect(Collectors.toList())
        );
    }

    private List<BudgetCalculationResultViewModel> createFromCalculationResults(final Lease lease, final Unit unit, final List<BudgetCalculationViewmodel> calculationResultsForLease){
        List<BudgetCalculationResultViewModel> assignmentResults = new ArrayList<>();
        for (BudgetCalculationViewmodel calculationResult : calculationResultsForLease){
            List<BudgetCalculationResultViewModel> filteredByChargeAndKeyTable = assignmentResults.stream()
                    .filter(x -> x.getInvoiceCharge().equals(calculationResult.getPartitionItem().getCharge().getReference()))
                    .filter(x -> x.getKeyTable().equals(calculationResult.getPartitionItem().getKeyTable().getName()))
                    .collect(Collectors.toList());
            if (filteredByChargeAndKeyTable.size()>0){
                filteredByChargeAndKeyTable.get(0).add(calculationResult);
            } else {
                assignmentResults.add(new BudgetCalculationResultViewModel(
                    lease,
                    unit,
                    calculationResult.getKeyItem().getKeyTable(),
                    calculationResult.getPartitionItem().getCharge(),
                    calculationResult.getValue()
                ));
            }
        }
        return assignmentResults;
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
    private LeaseRepository leaseRepository;

    @Inject
    private BudgetOverrideRepository budgetOverrideRepository;

    @Inject
    private BudgetCalculationRunRepository budgetCalculationRunRepository;

    @Inject
    private BudgetCalculationResultRepository budgetCalculationResultRepository;

}
