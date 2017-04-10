package org.estatio.capex.dom.invoice;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.capex.dom.charge.IncomingCharge;
import org.estatio.dom.asset.Property;
import org.estatio.dom.project.Project;
import org.estatio.dom.tax.Tax;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = IncomingInvoiceItem.class
)
public class IncomingInvoiceItemRepository {

    @Programmatic
    public IncomingInvoiceItem findByInvoiceAndIncomingCharge(final IncomingInvoice invoice, final IncomingCharge incomingCharge) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        IncomingInvoiceItem.class,
                        "findByInvoiceAndIncomingCharge",
                        "invoice", invoice,
                        "incomingCharge", incomingCharge
                ));
    }


    @Programmatic
    public IncomingInvoiceItem create(
            final IncomingInvoice invoice,
            final IncomingCharge charge,
            final String description,
            final BigDecimal netAmount,
            final BigDecimal vatAmount,
            final BigDecimal grossAmount,
            final Tax tax,
            final LocalDate startDate,
            final LocalDate endDate,
            final Property property,
            final Project project) {
        final IncomingInvoiceItem invoiceItem = IncomingInvoiceItem.builder()
                .invoice(invoice)
                .incomingCharge(charge)
                .description(description)
                .netAmount(netAmount)
                .vatAmount(vatAmount)
                .grossAmount(grossAmount)
                .tax(tax)
                .startDate(startDate)
                .endDate(endDate)
                .property(property)
                .project(project)
                .build();
        serviceRegistry2.injectServicesInto(invoiceItem);
        repositoryService.persistAndFlush(invoiceItem);
        return invoiceItem;
    }

    @Programmatic
    public IncomingInvoiceItem findOrCreate(
            final IncomingInvoice invoice,
            final IncomingCharge charge,
            final String description,
            final BigDecimal netAmount,
            final BigDecimal vatAmount,
            final BigDecimal grossAmount,
            final Tax tax,
            final LocalDate startDate,
            final LocalDate endDate,
            final Property property,
            final Project project
    ) {
        IncomingInvoiceItem invoiceItem = findByInvoiceAndIncomingCharge(invoice, charge);
        if (invoiceItem == null) {
            invoiceItem = create(invoice, charge, description, netAmount, vatAmount, grossAmount, tax, startDate, endDate,
                    property, project);
        }
        return invoiceItem;
    }


    @Inject
    RepositoryService repositoryService;
    @Inject
    ServiceRegistry2 serviceRegistry2;
}
