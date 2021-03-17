package org.estatio.module.capex.app.project;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItemRepository;
import org.estatio.module.capex.dom.orderinvoice.OrderItemInvoiceItemLink;
import org.estatio.module.capex.dom.orderinvoice.OrderItemInvoiceItemLinkRepository;
import org.estatio.module.capex.dom.project.Project;

public class ProjectInvoiceItemTransferManager_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    IncomingInvoiceItemRepository mockIncomingInvoiceItemRepository;

    @Mock
    OrderItemInvoiceItemLinkRepository mockOrderItemInvoiceItemLinkRepository;

    @Test
    public void getInvoiceItemsHavingNoOrder_works() throws Exception {

        // given
        Project source = new Project();
        ProjectInvoiceItemTransferManager manager = new ProjectInvoiceItemTransferManager(null, source);
        manager.incomingInvoiceItemRepository = mockIncomingInvoiceItemRepository;
        manager.orderItemInvoiceItemLinkRepository = mockOrderItemInvoiceItemLinkRepository;
        IncomingInvoiceItem itemLinked = new IncomingInvoiceItem();
        IncomingInvoiceItem itemNotLinked = new IncomingInvoiceItem();
        OrderItemInvoiceItemLink link = new OrderItemInvoiceItemLink();

        // expect
        context.checking(new Expectations(){{
            oneOf(mockIncomingInvoiceItemRepository).findByProject(source);
            will(returnValue(Arrays.asList(itemLinked, itemNotLinked)));
            oneOf(mockOrderItemInvoiceItemLinkRepository).findByInvoiceItem(itemLinked);
            will(returnValue(Optional.of(link)));
            oneOf(mockOrderItemInvoiceItemLinkRepository).findByInvoiceItem(itemNotLinked);
            will(returnValue(Optional.empty()));
        }});

        // when
        List<IncomingInvoiceItem> items = manager.getInvoiceItemsHavingNoOrder();

        // then
        Assertions.assertThat(items).contains(itemNotLinked);
        Assertions.assertThat(items).doesNotContain(itemLinked);

    }

}