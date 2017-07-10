package org.estatio.capex.dom.payment;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.payment.approval.PaymentBatchApprovalState;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = PaymentLine.class
)
public class PaymentLineRepository {

    @Programmatic
    public List<PaymentLine> findByInvoice(final IncomingInvoice invoice) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        PaymentLine.class,
                        "findByInvoice",
                        "invoice", invoice));
    }

    @Programmatic
    public List<PaymentLine> findByInvoiceAndBatchApprovalState(
            final IncomingInvoice invoice,
            final PaymentBatchApprovalState approvalState) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        PaymentLine.class,
                        "findByInvoiceAndBatchApprovalState",
                        "invoice", invoice,
                        "approvalState", approvalState));
    }


    @Inject
    RepositoryService repositoryService;

}
