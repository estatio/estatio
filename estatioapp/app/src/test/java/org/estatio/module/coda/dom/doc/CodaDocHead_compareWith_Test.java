package org.estatio.module.coda.dom.doc;

import java.math.BigDecimal;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.module.financial.dom.BankAccount;

import static org.assertj.core.api.Assertions.assertThat;

public class CodaDocHead_compareWith_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    CodaDocHead codaDocHead;
    CodaDocLine summaryDocLine;
    CodaDocLine analysisDocLine;

    CodaDocHead existing;
    CodaDocLine existingSummaryDocLine;
    CodaDocLine existingAnalysisDocLine;

    @Before
    public void setUp() throws Exception {

        existing = new CodaDocHead("IT01", "FR-GEN", "1234", (short)1, null, null, null, null, "");

        existingSummaryDocLine = addLine(existing, 1, LineType.SUMMARY);
        existingAnalysisDocLine = addLine(existing, 2, LineType.ANALYSIS);


        codaDocHead = new CodaDocHead("IT01", "FR-GEN", "1234", (short)2, null, null, null, null, "");

        summaryDocLine = addLine(codaDocHead, 1, LineType.SUMMARY);
        analysisDocLine = addLine(codaDocHead, 2, LineType.ANALYSIS);
    }

    CodaDocLine addLine(final CodaDocHead docHead, final int lineNum, final LineType lineType) {
        CodaDocLine codaDocLine = new CodaDocLine(docHead, lineNum, lineType, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        docHead.getLines().add(codaDocLine);
        return codaDocLine;
    }

    @Test
    public void when_no_previous() throws Exception {

        // when
        CodaDocHead.Comparison comparison = codaDocHead.compareWith(null);

        // then
        assertThat(comparison.getType()).isEqualTo(CodaDocHead.Comparison.Type.NO_PREVIOUS);
    }

    @Test
    public void when_same() throws Exception {
        // when
        CodaDocHead.Comparison comparison = codaDocHead.compareWith(codaDocHead);

        // then
        assertThat(comparison.getType()).isEqualTo(CodaDocHead.Comparison.Type.SAME);
    }

    @Test
    public void when_differs_but_same_by_timestamp() throws Exception {
        // given
        existing.setCodaTimeStamp((short)2);

        // when
        CodaDocHead.Comparison comparison = codaDocHead.compareWith(existing);

        // then
        assertThat(comparison.getType()).isEqualTo(CodaDocHead.Comparison.Type.SAME);
    }

    @Test
    public void when_replacement_missing_summary_line() throws Exception {

        // given
        codaDocHead.getLines().remove(summaryDocLine);

        // when
        CodaDocHead.Comparison comparison = codaDocHead.compareWith(existing);

        // then
        assertThat(comparison.getType()).isEqualTo(CodaDocHead.Comparison.Type.DIFFERS_INVALIDATING_APPROVALS);
        assertThat(comparison.getReason()).isEqualTo("Replacement has no summary doc line");
    }


    @Test
    public void when_replacement_adds_summary_line() throws Exception {
        // given
        existing.getLines().remove(existingSummaryDocLine);

        // when
        CodaDocHead.Comparison comparison = codaDocHead.compareWith(existing);

        // then
        assertThat(comparison.getType()).isEqualTo(CodaDocHead.Comparison.Type.DIFFERS_INVALIDATING_APPROVALS);
        assertThat(comparison.getReason()).isEqualTo("Previous had no summary doc line");
    }

    @Test
    public void when_supplier_bank_account_differs() throws Exception {

        // given
        summaryDocLine.setSupplierBankAccount(new BankAccount());
        existingSummaryDocLine.setSupplierBankAccount(new BankAccount());

        // when
        CodaDocHead.Comparison comparison = codaDocHead.compareWith(existing);

        // then
        assertThat(comparison.getType()).isEqualTo(CodaDocHead.Comparison.Type.DIFFERS_INVALIDATING_APPROVALS);
        assertThat(comparison.getReason()).isEqualTo("Supplier bank account has changed");
    }

    @Test
    public void when_doc_sum_tax_differs() throws Exception {

        // given

        summaryDocLine.setDocSumTax(BigDecimal.ONE);
        existingSummaryDocLine.setDocSumTax(BigDecimal.TEN);

        // when
        CodaDocHead.Comparison comparison = codaDocHead.compareWith(existing);

        // then
        assertThat(comparison.getType()).isEqualTo(CodaDocHead.Comparison.Type.DIFFERS_INVALIDATING_APPROVALS);
        assertThat(comparison.getReason()).isEqualTo("Doc sum tax (VAT amount) has changed");
    }


    @Test
    public void when_doc_value_differs() throws Exception {

        // given
        summaryDocLine.setDocValue(BigDecimal.ONE);
        existingSummaryDocLine.setDocValue(BigDecimal.TEN);

        // when
        CodaDocHead.Comparison comparison = codaDocHead.compareWith(existing);

        // then
        assertThat(comparison.getType()).isEqualTo(CodaDocHead.Comparison.Type.DIFFERS_INVALIDATING_APPROVALS);
        assertThat(comparison.getReason()).isEqualTo("Doc value (gross amount) has changed");
    }


    @Test
    public void when_media_code_differs() throws Exception {

        // given
        summaryDocLine.setMediaCode("X");
        existingSummaryDocLine.setMediaCode("Y");

        // when
        CodaDocHead.Comparison comparison = codaDocHead.compareWith(existing);

        // then
        assertThat(comparison.getType()).isEqualTo(CodaDocHead.Comparison.Type.DIFFERS_INVALIDATING_APPROVALS);
        assertThat(comparison.getReason()).isEqualTo("Media code (payment method) has changed");
    }


    @Test
    public void when_due_date_differs() throws Exception {

        // given
        summaryDocLine.setDueDate(LocalDate.now());
        existingSummaryDocLine.setDueDate(LocalDate.now().plusDays(-1));

        // when
        CodaDocHead.Comparison comparison = codaDocHead.compareWith(existing);

        // then
        assertThat(comparison.getType()).isEqualTo(CodaDocHead.Comparison.Type.DIFFERS_INVALIDATING_APPROVALS);
        assertThat(comparison.getReason()).isEqualTo("Due date has changed");
    }


    @Test
    public void when_value_date_differs() throws Exception {

        // given
        summaryDocLine.setValueDate(LocalDate.now());
        existingSummaryDocLine.setValueDate(LocalDate.now().plusDays(-1));

        // when
        CodaDocHead.Comparison comparison = codaDocHead.compareWith(existing);

        // then
        assertThat(comparison.getType()).isEqualTo(CodaDocHead.Comparison.Type.DIFFERS_INVALIDATING_APPROVALS);
        assertThat(comparison.getReason()).isEqualTo("Value date has changed");
    }


    @Test
    public void when_user_ref_1_differs() throws Exception {

        // given
        summaryDocLine.setUserRef1("10010010");
        existingSummaryDocLine.setUserRef1("20020020");

        // when
        CodaDocHead.Comparison comparison = codaDocHead.compareWith(existing);

        // then
        assertThat(comparison.getType()).isEqualTo(CodaDocHead.Comparison.Type.DIFFERS_INVALIDATING_APPROVALS);
        assertThat(comparison.getReason()).isEqualTo("User Ref 1 (bar code) has changed");
    }


    @Test
    public void when_description_differs() throws Exception {

        // given
        summaryDocLine.setDescription("some description");
        existingSummaryDocLine.setDescription("some previous description");

        // when
        CodaDocHead.Comparison comparison = codaDocHead.compareWith(existing);

        // then
        assertThat(comparison.getType()).isEqualTo(CodaDocHead.Comparison.Type.DIFFERS_INVALIDATING_APPROVALS);
        assertThat(comparison.getReason()).isEqualTo("Description has changed");
    }


    @Test
    public void when_some_other_difference() throws Exception {

        // given
        assertThat(codaDocHead.getCodaTimeStamp()).isNotEqualTo(existing.getCodaTimeStamp());

        // when
        CodaDocHead.Comparison comparison = codaDocHead.compareWith(existing);

        // then
        assertThat(comparison.getType()).isEqualTo(CodaDocHead.Comparison.Type.DIFFERS_RETAIN_APPROVALS);
    }

}