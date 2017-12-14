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
package org.estatio.module.lease.fixtures.invoicing.personas;

import org.estatio.module.lease.fixtures.invoice.InvoiceAbstract;
import org.estatio.module.lease.fixtures.invoice.enums.InvoiceForLease_enum;

public class InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003 extends InvoiceAbstract {

//    public static final String PARTY_REF_SELLER = InvoiceForLease_enum.OxfPoison003Gb.getSeller_d().getRef();
//    public static final String PARTY_REF_BUYER = InvoiceForLease_enum.OxfPoison003Gb.getBuyer_d().getRef();
//    public static final String LEASE_REF = InvoiceForLease_enum.OxfPoison003Gb.getLease_d().getRef();
//    public static final String AT_PATH = InvoiceForLease_enum.OxfPoison003Gb.getApplicationTenancy_d().getPath();
//
//    public InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003() {
//        this(null, null);
//    }

    public InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003(final String friendlyName, final String localName) {
        super(friendlyName, localName);
    }

    @Override
    protected void execute(final ExecutionContext ec) {

        // prereqs
//        ec.executeChild(this, OrganisationAndComms_enum.HelloWorldNl.builder());
//
//        ec.executeChild(this, LeaseItemForRent_enum.OxfPoison003Gb.builder());
//        ec.executeChild(this, LeaseItemForServiceCharge_enum.OxfPoison003Gb.builder());
//        ec.executeChild(this, LeaseItemForTurnoverRent_enum.OxfPoison003Gb.builder());

        ec.executeChildren(this, InvoiceForLease_enum.OxfPoison003Gb);

//
//        // exec
//
//        final ApplicationTenancy applicationTenancy = applicationTenancies.findTenancyByPath(AT_PATH);
//        final Lease lease = leaseRepository.findLeaseByReference(LEASE_REF);
//
//        final LocalDate startDate = startDateFor(lease);
//
//        final InvoiceForLease invoice = createInvoiceAndNumerator(
//                applicationTenancy,
//                lease, PARTY_REF_SELLER,
//                PARTY_REF_BUYER, PaymentMethod.DIRECT_DEBIT,
//                Currency_enum.EUR.getReference(),
//                startDate, ec);
//
//        createInvoiceItemsForTermsOfFirstLeaseItemOfType(
//                invoice, LeaseItemType.RENT,
//                startDate, ldix(startDate, startDate.plusMonths(3)),
//                ec);
    }

}
