package org.estatio.capex.dom.orderinvoice;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.DomainObject;

import org.estatio.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.capex.dom.order.OrderItem;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "capex",
        table = "OrderItemInvoiceItemLink"
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Queries({
        @Query(
                name = "findUnique", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.capex.dom.orderinvoice.OrderItemInvoiceItemLink " +
                        "WHERE orderItem == :orderItem && "
                        + "invoiceItem == :invoiceItem"),
        @Query(
                name = "findByOrderItem", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.capex.dom.orderinvoice.OrderItemInvoiceItemLink " +
                        "WHERE orderItem == :orderItem"),
        @Query(
                name = "findByInvoiceItem", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.capex.dom.orderinvoice.OrderItemInvoiceItemLink " +
                        "WHERE invoiceItem == :invoiceItem")
})
@Unique(name = "OrderItemInvoiceItemLink_UNQ", members = { "orderItem", "invoiceItem" })

@DomainObject(
        objectType = "org.estatio.capex.dom.orderinvoice.OrderItemInvoiceItemLink"
)
public class OrderItemInvoiceItemLink {

    @Getter @Setter
    @Column(allowsNull = "false", name = "orderItemId")
    private OrderItem orderItem;
    @Getter @Setter
    @Column(allowsNull = "false", name = "invoiceItemId")
    private IncomingInvoiceItem invoiceItem;

}
