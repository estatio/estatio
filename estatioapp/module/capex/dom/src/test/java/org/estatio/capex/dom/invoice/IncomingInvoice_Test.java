package org.estatio.capex.dom.invoice;

import java.math.BigDecimal;
import java.util.Arrays;

import org.assertj.core.api.Assertions;
import org.junit.Test;


public class IncomingInvoice_Test {

    IncomingInvoice incomingInvoice;

    @Test
    public void recalculateAmounts() throws Exception {

        // given
        incomingInvoice = new IncomingInvoice();
        IncomingInvoiceItem item1 = new IncomingInvoiceItem();
        IncomingInvoiceItem item2 = new IncomingInvoiceItem();
        IncomingInvoiceItem item3 = new IncomingInvoiceItem();
        IncomingInvoiceItem item4 = new IncomingInvoiceItem();
        item1.setNetAmount(new BigDecimal("100.00"));
        item1.setGrossAmount(new BigDecimal("120.00"));
        item2.setNetAmount(new BigDecimal("50.00"));
        item2.setGrossAmount(new BigDecimal("55.00"));
        item3.setNetAmount(null); // explicit for test
        item3.setGrossAmount(new BigDecimal("-1.00"));
        item4.setNetAmount(new BigDecimal("-1.00"));
        item4.setGrossAmount(null); // explicit for test
        incomingInvoice.getItems().addAll(Arrays.asList(item1, item2, item3, item4));

        // when
        incomingInvoice.recalculateAmounts();

        // then
        Assertions.assertThat(incomingInvoice.getNetAmount()).isEqualTo(new BigDecimal("149.00"));
        Assertions.assertThat(incomingInvoice.getGrossAmount()).isEqualTo(new BigDecimal("174.00"));

    }

}