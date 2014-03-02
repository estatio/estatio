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

import java.util.concurrent.Callable;

import org.joda.time.LocalDate;

import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.lease.invoicing.InvoiceCalculationParameters;
import org.estatio.dom.lease.invoicing.InvoiceCalculationSelection;
import org.estatio.dom.lease.invoicing.InvoiceCalculationService;
import org.estatio.dom.lease.invoicing.InvoiceRunType;

public class GenerateTopModelInvoice implements Callable<Object> {

    @Override
    public Object call() throws Exception {
        final Lease lease = leases.findLeaseByReference("OXF-TOPMODEL-001");
        lease.verifyUntil(new LocalDate(2014, 1, 1));

        InvoiceCalculationParameters parameters = new InvoiceCalculationParameters(
                lease,
                InvoiceCalculationSelection.RENT_AND_SERVICE_CHARGE.selectedTypes(),
                InvoiceRunType.NORMAL_RUN,
                new LocalDate(2013, 4, 1),
                new LocalDate(2013, 4, 1),
                new LocalDate(2013, 4, 2));
        return calculationService.calculateAndInvoice(parameters);
    }

    // //////////////////////////////////////

    private Leases leases;

    public void setLeases(final Leases leases) {
        this.leases = leases;
    }

    private InvoiceCalculationService calculationService;

    public void injectCalculationService(final InvoiceCalculationService calculationService) {
        this.calculationService = calculationService;
    }

}
