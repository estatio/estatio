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
package org.estatio.integtest.testing;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.hamcrest.core.Is;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import org.estatio.dom.agreement.AgreementRole;
import org.estatio.dom.agreement.AgreementRoleCommunicationChannelType;
import org.estatio.dom.agreement.AgreementRoleType;
import org.estatio.dom.asset.Unit;
import org.estatio.dom.communicationchannel.CommunicationChannelType;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseConstants;
import org.estatio.fixture.EstatioTransactionalObjectsFixture;
import org.estatio.integtest.AbstractEstatioIntegrationTest;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ApiIntegrationTest extends AbstractEstatioIntegrationTest {

    private static final LocalDate START_DATE = new LocalDate(2012, 1, 1);

    @BeforeClass
    public static void setupTransactionalData() {
        app.install(new EstatioTransactionalObjectsFixture());
    }

    @Test
    public void t00_refData() throws Exception {
        app.api.putCountry("NLD", "NL", "Netherlands");
        app.api.putState("NH", "NH", "NLD");
        app.api.putTax("APITAX", "APITAX", "APITAX", BigDecimal.valueOf(21.0), new LocalDate(1980, 1, 1));
        app.api.putCharge("APICHARGE", "APICHARGE", "API CHARGE", "APITAX");
    }

    @Test
    public void t01_putAsset() throws Exception {
        app.api.putProperty("APIPROP", "Apiland", "SHOPPING_CENTER", null, null, null, "HELLOWORLD");
        app.api.putUnit("APIUNIT", "APIPROP", "APIONWER", "Name", "BOUTIQUE", null, null, null, null, null, null, null, null, null, null, null, null);
        Assert.assertThat(app.properties.findPropertiesByReference("APIPROP").size(), Is.is(1));
    }

    @Test
    public void t02_putOrganisation() {
        app.api.putOrganisation("APITENANT", "API Tenant");
        app.api.putOrganisation("APILANDLORD", "API Landlord");
        Assert.assertThat(app.parties.findParties("API*").size(), Is.is(2));
    }

    @Test
    public void t03_putPartyCommunicationChannels() {
        app.api.putPartyCommunicationChannels("APITENANT", "APITENANT", "Address1", "Address2", "CITY", "Postal Code", "NH", "NLD", "+31987654321", "+31876543210");
        Assert.assertNotNull(app.communicationChannels.findByReferenceAndType("APITENANT", CommunicationChannelType.POSTAL_ADDRESS));
        Assert.assertNotNull(app.communicationChannels.findByReferenceAndType("APITENANT", CommunicationChannelType.FAX_NUMBER));
        Assert.assertNotNull(app.communicationChannels.findByReferenceAndType("APITENANT", CommunicationChannelType.PHONE_NUMBER));
    }

    @Test
    public void t04_putLeaseWorks() throws Exception {
        app.api.putLease("APILEASE", "Lease", "APITENANT", "APILANDLORD", null, START_DATE, new LocalDate(2021, 12, 31), null, "APIPROP");
        Lease lease = app.leases.findLeaseByReference("APILEASE");
        Assert.assertNotNull(lease);
        Assert.assertThat(lease.getRoles().size(), Is.is(2));
    }

    @Test
    public void t05_putLeaseUnitWorks() throws Exception {
        app.api.putLeaseUnit("APILEASE", "APIUNIT", START_DATE, null, null, null, "ABIBRAND", "APISECTOR", "APIACTIVITY");
        Lease l = app.leases.findLeaseByReference("APILEASE");
        Unit u = app.units.findUnitByReference("APIUNIT");
        Assert.assertNotNull(app.leaseUnits.findByLeaseAndUnitAndStartDate(l, u, START_DATE));
        Assert.assertNotNull(app.leaseUnits.findByLeaseAndUnitAndStartDate(l, u, START_DATE));
    }

    @Test
    public void t05b_putLeasePostalAddress() throws Exception {
        final AgreementRoleCommunicationChannelType arcttInvoiceAddress = app.agreementRoleCommunicationChannelTypes.findByTitle(LeaseConstants.ARCCT_INVOICE_ADDRESS);
        app.api.putLeasePostalAddress("APITENANT", "APILEASE", "Address1", "Address2", "PostalCode", "City", "NH", "NLD", arcttInvoiceAddress);
        final Lease l = app.leases.findLeaseByReference("APILEASE");
        final AgreementRoleType artTenant = app.agreementRoleTypes.findByTitle(LeaseConstants.ART_TENANT);
        final AgreementRole ar = l.findRoleWithType(artTenant, app.clock.now());
        Assert.assertThat(ar.getCommunicationChannels().size(), Is.is(1));
    }

    @Test
    public void t06_putLeaseItemWorks() throws Exception {
        app.api.putLeaseItem("APILEASE", "APITENANT", "APIUNIT", "RENT", BigInteger.valueOf(1), START_DATE, new LocalDate(2012, 12, 31), "APICHARGE", null, "QUARTERLY_IN_ADVANCE", "DIRECT_DEBIT");
        Assert.assertThat(app.leases.findLeaseByReference("APILEASE").getItems().size(), Is.is(1));
    }

    @Test
    public void t07_putLeaseTermWorks() throws Exception {
        app.api.putLeaseTermForIndexableRent("APILEASE", "APITENANT", "APIUNIT", BigInteger.valueOf(1), "RENT", START_DATE, BigInteger.valueOf(1), START_DATE, new LocalDate(2012, 12, 31), "NEW", null, null, BigDecimal.valueOf(12345), BigDecimal.valueOf(12345), null, null, null, "APIINDEX", "YEARLY",
                null, null, null, null, null, null, null, null, null);
        app.api.putLeaseTermForIndexableRent("APILEASE", "APITENANT", "APIUNIT", BigInteger.valueOf(1), "RENT", START_DATE, BigInteger.valueOf(2), new LocalDate(2013, 1, 1), new LocalDate(2013, 12, 31), "NEW", null, null, BigDecimal.valueOf(12345), BigDecimal.valueOf(12345), null, null, null,
                "APIINDEX", "YEARLY", null, null, null, null, null, null, null, null, null);
        Lease lease = app.leases.findLeaseByReference("APILEASE");
        Assert.assertThat(lease.getItems().first().getTerms().size(), Is.is(2));
    }

}
