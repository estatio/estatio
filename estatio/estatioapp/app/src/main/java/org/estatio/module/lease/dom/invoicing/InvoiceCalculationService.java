/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.module.lease.dom.invoicing;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.commons.lang3.ObjectUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.incode.module.base.dom.valuetypes.AbstractInterval.IntervalEnding;
import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.base.dom.UdoDomainService;
import org.estatio.module.lease.dom.amendments.PersistedCalculationResultRepository;
import org.estatio.module.lease.dom.settings.LeaseInvoicingSettingsService;
import org.estatio.module.base.platform.docfragment.FragmentRenderService;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.invoice.dom.InvoiceRunType;
import org.estatio.module.invoice.dom.InvoicingInterval;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemStatus;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.LeaseStatus;
import org.estatio.module.lease.dom.LeaseTerm;
import org.estatio.module.lease.dom.LeaseTermValueType;
import org.estatio.module.lease.dom.invoicing.ssrs.InvoiceItemAttributesVM;

//@RequestScoped  // TODO: this should be @RequestScoped, I think, since has a field
@DomainService(menuOrder = "50", nature = NatureOfService.DOMAIN)
public class InvoiceCalculationService extends UdoDomainService<InvoiceCalculationService> {

    public InvoiceCalculationService() {
        super(InvoiceCalculationService.class);
    }


    /**
     * class to store the result a calculation
     */
    public static class CalculationResult {
        private static final BigDecimal ZERO = new BigDecimal("0.00");
        private BigDecimal value;

        private InvoicingInterval invoicingInterval;
        private LocalDateInterval effectiveInterval;

        public CalculationResult() {
            this(null);
        }

        public CalculationResult(final InvoicingInterval interval) {
            this(interval, interval.asLocalDateInterval(), ZERO);
        }

        public CalculationResult(
                final InvoicingInterval interval,
                final LocalDateInterval effectiveInterval,
                final BigDecimal value) {
            this.invoicingInterval = interval;
            this.effectiveInterval = effectiveInterval;
            this.value = value;
        }

        public BigDecimal value() {
            return value;
        }

        public InvoicingInterval invoicingInterval() {
            return invoicingInterval;
        }

        public LocalDateInterval effectiveInterval() {
            return effectiveInterval;
        }

        @Override
        public String toString() {
            return invoicingInterval.toString().concat(" : ").concat(value.toString());
        }
    }

    /**
     * Utility class for collection of calculation results
     */
    public static class CalculationResultsUtil {
        public static BigDecimal sum(final List<CalculationResult> list) {
            BigDecimal sum = BigDecimal.ZERO;
            if (list == null) {
                return sum;
            }
            for (CalculationResult result : list) {
                sum = sum.add(result.value());
            }
            return sum;
        }
    }

    private LocalDate systemEpochDate() {
        final LocalDate defaultEpochDate = new LocalDate(1980, 1, 1);
        if (leaseInvoicingSettingsService != null) {
            final LocalDate date = leaseInvoicingSettingsService.fetchEpochDate();
            return date == null ? defaultEpochDate : date;
        }
        return defaultEpochDate;
    }

    private String interactionId;

    private void startInteraction(final String parameters) {
        if (interactionId == null) {
            interactionId = LocalDateTime.now().toString().concat(" - ").concat(parameters);
        }
    }

    private void endInteraction() {
        interactionId = null;
    }

    @Programmatic
    public String calculateAndInvoice(InvoiceCalculationParameters parameters) {
        String lastInteractionId = null;
        invoiceForLeaseRepository.removeRuns(parameters);
        try {
            startInteraction(parameters.toString());
            final List<Lease> leases = parameters.leases();
            for (Lease lease : leases.size() == 0 ? leaseRepository.findLeasesByProperty(parameters.property()) : leases) {
                lease.verifyUntil(parameters.dueDateRange().endDateExcluding());
                if (lease.getStatus() != LeaseStatus.SUSPENDED) {
                    SortedSet<LeaseItem> leaseItems =
                            parameters.leaseItem() == null ?
                                    lease.getItems() :
                                    new TreeSet<>(Arrays.asList(parameters.leaseItem()));
                    for (LeaseItem leaseItem : leaseItems) {
                        if (!leaseItem.getStatus().equals(LeaseItemStatus.SUSPENDED) && leaseItem.getInvoicedBy().equals(
                                LeaseAgreementRoleTypeEnum.LANDLORD)) {
                            //TODO: We only filter the Landlords
                            if (parameters.leaseItemTypes() == null || parameters.leaseItemTypes().contains(leaseItem.getType())) {
                                SortedSet<LeaseTerm> leaseTerms =
                                        parameters.leaseTerm() == null ?
                                                leaseItem.getTerms() :
                                                new TreeSet<>(Arrays.asList(parameters.leaseTerm()));
                                for (LeaseTerm leaseTerm : leaseTerms) {
                                    final List<CalculationResult> results;
                                    results = calculateDueDateRange(leaseTerm, parameters);
                                    if (lease.getStatus()==LeaseStatus.PREVIEW){
                                        // for PREVIEW we persist results only; also we filter results with 0 value
                                        persistedCalculationResultRepository.deleteIfAnyAndRecreate(
                                                results.stream().filter(r->r.value().compareTo(BigDecimal.ZERO)!=0).collect(
                                                        Collectors.toList()),
                                                leaseTerm);
                                    } else {
                                        createInvoiceItems(leaseTerm, parameters,
                                                results);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } finally {
            lastInteractionId = interactionId;
            endInteraction();
        }
        return lastInteractionId;
    }

    /**
     * Calculates a term with a given invoicing frequency
     */
    @Programmatic
    public List<CalculationResult> calculateDueDateRange(
            final LeaseTerm leaseTerm,
            final InvoiceCalculationParameters parameters) {
        final LocalDateInterval dueDateRangeInterval =
                parameters.invoiceRunType().equals(InvoiceRunType.RETRO_RUN) &&
                        leaseTerm.getLeaseItem().getLease().getStartDate().compareTo(parameters.dueDateRange().startDate()) < 0 ?
                        new LocalDateInterval(leaseTerm.getLeaseItem().getLease().getStartDate(), parameters.dueDateRange().endDateExcluding(), IntervalEnding.EXCLUDING_END_DATE) :
                        parameters.dueDateRange();
        // TODO: As a result of EST-413 the check for 'termInterval != null &&
        // termInterval.isValid()' is removed because this is blocking the
        // calculation of periods outside the interval of the leases. As a
        // result the invoice calculation will be more eager so improving
        // performance, EST-315, should get some attention.
        if (dueDateRangeInterval.isValid()) {
            final List<InvoicingInterval> intervals = leaseTerm.getLeaseItem().getInvoicingFrequency().intervalsInDueDateRange(
                    dueDateRangeInterval,
                    leaseTerm.getInterval());

            final LocalDate dueDateForCalculation
                    = parameters.dueDateRange().endDateExcluding().minusDays(1);

            return calculateTerm(leaseTerm, intervals);
        }
        return Lists.newArrayList();
    }

    /**
     * Calculates a term for a given interval
     *
     * @param term
     * @param interval
     * @return
     */
    @Programmatic
    public List<CalculationResult> calculateDateRange(
            final LeaseTerm term,
            final LocalDateInterval interval) {
        if (!interval.isOpenEnded() && interval.isValid()) {
            return calculateTerm(term, term.getLeaseItem().getInvoicingFrequency().intervalsInRange(interval));
        }
        return Lists.newArrayList();
    }

    /**
     * Calculates a term for a given list of invoicing intervals
     *
     * @param leaseTerm
     * @param intervals
     * @return
     */
    @Programmatic
    public List<CalculationResult> calculateTerm(
            final LeaseTerm leaseTerm,
            final List<InvoicingInterval> intervals) {
        final List<CalculationResult> results2 = Lists.newArrayList();
        if (!intervals.isEmpty()) {
            LocalDate dueDateForCalculation = intervals.get(intervals.size() - 1).dueDate();
            for (final InvoicingInterval invoicingInterval : intervals) {
                final LocalDate epochDate = ObjectUtils.firstNonNull(leaseTerm.getLeaseItem().getEpochDate(), systemEpochDate());
                if (!invoicingInterval.dueDate().isBefore(epochDate)) {
                    final LocalDateInterval effectiveInterval = invoicingInterval.asLocalDateInterval().overlap(leaseTerm.getEffectiveInterval());
                    if (effectiveInterval == null) {
                        results2.add(new CalculationResult(invoicingInterval));
                    } else {
                        final BigDecimal overlapDays = new BigDecimal(effectiveInterval.days());
                        final BigDecimal frequencyDays = new BigDecimal(invoicingInterval.days());
                        final BigDecimal rangeFactor = frequencyDays.equals(BigDecimal.ZERO) ? BigDecimal.ZERO : overlapDays.divide(frequencyDays, MathContext.DECIMAL64);
                        final BigDecimal annualFactor = leaseTerm.getLeaseItem().getInvoicingFrequency().annualMultiplier();
                        final CalculationResult calculationResult = new CalculationResult(
                                invoicingInterval,
                                effectiveInterval,
                                calculateValue(rangeFactor, annualFactor, leaseTerm.valueForDate(dueDateForCalculation), leaseTerm.valueType())
                        );
                        results2.add(calculationResult);
                    }
                }
            }
        }
        return results2;
    }

    /**
     * Multiplies a value with the range and annual factors
     */
    private BigDecimal calculateValue(
            final BigDecimal rangeFactor,
            final BigDecimal annualFactor,
            final BigDecimal value,
            final LeaseTermValueType valueType) {
        if (valueType == LeaseTermValueType.FIXED) {
            // If it's fixed we don't care about the factors, always return the full value
            // TODO: offload this responsibility to the lease term
            return value.setScale(2, RoundingMode.HALF_UP);
        }
        if (value != null && annualFactor != null && rangeFactor != null) {
            return value.multiply(annualFactor)
                    .multiply(rangeFactor)
                    .setScale(2, RoundingMode.HALF_UP);
        }
        return new BigDecimal("0.00");
    }

    /**
     * Calculates an invoice item with the difference between the already
     * invoiced and calculated value.
     */
    void createInvoiceItems(
            final LeaseTerm leaseTerm,
            final InvoiceCalculationParameters parameters,
            final List<CalculationResult> results) {

        for (CalculationResult result : results) {
            // TODO: this is a hack to speed up processing by ignoring zero
            // values on a normal run
            if (result.value().compareTo(BigDecimal.ZERO) != 0 || parameters.invoiceRunType().equals(InvoiceRunType.RETRO_RUN)) {
                final LocalDateInterval invoicingInterval = result.invoicingInterval().asLocalDateInterval();
                BigDecimal invoicedValue = invoiceItemForLeaseRepository.invoicedValue(leaseTerm, invoicingInterval);
                BigDecimal newValue = result.value().subtract(invoicedValue);

                //
                LocalDateInterval calculationInterval = result.effectiveInterval();
                LocalDateInterval effectiveInterval = calculationInterval;
                Boolean adjustment = false;
                if (newValue.compareTo(BigDecimal.ZERO) != 0) {
                    if (invoicedValue.compareTo(BigDecimal.ZERO) != 0) {
                        // Has been invoiced before
                        if (invoiceItemForLeaseRepository.findByLeaseTermAndEffectiveInterval(leaseTerm, calculationInterval).size() > 0) {
                            // this exact period has been invoiced before so it is an adjusment
                            adjustment = true;
                        } else {
                            //there is new calculated amount which is caused by tinkering the dates
                            effectiveInterval = attemptToCalculateRightSideLeftover(invoicingInterval, calculationInterval);
                       }
                    }

                    InvoiceItemForLease invoiceItem =
                            invoiceItemForLeaseRepository.createUnapprovedInvoiceItem(
                                    leaseTerm,
                                    invoicingInterval,
                                    calculationInterval,
                                    effectiveInterval,
                                    parameters.invoiceDueDate(),
                                    interactionId);
                    invoiceItem.setNetAmount(newValue);
                    invoiceItem.setQuantity(BigDecimal.ONE);
                    LeaseItem leaseItem = leaseTerm.getLeaseItem();
                    Charge charge = leaseItem.getCharge();
                    invoiceItem.setCharge(charge);

                    invoiceItem.setTax(leaseItem.getEffectiveTax());

                    final InvoiceItemAttributesVM vm = new InvoiceItemAttributesVM(invoiceItem);
                    final String description = fragmentRenderService.render(vm, "description");
                    invoiceItem.setDescription(description);

                    invoiceItem.verify();
                    invoiceItem.setAdjustment(adjustment);
                }
            }
        }
    }

    // //////////////////////////////////////

    @javax.inject.Inject
    FragmentRenderService fragmentRenderService;

    @Inject
    LeaseInvoicingSettingsService leaseInvoicingSettingsService;

    @Inject
    private InvoiceForLeaseRepository invoiceForLeaseRepository;

    @Inject
    private InvoiceItemForLeaseRepository invoiceItemForLeaseRepository;

    @Inject
    private LeaseRepository leaseRepository;

    @Inject
    PersistedCalculationResultRepository persistedCalculationResultRepository;


    public static LocalDateInterval attemptToCalculateRightSideLeftover(final LocalDateInterval ldi1, final LocalDateInterval ldi2){
        // Do not try to understand this. Consult Johan or Jeroen before reading further.

        // In case you really want to know:

        // When the two interval are the same we don't subtract
        if(ldi1.equals(ldi2)){
            return ldi1;
        }

        // You've made it this far! Great. Now the magic kicks in. See the test for what is does, it's too hard to explain.
        final LocalDateInterval ldiNew = LocalDateInterval.excluding(
                ldi2 == null || ldi2.endDateExcluding() == null ? ldi1.startDate() : ldi2.endDateExcluding() ,
                ldi1.endDateExcluding());
        if (ldiNew.isValid()){
            return ldiNew;
        }
        return null;
    }


}
