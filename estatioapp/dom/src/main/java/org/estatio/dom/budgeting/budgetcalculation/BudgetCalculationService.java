package org.estatio.dom.budgeting.budgetcalculation;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.estatio.dom.budgeting.Distributable;
import org.estatio.dom.budgeting.DistributionService;
import org.estatio.dom.budgeting.allocation.BudgetItemAllocation;
import org.estatio.dom.budgeting.budget.Budget;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.budgeting.keyitem.KeyItem;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.lease.*;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

@DomainService(nature = NatureOfService.DOMAIN)
public class BudgetCalculationService {

    public List<BudgetCalculationResult> calculate(final Budget budget) {

        List<BudgetCalculationResult> result = new ArrayList<>();
        for (BudgetItem budgetItem : budget.getItems()) {

            result.addAll(calculate(budgetItem));

        }

        return result;
    }

    private List<BudgetCalculationResult> calculate(final BudgetItem budgetItem) {

        List<BudgetCalculationResult> result = new ArrayList<>();
        for (BudgetItemAllocation itemAllocation : budgetItem.getBudgetItemAllocations()) {

            result.addAll(calculate(itemAllocation));

        }

        return result;
    }

    private List<BudgetCalculationResult> calculate(final BudgetItemAllocation itemAllocation) {

        List<BudgetCalculationResult> results = new ArrayList<>();

        BigDecimal budgetedTotal = percentageOf(itemAllocation.getBudgetItem().getBudgetedValue(), itemAllocation.getPercentage());
        results.addAll(calculateForTotalAndType(itemAllocation, budgetedTotal, CalculationType.BUDGETED));

        if (itemAllocation.getBudgetItem().getAuditedValue() != null){
            BigDecimal auditedTotal = percentageOf(itemAllocation.getBudgetItem().getAuditedValue(), itemAllocation.getPercentage());
            results.addAll(calculateForTotalAndType(itemAllocation,auditedTotal,CalculationType.AUDITED));
        }

        return results;
    }

    private List<BudgetCalculationResult> calculateForTotalAndType(final BudgetItemAllocation itemAllocation, final BigDecimal total, final CalculationType calculationType) {

        List<Distributable> results = new ArrayList<>();

        BigDecimal keySum = itemAllocation.getKeyTable().getKeyValueMethod().keySum(itemAllocation.getKeyTable());

        for (KeyItem keyItem : itemAllocation.getKeyTable().getItems()) {

            BudgetCalculationResult calculationResult;

            // case all values in keyTable are zero
            if (keySum.compareTo(BigDecimal.ZERO) == 0) {
                calculationResult = new BudgetCalculationResult(
                        itemAllocation,
                        keyItem,
                        BigDecimal.ONE,
                        BigDecimal.ZERO,
                        calculationType);
            } else {
                calculationResult = new BudgetCalculationResult(
                        itemAllocation,
                        keyItem,
                        BigDecimal.ONE,
                        total.multiply(keyItem.getValue()).
                                divide(keySum, MathContext.DECIMAL64),
                        calculationType);

            }

            results.add(calculationResult);
        }

        DistributionService distributionService = new DistributionService();
        distributionService.distribute(results, total, 2);

        return (List<BudgetCalculationResult>) (Object) results;

    }

    public List<BudgetCalculationLink> assignBudgetCalculationsToLeases(final Budget budget) {
        List<BudgetCalculationLink> result = new ArrayList<>();

        for (Charge targetCharge : budget.getTargetCharges()) {

            List<BudgetCalculation> calculationsForCharge = budgetCalculationRepository.findByBudgetAndCharge(budget, targetCharge);

            for (Occupancy occupancy : budget.getOccupanciesInBudgetInterval()) {

                BigDecimal totalBudgetedValueForChargeAndOcupancy = BigDecimal.ZERO;
                BigDecimal totalAuditedValueForChargeAndOccupancy = BigDecimal.ZERO;

                for (BudgetCalculation calculation : calculationsForCharge) {

                    if (calculation.getKeyItem().getUnit() == occupancy.getUnit()) {

                        if (calculation.getCalculationType() == CalculationType.BUDGETED ) {
                            totalBudgetedValueForChargeAndOcupancy = totalBudgetedValueForChargeAndOcupancy.add(calculation.getValue());
                        }

                        if (calculation.getCalculationType() == CalculationType.AUDITED ) {
                            totalAuditedValueForChargeAndOccupancy = totalAuditedValueForChargeAndOccupancy.add(calculation.getValue());
                        }

                        LeaseTermForServiceCharge term = updateOrCreateLeaseTermForServiceCharge(
                                totalBudgetedValueForChargeAndOcupancy,
                                totalAuditedValueForChargeAndOccupancy,
                                budget,
                                targetCharge,
                                occupancy.getLease()
                        );

                        if (term != null) {
                            result.add(budgetCalculationLinkRepository.findOrCreateBudgetCalculationLink(calculation, term));
                        }

                    }

                }

            }

        }

        return result;
    }

    private BigDecimal percentageOf(final BigDecimal value, final BigDecimal percentage) {
        return value
                .multiply(percentage)
                .divide(new BigDecimal(100), MathContext.DECIMAL64);
    }

    private LeaseTermForServiceCharge updateOrCreateLeaseTermForServiceCharge(
            final BigDecimal budgetedValue,
            final BigDecimal auditedValue,
            final Budget budget,
            final Charge charge,
            final Lease lease){
        LeaseItem leaseItem = leaseItems.findByLeaseAndTypeAndCharge(lease,LeaseItemType.SERVICE_CHARGE_BUDGETED, charge);
        if (leaseItem != null){

            LeaseTermForServiceCharge term = (LeaseTermForServiceCharge) leaseItem.findTerm(budget.getStartDate());
            if (term == null) {
                term = (LeaseTermForServiceCharge) leaseItem.newTerm(
                        budget.getStartDate(),
                        budget.getEndDate());
            }
            term.setBudgetedValue(budgetedValue);
            term.setAuditedValue(auditedValue);
            term.setStartDate(budget.getStartDate());
            term.setEndDate(budget.getEndDate());
            term.setFrequency(LeaseTermFrequency.NO_FREQUENCY);
            return term;
        } else {
            return null; // case no budgetItem for service charge is found
        }
    }

    @Inject
    private BudgetCalculationRepository budgetCalculationRepository;

    @Inject
    private BudgetCalculationLinkRepository budgetCalculationLinkRepository;

    @Inject
    private LeaseItems leaseItems;

}
