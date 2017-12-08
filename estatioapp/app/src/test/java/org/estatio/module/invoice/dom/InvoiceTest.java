package org.estatio.module.invoice.dom;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.Test;

import org.incode.module.base.integtests.VT;

import org.estatio.module.charge.dom.Charge;

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
        BigDecimal netAmount = invoiceForTesting.getTotalNetAmount();

        // then
        assertThat(netAmount).isEqualTo(new BigDecimal("666.66"));


    }

    @Test
    public void first_item_with_charge_works() throws Exception {

        // given
        InvoiceForTesting invoice = new InvoiceForTesting();
        Charge charge1 = new Charge();
        Charge charge2 = new Charge();
        InvoiceItem itemWithCharge1 = new InvoiceItemForTesting(invoice);
        itemWithCharge1.setCharge(charge1);
        itemWithCharge1.setSequence(VT.bi(1)); // done for sort order in SortedSet items
        InvoiceItem otherItemWithCharge1 = new InvoiceItemForTesting(invoice);
        otherItemWithCharge1.setCharge(charge1);
        otherItemWithCharge1.setSequence(VT.bi(2)); // done for sort order SortedSet items
        InvoiceItem itemWithCharge2 = new InvoiceItemForTesting(invoice);
        itemWithCharge2.setCharge(charge2);
        invoice.getItems().addAll(Arrays.asList(itemWithCharge1, otherItemWithCharge1, itemWithCharge2));
        assertThat(invoice.getItems().size()).isEqualTo(3);

        // when, then
        assertThat(invoice.findFirstItemWithCharge(charge1)).isEqualTo(itemWithCharge1);
        assertThat(invoice.findFirstItemWithCharge(charge2)).isEqualTo(itemWithCharge2);

    }
}