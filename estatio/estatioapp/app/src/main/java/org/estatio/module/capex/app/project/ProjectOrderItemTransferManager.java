package org.estatio.module.capex.app.project;

import java.util.List;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Collection;
import org.apache.isis.applib.annotation.DomainObject;

import org.estatio.module.capex.dom.order.OrderItem;
import org.estatio.module.capex.dom.order.OrderItemRepository;
import org.estatio.module.capex.dom.orderinvoice.OrderItemInvoiceItemLink;
import org.estatio.module.capex.dom.orderinvoice.OrderItemInvoiceItemLinkRepository;
import org.estatio.module.capex.dom.project.Project;

import lombok.Getter;

@DomainObject(objectType = "org.estatio.module.capex.app.project.ProjectOrderItemTransferManager")
@XmlRootElement
@XmlType(
        propOrder = {
                "target",
                "source"
        }
)
@XmlAccessorType(XmlAccessType.FIELD)
public class ProjectOrderItemTransferManager {

    public ProjectOrderItemTransferManager() {}

    public ProjectOrderItemTransferManager(final Project target, final Project source) {
        this.target = target;
        this.source = source;
    }

    public String title(){
        return "Move order items (including linked invoice items)";
    }

    @Getter
    private Project target;

    @Getter
    private Project source;

    @Collection
    public List<OrderItem> getOrderItems(){
        return orderItemRepository.findByProject(getSource());
    }

    @Action(associateWith = "orderItems", associateWithSequence = "1" )
    public ProjectOrderItemTransferManager move(final List<OrderItem> items){
        for (OrderItem orderItem : items){
            for (OrderItemInvoiceItemLink link : orderItemInvoiceItemLinkRepository.findByOrderItem(orderItem)){
                link.getInvoiceItem().setProject(getTarget());
            }
            orderItem.setProject(getTarget());
        }
        return new ProjectOrderItemTransferManager(getTarget(), getSource());
    }

    public List<OrderItem> choices0Move(){
        return getOrderItems();
    }

    @Inject
    @XmlTransient
    OrderItemInvoiceItemLinkRepository orderItemInvoiceItemLinkRepository;

    @Inject
    @XmlTransient
    OrderItemRepository orderItemRepository;


}
