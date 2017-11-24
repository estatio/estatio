package org.estatio.module.capex.dom.orderinvoice;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Uniques;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.title.TitleService;

import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.capex.dom.order.OrderItem;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.asset.dom.Property;
import org.estatio.module.budget.dom.budgetitem.BudgetItem;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.invoice.dom.Invoice;
import org.estatio.module.tax.dom.Tax;
import org.estatio.module.tax.dom.TaxRate;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "dbo",
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
                        "FROM org.estatio.module.capex.dom.orderinvoice.OrderItemInvoiceItemLink " +
                        "WHERE orderItem == :orderItem && "
                        + "invoiceItem == :invoiceItem"),
        @Query(
                name = "findByOrder", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.module.capex.dom.orderinvoice.OrderItemInvoiceItemLink " +
                        "WHERE orderItem.ordr == :order"),
        @Query(
                name = "findByOrderItem", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.module.capex.dom.orderinvoice.OrderItemInvoiceItemLink " +
                        "WHERE orderItem == :orderItem"),
        @Query(
                name = "findByInvoice", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.module.capex.dom.orderinvoice.OrderItemInvoiceItemLink " +
                        "WHERE invoiceItem.invoice == :invoice"),
        @Query(
                name = "findByInvoiceItem", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.module.capex.dom.orderinvoice.OrderItemInvoiceItemLink " +
                        "WHERE invoiceItem == :invoiceItem")
})
@Uniques({
    @Unique(name = "OrderItemInvoiceItemLink_UNQ", members = { "orderItem", "invoiceItem" }), // legacy
    @Unique(name = "OrderItemInvoiceItemLink_invoiceItem_UNQ", members = { "invoiceItem" }) // can only link to one order item
})
@DomainObject(
        objectType = "org.estatio.capex.dom.orderinvoice.OrderItemInvoiceItemLink"
)
public class OrderItemInvoiceItemLink {


    public String title() {
        return titleService.titleOf(getOrderItem()) + " --[ " + getNetAmount() + " ]-- " + titleService.titleOf(getInvoiceItem());
    }

    @Getter @Setter
    @Column(allowsNull = "false", name = "orderItemId")
    private OrderItem orderItem;
    @Getter @Setter
    @Column(allowsNull = "false", name = "invoiceItemId")
    private IncomingInvoiceItem invoiceItem;



    /**
     * One of "reported", "reported-reversal" or "reversal"
     */
    public String cssClass() {
        return getInvoiceItem().cssClass();
    }


    // both order item and invoice item will have the same value for these dimensions
    public Charge getCharge() {
        return orderItem.getCharge();
    }
    public Property getProperty() {
        return orderItem.getProperty();
    }
    public Project getProject() {
        return orderItem.getProject();
    }
    public BudgetItem getBudgetItem() {
        return orderItem.getBudgetItem();
    }


    @javax.jdo.annotations.Column(scale = 2, allowsNull = "false")
    @org.apache.isis.applib.annotation.Property(editing = Editing.ENABLED)
    @Getter @Setter
    private BigDecimal netAmount;

    public String validateNetAmount(final BigDecimal proposedNetAmount) {
        if(proposedNetAmount == null) return null;
        final BigDecimal netAmountLinked = repository.calculateNetAmountLinkedFromInvoiceItem(getInvoiceItem());
        final BigDecimal netAmountInvoice = invoiceItem.getNetAmount();
        final BigDecimal netAmountNotLinked = netAmountInvoice.subtract(netAmountLinked);
        final BigDecimal netAmountNotLinkedExcludingThis = netAmountNotLinked.add(getNetAmount());
        return proposedNetAmount.compareTo(netAmountNotLinkedExcludingThis) > 0
                ? "Cannot exceed remaining amount to be linked (" + netAmountNotLinkedExcludingThis + ")"
                : null;
    }


    // derived from the order item
    public Order getOrder() {
        return orderItem.getOrdr();
    }
    public String getOrderItemDescription() {
        return orderItem.getDescription();
    }
    public LocalDate getOrderItemStartDate() {
        return orderItem.getStartDate();
    }
    public LocalDate getOrderItemEndDate() {
        return orderItem.getEndDate();
    }
    public BigDecimal getOrderItemNetAmount() {
        return orderItem.getNetAmount();
    }
    public BigDecimal getOrderItemVatAmount() {
        return orderItem.getVatAmount();
    }
    public BigDecimal getOrderItemGrossAmount() {
        return orderItem.getGrossAmount();
    }
    public Tax getOrderItemTax() {
        return orderItem.getTax();
    }


    // derived from the invoice item
    public Invoice getInvoice() {
        return invoiceItem.getInvoice();
    }

    public IncomingInvoiceType getIncomingInvoiceType() {
        return invoiceItem.getIncomingInvoiceType();
    }

    public String getInvoiceItemDescription() {
        return invoiceItem.getDescription();
    }

    public BigInteger getInvoiceItemSequence() {
        return invoiceItem.getSequence();
    }

    public LocalDate getInvoiceItemDueDate() {
        return invoiceItem.getDueDate();
    }

    @org.apache.isis.applib.annotation.Property(hidden = Where.PARENTED_TABLES)
    public LocalDate getInvoiceItemStartDate() {
        return invoiceItem.getStartDate();
    }

    @org.apache.isis.applib.annotation.Property(hidden = Where.PARENTED_TABLES)
    public LocalDate getInvoiceItemEndDate() {
        return invoiceItem.getEndDate();
    }

    public BigDecimal getInvoiceItemNetAmount() {
        return invoiceItem.getNetAmount();
    }

    public BigDecimal getInvoiceItemVatAmount() {
        return invoiceItem.getVatAmount();
    }

    public BigDecimal getInvoiceItemVatPercentage() {
        return invoiceItem.getVatPercentage();
    }

    public BigDecimal getInvoiceItemGrossAmount() {
        return invoiceItem.getGrossAmount();
    }

    public Tax getInvoiceItemTax() {
        return invoiceItem.getTax();
    }

    public TaxRate getInvoiceItemTaxRate() {
        return invoiceItem.getTaxRate();
    }



    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public IncomingInvoiceItem remove() {
        final IncomingInvoiceItem invoiceItem = getInvoiceItem();
        repository.removeLink(this);
        return invoiceItem;
    }



    @Inject
    TitleService titleService;

    @Inject
    OrderItemInvoiceItemLinkRepository repository;


}
