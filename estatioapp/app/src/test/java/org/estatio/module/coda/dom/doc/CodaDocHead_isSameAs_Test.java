package org.estatio.module.coda.dom.doc;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

public class CodaDocHead_isSameAs_Test {

    CodaDocHead codaDocHead;
    CodaDocHead other;

    @Before
    public void setUp() throws Exception {
        codaDocHead = new CodaDocHead();
        codaDocHead.setSha256("SHA256");
        codaDocHead.setStatPay("");

        other = new CodaDocHead();
        other.setSha256(codaDocHead.getSha256()); // start off as equal
        other.setStatPay(codaDocHead.getStatPay());
    }

    @Test
    public void when_null() throws Exception {
        // given
        other = null;

        // when
        Assertions.assertThat(codaDocHead.isSameAs(other)).isFalse();
    }

    @Test
    public void when_same() throws Exception {
        // given
        other = codaDocHead;

        // when
        Assertions.assertThat(codaDocHead.isSameAs(other)).isTrue();
    }

    @Test
    public void when_equal() throws Exception {
        // given
        Assertions.assertThat(codaDocHead.getSha256()).isEqualTo(other.getSha256());
        Assertions.assertThat(codaDocHead.getStatPay()).isEqualTo(other.getStatPay());

        // when
        Assertions.assertThat(codaDocHead.isSameAs(other)).isTrue();
    }

    @Test
    public void when_different_sha256() throws Exception {
        // given
        other.setSha256("DIFFERENT_SHA256");
        Assertions.assertThat(codaDocHead.getStatPay()).isEqualTo(other.getStatPay());

        // when
        Assertions.assertThat(codaDocHead.isSameAs(other)).isFalse();
    }

    @Test
    public void when_different_statPay() throws Exception {
        // given
        Assertions.assertThat(codaDocHead.getSha256()).isEqualTo(other.getSha256());
        codaDocHead.setStatPay("");
        other.setStatPay("paid");

        // when
        Assertions.assertThat(codaDocHead.isSameAs(other)).isFalse();
    }
}