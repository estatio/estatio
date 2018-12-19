package org.estatio.module.coda.dom.doc;

/**
 * Necessary to decouple because some of the validation relies on Coda WSDL, which is proprietary
 * and so cannot be in the open source package.
 */
public interface LineValidator {
    void validateSummaryDocLine(CodaDocLine summaryDocLine);

    void validateAnalysisDocLine(CodaDocLine analysisDocLine);

    LineValidator NOOP = new LineValidator() {
        @Override public void validateSummaryDocLine(final CodaDocLine summaryDocLine) {
        }

        @Override public void validateAnalysisDocLine(final CodaDocLine analysisDocLine) {

        }
    };
}
