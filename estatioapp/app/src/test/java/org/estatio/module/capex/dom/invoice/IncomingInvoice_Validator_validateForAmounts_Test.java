package org.estatio.module.capex.dom.invoice;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class IncomingInvoice_Validator_validateForAmounts_Test {

    IncomingInvoice invoice;
    IncomingInvoiceItem item;

    IncomingInvoice.Validator validator;

    @Before
    public void setUp() throws Exception {
        validator = new IncomingInvoice.Validator();

        invoice = new IncomingInvoice();
        item = new IncomingInvoiceItem(){
            @Override void invalidateApproval() {
            }
        };
    }

    @Test
    public void when_gross_amount_not_set_validation_is_skipped() throws Exception {

        // given
        invoice.setNetAmount(bd("100.00"));
        invoice.setGrossAmount(null);

        // when
        final String result = validator.validateForAmounts(invoice).getResult();

        // then
        assertThat(result).isNull();
    }

    @Test
    public void when_net_amount_not_set_validation_is_skipped() throws Exception {

        // given
        invoice.setGrossAmount(bd("100.00"));
        invoice.setNetAmount(null);

        // when
        final String result = validator.validateForAmounts(invoice).getResult();

        // then
        assertThat(result).isNull();
    }

    @Test
    public void net_amount_does_not_match() throws Exception {

        // given
        invoice.setNetAmount(bd("100.00"));
        invoice.setGrossAmount(BigDecimal.ZERO);

        // when
        final String result = validator.validateForAmounts(invoice).getResult();

        // then
        assertThat(result).isEqualTo("total amount on items equal to amount on the invoice required");

    }

    @Test
    public void gross_amount_does_not_match() throws Exception {

        // given
        invoice.setNetAmount(BigDecimal.ZERO);
        invoice.setGrossAmount(bd("100.00"));

        // when
        final String result = validator.validateForAmounts(invoice).getResult();

        // then
        assertThat(result).isEqualTo("total amount on items equal to amount on the invoice required");
    }

    @Test
    public void when_has_items_and_amounts_do_match() throws Exception {

        // and given
        invoice.setNetAmount(bd("90.00"));
        invoice.setGrossAmount(bd("100.00"));
        assertThat(invoice.getVatAmount()).isEqualTo(bd("10.00"));

        item.setNetAmount(bd("90.00"));
        item.setGrossAmount(bd("100.00"));
        item.setVatAmount(bd("10.00"));
        invoice.getItems().add(item);

        // when
        final String result = validator.validateForAmounts(invoice).getResult();

        // then
        assertThat(result).isNull();
    }

    @Test
    public void vat_amount_does_not_match() throws Exception {

        // given
        invoice.setNetAmount(bd("90.00"));
        invoice.setGrossAmount(bd("100.00"));
        assertThat(invoice.getVatAmount()).isEqualTo(bd("10.00"));

        item.setNetAmount(bd("90.00"));
        item.setGrossAmount(bd("100.00"));
        item.setVatAmount(bd("11.00")); // should be 10.00
        invoice.getItems().add(item);

        // when
        final String result = validator.validateForAmounts(invoice).getResult();

        // then
        assertThat(result).isEqualTo("total amount on items equal to amount on the invoice required");
    }


    @Test
    public void when_all_zero_no_items_and_amounts_do_match() throws Exception {

        // given
        invoice.setGrossAmount(BigDecimal.ZERO);
        invoice.setNetAmount(BigDecimal.ZERO);

        // when
        final String result = validator.validateForAmounts(invoice).getResult();

        // then
        assertThat(result).isNull();
    }


    private static BigDecimal bd(final String val) {
        return new BigDecimal(val);
    }

}