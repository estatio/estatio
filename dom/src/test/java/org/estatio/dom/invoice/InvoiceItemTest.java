package org.estatio.dom.invoice;

import java.math.BigDecimal;

import org.estatio.dom.charge.Charge;
import org.estatio.dom.tax.Tax;
import org.estatio.dom.tax.TaxRate;
import org.estatio.dom.tax.Taxes;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.danhaywood.testsupport.jmock.JUnitRuleMockery2;
import com.danhaywood.testsupport.jmock.JUnitRuleMockery2.Mode;

public class InvoiceItemTest {

    private Charge charge;
    private Tax tax;
    private TaxRate rate;
    private InvoiceItem item;

    @Mock
    Taxes mockTaxes;

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Before
    public void setup() {
        charge = new Charge();
        tax = new Tax();
        tax.setTaxRepo(mockTaxes);
        rate = new TaxRate();
        rate.setPercentage(BigDecimal.valueOf(21));
        item = new InvoiceItem();
        item.setDueDate(new LocalDate(2012, 1, 1));
        item.setCharge(charge);
        item.setTax(tax);
        item.setNetAmount(BigDecimal.ZERO);
        item.setVatAmount(BigDecimal.ZERO);
        item.setGrossAmount(BigDecimal.ZERO);
    }

    @Test
    public void testVerify() {
        context.checking(new Expectations() {
            {
                allowing(mockTaxes).findTaxRateForDate(with(tax), with(new LocalDate(2012, 1, 1)));
                will(returnValue(rate));
            }
        });
        item.setNetAmount(BigDecimal.valueOf(12.34));
        item.verify();
        Assert.assertEquals(item.getVatAmount(), BigDecimal.valueOf(2.59));
        Assert.assertEquals(item.getGrossAmount(), BigDecimal.valueOf(14.93));
    }

}
