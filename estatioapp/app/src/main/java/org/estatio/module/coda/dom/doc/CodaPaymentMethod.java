package org.estatio.module.coda.dom.doc;

import com.google.common.base.Strings;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum CodaPaymentMethod {
    /**
     * All invoices that have this payment method must be approve
     */
    P_BON("P-BON"),
    /**
     * in this case the invoices are paid by SEPA, we don't need approval
     */
    P_RID("P-RID"),
    /**
     * the invoices are paid with credit card, or cash, we don't need approval.
     */
    P_RIBA("P-RIBA"),
    ;

    final String mediaCode;

    public static CodaPaymentMethod parse(final String mediaCode) {
        if(Strings.isNullOrEmpty(mediaCode)) {
            return null;
        }
        final String trimmedMediaCode = mediaCode.trim();

        for (final CodaPaymentMethod codaPaymentMethod : values()) {
            if(codaPaymentMethod.mediaCode.equalsIgnoreCase(trimmedMediaCode)) {
                return codaPaymentMethod;
            }
        }
        return null;
    }

    public String title() {
        return mediaCode;
    }

}
