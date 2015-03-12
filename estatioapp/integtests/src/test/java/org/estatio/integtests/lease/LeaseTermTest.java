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

import java.math.BigDecimal;
import java.util.List;
import java.util.SortedSet;
import javax.inject.Inject;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.wrapper.DisabledException;
import org.estatio.dom.invoice.Invoices;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseItem;
import org.estatio.dom.lease.LeaseItemType;
import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.lease.LeaseTermForIndexable;
import org.estatio.dom.lease.LeaseTermStatus;
import org.estatio.dom.lease.LeaseTermValueType;
import org.estatio.dom.lease.Leases;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.EstatioFixtureScript;
import org.estatio.fixture.invoice.InvoiceForLeaseItemTypeOfDiscountOneQuarterForOxfMiracle005;
import org.estatio.fixture.invoice.InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003;
import org.estatio.fixture.lease.LeaseItemAndLeaseTermForDiscountForOxfMiracl005Gb;
import org.estatio.fixture.lease.LeaseItemAndTermsForOxfTopModel001;
import org.estatio.fixture.lease._LeaseForOxfMiracl005Gb;
import org.estatio.fixture.lease._LeaseForOxfPoison003Gb;
import org.estatio.fixture.lease._LeaseForOxfTopModel001Gb;
import org.estatio.integtests.EstatioIntegrationTest;
import org.estatio.integtests.VT;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertNotNull;

public class LeaseTermTest extends EstatioIntegrationTest {

    @Inject
    Leases leases;

    /**
     * Fixed lease terms (that is, those that have a
     * {@link org.estatio.dom.lease.LeaseTerm#valueType() value type} of
     * {@link org.estatio.dom.lease.LeaseTermValueType#FIXED}) are such that the
     * amount to be invoiced is fixed for the term and is apportioned according
     * to the {@link org.estatio.dom.lease.LeaseTerm}'s parent
     * {@link org.estatio.dom.lease.LeaseTerm#getLeaseItem() lease item}'s
     * {@link org.estatio.dom.lease.LeaseItem#getInvoicingFrequency() invoicing
     * frequency}.
     *
     * <p>
     * Unlike {@link org.estatio.dom.lease.LeaseTermValueType#ANNUAL annual}
     * lease terms (which are potentially open ended), a fixed lease term must
     * have fixed {@link org.estatio.dom.lease.LeaseTerm#getStartDate() start}
     * and {@link org.estatio.dom.lease.LeaseTerm#getEndDate() end date}s
     * because the fixed amount is apportioned over that period.
     * </p>
     *
     * <p>
     * As a slight modification to that rule, we do allow the start and end
     * dates to be changed if no
     * {@link org.estatio.dom.lease.invoicing.InvoiceItemsForLease invoice item}
     * s have yet been created for the lease term; this allows for corrections
     * to incorrectly entered data prior to the first invoice run.
     * </p>
     */
    public static class ValueTypeOfFixed extends LeaseTermTest {

        public static class ChangeDates extends LeaseTermTest {

            @Before
            public void setupData() {
                runFixtureScript(new FixtureScript() {
                    @Override
                    protected void execute(ExecutionContext executionContext) {
                        executionContext.executeChild(this, new EstatioBaseLineFixture());
                        executionContext.executeChild(this, new LeaseItemAndLeaseTermForDiscountForOxfMiracl005Gb());
                        executionContext.executeChild(this, new InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003());
                    }
                });
            }

            @Inject
            private Invoices invoices;

            private Lease lease;
            private LeaseItem leaseTopModelRentItem;

            @Before
            public void setup() {
                lease = leases.findLeaseByReference(_LeaseForOxfMiracl005Gb.REF);
                leaseTopModelRentItem = lease.getItems().first();
                assertNotNull(leaseTopModelRentItem);
                assertNotNull(leaseTopModelRentItem.getStartDate());
                assertNotNull(leaseTopModelRentItem.getEndDate());
            }

            /**
             * <pre>
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
             * </pre>
             */
            @Test
            public void disabledIfLeaseHasInvoiceForFixedInvoicingFrequencyTerm() throws Exception {

                // given
                EstatioFixtureScript.withSkipPrereqs(
                        new Runnable() {

                            @Override
                            public void run() {
                                runFixtureScript(new InvoiceForLeaseItemTypeOfDiscountOneQuarterForOxfMiracle005());
                            }
                        }
                        );

                // have to obtain again because runScript commits and so JDO
                // clears out all enlisted objects.
                lease = leases.findLeaseByReference(_LeaseForOxfMiracl005Gb.REF);
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
             * <pre>
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
             * </pre>
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
             * <pre>
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
             * </pre>
             */
            @Test
            public void allowedIfLeaseHasInvoiceForNonFixedInvoicingFrequencyTerm() throws Exception {

                // given
                lease = leases.findLeaseByReference(_LeaseForOxfPoison003Gb.REF);
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
    }

    public static class Approve extends LeaseTermTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());

                    executionContext.executeChild(this, new LeaseItemAndTermsForOxfTopModel001());
                }
            });
        }

        private Lease lease;
        private LeaseItem leaseTopModelRentItem;

        @Before
        public void setup() {
            lease = leases.findLeaseByReference(_LeaseForOxfTopModel001Gb.REF);
            leaseTopModelRentItem = lease.findItem(LeaseItemType.RENT, VT.ld(2010, 7, 15), VT.bi(1));
            assertNotNull(leaseTopModelRentItem);
        }

        @Test
        public void happyCase() throws Exception {

            // given
            lease.verifyUntil(VT.ld(2014, 1, 1));

            LeaseTerm term0 = leaseTopModelRentItem.findTerm(VT.ld(2010, 7, 15));
            LeaseTerm term2 = leaseTopModelRentItem.findTerm(VT.ld(2012, 7, 15));
            Assert.assertThat(term2, is(not(sameInstance(term0))));

            Assert.assertThat(term0.getStatus(), is(LeaseTermStatus.NEW));
            Assert.assertThat(term2.getStatus(), is(LeaseTermStatus.NEW));

            // when
            term0.approve();

            // then only the term that is approved has a changed status
            Assert.assertThat(term0.getStatus(), is(LeaseTermStatus.APPROVED));
            Assert.assertThat(term2.getStatus(), is(LeaseTermStatus.NEW));
        }

    }

    public static class VerifyUntil extends LeaseTermTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());

                    executionContext.executeChild(this, new LeaseItemAndTermsForOxfTopModel001());
                }
            });
        }

        private Lease lease;

        @Before
        public void setup() {
            lease = leases.findLeaseByReference(_LeaseForOxfTopModel001Gb.REF);
            Assert.assertThat(lease.getItems().size(), is(6));
        }

        @Test
        public void givenLeaseTermForIndexableRent() throws Exception {

            // given
            LeaseItem leaseTopModelRentItem = lease.findItem(LeaseItemType.RENT, VT.ld(2010, 7, 15), VT.bi(1));
            Assert.assertNotNull(leaseTopModelRentItem);

            Assert.assertThat(leaseTopModelRentItem.getTerms().size(), is(1));
            LeaseTermForIndexable leaseTopModelRentTerm1 = (LeaseTermForIndexable) leaseTopModelRentItem.getTerms().first();
            LeaseTermForIndexable leaseTopModelRentTerm = (LeaseTermForIndexable) leaseTopModelRentItem.findTerm(VT.ld(2010, 7, 15));
            Assert.assertThat(leaseTopModelRentTerm, is(sameInstance(leaseTopModelRentTerm1)));

            // when
            leaseTopModelRentTerm1.verifyUntil(VT.ld(2014, 1, 1));

            // then
            Assert.assertThat(leaseTopModelRentTerm1.getBaseIndexValue(), is(BigDecimal.valueOf(137.6).setScale(4)));
            Assert.assertThat(leaseTopModelRentTerm1.getNextIndexValue(), is(VT.bd4(101.2)));
            Assert.assertThat(leaseTopModelRentTerm1.getIndexationPercentage(), is(VT.bd1(1)));
            Assert.assertThat(leaseTopModelRentTerm1.getIndexedValue(), is(VT.bd2(20200)));
        }

        @Test
        public void givenLeaseTermForServiceCharge() throws Exception {
            // given
            LeaseItem leaseTopModelServiceChargeItem = lease.findItem(LeaseItemType.SERVICE_CHARGE, VT.ld(2010, 7, 15), VT.bi(1));
            Assert.assertNotNull(leaseTopModelServiceChargeItem);

            Assert.assertThat(leaseTopModelServiceChargeItem.getTerms().size(), is(1));
            final LeaseTerm leaseTopModelServiceChargeTerm1 = leaseTopModelServiceChargeItem.getTerms().first();
            LeaseTerm leaseTopModelServiceChargeTerm = leaseTopModelServiceChargeItem.findTerm(VT.ld(2010, 7, 15));
            Assert.assertThat(leaseTopModelServiceChargeTerm1, is(sameInstance(leaseTopModelServiceChargeTerm)));

            // when
            leaseTopModelServiceChargeTerm1.verifyUntil(VT.ld(2014, 1, 1));

            // then
            SortedSet<LeaseTerm> terms = leaseTopModelServiceChargeItem.getTerms();
            assertNotNull(terms.toString(), leaseTopModelServiceChargeItem.findTerm(VT.ld(2012, 7, 15)));
        }

        @Test
        public void termsAreBeingRemoved() throws Exception {
            // given
            LeaseItem leaseTopModelServiceChargeItem = lease.findItem(LeaseItemType.SERVICE_CHARGE, VT.ld(2010, 7, 15), VT.bi(1));
            Assert.assertNotNull(leaseTopModelServiceChargeItem);
            leaseTopModelServiceChargeItem.verifyUntil(VT.ld(2014, 1, 1));
            assertThat(leaseTopModelServiceChargeItem.getTerms().size(), is(4));

            // when
            leaseTopModelServiceChargeItem.terminate(new LocalDate(2013, 7, 14));
            leaseTopModelServiceChargeItem.verifyUntil(VT.ld(2014, 1, 1));

            // then
            assertThat(leaseTopModelServiceChargeItem.getTerms().size(), is(3));

        }

    }

}