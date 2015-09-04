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
package org.estatio.integtests.api;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.inject.Inject;

import org.apache.chemistry.opencmis.commons.enums.PropertyType;
import org.hamcrest.core.Is;
import org.joda.time.LocalDate;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancies;

import org.estatio.api.Api;
import org.estatio.dom.agreement.AgreementRole;
import org.estatio.dom.agreement.AgreementRoleType;
import org.estatio.dom.agreement.AgreementRoleTypes;
import org.estatio.dom.asset.Properties;
import org.estatio.dom.asset.Unit;
import org.estatio.dom.asset.Units;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.ChargeGroup;
import org.estatio.dom.charge.ChargeGroups;
import org.estatio.dom.charge.Charges;
import org.estatio.dom.communicationchannel.CommunicationChannelType;
import org.estatio.dom.communicationchannel.CommunicationChannels;
import org.estatio.dom.communicationchannel.EmailAddresses;
import org.estatio.dom.communicationchannel.PhoneOrFaxNumbers;
import org.estatio.dom.communicationchannel.PostalAddresses;
import org.estatio.dom.geography.Countries;
import org.estatio.dom.geography.Country;
import org.estatio.dom.geography.State;
import org.estatio.dom.geography.States;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.lease.InvoicingFrequency;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseConstants;
import org.estatio.dom.lease.LeaseItemStatus;
import org.estatio.dom.lease.LeaseItemType;
import org.estatio.dom.lease.LeaseTermFrequency;
import org.estatio.dom.lease.LeaseTermStatus;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.lease.Occupancies;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.dom.tax.Tax;
import org.estatio.dom.tax.Taxes;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.EstatioOperationalTeardownFixture;
import org.estatio.fixture.EstatioRefDataTeardownFixture;
import org.estatio.fixture.asset.PropertyForKalNl;
import org.estatio.fixture.asset.PropertyForOxfGb;
import org.estatio.fixture.financial.BankAccountAndMandateForTopModelGb;
import org.estatio.fixture.financial.BankAccountForAcmeNl;
import org.estatio.fixture.financial.BankAccountForMediaXGb;
import org.estatio.fixture.financial.BankAccountForMiracleGb;
import org.estatio.fixture.financial.BankAccountForPretGb;
import org.estatio.fixture.financial.BankAccountAndMandateForPoisonNl;
import org.estatio.fixture.financial.BankAccountForHelloWorldGb;
import org.estatio.fixture.invoice.InvoiceForLeaseItemTypeOfRentOneQuarterForKalPoison001;
import org.estatio.fixture.invoice.InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003;
import org.estatio.fixture.lease.LeaseBreakOptionsForOxfMediax002Gb;
import org.estatio.fixture.lease.LeaseBreakOptionsForOxfPoison003Gb;
import org.estatio.fixture.lease.LeaseBreakOptionsForOxfTopModel001;
import org.estatio.fixture.lease.LeaseItemAndLeaseTermForRentForKalPoison001;
import org.estatio.fixture.lease.LeaseItemAndTermsForOxfMiracl005Gb;
import org.estatio.fixture.lease.LeaseForOxfPret004Gb;
import org.estatio.fixture.party.OrganisationForAcmeNl;
import org.estatio.fixture.party.OrganisationForHelloWorldNl;
import org.estatio.fixture.party.OrganisationForMediaXGb;
import org.estatio.fixture.party.OrganisationForMiracleGb;
import org.estatio.fixture.party.OrganisationForPoisonNl;
import org.estatio.fixture.party.OrganisationForPretGb;
import org.estatio.fixture.party.OrganisationForTopModelGb;
import org.estatio.fixture.party.PersonForJohnDoeNl;
import org.estatio.fixture.party.PersonForLinusTorvaldsNl;
import org.estatio.integtests.EstatioIntegrationTestForMigration;
import org.estatio.services.clock.ClockService;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ApiIntegrationTest extends EstatioIntegrationTestForMigration {

    @BeforeClass
    public static void setupDataForClass() {
        scenarioExecution().install(
                new FixtureScript() {
                    @Override
                    protected void execute(ExecutionContext executionContext) {
                        executionContext.executeChild(this, new EstatioBaseLineFixture());

                        executionContext.executeChild(this, new PersonForJohnDoeNl());
                        executionContext.executeChild(this, new PersonForLinusTorvaldsNl());


                        executionContext.executeChild(this, new OrganisationForHelloWorldNl());
                        executionContext.executeChild(this, new PropertyForOxfGb());
                        executionContext.executeChild(this, new BankAccountForHelloWorldGb());

                        executionContext.executeChild(this, new OrganisationForAcmeNl());
                        executionContext.executeChild(this, new PropertyForKalNl());
                        executionContext.executeChild(this, new BankAccountForAcmeNl());


                        executionContext.executeChild(this, new OrganisationForTopModelGb());
                        executionContext.executeChild(this, new LeaseBreakOptionsForOxfTopModel001());
                        executionContext.executeChild(this, new BankAccountAndMandateForTopModelGb());

                        executionContext.executeChild(this, new OrganisationForMediaXGb());
                        executionContext.executeChild(this, new LeaseBreakOptionsForOxfMediax002Gb());
                        executionContext.executeChild(this, new BankAccountForMediaXGb());

                        executionContext.executeChild(this, new OrganisationForPoisonNl());
                        executionContext.executeChild(this, new LeaseBreakOptionsForOxfPoison003Gb());
                        executionContext.executeChild(this, new LeaseItemAndLeaseTermForRentForKalPoison001());
                        executionContext.executeChild(this, new BankAccountAndMandateForPoisonNl());
                        executionContext.executeChild(this, new InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003());
                        executionContext.executeChild(this, new InvoiceForLeaseItemTypeOfRentOneQuarterForKalPoison001());

                        executionContext.executeChild(this, new OrganisationForPretGb());
                        executionContext.executeChild(this, new LeaseForOxfPret004Gb());
                        executionContext.executeChild(this, new BankAccountForPretGb());

                        executionContext.executeChild(this, new OrganisationForMiracleGb());
                        executionContext.executeChild(this, new LeaseItemAndTermsForOxfMiracl005Gb());
                        executionContext.executeChild(this, new BankAccountForMiracleGb());
                    }
                }
        );
    }

    // installs a non-standard set of reference data, so clean up for other tests.
    @AfterClass
    public static void tearDownDataForClass() {
        scenarioExecution().install(
                new FixtureScript() {
                    @Override
                    protected void execute(ExecutionContext executionContext) {
                        executionContext.executeChild(this, new EstatioOperationalTeardownFixture());
                        executionContext.executeChild(this, new EstatioRefDataTeardownFixture());
                    }
                }
        );
    }

    private static final LocalDate START_DATE = dt(2012, 1, 1);

    @Inject
    private Api api;
    @Inject
    private Leases leases;
    @Inject
    private Properties properties;
    @Inject
    private Parties parties;
    @Inject
    private CommunicationChannels communicationChannels;
    @Inject
    private Units units;
    @Inject
    private Occupancies leaseUnits;
    @Inject
    private AgreementRoleTypes agreementRoleTypes;
    @Inject
    private ClockService clockService;
    @Inject
    private PostalAddresses postalAddresses;
    @Inject
    private PhoneOrFaxNumbers phoneOrFaxNumbers;
    @Inject
    private EmailAddresses emailAddresses;
    @Inject
    private Countries countries;
    @Inject
    private States states;
    @Inject
    private Taxes taxes;
    @Inject
    private ChargeGroups chargeGroups;
    @Inject
    private Charges charges;
    @Inject
    private ApplicationTenancies applicationTenancies;

    @Test
    public void t00_refData() throws Exception {

        // country
        api.putCountry("NLD", "NL", "Netherlands");

        Country netherlands = countries.findCountry("NLD");
        Assert.assertNotNull(netherlands);
        assertThat(netherlands.getReference(), is("NLD"));
        assertThat(netherlands.getAlpha2Code(), is("NL"));
        assertThat(netherlands.getName(), is("Netherlands"));

        // state
        api.putState("NH", "North Holland", "NLD");
        State state = states.findState("NH");
        Assert.assertNotNull(state);
        assertThat(state.getReference(), is("NH"));
        assertThat(state.getName(), is("North Holland"));
        assertThat(state.getCountry(), is(netherlands));

        api.putTax("/NLD", "APITAXREF", "APITAX Name", "APITAXEXTREF", "APITAX Desc", BigDecimal.valueOf(21.0), dt(1980, 1, 1), "APITAXEXTRATEREF");
        api.putTax("/NLD", "APITAXREF", "APITAX Name", "APITAXEXTREF", "APITAX Desc", BigDecimal.valueOf(21), dt(1980, 1, 1), "APITAXEXTRATEREF");

        final Tax tax = taxes.findByReference("APITAXREF");
        Assert.assertNotNull(tax);
        assertThat(tax.getReference(), is("APITAXREF"));
        assertThat(tax.getName(), is("APITAX Name"));
        Assert.assertNotNull(tax.percentageFor(clockService.now()));

        api.putCharge("/NLD", "APICHARGEREF", "APICHARGENAME", "API CHARGE", "APITAXREF", "APISORTORDER", "APICHARGEGROUP", "APICHARGEGROUPNAME", "APICHARGEEXTREF");

        final ChargeGroup chargeGroup = chargeGroups.findChargeGroup("APICHARGEGROUP");
        Assert.assertNotNull(chargeGroup);
        assertThat(chargeGroup.getReference(), is("APICHARGEGROUP"));
        assertThat(chargeGroup.getName(), is("APICHARGEGROUPNAME"));

        final Charge charge = charges.findByReference("APICHARGEREF");
        Assert.assertNotNull(charge);
        assertThat(charge.getReference(), is("APICHARGEREF"));
        assertThat(charge.getName(), is("APICHARGENAME"));
        assertThat(charge.getDescription(), is("API CHARGE"));
        assertThat(charge.getTax(), is(tax));
        assertThat(charge.getGroup(), is(chargeGroup));
    }

    @Test
    public void t01_putAsset() throws Exception {
        api.putProperty("APIP", "Apiland", "NLD", "ApiCity", org.estatio.dom.asset.PropertyType.SHOPPING_CENTER.name(), null, null, null, OrganisationForHelloWorldNl.REF, "APIFORMAT", "APIEXTREF", "/NLD");
        api.putUnit("APIUNIT", "APIP", "APIONWER", "Name", "BOUTIQUE", dt(1999, 6, 1), null, null, null, null, null, null, null, null, null, null, null);
        Assert.assertThat(properties.findProperties("APIP").size(), Is.is(1));
    }

    @Test
    public void t02_putOrganisation() {
        api.putOrganisation("/NLD", "APITENANT", "API Tenant", "vat", "fiscal");
        api.putOrganisation("/NLD", "APILANDLORD", "API Landlord", "vat", "fiscal");
        Assert.assertThat(parties.findParties("API*").size(), Is.is(2));
    }

    @Test
    public void t03_putPartyCommunicationChannels() {
        api.putPartyCommunicationChannels("APITENANT", "APITENANT", "Address1", "Address2", "NewAddress3", "CITY", "Postal Code", "NH", "NLD", "+31987654321", "+31876543210", "test@api.local", true);
        Assert.assertNotNull(communicationChannels.findByReferenceAndType("APITENANT", CommunicationChannelType.POSTAL_ADDRESS));
        Assert.assertNotNull(communicationChannels.findByReferenceAndType("APITENANT", CommunicationChannelType.FAX_NUMBER));
        Assert.assertNotNull(communicationChannels.findByReferenceAndType("APITENANT", CommunicationChannelType.PHONE_NUMBER));
    }

    @Test
    public void t03_putPartyCommunicationChannelsWithoutReference() {
        api.putPartyCommunicationChannels("APITENANT", null, "NewAddress1", "NewAddress2", "NewAddress3", "NewCity", "NewPostCode", "NH", "NLD", "+31222222222", "+31333333333", "test@example.com", true);
        Party party = parties.findPartyByReference("APITENANT");
        Assert.assertNotNull(postalAddresses.findByAddress(party, "NewAddress1", "NewPostCode", "NewCity", countries.findCountry("NLD")));
        Assert.assertNotNull(phoneOrFaxNumbers.findByPhoneOrFaxNumber(party, "+31222222222"));
        Assert.assertNotNull(emailAddresses.findByEmailAddress(party, "test@example.com"));
    }

    @Test
    public void t04_putLeaseWorks() throws Exception {
        api.putLease("APILEASE", "Lease", "APITENANT", "APILANDLORD", "APILEASETYPE", START_DATE, dt(2021, 12, 31), null, null, "APIP");
        Lease lease = leases.findLeaseByReference("APILEASE");
        Assert.assertNotNull(lease);
        Assert.assertThat(lease.getRoles().size(), Is.is(2));
    }

    @Test
    public void t05_putLeaseUnitWorks() throws Exception {
        api.putOccupancy("APILEASE", "APIUNIT", START_DATE, null, null, null, "APISIZE", "ABIBRAND", "APISECTOR", "APIACTIVITY", "YES", "YES", "YES");
        Lease l = leases.findLeaseByReference("APILEASE");
        Unit u = units.findUnitByReference("APIUNIT");
        Assert.assertNotNull(leaseUnits.findByLeaseAndUnitAndStartDate(l, u, START_DATE));
        Assert.assertNotNull(leaseUnits.findByLeaseAndUnitAndStartDate(l, u, START_DATE));
    }

    @Test
    public void t05b_putLeasePostalAddress() throws Exception {
        api.putLeasePostalAddress("APITENANT", LeaseConstants.ART_TENANT, "APILEASE", "Address1", "Address2", null, "PostalCode", "City", "NH", "NLD", BigInteger.valueOf(1));
        final Lease l = leases.findLeaseByReference("APILEASE");
        final AgreementRoleType artTenant = agreementRoleTypes.findByTitle(LeaseConstants.ART_TENANT);
        final AgreementRole ar = l.findRoleWithType(artTenant, clockService.now());
        Assert.assertThat(ar.getCommunicationChannels().size(), Is.is(1));
    }

    @Test
    public void t05b_putLeasePostalAddress_idempotent() throws Exception {
        api.putLeasePostalAddress("APITENANT", LeaseConstants.ART_TENANT, "APILEASE", "Address1", "Address2", null, "PostalCode", "City", "NH", "NLD", BigInteger.valueOf(1));
        final Lease l = leases.findLeaseByReference("APILEASE");
        final AgreementRoleType artTenant = agreementRoleTypes.findByTitle(LeaseConstants.ART_TENANT);
        final AgreementRole ar = l.findRoleWithType(artTenant, clockService.now());
        Assert.assertThat(ar.getCommunicationChannels().size(), Is.is(1));
    }

    @Test
    public void t06_putLeaseItemWorks() throws Exception {
        api.putLeaseItem("APILEASE", "APITENANT", "APIUNIT", LeaseItemType.RENT.name(), BigInteger.valueOf(1), START_DATE, null, "APICHARGEREF", null, InvoicingFrequency.QUARTERLY_IN_ADVANCE.name(), PaymentMethod.DIRECT_DEBIT.name(), LeaseItemStatus.ACTIVE.name(), "/nl/APIP/_");
        Assert.assertThat(leases.findLeaseByReference("APILEASE").getItems().size(), Is.is(1));
    }

    @Test
    public void t07_putLeaseTermWorks() throws Exception {
        api.putLeaseTermForIndexableRent(
                "APILEASE",
                "APITENANT",
                "APIUNIT",
                BigInteger.valueOf(1),
                LeaseItemType.RENT.name(),
                START_DATE,
                BigInteger.valueOf(1),
                START_DATE,
                dt(2012, 12, 31),
                LeaseTermStatus.NEW.name(),
                null,
                null,
                BigDecimal.valueOf(12345),
                BigDecimal.valueOf(12345),
                null,
                null,
                null,
                "APIINDEX",
                LeaseTermFrequency.YEARLY.name(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null, 
                null);
        api.putLeaseTermForIndexableRent(
                "APILEASE",
                "APITENANT",
                "APIUNIT",
                BigInteger.valueOf(1),
                LeaseItemType.RENT.name(),
                START_DATE,
                BigInteger.valueOf(2),
                dt(2013, 1, 1),
                dt(2013, 12, 31),
                LeaseTermStatus.NEW.name(),
                null,
                null,
                BigDecimal.valueOf(12345),
                BigDecimal.valueOf(12345),
                null,
                null,
                null,
                "APIINDEX",
                LeaseTermFrequency.YEARLY.name(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null, null);
        Lease lease = leases.findLeaseByReference("APILEASE");
        Assert.assertThat(lease.getItems().first().getTerms().size(), Is.is(2));
    }

    @Test
    public void t08_putBreakOptionWorks() throws Exception {
        api.putBreakOption("APILEASE", "FIXED", "TENANT", dt(2015, 1, 1), dt(2014, 7, 1), null, "Test");
        api.putBreakOption("APILEASE", "ROLLING", "MUTUAL", dt(2019, 1, 1), null, "6m", "Test");
        Assert.assertThat(leases.findLeaseByReference("APILEASE").getBreakOptions().size(), Is.is(2));
    }

}
