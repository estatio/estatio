package org.incode.module.document.dom.impl.docs;

import org.joda.time.DateTime;

import org.apache.isis.applib.annotation.Programmatic;

public enum DocumentState {
    NOT_RENDERED {
        @Override public DateTime dateOf(final Document document) {
            return document.getCreatedAt();
        }
    },
    RENDERED {
        @Override public DateTime dateOf(final Document document) {
            return document.getRenderedAt();
        }
    };

    @Programmatic
    public abstract DateTime dateOf(final Document document);
}
