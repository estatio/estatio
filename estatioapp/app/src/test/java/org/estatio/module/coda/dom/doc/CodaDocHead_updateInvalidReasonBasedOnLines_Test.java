package org.estatio.module.coda.dom.doc;

import com.google.common.collect.Sets;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CodaDocHead_updateInvalidReasonBasedOnLines_Test {

    private CodaDocHead codaDocHead;
    private CodaDocLine line1;
    private CodaDocLine line2;

    @Before
    public void setUp() throws Exception {
        codaDocHead = new CodaDocHead("IT01","FR-GEN","12345", (short)1, null, null, null, null);
        codaDocHead.setLines(Sets.newTreeSet());

        CodaDocHead codaDocHead = this.codaDocHead;
        line1 = addLine(codaDocHead, 1);
        line2 = addLine(codaDocHead, 2);
    }

    @Test
    public void when_no_lines_are_invalid() throws Exception {
        // given
        assertThat(codaDocHead.getReasonInvalid()).isNull();

        // when
        codaDocHead.updateInvalidReasonBasedOnLines();

        // then
        assertThat(codaDocHead.getReasonInvalid()).isNull();
    }

    @Test
    public void when_some_lines_are_invalid() throws Exception {
        // given
        line1.setLineType(LineType.SUMMARY);
        line1.setReasonInvalid("line 1 is bad");
        line2.setLineType(LineType.ANALYSIS);
        line2.setReasonInvalid("line 2 is bad");
        assertThat(codaDocHead.getReasonInvalid()).isNull();

        // when
        codaDocHead.updateInvalidReasonBasedOnLines();

        // then
        assertThat(codaDocHead.getReasonInvalid()).isNotNull();
        assertThat(codaDocHead.getReasonInvalid()).isEqualTo("SUMMARY: line 1 is bad\nANALYSIS: line 2 is bad");
    }


    static CodaDocLine addLine(final CodaDocHead codaDocHead, final int lineNum) {
        final CodaDocLine docLine = new CodaDocLine();
        docLine.setLineNum(lineNum);
        docLine.setDocHead(codaDocHead);
        codaDocHead.getLines().add(docLine);
        return docLine;
    }

}