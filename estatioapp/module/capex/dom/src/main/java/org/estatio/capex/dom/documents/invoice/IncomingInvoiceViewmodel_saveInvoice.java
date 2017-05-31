package org.estatio.capex.dom.documents.invoice;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

import org.estatio.capex.dom.EstatioCapexDomModule;
import org.estatio.capex.dom.documents.HasDocumentAbstract;
import org.estatio.capex.dom.documents.IncomingDocumentRepository;
import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationState;
import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransition;
import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransitionType;
import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.capex.dom.order.Order;
import org.estatio.capex.dom.order.OrderItem;
import org.estatio.capex.dom.order.OrderItemRepository;
import org.estatio.capex.dom.order.OrderRepository;
import org.estatio.capex.dom.orderinvoice.OrderItemInvoiceItemLinkRepository;
import org.estatio.capex.dom.triggers.DomainObject_triggerAbstract;
import org.estatio.capex.dom.util.PeriodUtil;
import org.estatio.dom.asset.Property;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.invoice.InvoiceStatus;

import lombok.Getter;

@Mixin(method = "act")
public class IncomingInvoiceViewmodel_saveInvoice extends DomainObject_triggerAbstract<
        Document,
        IncomingDocumentCategorisationStateTransition,
        IncomingDocumentCategorisationStateTransitionType,
        IncomingDocumentCategorisationState
        > {

    @Getter
    private final IncomingInvoiceViewModel viewmodel;

    public IncomingInvoiceViewmodel_saveInvoice(final IncomingInvoiceViewModel viewModel) {
        super(viewModel.getDocument(), IncomingDocumentCategorisationStateTransitionType.CLASSIFY_AS_INVOICE_OR_ORDER);
        this.viewmodel = viewModel;
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
        IncomingInvoice incomingInvoice = doCreate();

        triggerStateTransition(comment);

        this.viewmodel.incomingInvoice = incomingInvoice;
        return goToNext && nextDocument()!=null ? factory.createFor(nextDocument()) : incomingInvoice;
    }

    public boolean default1Act(){
        return true;
    }

    public boolean hideAct() {
        return cannotTriggerStateTransition();
    }

    public String disableAct(){
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
