package org.estatio.capex.dom.documents.categorisation.invoice;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.EstatioCapexDomModule;
import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransitionType;
import org.estatio.capex.dom.documents.categorisation.document.IncomingDocViewModel_saveAbstract;
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
import org.estatio.dom.invoice.DocumentTypeData;
import org.estatio.dom.invoice.InvoiceStatus;

@Mixin(method = "act")
public class IncomingDocAsInvoiceViewmodel_saveInvoice
        extends IncomingDocViewModel_saveAbstract<IncomingInvoice, IncomingDocAsInvoiceViewModel> {

    // workaround for ISIS-1628
    private final IncomingDocAsInvoiceViewModel viewModel;

    public IncomingDocAsInvoiceViewmodel_saveInvoice(final IncomingDocAsInvoiceViewModel viewModel) {
        super(viewModel, inferTransitionTypeFrom(viewModel));
        this.viewModel = viewModel;
    }

    private static IncomingDocumentCategorisationStateTransitionType inferTransitionTypeFrom(
            final IncomingDocAsInvoiceViewModel viewModel) {

        Document document = viewModel.getDocument();
        DocumentTypeData documentTypeData = DocumentTypeData.docTypeDataFor(document);
        switch (documentTypeData) {
        case INCOMING_INVOICE:
            return IncomingDocumentCategorisationStateTransitionType.PROCESS_INVOICE;
        case INCOMING_LOCAL_INVOICE:
            return IncomingDocumentCategorisationStateTransitionType.PROCESS_LOCAL_INVOICE;
        case INCOMING_CORPORATE_INVOICE:
            return IncomingDocumentCategorisationStateTransitionType.PROCESS_CORPORATE_INVOICE;
        }
        // fail fast
        throw new IllegalArgumentException("Document type for document (" + documentTypeData + ") not recognised");
    }

    public static class DomainEvent
            extends EstatioCapexDomModule.ActionDomainEvent<IncomingDocAsInvoiceViewmodel_saveInvoice> {}

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
        IncomingInvoice incomingInvoice = incomingInvoiceRepository.create(
                viewModel.getInvoiceNumber(),
                viewModel.getBuyer().getAtPath(),
                viewModel.getBuyer(),
                viewModel.getSeller(),
                viewModel.getInvoiceDate(),
                viewModel.getDueDate(),
                viewModel.getPaymentMethod(),
                InvoiceStatus.NEW,
                viewModel.getDateReceived(),
                viewModel.getBankAccount()
        );
        // create invoice item
        incomingInvoice.addItem(
                viewModel.getCharge(),
                viewModel.getDescription(),
                viewModel.getNetAmount(),
                viewModel.getVatAmount(),
                viewModel.getGrossAmount(),
                viewModel.getTax(),
                viewModel.getDueDate(),
                viewModel.getPeriod()!= null ? PeriodUtil.yearFromPeriod(viewModel.getPeriod()).startDate() : null,
                viewModel.getPeriod()!= null ? PeriodUtil.yearFromPeriod(viewModel.getPeriod()).endDate() : null,
                (Property) viewModel.getFixedAsset(),
                viewModel.getProject(),
                viewModel.getBudgetItem());
        // attach document
        paperclipRepository.attach(viewModel.getDocument(),null,incomingInvoice);
        // link to orderItem if applicable
        if (viewModel.getOrderItem()!=null){
            Order order = viewModel.getOrderItem().getOrdr();
            Charge chargeFromWrapper = viewModel.getOrderItem().getCharge();
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
