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
package org.estatio.fixture.invoice;

import org.joda.time.LocalDate;

import org.apache.isis.core.commons.ensure.Ensure;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseItemType;
import org.estatio.dom.lease.invoicing.InvoiceForLease;
import org.estatio.fixture.currency.CurrenciesRefData;
import org.estatio.fixture.lease.LeaseForOxfMiracl005Gb;
import org.estatio.fixture.lease.LeaseItemAndLeaseTermForDiscountForOxfMiracl005Gb;
import org.estatio.fixture.party.OrganisationForHelloWorldGb;
import org.estatio.fixture.party.OrganisationForMiracleGb;
import org.estatio.fixture.security.tenancy.ApplicationTenancyForGbOxf;

import static org.hamcrest.CoreMatchers.is;
import static org.incode.module.base.integtests.VT.ldix;

public class InvoiceForLeaseItemTypeOfDiscountOneQuarterForOxfMiracle005 extends InvoiceAbstract {

    public static final String PARTY_REF_SELLER = OrganisationForHelloWorldGb.REF;
    public static final String PARTY_REF_BUYER = OrganisationForMiracleGb.REF;
    public static final String LEASE_REF = LeaseForOxfMiracl005Gb.REF;

    public static final String AT_PATH = ApplicationTenancyForGbOxf.PATH;

    // simply within the lease's start/end date
    public static LocalDate startDateFor(final Lease lease) {
        Ensure.ensureThatArg(lease.getReference(), is(LEASE_REF));
        return lease.getStartDate().plusYears(1);
    }

    public InvoiceForLeaseItemTypeOfDiscountOneQuarterForOxfMiracle005() {
        this(null, null);
    }

    public InvoiceForLeaseItemTypeOfDiscountOneQuarterForOxfMiracle005(final String friendlyName, final String localName) {
        super(friendlyName, localName);
    }

    @Override
    protected void execute(final ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, new OrganisationForHelloWorldGb());
        executionContext.executeChild(this, new LeaseItemAndLeaseTermForDiscountForOxfMiracl005Gb());

        // exec
        final ApplicationTenancy applicationTenancy = applicationTenancies.findTenancyByPath(AT_PATH);
        final Lease lease = leaseRepository.findLeaseByReference(LEASE_REF);
        final LocalDate invoiceStartDate = startDateFor(lease);

        final InvoiceForLease invoice = createInvoiceAndNumerator(
                applicationTenancy,
                lease,
                PARTY_REF_SELLER,
                PARTY_REF_BUYER,
                PaymentMethod.DIRECT_DEBIT,
                CurrenciesRefData.EUR,
                invoiceStartDate,
                executionContext);

        createInvoiceItemsForTermsOfFirstLeaseItemOfType(
                invoice, LeaseItemType.DISCOUNT,
                invoiceStartDate, ldix(invoiceStartDate, invoiceStartDate.plusMonths(3)),
                executionContext);

    }
}
