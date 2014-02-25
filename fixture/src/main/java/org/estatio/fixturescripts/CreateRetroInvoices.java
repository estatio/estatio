/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
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

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.ObjectUtils;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.dom.asset.Properties;
import org.estatio.dom.asset.Property;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.invoice.Invoices;
import org.estatio.dom.invoice.InvoicingInterval;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseItem;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.lease.Leases.InvoiceRunType;
import org.estatio.dom.lease.invoicing.InvoiceCalculationParameters;
import org.estatio.dom.lease.invoicing.InvoiceCalculationSelection;
import org.estatio.dom.lease.invoicing.InvoiceCalculationService;

/**
 * Replays the invoice creation process
 * 
 */
public class CreateRetroInvoices implements Callable<Object> {

    private static LocalDate MOCK_START_DATE = new LocalDate(2013, 1, 1);
    private static LocalDate MOCK_END_DATE = new LocalDate(2014, 4, 1);

    @Override
    public Object call() throws Exception {
        createProperties(
                propertiesService.allProperties(),
                ObjectUtils.firstNonNull(MOCK_START_DATE, new LocalDate(2013, 1, 1)),
                MOCK_END_DATE);
        return "Finished";
    }

    @Programmatic
    public void createAllProperties(
            final LocalDate startDueDate,
            final LocalDate endDueDate) {
        createProperties(
                propertiesService.allProperties(),
                startDueDate,
                endDueDate);
    }

    @Programmatic
    public void createProperty(
            final Property property,
            final LocalDate startDueDate,
            final LocalDate nextDueDate) {
        List<Property> properties = new ArrayList<Property>();
        properties.add(property);
        createProperties(properties, startDueDate, nextDueDate);
    }

    @Programmatic
    public void createProperties(
            final List<Property> properties,
            final LocalDate startDueDate,
            final LocalDate nextDueDate) {
        for (Property property : properties) {
            for (Lease lease : leases.findLeasesByProperty(property)) {
                createLease(lease, startDueDate, nextDueDate);
            }
        }
    }

    @Programmatic
    public void createLease(Lease lease, LocalDate startDueDate, LocalDate nextDueDate) {
        for (LocalDate dueDate : findDueDatesForLease(startDueDate, nextDueDate, lease)) {
            InvoiceCalculationParameters parameters =
                    new InvoiceCalculationParameters(
                            lease,
                            InvoiceCalculationSelection.ALL.selectedTypes(),
                            InvoiceRunType.NORMAL_RUN,
                            dueDate,
                            startDueDate,
                            dueDate.plusDays(1));
            createAndApprove(parameters);
        }
    }

    // //////////////////////////////////////

    private void createAndApprove(InvoiceCalculationParameters parameters) {
        invoiceCalculationService.calculateAndInvoice(parameters);
        for (Invoice invoice : invoices.findInvoices(InvoiceStatus.NEW)) {
            invoice.setStatus(InvoiceStatus.HISTORIC);
            invoice.setRunId(null);
        }
    }

    // //////////////////////////////////////

    @Programmatic
    public SortedSet<LocalDate> findDueDatesForLease(LocalDate startDueDate, LocalDate nextDueDate, Lease lease) {
        SortedSet<LocalDate> dates = new TreeSet<LocalDate>();
        for (LeaseItem leaseItem : lease.getItems()) {
            dates.addAll(findDueDatesForLeaseItem(startDueDate, nextDueDate, leaseItem));
        }
        return dates;
    }

    private SortedSet<LocalDate> findDueDatesForLeaseItem(LocalDate startDueDate, LocalDate nextDueDate, LeaseItem leaseItem) {
        SortedSet<LocalDate> dates = new TreeSet<LocalDate>();
        List<InvoicingInterval> invoiceIntervals = leaseItem.getInvoicingFrequency().intervalsInDueDateRange(
                startDueDate, ObjectUtils.firstNonNull(leaseItem.getNextDueDate(), nextDueDate));
        for (InvoicingInterval interval : invoiceIntervals) {
            dates.add(interval.dueDate());
        }
        return dates;
    }

    // //////////////////////////////////////

    private Invoices invoices;

    final public void injectInvoices(final Invoices invoices) {
        this.invoices = invoices;
    }

    private Leases leases;

    public final void injectLeases(final Leases leases) {
        this.leases = leases;
    }

    Properties propertiesService;

    final public void injectProperties(final Properties properties) {
        this.propertiesService = properties;
    }

    private InvoiceCalculationService invoiceCalculationService;

    public final void injectCalculationService(final InvoiceCalculationService invoiceCalculationService) {
        this.invoiceCalculationService = invoiceCalculationService;
    }

}
