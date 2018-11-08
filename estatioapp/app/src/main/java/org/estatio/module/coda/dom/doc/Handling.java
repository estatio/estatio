package org.estatio.module.coda.dom.doc;

public enum Handling {
    /**
     * The document was valid and an Estatio invoice has been created for it.
     */
    SYNCED,
    /**
     * The document should not be ignored, but has validation problems and so needs attention.
     */
    ATTENTION,
    /**
     * The document has been marked as to be ignored; it will not be synced.
     */
    EXCLUDED
    ;
}
