package org.estatio.module.capex.dom.invoice;

import java.util.Arrays;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalState;

public class IncomingInvoiceRepository_Test {

    @Test
    public void construct_Sql_for_credit_notes_test() {

        // given
        IncomingInvoiceRepository incomingInvoiceRepository = new IncomingInvoiceRepository();

        // and when
        String sql = incomingInvoiceRepository.constructSqlForCreditNotesInStateOf(
                Arrays.asList(IncomingInvoiceApprovalState.COMPLETED));
        // then
        Assertions.assertThat(sql).isEqualTo("SELECT TOP 1 I.id FROM Invoice I WHERE I.bankAccountId = :bankAccountId AND I.netAmount < 0 AND I.approvalState IN ('COMPLETED')");

        // when
        sql = incomingInvoiceRepository.constructSqlForCreditNotesInStateOf(
                Arrays.asList(IncomingInvoiceApprovalState.COMPLETED, IncomingInvoiceApprovalState.APPROVED));
        // then
        Assertions.assertThat(sql).isEqualTo("SELECT TOP 1 I.id FROM Invoice I WHERE I.bankAccountId = :bankAccountId AND I.netAmount < 0 AND I.approvalState IN ('COMPLETED', 'APPROVED')");

        // and when
        sql = incomingInvoiceRepository.constructSqlForCreditNotesInStateOf(
                Arrays.asList());
        // then
        Assertions.assertThat(sql).isEqualTo("SELECT TOP 1 I.id FROM Invoice I WHERE I.bankAccountId = :bankAccountId AND I.netAmount < 0");

        // and when
        sql = incomingInvoiceRepository.constructSqlForCreditNotesInStateOf(
                null);
        // then
        Assertions.assertThat(sql).isEqualTo("SELECT TOP 1 I.id FROM Invoice I WHERE I.bankAccountId = :bankAccountId AND I.netAmount < 0");

    }

}