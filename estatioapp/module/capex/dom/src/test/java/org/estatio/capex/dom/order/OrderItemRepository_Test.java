package org.estatio.capex.dom.order;

import java.math.BigDecimal;

import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.capex.dom.project.Project;
import org.estatio.dom.asset.Property;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.tax.Tax;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderItemRepository_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    private OrderItem orderItem = new OrderItem();

    @Mock
    private Order mockOrder;

    @Mock
    private Charge mockCharge;

    @Test
    public void upsert_works() throws Exception {

        // given
        OrderItemRepository orderItemRepository = new OrderItemRepository(){
            @Override
            public OrderItem findByOrderAndCharge(final Order order, final Charge charge) {
                return orderItem;
            }
        };
        String description = new String("some description");
        BigDecimal netAmount = BigDecimal.ZERO;
        BigDecimal vatAmount = BigDecimal.ONE;
        BigDecimal grossAmount = BigDecimal.TEN;
        Tax tax = new Tax();
        LocalDate startDate = new LocalDate(2017,1,1);
        LocalDate endDate = new LocalDate(2017,1,2);
        Property property = new Property();
        Project project = new Project();
        BudgetItem budgetItem = new BudgetItem();

        assertThat(orderItem.getOrdr()).isNull();
        assertThat(orderItem.getCharge()).isNull();

        // when
        orderItemRepository.upsert(
                mockOrder,
                mockCharge,
                description,
                netAmount,
                vatAmount,
                grossAmount,
                tax,
                startDate,
                endDate,
                property,
                project,
                budgetItem
                );

        // then
        assertThat(orderItem.getOrdr()).isNull();
        assertThat(orderItem.getCharge()).isNull();
        assertThat(orderItem.getDescription()).isEqualTo(description);
        assertThat(orderItem.getNetAmount()).isEqualTo(netAmount);
        assertThat(orderItem.getVatAmount()).isEqualTo(vatAmount);
        assertThat(orderItem.getGrossAmount()).isEqualTo(grossAmount);
        assertThat(orderItem.getTax()).isEqualTo(tax);
        assertThat(orderItem.getStartDate()).isEqualTo(startDate);
        assertThat(orderItem.getEndDate()).isEqualTo(endDate);
        assertThat(orderItem.getProperty()).isEqualTo(property);
        assertThat(orderItem.getProject()).isEqualTo(project);
        assertThat(orderItem.getBudgetItem()).isEqualTo(budgetItem);
    }

}