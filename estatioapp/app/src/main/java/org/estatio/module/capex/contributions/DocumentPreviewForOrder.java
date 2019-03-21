package org.estatio.module.capex.contributions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;

import org.incode.module.document.dom.mixins.DocumentPreview;

import org.estatio.module.capex.dom.order.Order;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@DomainObject(
        objectType = "org.estatio.module.capex.contributions.DocumentPreviewForOrder",
        editing = Editing.DISABLED
)
@XmlRootElement(name = "documentPreviewForOrder")
@XmlType(
        propOrder = {
        }
)
@XmlAccessorType(XmlAccessType.FIELD)
@NoArgsConstructor
public class DocumentPreviewForOrder extends DocumentPreview<Order> {

    @Getter @Setter
    private Order domainObject;


}
