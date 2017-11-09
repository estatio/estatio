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

import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;
import org.estatio.module.currency.fixtures.CurrenciesRefData;
import org.estatio.fixture.lease.LeaseForOxfPoison003Gb;
import org.estatio.fixture.lease.LeaseItemAndTermsForOxfPoison003Gb;
import org.estatio.module.party.fixtures.organisation.personas.OrganisationForHelloWorldGb;
import org.estatio.module.party.fixtures.organisation.personas.OrganisationForHelloWorldNl;
import org.estatio.module.party.fixtures.organisation.personas.OrganisationForPoisonGb;
import org.estatio.module.base.fixtures.security.apptenancy.personas.ApplicationTenancyForGbOxf;

import static org.hamcrest.CoreMatchers.is;
import static org.incode.module.base.integtests.VT.ldix;

public class InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003 extends InvoiceAbstract {

    public static final String PARTY_REF_SELLER = OrganisationForHelloWorldGb.REF;
    public static final String PARTY_REF_BUYER = OrganisationForPoisonGb.REF;

    public static final String LEASE_REF = LeaseForOxfPoison003Gb.REF;

    public static final String AT_PATH = ApplicationTenancyForGbOxf.PATH;

    // simply within the lease's start/end date
    public static LocalDate startDateFor(final Lease lease) {
        Ensure.ensureThatArg(lease.getReference(), is(LEASE_REF));
        return lease.getStartDate().plusYears(1);
    }

    public InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003() {
        this(null, null);
    }

    public InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003(final String friendlyName, final String localName) {
        super(friendlyName, localName);
    }

    @Override
    protected void execute(final ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, new OrganisationForHelloWorldNl());
        executionContext.executeChild(this, new LeaseItemAndTermsForOxfPoison003Gb());

        // exec

        final ApplicationTenancy applicationTenancy = applicationTenancies.findTenancyByPath(AT_PATH);
        final Lease lease = leaseRepository.findLeaseByReference(LEASE_REF);

        final LocalDate startDate = startDateFor(lease);

        final InvoiceForLease invoice = createInvoiceAndNumerator(
                applicationTenancy,
                lease, PARTY_REF_SELLER,
                PARTY_REF_BUYER, PaymentMethod.DIRECT_DEBIT,
                CurrenciesRefData.EUR,
                startDate, executionContext);

        createInvoiceItemsForTermsOfFirstLeaseItemOfType(
                invoice, LeaseItemType.RENT,
                startDate, ldix(startDate, startDate.plusMonths(3)),
                executionContext);
    }

}
