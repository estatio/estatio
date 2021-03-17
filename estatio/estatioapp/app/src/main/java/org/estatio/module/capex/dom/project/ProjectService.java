package org.estatio.module.capex.dom.project;

import java.util.Optional;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.capex.dom.order.OrderItem;
import org.estatio.module.capex.dom.orderinvoice.OrderItemInvoiceItemLink;
import org.estatio.module.capex.dom.orderinvoice.OrderItemInvoiceItemLinkRepository;

@DomainService(nature = NatureOfService.DOMAIN)
public class ProjectService {

    public void moveIncomingInvoiceItemToProject(final IncomingInvoiceItem incomingInvoiceItem, final Project project){

        Optional linkToOrderItemIfAny = orderItemInvoiceItemLinkRepository.findByInvoiceItem(incomingInvoiceItem);
        if (linkToOrderItemIfAny.isPresent()){
            OrderItemInvoiceItemLink link = (OrderItemInvoiceItemLink) linkToOrderItemIfAny.get();
            OrderItem linkedOrderItem = link.getOrderItem();
            for (OrderItemInvoiceItemLink itemLinked: orderItemInvoiceItemLinkRepository.findByOrderItem(linkedOrderItem)){
                itemLinked.getInvoiceItem().setProjectByPassingInvalidateApproval(project);
            }
            linkedOrderItem.setProject(project);
        } else {
            incomingInvoiceItem.setProjectByPassingInvalidateApproval(project);
        }

    }

    public void moveOrderItemToProject(final OrderItem orderItem, final Project project){

        for (OrderItemInvoiceItemLink link : orderItemInvoiceItemLinkRepository.findByOrderItem(orderItem)){
            link.getInvoiceItem().setProjectByPassingInvalidateApproval(project);
        }
        orderItem.setProject(project);

    }

    @Inject
    OrderItemInvoiceItemLinkRepository orderItemInvoiceItemLinkRepository;

}
