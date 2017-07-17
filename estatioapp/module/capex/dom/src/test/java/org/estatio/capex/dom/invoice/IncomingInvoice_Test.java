package org.estatio.capex.dom.invoice;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.dom.financial.bankaccount.BankAccount;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.party.Organisation;

import static org.assertj.core.api.Assertions.assertThat;

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

    @Test
    public void minimalRequiredDataToComplete() throws Exception {

        // given
        IncomingInvoice invoice = new IncomingInvoice();
        invoice.setPaymentMethod(PaymentMethod.MANUAL_PROCESS);
        invoice.setBankAccount(new BankAccount());

        IncomingInvoiceItem item1 = new IncomingInvoiceItem();
        item1.setSequence(BigInteger.ONE);
        invoice.getItems().add(item1);

        // when
        String result = invoice.reasonIncomplete();

        // then
        assertThat(result).isEqualTo("invoice number, buyer, seller, date received, due date, net amount, gross amount, (on item 1) start date, end date, net amount, gross amount required");

        // and when
        invoice.setInvoiceNumber("123");
        invoice.setNetAmount(new BigDecimal("100"));
        item1.setStartDate(new LocalDate());
        item1.setEndDate(new LocalDate());
        item1.setNetAmount(new BigDecimal("100"));
        item1.setGrossAmount(new BigDecimal("100"));
        result = invoice.reasonIncomplete();

        // then
        assertThat(result).isEqualTo("buyer, seller, date received, due date, gross amount required");

        // and when
        invoice.setBuyer(new Organisation());
        invoice.setSeller(new Organisation());
        invoice.setBankAccount(new BankAccount());
        invoice.setDateReceived(new LocalDate());
        invoice.setDueDate(new LocalDate());
        invoice.setGrossAmount(BigDecimal.ZERO);
        result = invoice.reasonIncomplete();

        // then
        assertThat(result).isNull();

    }

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    IncomingInvoice.addItem mockAddItemMixin;

    @Test
    public void splitItem_mixin_works() throws Exception {

        // given
        IncomingInvoice invoice = new IncomingInvoice();
        IncomingInvoice.splitItem mixin = new IncomingInvoice.splitItem(invoice);
        mixin.factoryService = new FactoryService() {
            @Override public <T> T instantiate(final Class<T> aClass) {
                return null;
            }

            @Override public <T> T mixin(final Class<T> aClass, final Object o) {
                return (T) mockAddItemMixin;
            }

        };

        IncomingInvoiceItem item = new IncomingInvoiceItem();
        item.setNetAmount(new BigDecimal("200.00"));
        item.setVatAmount(new BigDecimal("40.00"));
        item.setGrossAmount(new BigDecimal("240.00"));

        BigDecimal newItemNetAmount = new BigDecimal("50.00");
        BigDecimal newItemVatAmount = new BigDecimal("10");
        BigDecimal newItemGrossAmount = new BigDecimal("60.00");

        // expect
        context.checking(new Expectations(){{
            oneOf(mockAddItemMixin).act(
                    null,
                    null,
                    null,
                    newItemNetAmount,
                    newItemVatAmount,
                    newItemGrossAmount, null, null, null, null,null, null);
        }});

        // when
        mixin.act(item, null, newItemNetAmount, newItemVatAmount, null, newItemGrossAmount,null, null, null, null, null);

        // then
        Assertions.assertThat(item.getNetAmount()).isEqualTo(new BigDecimal("150.00"));
        Assertions.assertThat(item.getVatAmount()).isEqualTo(new BigDecimal("30.00"));
        Assertions.assertThat(item.getGrossAmount()).isEqualTo(new BigDecimal("180.00"));

    }

    @Test
    public void splitItem_mixin_works_with_no_new_vat_value() throws Exception {

        // given
        IncomingInvoice invoice = new IncomingInvoice();
        IncomingInvoice.splitItem mixin = new IncomingInvoice.splitItem(invoice);
        mixin.factoryService = new FactoryService() {
            @Override public <T> T instantiate(final Class<T> aClass) {
                return null;
            }

            @Override public <T> T mixin(final Class<T> aClass, final Object o) {
                return (T) mockAddItemMixin;
            }

        };

        IncomingInvoiceItem item = new IncomingInvoiceItem();
        item.setNetAmount(new BigDecimal("200.00"));
        item.setVatAmount(new BigDecimal("40.00"));
        item.setGrossAmount(new BigDecimal("240.00"));

        BigDecimal newItemNetAmount = new BigDecimal("50.00");
        BigDecimal newItemVatAmount = null;
        BigDecimal newItemGrossAmount = new BigDecimal("60.00");

        // expect
        context.checking(new Expectations(){{
            oneOf(mockAddItemMixin).act(
                    null,
                    null,
                    null,
                    newItemNetAmount,
                    newItemVatAmount,
                    newItemGrossAmount, null, null, null, null,null, null);
        }});

        // when
        mixin.act(item, null, newItemNetAmount, newItemVatAmount, null, newItemGrossAmount,null, null, null, null, null);

        // then
        Assertions.assertThat(item.getNetAmount()).isEqualTo(new BigDecimal("150.00"));
        Assertions.assertThat(item.getVatAmount()).isEqualTo(new BigDecimal("40.00"));
        Assertions.assertThat(item.getGrossAmount()).isEqualTo(new BigDecimal("180.00"));

    }

    @Test
    public void splitItem_mixin_works_with_no_given_item_values() throws Exception {

        // given
        IncomingInvoice invoice = new IncomingInvoice();
        IncomingInvoice.splitItem mixin = new IncomingInvoice.splitItem(invoice);
        mixin.factoryService = new FactoryService() {
            @Override public <T> T instantiate(final Class<T> aClass) {
                return null;
            }

            @Override public <T> T mixin(final Class<T> aClass, final Object o) {
                return (T) mockAddItemMixin;
            }

        };

        IncomingInvoiceItem item = new IncomingInvoiceItem();
        item.setNetAmount(null);
        item.setVatAmount(null);
        item.setGrossAmount(null);

        BigDecimal newItemNetAmount = new BigDecimal("50.00");
        BigDecimal newItemVatAmount = new BigDecimal("10.00");
        BigDecimal newItemGrossAmount = new BigDecimal("60.00");

        // expect
        context.checking(new Expectations(){{
            oneOf(mockAddItemMixin).act(
                    null,
                    null,
                    null,
                    newItemNetAmount,
                    newItemVatAmount,
                    newItemGrossAmount, null, null, null, null,null, null);
        }});

        // when
        mixin.act(item, null, newItemNetAmount, newItemVatAmount, null, newItemGrossAmount,null, null, null, null, null);

        // then
        Assertions.assertThat(item.getNetAmount()).isNull();
        Assertions.assertThat(item.getVatAmount()).isNull();
        Assertions.assertThat(item.getGrossAmount()).isNull();

    }
}