package org.estatio.module.coda.dom.doc;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class CodaPaymentMethod_parse_Test {

    @Test
    public void happy_cases() throws Exception {
        assertParse("P-BON", CodaPaymentMethod.P_BON);
        assertParse("P-RIBA", CodaPaymentMethod.P_RIBA);
        assertParse("P-RID", CodaPaymentMethod.P_RID);

        assertParse("  P-BON", CodaPaymentMethod.P_BON);
        assertParse("P-RIBA  ", CodaPaymentMethod.P_RIBA);
        assertParse("  P-RID  ", CodaPaymentMethod.P_RID);

        assertParse("p-bon", CodaPaymentMethod.P_BON);
        assertParse("p-riba", CodaPaymentMethod.P_RIBA);
        assertParse("p-rid", CodaPaymentMethod.P_RID);
    }

    @Test
    public void sad_cases() throws Exception {
        assertParse(null, null);
        assertParse("", null);
        assertParse("XX", null);
    }

    void assertParse(
            final String mediaCode,
            final CodaPaymentMethod expected) {
        Assertions.assertThat(CodaPaymentMethod.parse(mediaCode)).isEqualTo(expected);
    }
}