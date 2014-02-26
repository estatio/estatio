package org.estatio.dom.lease.invoicing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.estatio.dom.asset.Property;

public class InvoiceCalculationParametersTest {

    private Property property;

    @Before
    public void setup() {
        property = new Property();
        property.setReference("HELLO");
    }

    @Test
    public void test() {
        InvoiceCalculationParameters parameters =
                new InvoiceCalculationParameters(
                        property,
                        InvoiceCalculationSelection.RENT.selectedTypes(),
                        InvoiceRunType.NORMAL_RUN,
                        new LocalDate(2012, 1, 1),
                        new LocalDate(2012, 1, 1),
                        new LocalDate(2012, 1, 1));

        assertThat(parameters.toString(), is("HELLO - [RENT] - 2012-01-01 - 2012-01-01/2012-01-01"));

    }
}
