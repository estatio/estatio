package org.estatio.module.budgetassignment.dom;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.budget.dom.budgetitem.BudgetItem;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItemRepository;

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

    @Test
    public void netamountForInvoiceItemAndCalculationInterval_works() throws Exception {

        // given
        BudgetService service = new BudgetService();

        LocalDate calculationStartDate = new LocalDate(2019, 7, 1);
        LocalDate calculationEndDate = new LocalDate(2020,9,10);
        LocalDateInterval calculationInterval = LocalDateInterval.including(calculationStartDate, calculationEndDate);

        BigDecimal invoiceItemNetAmount = new BigDecimal("1234.56");
        IncomingInvoiceItem invoiceItem = new IncomingInvoiceItem(){
            @Override public BigDecimal getNetAmount() {
                return invoiceItemNetAmount;
            }
        };

        // when no charge period the total amount is returned
        invoiceItem.setChargeStartDate(null);
        invoiceItem.setChargeEndDate(null);
        // then
        Assertions.assertThat(service.netamountForInvoiceItemAndCalculationInterval(invoiceItem, calculationInterval)).isEqualTo(invoiceItemNetAmount);

        // when charge interval before calculationInterval a zero value is returned
        invoiceItem.setChargeStartDate(null);
        invoiceItem.setChargeEndDate(calculationStartDate.minusDays(1));
        // then
        Assertions.assertThat(service.netamountForInvoiceItemAndCalculationInterval(invoiceItem, calculationInterval)).isEqualTo(BigDecimal.ZERO);

        // when charge interval after calculationInterval a zero value is returned
        invoiceItem.setChargeStartDate(calculationEndDate.plusDays(1));
        invoiceItem.setChargeEndDate(null);
        // then
        Assertions.assertThat(service.netamountForInvoiceItemAndCalculationInterval(invoiceItem, calculationInterval)).isEqualTo(BigDecimal.ZERO);

        // when charge interval contained by calculationInterval the total amount is returned
        invoiceItem.setChargeStartDate(calculationStartDate);
        invoiceItem.setChargeEndDate(calculationEndDate);
        // then
        Assertions.assertThat(service.netamountForInvoiceItemAndCalculationInterval(invoiceItem, calculationInterval)).isEqualTo(invoiceItemNetAmount);

        //  when charge interval and calculationInterval partially overlap a pro rata amount is returned
        // 2/3 of charge interval after calculation interval
        invoiceItem.setChargeStartDate(calculationEndDate);
        invoiceItem.setChargeEndDate(calculationEndDate.plusDays(2));
        // then
        Assertions.assertThat(service.netamountForInvoiceItemAndCalculationInterval(invoiceItem, calculationInterval))
                .isEqualTo(invoiceItemNetAmount.divide(new BigDecimal("3", MathContext.DECIMAL64)).setScale(6, RoundingMode.HALF_UP));
        Assertions.assertThat(service.netamountForInvoiceItemAndCalculationInterval(invoiceItem, calculationInterval))
                .isEqualTo(new BigDecimal("411.520000"));

        // A little scenario

        // 5/5 of charge interval before calculation interval
        invoiceItem.setChargeStartDate(calculationStartDate.minusDays(5));
        invoiceItem.setChargeEndDate(calculationStartDate.minusDays(1));
        // then
        Assertions.assertThat(service.netamountForInvoiceItemAndCalculationInterval(invoiceItem, calculationInterval))
                .isEqualTo(BigDecimal.ZERO);

        // 4/5 of charge interval before calculation interval
        invoiceItem.setChargeStartDate(calculationStartDate.minusDays(4));
        invoiceItem.setChargeEndDate(calculationStartDate);
        // then
        Assertions.assertThat(service.netamountForInvoiceItemAndCalculationInterval(invoiceItem, calculationInterval))
                .isEqualTo(new BigDecimal("246.912000"));

        // 3/5 of charge interval before calculation interval
        invoiceItem.setChargeStartDate(calculationStartDate.minusDays(3));
        invoiceItem.setChargeEndDate(calculationStartDate.plusDays(1));
        // then
        Assertions.assertThat(service.netamountForInvoiceItemAndCalculationInterval(invoiceItem, calculationInterval))
                .isEqualTo(new BigDecimal("493.824000"));

        // 2/5 of charge interval before calculation interval
        invoiceItem.setChargeStartDate(calculationStartDate.minusDays(2));
        invoiceItem.setChargeEndDate(calculationStartDate.plusDays(2));
        // then
        Assertions.assertThat(service.netamountForInvoiceItemAndCalculationInterval(invoiceItem, calculationInterval))
                .isEqualTo(new BigDecimal("740.736000"));

        // 1/5 of charge interval before calculation interval
        invoiceItem.setChargeStartDate(calculationStartDate.minusDays(1));
        invoiceItem.setChargeEndDate(calculationStartDate.plusDays(3));
        // then
        Assertions.assertThat(service.netamountForInvoiceItemAndCalculationInterval(invoiceItem, calculationInterval))
                .isEqualTo(new BigDecimal("987.648000"));

        // 0/5 of charge interval before calculation interval
        invoiceItem.setChargeStartDate(calculationStartDate);
        invoiceItem.setChargeEndDate(calculationStartDate.plusDays(4));
        // then
        Assertions.assertThat(service.netamountForInvoiceItemAndCalculationInterval(invoiceItem, calculationInterval))
                .isEqualTo(new BigDecimal("1234.56"));

    }

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    IncomingInvoiceItemRepository mockIncomingInvoiceItemRepository;

    @Test
    public void auditedValueForBudgetItemAndInterval_works() throws Exception {

        // given
        BudgetService service = new BudgetService();
        service.incomingInvoiceItemRepository = mockIncomingInvoiceItemRepository;
        BudgetItem budgetItem = new BudgetItem();
        LocalDate calculationStartDate = new LocalDate(2019, 7, 1);
        LocalDate calculationEndDate = new LocalDate(2020,9,10);

        List<IncomingInvoiceItem> invoiceItemList = new ArrayList<>();

        IncomingInvoiceItem itemWithChargePeriod4of5DaysOutsideCalculationPeriod = new IncomingInvoiceItem(){
            @Override public BigDecimal getNetAmount() {
                return new BigDecimal("1234.56");
            }
        };
        itemWithChargePeriod4of5DaysOutsideCalculationPeriod.setChargeStartDate(calculationStartDate.minusDays(4));
        itemWithChargePeriod4of5DaysOutsideCalculationPeriod.setChargeEndDate(calculationStartDate);
        invoiceItemList.add(itemWithChargePeriod4of5DaysOutsideCalculationPeriod);

        IncomingInvoiceItem itemWithChargePeriodInCalculationPeriod = new IncomingInvoiceItem(){
            @Override public BigDecimal getNetAmount() {
                return new BigDecimal("1000.00");
            }
        };
        itemWithChargePeriodInCalculationPeriod.setChargeStartDate(calculationEndDate.minusDays(1));
        itemWithChargePeriodInCalculationPeriod.setChargeEndDate(calculationEndDate);
        invoiceItemList.add(itemWithChargePeriodInCalculationPeriod);

        // expect
        context.checking(new Expectations(){{
            oneOf(mockIncomingInvoiceItemRepository).findByBudgetItem(budgetItem);
            will(returnValue(invoiceItemList));
        }});

        // when
        final BigDecimal returnValue = service
                .auditedValueForBudgetItemAndCalculationInterval(budgetItem, calculationStartDate, calculationEndDate);
        Assertions.assertThat(returnValue).isEqualTo(new BigDecimal("1246.912000"));

    }
}