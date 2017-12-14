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

public class InvoiceForLeaseItemTypeOfRentOneQuarterForKalPoison001 extends InvoiceAbstract {

//    private static final OrganisationAndComms_enum seller_d = OrganisationAndComms_enum.AcmeNl;
//    private static final OrganisationAndComms_enum buyer_d = OrganisationAndComms_enum.PoisonNl;
//    private static final Lease_enum lease_d = Lease_enum.KalPoison001Nl;
//    private static final ApplicationTenancy_enum applicationTenancy_d = ApplicationTenancy_enum.NlKal;

//    public static final String PARTY_REF_SELLER = InvoiceForLease_enum.KalPoison001Nl.getSeller_d().getRef();
//    public static final String PARTY_REF_BUYER = InvoiceForLease_enum.KalPoison001Nl.getBuyer_d().getRef();
//    public static final String LEASE_REF = InvoiceForLease_enum.KalPoison001Nl.getLease_d().getRef();

    //public static final String AT_PATH = applicationTenancy_d.getPath();

    public InvoiceForLeaseItemTypeOfRentOneQuarterForKalPoison001() {
        this(null, null);
    }

    public InvoiceForLeaseItemTypeOfRentOneQuarterForKalPoison001(String friendlyName, String localName) {
        super(friendlyName, localName);
    }

    @Override
    protected void execute(ExecutionContext ec) {

//        // prereqs
//        ec.executeChild(this, seller_d.builder());
//        ec.executeChild(this, LeaseItemForRent_enum.KalPoison001Nl.builder());

        ec.executeChildren(this, InvoiceForLease_enum.KalPoison001Nl);

//
//        // exec
//        final ApplicationTenancy applicationTenancy = applicationTenancies.findTenancyByPath(AT_PATH);
//        final Lease lease = lease_d.findUsing(serviceRegistry);
//
//        // simply within the lease's start/end date
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
