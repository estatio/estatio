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
import java.util.Iterator;
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
import org.estatio.dom.lease.invoicing.InvoiceCalculationSelection;
import org.estatio.services.settings.EstatioSettingsService;

/**
 * Replays the invoice creation process
 * 
 * @author jvanderwal
 * 
 */
public class CreateRetroInvoices implements Callable<Object> {

    @Override
    public Object call() throws Exception {
        create(
                propertiesService.allProperties(),
                estatioSettingsService.fetchEpochDate(),
                new LocalDate(2014, 1, 1));
        return "Finished";
    }

    @Programmatic
    public void create(Property property, LocalDate startDueDate, LocalDate nextDueDate) {
        List<Property> properties = new ArrayList<Property>();
        properties.add(property);
        create(properties, startDueDate, nextDueDate);
    }

    @Programmatic
    public void create(List<Property> properties, LocalDate startDueDate, LocalDate nextDueDate) {
        for (Property property : properties) {
            List<Lease> leaseList = leases.findLeasesByProperty(property);
            for (Lease lease : leaseList) {
                create(lease, startDueDate, nextDueDate);
            }
        }
    }

    @Programmatic
    public void create(Lease lease, LocalDate startDueDate, LocalDate nextDueDate) {
        lease.verifyUntil(nextDueDate);
        for (LocalDate dueDate : findDueDatesForLease(startDueDate, nextDueDate, lease)) {
            for (LeaseItem leaseItem : lease.getItems()) {
                if (InvoiceCalculationSelection.RENT_AND_SERVICE_CHARGE.contains(leaseItem.getType())) {
                    leaseItem.calculate(
                            InvoiceRunType.NORMAL_RUN,
                            dueDate,
                            startDueDate,
                            dueDate.plusDays(1));
                }
            }
            for (Invoice invoice : invoices.findInvoicesToBeApproved()) {
                invoice.setStatus(InvoiceStatus.APPROVED);
            }
        }
    }

    @Programmatic
    public SortedSet<LocalDate> findDueDatesForLease(LocalDate startDueDate, LocalDate nextDueDate, Lease lease) {
        SortedSet<LocalDate> dates = new TreeSet<LocalDate>();
        for (LeaseItem leaseItem : lease.getItems()) {
            dates.addAll(findDueDatesForLeaseItem(startDueDate, nextDueDate, leaseItem));
        }
        return dates;
    }

    @Programmatic
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

    Properties propertiesService;

    final public void injectProperties(final Properties properties) {
        this.propertiesService = properties;
    }

    private Leases leases;

    public void injectLeases(final Leases leases) {
        this.leases = leases;
    }

    private EstatioSettingsService estatioSettingsService;

    public void setEstatioSettingsService(final EstatioSettingsService estatioSettingsService) {
        this.estatioSettingsService = estatioSettingsService;
    }

}
