package org.estatio.module.coda.dom.doc;

import java.util.Map;

import com.google.common.collect.Sets;

import org.junit.Before;
import org.junit.Test;

import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.charge.dom.Charge;

import static org.assertj.core.api.Assertions.assertThat;

public class CodaDocHead_getAnalysisLineDataByLineNumber_Test {

    CodaDocHead codaDocHead;
    boolean codaDocHeadLegacyState;

    CodaDocLine codaDocLine;

    IncomingInvoice invoice;
    IncomingInvoiceItem invoiceItem;

    Charge charge;

    Project project;

    @Before
    public void setUp() throws Exception {
        codaDocHead = new CodaDocHead() {
            @Override boolean isLegacy() {
                return codaDocHeadLegacyState;
            }
        };
        codaDocHead.setCmpCode("IT01");
        codaDocHead.setDocCode("FR-GEN");
        codaDocHead.setDocNum("12345");
        codaDocHead.setLines(Sets.newTreeSet());

        codaDocLine = new CodaDocLine();

        invoice = new IncomingInvoice();
        invoiceItem = new IncomingInvoiceItem();
        invoice.setItems(Sets.newTreeSet());
        invoice.getItems().add(invoiceItem);

        charge = new Charge();
        project = new Project();

    }

    @Test
    public void nonLegacy_and_none() throws Exception {

        // given
        codaDocHeadLegacyState = false;

        // when
        final Map<Integer, LineData> map =
                codaDocHead.getAnalysisLineDataByLineNumber();

        // then
        assertThat(map).isEmpty();
    }

    @Test
    public void nonLegacy_and_one_with_item() throws Exception {

        // given
        codaDocHeadLegacyState = false;
        final CodaDocLine codaDocLine = new CodaDocLine();
        codaDocLine.setDocHead(codaDocHead);
        codaDocLine.setLineNum(2);
        codaDocLine.setLineType(LineType.ANALYSIS);

        codaDocLine.setIncomingInvoiceItem(invoiceItem);
        codaDocLine.setExtRefWorkTypeCharge(charge);
        codaDocLine.setExtRefProject(project);

        codaDocHead.getLines().add(codaDocLine);


        // when
        final Map<Integer, LineData> map =
                codaDocHead.getAnalysisLineDataByLineNumber();

        // then
        assertThat(map).hasSize(1);
        assertThat(map).containsKey(2);
        assertThat(map.get(2).getInvoiceItemIfAny()).isPresent();
        assertThat(map.get(2).getInvoiceItemIfAny().get()).isSameAs(invoiceItem);
        assertThat(map.get(2).getChargeIfAny()).isPresent();
        assertThat(map.get(2).getChargeIfAny().get()).isSameAs(charge);
        assertThat(map.get(2).getProjectIfAny()).isPresent();
        assertThat(map.get(2).getProjectIfAny().get()).isSameAs(project);
    }

    @Test
    public void nonLegacy_and_with_no_item() throws Exception {

        // given
        codaDocHeadLegacyState = false;
        final CodaDocLine codaDocLine = new CodaDocLine();
        codaDocLine.setDocHead(codaDocHead);
        codaDocLine.setLineNum(2);
        codaDocLine.setLineType(LineType.ANALYSIS);

        codaDocLine.setIncomingInvoiceItem(null);
        codaDocLine.setExtRefWorkTypeCharge(null);
        codaDocLine.setExtRefProject(null);

        codaDocHead.getLines().add(codaDocLine);


        // when
        final Map<Integer, LineData> map =
                codaDocHead.getAnalysisLineDataByLineNumber();

        // then
        assertThat(map).hasSize(1);
        assertThat(map).containsKey(2);
        assertThat(map.get(2).getInvoiceItemIfAny()).isNotPresent();
        assertThat(map.get(2).getChargeIfAny()).isNotPresent();
        assertThat(map.get(2).getProjectIfAny()).isNotPresent();
    }

    @Test
    public void legacy_and_none() throws Exception {

        // given
        codaDocHeadLegacyState = true;

        // when
        final Map<Integer, LineData> map =
                codaDocHead.getAnalysisLineDataByLineNumber();

        // then
        assertThat(map).isEmpty();
    }

    @Test
    public void legacy_and_one_with_item() throws Exception {

        // given
        codaDocHeadLegacyState = true;
        final CodaDocLine codaDocLine = new CodaDocLine();
        codaDocLine.setDocHead(codaDocHead);
        codaDocLine.setLineNum(1);
        codaDocLine.setLineType(LineType.SUMMARY);

        codaDocHead.setIncomingInvoice(invoice);

        codaDocLine.setIncomingInvoiceItem(null); // will infer item from the parent incoming invoice
        codaDocLine.setExtRefWorkTypeCharge(charge);
        codaDocLine.setExtRefProject(project);

        codaDocHead.getLines().add(codaDocLine);


        // when
        final Map<Integer, LineData> map =
                codaDocHead.getAnalysisLineDataByLineNumber();

        // then
        assertThat(map).hasSize(1);
        assertThat(map).containsKey(2);
        assertThat(map.get(2).getInvoiceItemIfAny()).isPresent();
        assertThat(map.get(2).getInvoiceItemIfAny().get()).isSameAs(invoiceItem);
        assertThat(map.get(2).getChargeIfAny()).isPresent();
        assertThat(map.get(2).getChargeIfAny().get()).isSameAs(charge);
        assertThat(map.get(2).getProjectIfAny()).isPresent();
        assertThat(map.get(2).getProjectIfAny().get()).isSameAs(project);
    }

    @Test
    public void legacy_and_with_no_item() throws Exception {

        // given
        codaDocHeadLegacyState = true;

        codaDocLine.setDocHead(codaDocHead);
        codaDocLine.setLineNum(1);
        codaDocLine.setLineType(LineType.SUMMARY);

        codaDocLine.setIncomingInvoiceItem(null);
        codaDocLine.setExtRefWorkTypeCharge(null);
        codaDocLine.setExtRefProject(null);

        codaDocHead.getLines().add(codaDocLine);


        // when
        final Map<Integer, LineData> map =
                codaDocHead.getAnalysisLineDataByLineNumber();

        // then
        assertThat(map).hasSize(1);
        assertThat(map).containsKey(2);
        assertThat(map.get(2).getInvoiceItemIfAny()).isNotPresent();
        assertThat(map.get(2).getChargeIfAny()).isNotPresent();
        assertThat(map.get(2).getProjectIfAny()).isNotPresent();
    }

}