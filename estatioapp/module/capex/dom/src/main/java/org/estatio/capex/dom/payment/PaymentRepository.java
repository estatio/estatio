package org.estatio.capex.dom.payment;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.payment.Payment;
import org.estatio.dom.invoice.PaymentMethod;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = Payment.class
)
public class PaymentRepository {

    @Programmatic
    public java.util.List<Payment> listAll() {
        return repositoryService.allInstances(Payment.class);
    }

    @Programmatic
    public List<Payment> findByInvoice(
            final IncomingInvoice invoice
    ) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        Payment.class,
                        "findByInvoice",
                        "invoice", invoice));
    }

    @Programmatic
    public Payment create(final BigDecimal amount, final IncomingInvoice invoice, final PaymentMethod paymentMethod) {
        final Payment payment = new Payment(amount, invoice, paymentMethod);
        serviceRegistry2.injectServicesInto(payment);
        repositoryService.persistAndFlush(payment);
        return payment;
    }

    @Inject
    RepositoryService repositoryService;

    @Inject
    ServiceRegistry2 serviceRegistry2;
}
