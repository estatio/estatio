package org.estatio.module.capex.dom.util;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class DocumentNameUtilTest {

    @Test
    public void stripPdfSuffixFromDocumentName() {


        // when, then
        // normal stuff
        Assertions.assertThat(DocumentNameUtil.stripPdfSuffixFromDocumentName("S123456789.PDF")).isEqualTo("S123456789");
        Assertions.assertThat(DocumentNameUtil.stripPdfSuffixFromDocumentName("S123456789.pdf")).isEqualTo("S123456789");
        Assertions.assertThat(DocumentNameUtil.stripPdfSuffixFromDocumentName("S123456789.pDf")).isEqualTo("S123456789");
        Assertions.assertThat(DocumentNameUtil.stripPdfSuffixFromDocumentName("123456789.pDf")).isEqualTo("123456789");

        // weird stuff
        Assertions.assertThat(DocumentNameUtil.stripPdfSuffixFromDocumentName("123456789.xml")).isEqualTo("123456789.xml");
        Assertions.assertThat(DocumentNameUtil.stripPdfSuffixFromDocumentName("123456789..pDf")).isEqualTo("123456789.");
        Assertions.assertThat(DocumentNameUtil.stripPdfSuffixFromDocumentName("123456789..pDfX")).isEqualTo("123456789.X");
        Assertions.assertThat(DocumentNameUtil.stripPdfSuffixFromDocumentName("S123456789.pD")).isEqualTo("S123456789.pD");
        Assertions.assertThat(DocumentNameUtil.stripPdfSuffixFromDocumentName("S123456789pDf")).isEqualTo("S123456789pDf");

    }
}