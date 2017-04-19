package org.estatio.capex.dom.documents;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.documents.incoming.IncomingDocumentViewModel;
import org.estatio.dom.invoice.DocumentTypeData;

@Mixin(method = "act")
public class HasDocumentAbstract_categorizeAsUnknown extends HasDocumentAbstract_categorizeAbstract {

    public HasDocumentAbstract_categorizeAsUnknown(final HasDocumentAbstract hasDocument) {
        super(hasDocument, DocumentTypeData.INCOMING, IncomingDocumentViewModel.class);
    }

}
