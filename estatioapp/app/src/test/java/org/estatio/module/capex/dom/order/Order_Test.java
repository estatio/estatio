package org.estatio.module.capex.dom.order;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.budget.dom.budgetitem.BudgetItem;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.capex.dom.util.PeriodUtil;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.tax.dom.Tax;

import static org.assertj.core.api.Assertions.assertThat;

public class Order_Test {

    @Test
    public void minimalRequiredDataToComplete() throws Exception {

        // given
        Order order = new Order();
        order.setAtPath("/FRA");
        OrderItem item1 = new OrderItem();
        order.getItems().add(item1);
        item1.setOrdr(order);

        // when
        String result = order.reasonIncomplete();

        // then
        assertThat(result).isEqualTo("type, order number, buyer, seller, (on item) description, charge, start date, end date, net amount, gross amount required");

        // and when
        order.setOrderNumber("123");
        order.setType(IncomingInvoiceType.CAPEX);
        order.setProperty(new Property());
        item1.setNetAmount(new BigDecimal("100"));
        result = order.reasonIncomplete();

        // then
        assertThat(result).isEqualTo("buyer, seller, (on item) description, charge, start date, end date, gross amount required");

        // and when
        order.setBuyer(new Organisation());
        order.setSeller(new Organisation());
        item1.setDescription("blah");
        item1.setGrossAmount(BigDecimal.ZERO);
        item1.setCharge(new Charge());
        item1.setStartDate(new LocalDate());
        item1.setEndDate(new LocalDate());
        result = order.reasonIncomplete();

        // then
        assertThat(result).isNull();

    }

    @Test
    public void minimalRequiredDataToComplete_consitent_dimensions_for_property_project() throws Exception {

        // given
        Order order = new Order();
        order.setAtPath("/FRA");
        OrderItem item1 = new OrderItem();
        item1.setOrdr(order);
        order.getItems().add(item1);
        order.setOrderNumber("123");
        order.setType(IncomingInvoiceType.CAPEX);
        order.setBuyer(new Organisation());
        order.setSeller(new Organisation());
        item1.setNetAmount(new BigDecimal("100"));
        item1.setDescription("blah");
        item1.setGrossAmount(BigDecimal.ZERO);
        item1.setCharge(new Charge());
        item1.setStartDate(new LocalDate());
        item1.setEndDate(new LocalDate());

        // when
        item1.setProject(new Project());
        String result = order.reasonIncomplete();

        // then
        assertThat(result).isEqualTo("property, (on item) when project filled in then property required");

    }

    @Test
    public void minimalRequiredDataToComplete_consitent_dimensions_for_property_budgetItem() throws Exception {

        // given
        Order order = new Order();
        order.setAtPath("/FRA");
        OrderItem item1 = new OrderItem();
        order.getItems().add(item1);
        order.setOrderNumber("123");
        order.setType(IncomingInvoiceType.CAPEX);
        order.setBuyer(new Organisation());
        order.setSeller(new Organisation());
        item1.setNetAmount(new BigDecimal("100"));
        item1.setDescription("blah");
        item1.setGrossAmount(BigDecimal.ZERO);
        item1.setCharge(new Charge());
        item1.setStartDate(new LocalDate());
        item1.setEndDate(new LocalDate());
        item1.setOrdr(order);

        // when
        item1.setBudgetItem(new BudgetItem());
        String result = order.reasonIncomplete();

        // then
        assertThat(result).isEqualTo("property, (on item) when budget item filled in then property required");

    }

    @Test
    public void minimalRequiredDataToComplete_consitent_dimensions_for_project_budgetItem() throws Exception {

        // given
        Order order = new Order();
        order.setAtPath("/FRA");
        OrderItem item1 = new OrderItem();
        item1.setOrdr(order);
        order.getItems().add(item1);
        order.setOrderNumber("123");
        order.setType(IncomingInvoiceType.CAPEX);
        order.setProperty(new Property());
        order.setBuyer(new Organisation());
        order.setSeller(new Organisation());
        item1.setNetAmount(new BigDecimal("100"));
        item1.setDescription("blah");
        item1.setGrossAmount(BigDecimal.ZERO);
        item1.setCharge(new Charge());
        item1.setStartDate(new LocalDate());
        item1.setEndDate(new LocalDate());
        item1.setProperty(new Property());

        // when
        item1.setBudgetItem(new BudgetItem());
        item1.setProject(new Project());
        String result = order.reasonIncomplete();

        // then
        assertThat(result).isEqualTo("(on item) either project or budget item - not both required");

    }

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    OrderItemRepository mockOrderItemRepository;

    @Test
    public void splitItem_mixin_works() throws Exception {

        // given
        Order order = new Order();
        order.orderItemRepository = mockOrderItemRepository;

        String description = "some description";
        Tax tax = new Tax();
        Charge charge = new Charge();
        Property property = new Property();
        Project project = new Project();
        BudgetItem budgetItem = new BudgetItem();
        String period = "F2018";

        OrderItem itemToSplit = new OrderItem();

        BigDecimal newItemNetAmount = new BigDecimal("50.00");
        BigDecimal newItemVatAmount = new BigDecimal("10");
        BigDecimal newItemGrossAmount = new BigDecimal("60.00");

        // expect
        context.checking(new Expectations() {{
            oneOf(mockOrderItemRepository).upsert(
                    order,
                    charge,
                    description,
                    newItemNetAmount,
                    newItemVatAmount,
                    newItemGrossAmount,
                    tax,
                    PeriodUtil.yearFromPeriod(period).startDate(),
                    PeriodUtil.yearFromPeriod(period).endDate(),
                    property,
                    project,
                    budgetItem, 0);
        }});

        // when
        order.splitItem(itemToSplit, description, newItemNetAmount, newItemVatAmount, tax, newItemGrossAmount, charge, property, project, budgetItem, period);

    }

    @Mock
    ChargeRepository mockChargeRepository;

    @Test
    public void splitItem_charge_choices_type_capex() throws Exception {
        // given
        Charge charge1 = new Charge();
        charge1.setReference("FOO");
        Charge charge2 = new Charge();
        charge1.setReference("BAR");

        OrderItem orderItemToSplit = new OrderItem();
        orderItemToSplit.setCharge(charge1);

        Order order = new Order();
        order.setType(IncomingInvoiceType.CAPEX);
        order.chargeRepository = mockChargeRepository;
        order.getItems().add(orderItemToSplit);

        // expect
        context.checking(new Expectations() {{
            oneOf(mockChargeRepository).allIncoming();
            will(returnValue(Lists.newArrayList(charge1, charge2)));
        }});

        // when
        final List<Charge> chargeChoices = order.choices6SplitItem();

        // then
        assertThat(chargeChoices).containsExactly(charge2);
    }

    @Test
    public void splitItem_charge_choices_type_property_expenses() throws Exception {
        // given
        Charge charge1 = new Charge();
        charge1.setReference("FOO");
        Charge charge2 = new Charge();
        charge1.setReference("BAR");

        OrderItem orderItemToSplit = new OrderItem();
        orderItemToSplit.setCharge(charge1);

        Order order = new Order();
        order.setType(IncomingInvoiceType.PROPERTY_EXPENSES);
        order.chargeRepository = mockChargeRepository;
        order.getItems().add(orderItemToSplit);

        // expect
        context.checking(new Expectations() {{
            oneOf(mockChargeRepository).allIncoming();
            will(returnValue(Lists.newArrayList(charge1, charge2)));
        }});

        // when
        final List<Charge> chargeChoices = order.choices6SplitItem();

        // then
        assertThat(chargeChoices).containsExactly(charge1, charge2);
    }

    @Test
    public void mergeItem_mixin_works() throws Exception {

        // given
        Order order = new Order();
        order.orderItemRepository = mockOrderItemRepository;

        OrderItem sourceItem = new OrderItem();
        OrderItem targetItem = new OrderItem();

        // expect
        context.checking(new Expectations() {{
            oneOf(mockOrderItemRepository).mergeItems(
                    sourceItem, targetItem);
        }});

        // when
        order.mergeItems(sourceItem, targetItem);

    }

    @Test
    public void addItem_works_for_ita_when_no_items() throws Exception {

        // given
        Order order = new Order();
        order.orderItemRepository = mockOrderItemRepository;
        order.setAtPath("/ITA");
        Charge chargeForIta = new Charge();

        // expect
        int expectedNumber = 0;
        context.checking(new Expectations() {{
            oneOf(mockOrderItemRepository).upsert(order, chargeForIta, null, null, null, null, null, null, null, null, null, null, expectedNumber);
        }});

        // when
        order.addItem(chargeForIta, null, null, null, null, null, null, null, null, null);

    }

    @Test
    public void addItem_works_for_ita_when_item_with_same_charge() throws Exception {

        // given
        Order order = new Order();
        order.orderItemRepository = mockOrderItemRepository;
        order.setAtPath("/ITA");
        Charge chargeForIta = new Charge();

        OrderItem existingItem = new OrderItem();
        existingItem.setCharge(chargeForIta);
        order.getItems().add(existingItem);

        // expect
        int expectedNumber = 1;
        context.checking(new Expectations() {{
            oneOf(mockOrderItemRepository).upsert(order, chargeForIta, null, null, null, null, null, null, null, null, null, null, expectedNumber);
        }});

        // when
        order.addItem(chargeForIta, null, null, null, null, null, null, null, null, null);
    }

    @Test
    public void addItem_works_for_ita_when_item_with_other_charge_only_hypothetical() throws Exception {

        // given
        Order order = new Order();
        order.orderItemRepository = mockOrderItemRepository;
        order.setAtPath("/ITA");
        Charge chargeForIta = new Charge();
        Charge otherChargeForIta = new Charge();

        OrderItem existingItem = new OrderItem();
        existingItem.setCharge(otherChargeForIta);
        order.getItems().add(existingItem);

        // expect
        int expectedNumber = 0;
        context.checking(new Expectations() {{
            oneOf(mockOrderItemRepository).upsert(order, chargeForIta, null, null, null, null, null, null, null, null, null, null, expectedNumber);
        }});

        // when
        order.addItem(chargeForIta, null, null, null, null, null, null, null, null, null);
    }

    @Test
    public void addItem_works_for_fra_when_no_items() throws Exception {

        // given
        Order order = new Order();
        order.orderItemRepository = mockOrderItemRepository;
        order.setAtPath("/FRA");
        Charge chargeForFra = new Charge();

        // expect
        int expectedNumber = 0;
        context.checking(new Expectations() {{
            oneOf(mockOrderItemRepository).upsert(order, chargeForFra, null, null, null, null, null, null, null, null, null, null, expectedNumber);
        }});

        // when
        order.addItem(chargeForFra, null, null, null, null, null, null, null, null, null);

    }

    @Test
    public void addItem_works_for_fra_when_item_with_same_charge() throws Exception {

        // given
        Order order = new Order();
        order.orderItemRepository = mockOrderItemRepository;
        order.setAtPath("/FRA");
        Charge chargeForFra = new Charge();

        OrderItem existingItem = new OrderItem();
        existingItem.setCharge(chargeForFra);
        order.getItems().add(existingItem);

        // expect
        int expectedNumber = 0;
        context.checking(new Expectations() {{
            oneOf(mockOrderItemRepository).upsert(order, chargeForFra, null, null, null, null, null, null, null, null, null, null, expectedNumber);
        }});

        // when
        order.addItem(chargeForFra, null, null, null, null, null, null, null, null, null);
    }

    @Test
    public void updateOrderNumber_works() throws Exception {
        // given
        Order order = new Order();
        order.setAtPath("/ITA");
        order.setOrderNumber("1234/OXF/123/005");
        OrderRepository repository = new OrderRepository();
        order.orderRepository = repository;
        OrderItem firstItem = new OrderItem();
        order.getItems().add(firstItem);

        // when
        order.setBuyerOrderNumber(BigInteger.valueOf(1234));
        order.updateOrderNumber();

        // then
        assertThat(order.getOrderNumber()).isEqualTo("1234/OXF//");

        // and when
        Property property = new Property();
        property.setReference("LON");
        order.editProperty(property, true);

        // then
        assertThat(order.getOrderNumber()).isEqualTo("1234/LON//");

        // and when
        Project project = new Project();
        project.setReference("GBPR321");
        firstItem.setProject(project);
        order.updateOrderNumber();
        // then
        assertThat(order.getOrderNumber()).isEqualTo("1234/LON/321/");

        // and when
        Charge charge = new Charge();
        charge.setReference("GBWT006");
        firstItem.setCharge(charge);
        order.updateOrderNumber();
        // then
        assertThat(order.getOrderNumber()).isEqualTo("1234/LON/321/006");
    }

    @Test
    public void editProperty_french_order_does_not_update_order_number() {
        // given
        Order order = new Order();
        order.setAtPath("/FRA");
        order.setOrderNumber("1234");

        Property property = new Property();
        property.setReference("PAR");
        order.setProperty(property);

        // when
        Property newProperty = new Property();
        newProperty.setReference("CAN");
        order.editProperty(newProperty, true);

        // then
        assertThat(order.getOrderNumber()).isEqualTo("1234");
        assertThat(order.getProperty()).isEqualTo(newProperty);
    }

    @Mock
    OrderRepository mockOrderRepository;

    @Test
    public void double_order_check_works() throws Exception {

        // given
        Order newOrder = new Order();
        newOrder.orderRepository = mockOrderRepository;

        String sellerOrderReference = "123-456-7";
        Organisation seller = new Organisation();
        LocalDate orderDate = new LocalDate(2017,01,01);

        Order existingOrder = new Order();

        // expect
        context.checking(new Expectations(){{
            oneOf(mockOrderRepository).findBySellerOrderReferenceAndSellerAndOrderDate(sellerOrderReference, seller, orderDate);
            will(returnValue(newOrder));
            oneOf(mockOrderRepository).findBySellerOrderReferenceAndSeller(sellerOrderReference, seller);
            will(returnValue(Arrays.asList(newOrder, existingOrder)));
        }});

        // when
        newOrder.setSellerOrderReference(sellerOrderReference);
        newOrder.setSeller(seller);
        newOrder.setOrderDate(orderDate);
        String message = newOrder.doubleOrderCheck();

        // then
        Assertions.assertThat(message).contains("WARNING: Orders with the same seller order reference of this seller are found");

        // and expect
        context.checking(new Expectations(){{
            oneOf(mockOrderRepository).findBySellerOrderReferenceAndSellerAndOrderDate(sellerOrderReference, seller, orderDate);
            will(returnValue(existingOrder));
        }});

        // when
        message = newOrder.doubleOrderCheck();

        // then
        Assertions.assertThat(message).isEqualTo("WARNING: There is already an order with the same seller order reference and order date for this seller. Please check.");

    }
}