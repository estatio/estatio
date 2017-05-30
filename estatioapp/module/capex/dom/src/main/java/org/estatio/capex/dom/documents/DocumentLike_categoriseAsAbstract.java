package org.estatio.capex.dom.documents;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Programmatic;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;

import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationState;
import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransition;
import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransitionType;
import org.estatio.capex.dom.triggers.DomainObject_triggerBaseAbstract;
import org.estatio.dom.asset.Property;
import org.estatio.dom.invoice.DocumentTypeData;

/**
 * Intended to be subclassed by 'act' mixins with first param being a {@link Property};
 * mixins should override relevant methods to make public
 */
public abstract class DocumentLike_categoriseAsAbstract
        extends DomainObject_triggerBaseAbstract<
                    Document,
                    IncomingDocumentCategorisationStateTransition,
                    IncomingDocumentCategorisationStateTransitionType,
                    IncomingDocumentCategorisationState
                > {

    protected final DocumentTypeData documentTypeData;

    public DocumentLike_categoriseAsAbstract(
            final DocumentTypeData documentTypeData,
            final IncomingDocumentCategorisationStateTransitionType transitionType) {
        super(transitionType);
        this.documentTypeData = documentTypeData;
    }

    @Programmatic
    public abstract Document getDomainObject();

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
        final Document document = getDomainObject();
        return documentTypeData.isDocTypeFor(document) || !DocumentTypeData.hasIncomingType(document);
    }

    HasDocumentAbstract categoriseAndAttachPaperclip(final Property property) {
        final Document document = getDomainObject();
        document.setType(documentTypeData.findUsing(documentTypeRepository));
        attachPaperclipTo(property);
        return viewModelFactory.createFor(document);
    }

    private void attachPaperclipTo(final Property property) {
        final Document document = getDomainObject();
        final Property existingProperty = existingPropertyAttachmentIfAny();
        if(existingProperty != null) {
            paperclipRepository.deleteIfAttachedTo(existingProperty);
        }
        paperclipRepository.attach(document, null, property);
    }

    private Property existingPropertyAttachmentIfAny() {
        final Document document = getDomainObject();
        return paperclipRepository.paperclipAttaches(document, Property.class);
    }

    @Inject
    protected DocumentTypeRepository documentTypeRepository;

    @Inject
    protected PaperclipRepository paperclipRepository;

    @Inject
    protected HasDocumentAbstract.Factory viewModelFactory;

}
