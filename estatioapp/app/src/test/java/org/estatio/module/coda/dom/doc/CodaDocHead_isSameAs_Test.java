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
        codaDocHead.setCodaTimeStamp((short)3);
        codaDocHead.setStatPay("");

        other = new CodaDocHead();
        other.setCodaTimeStamp((short)3);
        other.setStatPay("");
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
        Assertions.assertThat(codaDocHead.getCodaTimeStamp()).isEqualTo(other.getCodaTimeStamp());
        Assertions.assertThat(codaDocHead.getStatPay()).isEqualTo(other.getStatPay());

        // when
        Assertions.assertThat(codaDocHead.isSameAs(other)).isTrue();
    }

    @Test
    public void when_different_timestamp() throws Exception {
        // given
        other.setCodaTimeStamp((short) (codaDocHead.getCodaTimeStamp() + 1));
        Assertions.assertThat(codaDocHead.getStatPay()).isEqualTo(other.getStatPay());

        // when
        Assertions.assertThat(codaDocHead.isSameAs(other)).isFalse();
    }

    @Test
    public void when_different_statPay() throws Exception {
        // given
        Assertions.assertThat(codaDocHead.getCodaTimeStamp()).isEqualTo(other.getCodaTimeStamp());
        codaDocHead.setStatPay("");
        other.setStatPay("paid");

        // when
        Assertions.assertThat(codaDocHead.isSameAs(other)).isFalse();
    }
}