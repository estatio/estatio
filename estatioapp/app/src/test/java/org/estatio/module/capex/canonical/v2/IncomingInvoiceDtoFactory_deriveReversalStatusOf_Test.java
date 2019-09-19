package org.estatio.module.capex.canonical.v2;

import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;

import org.estatio.canonical.incominginvoice.v2.IncomingInvoiceItemReversalStatus;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.invoice.dom.InvoiceItem;

import static org.assertj.core.api.Assertions.assertThat;

public class IncomingInvoiceDtoFactory_deriveReversalStatusOf_Test {

    private IncomingInvoice incomingInvoice;
    IncomingInvoiceItem notReversedItem1;
    IncomingInvoiceItem notReversedItem2;
    IncomingInvoiceItem reversedItem3;
    IncomingInvoiceItem reversalItem4;
    IncomingInvoiceItem reversedItem5;
    IncomingInvoiceItem reversalItem6;
    IncomingInvoiceItem notReversedItem7;
    IncomingInvoiceItem notReversedItem8;

    @Before
    public void setUp() throws Exception {
        incomingInvoice = new IncomingInvoice();

        final TreeSet<InvoiceItem> items = new TreeSet<>();
        incomingInvoice.setItems(items);

        notReversedItem1 = addItem(incomingInvoice, "item1");
        notReversedItem2 = addItem(incomingInvoice, "item2");

        reversedItem3 = addItem(incomingInvoice, "item3");
        reversalItem4 = addItem(incomingInvoice, "item4");
        reversalItem4.setReversalOf(reversedItem3);

        reversedItem5 = addItem(incomingInvoice, "item5");
        reversalItem6 = addItem(incomingInvoice, "item6");
        reversalItem6.setReversalOf(reversedItem5);

        notReversedItem7 = addItem(incomingInvoice, "item7");
        notReversedItem8 = addItem(incomingInvoice, "item8");
    }

    private static IncomingInvoiceItem addItem(final IncomingInvoice incomingInvoice, final String str) {
        final IncomingInvoiceItem item = new IncomingInvoiceItem() {
            @Override public String toString() {
                return str;
            }

            @Override public int compareTo(final IncomingInvoiceItem other) {
                return toString().compareTo(other.toString());
            }
        };
        item.setInvoice(incomingInvoice);
        incomingInvoice.getItems().add(item);
        return item;
    }

    @Test
    public void not_reversed() throws Exception {

        assertReversalStatus(this.notReversedItem1, IncomingInvoiceItemReversalStatus.NOT_REVERSED);
        assertReversalStatus(this.notReversedItem2, IncomingInvoiceItemReversalStatus.NOT_REVERSED);
        assertReversalStatus(this.reversedItem3, IncomingInvoiceItemReversalStatus.REVERSED);
        assertReversalStatus(this.reversalItem4, IncomingInvoiceItemReversalStatus.REVERSAL);
        assertReversalStatus(this.reversedItem5, IncomingInvoiceItemReversalStatus.REVERSED);
        assertReversalStatus(this.reversalItem6, IncomingInvoiceItemReversalStatus.REVERSAL);
        assertReversalStatus(this.notReversedItem7, IncomingInvoiceItemReversalStatus.NOT_REVERSED);
        assertReversalStatus(this.notReversedItem8, IncomingInvoiceItemReversalStatus.NOT_REVERSED);
    }

    private static void assertReversalStatus(
            final IncomingInvoiceItem item,
            final IncomingInvoiceItemReversalStatus expected) {
        assertThat(IncomingInvoiceDtoFactory.deriveReversalStatusOf(item)).isSameAs(expected);
    }
}