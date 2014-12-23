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
package org.estatio.fixturescripts;

import java.util.List;
import java.util.SortedSet;
import javax.inject.Inject;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.ObjectUtils;
import org.estatio.dom.asset.Properties;
import org.estatio.dom.asset.Property;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.invoice.Invoices;
import org.estatio.dom.invoice.InvoicingInterval;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseItem;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.lease.invoicing.InvoiceCalculationParameters;
import org.estatio.dom.lease.invoicing.InvoiceCalculationSelection;
import org.estatio.dom.lease.invoicing.InvoiceCalculationService;
import org.estatio.dom.lease.invoicing.InvoiceRunType;
import org.joda.time.LocalDate;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.fixturescripts.DiscoverableFixtureScript;

import static org.estatio.integtests.VT.ld;

/**
 * Replays the invoice creation process
 * 
 */
public class CreateRetroInvoices extends DiscoverableFixtureScript {

    private static LocalDate EPOCH_START_DATE = ld(2013, 1, 1);

    private static LocalDate DEFAULT_START_DATE = ld(2013, 1, 1);
    private static LocalDate DEFAULT_END_DATE = ld(2014, 4, 1);

    private final LocalDate startDate;
    private final LocalDate endDate;

    public CreateRetroInvoices() {
        this(DEFAULT_START_DATE, DEFAULT_END_DATE);
    }

    public CreateRetroInvoices(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    protected void execute(ExecutionContext executionContext) {
        createProperties(
                properties.allProperties(),
                ObjectUtils.firstNonNull(startDate, EPOCH_START_DATE),
                endDate,
                executionContext);
    }

    @Programmatic
    public void createAllProperties(
            final LocalDate startDueDate,
            final LocalDate endDueDate,
            final ExecutionContext executionContext) {
        createProperties(
                properties.allProperties(),
                startDueDate,
                endDueDate,
                executionContext);
    }

    @Programmatic
    public ExecutionContext createProperty(
            final Property property,
            final LocalDate startDueDate,
            final LocalDate nextDueDate,
            final ExecutionContext executionContext) {
        List<Property> properties = Lists.newArrayList();
        properties.add(property);
        createProperties(properties, startDueDate, nextDueDate, executionContext);
        return executionContext;
    }

    @Programmatic
    public ExecutionContext createProperties(
            final List<Property> properties,
            final LocalDate startDueDate,
            final LocalDate nextDueDate,
            final ExecutionContext executionContext) {

        for (Property property : properties) {
            executionContext.addResult(this, property.getReference(), property);

            for (Lease lease : leases.findLeasesByProperty(property)) {
                executionContext.addResult(this, lease.getReference(), lease);
                createLease(lease, startDueDate, nextDueDate, executionContext);
            }
        }
        return executionContext;
    }

    @Programmatic
    public ExecutionContext createLease(
            final Lease lease,
            final LocalDate startDueDate,
            final LocalDate nextDueDate,
            final ExecutionContext fixtureResults) {
        for (LocalDate dueDate : findDueDatesForLease(startDueDate, nextDueDate, lease)) {
            InvoiceCalculationParameters parameters =
                    new InvoiceCalculationParameters(
                            lease,
                            InvoiceCalculationSelection.ALL.selectedTypes(),
                            InvoiceRunType.NORMAL_RUN,
                            dueDate,
                            startDueDate,
                            dueDate.plusDays(1));
            createAndApprove(parameters, fixtureResults);
        }
        return fixtureResults;
    }

    // //////////////////////////////////////

    private ExecutionContext createAndApprove(
            final InvoiceCalculationParameters parameters,
            final ExecutionContext executionContext) {
        invoiceCalculationService.calculateAndInvoice(parameters);

        for (Invoice invoice : invoices.findInvoices(InvoiceStatus.NEW)) {
            invoice.setStatus(InvoiceStatus.HISTORIC);
            invoice.setRunId(null);
            executionContext.addResult(this, invoice.getInvoiceNumber(), invoice);
        }
        return executionContext;
    }

    // //////////////////////////////////////

    @Programmatic
    public SortedSet<LocalDate> findDueDatesForLease(LocalDate startDueDate, LocalDate nextDueDate, Lease lease) {
        final SortedSet<LocalDate> dates = Sets.newTreeSet();
        for (LeaseItem leaseItem : lease.getItems()) {
            dates.addAll(findDueDatesForLeaseItem(startDueDate, nextDueDate, leaseItem));
        }
        return dates;
    }

    private SortedSet<LocalDate> findDueDatesForLeaseItem(LocalDate startDueDate, LocalDate nextDueDate, LeaseItem leaseItem) {
        final SortedSet<LocalDate> dates = Sets.newTreeSet();
        List<InvoicingInterval> invoiceIntervals = leaseItem.getInvoicingFrequency().intervalsInDueDateRange(
                startDueDate, ObjectUtils.firstNonNull(leaseItem.getNextDueDate(), nextDueDate));
        for (InvoicingInterval interval : invoiceIntervals) {
            dates.add(interval.dueDate());
        }
        return dates;
    }

    // //////////////////////////////////////

    @Inject
    public Invoices invoices;

    @Inject
    public Leases leases;

    @Inject
    public Properties properties;

    @Inject
    public InvoiceCalculationService invoiceCalculationService;

}
