package org.estatio.capex.dom.documents.categorisation.document;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;

import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransitionType;
import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.dom.asset.Property;
import org.estatio.dom.invoice.DocumentTypeData;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.invoice.PaymentMethod;

@Mixin(method = "act")
public class Document_categoriseAsPropertyInvoice
        extends Document_triggerAbstract {

    private final Document document;

    public Document_categoriseAsPropertyInvoice(final Document document) {
        super(document, IncomingDocumentCategorisationStateTransitionType.CATEGORISE);
        this.document = document;
    }

    @Action(
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(cssClassFa = "folder-open-o")
    public Object act(
            final Property property,
            @Nullable final String comment) {

        final Document document = getDomainObject();

        document.setType(DocumentTypeData.INCOMING_INVOICE.findUsing(documentTypeRepository));

        LocalDate dateReceived = document.getCreatedAt().toLocalDate();
        LocalDate dueDate = document.getCreatedAt().toLocalDate().plusDays(30);
        PaymentMethod paymentMethod = PaymentMethod.MANUAL_PROCESS;

        final IncomingInvoice incomingInvoice = incomingInvoiceRepository.create(
                IncomingInvoiceType.PROPERTY_EXPENSES, // a reasonable default
                null, // invoiceNumber
                property,
                document.getAtPath(),
                null, // buyer
                null, // seller
                null, // invoiceDate
                dueDate,
                paymentMethod,
                InvoiceStatus.NEW,
                dateReceived,
                null, // bankAccount
                null  // approval state... will cause state transition to be created automatically by subscriber
        );

        paperclipRepository.attach(document, null, incomingInvoice);

        trigger(comment);

        return incomingInvoice;
    }


    public boolean hideAct() {
        if(cannotTransition()) {
            return true;
        }
        final Document document = getDomainObject();
        return !DocumentTypeData.hasIncomingType(document);
    }

    public String disableAct() {
        return reasonGuardNotSatisified();
    }


    @Inject
    DocumentTypeRepository documentTypeRepository;

    @Inject
    PaperclipRepository paperclipRepository;

    @Inject
    IncomingInvoiceRepository incomingInvoiceRepository;

}
