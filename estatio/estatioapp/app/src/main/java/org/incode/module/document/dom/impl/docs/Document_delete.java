package org.incode.module.document.dom.impl.docs;

import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.document.DocumentModule;
import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

@Mixin
public class Document_delete {

    //region > constructor
    private final Document document;

    public Document_delete(final Document document) {
        this.document = document;
    }
    //endregion


    public static class ActionDomainEvent extends DocumentModule.ActionDomainEvent<Document_delete> { }
    @Action(
            semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE,
            domainEvent = ActionDomainEvent.class
    )
    @ActionLayout(cssClassFa = "trash")
    public Object $$() {

        List<Object> attachedToList = Lists.newArrayList();

        // links from this document to other objects
        List<Paperclip> paperclips = paperclipRepository.findByDocument(document);
        for (Paperclip paperclip : paperclips) {

            final Object attachedTo = paperclip.getAttachedTo();
            attachedToList.add(attachedTo);

            paperclipRepository.delete(paperclip);
        }

        // links from other documents to this document
        paperclips = paperclipRepository.findByAttachedTo(document);
        for (Paperclip paperclip : paperclips) {
            paperclipRepository.delete(paperclip);
        }

        documentRepository.delete(document);

        // if was only attached to a single object, then return; otherwise return null.
        return attachedToList.size() == 1 ? attachedToList.get(0): null;
    }


    @Inject
    DocumentRepository documentRepository;

    @Inject
    PaperclipRepository paperclipRepository;

}
