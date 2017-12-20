package org.incode.module.document.dom.impl.docs;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.i18n.TranslatableString;

import org.incode.module.document.dom.DocumentModule;
import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

@Mixin
public class DocumentTemplate_delete {

    //region > constructor
    private final DocumentTemplate documentTemplate;

    public DocumentTemplate_delete(final DocumentTemplate documentTemplate) {
        this.documentTemplate = documentTemplate;
    }
    //endregion


    public static class ActionDomainEvent extends DocumentModule.ActionDomainEvent<DocumentTemplate_delete>  { }
    @Action(
            semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE,
            domainEvent = ActionDomainEvent.class
    )
    public void $$() {
        documentTemplateRepository.delete(documentTemplate);
        return;
    }
    public TranslatableString disable$$() {
        final List<Paperclip> paperclips = paperclipRepository.findByDocument(documentTemplate);
        return !paperclips.isEmpty()
                ? TranslatableString.tr("This template is attached to objects")
                : null;
    }

    @Inject
    private PaperclipRepository paperclipRepository;

    @Inject
    private DocumentTemplateRepository documentTemplateRepository;


}
