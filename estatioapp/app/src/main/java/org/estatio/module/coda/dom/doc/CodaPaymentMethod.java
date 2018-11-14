package org.estatio.module.coda.dom.doc;

import com.google.common.base.Strings;

import org.estatio.module.invoice.dom.PaymentMethod;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum CodaPaymentMethod {
    /**
     * All invoices that have this payment method must be approve
     */
    P_BON("P-BON", PaymentMethod.BANK_TRANSFER),
    /**
     * in this case the invoices are paid by SEPA, we don't need approval
     */
    P_RID("P-RID", PaymentMethod.DIRECT_DEBIT),
    /**
     * the invoices are paid with credit card, or cash, we don't need approval.
     */
    P_RIBA("P-RIBA", PaymentMethod.MANUAL_PROCESS),
    ;

    final String mediaCode;
    final PaymentMethod paymentMethod;

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

    public PaymentMethod asPaymentMethod() {
        return paymentMethod;
    }
}
