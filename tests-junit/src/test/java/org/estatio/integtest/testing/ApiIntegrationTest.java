package org.estatio.integtest.testing;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.estatio.api.Api;
import org.estatio.dom.asset.Properties;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.party.Parties;
import org.estatio.integtest.IntegrationSystemForTestRule;
import org.estatio.jdo.LeasesJdo;
import org.estatio.jdo.PartiesJdo;
import org.estatio.jdo.PropertiesJdo;
import org.hamcrest.core.Is;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import org.apache.isis.core.integtestsupport.IsisSystemForTest;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ApiIntegrationTest {

    private Api api;

    private Leases leases;
    
    @Rule
    public IntegrationSystemForTestRule integrationTestRule = new IntegrationSystemForTestRule();

    public IsisSystemForTest getIsft() {
        return integrationTestRule.getIsisSystemForTest();
    }

    @Before
    public void init() {
        api = getIsft().getService(Api.class);
        leases = getIsft().getService(LeasesJdo.class);
    }

    @Test
    public void t00_refData() throws Exception {
        api.putCountry("NLD", "NL", "Netherlands");
        api.putTax("APITAX", "APITAX");
        api.putCharge("APICHARGE", "APICHARGE", "API CHARGE", "APITAX");
    }

    @Test
    public void t01_putAsset() throws Exception {
        api.putProperty("APIPROP", "Apiland", "SHOPPING_CENTER", null, null, null, "HELLOWORLD");
        api.putUnit("APIUNIT", "APIPROP", "APIONWER", "Name", "BOUTIQUE", null, null, null, null, null, null, null, null, null, null, null, null);
        Properties properties = getIsft().getService(PropertiesJdo.class);
        Assert.assertThat(properties.findPropertiesByReference("APIPROP").size(), Is.is(1));
    }

    @Test
    public void t02_putOrganisation() {
        api.putOrganisation("APITENANT", "API Tenant");
        api.putOrganisation("APILANDLORD", "API Landlord");
        Parties p = getIsft().getService(PartiesJdo.class);
        Assert.assertThat(p.findPartiesByReference("API*").size(), Is.is(2));
    }

    @Test
    public void t03_putLeaseWorks() throws Exception {
        api.putLease("APILEASE", "Lease", "APITENANT", "APILANDLORD", null, new LocalDate(2012, 1, 1), new LocalDate(2021, 12, 31), null, "APIPROP");
        Leases leases = getIsft().getService(LeasesJdo.class);
        Assert.assertNotNull(leases.findByReference("APILEASE"));
    }

    @Test
    public void t04_putLeaseItemWorks() throws Exception {
        api.putLeaseItem("APILEASE", "APITENANT", "APIUNIT", "RENT", BigInteger.valueOf(1), new LocalDate(2012, 1, 1), new LocalDate(2012, 12, 31), null, null, "APICHARGE", null, "QUARTERLY_IN_ADVANCE", "DIRECT_DEBIT");
        Leases leases = getIsft().getService(LeasesJdo.class);
        Assert.assertThat(leases.findByReference("APILEASE").getItems().size(), Is.is(1));
    }

    @Test
    public void t05_putLeaseTermWorks() throws Exception {
        api.putLeaseTermForIndexableRent("APILEASE", "APITENANT", "APIUNIT", BigInteger.valueOf(1), "RENT", new LocalDate(2012, 1, 1), BigInteger.valueOf(1), new LocalDate(2012, 1, 1), new LocalDate(2012, 12, 31), "NEW", BigDecimal.valueOf(12345), null, null, BigDecimal.valueOf(12345), null, null,
                null, "APIINDEX", "YEARLY", null, null, null, null, null, null, null, null, null);
        api.putLeaseTermForIndexableRent("APILEASE", "APITENANT", "APIUNIT", BigInteger.valueOf(1), "RENT", new LocalDate(2012, 1, 1), BigInteger.valueOf(2), new LocalDate(2013, 1, 1), new LocalDate(2013, 12, 31), "NEW", BigDecimal.valueOf(12345), null, null, BigDecimal.valueOf(12345), null, null,
                null, "APIINDEX", "YEARLY", null, null, null, null, null, null, null, null, null);
        Lease lease = leases.findByReference("APILEASE");
        Assert.assertThat(lease.getItems().first().getTermsWorkaround().size() , Is.is(2));
    }

}
