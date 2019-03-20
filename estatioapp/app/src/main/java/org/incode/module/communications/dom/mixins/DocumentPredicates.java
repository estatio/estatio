package org.incode.module.communications.dom.mixins;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import org.incode.module.base.dom.MimeTypes;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentSort;

public class DocumentPredicates {
    private DocumentPredicates() {
    }

    public static Predicate<Document> isPdfAndBlob() {
        return Predicates.and(isPdf(), isBlobSort());
    }

    public static Predicate<Document> isPdf() {
        return document -> MimeTypes.APPLICATION_PDF.equals(document.getMimeType());
    }

    public static Predicate<Document> isBlobSort() {
        return document -> {
            final DocumentSort documentSort = document.getSort();
            return !(documentSort != DocumentSort.BLOB && documentSort != DocumentSort.EXTERNAL_BLOB);
        };
    }
}
