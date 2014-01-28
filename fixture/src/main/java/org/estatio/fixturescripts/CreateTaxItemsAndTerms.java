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

import java.math.BigDecimal;
import java.util.concurrent.Callable;

import org.joda.time.LocalDate;

import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.Charges;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.lease.InvoicingFrequency;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseItem;
import org.estatio.dom.lease.LeaseItemType;
import org.estatio.dom.lease.LeaseTermForTax;
import org.estatio.dom.lease.Leases;
import org.estatio.services.settings.EstatioSettingsService;

public class CreateTaxItemsAndTerms implements Callable<Object> {

    @Override
    public Object call() throws Exception {
        final LocalDate epochDate = settingsService.fetchEpochDate();
        String chargeReference = "1378"; // TODO: make configurable
        Charge charge = charges.findCharge(chargeReference);
        if (epochDate == null) {
            return "No epochdate available";
        }
        if (charge == null) {
            return String.format("Charge with reference '%s' not found'", chargeReference);
        }

        String reference = "";
        for (Lease lease : leases.allLeases()) {
            // only active lease on epoch date
            reference = lease.getReference();
            if (reference.contains("CAS-ELDO")) {
                System.out.println(reference);
            }
            LocalDate startDate = lease.getStartDate();
            if (startDate != null) {
                if (lease.getEffectiveInterval().contains(epochDate) || epochDate.isBefore(startDate)) {
                    LeaseItem taxItem = lease.findFirstItemOfType(LeaseItemType.TAX);
                    if (taxItem == null) {
                        LeaseItem rentItem = lease.findFirstItemOfType(LeaseItemType.RENT);
                        if (rentItem != null) {
                            taxItem = lease.newItem(LeaseItemType.TAX, charge, InvoicingFrequency.YEARLY_IN_ADVANCE, PaymentMethod.BANK_TRANSFER, startDate);
                            taxItem.setStatus(rentItem.getStatus());
                            LeaseTermForTax newTerm = (LeaseTermForTax) taxItem.newTerm(startDate.withYear(epochDate.getYear()));
                            newTerm.setTaxPercentage(BigDecimal.ONE);
                            newTerm.setRecoverablePercentage(BigDecimal.valueOf(100));
                            newTerm.verifyUntil(epochDate);
                        }
                    }
                }
            }
        }

        return null;
    }

    private Leases leases;

    public void injectLeases(final Leases leases) {
        this.leases = leases;
    }

    private EstatioSettingsService settingsService;

    public void injectSettingsService(EstatioSettingsService settingsService) {
        this.settingsService = settingsService;
    }

    private Charges charges;

    public void injectCharges(final Charges charges) {
        this.charges = charges;
    }

}
