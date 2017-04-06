package org.estatio.app.services.order;

import java.util.List;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.isis.applib.annotation.Action;
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
public class OrderInvoiceSheet {

    @XmlElementWrapper
    @XmlElement(name = "line")
    @Getter @Setter
    List<OrderInvoiceLine> lines;

    @Action(semantics = SemanticsOf.SAFE)
    public Blob download() {
        return orderInvoiceImportService.createSheet(lines);
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public OrderInvoiceSheet apply() {

        for (OrderInvoiceLine line : lines) {
            factoryService.mixin(OrderInvoiceLine._apply.class, line).act();
        }

        return this;
    }

    @Inject
    OrderInvoiceImportService orderInvoiceImportService;
    @Inject
    FactoryService factoryService;

}

