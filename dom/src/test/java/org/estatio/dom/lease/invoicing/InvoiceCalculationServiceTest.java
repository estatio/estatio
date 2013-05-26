package org.estatio.dom.lease.invoicing;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.danhaywood.testsupport.jmock.JUnitRuleMockery2;
import com.danhaywood.testsupport.jmock.JUnitRuleMockery2.Mode;

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
import org.estatio.dom.lease.invoicing.InvoiceCalculationService;
import org.estatio.dom.lease.invoicing.InvoiceItemForLease;
import org.estatio.dom.lease.invoicing.InvoiceCalculationService.CalculationResult;
import org.estatio.dom.party.Party;
import org.estatio.dom.tax.Tax;
import org.estatio.dom.tax.TaxRate;
import org.estatio.dom.tax.Taxes;
import org.estatio.services.appsettings.EstatioSettingsService;

import org.hamcrest.core.Is;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class InvoiceCalculationServiceTest {
    private static LocalDate START_DATE = new LocalDate(2011, 11, 1);

    private Lease l;
    private LeaseItem li;
    private LeaseTerm lt;

    private Tax tax;
    private TaxRate taxRate;
    private Charge charge;

    private AgreementRoleType artLandlord;
    private AgreementRoleType artTenant;
    
    @Mock
    private Taxes mockTaxes;

    @Mock
    private InvoicesForLease mockInvoices;

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

    @Before
    public void setup() {
        artLandlord = new AgreementRoleType();
        artLandlord.setTitle("Landlord");
        
        artTenant = new AgreementRoleType();
        artTenant.setTitle("Tenant");
        
        l = new Lease();
        l.injectAgreementRoles(mockAgreementRoles);
        l.setStartDate(START_DATE);
        l.setEndDate(START_DATE.plusYears(1).minusDays(1));
        tax = new Tax();
        tax.setReference("VAT");
        taxRate = new TaxRate();
        taxRate.setPercentage(BigDecimal.valueOf(21));
        charge = new Charge();
        charge.setReference("RENT");
        charge.setTax(tax);
        
        context.checking(new Expectations() {
            {
                allowing(mockAgreementRoleTypes).find("Landlord");
                will(returnValue(artLandlord));
                allowing(mockAgreementRoleTypes).find("Tenant");
                will(returnValue(artTenant));
            }
        });
    }

    @Test
    public void testCalculateFullQuarter() {
        li = new LeaseItem();
        li.setStartDate(START_DATE);
        li.setInvoicingFrequency(InvoicingFrequency.QUARTERLY_IN_ADVANCE);
        li.setNextDueDate(new LocalDate(2012, 1, 1));
        li.modifyLease(l);
        lt = new LeaseTerm();
        lt.setStartDate(START_DATE);
        lt.setValue(BigDecimal.valueOf(20000));
        lt.modifyLeaseItem(li);
        InvoiceCalculationService ic = new InvoiceCalculationService();
        CalculationResult result = ic.calculate(lt, new LocalDate(2012, 1, 1), new LocalDate(2012, 1, 1));
        Assert.assertEquals(BigDecimal.valueOf(5000).setScale(2, RoundingMode.HALF_UP), result.getCalculatedValue());
    }

    @Test
    public void testValuuForPeriod() {
        li = new LeaseItem();
        li.setStartDate(START_DATE);
        li.setNextDueDate(new LocalDate(2012, 1, 1));
        li.modifyLease(l);
        lt = new LeaseTerm();
        lt.setStartDate(START_DATE);
        lt.setValue(BigDecimal.valueOf(20000));
        lt.modifyLeaseItem(li);
        InvoiceCalculationService ic = new InvoiceCalculationService();
        BigDecimal result = ic.calculatedValue(lt, new LocalDate(2012, 1, 1), new LocalDate(2012, 1, 1), InvoicingFrequency.YEARLY_IN_ADVANCE);
        Assert.assertThat(result, Is.is(BigDecimal.valueOf(20000).setScale(2, RoundingMode.HALF_UP)));
    }

    @Test
    public void testCalculateExactPeriod() {
        li = new LeaseItem();
        li.setStartDate(START_DATE);
        li.setInvoicingFrequency(InvoicingFrequency.QUARTERLY_IN_ADVANCE);
        li.setNextDueDate(new LocalDate(2012, 1, 1));
        li.modifyLease(l);
        lt = new LeaseTerm();
        lt.setStartDate(new LocalDate(2012, 1, 1));
        lt.setEndDate(new LocalDate(2012, 3, 31));
        lt.setValue(BigDecimal.valueOf(20000));
        lt.modifyLeaseItem(li);
        InvoiceCalculationService ic = new InvoiceCalculationService();
        CalculationResult result = ic.calculate(lt, new LocalDate(2012, 1, 1), new LocalDate(2012, 1, 1));
        Assert.assertEquals(BigDecimal.valueOf(5000).setScale(2, RoundingMode.HALF_UP), result.getCalculatedValue());
    }

    @Test
    public void testCalculateSingleMonth() {
        li = new LeaseItem();
        li.setStartDate(START_DATE);
        li.setInvoicingFrequency(InvoicingFrequency.QUARTERLY_IN_ADVANCE);
        li.setNextDueDate(new LocalDate(2012, 1, 1));
        li.modifyLease(l);
        lt = new LeaseTerm();
        lt.setStartDate(new LocalDate(2012, 2, 1));
        lt.setEndDate(new LocalDate(2012, 2, 29));
        lt.setValue(BigDecimal.valueOf(20000));
        lt.modifyLeaseItem(li);
        InvoiceCalculationService ic = new InvoiceCalculationService();
        CalculationResult result = ic.calculate(lt, new LocalDate(2012, 1, 1), new LocalDate(2012, 1, 1));
        Assert.assertEquals(BigDecimal.valueOf(1593.41).setScale(2, RoundingMode.HALF_UP), result.getCalculatedValue());
    }

    @Test
    public void testCalculateNothing() {
        li = new LeaseItem();
        li.setStartDate(START_DATE);
        li.setInvoicingFrequency(InvoicingFrequency.QUARTERLY_IN_ADVANCE);
        li.setNextDueDate(new LocalDate(2012, 1, 1));
        li.modifyLease(l);
        lt = new LeaseTerm();
        lt.setStartDate(new LocalDate(2013, 1, 1));
        lt.setEndDate(new LocalDate(2013, 3, 1));
        lt.setValue(BigDecimal.valueOf(20000));
        lt.modifyLeaseItem(li);
        InvoiceCalculationService ic = new InvoiceCalculationService();
        CalculationResult result = ic.calculate(lt, new LocalDate(2012, 1, 1), new LocalDate(2012, 1, 1));
        Assert.assertEquals(BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_UP), result.getCalculatedValue());
    }

    @Test
    public void testWithWrongDate() {
        li = new LeaseItem();
        li.setStartDate(START_DATE);
        li.setInvoicingFrequency(InvoicingFrequency.QUARTERLY_IN_ADVANCE);
        li.setNextDueDate(new LocalDate(2012, 1, 1));
        li.modifyLease(l);
        lt = new LeaseTerm();
        lt.setStartDate(new LocalDate(2013, 1, 1));
        lt.setEndDate(new LocalDate(2013, 3, 1));
        lt.setValue(BigDecimal.valueOf(20000));
        lt.modifyLeaseItem(li);
        InvoiceCalculationService ic = new InvoiceCalculationService();
        CalculationResult result = ic.calculate(lt, new LocalDate(2012, 1, 2), new LocalDate(2012, 1, 1));
        Assert.assertThat(result.getCalculatedValue(), Is.is(BigDecimal.ZERO.setScale(2)));
    }

    @Test
    public void testCreateInvoiceItem() {
        li = new LeaseItem();
        li.setStartDate(START_DATE);
        li.setInvoicingFrequency(InvoicingFrequency.QUARTERLY_IN_ADVANCE);
        li.setNextDueDate(new LocalDate(2012, 1, 1));
        li.setCharge(charge);
        li.modifyLease(l);
        
        lt = new LeaseTerm();
        lt.injectInvoices(mockInvoices);
        lt.setStartDate(new LocalDate(2012, 1, 1));
        lt.setEndDate(new LocalDate(2013, 1, 1));
        lt.setValue(BigDecimal.valueOf(20000));
        lt.modifyLeaseItem(li);
        li.getTerms().add(lt);
        
        tax.injectTaxes(mockTaxes);
        final InvoiceItemForLease ii = new InvoiceItemForLease();
        ii.injectInvoices(mockInvoices);
        ii.injectAgreementRoleTypes(mockAgreementRoleTypes);
        ii.injectAgreementTypes(mockAgreementTypes);

        context.checking(new Expectations() {
            {
                oneOf(mockInvoices).newInvoiceItem();
                will(returnValue(ii));
                oneOf(mockTaxes).findTaxRateForDate(with(tax), with(new LocalDate(2012, 1, 1)));
                will(returnValue(taxRate));
                exactly(2).of(mockAgreementRoles).findAgreementRoleWithType(with(any(Agreement.class)), with(any(AgreementRoleType.class)), with(any(LocalDate.class)));
                will(returnValue(new AgreementRole()));
                oneOf(mockInvoices).findMatchingInvoice(with(aNull(Party.class)), with(aNull(Party.class)), with(aNull(PaymentMethod.class)), with(any(Lease.class)), with(any(InvoiceStatus.class)), with(any(LocalDate.class)));
                will(returnValue(new Invoice()));
                oneOf(mockSettings).fetchEpochDate();
                will(returnValue(null));
            }
        });

        InvoiceCalculationService ic = new InvoiceCalculationService();
        ic.setEstatioSettings(mockSettings);
        ic.calculateAndInvoiceItems(lt, new LocalDate(2012, 1, 1), new LocalDate(2012, 1, 1));

        InvoiceItemForLease invoiceItem = lt.getInvoiceItems().iterator().next();

        Assert.assertEquals(BigDecimal.valueOf(5000).setScale(2, RoundingMode.HALF_UP), invoiceItem.getNetAmount());
        Assert.assertEquals(new LocalDate(2012, 1, 1), invoiceItem.getStartDate());
    }
}
