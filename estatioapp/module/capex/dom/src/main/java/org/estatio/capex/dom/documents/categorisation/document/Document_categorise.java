package org.estatio.capex.dom.documents.categorisation.document;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;

import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationState;
import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransition;
import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransitionType;
import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.capex.dom.order.Order;
import org.estatio.capex.dom.order.OrderRepository;
import org.estatio.capex.dom.triggers.DomainObject_triggerAbstract;
import org.estatio.dom.asset.Property;
import org.estatio.dom.invoice.DocumentTypeData;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.invoice.PaymentMethod;

@Mixin(method = "act")
public class Document_categorise
        extends DomainObject_triggerAbstract<
                                            Document,
                                            IncomingDocumentCategorisationStateTransition,
                                            IncomingDocumentCategorisationStateTransitionType,
                                            IncomingDocumentCategorisationState> {

    public Document_categorise(final Document document) {
        super(document, IncomingDocumentCategorisationStateTransition.class,
              Arrays.asList( IncomingDocumentCategorisationState.NEW), IncomingDocumentCategorisationStateTransitionType.CATEGORISE);
    }

    @Action(
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(cssClassFa = "folder-open-o")
    public Object act(
            final DocumentTypeData documentTypeData,
            @Nullable final IncomingInvoiceType incomingInvoiceType,
            @Nullable final Property property,
            @Nullable final String comment) {

        final Document document = getDomainObject();

        document.setType(documentTypeData.findUsing(documentTypeRepository));

        Object entity = null;

        switch (documentTypeData) {
        case INCOMING_ORDER:

            // create order
            Order order = orderRepository.create(
                    null,
                    null,
                    clockService.now(),
                    null,
                    null,
                    null,
                    document.getAtPath(),
                    null,
                    null
            );
            entity = order;

            break;

        case INCOMING_INVOICE:

            final IncomingInvoice incomingInvoice = incomingInvoiceRepository.create(
                    incomingInvoiceType,
                    null,
                    document.getAtPath(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    InvoiceStatus.NEW,
                    clockService.now(),
                    null
            );
            entity = incomingInvoice;

            // some invoice types will have a property, so we copy over directly.
            incomingInvoice.setProperty(property);

            incomingInvoice.setDateReceived(document.getCreatedAt().toLocalDate());
            incomingInvoice.setDueDate(document.getCreatedAt().toLocalDate().plusDays(30));
            incomingInvoice.setPaymentMethod(PaymentMethod.MANUAL_PROCESS);

            break;
        }

        if(entity != null) {
            // should always be true...
            paperclipRepository.attach(document, null, entity);
        }

        trigger(comment);

        return entity;
    }

    public List<DocumentTypeData> choices0Act() {
        return Arrays.asList(DocumentTypeData.INCOMING_ORDER, DocumentTypeData.INCOMING_INVOICE);
    }

    public String validateAct(
            final DocumentTypeData documentTypeData,
            final IncomingInvoiceType incomingInvoiceType,
            final Property property,
            final String comment) {

        switch (documentTypeData) {
        case INCOMING_INVOICE:
            if (incomingInvoiceType == null) {
                return "Invoice type is required";
            }
            String validateReason = incomingInvoiceType.validateProperty(property);
            if(validateReason != null) {
                return validateReason;
            }
            break;
        }
        return null;
    }


    public boolean hideAct() {
        if(cannotTransition()) {
            return true;
        }
        final Document document = getDomainObject();
        return !DocumentTypeData.hasIncomingType(document);
    }

    @Inject
    DocumentTypeRepository documentTypeRepository;

    @Inject
    PaperclipRepository paperclipRepository;

    @Inject
    IncomingInvoiceRepository incomingInvoiceRepository;

    @Inject
    OrderRepository orderRepository;

    @Inject
    ClockService clockService;

}
