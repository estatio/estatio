package org.estatio.capex.dom.documents.invoice;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.capex.dom.EstatioCapexDomModule;
import org.estatio.capex.dom.documents.incoming.IncomingOrderOrInvoiceViewmodel_saveAbstract;
import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.capex.dom.order.Order;
import org.estatio.capex.dom.order.OrderItem;
import org.estatio.capex.dom.order.OrderItemRepository;
import org.estatio.capex.dom.orderinvoice.OrderItemInvoiceItemLinkRepository;
import org.estatio.capex.dom.util.PeriodUtil;
import org.estatio.dom.asset.Property;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.invoice.InvoiceStatus;

@Mixin(method = "act")
public class IncomingInvoiceViewmodel_saveInvoice
        extends IncomingOrderOrInvoiceViewmodel_saveAbstract<IncomingInvoice, IncomingInvoiceViewModel> {

    // workaround for ISIS-1628
    private final IncomingInvoiceViewModel viewModel;

    public IncomingInvoiceViewmodel_saveInvoice(final IncomingInvoiceViewModel viewModel) {
        super(viewModel);
        this.viewModel = viewModel;
    }

    public static class DomainEvent
            extends EstatioCapexDomModule.ActionDomainEvent<IncomingInvoiceViewmodel_saveInvoice> {}

    @Action(
            semantics = SemanticsOf.IDEMPOTENT,
            domainEvent = DomainEvent.class
    )
    public Object act(
            @Nullable final String comment,
            final boolean goToNext){
        return super.act(comment, goToNext);
    }

    protected IncomingInvoice doCreate(){
        // create invoice
        IncomingInvoice incomingInvoice = incomingInvoiceRepository.findOrCreate(
                getViewmodel().getInvoiceNumber(),
                getViewmodel().getBuyer().getAtPath(),
                getViewmodel().getBuyer(),
                getViewmodel().getSeller(),
                getViewmodel().getInvoiceDate(),
                getViewmodel().getDueDate(),
                getViewmodel().getPaymentMethod(),
                InvoiceStatus.NEW,
                getViewmodel().getDateReceived(),
                getViewmodel().getBankAccount()
        );
        // create invoice item
        incomingInvoice.addItem(
                getViewmodel().getCharge(),
                getViewmodel().getDescription(),
                getViewmodel().getNetAmount(),
                getViewmodel().getVatAmount(),
                getViewmodel().getGrossAmount(),
                getViewmodel().getTax(),
                getViewmodel().getDueDate(),
                getViewmodel().getPeriod()!= null ? PeriodUtil.yearFromPeriod(getViewmodel().getPeriod()).startDate() : null,
                getViewmodel().getPeriod()!= null ? PeriodUtil.yearFromPeriod(getViewmodel().getPeriod()).endDate() : null,
                (Property) getViewmodel().getFixedAsset(),
                getViewmodel().getProject(),
                getViewmodel().getBudgetItem());
        // attach document
        paperclipRepository.attach(getViewmodel().getDocument(),null,incomingInvoice);
        // link to orderItem if applicable
        if (getViewmodel().getOrderItem()!=null){
            Order order = getViewmodel().getOrderItem().getOrdr();
            Charge chargeFromWrapper = getViewmodel().getOrderItem().getCharge();
            OrderItem orderItemToLink = orderItemRepository.findByOrderAndCharge(order, chargeFromWrapper);
            IncomingInvoiceItem invoiceItemToLink = (IncomingInvoiceItem) incomingInvoice.getItems().first();
            orderItemInvoiceItemLinkRepository.findOrCreateLink(orderItemToLink, invoiceItemToLink);
        }
        return incomingInvoice;
    }

    @Inject
    private IncomingInvoiceRepository incomingInvoiceRepository;

    @Inject
    private OrderItemRepository orderItemRepository;

    @Inject
    private OrderItemInvoiceItemLinkRepository orderItemInvoiceItemLinkRepository;

}
