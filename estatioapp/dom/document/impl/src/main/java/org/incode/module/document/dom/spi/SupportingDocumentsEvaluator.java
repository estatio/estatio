package org.incode.module.document.dom.spi;

import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

public interface SupportingDocumentsEvaluator {

    enum Evaluation {
        /**
         * The {@link Document} should be treated as a supporting document
         *
         * <p>
         *     No further {@link SupportingDocumentsEvaluator evaluator}s will be consulted.
         * </p>
         */
        SUPPORTING,
        /**
         * The {@link Document} should <i>not</i>be treated as a supporting document
         *
         * <p>
         *     No further {@link SupportingDocumentsEvaluator evaluator}s will be consulted.
         * </p>
         */
        NOT_SUPPORTING,
        /**
         * Has no opinion on whether the {@link Document} is to be considered as supporting or not.
         *
         * <p>
         *     In such a case the next {@link SupportingDocumentsEvaluator evaluator} available (if any) will be
         *     considered.  If no evaluator recognizes the {@link Document} then it is considered as
         *     <i>not</i> being a supporting document.
         * </p>
         */
        UNKNOWN
    }

    /**
     * Whether this is a supporting document or not.
     *
     * <p>
     *     For the default implementation, this returns {@link Evaluation#SUPPORTING} if and only if
     *     {@link #supportedBy(Document)} returns a non-null value, otherwise it returns {@link Evaluation#UNKNOWN}.
     *     However, other implementations might indicate that an object <i>is</i> supporting, without necessarily
     *     knowing which document(s) they support (ie also return null for {@link #supportedBy(Document)}).
     * </p>
     */
    public Evaluation evaluate(Document candidateSupportingDocument);

    /**
     * The &quot;primary&quot; document, if any, for which *this* document is a supporting document, else null.
     *
     * <p>
     *     For the default implementation, this searches for any other {@link Document} that has the candidate
     *     document attached to it.
     * </p>
     */
    @Programmatic
    public List<Document> supportedBy(Document candidateSupportingDocument);


    @DomainService(
            nature = NatureOfService.DOMAIN
    )
    public static class Default implements SupportingDocumentsEvaluator {

        @Override
        public List<Document> supportedBy(Document candidateSupportingDocument) {
            final List<Paperclip> byDocument = paperclipRepository.findByDocument(candidateSupportingDocument);
            final List<Document> supportedDocumentsIfAny =
                    FluentIterable.from(byDocument)
                            .transform(x -> x.getAttachedTo())
                            .filter(Document.class::isInstance)
                            .transform(Document.class::cast)
                            .toList();
            return Lists.newArrayList(supportedDocumentsIfAny);
        }

        @Override
        public Evaluation evaluate(final Document candidateSupportingDocument) {
            List<Document> documents = supportedBy(candidateSupportingDocument);
            boolean b = documents != null && !documents.isEmpty();
            return b ? Evaluation.SUPPORTING : Evaluation.UNKNOWN;
        }

        @Inject
        PaperclipRepository paperclipRepository;
    }

}
