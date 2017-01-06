
/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.dom.leaseinvoicing.dnc;

import java.io.IOException;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.title.TitleService;

import org.incode.module.document.dom.DocumentModule;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;
import org.incode.module.document.dom.impl.docs.DocumentTemplateRepository;
import org.incode.module.document.dom.impl.types.DocumentType;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;
import org.incode.module.document.dom.mixins.T_createAndAttachDocumentAndRender;
import org.incode.module.document.dom.services.DocumentCreatorService;

import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.leaseinvoicing.viewmodel.dnc.DocAndCommAbstract_abstract;
import org.estatio.dom.leaseinvoicing.viewmodel.dnc.InvoiceSummaryForPropertyDueDateStatus_actionAbstract;

public abstract class Invoice_prepareAbstract {

    final Invoice invoice;
    final String documentTypeReference;

    public Invoice_prepareAbstract(final Invoice invoice, final String documentTypeReference) {
        this.invoice = invoice;
        this.documentTypeReference = documentTypeReference;
    }

    public static class ActionDomainEvent extends DocumentModule.ActionDomainEvent<T_createAndAttachDocumentAndRender> {}

    @Action(
            domainEvent = ActionDomainEvent.class,
            semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE
    )
    @ActionLayout(
            contributed = Contributed.AS_ACTION
    )
    public Invoice $$() throws IOException {

        final DocumentTemplate template = documentTemplateFor(invoice);
        final Document document =
                documentCreatorService.createDocumentAndAttachPaperclips(invoice, template);

        document.render(template, invoice);

        return invoice;
    }

    public String disable$$() {
        return documentTemplateFor(invoice) == null
                ? String.format("Could not locate a DocumentTemplate for %s for invoice %s",
                                titleService.titleOf(getDocumentType()),
                                titleService.titleOf(invoice))
                : null;
    }

    DocumentType getDocumentType() {
        return queryResultsCache.execute(
                () -> documentTypeRepository.findByReference(documentTypeReference),
                DocAndCommAbstract_abstract.class,
                "getDocumentType", documentTypeReference);
    }

    DocumentTemplate documentTemplateFor(final Invoice invoice) {
        return queryResultsCache.execute(
                () -> documentTemplateRepository
                        .findFirstByTypeAndApplicableToAtPath(
                                getDocumentType(),
                                invoice.getApplicationTenancyPath()),
                InvoiceSummaryForPropertyDueDateStatus_actionAbstract.class,
                "findFirstByTypeAndApplicableToAtPath",
                getDocumentType(), invoice
        );
    }

    //region > injected services
    @Inject
    TitleService titleService;

    @Inject
    DocumentTypeRepository documentTypeRepository;

    @Inject
    DocumentCreatorService documentCreatorService;

    @Inject
    QueryResultsCache queryResultsCache;

    @Inject
    DocumentTemplateRepository documentTemplateRepository;
    //endregion

}
