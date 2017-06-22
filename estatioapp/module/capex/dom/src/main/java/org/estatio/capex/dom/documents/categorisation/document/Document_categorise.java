package org.estatio.capex.dom.documents.categorisation.document;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Programmatic;
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
import org.estatio.capex.dom.order.Order;
import org.estatio.capex.dom.order.OrderRepository;
import org.estatio.capex.dom.triggers.DomainObject_triggerBaseAbstract;
import org.estatio.dom.asset.Property;
import org.estatio.dom.invoice.DocumentTypeData;
import org.estatio.dom.invoice.InvoiceStatus;

@Mixin(method = "act")
public class Document_categorise
        extends DomainObject_triggerBaseAbstract<
                    Document,
                    IncomingDocumentCategorisationStateTransition,
                    IncomingDocumentCategorisationStateTransitionType,
                    IncomingDocumentCategorisationState> {

    private final Document document;

    public Document_categorise(final Document document) {
        super(IncomingDocumentCategorisationStateTransition.class,
              Arrays.asList( IncomingDocumentCategorisationState.NEW));
        this.document = document;
    }

    @Programmatic
    @Override
    public Document getDomainObject() {
        return document;
    }

    @Action(
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(cssClassFa = "folder-open-o")
    public Object act(
            final DocumentTypeData documentTypeData,
            @Nullable final IncomingInvoice.Type incomingInvoiceType,
            @Nullable final Property property,
            @Nullable final String comment) {

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
            final IncomingInvoice.Type incomingInvoiceType,
            final Property property,
            final String comment) {

        switch (documentTypeData) {
        case INCOMING_INVOICE:
            if (incomingInvoiceType == null) {
                return "Invoice type is required";
            }
            if (incomingInvoiceType.relatesToProperty() && property == null) {
                return "Property is required for " + incomingInvoiceType;
            }
            break;
        }
        return null;
    }

    public Property default2Act() {
        return existingPropertyAttachmentIfAny();
    }

    public boolean hideAct() {
        if(cannotTransition()) {
            return true;
        }
        final Document document = getDomainObject();
        return !DocumentTypeData.hasIncomingType(document);
    }

    private Property existingPropertyAttachmentIfAny() {
        final Document document = getDomainObject();
        return paperclipRepository.paperclipAttaches(document, Property.class);
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
