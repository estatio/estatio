package org.estatio.fixture.tax;

import java.math.BigDecimal;

import org.apache.isis.applib.fixtures.AbstractFixture;
import org.estatio.dom.tax.Tax;
import org.estatio.dom.tax.Taxes;
import org.joda.time.LocalDate;


public class TaxFixture extends AbstractFixture {

    @Override
    public void install() {
       Tax tax = taxes.newTax("IT-VATSTD");
       tax.newRate(new LocalDate(1980,1,1), BigDecimal.valueOf(19)).newRate(new LocalDate(2011,9,17), BigDecimal.valueOf(21));
    }

    private Taxes taxes;

    public void injectTaxes(Taxes taxes) {
        this.taxes = taxes;
    }

}
