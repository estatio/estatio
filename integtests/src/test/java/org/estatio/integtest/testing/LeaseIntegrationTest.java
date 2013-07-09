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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;

import org.hamcrest.core.Is;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import org.estatio.dom.agreement.AgreementRole;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseConstants;
import org.estatio.dom.lease.LeaseItem;
import org.estatio.dom.lease.LeaseItemType;
import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.lease.LeaseTermForIndexableRent;
import org.estatio.dom.lease.LeaseTermForServiceCharge;
import org.estatio.dom.lease.LeaseTermStatus;
import org.estatio.dom.lease.Leases.InvoiceRunType;
import org.estatio.dom.party.Party;
import org.estatio.fixture.EstatioTransactionalObjectsFixture;
import org.estatio.integtest.AbstractEstatioIntegrationTest;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LeaseIntegrationTest extends AbstractEstatioIntegrationTest {

    @BeforeClass
    public static void setupTransactionalData() {
        app.install(new EstatioTransactionalObjectsFixture());
    }

    private Lease leaseTopModel;

    @Before
    public void setup() {
        leaseTopModel = app.leases.findLeaseByReference("OXF-TOPMODEL-001");
    }

    @Test
    public void t01_numberOfLeaseRolesIs3() throws Exception {
        assertThat(leaseTopModel.getRoles().size(), is(3));
    }

    @Test
    public void t02_leaseRoleCanBeFound() throws Exception {
        Party party = app.parties.findPartyByReferenceOrName("TOPMODEL");
        AgreementRole role = app.agreementRoles.findByAgreementAndPartyAndTypeAndStartDate(leaseTopModel, party, app.agreementRoleTypes.findByTitle(LeaseConstants.ART_TENANT), null);
        Assert.assertNotNull(role);
    }

    @Test
    public void t02b_numberOfleaseActorsIs3() throws Exception {
        assertThat(leaseTopModel.getRoles().size(), is(3));
    }

    @Test
    public void t03_indexationFrequencyCannotBeNull() throws Exception {
        List<LeaseTerm> allLeaseTerms = app.leaseTerms.allLeaseTerms();
        LeaseTerm term = (LeaseTerm) allLeaseTerms.get(0);
        Assert.assertNotNull(term.getFrequency());
    }

    @Test
    public void t04_nextDateCannotBeNull() throws Exception {
        List<LeaseTerm> allLeaseTerms = app.leaseTerms.allLeaseTerms();
        LeaseTerm term = (LeaseTerm) allLeaseTerms.get(0);
        Assert.assertNotNull(term.getFrequency().nextDate(new LocalDate(2012, 1, 1)));
    }

    @Test
    public void t05_leaseCanBeFound() throws Exception {
        Assert.assertEquals("OXF-TOPMODEL-001", app.leases.findLeaseByReference("OXF-TOPMODEL-001").getReference());
    }

    @Test
    public void t06_leasesCanBeFoundUsingWildcard() throws Exception {
        assertThat(app.leases.findLeasesByReference("OXF*").size(), is(4));
    }

    @Test
    public void t07_leaseHasXItems() throws Exception {
        assertThat(leaseTopModel.getItems().size(), is(3));
    }

    @Test
    public void t08_leaseItemCanBeFound() throws Exception {
        LeaseItem rentItem = leaseTopModel.findItem(LeaseItemType.RENT, new LocalDate(2010, 7, 15), BigInteger.valueOf(1));
        Assert.assertNotNull(rentItem);
    }

    @Test
    public void t09_leaseTermForRentCanBeFound() throws Exception {
        LeaseItem item = leaseTopModel.findItem(LeaseItemType.RENT, new LocalDate(2010, 7, 15), BigInteger.valueOf(1));
        LeaseTermForIndexableRent leaseTerm = (LeaseTermForIndexableRent) item.findTerm(new LocalDate(2010, 7, 15));
        Assert.assertNotNull(leaseTerm);
        BigDecimal baseValue = leaseTerm.getBaseValue();
        Assert.assertEquals(new BigDecimal("20000.00"), baseValue);
    }

    @Test
    public void t09_leaseTermForServiceChargeCanBeFound() throws Exception {
        LeaseItem item = (LeaseItem) leaseTopModel.findItem(LeaseItemType.SERVICE_CHARGE, new LocalDate(2010, 7, 15), BigInteger.valueOf(1));
        Assert.assertThat(item.getTerms().size(), Is.is(1));
        LeaseTermForServiceCharge leaseTerm = (LeaseTermForServiceCharge) item.findTerm(new LocalDate(2010, 7, 15));
        Assert.assertThat(leaseTerm.getBudgetedValue(), Is.is(new BigDecimal("6000.00")));
    }

    @Test
    public void t10_leaseTermForIndexableRentVerifiedCorrectly() throws Exception {
        LeaseItem item = leaseTopModel.findItem(LeaseItemType.RENT, new LocalDate(2010, 7, 15), BigInteger.valueOf(1));
        LeaseTermForIndexableRent term = (LeaseTermForIndexableRent) item.getTerms().first();
        term.verify();
        assertThat(term.getBaseIndexValue(), is(BigDecimal.valueOf(137.6).setScale(4)));
        assertThat(term.getNextIndexValue(), is(BigDecimal.valueOf(101.2).setScale(4)));
        assertThat(term.getIndexationPercentage(), is(BigDecimal.valueOf(1).setScale(1)));
        assertThat(term.getIndexedValue(), is(BigDecimal.valueOf(20200).setScale(4)));
    }

    @Test
    public void t10_leaseTermForServiceChargeVerifiedCorrectly() throws Exception {
        LeaseItem item = leaseTopModel.findItem(LeaseItemType.SERVICE_CHARGE, new LocalDate(2010, 7, 15), BigInteger.valueOf(1));
        assertThat(item.getTerms().size(), is(1));
        LeaseTerm term = item.getTerms().first();
        term.verify();
        assertNotNull(item.findTerm(new LocalDate(2012, 7, 15)));
    }

    @Test
    public void t11_leaseVerifiesWell() throws Exception {
        leaseTopModel.verify();
        LeaseItem item1 = leaseTopModel.findItem(LeaseItemType.RENT, new LocalDate(2010, 7, 15), BigInteger.valueOf(1));
        assertNotNull(item1.findTerm(new LocalDate(2012, 7, 15)));
        LeaseItem item2 = leaseTopModel.findItem(LeaseItemType.SERVICE_CHARGE, new LocalDate(2010, 7, 15), BigInteger.valueOf(1));
        item2.verify();
        assertNotNull(item2.findTerm(new LocalDate(2012, 7, 15)));
    }

    @Test
    public void t12_leaseVerifiesWell() throws Exception {
        Lease leaseMediax = app.leases.findLeaseByReference("OXF-MEDIAX-002");
        leaseMediax.verify();
        LeaseItem item = leaseMediax.findItem(LeaseItemType.SERVICE_CHARGE, new LocalDate(2008, 1, 1), BigInteger.valueOf(1));
        assertNotNull(item.findTerm(new LocalDate(2008, 1, 1)));
        assertThat(item.getTerms().last().getTrialValue(), is(BigDecimal.valueOf(6600).setScale(2)));
    }

    @Test
    public void t12b_thereAreTwoLeaseTems() throws Exception {
        Lease leaseMediax = app.leases.findLeaseByReference("OXF-POISON-003");
        LeaseItem item = leaseMediax.findItem(LeaseItemType.RENT, new LocalDate(2011, 1, 1), BigInteger.valueOf(1));
        assertThat(item.getTerms().size(), is(4));
        assertNotNull(item.findTerm(new LocalDate(2011, 1, 1)));

    }

    @Test
    public void t12b_leaseVerifiesWell() throws Exception {
        Lease leasePoison = app.leases.findLeaseByReference("OXF-POISON-003");
        leasePoison.verify();
        LeaseItem item = leasePoison.findItem(LeaseItemType.SERVICE_CHARGE, new LocalDate(2011, 1, 1), BigInteger.valueOf(1));
        assertNotNull(item.findTerm(new LocalDate(2011, 1, 1)));
        assertThat(item.getTerms().last().getTrialValue(), is(BigDecimal.valueOf(13640).setScale(2)));

    }

    @Test
    public void t13_leaseTermApprovesWell() throws Exception {
        LeaseItem item = leaseTopModel.findItem(LeaseItemType.RENT, new LocalDate(2010, 7, 15), BigInteger.valueOf(1));
        LeaseTerm term = (LeaseTerm) item.getTerms().toArray()[0];
        term.lock();
        assertThat(term.getStatus(), is(LeaseTermStatus.APPROVED));
        assertThat(term.getApprovedValue(), is(BigDecimal.valueOf(20200).setScale(2)));
    }

    @Test
    public void t141a_invoiceItemsNotCreated() throws Exception {
        LeaseItem item = leaseTopModel.findItem(LeaseItemType.RENT, new LocalDate(2010, 7, 15), BigInteger.ONE);
        LeaseTerm term = item.findTerm(new LocalDate(2010, 7, 15));
        term.verify();
        term.calculate(new LocalDate(2010, 7, 2), new LocalDate(2010, 7, 1));
        assertThat(term.getInvoiceItems().size(), is(0));
    }

    @Test
    public void t14b_invoiceItemsForRentCreated() throws Exception {
        app.settings.updateEpochDate(null);
        LeaseItem item = leaseTopModel.findItem(LeaseItemType.RENT, new LocalDate(2010, 7, 15), BigInteger.valueOf(1));
        // unapproved doesn't work
        LeaseTerm term = (LeaseTerm) item.getTerms().first();
        term.calculate(new LocalDate(2010, 7, 1), new LocalDate(2010, 7, 1));
        Assert.assertNull(term.findUnapprovedInvoiceItemFor(term, new LocalDate(2010, 7, 1), new LocalDate(2010, 6, 1)));
        // let's approve
        term.lock();
        // partial period
        term.calculate(new LocalDate(2010, 7, 1), new LocalDate(2010, 7, 1));
        assertThat(term.findUnapprovedInvoiceItemFor(term, new LocalDate(2010, 7, 1), new LocalDate(2010, 7, 1)).getNetAmount(), is(new BigDecimal(4239.13).setScale(2, RoundingMode.HALF_UP)));
        // full term
        term.calculate(new LocalDate(2010, 10, 1), new LocalDate(2010, 10, 1));
        assertThat(term.findUnapprovedInvoiceItemFor(term, new LocalDate(2010, 10, 1), new LocalDate(2010, 10, 1)).getNetAmount(), is(new BigDecimal(5000.00).setScale(2, RoundingMode.HALF_UP)));
        // invoice after effective date
        term.calculate(new LocalDate(2010, 10, 1), new LocalDate(2011, 4, 1));
        assertThat(term.findUnapprovedInvoiceItemFor(term, new LocalDate(2010, 10, 1), new LocalDate(2011, 4, 1)).getNetAmount(), is(new BigDecimal(5050.00).setScale(2, RoundingMode.HALF_UP)));
        // invoice after effective date with mock
        app.settings.updateEpochDate(new LocalDate(2011, 1, 1));
        term.calculate(new LocalDate(2010, 10, 1), new LocalDate(2011, 4, 1));
        assertThat(term.findUnapprovedInvoiceItemFor(term, new LocalDate(2010, 10, 1), new LocalDate(2011, 4, 1)).getNetAmount(), is(new BigDecimal(50.00).setScale(2, RoundingMode.HALF_UP)));
        // remove
        term.removeUnapprovedInvoiceItemsForDate(new LocalDate(2010, 10, 1), new LocalDate(2010, 10, 1));
        app.settings.updateEpochDate(null);
        assertThat(term.getInvoiceItems().size(), is(2));
    }


    @Test
    public void t15_invoiceItemsForServiceChargeCreated() throws Exception {
        app.settings.updateEpochDate(null);
        LeaseItem item = leaseTopModel.findItem(LeaseItemType.SERVICE_CHARGE, new LocalDate(2010, 7, 15), BigInteger.valueOf(1));
        LeaseTermForServiceCharge term = (LeaseTermForServiceCharge) item.getTerms().first();
        term.lock();
        // partial period
        term.calculate(new LocalDate(2010, 7, 1), new LocalDate(2010, 7, 1));
        assertThat(term.findUnapprovedInvoiceItemFor(term, new LocalDate(2010, 7, 1), new LocalDate(2010, 7, 1)).getNetAmount(), is(new BigDecimal(1271.74).setScale(2, RoundingMode.HALF_UP)));
        // full period
        term.calculate(new LocalDate(2010, 10, 1), new LocalDate(2010, 10, 1));
        assertThat(term.findUnapprovedInvoiceItemFor(term, new LocalDate(2010, 10, 1), new LocalDate(2010, 10, 1)).getNetAmount(), is(new BigDecimal(1500.00).setScale(2, RoundingMode.HALF_UP)));
        // reconcile without mock
        term.calculate(new LocalDate(2010, 10, 1), new LocalDate(2011, 10, 1));
        assertThat(term.findUnapprovedInvoiceItemFor(term, new LocalDate(2010, 10, 1), new LocalDate(2011, 10, 1)).getNetAmount(), is(new BigDecimal(1650.00).setScale(2, RoundingMode.HALF_UP)));
        // reconcile with mock date
        app.settings.updateEpochDate(new LocalDate(2011, 10, 1));
        term.calculate(new LocalDate(2010, 10, 1), new LocalDate(2011, 10, 1));
        assertThat(term.findUnapprovedInvoiceItemFor(term, new LocalDate(2010, 10, 1), new LocalDate(2011, 10, 1)).getNetAmount(), is(new BigDecimal(150.00).setScale(2, RoundingMode.HALF_UP)));
        app.settings.updateEpochDate(null);
    }

    @Ignore
    @Test
    public void t16_bulkLeaseCalculate() throws Exception {
        LeaseItem item = leaseTopModel.findItem(LeaseItemType.SERVICE_CHARGE, new LocalDate(2010, 7, 15), BigInteger.valueOf(1));
        LeaseTermForServiceCharge term = (LeaseTermForServiceCharge) item.getTerms().first();
        // call calulate on leaseTopModel
        leaseTopModel.calculate(new LocalDate(2010, 10, 1), new LocalDate(2010, 10, 1), InvoiceRunType.NORMAL_RUN);
        assertThat(term.getInvoiceItems().size(), is(2)); // the previous test
                                                          // already supplied
                                                          // one
    }
}
