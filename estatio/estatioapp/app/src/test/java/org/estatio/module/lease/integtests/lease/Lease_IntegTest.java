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

import org.incode.module.base.integtests.VT;

import org.estatio.module.agreement.dom.AgreementRoleRepository;
import org.estatio.module.agreement.dom.role.AgreementRoleTypeRepository;
import org.estatio.module.agreement.dom.type.AgreementTypeRepository;
import org.estatio.module.base.dom.EstatioRole;
import org.estatio.module.base.fixtures.security.users.personas.EstatioAdmin;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.fixtures.charges.enums.Charge_enum;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.lease.app.LeaseMenu;
import org.estatio.module.lease.dom.InvoicingFrequency;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;
import org.estatio.module.lease.dom.LeaseAgreementTypeEnum;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.LeaseTerm;
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
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.fixtures.orgcomms.enums.OrganisationAndComms_enum;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.incode.module.base.integtests.VT.ld;

public class Lease_IntegTest extends LeaseModuleIntegTestAbstract {

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

        @Test
        public void happy_case() throws Exception {
            //Given
            Lease lease = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
            assertThat(lease.getOccupancies().size()).isGreaterThan(0);

            //When
            sudoService.sudo(EstatioAdmin.USER_NAME, Lists.newArrayList(EstatioRole.ADMINISTRATOR.getRoleName()),
                    new Runnable() {
                        @Override public void run() {
                            wrap(lease).remove("Some reason");
                        }
                    });

            //Then
            assertThat(Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry)).isNull();
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

        @Test
        public void happyCase() throws Exception {
            // given
            final String newReference = "OXF-MEDIA-001";
            final Lease lease = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
            final Party newParty = OrganisationAndComms_enum.MediaXGb.findUsing(serviceRegistry);
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

        @Test
        public void whenExists() throws Exception {

            // given
            Lease lease = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
            assertThat(lease.getItems().size()).isEqualTo(8);

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

        @Test
        public void whenNonEmpty() throws Exception {
            Lease lease = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
            assertThat(lease.getItems().size()).isEqualTo(8);
        }
    }

    public static class NewItem extends Lease_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {

                    executionContext.executeChild(this, Lease_enum.OxfPoison003Gb.builder());
                }
            });
        }

        private Lease leasePoison;

        @Before
        public void setup() {
            leasePoison = Lease_enum.OxfPoison003Gb.findUsing(serviceRegistry);
        }


        @Test
        public void happyCase() throws Exception {

            // given
            final Charge charge = Charge_enum.GbDiscount.findUsing(serviceRegistry);

            // when
            final LeaseItem leaseItem = wrap(leasePoison).newItem(
                    LeaseItemType.RENT_DISCOUNT_FIXED, LeaseAgreementRoleTypeEnum.LANDLORD,
                    charge, InvoicingFrequency.FIXED_IN_ADVANCE, PaymentMethod.DIRECT_DEBIT,
                    leasePoison.getStartDate());

            // then
            assertThat(leaseItem.getLease()).isEqualTo(leasePoison);
            assertThat(leaseItem.getType()).isEqualTo(LeaseItemType.RENT_DISCOUNT_FIXED);
            assertThat(leaseItem.getInvoicingFrequency()).isEqualTo(InvoicingFrequency.FIXED_IN_ADVANCE);
            assertThat(leaseItem.getPaymentMethod()).isEqualTo(PaymentMethod.DIRECT_DEBIT);
            assertThat(leaseItem.getStartDate()).isEqualTo(leasePoison.getStartDate());
            assertThat(leaseItem.getSequence()).isEqualTo(VT.bi(1));
            assertThat(leaseItem.getApplicationTenancy().getPath()).isEqualTo("/GBR/OXF");
        }

        @Test
        public void invalidCharge() throws Exception {

            // given
            final Charge charge = Charge_enum.ItDiscount.findUsing(serviceRegistry);

            expectedExceptions.expect(InvalidException.class);
            expectedExceptions.expectMessage(containsString("not valid for this lease"));

            // when
            wrap(leasePoison).newItem(
                    LeaseItemType.RENT_DISCOUNT_FIXED, LeaseAgreementRoleTypeEnum.LANDLORD, charge, InvoicingFrequency.FIXED_IN_ADVANCE, PaymentMethod.DIRECT_DEBIT,
                    leasePoison.getStartDate());
        }


    }

    public static class VerifyUntil extends Lease_IntegTest {

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

        private Lease leaseTopModel;
        private LeaseItem leaseTopModelRentItem;
        private LeaseItem leaseTopModelServiceChargeItem;

        @Before
        public void setUp() throws Exception {
            leaseTopModel = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);

            leaseTopModelRentItem = leaseTopModel.findItem(LeaseItemType.RENT, VT.ld(2010, 7, 15), VT.bi(1));
            assertThat(leaseTopModelRentItem).isNotNull();

            leaseTopModelServiceChargeItem = leaseTopModel.findItem(LeaseItemType.SERVICE_CHARGE, VT.ld(2010, 7, 15), VT.bi(1));
            assertThat(leaseTopModelServiceChargeItem).isNotNull();
        }

        /**
         * Compare to tests that verify at the
         * {@link LeaseTerm} level.
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


                    executionContext.executeChild(this, LeaseItemForRent_enum.OxfMediaX002Gb.builder());
                    executionContext.executeChild(this, LeaseItemForServiceCharge_enum.OxfMediaX002Gb.builder());
                    executionContext.executeChild(this, LeaseItemForTurnoverRent_enum.OxfMediaX002Gb.builder());

                    executionContext.executeChild(this, LeaseItemForRent_enum.OxfPoison003Gb.builder());
                    executionContext.executeChild(this, LeaseItemForServiceCharge_enum.OxfPoison003Gb.builder());
                    executionContext.executeChild(this, LeaseItemForTurnoverRent_enum.OxfPoison003Gb.builder());
                }
            });
        }

        @Test
        public void happyCase1() throws Exception {
            // TODO: what is the variation being tested here ?

            // given
            Lease leaseMediax = Lease_enum.OxfMediaX002Gb.findUsing(serviceRegistry);

            LeaseItem leaseMediaXServiceChargeItem = leaseMediax.findItem(LeaseItemType.SERVICE_CHARGE, VT.ld(2008, 1, 1), VT.bi(1));
            LeaseTerm leaseMediaXServiceChargeTerm = leaseMediaXServiceChargeItem.findTerm(VT.ld(2008, 1, 1));
            assertThat(leaseMediaXServiceChargeTerm).isNotNull();

            // when
            leaseMediax.verifyUntil(VT.ld(2014, 1, 1));

            // commit to get the BigDecimals to be stored to the correct
            // precision by DN.
            transactionService.nextTransaction();

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
            transactionService.nextTransaction();
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


                    executionContext.executeChild(this, Lease_enum.OxfTopModel001Gb.builder());
                }
            });
        }

        private Lease leaseTopModel;

        @Inject
        private LeaseMenu leaseMenu;

        @Before
        public void setup() {
            leaseTopModel = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
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


                    executionContext.executeChild(this, Lease_enum.OxfTopModel001Gb.builder());
                }
            });
        }

        private Lease leaseTopModel;

        @Inject
        private LeaseMenu leaseMenu;

        @Before
        public void setup() {
            leaseTopModel = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
        }

        @Test
        public void onDateChange() throws Exception {

            //then
            expectedException.expect(DisabledException.class);
            expectedException.expectMessage("You need super user rights to change the dates");

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

                    executionContext.executeChild(this, Lease_enum.OxfTopModel001Gb.builder());
                }
            });
            leaseTopModel1 = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
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

                    executionContext.executeChild(this, Lease_enum.OxfTopModel001Gb.builder());
                }
            });
            leaseTopModel1 = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
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

                    executionContext.executeChild(this, Lease_enum.OxfPoison003Gb.builder());
                }
            });
        }
        
        @Test
        public void xxx() throws Exception {
            Lease lease = Lease_enum.OxfPoison003Gb.findUsing(serviceRegistry);

            assertThat(lease.getOccupancies().size()).isEqualTo(1);
            assertThat(lease.primaryOccupancy().get()).isEqualTo(lease.getOccupancies().first());

        }



    }

    public static class Renew extends Lease_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, Lease_enum.HanTopModel002Se.builder());
                    executionContext.executeChild(this, Lease_enum.OxfTopModel001Gb.builder());
                }
            });
        }


        @Test
        public void tenancyEndDateNotSetSweden() throws Exception {
            // given
            Lease leaseSe = Lease_enum.HanTopModel002Se.findUsing(serviceRegistry);
            Lease leaseGb = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
            LocalDate startDate = ld(2022, 7, 15);
            LocalDate endDate = ld(2032, 7, 14);

            // when
            Lease leaseSe2 = leaseSe.renew("HAN-TOPMODEL-003", "Lease2", startDate, endDate);
            Lease leaseGb2 = leaseGb.renew("OXF-TOPMODEL-002", "Lease2", startDate, endDate);

            // then
            assertThat(leaseSe2.getTenancyEndDate()).isNull();
            assertThat(leaseGb2.getTenancyEndDate()).isEqualTo(endDate);


        }


        @Test
        public void renew() {
            // Given
            Lease oxfTopmodelLease = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
            String newReference = oxfTopmodelLease.default0Renew() + "-2";
            String newName = oxfTopmodelLease.default1Renew() + "-2";
            LocalDate newStartDate = oxfTopmodelLease.default2Renew().plusDays(5); // +5 is to ensure that the change in tenancy end date is detected by the test
            LocalDate newEndDate = new LocalDate(2030, 12, 31);
            oxfTopmodelLease.setComments("Some comments");

            // When
            Lease newLease = oxfTopmodelLease.renew(newReference, newName, newStartDate, newEndDate);

            // Then

            // the lease is terminated
            assertThat(oxfTopmodelLease.getTenancyEndDate()).isEqualTo(newStartDate.minusDays(1));
            assertThat(oxfTopmodelLease.getOccupancies().first().getEndDate()).isEqualTo(newStartDate.minusDays(1));

            assertThat(newLease.getOccupancies().size()).isEqualTo(1);
            assertThat(newLease.getStartDate()).isEqualTo(newStartDate);
            assertThat(newLease.getEndDate()).isEqualTo(newEndDate);
            assertThat(newLease.getTenancyStartDate()).isEqualTo(newStartDate);
            assertThat(newLease.getTenancyEndDate()).isEqualTo(newEndDate);
            assertThat(newLease.getComments()).isEqualTo("Some comments");

            // Then
            assertThat(agreementRoles.findByAgreementAndPartyAndTypeAndContainsDate(newLease, newLease.getSecondaryParty(), agreementRoleTypeRepository
                    .findByAgreementTypeAndTitle(agreementTypeRepository.find(LeaseAgreementTypeEnum.LEASE), "Tenant"), newLease.getStartDate()).getCommunicationChannels().size()).isEqualTo(2);
            assertThat(newLease.getOccupancies().size()).isEqualTo(1);
        }

        @Test
        public void reneWithTerminatedOccupancies() {
            // Given
            Lease lease = leaseRepository.allLeases().get(0);
            String newReference = lease.default0Renew() + "-2";
            String newName = lease.default1Renew() + "-2";
            LocalDate newStartDate = lease.default2Renew();
            LocalDate newEndDate = new LocalDate(2030, 12, 31);

            // When
            lease.primaryOccupancy().get().setEndDate(lease.getTenancyEndDate());
            Lease newLease = lease.renew(newReference, newName, newStartDate, newEndDate);

            // Then
            assertThat(newLease.getOccupancies().size()).isEqualTo(1);
        }

        @Test
        public void renew_with_terms_ending_on_tenancy_end_date() throws Exception {

            // Given
            Lease oxfTopmodelLease = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
            final LeaseItem rentItem = oxfTopmodelLease
                    .newItem(LeaseItemType.RENT, LeaseAgreementRoleTypeEnum.LANDLORD,
                            Charge_enum.GbRent.findUsing(serviceRegistry), InvoicingFrequency.QUARTERLY_IN_ADVANCE,
                            PaymentMethod.DIRECT_DEBIT, oxfTopmodelLease.getStartDate());
            rentItem.newTerm(rentItem.getStartDate(), rentItem.getStartDate().withMonthOfYear(12).withDayOfMonth(31));
            oxfTopmodelLease.setTenancyEndDate(rentItem.getStartDate().withMonthOfYear(12).withDayOfMonth(31));

            assertThat(rentItem.getTerms()).hasSize(1);
            // ECP-1222: to state the case ...
            oxfTopmodelLease.verifyUntil(oxfTopmodelLease.getTenancyEndDate().plusMonths(1));
            transactionService.nextTransaction();
            // still ...
            assertThat(rentItem.getTerms()).hasSize(1);

            // when
            final Lease renewal = oxfTopmodelLease
                    .renew("Topmodel-2", "Topmodel-2", oxfTopmodelLease.getTenancyEndDate().plusDays(1), null);

            // then
            assertThat(renewal.getItems().first().getTerms()).hasSize(1);
            final LeaseTerm term = renewal.getItems().first().getTerms().first();
            assertThat(term.getEndDate()).isLessThan(renewal.getStartDate());
            assertThat(term.getEffectiveInterval()).isNull();

            // .. and a verify will produce a term within the effective interval of the lease
            // when
            renewal.verifyUntil(renewal.getStartDate().plusDays(1));
            transactionService.nextTransaction();
            assertThat(renewal.getItems().first().getTerms()).hasSize(2);
            assertThat(renewal.getItems().first().getTerms().last().getEffectiveInterval()).isNotNull();
            assertThat(renewal.getItems().first().getTerms().last().getPrevious()).isEqualTo(term);
        }

        @Test
        public void renew_copies_future_terms_also() throws Exception {

            // Given
            Lease oxfTopmodelLease = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
            final LeaseItem rentItem = oxfTopmodelLease
                    .newItem(LeaseItemType.RENT, LeaseAgreementRoleTypeEnum.LANDLORD,
                            Charge_enum.GbRent.findUsing(serviceRegistry), InvoicingFrequency.QUARTERLY_IN_ADVANCE,
                            PaymentMethod.DIRECT_DEBIT, oxfTopmodelLease.getStartDate());
            final LeaseTerm firstRentTerm = rentItem
                    .newTerm(rentItem.getStartDate(), rentItem.getStartDate().withMonthOfYear(12).withDayOfMonth(31));

            assertThat(rentItem.getTerms()).hasSize(1);
            oxfTopmodelLease.verifyUntil(firstRentTerm.getEndDate().plusDays(2).plusYears(1));
            transactionService.nextTransaction();
            assertThat(rentItem.getTerms()).hasSize(3);

            // when
            final Lease renewal = oxfTopmodelLease
                    .renew("Topmodel-2", "Topmodel-2", firstRentTerm.getEndDate().plusDays(1), null);
            renewal.setTenancyEndDate(firstRentTerm.getEndDate().plusMonths(1));

            // then
            assertThat(renewal.getItems().first().getTerms()).hasSize(3);

            // which - when after tenancy enddate of the lease - can be removed with another verify
            renewal.verifyUntil(renewal.getStartDate().plusDays(1));
            transactionService.nextTransaction();
            assertThat(renewal.getItems().first().getTerms()).hasSize(2);

        }

        @Inject
        private AgreementRoleRepository agreementRoles;

        @Inject
        private AgreementRoleTypeRepository agreementRoleTypeRepository;

        @Inject AgreementTypeRepository agreementTypeRepository;

    }
}