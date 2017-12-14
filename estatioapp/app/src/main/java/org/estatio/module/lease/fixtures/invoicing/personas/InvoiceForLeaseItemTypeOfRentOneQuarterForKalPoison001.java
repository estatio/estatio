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

import org.joda.time.LocalDate;

import org.apache.isis.core.commons.ensure.Ensure;

import org.estatio.module.base.fixtures.security.apptenancy.enums.ApplicationTenancy_enum;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.fixtures.invoice.InvoiceAbstract;
import org.estatio.module.lease.fixtures.invoice.enums.InvoiceForLease_enum;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.party.fixtures.organisation.enums.OrganisationAndComms_enum;

import static org.hamcrest.CoreMatchers.is;

public class InvoiceForLeaseItemTypeOfRentOneQuarterForKalPoison001 extends InvoiceAbstract {

    private static final OrganisationAndComms_enum seller_d = OrganisationAndComms_enum.AcmeNl;
    private static final OrganisationAndComms_enum buyer_d = OrganisationAndComms_enum.PoisonNl;
    private static final Lease_enum lease_d = Lease_enum.KalPoison001Nl;
    private static final ApplicationTenancy_enum applicationTenancy_d = ApplicationTenancy_enum.NlKal;

    public static final String PARTY_REF_SELLER = seller_d.getRef();
    public static final String PARTY_REF_BUYER = buyer_d.getRef();
    public static final String LEASE_REF = lease_d.getRef();

    public static final String AT_PATH = applicationTenancy_d.getPath();

    // simply within the lease's start/end date
    public static LocalDate startDateFor(Lease lease) {
        Ensure.ensureThatArg(lease.getReference(), is(LEASE_REF));
        return lease.getStartDate().plusYears(1);
    }

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
