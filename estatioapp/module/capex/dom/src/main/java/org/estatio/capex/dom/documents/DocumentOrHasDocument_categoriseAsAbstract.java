package org.estatio.capex.dom.documents;

import javax.inject.Inject;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;

import org.estatio.capex.dom.EstatioCapexDomModule;
import org.estatio.dom.asset.Property;
import org.estatio.dom.invoice.DocumentTypeData;

/**
 * Intended to be subclassed by 'act' mixins with first param being a {@link Property};
 * mixins should override relevant methods to make public
 */
public abstract class DocumentOrHasDocument_categoriseAsAbstract {

    private final DocumentTypeData documentTypeData;

    public DocumentOrHasDocument_categoriseAsAbstract(final DocumentTypeData documentTypeData) {
        this.documentTypeData = documentTypeData;
    }

    protected abstract Document getDocument();

    public static class DomainEvent extends EstatioCapexDomModule.ActionDomainEvent<Document> {}

    /**
     * mixins should override to make public
     */
    protected Property default0Act() {
        return existingPropertyAttachmentIfAny();
    }

    /**
     * mixins should override to make public
     */
    protected boolean hideAct() {
        final Document document = getDocument();
        return documentTypeData.isDocTypeFor(document);
    }

    protected HasDocument categoriseAndAttachPaperclip(final Property property) {
        final Document document = getDocument();
        document.setType(documentTypeData.findUsing(documentTypeRepository));
        attachPaperclipTo(property);
        return viewModelFactory.createFor(document);
    }

    private void attachPaperclipTo(final Property property) {
        final Document document = getDocument();
        final Property existingProperty = existingPropertyAttachmentIfAny();
        if(existingProperty != null) {
            paperclipRepository.deleteIfAttachedTo(existingProperty);
        }
        paperclipRepository.attach(document, null, property);
    }

    private Property existingPropertyAttachmentIfAny() {
        final Document document = getDocument();
        return paperclipRepository.paperclipAttaches(document, Property.class);
    }

    @Inject
    protected DocumentTypeRepository documentTypeRepository;

    @Inject
    protected PaperclipRepository paperclipRepository;

    @Inject
    protected HasDocumentAbstract.Factory viewModelFactory;

}
