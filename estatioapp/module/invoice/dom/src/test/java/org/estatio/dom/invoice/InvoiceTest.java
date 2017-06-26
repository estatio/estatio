package org.estatio.dom.invoice;

import java.math.BigDecimal;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class InvoiceTest {

    @Test
    public void netAmount() throws Exception {

        // given
        InvoiceForTesting invoiceForTesting = new InvoiceForTesting();

        InvoiceItemForTesting item1 = new InvoiceItemForTesting(invoiceForTesting);
        item1.setNetAmount(new BigDecimal("123.45"));

        InvoiceItemForTesting item2 = new InvoiceItemForTesting(invoiceForTesting);
        item2.setNetAmount(new BigDecimal("543.21"));

        invoiceForTesting.getItems().add(item1);
        invoiceForTesting.getItems().add(item2);

        assertThat(invoiceForTesting.getItems()).hasSize(2);

        // when
        BigDecimal netAmount = invoiceForTesting.getNetAmount();

        // then
        assertThat(netAmount).isEqualTo(new BigDecimal("666.66"));


    }
}