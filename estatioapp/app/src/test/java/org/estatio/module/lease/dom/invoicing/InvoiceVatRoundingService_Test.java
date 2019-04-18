package org.estatio.module.lease.dom.invoicing;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.estatio.module.invoice.dom.InvoiceItem;
import org.estatio.module.tax.dom.TaxRate;

import static org.assertj.core.api.Assertions.assertThat;

public class InvoiceVatRoundingService_Test {

    InvoiceVatRoundingService invoiceVatRoundingService;
    TaxRate taxRate22;
    TaxRate taxRate10;
    TaxRate taxRate10AsWell;
    InvoiceForLease itaInvoice;

    @Before
    public void setup() {
        itaInvoice = new InvoiceForLease(){
            @Override
            public String getAtPath(){
                return "/ITA/bla";
            }
        };
        taxRate22 = new TaxRate();
        taxRate22.setPercentage(new BigDecimal("22.00"));
        taxRate10 = new TaxRate();
        taxRate10.setPercentage(new BigDecimal("10.00"));
        taxRate10AsWell = new TaxRate();
        taxRate10AsWell.setPercentage(new BigDecimal("10.00"));
        invoiceVatRoundingService = new InvoiceVatRoundingService();
    }

    @Test
    public void delta_Vat_Calculation_For_Percentage_works() throws Exception {
        // given, when
        List<InvoiceItemForLease> items = new ArrayList<>();
        // then
        assertThat(invoiceVatRoundingService.deltaVatCalculationForPercentage(items, taxRate22.getPercentage())).isEqualTo(new BigDecimal("0.00"));

        // when
        final InvoiceItemForLease item1 = new InvoiceItemForLease();
        item1.setNetAmount(new BigDecimal("0.07"));
        item1.setVatAmount(new BigDecimal("0.02"));
        items.add(item1);
        // then
        assertThat(invoiceVatRoundingService.deltaVatCalculationForPercentage(items, taxRate22.getPercentage())).isEqualTo(new BigDecimal("0.00"));

        // when
        final InvoiceItemForLease item2 = new InvoiceItemForLease();
        item2.setNetAmount(new BigDecimal("0.07"));
        item2.setVatAmount(new BigDecimal("0.02"));
        items.add(item2);
        // then
        assertThat(invoiceVatRoundingService.deltaVatCalculationForPercentage(items, taxRate22.getPercentage())).isEqualTo(new BigDecimal("0.01"));

        // when
        final InvoiceItemForLease item3 = new InvoiceItemForLease();
        item3.setNetAmount(new BigDecimal("-0.07"));
        item3.setVatAmount(new BigDecimal("-0.02"));
        items.add(item3);
        // then
        assertThat(invoiceVatRoundingService.deltaVatCalculationForPercentage(items, taxRate22.getPercentage())).isEqualTo(new BigDecimal("0.00"));

        // when
        final InvoiceItemForLease item4 = new InvoiceItemForLease();
        item4.setNetAmount(new BigDecimal("-0.07"));
        item4.setVatAmount(new BigDecimal("-0.02"));
        items.add(item4);
        // then
        assertThat(invoiceVatRoundingService.deltaVatCalculationForPercentage(items, taxRate22.getPercentage())).isEqualTo(new BigDecimal("0.00"));

        final InvoiceItemForLease item5 = new InvoiceItemForLease();
        item5.setNetAmount(new BigDecimal("-0.07"));
        item5.setVatAmount(new BigDecimal("-0.02"));
        items.add(item5);
        // then
        assertThat(invoiceVatRoundingService.deltaVatCalculationForPercentage(items, taxRate22.getPercentage())).isEqualTo(new BigDecimal("0.00"));

        final InvoiceItemForLease item6 = new InvoiceItemForLease();
        item6.setNetAmount(new BigDecimal("-0.07"));
        item6.setVatAmount(new BigDecimal("-0.02"));
        items.add(item6);
        // then
        assertThat(invoiceVatRoundingService.deltaVatCalculationForPercentage(items, taxRate22.getPercentage())).isEqualTo(new BigDecimal("-0.01"));

    }

    @Test
    public void distributeVatRoundingByVatPercentage_with_one_vat_rate_works() {

        // given
        itaInvoice.getItems().add(createItem(new BigDecimal("0.07"), new BigDecimal("0.02"), new BigDecimal("0.09"), taxRate22, 1));
        itaInvoice.getItems().add(createItem(new BigDecimal("0.07"), new BigDecimal("0.02"), new BigDecimal("0.09"), taxRate22, 2));
        itaInvoice.getItems().add(createItem(new BigDecimal("0.16"), new BigDecimal("0.04"), new BigDecimal("0.20"), taxRate22, 3));
        itaInvoice.getItems().add(createItem(new BigDecimal("0.16"), new BigDecimal("0.04"), new BigDecimal("0.20"), taxRate22, 4));

        List<InvoiceItem> itemsAsList = new ArrayList<>(itaInvoice.getItems());
        assertThat(itemsAsList.get(2).getVatAmount()).isEqualTo(new BigDecimal("0.04"));
        assertThat(itemsAsList.get(2).getGrossAmount()).isEqualTo(new BigDecimal("0.20"));
        assertThat(itemsAsList.get(3).getVatAmount()).isEqualTo(new BigDecimal("0.04"));
        assertThat(itemsAsList.get(3).getGrossAmount()).isEqualTo(new BigDecimal("0.20"));
        assertThat(invoiceVatRoundingService.distinctPercentagesOn(itaInvoice)).hasSize(1);

        final List<InvoiceItemForLease> itemsWith22Percent = invoiceVatRoundingService.itemsWithVatPercentage(itaInvoice, taxRate22.getPercentage());
        assertThat(itemsWith22Percent).hasSize(4);
        assertThat(invoiceVatRoundingService.deltaVatCalculationForPercentage(itemsWith22Percent, taxRate22.getPercentage())).isEqualTo(new BigDecimal("0.02"));

        // when
        itaInvoice = invoiceVatRoundingService.distributeVatRoundingByVatPercentage(itaInvoice);

        // then
        assertThat(vatAmountTotalForPercentage(itaInvoice, taxRate22.getPercentage())).isEqualTo(new BigDecimal("0.10"));
        // the highest amounts are corrected
        assertThat(itemsAsList.get(2).getVatAmount()).isEqualTo(new BigDecimal("0.03"));
        assertThat(itemsAsList.get(2).getGrossAmount()).isEqualTo(new BigDecimal("0.19"));
        assertThat(itemsAsList.get(3).getVatAmount()).isEqualTo(new BigDecimal("0.03"));
        assertThat(itemsAsList.get(3).getGrossAmount()).isEqualTo(new BigDecimal("0.19"));

    }

    @Test
    public void distributeVatRoundingByVatPercentage_with_one_vat_rate_having_negative_amounts_works() {

        // given
        itaInvoice.getItems().add(createItem(new BigDecimal("-0.07"), new BigDecimal("-0.02"), new BigDecimal("-0.09"), taxRate22, 1));
        itaInvoice.getItems().add(createItem(new BigDecimal("-0.07"), new BigDecimal("-0.02"), new BigDecimal("-0.09"), taxRate22, 2));
        itaInvoice.getItems().add(createItem(new BigDecimal("0.07"), new BigDecimal("0.02"), new BigDecimal("0.09"), taxRate22, 3));
        itaInvoice.getItems().add(createItem(new BigDecimal("-0.16"), new BigDecimal("-0.04"), new BigDecimal("-0.20"), taxRate22, 4));
        itaInvoice.getItems().add(createItem(new BigDecimal("-0.16"), new BigDecimal("-0.04"), new BigDecimal("-0.20"), taxRate22, 5));

        List<InvoiceItem> itemsAsList = new ArrayList<>(itaInvoice.getItems());
        assertThat(itemsAsList.get(0).getVatAmount()).isEqualTo(new BigDecimal("-0.04"));
        assertThat(itemsAsList.get(0).getGrossAmount()).isEqualTo(new BigDecimal("-0.20"));

        assertThat(invoiceVatRoundingService.distinctPercentagesOn(itaInvoice)).hasSize(1);
        final List<InvoiceItemForLease> itemsWith22Percent = invoiceVatRoundingService.itemsWithVatPercentage(itaInvoice, taxRate22.getPercentage());

        assertThat(itemsWith22Percent).hasSize(5);
        assertThat(invoiceVatRoundingService.deltaVatCalculationForPercentage(itemsWith22Percent, taxRate22.getPercentage())).isEqualTo(new BigDecimal("-0.01"));


        // when
        itaInvoice = invoiceVatRoundingService.distributeVatRoundingByVatPercentage(itaInvoice);

        // then
        assertThat(vatAmountTotalForPercentage(itaInvoice, taxRate22.getPercentage())).isEqualTo(new BigDecimal("-0.09"));
        // the first lowest amount is corrected
        assertThat(itemsAsList.get(0).getVatAmount()).isEqualTo(new BigDecimal("-0.03"));
        assertThat(itemsAsList.get(0).getGrossAmount()).isEqualTo(new BigDecimal("-0.19"));

    }


    @Test
    public void distributeVatRoundingByVatPercentage_with_multiple_vat_percentages_works() {

        // given
        itaInvoice.getItems().add(createItem(new BigDecimal("-0.07"), new BigDecimal("-0.02"), new BigDecimal("-0.09"), taxRate22, 1));
        itaInvoice.getItems().add(createItem(new BigDecimal("-0.07"), new BigDecimal("-0.02"), new BigDecimal("-0.09"), taxRate22, 2));
        itaInvoice.getItems().add(createItem(new BigDecimal("0.05"), new BigDecimal("0.01"), new BigDecimal("0.06"), taxRate10, 3));
        itaInvoice.getItems().add(createItem(new BigDecimal("0.05"), new BigDecimal("0.01"), new BigDecimal("0.06"), taxRate10AsWell, 4));

        assertThat(invoiceVatRoundingService.distinctPercentagesOn(itaInvoice)).hasSize(2);

        final List<InvoiceItemForLease> itemsWith22Percent = invoiceVatRoundingService.itemsWithVatPercentage(itaInvoice, taxRate22.getPercentage());
        assertThat(itemsWith22Percent).hasSize(2);
        assertThat(invoiceVatRoundingService.deltaVatCalculationForPercentage(itemsWith22Percent, taxRate22.getPercentage())).isEqualTo(new BigDecimal("-0.01"));

        final List<InvoiceItemForLease> itemsWith10Percent = invoiceVatRoundingService.itemsWithVatPercentage(itaInvoice, taxRate10.getPercentage()); // NOTE: the algorithm works with the percentage of the taxrate, and not the taxrate itself!!
        assertThat(itemsWith10Percent).hasSize(2);
        assertThat(invoiceVatRoundingService.deltaVatCalculationForPercentage(itemsWith10Percent, taxRate10.getPercentage())).isEqualTo(new BigDecimal("0.01"));

        // when
        itaInvoice = invoiceVatRoundingService.distributeVatRoundingByVatPercentage(itaInvoice);

        // then
        assertThat(vatAmountTotalForPercentage(itaInvoice, taxRate10.getPercentage())).isEqualTo("0.01");
        assertThat(vatAmountTotalForPercentage(itaInvoice, taxRate22.getPercentage())).isEqualTo(new BigDecimal("-0.03"));

    }

    @Test
    public void distributeVatRoundingByVatPercentage_without_items_works() throws Exception {
        // given // when
        invoiceVatRoundingService.distributeVatRoundingByVatPercentage(itaInvoice);
        // then nothing
    }

    private InvoiceItemForLease createItem(final BigDecimal netAmount, final BigDecimal vatAmount, final BigDecimal grossAmount, final TaxRate taxRate, final int itemNumber){
        InvoiceItemForLease item = new InvoiceItemForLease();
        item.setNetAmount(netAmount);
        item.setVatAmount(vatAmount);
        item.setGrossAmount(grossAmount);
        item.setTaxRate(taxRate);
        item.setSequence(BigInteger.valueOf(itemNumber)); // makes sorted set invoice#items work
        return item;
    }

    private BigDecimal vatAmountTotalForPercentage(final InvoiceForLease invoice, final BigDecimal percentage){
        BigDecimal total = BigDecimal.ZERO;
        for (InvoiceItem item : invoice.getItems()){
            if (item.getTaxRate().getPercentage().equals(percentage)) total = total.add(item.getVatAmount());
        }
        return total;
    }

}