package org.estatio.module.coda.dom.doc;

import java.math.BigDecimal;

import com.google.common.collect.Sets;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CodaDocHead_isLegacy_Test {

    CodaDocHead codaDocHead;
    CodaDocLine summaryLine;
    CodaDocLine analysisLine;

    @Before
    public void setUp() throws Exception {
        codaDocHead = new CodaDocHead();
        codaDocHead.setCmpCode("IT01");
        codaDocHead.setDocCode("FR-GEN");
        codaDocHead.setDocNum("12345");
        codaDocHead.setLines(Sets.newTreeSet());

        summaryLine = newLine(this.codaDocHead, 1, LineType.SUMMARY);
        analysisLine = newLine(this.codaDocHead, 2, LineType.ANALYSIS);
    }

    private CodaDocLine newLine(
            final CodaDocHead codaDocHead,
            final int lineNum,
            final LineType lineType) {
        final CodaDocLine line = new CodaDocLine();
        line.setLineType(lineType);
        line.setDocHead(codaDocHead);
        line.setLineNum(lineNum);

        codaDocHead.getLines().add(line);
        return line;
    }

    @Test
    public void given_no_doc_value_on_analysis_line_then_treat_as_legacy() throws Exception {

        // given
        analysisLine.setDocValue(null);

        // when, then
        assertThat(codaDocHead.isLegacyAnalysisLineWithNullDocValue()).isTrue();

    }

    @Test
    public void given_non_null_value_on_analysis_line_then_not_legacy() throws Exception {

        // given
        analysisLine.setDocValue(BigDecimal.ONE);

        // when, then
        assertThat(codaDocHead.isLegacyAnalysisLineWithNullDocValue()).isFalse();
    }

    @Test
    public void given_zero_value_on_analysis_line_then_not_legacy() throws Exception {

        // given
        analysisLine.setDocValue(BigDecimal.ZERO);  // can even be zero, still not legacy.

        // when, then
        assertThat(codaDocHead.isLegacyAnalysisLineWithNullDocValue()).isFalse();
    }

    @Test
    public void given_no_lines_then_not_legacy() throws Exception {
        // given
        codaDocHead.getLines().clear();

        // when, then
        assertThat(codaDocHead.isLegacyAnalysisLineWithNullDocValue()).isFalse();
    }

}