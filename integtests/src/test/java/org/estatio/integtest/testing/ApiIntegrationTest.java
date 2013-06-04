package org.estatio.integtest.testing;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.estatio.dom.asset.Properties;
import org.estatio.dom.asset.Unit;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseUnits;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.party.Parties;
import org.estatio.jdo.LeasesJdo;
import org.estatio.jdo.PartiesJdo;
import org.estatio.jdo.PropertiesJdo;
import org.hamcrest.core.Is;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ApiIntegrationTest extends AbstractEstatioIntegrationTest {

    private static final LocalDate START_DATE = new LocalDate(2012, 1, 1);
    private Leases leases;
    private Properties properties;
    private Parties parties;
    private LeaseUnits leaseUnits;

    @Before
    public void setup() {
        leases = getIsft().getService(LeasesJdo.class);
        properties = getIsft().getService(PropertiesJdo.class);
        parties = getIsft().getService(PartiesJdo.class);
        leaseUnits = getIsft().getService(LeaseUnits.class);
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
        Assert.assertThat(properties.findPropertiesByReference("APIPROP").size(), Is.is(1));
    }

    @Test
    public void t02_putOrganisation() {
        api.putOrganisation("APITENANT", "API Tenant");
        api.putOrganisation("APILANDLORD", "API Landlord");
        Assert.assertThat(parties.findPartiesByReference("API*").size(), Is.is(2));
    }

    @Test
    public void t03_putLeaseWorks() throws Exception {
        api.putLease("APILEASE", "Lease", "APITENANT", "APILANDLORD", null, START_DATE, new LocalDate(2021, 12, 31), null, "APIPROP");
        Assert.assertNotNull(leases.findByReference("APILEASE"));
    }

    @Test
    public void t04_putLeaseUnitWorks() throws Exception {
        api.putLeaseUnit("APILEASE", "APIUNIT", START_DATE, null, null, null, "ABIBRAND", "APISECTOR", "APIACTIVITY");
        Lease l = leases.findByReference("APILEASE");
        Unit u = units.findUnitByReference("APIUNIT");
        Assert.assertNotNull(leaseUnits.find(l, u, START_DATE));
        Assert.assertNotNull(leaseUnits.find(l, u, START_DATE));

    }

    @Test
    public void t04_putLeaseItemWorks() throws Exception {
        api.putLeaseItem("APILEASE", "APITENANT", "APIUNIT", "RENT", BigInteger.valueOf(1), START_DATE, new LocalDate(2012, 12, 31), "APICHARGE", null, "QUARTERLY_IN_ADVANCE", "DIRECT_DEBIT");
        Assert.assertThat(leases.findByReference("APILEASE").getItems().size(), Is.is(1));
    }

    @Test
    public void t05_putLeaseTermWorks() throws Exception {
        api.putLeaseTermForIndexableRent("APILEASE", "APITENANT", "APIUNIT", BigInteger.valueOf(1), "RENT", START_DATE, BigInteger.valueOf(1), START_DATE, new LocalDate(2012, 12, 31), "NEW", BigDecimal.valueOf(12345), null, null, BigDecimal.valueOf(12345), null, null,
                null, "APIINDEX", "YEARLY", null, null, null, null, null, null, null, null, null);
        api.putLeaseTermForIndexableRent("APILEASE", "APITENANT", "APIUNIT", BigInteger.valueOf(1), "RENT", START_DATE, BigInteger.valueOf(2), new LocalDate(2013, 1, 1), new LocalDate(2013, 12, 31), "NEW", BigDecimal.valueOf(12345), null, null, BigDecimal.valueOf(12345), null, null,
                null, "APIINDEX", "YEARLY", null, null, null, null, null, null, null, null, null);
        Lease lease = leases.findByReference("APILEASE");
        Assert.assertThat(lease.getItems().first().getTerms().size(), Is.is(2));
    }

}
