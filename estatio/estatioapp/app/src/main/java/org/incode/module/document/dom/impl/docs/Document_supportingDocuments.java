package org.incode.module.document.dom.impl.docs;

import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.tablecol.TableColumnOrderService;

import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.mixins.T_documents;
import org.incode.module.document.dom.spi.SupportingDocumentsEvaluator;

@Mixin
public class Document_supportingDocuments extends T_documents<Document> {

    public Document_supportingDocuments(final Document document) {
        super(document);
    }

    // hide if this document is actually a supporting document for some other primary document
    public boolean hide$$() {
        Document document = getAttachedTo();
        for (SupportingDocumentsEvaluator supportingDocumentsEvaluator : supportingDocumentsEvaluators) {
            final SupportingDocumentsEvaluator.Evaluation evaluation =
                    supportingDocumentsEvaluator.evaluate(document);
            if(evaluation == SupportingDocumentsEvaluator.Evaluation.SUPPORTING) {
                return true;
            }
        }
        return false;
    }

    @Inject
    List<SupportingDocumentsEvaluator> supportingDocumentsEvaluators;


    @DomainService(
            nature = NatureOfService.DOMAIN,
            menuOrder = "100"
    )
    public static class ColumnOrderServiceInbound implements TableColumnOrderService {

        @Override
        public List<String> orderParented(
                final Object domainObject,
                final String collectionId,
                final Class<?> collectionType,
                final List<String> propertyIds) {
            if (!Paperclip.class.isAssignableFrom(collectionType)) {
                return null;
            }

            if (!(domainObject instanceof DocumentAbstract)) {
                return null;
            }

            if (!"supportingDocuments".equals(collectionId)) {
                return null;
            }

            final List<String> trimmedPropertyIds = Lists.newArrayList(propertyIds);
            trimmedPropertyIds.remove("attachedTo");
            return trimmedPropertyIds;
        }

        @Override
        public List<String> orderStandalone(final Class<?> collectionType, final List<String> propertyIds) {
            return null;
        }
    }


}
