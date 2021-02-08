package org.estatio.module.budgetassignment.dom;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.assertj.core.util.Lists;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.message.MessageService;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.asset.dom.Unit;
import org.estatio.module.asset.dom.UnitRepository;
import org.estatio.module.budget.dom.budget.Budget;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculation;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationRepository;
import org.estatio.module.budget.dom.budgetcalculation.BudgetCalculationType;
import org.estatio.module.budget.dom.budgetcalculation.InMemBudgetCalculation;
import org.estatio.module.budget.dom.budgetcalculation.Status;
import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationResult;
import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationResultLeaseTermLinkRepository;
import org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationResultRepository;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.lease.dom.InvoicingFrequency;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseTerm;
import org.estatio.module.lease.dom.LeaseTermForServiceCharge;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.lease.dom.occupancy.OccupancyRepository;

@DomainService(nature = NatureOfService.DOMAIN)
public class BudgetAssignmentService {

    public static Logger LOG = LoggerFactory.getLogger(BudgetAssignmentService.class);

    @Programmatic
    public List<BudgetCalculationResult> calculateResults(final Budget budget, final BudgetCalculationType type){

        List<BudgetCalculationResult> results = new ArrayList<>();
        for (Unit unit : unitRepository.findByProperty(budget.getProperty())) {
            results.addAll(calculatResultsForUnit(budget, type, unit));
        }
        return results;
    }

    @Programmatic
    public List<BudgetCalculationResult> calculatResultsForUnit(final Budget budget, final BudgetCalculationType type, final Unit unit) {

        List<BudgetCalculationResult> results = new ArrayList<>();
        final List<Occupancy> occupanciesForUnitDuringBudgetInterval = occupancyRepository.occupanciesByUnitAndInterval(unit, budget.getInterval());

        if (!occupanciesForUnitDuringBudgetInterval.isEmpty()) {

            if (overlappingOccupanciesFoundIn(occupanciesForUnitDuringBudgetInterval)) {
                String message = String.format("Overlapping occupancies found for unit %s", unit.getReference());
                message.concat(". No calculation results made for this unit.");
                messageService.warnUser(message);
                LOG.warn(message);
            } else {

                List<BudgetCalculation> calculationsForUnitAndType = budgetCalculationRepository.findByBudgetAndUnitAndType(budget, unit, type);
                List<Charge> invoiceChargesUsed = calculationsForUnitAndType.stream().map(c -> c.getInvoiceCharge()).distinct().collect(Collectors.toList());

                for (Occupancy occupancy : occupanciesForUnitDuringBudgetInterval) {

                    for (Charge charge : invoiceChargesUsed) {
                        BigDecimal value = BigDecimal.ZERO;
                        List<BudgetCalculation> calculationsForCharge = calculationsForUnitAndType.stream().filter(c -> c.getInvoiceCharge().equals(charge)).collect(Collectors.toList());
                        for (BudgetCalculation calc : calculationsForCharge) {
                            value = value.add(calc.getValue());
                        }
                        BudgetCalculationResult calcResult = budgetCalculationResultRepository.upsertBudgetCalculationResult(budget, occupancy, charge, type, value);
                        results.add(calcResult);
                    }

                }

                // finalize calculations
                calculationsForUnitAndType.stream().forEach(c -> c.setStatus(Status.ASSIGNED));

            }
        }

        return results;
    }

    boolean overlappingOccupanciesFoundIn(final List<Occupancy> occupancies){
        boolean overlappingOccupanciesFound = false;
        if (occupancies.size()>1) {
            List<LocalDateInterval> intervals = new ArrayList<>();
            for (Occupancy occupancy : occupancies){
                for (LocalDateInterval interval : intervals){
                    if (occupancy.getInterval().overlaps(interval)){
                        overlappingOccupanciesFound = true;
                        break;
                    }
                }
                intervals.add(occupancy.getInterval());
            }
        }
        return overlappingOccupanciesFound;
    }

    @Programmatic
    public void assignNonAssignedCalculationResultsToLeases(final Budget budget, final BudgetCalculationType budgetCalculationType) {
        final List<BudgetCalculationResult> calculationResultsForBudget = budgetCalculationResultRepository.findByBudget(budget);
        List<BudgetCalculationResult> nonAssignedResultsForType = calculationResultsForBudget.stream()
                .filter(r->budgetCalculationResultLeaseTermLinkRepository.findByBudgetCalculationResult(r).isEmpty())
                .filter(r->r.getType() == budgetCalculationType)
                .collect(Collectors.toList());
        List<Occupancy> distinctOccupanciesInResults = nonAssignedResultsForType.stream().map(r->r.getOccupancy()).distinct().collect(Collectors.toList());
        for (Occupancy occupancy : distinctOccupanciesInResults){
            List<BudgetCalculationResult> nonAssignedResultsForOccupancy = nonAssignedResultsForType.stream().filter(r->r.getOccupancy().equals(occupancy)).collect(Collectors.toList());
            assignNonAssignedCalculationResultsToLeaseFor(occupancy, nonAssignedResultsForOccupancy);
        }
    }

    /* Convenience for possible mixin on a lease */
    @Programmatic
    public void assignNonAssignedCalculationResultsToLeaseFor(final Lease lease, final Budget budget, final BudgetCalculationType budgetCalculationType){
        for (Occupancy occupancy : lease.getOccupancies()){
            List<BudgetCalculationResult> nonAssignedResultsForOccupancy = budgetCalculationResultRepository.findByBudget(budget).stream()
                    .filter(cr -> cr.getOccupancy().equals(occupancy))
                    .filter(r->budgetCalculationResultLeaseTermLinkRepository.findByBudgetCalculationResult(r).isEmpty())
                    .filter(r->r.getType()==budgetCalculationType)
                    .collect(Collectors.toList());
            assignNonAssignedCalculationResultsToLeaseFor(occupancy, nonAssignedResultsForOccupancy);
        }
    }

    @Programmatic
    void assignNonAssignedCalculationResultsToLeaseFor(final Occupancy occupancy, List<BudgetCalculationResult> nonAssignedResultsForOccupancyAndType){

        List<Charge> distinctInvoiceChargesForOccupancy = nonAssignedResultsForOccupancyAndType.stream().map(r->r.getInvoiceCharge()).distinct().collect(Collectors.toList());
        Lease lease = occupancy.getLease();

        if (nonAssignedResultsForOccupancyAndType.size() != distinctInvoiceChargesForOccupancy.size()){
            // this should not be possible
            String message = String.format("Multiple budget calculation results with same invoice charge found for occupancy %s.", occupancy.title());
            message.concat(String.format("The calculation results were not assigned to lease %s.", lease.getReference()));
            messageService.warnUser(message);
            LOG.warn(message);
            return;
        }

        for (BudgetCalculationResult result : nonAssignedResultsForOccupancyAndType){

            /*
                When type = BUDGETED we update all service charge terms found on lease items with charge corresponding to budgetcalculation result that are active during the budget period.
                - if no lease item is found we create one with a term for the budget period
                - if a term is found that exceeds the budget enddate, we will split the term
                - we need to support multiple lease items on the lease (for instance Quarterly and Monthly with same charge)
                - in order to support multiple occupancies on a lease, we need to check if the term is already linked for type BUDGETED
                  and make sure to add and not replace the value
            */

            if (result.getType()==BudgetCalculationType.BUDGETED &&
                    !Arrays.asList(
                            org.estatio.module.budget.dom.budget.Status.RECONCILING,
                            org.estatio.module.budget.dom.budget.Status.RECONCILED
                    )
                    .contains(result.getBudget().getStatus())){

                List<LeaseItem> serviceChargeItemsToUpdate = findExistingLeaseItemsOrCreateNewForServiceCharge(lease, result);
                serviceChargeItemsToUpdate.forEach(li->upsertLeaseTermForServiceCharge(li, result));

            }

            /*
                When type = AUDITED we update all service charge terms found on lease with lease items with charge corresponding to budgetcalculation result
                for leases that are active for the entire budget period or that have previous/next together covering the entire budget period.
                If not, we log a warning and do not assign. These are left for manual treatment for the moment.
                - if no lease item is found DO NOT create one and log a warning
                - if a term is found that exceeds the budget enddate, we will split the term
                - in order to support multiple occupancies on a lease, we need to check if the term is already linked for type AUDITED
                  and make sure to add and not replace the value
            */

            if (result.getType()==BudgetCalculationType.AUDITED){

                List<LeaseItem> serviceChargeItemsToUpdate = findLeaseItemsForServiceChargeToUpdateForAudited(lease, result);
                serviceChargeItemsToUpdate.forEach(li-> updateLeaseTermsForServiceCharge(li, result));

            }

        }

    }

    List<LeaseItem> findLeaseItemsForServiceChargeToUpdateForAudited(
            final Lease lease,
            final BudgetCalculationResult budgetCalculationResult) {

        if (budgetCalculationResult.leaseCoversBudgetInterval()){

            if (budgetCalculationResult.occupancyCoversLeaseEffectiveInterval()){
                return findExistingLeaseItemsForServiceCharge(lease, budgetCalculationResult);

            } else {
                String msg = String.format("Lease %s covers budget interval but the occupancy does not - handle manually", budgetCalculationResult.getOccupancy().getLease().getReference());
                messageService.warnUser(msg);
                LOG.warn(msg);
                return Lists.emptyList();
            }

        } else {

            if (budgetCalculationResult.occupanciesOfleaseAndLeaseRenewalsCoverBudgetInterval()){

                return findExistingLeaseItemsForServiceCharge(lease, budgetCalculationResult);

            } else {
                String msg = String.format("Lease %s has started or ended during budget interval and has/is no renewal - handle manually", budgetCalculationResult.getOccupancy().getLease().getReference());
                messageService.warnUser(msg);
                LOG.warn(msg);
                return Lists.emptyList();
            }

        }
    }



    List<LeaseItem> findExistingLeaseItemsForServiceCharge(final Lease lease, final BudgetCalculationResult result){
        return lease.findItemsOfType(LeaseItemType.SERVICE_CHARGE).stream()
                .filter(li -> li.getCharge().equals(result.getInvoiceCharge()))
                .filter(li -> li.getEffectiveInterval().overlaps(result.getBudget().getInterval()))
                .collect(Collectors.toList());
    }

    List<LeaseItem> findExistingLeaseItemsOrCreateNewForServiceCharge(
            final Lease lease,
            final BudgetCalculationResult result) {
        final List<LeaseItem> leaseItems = findExistingLeaseItemsForServiceCharge(lease, result);
        if (leaseItems.isEmpty()){
            LeaseItem itemToCopyFrom = findItemToCopyFrom(lease); // tries to copy invoice frequency and payment method from another lease item
            LeaseItem leaseItem = lease.newItem(
                    LeaseItemType.SERVICE_CHARGE,
                    LeaseAgreementRoleTypeEnum.LANDLORD,
                    result.getInvoiceCharge(),
                    itemToCopyFrom!=null ? itemToCopyFrom.getInvoicingFrequency() : InvoicingFrequency.QUARTERLY_IN_ADVANCE,
                    itemToCopyFrom!=null ? itemToCopyFrom.getPaymentMethod() : PaymentMethod.DIRECT_DEBIT,
                    result.getBudget().getStartDate());
            leaseItems.add(leaseItem);
        }
        return leaseItems;
    }

    LeaseItem findItemToCopyFrom(final Lease lease){
        LeaseItem itemToCopyFrom = lease.findFirstItemOfType(LeaseItemType.SERVICE_CHARGE);
        if (itemToCopyFrom==null){
            // then try rent item
            itemToCopyFrom = lease.findFirstItemOfType(LeaseItemType.RENT);
        }
        if (itemToCopyFrom==null && lease.getItems().size()>0) {
            // then try any item
            itemToCopyFrom = lease.getItems().first();
        }
        return itemToCopyFrom;
    }

    void upsertLeaseTermForServiceCharge(final LeaseItem serviceChargeItem, final BudgetCalculationResult result) {

        final Budget budget = result.getBudget();
        final LocalDate budgetStartDate = budget.getStartDate();
        final LocalDate budgetEndDate = budget.getEndDate();

        final List<LeaseTerm> overlappingTermsIfAny = serviceChargeItem.findTermsActiveDuring(budget.getInterval());

        if (overlappingTermsIfAny.isEmpty()) {
            serviceChargeItem.newTerm(budgetStartDate, budgetEndDate);
        }

        updateLeaseTermsForServiceCharge(serviceChargeItem, result);

    }

    void updateLeaseTermsForServiceCharge(final LeaseItem serviceChargeItem, final BudgetCalculationResult result) {
        final Budget budget = result.getBudget();
        final LocalDate budgetStartDate = budget.getStartDate();
        final LocalDate budgetEndDate = budget.getEndDate();

        final List<LeaseTerm> overlappingTermsIfAny = serviceChargeItem.findTermsActiveDuring(budget.getInterval());
        for (LeaseTerm overlappingTerm : overlappingTermsIfAny){
            LeaseTermForServiceCharge termToUpDate = (LeaseTermForServiceCharge) overlappingTerm;
            // split term if needed
            if (termToUpDate.getStartDate()==null || termToUpDate.getStartDate().isBefore(budgetStartDate)){
                // split on budget start date
                termToUpDate = (LeaseTermForServiceCharge) termToUpDate.split(budgetStartDate);
            }
            if (termToUpDate.getEndDate()==null || termToUpDate.getEndDate().isAfter(budgetEndDate)){
                // split term day after budget end date
                termToUpDate = (LeaseTermForServiceCharge) termToUpDate.split(budgetEndDate.plusDays(1));
            }
            budgetCalculationResultLeaseTermLinkRepository.findOrCreate(result, termToUpDate);
            recalculateTerm(termToUpDate, result.getType());
        }
    }

    void recalculateTerm(final LeaseTermForServiceCharge term, final BudgetCalculationType budgetCalculationType) {

        if (budgetCalculationType==BudgetCalculationType.BUDGETED) term.setBudgetedValue(null);
        if (budgetCalculationType == BudgetCalculationType.AUDITED) term.setAuditedValue(null);

        final List<BudgetCalculationResult> resultsForTermAndType = budgetCalculationResultLeaseTermLinkRepository.findByLeaseTerm(term)
                .stream()
                .map(l->l.getBudgetCalculationResult())
                .filter(bcr->bcr.getType()==budgetCalculationType)
                .collect(Collectors.toList());
        for (BudgetCalculationResult result : resultsForTermAndType){

            BigDecimal newValue;

            switch (result.getType()){
            case BUDGETED:
                BigDecimal oldBudgeted = term.getBudgetedValue();
                newValue = oldBudgeted!=null ? oldBudgeted.add(result.getValue()) : result.getValue();
                term.setBudgetedValue(newValue);
                break;

            case AUDITED:
                BigDecimal oldActual = term.getAuditedValue();
                newValue = oldActual!=null ? oldActual.add(result.getValue()) : result.getValue();
                term.setAuditedValue(newValue);
                break;
            }

        }
    }

    /**
     * This method assumes to be called AFTER a reconciliation of the budget has been made
     * @param budget
     * @param lease
     * @return
     */
    @Programmatic
    public List<BudgetCalculationResult> calculateAuditedResultsForLease(final Budget budget, final Lease lease){
        List<BudgetCalculationResult> result = new ArrayList<>();
        if (budget.getStatus()!= org.estatio.module.budget.dom.budget.Status.RECONCILED) return result;
        for (Occupancy occupancy : lease.getOccupancies()){
            // check if the occupancy effective interval contains budget interval
            // if so, there should be calculation results already ...
            if (occupancy.getEffectiveInterval().contains(budget.getInterval())) {

                result.addAll(budgetCalculationResultRepository
                        .findByBudgetAndOccupancyAndType(budget, occupancy, BudgetCalculationType.AUDITED));

            } else {
                if (occupancy.getEffectiveInterval().overlaps(budget.getInterval())){
                    final List<InMemBudgetCalculation> inMemCalcs = budgetService
                            .auditedCalculationsForBudgetAndUnitAndCalculationInterval(budget, occupancy.getUnit(),
                                    occupancy.getEffectiveInterval());
                    List<BudgetCalculation> calculations = new ArrayList<>();
                    inMemCalcs.forEach(c->{
                        calculations.add(budgetCalculationRepository.findOrCreateBudgetCalculation(c));
                    });
                    // TODO: create BudgetCalculationResults
                    // TODO: set calculations to ASSIGNED
                }
            }
        }
        return result;
    }

    @Inject UnitRepository unitRepository;

    @Inject OccupancyRepository occupancyRepository;

    @Inject BudgetCalculationRepository budgetCalculationRepository;

    @Inject MessageService messageService;

    @Inject BudgetCalculationResultRepository budgetCalculationResultRepository;

    @Inject BudgetCalculationResultLeaseTermLinkRepository budgetCalculationResultLeaseTermLinkRepository;

    @Inject BudgetService budgetService;

}
