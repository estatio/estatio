/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
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
package org.estatio.integration.tests.api;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.estatio.api.Api;
import org.estatio.dom.agreement.AgreementRole;
import org.estatio.dom.agreement.AgreementRoleType;
import org.estatio.dom.agreement.AgreementRoleTypes;
import org.estatio.dom.asset.Properties;
import org.estatio.dom.asset.Unit;
import org.estatio.dom.asset.Units;
import org.estatio.dom.communicationchannel.CommunicationChannelType;
import org.estatio.dom.communicationchannel.CommunicationChannels;
import org.estatio.dom.communicationchannel.EmailAddresses;
import org.estatio.dom.communicationchannel.PhoneOrFaxNumbers;
import org.estatio.dom.communicationchannel.PostalAddresses;
import org.estatio.dom.geography.Countries;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseConstants;
import org.estatio.dom.lease.LeaseUnits;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.fixture.EstatioTransactionalObjectsFixture;
import org.estatio.integration.tests.EstatioIntegrationTest;
import org.estatio.services.clock.ClockService;
import org.hamcrest.core.Is;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ApiIntegrationTest extends EstatioIntegrationTest {

    private static final LocalDate START_DATE = new LocalDate(2012, 1, 1);
    private Api api;
    private Leases leases;
    private Properties properties;
    private Parties parties;
    private CommunicationChannels communicationChannels;
    private Units<?> units;
    private LeaseUnits leaseUnits;
    private AgreementRoleTypes agreementRoleTypes;
    private ClockService clockService;
    private PostalAddresses postalAddresses;
    private PhoneOrFaxNumbers phoneOrFaxNumbers;
    private EmailAddresses emailAddresses;
    private Countries countries;

    @BeforeClass
    public static void setupTransactionalData() {
        scenarioExecution().install(new EstatioTransactionalObjectsFixture());
    }

    @Before
    public void setUp() throws Exception {
        api = service(Api.class);
        leases = service(Leases.class);
        properties = service(Properties.class);
        parties = service(Parties.class);
        communicationChannels = service(CommunicationChannels.class);
        units = (Units<?>) service(Units.class);
        leaseUnits = service(LeaseUnits.class);
        agreementRoleTypes = service(AgreementRoleTypes.class);
        clockService = service(ClockService.class);
        phoneOrFaxNumbers = service(PhoneOrFaxNumbers.class);
        emailAddresses = service(EmailAddresses.class);
        postalAddresses = service(PostalAddresses.class);
        countries = service(Countries.class);

    }

    @Test
    public void t00_refData() throws Exception {
        api.putCountry("NLD", "NL", "Netherlands");
        api.putState("NH", "NH", "NLD");
        api.putTax("APITAX", "APITAX", "APITAX", BigDecimal.valueOf(21.0), new LocalDate(1980, 1, 1));
        api.putCharge("APICHARGE", "APICHARGE", "API CHARGE", "APITAX");
    }

    @Test
    public void t01_putAsset() throws Exception {
        api.putProperty("APIPROP", "Apiland", "SHOPPING_CENTER", null, null, null, "HELLOWORLD", "APIFORMAT");
        api.putUnit("APIUNIT", "APIPROP", "APIONWER", "Name", "BOUTIQUE", null, null, null, null, null, null, null, null, null, null, null, null);
        Assert.assertThat(properties.findPropertiesByReference("APIPROP").size(), Is.is(1));
    }

    @Test
    public void t02_putOrganisation() {
        api.putOrganisation("APITENANT", "API Tenant");
        api.putOrganisation("APILANDLORD", "API Landlord");
        Assert.assertThat(parties.findParties("API*").size(), Is.is(2));
    }

    @Test
    public void t03_putPartyCommunicationChannels() {
        api.putPartyCommunicationChannels("APITENANT", "APITENANT", "Address1", "Address2", "CITY", "Postal Code", "NH", "NLD", "+31987654321", "+31876543210", "test@api.local");
        Assert.assertNotNull(communicationChannels.findByReferenceAndType("APITENANT", CommunicationChannelType.POSTAL_ADDRESS));
        Assert.assertNotNull(communicationChannels.findByReferenceAndType("APITENANT", CommunicationChannelType.FAX_NUMBER));
        Assert.assertNotNull(communicationChannels.findByReferenceAndType("APITENANT", CommunicationChannelType.PHONE_NUMBER));
    }

    @Test
    public void t03_putPartyCommunicationChannelsWithoutReference() {
        api.putPartyCommunicationChannels("APITENANT", null, "NewAddress1", "NewAddress2", "NewCity", "NewPostalCode", "NH", "NLD", "+31222222222", "+31333333333", "test@example.com");
        Party party = parties.findPartyByReference("APITENANT");
        Assert.assertNotNull(postalAddresses.findByAddress(party, "NewAddress1", "NewPostalCode", "NewCity", countries.findCountryByReference("NLD")));
        Assert.assertNotNull(phoneOrFaxNumbers.findByPhoneOrFaxNumber(party, "+31222222222"));
        Assert.assertNotNull(emailAddresses.findByEmailAddress(party, "test@example.com"));
    }

    @Test
    public void t04_putLeaseWorks() throws Exception {
        api.putLease("APILEASE", "Lease", "APITENANT", "APILANDLORD", null, START_DATE, new LocalDate(2021, 12, 31), null, "APIPROP");
        Lease lease = leases.findLeaseByReference("APILEASE");
        Assert.assertNotNull(lease);
        Assert.assertThat(lease.getRoles().size(), Is.is(2));
    }

    @Test
    public void t05_putLeaseUnitWorks() throws Exception {
        api.putLeaseUnit("APILEASE", "APIUNIT", START_DATE, null, null, null, "APISIZE", "ABIBRAND", "APISECTOR", "APIACTIVITY");
        Lease l = leases.findLeaseByReference("APILEASE");
        Unit u = units.findUnitByReference("APIUNIT");
        Assert.assertNotNull(leaseUnits.findByLeaseAndUnitAndStartDate(l, u, START_DATE));
        Assert.assertNotNull(leaseUnits.findByLeaseAndUnitAndStartDate(l, u, START_DATE));
    }

    @Test
    public void t05b_putLeasePostalAddress() throws Exception {
        api.putLeasePostalAddress("APITENANT", LeaseConstants.ART_TENANT, "APILEASE", "Address1", "Address2", "PostalCode", "City", "NH", "NLD", BigInteger.valueOf(1));
        final Lease l = leases.findLeaseByReference("APILEASE");
        final AgreementRoleType artTenant = agreementRoleTypes.findByTitle(LeaseConstants.ART_TENANT);
        final AgreementRole ar = l.findRoleWithType(artTenant, clockService.now());
        Assert.assertThat(ar.getCommunicationChannels().size(), Is.is(1));
    }

    @Test
    public void t05b_putLeasePostalAddress_idempotent() throws Exception {
        api.putLeasePostalAddress("APITENANT", LeaseConstants.ART_TENANT, "APILEASE", "Address1", "Address2", "PostalCode", "City", "NH", "NLD", BigInteger.valueOf(1));
        final Lease l = leases.findLeaseByReference("APILEASE");
        final AgreementRoleType artTenant = agreementRoleTypes.findByTitle(LeaseConstants.ART_TENANT);
        final AgreementRole ar = l.findRoleWithType(artTenant, clockService.now());
        Assert.assertThat(ar.getCommunicationChannels().size(), Is.is(1));
    }

    @Test
    public void t06_putLeaseItemWorks() throws Exception {
        api.putLeaseItem("APILEASE", "APITENANT", "APIUNIT", "RENT", BigInteger.valueOf(1), START_DATE, new LocalDate(2012, 12, 31), "APICHARGE", null, "QUARTERLY_IN_ADVANCE", "DIRECT_DEBIT");
        Assert.assertThat(leases.findLeaseByReference("APILEASE").getItems().size(), Is.is(1));
    }

    @Test
    public void t07_putLeaseTermWorks() throws Exception {
        api.putLeaseTermForIndexableRent("APILEASE", "APITENANT", "APIUNIT", BigInteger.valueOf(1), "RENT", START_DATE, BigInteger.valueOf(1), START_DATE, new LocalDate(2012, 12, 31), "NEW", null, null, BigDecimal.valueOf(12345), BigDecimal.valueOf(12345), null, null, null, "APIINDEX", "YEARLY",
                null, null, null, null, null, null, null, null, null);
        api.putLeaseTermForIndexableRent("APILEASE", "APITENANT", "APIUNIT", BigInteger.valueOf(1), "RENT", START_DATE, BigInteger.valueOf(2), new LocalDate(2013, 1, 1), new LocalDate(2013, 12, 31), "NEW", null, null, BigDecimal.valueOf(12345), BigDecimal.valueOf(12345), null, null, null,
                "APIINDEX", "YEARLY", null, null, null, null, null, null, null, null, null);
        Lease lease = leases.findLeaseByReference("APILEASE");
        Assert.assertThat(lease.getItems().first().getTerms().size(), Is.is(2));
    }

}
