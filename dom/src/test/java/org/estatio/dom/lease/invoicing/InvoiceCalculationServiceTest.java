package org.estatio.dom.lease.invoicing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.hamcrest.core.Is;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Assert;
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
import org.estatio.dom.lease.LeaseTermForTesting;
import org.estatio.dom.lease.Leases.InvoiceRunType;
import org.estatio.dom.lease.invoicing.InvoiceCalculationService.CalculationResult;
import org.estatio.dom.party.Party;
import org.estatio.dom.tax.Tax;
import org.estatio.dom.tax.TaxRate;
import org.estatio.dom.tax.TaxRates;
import org.estatio.services.appsettings.EstatioSettingsService;

public class InvoiceCalculationServiceTest {
    private static LocalDate START_DATE = new LocalDate(2011, 11, 1);

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
        lease.setStartDate(START_DATE);
        lease.setEndDate(START_DATE.plusYears(1).minusDays(1));
        
        leaseItem = new LeaseItem();
        leaseItem.setStartDate(START_DATE);
        leaseItem.modifyLease(lease);
        leaseItem.setInvoicingFrequency(InvoicingFrequency.QUARTERLY_IN_ADVANCE);
        
        leaseTerm = new LeaseTermForTesting();
        leaseTerm.injectInvoiceItemsForLease(mockInvoiceItemsForLease);
        leaseTerm.modifyLeaseItem(leaseItem);
        
        tax = new Tax();
        tax.injectTaxRates(mockTaxRates);
        tax.setReference("VAT");
        
        taxRate = new TaxRate();
        taxRate.setPercentage(BigDecimal.valueOf(21));
        
        charge = new Charge();
        charge.setReference("RENT");
        charge.setTax(tax);

        invoiceItemForLease = new InvoiceItemForLease();
        invoiceItemForLease.injectInvoices(mockInvoices);
        invoiceItemForLease.injectInvoiceItemsForLease(mockInvoiceItemsForLease);
        invoiceItemForLease.injectAgreementRoleTypes(mockAgreementRoleTypes);
        invoiceItemForLease.injectAgreementTypes(mockAgreementTypes);
        
        ic = new InvoiceCalculationService();
    }

    @Test
    public void testCalculateFullQuarter() {
        
        leaseTerm.setStartDate(START_DATE);
        leaseTerm.setValue(BigDecimal.valueOf(20000));
        
        CalculationResult result = ic.calculate(leaseTerm, new LocalDate(2012, 1, 1), new LocalDate(2012, 1, 1));
        Assert.assertEquals(BigDecimal.valueOf(5000).setScale(2, RoundingMode.HALF_UP), result.getCalculatedValue());
    }

    @Test
    public void testValueForPeriod() {
        
        leaseTerm.setStartDate(START_DATE);
        leaseTerm.setValue(BigDecimal.valueOf(10000.22));
        
        BigDecimal result = ic.calculatedValue(leaseTerm, new LocalDate(2012, 1, 1), new LocalDate(2012, 1, 1), InvoicingFrequency.YEARLY_IN_ADVANCE);
        Assert.assertThat(result, Is.is(BigDecimal.valueOf(10000.24).setScale(2, RoundingMode.HALF_UP)));
    }

    @Test
    public void testCalculateExactPeriod() {
        
        leaseTerm.setStartDate(new LocalDate(2012, 1, 1));
        leaseTerm.setEndDate(new LocalDate(2012, 3, 31));
        leaseTerm.setValue(BigDecimal.valueOf(20000));
        
        CalculationResult result = ic.calculate(leaseTerm, new LocalDate(2012, 1, 1), new LocalDate(2012, 1, 1));
        Assert.assertEquals(BigDecimal.valueOf(5000).setScale(2, RoundingMode.HALF_UP), result.getCalculatedValue());
    }

    @Test
    public void testCalculateSingleMonth() {
        
        leaseTerm.setStartDate(new LocalDate(2012, 2, 1));
        leaseTerm.setEndDate(new LocalDate(2012, 2, 29));
        leaseTerm.setValue(BigDecimal.valueOf(20000));
        
        CalculationResult result = ic.calculate(leaseTerm, new LocalDate(2012, 1, 1), new LocalDate(2012, 1, 1));
        Assert.assertEquals(BigDecimal.valueOf(1593.41).setScale(2, RoundingMode.HALF_UP), result.getCalculatedValue());
    }

    @Test
    public void testCalculateNothing() {
        
        leaseTerm.setStartDate(new LocalDate(2013, 1, 1));
        leaseTerm.setEndDate(new LocalDate(2013, 3, 1));
        leaseTerm.setValue(BigDecimal.valueOf(20000));
        
        CalculationResult result = ic.calculate(leaseTerm, new LocalDate(2012, 1, 1), new LocalDate(2012, 1, 1));
        Assert.assertEquals(BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_UP), result.getCalculatedValue());
    }

    @Test
    public void testWithNonMatchingStartDate() {
        
        leaseTerm.setStartDate(new LocalDate(2013, 1, 1));
        leaseTerm.setEndDate(new LocalDate(2013, 3, 1));
        leaseTerm.setValue(BigDecimal.valueOf(20000));
        
        CalculationResult result = ic.calculate(leaseTerm, new LocalDate(2012, 1, 2), new LocalDate(2012, 1, 1));
        Assert.assertNull(result);
    }

    @Test
    public void testwithTerminationDate() {
        
        leaseTerm.setStartDate(new LocalDate(2012, 1, 1));
        leaseTerm.setEndDate(new LocalDate(2012, 3, 31));
        leaseTerm.setValue(BigDecimal.valueOf(20000));
        lease.setTerminationDate(new LocalDate(2012, 1, 31));
        
        CalculationResult result = ic.calculate(leaseTerm, new LocalDate(2012, 1, 1), new LocalDate(2012, 1, 1));
        Assert.assertEquals(BigDecimal.valueOf(1703.30).setScale(2, RoundingMode.HALF_UP), result.getCalculatedValue());
    }

    @Test
    public void testCalculationResults() {
        
        leaseTerm.setStartDate(new LocalDate(2012, 2, 1));
        leaseTerm.setEndDate(new LocalDate(2013, 1, 1));
        leaseTerm.setValue(BigDecimal.valueOf(20000));
        
        List<CalculationResult> results = ic.calculationResults(leaseTerm, new LocalDate(2012, 1, 1), new LocalDate(2012, 1, 1), InvoicingFrequency.YEARLY_IN_ARREARS);
        Assert.assertThat(results.get(0).getCalculatedValue(), Is.is(BigDecimal.valueOf(3296.70).setScale(2)));
        Assert.assertThat(results.get(1).getCalculatedValue(), Is.is(BigDecimal.valueOf(5000).setScale(2)));
        Assert.assertThat(results.get(2).getCalculatedValue(), Is.is(BigDecimal.valueOf(5000).setScale(2)));
        Assert.assertThat(results.get(3).getCalculatedValue(), Is.is(BigDecimal.valueOf(5000).setScale(2)));
        Assert.assertThat(ic.calculatedValue(leaseTerm, new LocalDate(2012, 1, 1), new LocalDate(2012, 1, 1), InvoicingFrequency.YEARLY_IN_ARREARS), Is.is(BigDecimal.valueOf(18296.70).setScale(2)));
    }

    @Test
    public void testFullCalculationResults() {
        
        leaseTerm.setStartDate(new LocalDate(2012, 2, 1));
        leaseTerm.setEndDate(new LocalDate(2013, 1, 31));
        leaseTerm.setValue(BigDecimal.valueOf(20000));
        
        List<CalculationResult> results = ic.fullCalculationResults(leaseTerm, new LocalDate(2013, 4, 1));
        Assert.assertThat(results.get(0).getCalculatedValue(), Is.is(BigDecimal.valueOf(3296.70).setScale(2)));
        Assert.assertThat(results.get(1).getCalculatedValue(), Is.is(BigDecimal.valueOf(5000).setScale(2)));
        Assert.assertThat(results.get(2).getCalculatedValue(), Is.is(BigDecimal.valueOf(5000).setScale(2)));
        Assert.assertThat(results.get(3).getCalculatedValue(), Is.is(BigDecimal.valueOf(5000).setScale(2)));
        Assert.assertThat(results.get(4).getCalculatedValue(), Is.is(BigDecimal.valueOf(1722.22).setScale(2)));
        // TODO: Since 2012 is a leap year, the sum of the invoices is greater
        // than the value of the term.....
    }

    @Test
    public void testFullCalulationResults2() {
        LocalDate startDate = new LocalDate(2012, 2, 1);
        LocalDate endDate = new LocalDate(2013, 1, 31);
        BigDecimal value = BigDecimal.valueOf(20000);
        
        leaseTerm.setStartDate(startDate);
        leaseTerm.setEndDate(endDate);
        leaseTerm.setValue(value);
        
        List<CalculationResult> results = ic.fullCalculationResults(leaseTerm, new LocalDate(2013, 4, 1));
        
        Assert.assertThat(results.get(0).getCalculatedValue(), Is.is(BigDecimal.valueOf(3296.70).setScale(2)));
        Assert.assertThat(results.get(1).getCalculatedValue(), Is.is(BigDecimal.valueOf(5000).setScale(2)));
        Assert.assertThat(results.get(2).getCalculatedValue(), Is.is(BigDecimal.valueOf(5000).setScale(2)));
        Assert.assertThat(results.get(3).getCalculatedValue(), Is.is(BigDecimal.valueOf(5000).setScale(2)));
        Assert.assertThat(results.get(4).getCalculatedValue(), Is.is(BigDecimal.valueOf(1722.22).setScale(2)));
        // TODO: Since 2012 is a leap year, the sum of the invoices is greater
        // than the value of the term.....
    }


    @Test
    public void testCreateInvoiceItem() {
        
        leaseItem.setCharge(charge);

        leaseTerm.setStartDate(new LocalDate(2012, 1, 1));
        leaseTerm.setEndDate(new LocalDate(2013, 1, 1));
        leaseTerm.setValue(BigDecimal.valueOf(20000));

        context.checking(new Expectations() {
            {
                oneOf(mockInvoiceItemsForLease).newInvoiceItem();
                will(returnValue(invoiceItemForLease));
                oneOf(mockTaxRates).findTaxRateForDate(with(tax), with(new LocalDate(2012, 1, 1)));
                will(returnValue(taxRate));
                exactly(2).of(mockAgreementRoles).findByAgreementAndTypeAndContainsDate(with(any(Agreement.class)), with(any(AgreementRoleType.class)), with(any(LocalDate.class)));
                will(returnValue(new AgreementRole()));
                oneOf(mockInvoices).findMatchingInvoice(with(aNull(Party.class)), with(aNull(Party.class)), with(aNull(PaymentMethod.class)), with(any(Lease.class)), with(any(InvoiceStatus.class)), with(any(LocalDate.class)));
                will(returnValue(new Invoice()));
                oneOf(mockSettings).fetchEpochDate();
                will(returnValue(null));
            }
        });

        ic.setEstatioSettings(mockSettings);
        ic.calculateAndInvoice(leaseTerm, new LocalDate(2012, 1, 1), new LocalDate(2012, 1, 1), leaseTerm.getLeaseItem().getInvoicingFrequency(), InvoiceRunType.NORMAL_RUN);

        InvoiceItemForLease invoiceItem = leaseTerm.getInvoiceItems().iterator().next();

        Assert.assertEquals(BigDecimal.valueOf(5000).setScale(2, RoundingMode.HALF_UP), invoiceItem.getNetAmount());
        Assert.assertEquals(new LocalDate(2012, 1, 1), invoiceItem.getStartDate());
    }
}
