package org.estatio.module.coda.dom.doc;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CodaDocHead_isSameAs_Test {

    CodaDocHead codaDocHead;
    boolean codaDocHeadLegacyState;
    boolean codaDocHeadLegacyFrArt17State;

    CodaDocHead otherDocHead;
    boolean otherDocHeadLegacyState;
    boolean otherDocHeadLegacyFrArt17State;

    @Before
    public void setUp() throws Exception {
        codaDocHead = new CodaDocHead() {
            @Override boolean isLegacyAnalysisLineWithNullDocValue() {
                return codaDocHeadLegacyState;
            }

            @Override boolean isLegacyAnalysisLineForFrArt17WithNonZeroTax() {
                return codaDocHeadLegacyFrArt17State;
            }
        };
        codaDocHead.setSha256("SHA256");
        codaDocHead.setStatPay("");
        codaDocHeadLegacyState = false;

        otherDocHead = new CodaDocHead() {
            @Override boolean isLegacyAnalysisLineWithNullDocValue() {
                return otherDocHeadLegacyState;
            }

            @Override boolean isLegacyAnalysisLineForFrArt17WithNonZeroTax() {
                return otherDocHeadLegacyFrArt17State;
            }
        };
        otherDocHead.setSha256(codaDocHead.getSha256()); // start off as equal
        otherDocHead.setStatPay(codaDocHead.getStatPay());
        otherDocHeadLegacyState = false;
    }

    @Test
    public void when_null() throws Exception {
        // given
        otherDocHead = null;

        // when
        assertThat(codaDocHead.isSameAs(otherDocHead)).isFalse();
    }

    @Test
    public void when_same() throws Exception {
        // given
        otherDocHead = codaDocHead;

        // when
        assertThat(codaDocHead.isSameAs(otherDocHead)).isTrue();
    }

    @Test
    public void when_equal() throws Exception {
        // given
        assertThat(codaDocHead.getSha256()).isEqualTo(otherDocHead.getSha256());
        assertThat(codaDocHead.getStatPay()).isEqualTo(otherDocHead.getStatPay());
        assertThat(codaDocHead.isLegacyAnalysisLineWithNullDocValue()).isEqualTo(otherDocHead.isLegacyAnalysisLineWithNullDocValue());

        // when
        assertThat(codaDocHead.isSameAs(otherDocHead)).isTrue();
    }

    @Test
    public void when_different_sha256() throws Exception {
        // given
        otherDocHead.setSha256("DIFFERENT_SHA256");
        assertThat(codaDocHead.getStatPay()).isEqualTo(otherDocHead.getStatPay());
        assertThat(codaDocHead.isLegacyAnalysisLineWithNullDocValue()).isEqualTo(otherDocHead.isLegacyAnalysisLineWithNullDocValue());

        // when
        assertThat(codaDocHead.isSameAs(otherDocHead)).isFalse();
    }

    @Test
    public void when_different_statPay() throws Exception {
        // given
        assertThat(codaDocHead.getSha256()).isEqualTo(otherDocHead.getSha256());
        codaDocHead.setStatPay("");
        otherDocHead.setStatPay("paid");
        assertThat(codaDocHead.isLegacyAnalysisLineWithNullDocValue()).isEqualTo(otherDocHead.isLegacyAnalysisLineWithNullDocValue());

        // when
        assertThat(codaDocHead.isSameAs(otherDocHead)).isFalse();
    }

    @Test
    public void when_different_legacyState_1() throws Exception {
        // given
        assertThat(codaDocHead.getSha256()).isEqualTo(otherDocHead.getSha256());
        assertThat(codaDocHead.getStatPay()).isEqualTo(otherDocHead.getStatPay());

        codaDocHeadLegacyState = false;
        otherDocHeadLegacyState = true;

        // when, then
        assertThat(codaDocHead.isSameAs(otherDocHead)).isFalse();
    }

    @Test
    public void when_different_legacyState_2() throws Exception {
        // given
        assertThat(codaDocHead.getSha256()).isEqualTo(otherDocHead.getSha256());
        assertThat(codaDocHead.getStatPay()).isEqualTo(otherDocHead.getStatPay());

        codaDocHeadLegacyState = true;
        otherDocHeadLegacyState = false;

        // when, then
        assertThat(codaDocHead.isSameAs(otherDocHead)).isFalse();
    }

    @Test
    public void when_different_legacyStateFrArt17_1() throws Exception {
        // given
        assertThat(codaDocHead.getSha256()).isEqualTo(otherDocHead.getSha256());
        assertThat(codaDocHead.getStatPay()).isEqualTo(otherDocHead.getStatPay());

        codaDocHeadLegacyFrArt17State = false;
        otherDocHeadLegacyFrArt17State = true;

        // when, then
        assertThat(codaDocHead.isSameAs(otherDocHead)).isFalse();
    }

    @Test
    public void when_different_legacyStateFrArt17_2() throws Exception {
        // given
        assertThat(codaDocHead.getSha256()).isEqualTo(otherDocHead.getSha256());
        assertThat(codaDocHead.getStatPay()).isEqualTo(otherDocHead.getStatPay());

        codaDocHeadLegacyFrArt17State = true;
        otherDocHeadLegacyFrArt17State = false;

        // when, then
        assertThat(codaDocHead.isSameAs(otherDocHead)).isFalse();
    }

}