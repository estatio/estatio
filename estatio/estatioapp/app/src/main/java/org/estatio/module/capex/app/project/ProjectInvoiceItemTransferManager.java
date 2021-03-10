package org.estatio.module.capex.app.project;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Collection;
import org.apache.isis.applib.annotation.DomainObject;

import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItemRepository;
import org.estatio.module.capex.dom.orderinvoice.OrderItemInvoiceItemLinkRepository;
import org.estatio.module.capex.dom.project.Project;

import lombok.Getter;

@DomainObject(objectType = "org.estatio.module.capex.app.project.ProjectInvoiceItemTransferManager")
@XmlRootElement
@XmlType(
        propOrder = {
                "target",
                "source"
        }
)
@XmlAccessorType(XmlAccessType.FIELD)
public class ProjectInvoiceItemTransferManager {

    public ProjectInvoiceItemTransferManager() {}

    public ProjectInvoiceItemTransferManager(final Project target, final Project source) {
        this.target = target;
        this.source = source;
    }

    public String title(){
        return "Move invoice items (unlinked to order)";
    }

    @Getter
    private Project target;

    @Getter
    private Project source;

    @Collection
    public List<IncomingInvoiceItem> getInvoiceItemsHavingNoOrder(){
        return incomingInvoiceItemRepository.findByProject(getSource())
                .stream()
                .filter(x->!orderItemInvoiceItemLinkRepository.findByInvoiceItem(x).isPresent())
                .collect(Collectors.toList());
    }

    @Action(associateWith = "invoiceItemsHavingNoOrder", associateWithSequence = "1" )
    public ProjectInvoiceItemTransferManager move(final List<IncomingInvoiceItem> items){
        for (IncomingInvoiceItem invoiceItem : items){
            if (!orderItemInvoiceItemLinkRepository.findByInvoiceItem(invoiceItem).isPresent()){
                invoiceItem.setProject(getTarget());
            }
        }
        return new ProjectInvoiceItemTransferManager(getTarget(), getSource());
    }

    public List<IncomingInvoiceItem> choices0Move(){
        return getInvoiceItemsHavingNoOrder();
    }

    @Inject
    @XmlTransient
    OrderItemInvoiceItemLinkRepository orderItemInvoiceItemLinkRepository;

    @Inject
    @XmlTransient
    IncomingInvoiceItemRepository incomingInvoiceItemRepository;


}
