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
package org.estatio.module.lease.integtests.lease;

import java.math.BigDecimal;
import java.util.List;
import java.util.SortedSet;

import javax.inject.Inject;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.wrapper.DisabledException;

import org.incode.module.base.integtests.VT;

import org.estatio.module.lease.app.LeaseMenu;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.LeaseTerm;
import org.estatio.module.lease.dom.LeaseTermForIndexable;
import org.estatio.module.lease.dom.LeaseTermStatus;
import org.estatio.module.lease.dom.LeaseTermValueType;
import org.estatio.module.lease.dom.invoicing.InvoiceForLeaseRepository;
import org.estatio.module.lease.dom.invoicing.InvoiceItemForLeaseRepository;
import org.estatio.module.lease.fixtures.invoice.enums.InvoiceForLease_enum;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForDeposit_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForDiscount_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForEntryFee_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForMarketing_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForRent_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForServiceCharge_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForTax_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForTurnoverRent_enum;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertNotNull;

public class LeaseTerm_IntegTest extends LeaseModuleIntegTestAbstract {

    @Inject
    LeaseMenu leaseMenu;

    @Inject
    LeaseRepository leaseRepository;

    /**
     * Fixed lease terms (that is, those that have a
     * {@link LeaseTerm#valueType() value type} of
     * {@link LeaseTermValueType#FIXED}) are such that the
     * amount to be invoiced is fixed for the term and is apportioned according
     * to the {@link LeaseTerm}'s parent
     * {@link LeaseTerm#getLeaseItem() lease item}'s
     * {@link LeaseItem#getInvoicingFrequency() invoicing
     * frequency}.
     *
     * <p>
     * Unlike {@link LeaseTermValueType#ANNUAL annual}
     * lease terms (which are potentially open ended), a fixed lease term must
     * have fixed {@link LeaseTerm#getStartDate() start}
     * and {@link LeaseTerm#getEndDate() end date}s
     * because the fixed amount is apportioned over that period.
     * </p>
     *
     * <p>
     * As a slight modification to that rule, we do allow the start and end
     * dates to be changed if no
     * {@link InvoiceItemForLeaseRepository invoice item}
     * s have yet been created for the lease term; this allows for corrections
     * to incorrectly entered data prior to the first invoice run.
     * </p>
     */
    public static class ValueTypeOfFixed extends LeaseTerm_IntegTest {

        public static class ChangeDates extends LeaseTerm_IntegTest {

            @Before
            public void setupData() {
                runFixtureScript(new FixtureScript() {
                    @Override
                    protected void execute(ExecutionContext ec) {

                        ec.executeChildren(this,
                                LeaseItemForDiscount_enum.OxfMiracle005bGb,
                                InvoiceForLease_enum.OxfPoison003Gb);

                    }
                });
            }

            @Inject
            InvoiceForLeaseRepository invoiceForLeaseRepository;

            private Lease lease;
            private LeaseItem leaseTopModelRentItem;

            @Before
            public void setup() {
                lease = leaseRepository.findLeaseByReference(Lease_enum.OxfMiracl005Gb.getRef());
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
            @Ignore // LeaseTerm#changeDates() is hidden everywhere until use case emerges
            public void disabledIfLeaseHasInvoiceForFixedInvoicingFrequencyTerm() throws Exception {

                // given

                // TODO: if this test is ever reinstated, then its fixtures will need sorting out.
                // previously, it had this code:
                //
                // EstatioFixtureScript.withSkipPrereqs(
                //        new Runnable() {
                //
                //            @Override
                //            public void run() {
                //                runFixtureScript(new InvoiceForLease_enum.OxfMiracle005Gb.builder());
                //            }
                //        }
                //        );
                //
                // however, the withSkipPrereqs has now been removed (this was the only test that used it).

                // have to obtain again because runScript commits and so JDO
                // clears out all enlisted objects.
                lease = leaseRepository.findLeaseByReference(Lease_enum.OxfMiracl005Gb.getRef());
                final LeaseTerm leaseTerm = findFirstLeaseTerm(lease, LeaseItemType.RENT_DISCOUNT_FIXED);

                // and given
                assertThat(leaseTerm.valueType(), is(LeaseTermValueType.FIXED));

                // and given
                Assert.assertThat(invoiceForLeaseRepository.findByLease(lease), not(empty()));

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
            @Ignore // LeaseTerm#changeDates() is hidden everywhere until use case emerges
            public void allowedIfLeaseHasNoInvoicesForItsFixedInvoicingFrequencyTerm() throws Exception {

                // given
                final LeaseItemType leaseItemType = LeaseItemType.RENT_DISCOUNT_FIXED;
                final LeaseTerm leaseTerm = findFirstLeaseTerm(lease, leaseItemType);

                // and given
                assertThat(leaseTerm.valueType(), is(LeaseTermValueType.FIXED));

                // and given
                assertThat(invoiceForLeaseRepository.findByLease(lease), empty());

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
            @Ignore // LeaseTerm#changeDates() is hidden everywhere until use case emerges
            public void allowedIfLeaseHasInvoiceForNonFixedInvoicingFrequencyTerm() throws Exception {

                // given
                lease = Lease_enum.OxfPoison003Gb.findUsing(serviceRegistry);
                final LeaseTerm leaseTerm = findFirstLeaseTerm(lease, LeaseItemType.TURNOVER_RENT);

                // and given
                assertThat(leaseTerm.valueType(), is(LeaseTermValueType.ANNUAL));

                // and given
                assertThat(invoiceForLeaseRepository.findByLease(lease), not(empty()));

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

    public static class Approve extends LeaseTerm_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {


                    executionContext.executeChild(this, LeaseItemForRent_enum.OxfTopModel001Gb.builder());
                    executionContext.executeChild(this, LeaseItemForServiceCharge_enum.OxfTopModel001Gb.builder());
                    executionContext.executeChild(this, LeaseItemForServiceCharge_enum.OxfTopModel001Gb_TA.builder());
                    executionContext.executeChild(this, LeaseItemForTurnoverRent_enum.OxfTopModel001Gb.builder());
                    executionContext.executeChild(this, LeaseItemForDiscount_enum.OxfTopModel001Gb.builder());
                    executionContext.executeChild(this, LeaseItemForEntryFee_enum.OxfTopModel001Gb.builder());
                    executionContext.executeChild(this, LeaseItemForTax_enum.OxfTopModel001Gb.builder());
                    executionContext.executeChild(this, LeaseItemForDeposit_enum.OxfTopModel001Gb.builder());
                    executionContext.executeChild(this, LeaseItemForMarketing_enum.OxfTopModel001Gb.builder());

                }
            });
        }

        private Lease lease;
        private LeaseItem leaseTopModelRentItem;

        @Before
        public void setup() {
            lease = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
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

    public static class VerifyUntil extends LeaseTerm_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {


                    executionContext.executeChild(this, LeaseItemForRent_enum.OxfTopModel001Gb.builder());
                    executionContext.executeChild(this, LeaseItemForServiceCharge_enum.OxfTopModel001Gb.builder());
                    executionContext.executeChild(this, LeaseItemForServiceCharge_enum.OxfTopModel001Gb_TA.builder());
                    executionContext.executeChild(this, LeaseItemForTurnoverRent_enum.OxfTopModel001Gb.builder());
                    executionContext.executeChild(this, LeaseItemForDiscount_enum.OxfTopModel001Gb.builder());
                    executionContext.executeChild(this, LeaseItemForEntryFee_enum.OxfTopModel001Gb.builder());
                    executionContext.executeChild(this, LeaseItemForTax_enum.OxfTopModel001Gb.builder());
                    executionContext.executeChild(this, LeaseItemForDeposit_enum.OxfTopModel001Gb.builder());
                    executionContext.executeChild(this, LeaseItemForMarketing_enum.OxfTopModel001Gb.builder());

                }
            });
        }

        private Lease lease;

        @Before
        public void setup() {
            lease = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
            Assert.assertThat(lease.getItems().size(), is(8));
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