package org.incode.module.communications.dom.mixins;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import org.incode.module.base.dom.MimeTypeData;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentSort;

public class DocumentPredicates {
    private DocumentPredicates() {
    }

    public static Predicate<Document> isPdfAndBlob() {
        return Predicates.and(MimeTypeData.APPLICATION_PDF::matches, isBlobSort());
    }

    public static Predicate<Document> isBlobSort() {
        return document -> {
            final DocumentSort documentSort = document.getSort();
            return !(documentSort != DocumentSort.BLOB && documentSort != DocumentSort.EXTERNAL_BLOB);
        };
    }
}
