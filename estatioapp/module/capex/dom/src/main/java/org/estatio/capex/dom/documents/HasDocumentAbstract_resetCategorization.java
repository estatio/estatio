package org.estatio.capex.dom.documents;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.documents.incoming.IncomingDocumentViewModel;
import org.estatio.dom.invoice.DocumentTypeData;

@Mixin(method = "act")
public class HasDocumentAbstract_resetCategorization extends HasDocumentAbstract_categorizeAbstract {

    public HasDocumentAbstract_resetCategorization(final HasDocumentAbstract hasDocument) {
        super(hasDocument, DocumentTypeData.INCOMING, IncomingDocumentViewModel.class);
    }

}
