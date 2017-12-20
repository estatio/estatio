package org.incode.module.document.dom.impl.types;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.document.dom.impl.docs.DocumentTemplate;
import org.incode.module.document.dom.impl.docs.DocumentTemplateRepository;

@Mixin
public class DocumentType_currentTemplates {

    //region > constructor
    private final DocumentType documentType;
    public DocumentType_currentTemplates(final DocumentType documentType) {
        this.documentType = documentType;
    }
    //endregion


    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<DocumentTemplate> $$() {
        return documentTemplateRepository.findByType(documentType);
    }


    @Inject
    DocumentTemplateRepository documentTemplateRepository;


}
