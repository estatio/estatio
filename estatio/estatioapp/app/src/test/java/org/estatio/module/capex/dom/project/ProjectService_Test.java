package org.estatio.module.capex.dom.project;

import java.util.Arrays;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.capex.dom.order.OrderItem;
import org.estatio.module.capex.dom.orderinvoice.OrderItemInvoiceItemLink;
import org.estatio.module.capex.dom.orderinvoice.OrderItemInvoiceItemLinkRepository;

public class ProjectService_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    OrderItemInvoiceItemLinkRepository mockOrderItemInvoiceItemLinkRepository;

    @Test
    public void moveIncomingInvoiceItemToProject_works_when_invoice_item_not_linked() throws Exception {

        // given
        ProjectService projectService = new ProjectService();
        projectService.orderItemInvoiceItemLinkRepository = mockOrderItemInvoiceItemLinkRepository;

        IncomingInvoiceItem incomingInvoiceItem = new IncomingInvoiceItem();
        Project project = new Project();

        // expect
        context.checking(new Expectations(){{
            oneOf(mockOrderItemInvoiceItemLinkRepository).findByInvoiceItem(incomingInvoiceItem);
            will(returnValue(Optional.empty()));

        }});

        // when
        projectService.moveIncomingInvoiceItemToProject(incomingInvoiceItem, project);

        // then
        Assertions.assertThat(incomingInvoiceItem.getProject()).isEqualTo(project);

    }

    @Test
    public void moveIncomingInvoiceItemToProject_works_when_invoice_item_linked_to_order_item() throws Exception {

        // given
        ProjectService projectService = new ProjectService();
        projectService.orderItemInvoiceItemLinkRepository = mockOrderItemInvoiceItemLinkRepository;

        Project project = new Project();

        OrderItem orderItem = new OrderItem();
        IncomingInvoiceItem incomingInvoiceItem = new IncomingInvoiceItem();
        IncomingInvoiceItem otherLinkedItem = new IncomingInvoiceItem();
        OrderItemInvoiceItemLink link1 = new OrderItemInvoiceItemLink();
        link1.setInvoiceItem(incomingInvoiceItem);
        link1.setOrderItem(orderItem);
        OrderItemInvoiceItemLink link2 = new OrderItemInvoiceItemLink();
        link2.setInvoiceItem(otherLinkedItem);
        link2.setOrderItem(orderItem);


        // expect
        context.checking(new Expectations(){{
            oneOf(mockOrderItemInvoiceItemLinkRepository).findByInvoiceItem(incomingInvoiceItem);
            will(returnValue(Optional.of(link1)));
            oneOf(mockOrderItemInvoiceItemLinkRepository).findByOrderItem(orderItem);
            will(returnValue(Arrays.asList(link1, link2)));
        }});

        // when
        projectService.moveIncomingInvoiceItemToProject(incomingInvoiceItem, project);

        // then
        Assertions.assertThat(incomingInvoiceItem.getProject()).isEqualTo(project);
        Assertions.assertThat(otherLinkedItem.getProject()).isEqualTo(project);
        Assertions.assertThat(orderItem.getProject()).isEqualTo(project);

    }

    @Test
    public void moveOrderItemToProject_works_when_no_invoice_items_linked() throws Exception {

        // given
        ProjectService projectService = new ProjectService();
        projectService.orderItemInvoiceItemLinkRepository = mockOrderItemInvoiceItemLinkRepository;

        Project project = new Project();

        OrderItem orderItem = new OrderItem();
        IncomingInvoiceItem incomingInvoiceItem = new IncomingInvoiceItem();
        OrderItemInvoiceItemLink link = new OrderItemInvoiceItemLink();
        link.setInvoiceItem(incomingInvoiceItem);
        link.setOrderItem(orderItem);

        // expect
        context.checking(new Expectations(){{
            oneOf(mockOrderItemInvoiceItemLinkRepository).findByOrderItem(orderItem);
            will(returnValue(Arrays.asList(link)));
        }});

        // when
        projectService.moveOrderItemToProject(orderItem, project);

        // then
        Assertions.assertThat(orderItem.getProject()).isEqualTo(project);
        Assertions.assertThat(incomingInvoiceItem.getProject()).isEqualTo(project);

    }

    @Test
    public void moveOrderItemToProject_works_when_having_invoice_items_linked() throws Exception {

        // given
        ProjectService projectService = new ProjectService();
        projectService.orderItemInvoiceItemLinkRepository = mockOrderItemInvoiceItemLinkRepository;

        Project project = new Project();

        OrderItem orderItem = new OrderItem();


        // expect
        context.checking(new Expectations(){{
            oneOf(mockOrderItemInvoiceItemLinkRepository).findByOrderItem(orderItem);
            will(returnValue(Arrays.asList()));
        }});

        // when
        projectService.moveOrderItemToProject(orderItem, project);

        // then
        Assertions.assertThat(orderItem.getProject()).isEqualTo(project);

    }

}