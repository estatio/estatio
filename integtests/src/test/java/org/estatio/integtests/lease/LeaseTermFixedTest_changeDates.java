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
package org.estatio.integtests.lease;

import java.util.List;
import java.util.SortedSet;
import javax.inject.Inject;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.estatio.dom.invoice.Invoices;
import org.estatio.dom.lease.*;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.invoice.InvoiceForLeaseItemTypeOfDiscountOneQuarterForOxfMiracle005;
import org.estatio.fixture.invoice.InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003;
import org.estatio.fixture.lease.LeaseForOxfMiracl005;
import org.estatio.fixture.lease.LeaseForOxfPoison003;
import org.estatio.fixture.lease.LeaseItemAndLeaseTermForDiscountForOxfMiracl005;
import org.estatio.integtests.EstatioIntegrationTest;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.wrapper.DisabledException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertNotNull;

/**
 * Fixed lease terms (that is, those that have a {@link org.estatio.dom.lease.LeaseTerm#valueType() value type} of
 * {@link org.estatio.dom.lease.LeaseTermValueType#FIXED}) are such that the amount to be invoiced is fixed for the
 * term and is apportioned according to the {@link org.estatio.dom.lease.LeaseTerm}'s parent
 * {@link org.estatio.dom.lease.LeaseTerm#getLeaseItem() lease item}'s {@link org.estatio.dom.lease.LeaseItem#getInvoicingFrequency() invoicing frequency}.
 *
 * <p>
 * Unlike {@link org.estatio.dom.lease.LeaseTermValueType#ANNUAL annual} lease terms (which are potentially open ended),
 * a fixed lease term must have fixed {@link org.estatio.dom.lease.LeaseTerm#getStartDate() start} and 
 * {@link org.estatio.dom.lease.LeaseTerm#getEndDate() end date}s because the fixed amount is apportioned over that period.
 * </p>
 *
 * <p>
 * As a slight modification to that rule, we do allow the start and end dates to be changed if no {@link org.estatio.dom.lease.invoicing.InvoiceItemsForLease invoice item}s
 * have yet been created for the lease term; this allows for corrections to incorrectly entered data prior to the
 * first invoice run.
 * </p>
 */
public class LeaseTermFixedTest_changeDates extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        runScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                execute(new EstatioBaseLineFixture(), executionContext);
                execute(new LeaseItemAndLeaseTermForDiscountForOxfMiracl005(), executionContext);
                execute(new InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003(), executionContext);
            }
        });
    }

    @Inject
    private Leases leases;

    @Inject
    private Invoices invoices;

    private Lease lease;
    private LeaseItem leaseTopModelRentItem;

    @Before
    public void setup() {
        lease = leases.findLeaseByReference(LeaseForOxfMiracl005.LEASE_REFERENCE);
        leaseTopModelRentItem = lease.getItems().first();
        assertNotNull(leaseTopModelRentItem);
        assertNotNull(leaseTopModelRentItem.getStartDate());
        assertNotNull(leaseTopModelRentItem.getEndDate());
    }

    /**
     *<pre>
     *    Given
     *    a lease term with fixed invoicing frequency (eg LeaseTermForFixed, LeaseTermForTax)
     *    with start and end dates
     *    and with at least one invoice
     *
     *    When
     *    I attempt to change the start dates
     *    Or
     *    I attempt to change the end dates
     *
     *    Then
     *    this is disallowed
     *</pre>
     */
    @Test
    public void disabledIfLeaseHasInvoiceForFixedInvoicingFrequencyTerm() throws Exception {

        // given
        runScript(new InvoiceForLeaseItemTypeOfDiscountOneQuarterForOxfMiracle005().withNoPrereqs());

        // have to obtain again because runScript commits and so JDO clears out all enlisted objects.
        lease = leases.findLeaseByReference(LeaseForOxfMiracl005.LEASE_REFERENCE);
        final LeaseTerm leaseTerm = findFirstLeaseTerm(lease, LeaseItemType.DISCOUNT);

        // and given
        assertThat(leaseTerm.valueType(), is(LeaseTermValueType.FIXED));

        // and given
        Assert.assertThat(invoices.findInvoices(lease), not(empty()));

        // then
        expectedExceptions.expect(DisabledException.class);
        expectedExceptions.expectMessage("Cannot change dates because this lease term has invoices and is fixed");

        // when
        final LocalDate newStartDate = leaseTerm.getStartDate().minusMonths(1);
        wrap(leaseTerm).changeDates(newStartDate, lease.getEndDate());
    }


    /**
     *<pre>
     *    Given
     *     a lease term with fixed invoicing frequency (eg LeaseTermForFixed, LeaseTermForTax)
     *     with start and end dates
     *     and NO invoices
     *
     *     When
     *     I change the start dates
     *     Or
     *     I change the end dates
     *
     *     Then
     *     this is allowed and the date changes accordingly
     *
     *</pre>
     */
    @Test
    public void allowedIfLeaseHasNoInvoicesForItsFixedInvoicingFrequencyTerm() throws Exception {

        // given
        final LeaseItemType leaseItemType = LeaseItemType.DISCOUNT;
        final LeaseTerm leaseTerm = findFirstLeaseTerm(lease, leaseItemType);

        // and given
        assertThat(leaseTerm.valueType(), is(LeaseTermValueType.FIXED));

        // and given
        assertThat(invoices.findInvoices(lease), empty());

        // when
        final LocalDate newStartDate = leaseTerm.getStartDate().minusMonths(1);
        wrap(leaseTerm).changeDates(newStartDate, lease.getEndDate());

        // then
        assertThat(leaseTerm.getStartDate(), is(newStartDate));

    }

    /**
     *<pre>
     *     Given
     *     a lease term with some non-fixed invoicing frequency (eg LeaseTermForIndexableRent)
     *     with start and end dates
     *     and at least one invoice
     *
     *     When
     *     I change the start dates
     *     Or
     *     I change the end dates
     *
     *     Then
     *     this is allowed and the date changes accordingly
     *</pre>
     */
    @Test
    public void allowedIfLeaseHasInvoiceForNonFixedInvoicingFrequencyTerm() throws Exception {

        // given
        lease = leases.findLeaseByReference(LeaseForOxfPoison003.LEASE_REFERENCE);
        final LeaseTerm leaseTerm = findFirstLeaseTerm(lease, LeaseItemType.TURNOVER_RENT);

        // and given
        assertThat(leaseTerm.valueType(), is(LeaseTermValueType.ANNUAL));

        // and given
        assertThat(invoices.findInvoices(lease), not(empty()));


        // when
        final LocalDate newStartDate = leaseTerm.getStartDate().minusMonths(1);
        wrap(leaseTerm).changeDates(newStartDate, lease.getEndDate());

        // then
        assertThat(leaseTerm.getStartDate(), is(newStartDate));
    }


    private LeaseTerm findFirstLeaseTerm(final Lease lease, final LeaseItemType leaseItemType) {
        final List<LeaseItem> leaseItems = Lists.newArrayList(Iterables.filter(lease.getItems(), LeaseItem.Predicates.ofType(leaseItemType)));
        assertThat(leaseItems, not(empty()));

        final LeaseItem leaseItem = leaseItems.get(0);
        final SortedSet<LeaseTerm> terms = leaseItem.getTerms();
        assertThat(terms, not(empty()));

        return terms.first();
    }



}
