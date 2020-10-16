package org.estatio.module.budgetassignment.dom;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;

public class BudgetService_Test {

    @Test
    public void calculateAuditedValues_works() {

        // given
        BudgetService service = new BudgetService();
        List<IncomingInvoiceItem> invoiceItems = new ArrayList<>();

        // when //then
        Assertions.assertThat(service.sumInvoiceNetAmount(invoiceItems)).isEqualTo(BigDecimal.ZERO);

        // and when
        final BigDecimal val1 = new BigDecimal("1234.56");
        IncomingInvoiceItem item1 = new IncomingInvoiceItem(){
            @Override public BigDecimal getNetAmount() {
                return val1;
            }
        };
        invoiceItems.add(item1);

        // then
        Assertions.assertThat(service.sumInvoiceNetAmount(invoiceItems)).isEqualTo(item1.getNetAmount());
        Assertions.assertThat(service.sumInvoiceNetAmount(invoiceItems)).isEqualTo(val1);

        // and when
        final BigDecimal val2 = new BigDecimal("1000.00");
        IncomingInvoiceItem item2 = new IncomingInvoiceItem(){
            @Override public BigDecimal getNetAmount() {
                return val2;
            }
        };
        invoiceItems.add(item2);

        // then
        Assertions.assertThat(service.sumInvoiceNetAmount(invoiceItems)).isEqualTo(new BigDecimal("2234.56"));

        // and when
        final BigDecimal val3 = new BigDecimal("-1234.00");
        IncomingInvoiceItem item3 = new IncomingInvoiceItem(){
            @Override public BigDecimal getNetAmount() {
                return val3;
            }
        };
        invoiceItems.add(item3);

        // then
        Assertions.assertThat(service.sumInvoiceNetAmount(invoiceItems)).isEqualTo(new BigDecimal("1000.56"));
    }
}