package org.estatio.capex.dom.documents.invoice;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

import org.estatio.capex.dom.EstatioCapexDomModule;
import org.estatio.capex.dom.documents.HasDocumentAbstract;
import org.estatio.capex.dom.documents.IncomingDocumentRepository;
import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.capex.dom.order.Order;
import org.estatio.capex.dom.order.OrderItem;
import org.estatio.capex.dom.order.OrderItemRepository;
import org.estatio.capex.dom.order.OrderRepository;
import org.estatio.capex.dom.orderinvoice.OrderItemInvoiceItemLinkRepository;
import org.estatio.capex.dom.util.PeriodUtil;
import org.estatio.dom.asset.Property;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.invoice.InvoiceStatus;

import lombok.Getter;

@Mixin
public class IncomingInvoiceViewmodel_createInvoice {

    @Getter
    private final IncomingInvoiceViewModel viewmodel;

    public IncomingInvoiceViewmodel_createInvoice(final IncomingInvoiceViewModel viewModel) {
        this.viewmodel = viewModel;
    }

    public static class ActionDomainEvent
            extends EstatioCapexDomModule.ActionDomainEvent<IncomingInvoiceViewmodel_createInvoice> {}

    @Action(
            semantics = SemanticsOf.IDEMPOTENT,
            domainEvent = ActionDomainEvent.class
    )
    public Object createInvoice(final boolean goToNext){
        IncomingInvoice incomingInvoice = doCreate();
        // make the newly created invoice available to any subscribers of this action's domain event.
        this.viewmodel.incomingInvoice = incomingInvoice;
        return goToNext && nextDocument()!=null ? factory.map(nextDocument()) : incomingInvoice;
    }

    public boolean default0CreateInvoice(){
        return true;
    }

    public String disableCreateInvoice(){
        return getViewmodel().minimalRequiredDataToComplete();
    }

    private IncomingInvoice doCreate(){
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
                getViewmodel().getNotCorrect()
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
                getViewmodel().getProject()
        );
        // attach document
        paperclipRepository.attach(getViewmodel().getDocument(),null,incomingInvoice);
        // link to orderItem if applicable
        if (getViewmodel().getOrderItem()!=null){
            Order orderFromWrapper = orderRepository.findByOrderNumber(getViewmodel().getOrderItem().getOrderNumber());
            Charge chargeFromWrapper = getViewmodel().getOrderItem().getCharge();
            OrderItem orderItemToLink = orderItemRepository.findByOrderAndCharge(orderFromWrapper, chargeFromWrapper);
            IncomingInvoiceItem invoiceItemToLink = (IncomingInvoiceItem) incomingInvoice.getItems().first();
            orderItemInvoiceItemLinkRepository.findOrCreateLink(orderItemToLink, invoiceItemToLink);
        }
        return incomingInvoice;
    }

    private Document nextDocument(){
        List<Document> incomingDocuments = incomingDocumentRepository.findUnclassifiedIncomingInvoices();
        return incomingDocuments.size() > 0 ? incomingDocuments.get(0) : null;
    }

    @Inject
    private IncomingInvoiceRepository incomingInvoiceRepository;

    @Inject
    private IncomingDocumentRepository incomingDocumentRepository;

    @Inject
    private HasDocumentAbstract.Factory factory;

    @Inject
    private PaperclipRepository paperclipRepository;

    @Inject
    private OrderRepository orderRepository;

    @Inject
    private OrderItemRepository orderItemRepository;

    @Inject
    private OrderItemInvoiceItemLinkRepository orderItemInvoiceItemLinkRepository;

}
