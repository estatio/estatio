package org.estatio.module.capex.dom.order;

import java.math.BigDecimal;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.budget.dom.budgetitem.BudgetItem;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.capex.dom.util.PeriodUtil;
import org.estatio.module.charge.dom.Charge;
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
    public void editOrderNumber_happyCase() throws Exception {
        // given
        final Order order = new Order();
        order.setOrderNumber("0001/CUR/001/001");

        // when
        final String validation = order.validateEditOrderNumber("0001/GEN/002/002");

        // then
        assertThat(validation).isNull();
    }

    @Test
    public void editOrderNumber_sadCase_incorrect_amount_separators() throws Exception {
        // given
        final Order order = new Order();
        order.setOrderNumber("0001/CUR/001/001");

        // when
        final String validation = order.validateEditOrderNumber("0001/GEN/002.002");

        // then
        assertThat(validation).isEqualTo("Order number format incorrect; should be aaaa/bbb/ccc/ddd");
    }

    @Test
    public void editOrderNumber_sadCase_numerator_value_changed() throws Exception {
        // given
        final Order order = new Order();
        order.setOrderNumber("0001/CUR/001/001");

        // when
        final String validation = order.validateEditOrderNumber("0002/CUR/001/001");

        // then
        assertThat(validation).isEqualTo("First element of order number (0001) can not be changed");
    }

    @Ignore("For ECP-866, when it is implemented")
    @Test
    public void orderNumberChanges_onEditProperty() throws Exception {
        // given
        final Project project = new Project();
        project.setReference("001");

        final Charge charge = new Charge();
        charge.setReference("001");

        final Property oldProperty = new Property();
        oldProperty.setReference("CUR");
        final Property newProperty = new Property();
        newProperty.setReference("COL");

        final Order order = new Order();
        order.setAtPath("/ITA");
        order.setOrderNumber("0001/CUR/001/001");

        final OrderItem orderItem = new OrderItem();
        orderItem.setCharge(charge);
        orderItem.setProject(project);
        orderItem.setProperty(oldProperty);

        order.getItems().add(orderItem);

        // when
        order.editProperty(newProperty, true);

        // then
        assertThat(order.getOrderNumber()).isEqualTo("0001/COL/001/001");
    }
}