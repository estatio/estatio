package org.estatio.capex.dom.documents.categorisation.document;

import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

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
public abstract class DocOrIncomingDocViewModel_categoriseAsAbstract
        extends DomainObject_triggerBaseAbstract<
                    Document,
        IncomingDocumentCategorisationStateTransition,
        IncomingDocumentCategorisationStateTransitionType,
        IncomingDocumentCategorisationState
                > {

    public DocOrIncomingDocViewModel_categoriseAsAbstract() {
        super(IncomingDocumentCategorisationStateTransitionType.CATEGORISE);
    }

    @Programmatic
    public abstract Document getDomainObject();

    /**
     * mixins should override to make <tt>public</tt>.
     */
    protected List<DocumentTypeData> choices0Act() {
        return Lists.newArrayList(
                DocumentTypeData.INCOMING_INVOICE,
                DocumentTypeData.INCOMING_ORDER,
                DocumentTypeData.INCOMING_LOCAL_INVOICE,
                DocumentTypeData.INCOMING_CORPORATE_INVOICE
        );
    }

    /**
     * mixins should override to make <tt>public</tt>.
     */
    protected Property default1Act() {
        return existingPropertyAttachmentIfAny();
    }

    /**
     * mixins should override to make <tt>public</tt>.
     */
    protected boolean hideAct() {
        if(cannotTransition()) {
            return true;
        }
        final Document document = getDomainObject();
        return !DocumentTypeData.hasIncomingType(document);
    }

    protected Document categoriseAndAttachPaperclip(final Property property, final DocumentTypeData documentTypeData) {
        final Document document = getDomainObject();
        document.setType(documentTypeData.findUsing(documentTypeRepository));
        if (property!=null) {
            attachPaperclipTo(property);
        }
        return document;
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
    protected IncomingDocViewModel.Factory viewModelFactory;

}
