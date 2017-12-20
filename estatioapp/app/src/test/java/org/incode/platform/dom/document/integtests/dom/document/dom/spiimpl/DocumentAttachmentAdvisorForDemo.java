package org.incode.platform.dom.document.integtests.dom.document.dom.spiimpl;

import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.types.DocumentType;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;
import org.incode.module.document.dom.spi.DocumentAttachmentAdvisor;
import org.incode.platform.dom.document.integtests.dom.document.fixture.seed.DocumentTypeAndTemplatesApplicableForDemoObjectFixture;

@DomainService(nature = NatureOfService.DOMAIN)
public class DocumentAttachmentAdvisorForDemo implements DocumentAttachmentAdvisor {

    private static final String ROLE_NAME = "receipt";

    @Override
    public List<DocumentType> documentTypeChoicesFor(final Document document) {
        final List<DocumentType> documentTypes = Lists.newArrayList();
        append(DocumentTypeAndTemplatesApplicableForDemoObjectFixture.DOC_TYPE_REF_TAX_RECEIPT, documentTypes);
        append(DocumentTypeAndTemplatesApplicableForDemoObjectFixture.DOC_TYPE_REF_SUPPLIER_RECEIPT, documentTypes);
        return documentTypes;
    }

    private void append(final String docTypeRef, final List<DocumentType> documentTypes) {
        final DocumentType documentType = documentTypeRepository
                .findByReference(docTypeRef);
        documentTypes.add(documentType);
    }

    @Override
    public DocumentType documentTypeDefaultFor(final Document document) {
        return documentTypeChoicesFor(document).get(0);
    }

    @Override
    public List<String> roleNameChoicesFor(final Document document) {
        return Lists.newArrayList(ROLE_NAME);
    }

    @Override
    public String roleNameDefaultFor(final Document document) {
        return roleNameChoicesFor(document).get(0);
    }

    @Inject
    DocumentTypeRepository documentTypeRepository;


}
