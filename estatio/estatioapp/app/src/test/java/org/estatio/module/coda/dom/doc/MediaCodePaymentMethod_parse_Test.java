package org.estatio.module.coda.dom.doc;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class MediaCodePaymentMethod_parse_Test {

    @Test
    public void happy_cases() throws Exception {
        assertParse("P-BON", MediaCodePaymentMethod.P_BON);
        assertParse("P-RIBA", MediaCodePaymentMethod.P_RIBA);
        assertParse("P-RID", MediaCodePaymentMethod.P_RID);

        assertParse("  P-BON", MediaCodePaymentMethod.P_BON);
        assertParse("P-RIBA  ", MediaCodePaymentMethod.P_RIBA);
        assertParse("  P-RID  ", MediaCodePaymentMethod.P_RID);

        assertParse("p-bon", MediaCodePaymentMethod.P_BON);
        assertParse("p-riba", MediaCodePaymentMethod.P_RIBA);
        assertParse("p-rid", MediaCodePaymentMethod.P_RID);
    }

    @Test
    public void sad_cases() throws Exception {
        assertParse(null, null);
        assertParse("", null);
        assertParse("XX", null);
    }

    void assertParse(
            final String mediaCode,
            final MediaCodePaymentMethod expected) {
        Assertions.assertThat(MediaCodePaymentMethod.parse(mediaCode)).isEqualTo(expected);
    }
}