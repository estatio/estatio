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
package org.estatio.dom.lease.invoicing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.estatio.dom.agreement.Agreement;
import org.estatio.dom.agreement.AgreementRole;
import org.estatio.dom.agreement.AgreementRoleType;
import org.estatio.dom.agreement.AgreementRoleTypes;
import org.estatio.dom.agreement.AgreementRoles;
import org.estatio.dom.agreement.AgreementTypes;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.invoice.Invoices;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.lease.InvoicingFrequency;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseItem;
import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.lease.LeaseTermForTesting;
import org.estatio.dom.lease.Leases.InvoiceRunType;
import org.estatio.dom.lease.invoicing.InvoiceCalculationService.CalculationResult;
import org.estatio.dom.lease.invoicing.InvoiceCalculationService.CalculationResultsUtil;
import org.estatio.dom.party.Party;
import org.estatio.dom.tax.Tax;
import org.estatio.dom.tax.TaxRate;
import org.estatio.dom.tax.TaxRates;
import org.estatio.dom.valuetypes.LocalDateInterval;
import org.estatio.services.settings.EstatioSettingsService;

public class InvoiceCalculationServiceTest {
    private static final LocalDate LEASE_START_DATE = new LocalDate(2011, 11, 1);
    private static final LocalDate LEASE_END_DATE = new LocalDate(2011, 11, 1).plusYears(10).minusDays(1);

    private Lease lease;
    private LeaseItem leaseItem;
    private LeaseTermForTesting leaseTerm;

    private Tax tax;
    private TaxRate taxRate;
    private Charge charge;

    private AgreementRoleType artLandlord;
    private AgreementRoleType artTenant;

    @Mock
    private TaxRates mockTaxRates;

    @Mock
    private Invoices mockInvoices;

    @Mock
    private InvoiceItemsForLease mockInvoiceItemsForLease;

    @Mock
    private AgreementRoles mockAgreementRoles;

    @Mock
    private AgreementRoleTypes mockAgreementRoleTypes;

    @Mock
    private AgreementTypes mockAgreementTypes;

    @Mock
    private EstatioSettingsService mockSettings;

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    private InvoiceCalculationService ic;

    private InvoiceItemForLease invoiceItemForLease;

    @Before
    public void setup() {
        artLandlord = new AgreementRoleType();
        artLandlord.setTitle("Landlord");

        artTenant = new AgreementRoleType();
        artTenant.setTitle("Tenant");

        context.checking(new Expectations() {
            {
                allowing(mockAgreementRoleTypes).findByTitle("Landlord");
                will(returnValue(artLandlord));
                allowing(mockAgreementRoleTypes).findByTitle("Tenant");
                will(returnValue(artTenant));
            }
        });

        lease = new Lease();
        lease.injectAgreementRoles(mockAgreementRoles);
        lease.setStartDate(LEASE_START_DATE);
        lease.setEndDate(LEASE_END_DATE);

        leaseItem = new LeaseItem();
        leaseItem.setStartDate(LEASE_START_DATE);
        leaseItem.setInvoicingFrequency(InvoicingFrequency.QUARTERLY_IN_ADVANCE);
        leaseItem.setLease(lease);

        lease.getItems().add(leaseItem);

        leaseTerm = new LeaseTermForTesting();
        leaseTerm.injectInvoiceItemsForLease(mockInvoiceItemsForLease);
        leaseTerm.setLeaseItem(leaseItem);

        leaseItem.getTerms().add(leaseTerm);

        tax = new Tax();
        tax.injectTaxRates(mockTaxRates);
        tax.setReference("VAT");

        taxRate = new TaxRate();
        taxRate.setPercentage(BigDecimal.valueOf(21));

        charge = new Charge();
        charge.setReference("RENT");
        charge.setTax(tax);

        invoiceItemForLease = new InvoiceItemForLease();
        invoiceItemForLease.modifyLeaseTerm(leaseTerm);

        invoiceItemForLease.injectInvoices(mockInvoices);
        invoiceItemForLease.injectAgreementRoleTypes(mockAgreementRoleTypes);
        invoiceItemForLease.injectAgreementTypes(mockAgreementTypes);

        ic = new InvoiceCalculationService();
    }

    @Test
    public void testCalculateFullQuarter() {

        leaseTerm.setStartDate(LEASE_START_DATE);
        leaseTerm.setValue(BigDecimal.valueOf(20000));

        CalculationResult result = ic.calculateLeaseTerm(leaseTerm, new LocalDate(2012, 1, 1), new LocalDate(2012, 1, 1));
        assertEquals(BigDecimal.valueOf(5000).setScale(2, RoundingMode.HALF_UP), result.getCalculatedValue());
    }

    @Test
    public void testValueForPeriod() {

        lease.setEndDate(null);
        leaseTerm.setStartDate(LEASE_START_DATE);
        leaseTerm.setValue(BigDecimal.valueOf(10000.22));

        BigDecimal result = ic.calculateSumForAllPeriods(leaseTerm, new LocalDate(2012, 1, 1), new LocalDate(2012, 1, 1), InvoicingFrequency.YEARLY_IN_ADVANCE);
        assertThat(result, is(BigDecimal.valueOf(10000.24).setScale(2, RoundingMode.HALF_UP)));
    }

    @Test
    public void testCalculateExactPeriod() {
        leaseTerm.setStartDate(new LocalDate(2012, 1, 1));
        leaseTerm.setEndDate(new LocalDate(2012, 3, 31));
        leaseTerm.setValue(BigDecimal.valueOf(20000));
        CalculationResult result = ic.calculateLeaseTerm(leaseTerm, new LocalDate(2012, 1, 1), new LocalDate(2012, 1, 1));
        assertEquals(BigDecimal.valueOf(5000).setScale(2, RoundingMode.HALF_UP), result.getCalculatedValue());
    }

    @Test
    public void testCalculateSingleMonth() {
        leaseTerm.setStartDate(new LocalDate(2012, 2, 1));
        leaseTerm.setEndDate(new LocalDate(2012, 2, 29));
        leaseTerm.setValue(BigDecimal.valueOf(20000));
        CalculationResult result = ic.calculateLeaseTerm(leaseTerm, new LocalDate(2012, 1, 1), new LocalDate(2012, 1, 1));
        assertEquals(BigDecimal.valueOf(1593.41).setScale(2, RoundingMode.HALF_UP), result.getCalculatedValue());
    }

    @Test
    public void testCalculateWithFrequency() {
        leaseTerm.setStartDate(new LocalDate(2012, 2, 1));
        leaseTerm.setEndDate(new LocalDate(2012, 2, 29));
        leaseTerm.setValue(BigDecimal.valueOf(20000));
        List<CalculationResult> results = ic.calculateWithFrequency(leaseTerm, new LocalDate(2012, 1, 1), new LocalDate(2012, 3, 1), new LocalDate(2012, 1, 1), leaseTerm.getLeaseItem().getInvoicingFrequency());
        CalculationResult result = results.get(0);
        assertThat(results.size(), is(1));
        assertEquals(BigDecimal.valueOf(1593.41).setScale(2, RoundingMode.HALF_UP), result.getCalculatedValue());
    }

    @Test
    public void testCalculateWithFrequencyDifferentEndDate() {
        leaseTerm.setStartDate(new LocalDate(2012, 2, 1));
        leaseTerm.setEndDate(new LocalDate(2012, 2, 29));
        leaseTerm.setValue(BigDecimal.valueOf(20000));
        List<CalculationResult> results = ic.calculateWithFrequency(leaseTerm, new LocalDate(2012, 1, 1), new LocalDate(2012, 2, 28), new LocalDate(2012, 1, 1), leaseTerm.getLeaseItem().getInvoicingFrequency());
        CalculationResult result = results.get(0);
        assertThat(results.size(), is(1));
        assertEquals(BigDecimal.valueOf(1593.41).setScale(2, RoundingMode.HALF_UP), result.getCalculatedValue());
    }

    @Test
    public void testCalculateNothing() {
        leaseTerm.setStartDate(new LocalDate(2013, 1, 1));
        leaseTerm.setEndDate(new LocalDate(2013, 3, 1));
        leaseTerm.setValue(BigDecimal.valueOf(20000));
        CalculationResult result = ic.calculateLeaseTerm(leaseTerm, new LocalDate(2012, 1, 1), new LocalDate(2012, 1, 1));
        assertEquals(BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_UP), result.getCalculatedValue());
    }

    @Test
    public void testDateAfterTerminationDate() {

        lease.terminate(new LocalDate(2013, 12, 31), true);
        LeaseTermForTesting t2 = new LeaseTermForTesting(leaseItem, new LocalDate(2014, 1, 1), null, new BigDecimal("20000.00"));
        CalculationResult r2 = ic.calculateLeaseTerm(t2, new LocalDate(2014, 1, 1), new LocalDate(2014, 1, 1));
        assertThat(r2.getCalculatedValue(), is(new BigDecimal("0.00")));

    }

    @Test
    public void testWithNonMatchingStartDate() {
        leaseTerm.setStartDate(new LocalDate(2013, 1, 1));
        leaseTerm.setEndDate(new LocalDate(2013, 3, 1));
        leaseTerm.setValue(BigDecimal.valueOf(20000));
        CalculationResult result = ic.calculateLeaseTerm(leaseTerm, new LocalDate(2012, 1, 2), new LocalDate(2012, 1, 1));
        assertThat(result.getCalculatedValue(), is(BigDecimal.ZERO.setScale(2)));
    }

    @Test
    public void testwithTerminationDate() {
        leaseTerm.setStartDate(new LocalDate(2012, 1, 1));
        leaseTerm.setEndDate(new LocalDate(2012, 3, 31));
        leaseTerm.setValue(BigDecimal.valueOf(20000));
        lease.setTenancyEndDate(new LocalDate(2012, 1, 31));
        CalculationResult result = ic.calculateLeaseTerm(leaseTerm, new LocalDate(2012, 1, 1), new LocalDate(2012, 1, 1));
        assertEquals(BigDecimal.valueOf(1703.30).setScale(2, RoundingMode.HALF_UP), result.getCalculatedValue());
        CalculationResult result2 = ic.calculateLeaseTerm(leaseTerm, new LocalDate(2014, 1, 1), new LocalDate(2014, 1, 1));
        assertThat(result2.getCalculatedValue(), is(new BigDecimal("0.00")));
    }

    @Test
    public void testWithYearlyInvoicingFrequency() {

        leaseTerm.setStartDate(new LocalDate(2012, 2, 1));
        leaseTerm.setEndDate(new LocalDate(2013, 1, 1));
        leaseTerm.setValue(BigDecimal.valueOf(20000));

        List<CalculationResult> results = ic.calculateAllPeriodsWithGivenInvoicingFrequency(leaseTerm, new LocalDate(2012, 1, 1), new LocalDate(2012, 1, 1), InvoicingFrequency.YEARLY_IN_ARREARS);
        assertThat(results.size(), is(4));
        assertThat(results.get(0).getCalculatedValue(), is(BigDecimal.valueOf(3296.70).setScale(2)));
        assertThat(results.get(1).getCalculatedValue(), is(BigDecimal.valueOf(5000).setScale(2)));
        assertThat(results.get(2).getCalculatedValue(), is(BigDecimal.valueOf(5000).setScale(2)));
        assertThat(results.get(3).getCalculatedValue(), is(BigDecimal.valueOf(5000).setScale(2)));
        assertThat(ic.calculateSumForAllPeriods(leaseTerm, new LocalDate(2012, 1, 1), new LocalDate(2012, 1, 1), InvoicingFrequency.YEARLY_IN_ARREARS), is(BigDecimal.valueOf(18296.70).setScale(2)));
    }

    @Test
    public void testFullCalculationResults() {
        leaseTerm.setStartDate(new LocalDate(2012, 2, 1));
        leaseTerm.setEndDate(new LocalDate(2013, 1, 31));
        leaseTerm.setValue(BigDecimal.valueOf(20000));
        List<CalculationResult> results = ic.calculateWithFrequency(leaseTerm, leaseTerm.getStartDate(), leaseTerm.getEndDate(), new LocalDate(2013, 4, 1), leaseTerm.getLeaseItem().getInvoicingFrequency());
        assertThat(results.size(), is(5));
        assertThat(results.get(0).getCalculatedValue(), is(BigDecimal.valueOf(3296.70).setScale(2)));
        assertThat(results.get(1).getCalculatedValue(), is(BigDecimal.valueOf(5000).setScale(2)));
        assertThat(results.get(2).getCalculatedValue(), is(BigDecimal.valueOf(5000).setScale(2)));
        assertThat(results.get(3).getCalculatedValue(), is(BigDecimal.valueOf(5000).setScale(2)));
        assertThat(results.get(4).getCalculatedValue(), is(BigDecimal.valueOf(1722.22).setScale(2)));
        // TODO: Since 2012 is a leap year, the sum of the invoices is greater
        // than the value of the term.....
    }

    @Test
    public void testMockResults() {

        leaseItem.setCharge(charge);

        leaseTerm.setStartDate(new LocalDate(2012, 1, 1));
        leaseTerm.setEndDate(null);
        leaseTerm.setValue(BigDecimal.valueOf(20000));

        List<CalculationResult> results = ic.calculateWithFrequency(leaseTerm, leaseTerm.getStartDate(), new LocalDate(2014, 1, 1), new LocalDate(2013, 1, 1), leaseTerm.getLeaseItem().getInvoicingFrequency());
        List<CalculationResult> mockResults = new ArrayList<InvoiceCalculationService.CalculationResult>();
        
        for (CalculationResult result : results) {
            List<CalculationResult> mock = ic.mockResults(leaseTerm, result, leaseTerm.getLeaseItem().getInvoicingFrequency(), new LocalDate(2013, 1, 1));
            mockResults.addAll(mock);
        }
        assertThat(results.size(), is(mockResults.size()));
        assertThat(CalculationResultsUtil.sum(results), is(new BigDecimal("40000.00")));
        assertThat(CalculationResultsUtil.sum(mockResults), is(new BigDecimal("20000.00")));
    }

    @Test
    public void testMockResults2() {

        leaseItem.setCharge(charge);

        leaseTerm.setStartDate(new LocalDate(2012, 9, 1));
        leaseTerm.setEndDate(new LocalDate(2013, 8, 31));
        leaseTerm.setValue(BigDecimal.valueOf(20000));
        leaseTerm.setAdjustedValue(BigDecimal.valueOf(22000));

        List<CalculationResult> results = ic.calculateWithFrequency(leaseTerm, leaseTerm.getStartDate(), new LocalDate(2014, 1, 1), new LocalDate(2013, 1, 1), leaseTerm.getLeaseItem().getInvoicingFrequency());
        List<CalculationResult> mockResults = new ArrayList<InvoiceCalculationService.CalculationResult>();
        
        for (CalculationResult result : results) {
            List<CalculationResult> mock = ic.mockResults(leaseTerm, result, leaseTerm.getLeaseItem().getInvoicingFrequency(), new LocalDate(2013, 1, 1));
            mockResults.addAll(mock);
        }
        assertThat(results.size(), is(mockResults.size()));
        assertThat(CalculationResultsUtil.sum(results), is(new BigDecimal("20000.00")));
        assertThat(CalculationResultsUtil.sum(mockResults), is(new BigDecimal("6630.43")));
    }

    @Test
    public void testMockResults3() {

        leaseItem.setCharge(charge);

        leaseTerm.setStartDate(new LocalDate(2012, 9, 1));
        leaseTerm.setEndDate(new LocalDate(2013, 8, 31));
        leaseTerm.setValue(BigDecimal.valueOf(20000));
        leaseTerm.setAdjustedValue(BigDecimal.valueOf(22000));

        List<CalculationResult> results = ic.calculateWithFrequency(leaseTerm, leaseTerm.getStartDate(), new LocalDate(2014, 1, 1), new LocalDate(2013, 10, 1), leaseTerm.getLeaseItem().getInvoicingFrequency());
        List<CalculationResult> mockResults = new ArrayList<InvoiceCalculationService.CalculationResult>();
        
        for (CalculationResult result : results) {
            List<CalculationResult> mock = ic.mockResults(leaseTerm, result, leaseTerm.getLeaseItem().getInvoicingFrequency(), new LocalDate(2013, 1, 1));
            mockResults.addAll(mock);
        }
        assertThat(results.size(), is(mockResults.size()));
        assertThat(CalculationResultsUtil.sum(results), is(new BigDecimal("22000.00")));
        assertThat(CalculationResultsUtil.sum(mockResults), is(new BigDecimal("6630.43")));
    }

}
