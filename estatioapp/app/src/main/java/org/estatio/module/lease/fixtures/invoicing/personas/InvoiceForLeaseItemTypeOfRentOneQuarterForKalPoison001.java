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

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.module.base.fixtures.security.apptenancy.enums.ApplicationTenancy_enum;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;
import org.estatio.module.currency.fixtures.CurrenciesRefData;
import org.estatio.module.lease.fixtures.lease.LeaseForKalPoison001Nl;
import org.estatio.module.lease.fixtures.lease.LeaseItemAndLeaseTermForRentForKalPoison001;
import org.estatio.module.lease.fixtures.invoice.InvoiceAbstract;
import org.estatio.module.party.fixtures.organisation.enums.Organisation_enum;
import org.estatio.module.party.fixtures.organisation.personas.OrganisationForAcmeNl;

import static org.hamcrest.CoreMatchers.is;
import static org.incode.module.base.integtests.VT.ldix;

public class InvoiceForLeaseItemTypeOfRentOneQuarterForKalPoison001 extends InvoiceAbstract {

    public static final String PARTY_REF_SELLER = Organisation_enum.AcmeNl.getRef();
    public static final String PARTY_REF_BUYER = Organisation_enum.PoisonNl.getRef();
    public static final String LEASE_REF = LeaseForKalPoison001Nl.REF;

    public static final String AT_PATH = ApplicationTenancy_enum.NlKal.getPath();

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
    protected void execute(ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, new OrganisationForAcmeNl());
        executionContext.executeChild(this, new LeaseItemAndLeaseTermForRentForKalPoison001());

        // exec
        final ApplicationTenancy applicationTenancy = applicationTenancies.findTenancyByPath(AT_PATH);
        final Lease lease = leaseRepository.findLeaseByReference(LEASE_REF);

        // simply within the lease's start/end date
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
