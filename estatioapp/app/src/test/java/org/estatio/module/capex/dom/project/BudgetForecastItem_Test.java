package org.estatio.module.capex.dom.project;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.capex.dom.invoice.contributions.ProjectItem_IncomingInvoiceItems;
import org.estatio.module.charge.dom.Charge;

public class BudgetForecastItem_Test {

    @Test
    public void getSumAmountTerms_works() throws Exception {

        // given
        BudgetForecastItem item = new BudgetForecastItem();
        // when, then
        Assertions.assertThat(item.getSumTerms()).isNull();

        // when
        final BudgetForecastTerm term1 = new BudgetForecastTerm();
        final BigDecimal amount = BigDecimal.valueOf(123);
        term1.setAmount(amount);
        term1.setStartDate(new LocalDate(2020,1,1));
        item.getTerms().add(term1);
        // then
        Assertions.assertThat(item.getSumTerms()).isEqualTo(amount);

        // when
        final BudgetForecastTerm term2 = new BudgetForecastTerm();
        final BigDecimal amount2 = BigDecimal.valueOf(0.45);
        term2.setAmount(amount2);
        term2.setStartDate(new LocalDate(2020,4,1));
        item.getTerms().add(term2);
        // then
        Assertions.assertThat(item.getSumTerms()).isEqualTo(BigDecimal.valueOf(123.45));
        Assertions.assertThat(item.getSumTerms()).isEqualTo(amount.add(amount2));

    }

    @Test
    public void getForecastedAmountCovered_works() {

        // given
        BudgetForecastItem item = new BudgetForecastItem();
        // when
        // then
        Assertions.assertThat(item.getForecastedAmountCovered()).isFalse();

        // when
        final BigDecimal forecastedAmount = BigDecimal.valueOf(123.45);
        item.setAmount(forecastedAmount);
        // then
        Assertions.assertThat(item.getForecastedAmountCovered()).isFalse();

        // when
        final BudgetForecastTerm term1 = new BudgetForecastTerm();
        term1.setAmount(BigDecimal.valueOf(123));
        term1.setStartDate(new LocalDate(2020,1,1));
        item.getTerms().add(term1);
        // then
        Assertions.assertThat(item.getForecastedAmountCovered()).isFalse();

        // when
        final BudgetForecastTerm term2 = new BudgetForecastTerm();
        term2.setAmount(BigDecimal.valueOf(0.45));
        term2.setStartDate(new LocalDate(2020,4,1));
        item.getTerms().add(term2);
        // then
        Assertions.assertThat(item.getForecastedAmountCovered()).isTrue();

        // when
        final BigDecimal forecastedAmountNoDecimals = BigDecimal.valueOf(124);
        item.setAmount(forecastedAmountNoDecimals);
        // then
        Assertions.assertThat(item.getForecastedAmountCovered()).isFalse();

        // when
        final BudgetForecastTerm term3 = new BudgetForecastTerm();
        term3.setAmount(BigDecimal.valueOf(0.55));
        term3.setStartDate(new LocalDate(2020,7,1));
        item.getTerms().add(term3);
        // then
        Assertions.assertThat(item.getForecastedAmountCovered()).isTrue();

    }

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock FactoryService mockFactoryService;

    @Test
    public void calculateAmounts_works() throws Exception {

        // given
        BigDecimal budgetedAmount = BigDecimal.valueOf(1234.56);
        BigDecimal invoicedAmount = BigDecimal.valueOf(1000);
        LocalDate forecastDate = new LocalDate(2020,1,1);

        final ProjectItem projectItem = new ProjectItem();
        final Charge charge = new Charge(); // to distinguish in sorted sets
        charge.setReference("REF123"); // to distinguish in sorted sets
        projectItem.setCharge(charge); // to distinguish in sorted sets
        final ProjectBudget budget = new ProjectBudget();
        ProjectBudgetItem budgetItemForProjectItem = new ProjectBudgetItem();
        budgetItemForProjectItem.setProjectItem(projectItem);
        budgetItemForProjectItem.setAmount(budgetedAmount);

        ProjectBudgetItem otherBudgetItem = new ProjectBudgetItem();
        otherBudgetItem.setProjectItem(new ProjectItem());
        otherBudgetItem.setAmount(BigDecimal.valueOf(1111.11));

        budget.getItems().add(otherBudgetItem);
        budget.getItems().add(budgetItemForProjectItem);

        BudgetForecastItem item = new BudgetForecastItem();
        item.setProjectItem(projectItem);
        item.factoryService = mockFactoryService;

        Project project = new Project(){
            @Override public ProjectBudget getLatestCommittedBudget() {
                return budget;
            }
        };

        BudgetForecast forecast = new BudgetForecast();
        forecast.setProject(project);
        forecast.setDate(forecastDate);
        item.setForecast(forecast);

        List<IncomingInvoiceItem> invoiceItemsOnProjectItem = new ArrayList<>();

        ProjectItem_IncomingInvoiceItems mixin = new ProjectItem_IncomingInvoiceItems(projectItem){
            @Override public List<IncomingInvoiceItem> invoiceItems() {
                return invoiceItemsOnProjectItem;
            }
        };

        // expect
        context.checking(new Expectations(){{
            allowing(mockFactoryService).mixin(ProjectItem_IncomingInvoiceItems.class, projectItem);
            will(returnValue(mixin));
        }});

        // when no invoices
        item.calculateAmounts();
        // then
        Assertions.assertThat(item.getInvoicedAmountToDate()).isEqualTo(BigDecimal.ZERO);
        Assertions.assertThat(item.getBudgetedAmountOnDate()).isEqualTo(budgetedAmount);
        Assertions.assertThat(item.getAmount()).isEqualTo(budgetedAmount);

        // and when there are invoices but on forecast date or later
        IncomingInvoice invoiceOnForecastDate = new IncomingInvoice();
        invoiceOnForecastDate.setInvoiceDate(forecastDate);
        IncomingInvoiceItem neglectedInvoiceItem = new IncomingInvoiceItem();
        neglectedInvoiceItem.setInvoice(invoiceOnForecastDate);
        neglectedInvoiceItem.setNetAmount(invoicedAmount);
        invoiceItemsOnProjectItem.add(neglectedInvoiceItem);

        item.calculateAmounts();
        // then still
        Assertions.assertThat(item.getInvoicedAmountToDate()).isEqualTo(BigDecimal.ZERO);
        Assertions.assertThat(item.getBudgetedAmountOnDate()).isEqualTo(budgetedAmount);
        Assertions.assertThat(item.getAmount()).isEqualTo(budgetedAmount);

        // and when there are invoices but before forecast date
        IncomingInvoice invoiceBeforeForecastDate1 = new IncomingInvoice();
        invoiceBeforeForecastDate1.setInvoiceDate(forecastDate.minusDays(1));
        IncomingInvoiceItem invoiceItem1 = new IncomingInvoiceItem();
        invoiceItem1.setInvoice(invoiceBeforeForecastDate1);
        invoiceItem1.setNetAmount(invoicedAmount.subtract(BigDecimal.valueOf(400)));
        invoiceItemsOnProjectItem.add(invoiceItem1);

        IncomingInvoice invoiceBeforeForecastDate2 = new IncomingInvoice();
        invoiceBeforeForecastDate2.setInvoiceDate(forecastDate.minusDays(2));
        IncomingInvoiceItem invoiceItem2 = new IncomingInvoiceItem();
        invoiceItem2.setInvoice(invoiceBeforeForecastDate2);
        invoiceItem2.setNetAmount(BigDecimal.valueOf(400));
        invoiceItemsOnProjectItem.add(invoiceItem2);

        item.calculateAmounts();
        //then
        Assertions.assertThat(item.getInvoicedAmountToDate()).isEqualTo(invoicedAmount);
        Assertions.assertThat(item.getBudgetedAmountOnDate()).isEqualTo(budgetedAmount);
        Assertions.assertThat(item.getAmount()).isEqualTo(BigDecimal.valueOf(234.56));
        Assertions.assertThat(item.getAmount()).isEqualTo(budgetedAmount.subtract(invoicedAmount));

        // and when overspent
        IncomingInvoice invoiceBeforeForecastDate3 = new IncomingInvoice();
        invoiceBeforeForecastDate3.setInvoiceDate(forecastDate.minusDays(1));
        IncomingInvoiceItem invoiceItem3 = new IncomingInvoiceItem();
        invoiceItem3.setInvoice(invoiceBeforeForecastDate1);
        invoiceItem3.setNetAmount(BigDecimal.valueOf(234.57));
        invoiceItemsOnProjectItem.add(invoiceItem3);

        item.calculateAmounts();
        //then
        Assertions.assertThat(item.getInvoicedAmountToDate()).isEqualTo(budgetedAmount.add(BigDecimal.valueOf(0.01)));
        Assertions.assertThat(item.getBudgetedAmountOnDate()).isEqualTo(budgetedAmount);
        Assertions.assertThat(item.getAmount()).isEqualTo(BigDecimal.valueOf(-0.01));

    }

}