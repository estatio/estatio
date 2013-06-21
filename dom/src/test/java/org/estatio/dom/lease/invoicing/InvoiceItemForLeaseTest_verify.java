package org.estatio.dom.lease.invoicing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.estatio.dom.charge.Charge;
import org.estatio.dom.tax.Tax;
import org.estatio.dom.tax.TaxRate;
import org.estatio.dom.tax.TaxRates;

public class InvoiceItemForLeaseTest_verify {

    private Charge charge;
    private Tax tax;
    private TaxRate rate;
    private InvoiceItemForLease item;

    @Mock
    TaxRates mockTaxRates;

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Before
    public void setup() {
        charge = new Charge();
        tax = new Tax();
        tax.injectTaxRates(mockTaxRates);
        
        rate = new TaxRate();
        rate.setPercentage(BigDecimal.valueOf(21));
        
        item = new InvoiceItemForLease();
        
        item.setCharge(charge);
        item.setTax(tax);
        item.setDueDate(new LocalDate(2012, 1, 1));
        item.setNetAmount(BigDecimal.ZERO);
        item.setVatAmount(BigDecimal.ZERO);
        item.setGrossAmount(BigDecimal.ZERO);
    }

    @Test
    public void happyCase() {
        context.checking(new Expectations() {
            {
                allowing(mockTaxRates).findTaxRateForDate(with(tax), with(new LocalDate(2012, 1, 1)));
                will(returnValue(rate));
            }
        });
        item.setNetAmount(BigDecimal.valueOf(12.34));
        item.verify();
        assertThat(item.getVatAmount(), is(BigDecimal.valueOf(2.59)));
        assertThat(item.getGrossAmount(), is(BigDecimal.valueOf(14.93)));
    }

}
