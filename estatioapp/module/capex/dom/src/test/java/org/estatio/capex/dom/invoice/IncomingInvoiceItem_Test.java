package org.estatio.capex.dom.invoice;

import java.math.BigDecimal;
import java.util.Arrays;

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.capex.dom.order.OrderItem;
import org.estatio.capex.dom.orderinvoice.OrderItemInvoiceItemLink;
import org.estatio.capex.dom.orderinvoice.OrderItemInvoiceItemLinkRepository;
import org.estatio.capex.dom.project.Project;
import org.estatio.dom.asset.Property;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.charge.Charge;

public class IncomingInvoiceItem_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    IncomingInvoice mockInvoice;

    @Mock
    RepositoryService mockRepositoryService;

    @Test
    public void removeItem() throws Exception {

        // given
        IncomingInvoiceItem item = new IncomingInvoiceItem(){
            @Override
            boolean isLinkedToOrderItem(){
                return false;
            }
        };
        item.setInvoice(mockInvoice);
        item.repositoryService = mockRepositoryService;

        // expect
        context.checking(new Expectations(){
            {
                oneOf(mockRepositoryService).removeAndFlush(item);
                oneOf(mockInvoice).recalculateAmounts();
            }
        });

        // when
        item.removeItem();

    }

    @Test
    public void subtractAmounts_works() throws Exception {

        IncomingInvoiceItem item = new IncomingInvoiceItem();
        item.setInvoice(new IncomingInvoice());

        // given
        BigDecimal amount = new BigDecimal("100.00");
        item.setNetAmount(amount);
        item.setVatAmount(null);
        item.setGrossAmount(amount);

        // when
        BigDecimal netToSubtract = new BigDecimal("50.50");
        BigDecimal vatToSubtract = new BigDecimal("10.10");
        BigDecimal grossToSubtract = null;
        item.subtractAmounts(netToSubtract, vatToSubtract, grossToSubtract);

        // then
        Assertions.assertThat(item.getNetAmount()).isEqualTo(new BigDecimal("49.50"));
        Assertions.assertThat(item.getVatAmount()).isEqualTo(new BigDecimal("-10.10"));
        Assertions.assertThat(item.getGrossAmount()).isEqualTo(new BigDecimal("100.00"));

    }

    @Test
    public void addAmounts_works() throws Exception {

        IncomingInvoiceItem item = new IncomingInvoiceItem();
        item.setInvoice(new IncomingInvoice());

        // given
        BigDecimal amount = new BigDecimal("100.00");
        item.setNetAmount(amount);
        item.setVatAmount(null);
        item.setGrossAmount(amount);

        // when
        BigDecimal netToAdd = new BigDecimal("50.50");
        BigDecimal vatToAdd = new BigDecimal("10.10");
        BigDecimal grossToAdd = null;
        item.addAmounts(netToAdd, vatToAdd, grossToAdd);

        // then
        Assertions.assertThat(item.getNetAmount()).isEqualTo(new BigDecimal("150.50"));
        Assertions.assertThat(item.getVatAmount()).isEqualTo(new BigDecimal("10.10"));
        Assertions.assertThat(item.getGrossAmount()).isEqualTo(new BigDecimal("100.00"));

    }

    @Test
    public void reasonIncomplete_works() throws Exception {

        // given
        IncomingInvoiceItem item = new IncomingInvoiceItem();
        item.setInvoice(new IncomingInvoice());

        // when, then
        Assertions.assertThat(item.reasonIncomplete()).isEqualTo("incoming invoice type, start date, end date, net amount, vat amount, gross amount, charge required");

        // and when
        item.setStartDate(new LocalDate());
        item.setEndDate(new LocalDate());
        item.setNetAmount(BigDecimal.ZERO);
        item.setVatAmount(BigDecimal.ZERO);
        item.setGrossAmount(BigDecimal.ZERO);
        item.setCharge(new Charge());
        item.setIncomingInvoiceType(IncomingInvoiceType.SERVICE_CHARGES);
        Charge chargeForBudgetItem = new Charge();
        BudgetItem budgetItem = new BudgetItem();
        budgetItem.setCharge(chargeForBudgetItem);
        item.setBudgetItem(budgetItem);
        // then
        Assertions.assertThat(item.reasonIncomplete()).isEqualTo("fixed asset, equal charge on budget item and invoice item required");

        // and when all conditions satisfied
        item.setFixedAsset(new Property());
        item.setCharge(chargeForBudgetItem);
        // then
        Assertions.assertThat(item.reasonIncomplete()).isNull();

    }

    @Test
    public void validator_checkNotNull_works() throws Exception {

        String result;

        // given
        IncomingInvoiceItem.Validator validator = new IncomingInvoiceItem.Validator();

        // when condition satisfied
        result = validator.checkNotNull(new Object(), "some property name").getResult();
        // then
        Assertions.assertThat(result).isNull();

        // and when not conditions satisfied
        result = validator.checkNotNull(null, "some property name").getResult();
        // then
        Assertions.assertThat(result).isEqualTo("some property name required");

    }

    @Test
    public void validator_validateForIncomingInvoiceType_works() throws Exception {

        String result;
        IncomingInvoiceItem.Validator validator;
        IncomingInvoiceItem item;

        // given
        validator = new IncomingInvoiceItem.Validator();
        item = new IncomingInvoiceItem();
        item.setInvoice(new IncomingInvoice());

        // when
        item.setIncomingInvoiceType(IncomingInvoiceType.CAPEX);
        result = validator.validateForIncomingInvoiceType(item).getResult();
        // then
        Assertions.assertThat(result).isEqualTo("project (capex), fixed asset required");

        // and given
        validator = new IncomingInvoiceItem.Validator();
        // when
        item.setFixedAsset(new Property());
        result = validator.validateForIncomingInvoiceType(item).getResult();
        // then
        Assertions.assertThat(result).isEqualTo("project (capex) required");

        // and given
        validator = new IncomingInvoiceItem.Validator();
        // when all conditions satisfied
        item.setProject(new Project());
        result = validator.validateForIncomingInvoiceType(item).getResult();
        // then
        Assertions.assertThat(result).isNull();

        // and given
        validator = new IncomingInvoiceItem.Validator();
        item = new IncomingInvoiceItem();
        item.setInvoice(new IncomingInvoice());
        // when
        item.setIncomingInvoiceType(IncomingInvoiceType.SERVICE_CHARGES);
        result = validator.validateForIncomingInvoiceType(item).getResult();
        // then
        Assertions.assertThat(result).isEqualTo("budget item (service charges), fixed asset required");

        // and given
        validator = new IncomingInvoiceItem.Validator();
        // when
        item.setFixedAsset(new Property());
        result = validator.validateForIncomingInvoiceType(item).getResult();
        // then
        Assertions.assertThat(result).isEqualTo("budget item (service charges) required");

        // and given
        validator = new IncomingInvoiceItem.Validator();
        // when
        BudgetItem budgetItem = new BudgetItem();
        Charge chargeForBudgetItem = new Charge();
        Charge chargeForInvoiceItem = new Charge();
        budgetItem.setCharge(chargeForBudgetItem);
        item.setBudgetItem(budgetItem);
        item.setCharge(chargeForInvoiceItem);
        result = validator.validateForIncomingInvoiceType(item).getResult();
        // then
        Assertions.assertThat(result).isEqualTo("equal charge on budget item and invoice item required");

        // and given
        validator = new IncomingInvoiceItem.Validator();
        // when all conditions satisfied
        item.setCharge(chargeForBudgetItem);
        result = validator.validateForIncomingInvoiceType(item).getResult();
        // then
        Assertions.assertThat(result).isNull();

        // and given
        validator = new IncomingInvoiceItem.Validator();
        item = new IncomingInvoiceItem();
        item.setInvoice(new IncomingInvoice());
        // when
        item.setIncomingInvoiceType(IncomingInvoiceType.PROPERTY_EXPENSES);
        result = validator.validateForIncomingInvoiceType(item).getResult();
        // then
        Assertions.assertThat(result).isEqualTo("fixed asset required");

        // and given
        validator = new IncomingInvoiceItem.Validator();
        item = new IncomingInvoiceItem();
        item.setInvoice(new IncomingInvoice());
        // when all conditions satisfied
        item.setIncomingInvoiceType(IncomingInvoiceType.LOCAL_EXPENSES);
        result = validator.validateForIncomingInvoiceType(item).getResult();
        // then
        Assertions.assertThat(result).isNull();

        // and when all conditions satisfied
        item.setIncomingInvoiceType(IncomingInvoiceType.INTERCOMPANY);
        result = validator.validateForIncomingInvoiceType(item).getResult();
        // when all conditions satisfied
        Assertions.assertThat(result).isNull();

        // and when all conditions satisfied
        item.setIncomingInvoiceType(IncomingInvoiceType.TANGIBLE_FIXED_ASSET);
        result = validator.validateForIncomingInvoiceType(item).getResult();
        // then
        Assertions.assertThat(result).isNull();

        // and when all conditions satisfied
        item.setIncomingInvoiceType(IncomingInvoiceType.RE_INVOICING);
        result = validator.validateForIncomingInvoiceType(item).getResult();
        // then
        Assertions.assertThat(result).isNull();

        // and when all conditions satisfied
        item.setIncomingInvoiceType(IncomingInvoiceType.CORPORATE_EXPENSES);
        result = validator.validateForIncomingInvoiceType(item).getResult();
        // then
        Assertions.assertThat(result).isNull();

    }

    @Mock
    OrderItemInvoiceItemLinkRepository mockOrderItemInvoiceItemLinkRepository;

    @Test
    public void copyChargeAndProjectFromSingleLinkedOrderItemIfAny_no_item_works() throws Exception {

        // given
        IncomingInvoiceItem invoiceItem = new IncomingInvoiceItem();
        invoiceItem.orderItemInvoiceItemLinkRepository = mockOrderItemInvoiceItemLinkRepository;

        // expect
        context.checking(new Expectations(){{
            oneOf(mockOrderItemInvoiceItemLinkRepository).findByInvoiceItem(invoiceItem);
        }});

        // when
        invoiceItem.copyChargeAndProjectFromSingleLinkedOrderItemIfAny();

    }

    @Test
    public void copyChargeAndProjectFromSingleLinkedOrderItemIfAny_two_items_works() throws Exception {

        // given
        IncomingInvoiceItem invoiceItem = new IncomingInvoiceItem();
        invoiceItem.orderItemInvoiceItemLinkRepository = mockOrderItemInvoiceItemLinkRepository;
        OrderItemInvoiceItemLink link1 = new OrderItemInvoiceItemLink();
        OrderItemInvoiceItemLink link2 = new OrderItemInvoiceItemLink();

        // expect
        context.checking(new Expectations(){{
            oneOf(mockOrderItemInvoiceItemLinkRepository).findByInvoiceItem(invoiceItem);
            will(returnValue(Arrays.asList(link1, link2)));
        }});

        // when
        invoiceItem.copyChargeAndProjectFromSingleLinkedOrderItemIfAny();

        // then
        Assertions.assertThat(invoiceItem.getCharge()).isNull();
        Assertions.assertThat(invoiceItem.getProject()).isNull();

    }


    @Test
    public void copyChargeAndProjectFromSingleLinkedOrderItemIfAny_one_item_works() throws Exception {

        // given
        IncomingInvoiceItem invoiceItem = new IncomingInvoiceItem(){
            @Override
            void invalidateApproval(){}
        };
        invoiceItem.orderItemInvoiceItemLinkRepository = mockOrderItemInvoiceItemLinkRepository;

        OrderItem orderItem = new OrderItem();
        Charge charge = new Charge();
        orderItem.setCharge(charge);
        Project project = new Project();
        orderItem.setProject(project);
        Property property = new Property();
        orderItem.setProperty(property);

        OrderItemInvoiceItemLink link = new OrderItemInvoiceItemLink();
        link.setOrderItem(orderItem);

        // expect
        context.checking(new Expectations(){{
            allowing(mockOrderItemInvoiceItemLinkRepository).findByInvoiceItem(invoiceItem);
            will(returnValue(Arrays.asList(link)));
        }});

        // when
        invoiceItem.copyChargeAndProjectFromSingleLinkedOrderItemIfAny();

        // then
        Assertions.assertThat(invoiceItem.getCharge()).isEqualTo(charge);
        Assertions.assertThat(invoiceItem.getProject()).isEqualTo(project);
        Assertions.assertThat(invoiceItem.getFixedAsset()).isEqualTo(property);

    }

}