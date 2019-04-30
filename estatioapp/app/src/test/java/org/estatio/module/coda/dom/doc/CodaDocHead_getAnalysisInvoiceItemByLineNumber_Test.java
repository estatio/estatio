package org.estatio.module.coda.dom.doc;

import java.util.Map;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;

import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;

import static org.assertj.core.api.Assertions.assertThat;

public class CodaDocHead_getAnalysisInvoiceItemByLineNumber_Test {

    CodaDocHead codaDocHead;
    @Before
    public void setUp() throws Exception {
        codaDocHead = new CodaDocHead();
        codaDocHead.setCmpCode("IT01");
        codaDocHead.setDocCode("FR-GEN");
        codaDocHead.setDocNum("12345");
        codaDocHead.setLines(new TreeSet<>());
    }

    @Test
    public void when_none() throws Exception {

        // when
        final Map<Integer, LineData> map =
                codaDocHead.getLineDataByLineNumber();

        // then
        assertThat(map).isEmpty();
    }

    @Test
    public void when_one_with_item() throws Exception {

        // given
        final CodaDocLine codaDocLine = new CodaDocLine();
        codaDocLine.setDocHead(codaDocHead);
        codaDocLine.setLineNum(2);
        codaDocLine.setLineType(LineType.ANALYSIS);

        final IncomingInvoiceItem item1 = new IncomingInvoiceItem();
        codaDocLine.setIncomingInvoiceItem(item1);

        codaDocHead.getLines().add(codaDocLine);


        // when
        final Map<Integer, LineData> map =
                codaDocHead.getLineDataByLineNumber();

        // then
        assertThat(map).hasSize(1);
        assertThat(map.get(2).getInvoiceItemIfAny()).isPresent();
        assertThat(map.get(2).getInvoiceItemIfAny().get()).isEqualTo(item1);
    }

    @Test
    public void when_one_with_no_item() throws Exception {

        // given
        final CodaDocLine codaDocLine = new CodaDocLine();
        codaDocLine.setDocHead(codaDocHead);
        codaDocLine.setLineNum(2);
        codaDocLine.setLineType(LineType.ANALYSIS);

        codaDocLine.setIncomingInvoiceItem(null);

        codaDocHead.getLines().add(codaDocLine);


        // when
        final Map<Integer, LineData> map =
                codaDocHead.getLineDataByLineNumber();

        // then
        assertThat(map).hasSize(1);
        assertThat(map.get(2).getInvoiceItemIfAny()).isNotPresent();
    }

}