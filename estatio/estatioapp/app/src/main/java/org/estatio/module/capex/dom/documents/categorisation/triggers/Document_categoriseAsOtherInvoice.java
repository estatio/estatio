package org.estatio.module.capex.dom.documents.categorisation.triggers;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;

import org.estatio.module.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransitionType;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.module.invoice.dom.DocumentTypeData;
import org.estatio.module.invoice.dom.InvoiceStatus;

/**
 * This cannot be inlined (needs to be a mixin) because Document is part of the incode platform and
 * does not know about its invoices
 */
@Mixin(method = "act")
public class Document_categoriseAsOtherInvoice
        extends Document_triggerAbstract {

    private final Document document;

    public Document_categoriseAsOtherInvoice(final Document document) {
        super(document, IncomingDocumentCategorisationStateTransitionType.CATEGORISE);
        this.document = document;
    }

    public static class ActionDomainEvent
            extends Document_triggerAbstract.ActionDomainEvent<Document_categoriseAsOtherInvoice> {}

    @Action(
            domainEvent = ActionDomainEvent.class,
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(cssClassFa = "folder-open-o")
    public Object act(
            final IncomingInvoiceType incomingInvoiceType,
            @Nullable final String comment) {

        final Document document = getDomainObject();

        document.setType(DocumentTypeData.INCOMING_INVOICE.findUsing(documentTypeRepository));

        LocalDate dateReceived = document.getCreatedAt().toLocalDate();
        LocalDate dueDate = document.getCreatedAt().toLocalDate().plusDays(30);

        final IncomingInvoice incomingInvoice = incomingInvoiceRepository.create(
                incomingInvoiceType,
                null, // invoiceNumber
                null, // property
                document.getAtPath(),
                buyerFinder.buyerDerivedFromDocumentName(document), // buyer
                null, // seller
                null, // invoiceDate
                null, // vatRegistrationDate
                dueDate,
                null,
                InvoiceStatus.NEW,
                dateReceived,
                null, // bankAccount
                null,  // approval state... will cause state transition to be created automatically by subscriber
                false, // postedToCodaBooks
                null
        );

        paperclipRepository.attach(document, null, incomingInvoice);

        trigger(comment, null);

        return incomingInvoice;
    }

    public List<IncomingInvoiceType> choices0Act() {
        return Lists.newArrayList(
                IncomingInvoiceType.LOCAL_EXPENSES,
                IncomingInvoiceType.CORPORATE_EXPENSES
        );
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
