package org.estatio.dom.capex;

import java.util.List;

import com.google.common.collect.ImmutableList;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.i18n.TranslatableString;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.Document_delete;

import org.estatio.dom.invoice.DocumentTypeData;

@DomainService(
        nature = NatureOfService.DOMAIN,
        objectType = "estatio.VetoDeleteOfIncomingDocumentIfCategorised"
)
public class VetoDeleteOfIncomingDocumentIfCategorised extends AbstractSubscriber {

    private static final List<DocumentTypeData> categorizedDocumentTypes =
            ImmutableList.of(
                    DocumentTypeData.INCOMING_INVOICE,
                    DocumentTypeData.INCOMING_ORDER
            );

    @Programmatic
    @org.axonframework.eventhandling.annotation.EventHandler
    public void on(final Document_delete.ActionDomainEvent ev) {
        switch (ev.getEventPhase()) {
        case DISABLE:
            final Document document = (Document) ev.getMixedIn();
            for (DocumentTypeData categorizedDocumentType : categorizedDocumentTypes) {
                if (categorizedDocumentType.isDocTypeFor(document)) {
                    ev.veto(TranslatableString.tr(
                            "Document has already been categorized (as {documentType})",
                            "documentType", categorizedDocumentType.getName()));
                }
            }
        }
    }
}
