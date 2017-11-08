package org.estatio.module.capex.imports;

import java.util.List;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.value.Blob;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name = "orderInvoiceSheet")
@XmlType(
        propOrder = {
                "lines"
        }
)
@DomainObject(
        objectType = "orders.OrderInvoiceSheet"
)
@XmlAccessorType(XmlAccessType.FIELD)
public class OrderInvoiceSheet {

    public String title(){
        return "Order and invoice import";
    }

    @XmlElementWrapper
    @XmlElement(name = "line")
    @Getter @Setter
    List<OrderInvoiceLine> lines;

    @Action(semantics = SemanticsOf.SAFE)
    public Blob download() {
        return orderInvoiceImportService.createSheet(lines);
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT, publishing = Publishing.DISABLED)
    public OrderInvoiceSheet apply() {

        for (OrderInvoiceLine line : lines) {
            OrderInvoiceLine._apply applyMixin = factoryService.mixin(OrderInvoiceLine._apply.class, line);
            if(applyMixin.disableAct() == null) {
                applyMixin.act();
            }
        }

        return this;
    }

    @XmlTransient
    @Inject
    OrderInvoiceImportService orderInvoiceImportService;

    @XmlTransient
    @Inject
    FactoryService factoryService;

}

