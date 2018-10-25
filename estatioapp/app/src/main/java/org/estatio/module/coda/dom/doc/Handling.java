package org.estatio.module.coda.dom.doc;

public enum Handling {
    /**
     * The document should not be ignored.
     *
     * If its {@link CodaDocHead#getValidationStatus() validation status} is {@link ValidationStatus#VALID}, then
     * there will be corresponding Estatio entities for the various implicitly referenced objects (property, project,
     * order, charge and incoming invoice).
     *
     * If its {@link CodaDocHead#getValidationStatus() validation status} is {@link ValidationStatus#INVALID}, then
     * there will be NO corresponding Estatio entities and the {@link CodaDocHead document} will be brought to the
     * users' attention as an exception.
     *
     *
     */
    INCLUDE,
    /**
     * The document corresponds to an archived project (so should be excluded from processing).
     */
    EXCLUDE_PROJECT_ARCHIVED,
    /**
     * The document should be excluded from processing for some other reason.
     */
    EXCLUDE_OTHER,
    ;
}
