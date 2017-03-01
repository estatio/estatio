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
package org.estatio.dom.lease.invoicing;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

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

import org.estatio.dom.UdoDomainService;
import org.estatio.dom.appsettings.LeaseInvoicingSettingsService;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.invoice.InvoiceRunType;
import org.estatio.dom.invoice.InvoicingInterval;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseConstants;
import org.estatio.dom.lease.LeaseItem;
import org.estatio.dom.lease.LeaseItemStatus;
import org.estatio.dom.lease.LeaseRepository;
import org.estatio.dom.lease.LeaseStatus;
import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.lease.LeaseTermValueType;


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
        private BigDecimal mockValue;
        ;

        private InvoicingInterval invoicingInterval;
        private LocalDateInterval effectiveInterval;

        public CalculationResult() {
            this(null);
        }

        public CalculationResult(final InvoicingInterval interval) {
            this(interval, interval.asLocalDateInterval(), ZERO, ZERO);
        }

        public CalculationResult(
                final InvoicingInterval interval,
                final LocalDateInterval effectiveInterval,
                final BigDecimal value,
                final BigDecimal mockValue) {
            this.invoicingInterval = interval;
            this.effectiveInterval = effectiveInterval;
            this.value = value;
            this.mockValue = mockValue;
        }

        public BigDecimal value() {
            return value;
        }

        public BigDecimal mockValue() {
            return mockValue;
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
        return leaseInvoicingSettingsService == null ? new LocalDate(1980, 1, 1) : leaseInvoicingSettingsService.fetchEpochDate();
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
                        if (!leaseItem.getStatus().equals(LeaseItemStatus.SUSPENDED) && leaseItem.getInvoicedBy().equals(LeaseConstants.AgreementRoleType.LANDLORD)) {
                            //TODO: We only filter the Landlords
                            if (parameters.leaseItemTypes() == null || parameters.leaseItemTypes().contains(leaseItem.getType())) {
                                SortedSet<LeaseTerm> leaseTerms =
                                        parameters.leaseTerm() == null ?
                                                leaseItem.getTerms() :
                                                new TreeSet<>(Arrays.asList(parameters.leaseTerm()));
                                for (LeaseTerm leaseTerm : leaseTerms) {
                                    final List<CalculationResult> results;
                                    results = calculateDueDateRange(leaseTerm, parameters);
                                    createInvoiceItems(leaseTerm, parameters, results);
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

            return calculateTerm(leaseTerm, intervals, dueDateForCalculation);
        }
        return Lists.newArrayList();
    }

    /**
     * Calculates a term for a given interval
     *
     * @param term
     * @param interval
     * @param dueDate
     * @return
     */
    @Programmatic
    public List<CalculationResult> calculateDateRange(
            final LeaseTerm term,
            final LocalDateInterval interval,
            final LocalDate dueDate) {
        if (!interval.isOpenEnded() && interval.isValid()) {
            return calculateTerm(term, term.getLeaseItem().getInvoicingFrequency().intervalsInRange(interval), dueDate);
        }
        return Lists.newArrayList();
    }

    /**
     * Calculates a term for a given list of invoicing intervals
     *
     * @param leaseTerm
     * @param intervals
     * @param dueDateForCalculation
     * @return
     */
    @Programmatic
    public List<CalculationResult> calculateTerm(
            final LeaseTerm leaseTerm,
            final List<InvoicingInterval> intervals,
            final LocalDate dueDateForCalculation) {
        final List<CalculationResult> results2 = Lists.newArrayList();
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
                    BigDecimal mockValue = BigDecimal.ZERO;
                    final CalculationResult calculationResult = new CalculationResult(
                            invoicingInterval,
                            effectiveInterval,
                            calculateValue(rangeFactor, annualFactor, leaseTerm.valueForDate(dueDateForCalculation), leaseTerm.valueType()),
                            calculateValue(rangeFactor, annualFactor, mockValue, leaseTerm.valueType()));
                    results2.add(calculationResult);
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
                BigDecimal invoicedValue = invoiceItemForLeaseRepository.invoicedValue(leaseTerm, result.invoicingInterval().asLocalDateInterval());
                BigDecimal newValue = result.value().subtract(invoicedValue).subtract(result.mockValue());
                if (newValue.compareTo(BigDecimal.ZERO) != 0) {
                    boolean adjustment = invoicedValue.add(result.mockValue()).compareTo(BigDecimal.ZERO) != 0;
                    InvoiceItemForLease invoiceItem =
                            invoiceItemForLeaseRepository.createUnapprovedInvoiceItem(
                                    leaseTerm,
                                    result.invoicingInterval().asLocalDateInterval(),
                                    parameters.invoiceDueDate(),
                                    interactionId);
                    invoiceItem.setNetAmount(newValue);
                    invoiceItem.setQuantity(BigDecimal.ONE);
                    LeaseItem leaseItem = leaseTerm.getLeaseItem();
                    Charge charge = leaseItem.getCharge();
                    invoiceItem.setCharge(charge);
                    invoiceItem.setDueDate(parameters.invoiceDueDate());
                    invoiceItem.setStartDate(result.invoicingInterval().startDate());
                    invoiceItem.setEndDate(result.invoicingInterval().endDate());

                    LocalDateInterval intervalToUse =
                            adjustment
                                    ? result.invoicingInterval().asLocalDateInterval()
                                    : result.effectiveInterval();
                    invoiceItem.setEffectiveStartDate(intervalToUse.startDate());
                    invoiceItem.setEffectiveEndDate(intervalToUse.endDate());

                    invoiceItem.setTax(leaseItem.getEffectiveTax());

                    invoiceDescriptionService.update(invoiceItem);

                    invoiceItem.verify();
                    invoiceItem.setAdjustment(adjustment);
                }
            }
        }
    }

    // //////////////////////////////////////

    @javax.inject.Inject
    InvoiceDescriptionService invoiceDescriptionService;

    @Inject
    LeaseInvoicingSettingsService leaseInvoicingSettingsService;

    @Inject
    private InvoiceForLeaseRepository invoiceForLeaseRepository;

    @Inject
    private InvoiceItemForLeaseRepository invoiceItemForLeaseRepository;

    @Inject
    private LeaseRepository leaseRepository;

}
