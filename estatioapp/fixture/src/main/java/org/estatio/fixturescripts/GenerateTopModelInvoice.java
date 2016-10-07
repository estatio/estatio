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

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.DiscoverableFixtureScript;

import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseRepository;
import org.estatio.dom.lease.invoicing.InvoiceCalculationParameters;
import org.estatio.dom.lease.invoicing.InvoiceCalculationSelection;
import org.estatio.dom.lease.invoicing.InvoiceCalculationService;
import org.estatio.dom.lease.invoicing.InvoiceRunType;

import static org.estatio.integtests.VT.ld;

public class GenerateTopModelInvoice extends DiscoverableFixtureScript {

    private final String propertyRef;

    public GenerateTopModelInvoice() {
        this("OXF-TOPMODEL-001");
    }

    public GenerateTopModelInvoice(String propertyRef) {
        this.propertyRef = propertyRef;
    }

    @Override
    protected void execute(ExecutionContext fixtureResults) {
        final Lease lease = leaseRepository.findLeaseByReference(coalesce(fixtureResults.getParameters(), propertyRef));
        lease.verifyUntil(ld(2014, 1, 1));

        InvoiceCalculationParameters calculationParameters = InvoiceCalculationParameters.builder()
                .lease(lease)
                .leaseItemTypes(InvoiceCalculationSelection.ALL_RENT_AND_SERVICE_CHARGE.selectedTypes())
                .invoiceRunType(InvoiceRunType.NORMAL_RUN)
                .invoiceDueDate(ld(2013, 4, 1))
                .startDueDate(ld(2013, 4, 1))
                .nextDueDate(ld(2013, 4, 2)).build();
        calculationService.calculateAndInvoice(calculationParameters);
    }

    // //////////////////////////////////////

//    public void setLeases(final Leases leases) {
//        this.leases = leases;
//    }

    @Inject
    private LeaseRepository leaseRepository;

    @Inject
    private InvoiceCalculationService calculationService;

}
