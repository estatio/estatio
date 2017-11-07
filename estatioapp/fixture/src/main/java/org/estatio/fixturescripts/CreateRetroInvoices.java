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

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.commons.lang3.ObjectUtils;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.fixturescripts.DiscoverableFixtureScript;
import org.apache.isis.applib.services.factory.FactoryService;

import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.module.invoice.dom.Invoice;
import org.estatio.module.invoice.dom.InvoiceStatus;
import org.estatio.module.invoice.dom.InvoiceRepository;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.invoicing.InvoiceCalculationParameters;
import org.estatio.module.lease.dom.invoicing.InvoiceCalculationSelection;
import org.estatio.module.lease.dom.invoicing.InvoiceCalculationService;
import org.estatio.module.invoice.dom.InvoiceRunType;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;

import static org.incode.module.base.integtests.VT.ld;

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
                propertyRepository.allProperties(),
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
                propertyRepository.allProperties(),
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

            for (Lease lease : leaseRepository.findLeasesByProperty(property)) {
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
        for (LocalDate dueDate : lease.dueDatesInRange(startDueDate, nextDueDate)) {
            InvoiceCalculationParameters parameters =
                    InvoiceCalculationParameters.builder()
                            .lease(lease)
                            .leaseItemTypes(InvoiceCalculationSelection.ALL_ITEMS.selectedTypes())
                            .invoiceRunType(InvoiceRunType.NORMAL_RUN)
                            .invoiceDueDate(dueDate)
                            .startDueDate(startDueDate)
                            .nextDueDate(dueDate.plusDays(1)).build();
            createAndApprove(parameters, fixtureResults);
        }
        return fixtureResults;
    }

    // //////////////////////////////////////

    private ExecutionContext createAndApprove(
            final InvoiceCalculationParameters parameters,
            final ExecutionContext executionContext) {
        invoiceCalculationService.calculateAndInvoice(parameters);

        for (Invoice invoice : invoiceRepository.findByStatus(InvoiceStatus.NEW)) {
            factoryService.mixin(InvoiceForLease._saveAsHistoric.class, invoice).$$();
            executionContext.addResult(this, invoice.getInvoiceNumber(), invoice);
        }
        return executionContext;
    }

    @Inject
    protected InvoiceRepository invoiceRepository;

    @Inject
    protected LeaseRepository leaseRepository;

    @Inject
    protected PropertyRepository propertyRepository;

    @Inject
    protected InvoiceCalculationService invoiceCalculationService;

    @Inject
    protected FactoryService factoryService;

}
