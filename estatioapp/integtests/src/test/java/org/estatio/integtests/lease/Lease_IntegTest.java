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

import java.util.SortedSet;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.sudo.SudoService;
import org.apache.isis.applib.services.wrapper.DisabledException;
import org.apache.isis.applib.services.wrapper.InvalidException;
import org.apache.isis.applib.services.wrapper.WrapperFactory;
import org.apache.isis.applib.services.xactn.TransactionService;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.integtests.VT;

import org.estatio.app.menus.lease.LeaseMenu;
import org.estatio.charge.dom.Charge;
import org.estatio.charge.dom.ChargeRepository;
import org.estatio.invoice.dom.PaymentMethod;
import org.estatio.dom.lease.InvoicingFrequency;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseItem;
import org.estatio.dom.lease.LeaseItemType;
import org.estatio.dom.lease.LeaseRepository;
import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyRepository;
import org.estatio.dom.roles.EstatioRole;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.charge.ChargeRefData;
import org.estatio.fixture.lease.LeaseForOxfMediaX002Gb;
import org.estatio.fixture.lease.LeaseForOxfPoison003Gb;
import org.estatio.fixture.lease.LeaseForOxfTopModel001Gb;
import org.estatio.fixture.lease.LeaseItemAndTermsForOxfMediax002Gb;
import org.estatio.fixture.lease.LeaseItemAndTermsForOxfPoison003Gb;
import org.estatio.fixture.lease.LeaseItemAndTermsForOxfTopModel001;
import org.estatio.fixture.party.OrganisationForMediaXGb;
import org.estatio.fixture.security.users.EstatioAdmin;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.incode.module.base.integtests.VT.ld;

public class Lease_IntegTest extends EstatioIntegrationTest {

    @Inject
    LeaseMenu leaseMenu;

    @Inject
    LeaseRepository leaseRepository;

    @Inject
    PartyRepository partyRepository;

    @Inject
    TransactionService transactionService;

    public static class Remove extends Lease_IntegTest {

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

        @Test
        public void happy_case() throws Exception {
            //Given
            Lease lease = leaseRepository.findLeaseByReference(LeaseForOxfTopModel001Gb.REF);
            assertThat(lease.getOccupancies().size()).isGreaterThan(0);

            //When
            sudoService.sudo(EstatioAdmin.USER_NAME, Lists.newArrayList(EstatioRole.ADMINISTRATOR.getRoleName()),
                    new Runnable() {
                        @Override public void run() {
                            wrap(lease).remove("Some reason");
                        }
                    });

            //Then
            assertThat(leaseRepository.findLeaseByReference(LeaseForOxfTopModel001Gb.REF)).isNull();
        }

        @Inject
        SudoService sudoService;

    }

    public static class Assign extends Lease_IntegTest {

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

        @Test
        public void happyCase() throws Exception {
            // given
            final String newReference = "OXF-MEDIA-001";
            final Lease lease = leaseRepository.findLeaseByReference(LeaseForOxfTopModel001Gb.REF);
            final Party newParty = partyRepository.findPartyByReference(OrganisationForMediaXGb.REF);
            final LocalDate newStartDate = VT.ld(2014, 1, 1);

            // when
            Lease newLease = lease.assign(
                    newReference,
                    newReference,
                    newParty,
                    newStartDate);

            // then
            assertThat(newLease.getReference()).isEqualTo(newReference);
            assertThat(newLease.getName()).isEqualTo(newReference);
            assertThat(newLease.getStartDate()).isEqualTo(lease.getStartDate());
            assertThat(newLease.getEndDate()).isEqualTo(lease.getEndDate());
            assertThat(newLease.getTenancyStartDate()).isEqualTo(newStartDate);
            assertThat(newLease.getTenancyEndDate()).isNull();
            assertThat(newLease.getPrimaryParty()).isEqualTo(lease.getPrimaryParty());
            assertThat(newLease.getSecondaryParty()).isEqualTo(newParty);
            assertThat(newLease.getItems().size()).isEqualTo(lease.getItems().size());

            assertThat(lease.getTenancyEndDate()).isEqualTo(newStartDate.minusDays(1));
        }
    }


    public static class FindItem extends Lease_IntegTest {

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

        @Test
        public void whenExists() throws Exception {

            // given
            Lease lease = leaseRepository.findLeaseByReference(LeaseForOxfTopModel001Gb.REF);
            assertThat(lease.getItems().size()).isEqualTo(9);

            // when
            LeaseItem leaseTopModelRentItem = lease.findItem(LeaseItemType.RENT, VT.ld(2010, 7, 15), VT.bi(1));
            LeaseItem leaseTopModelServiceChargeItem = lease.findItem(LeaseItemType.SERVICE_CHARGE, VT.ld(2010, 7, 15), VT.bi(1));

            // then
            Assert.assertNotNull(leaseTopModelRentItem);
            Assert.assertNotNull(leaseTopModelServiceChargeItem);
        }

    }

    public static class GetItems extends Lease_IntegTest {

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

        @Test
        public void whenNonEmpty() throws Exception {
            Lease lease = leaseRepository.findLeaseByReference(LeaseForOxfTopModel001Gb.REF);
            assertThat(lease.getItems().size()).isEqualTo(9);
        }
    }

    public static class NewItem extends Lease_IntegTest {

        @Before
        public void setupData() {
            runScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());
                    executionContext.executeChild(this, new LeaseForOxfPoison003Gb());
                }
            });
        }

        private Lease leasePoison;

        @Before
        public void setup() {
            leasePoison = leaseRepository.findLeaseByReference(LeaseForOxfPoison003Gb.REF);
        }


        @Inject
        private ChargeRepository chargeRepository;
        @Inject
        private WrapperFactory wrapperFactory;

        @Test
        public void happyCase() throws Exception {

            // given
            final String chargeRf = ChargeRefData.GB_DISCOUNT;
            final Charge charge = chargeRepository.findByReference(chargeRf);
            final ApplicationTenancy leaseAppTenancy = leasePoison.getApplicationTenancy();
            final ApplicationTenancy firstChildAppTenancy = leaseAppTenancy.getChildren().first();

            // when
            final LeaseItem leaseItem = wrap(leasePoison).newItem(
                    LeaseItemType.DISCOUNT, charge, InvoicingFrequency.FIXED_IN_ADVANCE, PaymentMethod.DIRECT_DEBIT,
                    leasePoison.getStartDate());

            // then
            assertThat(leaseItem.getLease()).isEqualTo(leasePoison);
            assertThat(leaseItem.getType()).isEqualTo(LeaseItemType.DISCOUNT);
            assertThat(leaseItem.getInvoicingFrequency()).isEqualTo(InvoicingFrequency.FIXED_IN_ADVANCE);
            assertThat(leaseItem.getPaymentMethod()).isEqualTo(PaymentMethod.DIRECT_DEBIT);
            assertThat(leaseItem.getStartDate()).isEqualTo(leasePoison.getStartDate());
            assertThat(leaseItem.getSequence()).isEqualTo(VT.bi(1));
            assertThat(leaseItem.getApplicationTenancy()).isEqualTo(firstChildAppTenancy);
        }

        @Test
        public void invalidCharge() throws Exception {

            // given
            final Charge charge = chargeRepository.findByReference(ChargeRefData.IT_DISCOUNT);
            final ApplicationTenancy leaseAppTenancy = leasePoison.getApplicationTenancy();

            expectedExceptions.expect(InvalidException.class);
            expectedExceptions.expectMessage(containsString("not valid for this lease"));

            // when
            wrap(leasePoison).newItem(
                    LeaseItemType.DISCOUNT, charge, InvoicingFrequency.FIXED_IN_ADVANCE, PaymentMethod.DIRECT_DEBIT,
                    leasePoison.getStartDate());
        }


    }

    public static class VerifyUntil extends Lease_IntegTest {

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

        private Lease leaseTopModel;
        private LeaseItem leaseTopModelRentItem;
        private LeaseItem leaseTopModelServiceChargeItem;

        @Before
        public void setUp() throws Exception {
            leaseTopModel = leaseRepository.findLeaseByReference(LeaseForOxfTopModel001Gb.REF);

            leaseTopModelRentItem = leaseTopModel.findItem(LeaseItemType.RENT, VT.ld(2010, 7, 15), VT.bi(1));
            assertThat(leaseTopModelRentItem).isNotNull();

            leaseTopModelServiceChargeItem = leaseTopModel.findItem(LeaseItemType.SERVICE_CHARGE, VT.ld(2010, 7, 15), VT.bi(1));
            assertThat(leaseTopModelServiceChargeItem).isNotNull();
        }

        /**
         * Compare to tests that verify at the
         * {@link org.estatio.dom.lease.LeaseTerm} level.
         *
         */
        @Test
        public void createsTermsForLeaseTermItems() throws Exception {

            // given
            assertThat(leaseTopModelRentItem.findTerm(VT.ld(2012, 7, 15))).isNull();
            assertThat(leaseTopModelServiceChargeItem.findTerm(VT.ld(2012, 7, 15))).isNull();

            // when
            leaseTopModel.verifyUntil(VT.ld(2014, 1, 1));

            // then
            assertThat(leaseTopModelRentItem.findTerm(VT.ld(2012, 7, 15))).isNotNull();
            assertThat(leaseTopModelServiceChargeItem.findTerm(VT.ld(2012, 7, 15))).isNotNull();
        }

    }

    public static class VerifyUntil_1 extends Lease_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());

                    executionContext.executeChild(this, new LeaseItemAndTermsForOxfMediax002Gb());
                    executionContext.executeChild(this, new LeaseItemAndTermsForOxfPoison003Gb());
                }
            });
        }

        @Test
        public void happyCase1() throws Exception {
            // TODO: what is the variation being tested here ?

            // given
            Lease leaseMediax = leaseRepository.findLeaseByReference(LeaseForOxfMediaX002Gb.REF);

            LeaseItem leaseMediaXServiceChargeItem = leaseMediax.findItem(LeaseItemType.SERVICE_CHARGE, VT.ld(2008, 1, 1), VT.bi(1));
            LeaseTerm leaseMediaXServiceChargeTerm = leaseMediaXServiceChargeItem.findTerm(VT.ld(2008, 1, 1));
            assertThat(leaseMediaXServiceChargeTerm).isNotNull();

            // when
            leaseMediax.verifyUntil(VT.ld(2014, 1, 1));

            // commit to get the BigDecimals to be stored to the correct
            // precision by DN.
            nextTransaction();

            // and reload
            leaseMediax = leaseRepository.findLeaseByReference("OXF-MEDIAX-002");
            leaseMediaXServiceChargeItem = leaseMediax.findItem(LeaseItemType.SERVICE_CHARGE, VT.ld(2008, 1, 1), VT.bi(1));

            // then
            leaseMediaXServiceChargeTerm = leaseMediaXServiceChargeItem.findTerm(VT.ld(2008, 1, 1));
            assertThat(leaseMediaXServiceChargeTerm).isNotNull();

            final LeaseTerm leaseMediaXServiceChargeTermN = leaseMediaXServiceChargeItem.getTerms().last();
            assertThat(leaseMediaXServiceChargeTermN.getEffectiveValue()).isEqualTo(VT.bd("6000.00"));
        }

        @Test
        public void happyCase2() throws Exception {
            // TODO: what is the variation being tested here ?

            // given
            Lease leasePoison = leaseRepository.findLeaseByReference("OXF-POISON-003");

            LeaseItem leasePoisonRentItem = leasePoison.findItem(LeaseItemType.RENT, VT.ld(2011, 1, 1), VT.bi(1));
            LeaseItem leasePoisonServiceChargeItem = leasePoison.findItem(LeaseItemType.SERVICE_CHARGE, VT.ld(2011, 1, 1), VT.bi(1));
            assertThat(leasePoisonServiceChargeItem).isNotNull();

            // when
            leasePoison.verifyUntil(VT.ld(2014, 1, 1));

            // commit to get the BigDecimals to be stored to the correct
            // precision by DN; reload
            nextTransaction();
            leasePoison = leaseRepository.findLeaseByReference("OXF-POISON-003");
            leasePoisonRentItem = leasePoison.findItem(LeaseItemType.RENT, VT.ld(2011, 1, 1), VT.bi(1));
            leasePoisonServiceChargeItem = leasePoison.findItem(LeaseItemType.SERVICE_CHARGE, VT.ld(2011, 1, 1), VT.bi(1));

            // then
            final LeaseTerm leaseTerm1 = leasePoisonServiceChargeItem.findTerm(VT.ld(2011, 1, 1));
            assertThat(leaseTerm1).isNotNull();

            final LeaseTerm leaseTerm2 = leasePoisonServiceChargeItem.getTerms().last();
            assertThat(leaseTerm2.getEffectiveValue()).isEqualTo(VT.bd("12400.00"));

            // and then
            SortedSet<LeaseTerm> terms = leasePoisonRentItem.getTerms();

            assertThat(terms.size()).isEqualTo(3);
            assertThat(leasePoisonRentItem.findTerm(VT.ld(2011, 1, 1))).isNotNull();
        }

    }

    public static class GetRoles extends Lease_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());

                    executionContext.executeChild(this, new LeaseForOxfTopModel001Gb());
                }
            });
        }

        private Lease leaseTopModel;

        @Inject
        private LeaseMenu leaseMenu;

        @Before
        public void setup() {
            leaseTopModel = leaseRepository.findLeaseByReference(LeaseForOxfTopModel001Gb.REF);
        }

        @Test
        public void whenNonEmpty() throws Exception {
            // TODO: this seems to be merely asserting on the contents of the
            // fixture
            assertThat(leaseTopModel.getRoles().size()).isEqualTo(3);
        }

    }

    public static class ChangeDates extends Lease_IntegTest {

        @Rule
        public ExpectedException expectedException = ExpectedException.none();


        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());

                    executionContext.executeChild(this, new LeaseForOxfTopModel001Gb());
                }
            });
        }

        private Lease leaseTopModel;

        @Inject
        private LeaseMenu leaseMenu;

        @Before
        public void setup() {
            leaseTopModel = leaseRepository.findLeaseByReference(LeaseForOxfTopModel001Gb.REF);
        }

        @Test
        public void onDateChange() throws Exception {

            //then
            expectedException.expect(DisabledException.class);
            expectedException.expectMessage("You need administrator rights to change the dates");

            // when
            wrap(leaseTopModel).changeDates(null, null);
        }
    }

    public static class changePrevious extends Lease_IntegTest {

        private Lease leaseTopModel1;
        private Lease leaseTopModel2a;
        private Lease leaseTopModel2b;
        private Lease leaseTopModel3;

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());
                    executionContext.executeChild(this, new LeaseForOxfTopModel001Gb());
                }
            });
            leaseTopModel1 = leaseRepository.findLeaseByReference(LeaseForOxfTopModel001Gb.REF);
            leaseTopModel2a = leaseTopModel1.renew("OXF-TOPMODEL-002A", "Lease2", ld(2022, 7, 15), ld(2032, 7, 14));
            leaseTopModel3 = leaseTopModel2a.renew("OXF-TOPMODEL-003", "lease3", ld(2032,7,15), ld(2042,7,14));
            leaseTopModel2b = leaseRepository.newLease(
                    leaseTopModel2a.getApplicationTenancy(),
                    "OXF-TOPMODEL-002B",
                    "lease2b",
                    leaseTopModel2a.getLeaseType(),
                    leaseTopModel2a.getStartDate(),
                    leaseTopModel2a.getEndDate(),
                    leaseTopModel2a.getTenancyStartDate(),
                    leaseTopModel2a.getTenancyEndDate(),
                    leaseTopModel2a.getPrimaryParty(),
                    leaseTopModel2a.getSecondaryParty()
            );
            transactionService.flushTransaction();
        }

        @Test
        public void changePrevious () throws Exception {

            // given
            assertThat(leaseTopModel2a.getReference()).isEqualTo("OXF-TOPMODEL-002A");
            assertThat(leaseTopModel2b.getReference()).isEqualTo("OXF-TOPMODEL-002B");
            assertThat(leaseTopModel3.getReference()).isEqualTo("OXF-TOPMODEL-003");

            assertThat(leaseTopModel1.getNext()).isEqualTo(leaseTopModel2a);
            assertThat(leaseTopModel2a.getNext()).isEqualTo(leaseTopModel3);
            assertThat(leaseTopModel2b.getNext()).isNull();

            assertThat(leaseTopModel2a.getPrevious()).isEqualTo(leaseTopModel1);
            assertThat(leaseTopModel2b.getPrevious()).isNull();
            assertThat(leaseTopModel3.getPrevious()).isEqualTo(leaseTopModel2a);


            // when
            leaseTopModel3.changePrevious(leaseTopModel2b);
            transactionService.flushTransaction();


            // then
            assertThat(leaseTopModel2b.getNext()).isEqualTo(leaseTopModel3);
            assertThat(leaseTopModel2a.getNext()).isNull();
            //TODO: find way to test this; Datanucleus issue with bi-direcitonal relationship
            //assertThat(leaseTopModel3.getPrevious()).isEqualTo(leaseTopModel2b));

        }

        @Inject private WrapperFactory wrapperFactory;


    }

    public static class setPreviousNull extends Lease_IntegTest {

        private Lease leaseTopModel1;
        private Lease leaseTopModel2;
        private Lease leaseTopModel3;

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());
                    executionContext.executeChild(this, new LeaseForOxfTopModel001Gb());
                }
            });
            leaseTopModel1 = leaseRepository.findLeaseByReference(LeaseForOxfTopModel001Gb.REF);
            leaseTopModel2 = leaseTopModel1.renew("OXF-TOPMODEL-002", "Lease2", ld(2022, 7, 15), ld(2032, 7, 14));
            // work-a-round: make an extra one in order to get leaseTopModel2.getPrevious() set
            leaseTopModel3 = leaseTopModel2.renew("OXF-TOPMODEL-003", "lease3", ld(2032,7,15), ld(2042,7,14));
        }

        @Test
        public void changePreviousToNull () throws Exception {

            // given
            assertThat(leaseTopModel2.getReference()).isEqualTo("OXF-TOPMODEL-002");
            assertThat(leaseTopModel3.getReference()).isEqualTo("OXF-TOPMODEL-003");
            assertThat(leaseTopModel1.getNext()).isEqualTo(leaseTopModel2);
            assertThat(leaseTopModel2.getNext()).isEqualTo(leaseTopModel3);
            assertThat(leaseTopModel2.getPrevious()).isEqualTo(leaseTopModel1);
            //TODO: Datanucleus issue with bi-direcitonal relationship
//            assertThat(leaseTopModel3.getPrevious()).isEqualTo(leaseTopModel2));

            // when
            leaseTopModel2.changePrevious(null);

            // then
            assertThat(leaseTopModel1.getNext()).isNull();
            //TODO: find way to test this; Datanucleus issue with bi-direcitonal relationship
            // assertNull(leaseTopModel2.getPrevious());

        }

    }

    public static class PrimaryOccupancy extends Lease_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());
                    executionContext.executeChild(this, new LeaseForOxfPoison003Gb());
                }
            });
        }
        
        @Test
        public void xxx() throws Exception {
            Lease lease = leaseRepository.findLeaseByReference(LeaseForOxfPoison003Gb.REF);

            assertThat(lease.getOccupancies().size()).isEqualTo(1);
            assertThat(lease.primaryOccupancy().get()).isEqualTo(lease.getOccupancies().first());

        }



    }
}