package org.estatio.module.coda.dom.doc;

import java.math.BigDecimal;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.module.financial.dom.BankAccount;

import static org.assertj.core.api.Assertions.assertThat;

public class CodaDocHead_compareWithPrevious_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    CodaDocHeadRepository mockCodaDocHeadRepository;

    CodaDocHead codaDocHead;
    CodaDocLine summaryDocLine;
    CodaDocLine analysisDocLine;

    CodaDocHead existing;
    CodaDocLine existingSummaryDocLine;
    CodaDocLine existingAnalysisDocLine;

    @Before
    public void setUp() throws Exception {

        existing = new CodaDocHead("IT01", "FR-GEN", "1234", (short)1, null, null, null, null, "");
        existing.codaDocHeadRepository = mockCodaDocHeadRepository;

        existingSummaryDocLine = addLine(existing, 1, LineType.SUMMARY);
        existingAnalysisDocLine = addLine(existing, 2, LineType.ANALYSIS);


        codaDocHead = new CodaDocHead("IT01", "FR-GEN", "1234", (short)2, null, null, null, null, "");
        codaDocHead.codaDocHeadRepository = mockCodaDocHeadRepository;

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
        // expecting
        context.checking(new Expectations() {{
            allowing(mockCodaDocHeadRepository).findByCandidate(codaDocHead);
            will(returnValue(null));
        }});

        // when
        CodaDocHead.Comparison comparison = codaDocHead.compareWithPrevious();

        // then
        assertThat(comparison.getType()).isEqualTo(CodaDocHead.Comparison.Type.NO_PREVIOUS);
    }

    @Test
    public void when_same() throws Exception {
        // expecting
        context.checking(new Expectations() {{
            allowing(mockCodaDocHeadRepository).findByCandidate(codaDocHead);
            will(returnValue(codaDocHead));
        }});

        // when
        CodaDocHead.Comparison comparison = codaDocHead.compareWithPrevious();

        // then
        assertThat(comparison.getType()).isEqualTo(CodaDocHead.Comparison.Type.SAME);
    }

    @Test
    public void when_differs_but_same_by_timestamp() throws Exception {
        // given
        existing.setCodaTimeStamp((short)2);

        // expecting
        context.checking(new Expectations() {{
            allowing(mockCodaDocHeadRepository).findByCandidate(codaDocHead);
            will(returnValue(existing));
        }});

        // when
        CodaDocHead.Comparison comparison = codaDocHead.compareWithPrevious();

        // then
        assertThat(comparison.getType()).isEqualTo(CodaDocHead.Comparison.Type.SAME);
    }

    @Test
    public void when_replacement_missing_summary_line() throws Exception {

        // given
        codaDocHead.getLines().remove(summaryDocLine);

        // expecting
        context.checking(new Expectations() {{
            allowing(mockCodaDocHeadRepository).findByCandidate(codaDocHead);
            will(returnValue(existing));
        }});

        // when
        CodaDocHead.Comparison comparison = codaDocHead.compareWithPrevious();

        // then
        assertThat(comparison.getType()).isEqualTo(CodaDocHead.Comparison.Type.DIFFERS_INVALIDATING_APPROVALS);
        assertThat(comparison.getReason()).isEqualTo("Replacement has no summary doc line");
    }


    @Test
    public void when_replacement_adds_summary_line() throws Exception {
        // given
        existing.getLines().remove(existingSummaryDocLine);

        // expecting
        context.checking(new Expectations() {{
            allowing(mockCodaDocHeadRepository).findByCandidate(codaDocHead);
            will(returnValue(existing));
        }});

        // when
        CodaDocHead.Comparison comparison = codaDocHead.compareWithPrevious();

        // then
        assertThat(comparison.getType()).isEqualTo(CodaDocHead.Comparison.Type.DIFFERS_INVALIDATING_APPROVALS);
        assertThat(comparison.getReason()).isEqualTo("Previous had no summary doc line");
    }

    @Test
    public void when_supplier_bank_account_differs() throws Exception {

        // given
        summaryDocLine.setSupplierBankAccount(new BankAccount());
        existingSummaryDocLine.setSupplierBankAccount(new BankAccount());


        // expecting
        context.checking(new Expectations() {{
            allowing(mockCodaDocHeadRepository).findByCandidate(codaDocHead);
            will(returnValue(existing));
        }});

        // when
        CodaDocHead.Comparison comparison = codaDocHead.compareWithPrevious();

        // then
        assertThat(comparison.getType()).isEqualTo(CodaDocHead.Comparison.Type.DIFFERS_INVALIDATING_APPROVALS);
        assertThat(comparison.getReason()).isEqualTo("Supplier bank account has changed");
    }

    @Test
    public void when_doc_sum_tax_differs() throws Exception {

        // given

        summaryDocLine.setDocSumTax(BigDecimal.ONE);
        existingSummaryDocLine.setDocSumTax(BigDecimal.TEN);


        // expecting
        context.checking(new Expectations() {{
            allowing(mockCodaDocHeadRepository).findByCandidate(codaDocHead);
            will(returnValue(existing));
        }});

        // when
        CodaDocHead.Comparison comparison = codaDocHead.compareWithPrevious();

        // then
        assertThat(comparison.getType()).isEqualTo(CodaDocHead.Comparison.Type.DIFFERS_INVALIDATING_APPROVALS);
        assertThat(comparison.getReason()).isEqualTo("VAT amount has changed");
    }


    @Test
    public void when_doc_value_differs() throws Exception {

        // given

        summaryDocLine.setDocValue(BigDecimal.ONE);
        existingSummaryDocLine.setDocValue(BigDecimal.TEN);


        // expecting
        context.checking(new Expectations() {{
            allowing(mockCodaDocHeadRepository).findByCandidate(codaDocHead);
            will(returnValue(existing));
        }});

        // when
        CodaDocHead.Comparison comparison = codaDocHead.compareWithPrevious();

        // then
        assertThat(comparison.getType()).isEqualTo(CodaDocHead.Comparison.Type.DIFFERS_INVALIDATING_APPROVALS);
        assertThat(comparison.getReason()).isEqualTo("Gross amount has changed");
    }


    @Test
    public void when_some_other_difference() throws Exception {

        // given
        assertThat(codaDocHead.getCodaTimeStamp()).isNotEqualTo(existing.getCodaTimeStamp());

        // expecting
        context.checking(new Expectations() {{
            allowing(mockCodaDocHeadRepository).findByCandidate(codaDocHead);
            will(returnValue(existing));
        }});

        // when
        CodaDocHead.Comparison comparison = codaDocHead.compareWithPrevious();

        // then
        assertThat(comparison.getType()).isEqualTo(CodaDocHead.Comparison.Type.DIFFERS_RETAIN_APPROVALS);
    }

}