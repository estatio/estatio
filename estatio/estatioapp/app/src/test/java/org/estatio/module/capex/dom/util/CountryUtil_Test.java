package org.estatio.module.capex.dom.util;

import org.junit.Before;
import org.junit.Test;

import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.order.Order;

import static org.assertj.core.api.Assertions.assertThat;

public class CountryUtil_Test {

    Order order;

    IncomingInvoice incomingInvoice;

    @Before
    public void setUp() {
        order = new Order();
        incomingInvoice = new IncomingInvoice() {
            @Override
            public String getAtPath() {
                return getApplicationTenancyPath();
            }
        };
    }

    @Test
    public void italian_order_with_atPath() throws Exception {
        // given
        order.setAtPath("/ITA");

        // when
        final boolean isItalian = CountryUtil.isItalian(order);

        // then
        assertThat(isItalian).isTrue();
    }

    @Test
    public void italian_invoice_with_atPath() throws Exception {
        // given
        incomingInvoice.setApplicationTenancyPath("/ITA");

        // when
        final boolean isItalian = CountryUtil.isItalian(incomingInvoice);

        // then
        assertThat(isItalian).isTrue();
    }

    @Test
    public void french_order_with_atPath() throws Exception {
        // given
        order.setAtPath("/FRA");

        // when
        final boolean isItalian = CountryUtil.isItalian(order);

        // then
        assertThat(isItalian).isFalse();
    }

    @Test
    public void french_invoice_with_atPath() throws Exception {
        // given
        incomingInvoice.setApplicationTenancyPath("/FRA");

        // when
        final boolean isItalian = CountryUtil.isItalian(incomingInvoice);

        // then
        assertThat(isItalian).isFalse();
    }

    @Test
    public void order_without_atPath() throws Exception {
        // when
        final boolean isItalian = CountryUtil.isItalian(order);

        // then
        assertThat(isItalian).isFalse();
    }

    @Test
    public void invoice_without_atPath() throws Exception {
        // when
        final boolean isItalian = CountryUtil.isItalian(incomingInvoice);

        // then
        assertThat(isItalian).isFalse();
    }

}
