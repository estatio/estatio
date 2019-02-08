package org.estatio.module.capex.dom.documents.categorisation.triggers;

import java.util.List;
import java.util.stream.Collectors;

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

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.role.FixedAssetRole;
import org.estatio.module.asset.dom.role.FixedAssetRoleRepository;
import org.estatio.module.asset.dom.role.FixedAssetRoleTypeEnum;
import org.estatio.module.capex.dom.documents.BuyerFinder;
import org.estatio.module.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransitionType;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.module.invoice.dom.DocumentTypeData;
import org.estatio.module.invoice.dom.InvoiceStatus;
import org.estatio.module.party.dom.Party;

/**
 * This cannot be inlined (needs to be a mixin) because Document is part of the incode platform and
 * does not know about its invoices
 */
@Mixin(method = "act")
public class Document_categoriseAsPropertyInvoice
        extends Document_triggerAbstract {

    private final Document document;

    public Document_categoriseAsPropertyInvoice(final Document document) {
        super(document, IncomingDocumentCategorisationStateTransitionType.CATEGORISE);
        this.document = document;
    }

    public static class ActionDomainEvent
            extends Document_triggerAbstract.ActionDomainEvent<Document_categoriseAsPropertyInvoice> {
    }

    @Action(
            domainEvent = ActionDomainEvent.class,
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

        final IncomingInvoice incomingInvoice = incomingInvoiceRepository.create(
                null, // EST-1508: the users prefer no default
                null, // invoiceNumber
                property,
                document.getAtPath(),
                buyerFinder.buyerDerivedFromDocumentName(document), // buyer
                null, // seller
                null, // invoiceDate
                dueDate,
                null, // vatRegistrationDate (only for ITA)
                null,
                InvoiceStatus.NEW,
                dateReceived,
                null, // bankAccount
                null,  // approval state... will cause state transition to be created automatically by subscriber
                false, // postedToCodaBooks (only for ITA, comes from Coda)
                null   // paidDate (only for ITA, comes from Coda)
        );

        paperclipRepository.attach(document, null, incomingInvoice);

        trigger(comment, null);

        return incomingInvoice;
    }

    public boolean hideAct() {
        if (cannotTransition()) {
            return true;
        }
        final Document document = getDomainObject();
        return !DocumentTypeData.hasIncomingType(document);
    }

    public String disableAct() {
        return reasonGuardNotSatisified();
    }

    public List<Property> choices0Act() {
        Party buyerFromBarcode = buyerFinder.buyerDerivedFromDocumentName(getDomainObject());
        return fixedAssetRoleRepository.findByPartyAndType(buyerFromBarcode, FixedAssetRoleTypeEnum.PROPERTY_OWNER)
                .stream()
                .map(FixedAssetRole::getAsset)
                .filter(fixedAsset -> fixedAsset instanceof Property)
                .map(Property.class::cast)
                .collect(Collectors.toList());
    }

    @Inject
    DocumentTypeRepository documentTypeRepository;

    @Inject
    PaperclipRepository paperclipRepository;

    @Inject
    IncomingInvoiceRepository incomingInvoiceRepository;

    @Inject
    BuyerFinder buyerFinder;

    @Inject
    FixedAssetRoleRepository fixedAssetRoleRepository;

}
