package org.estatio.module.coda.dom.doc;

public enum Handling {
    /**
     * The document has been marked as to be ignored; it will not be validated nor sync'd.
     */
    EXCLUDED,
    /**
     * The document should not be ignored, but has validation errors and so needs attention.
     */
    ATTENTION,
    /**
     * The document was valid but an Estatio invoice has not yet been created for it.
     *
     * <p>
     *     Whether auto-syncing occurs depends on the ApplicationSetting.
     * </p>
     */
    VALID,
    /**
     * The document was valid and an Estatio invoice (and related objects) has been (or is about to be) created for it.
     */
    SYNCED,
    ;

    public boolean isSynced() {
        return this == SYNCED;
    }
}
