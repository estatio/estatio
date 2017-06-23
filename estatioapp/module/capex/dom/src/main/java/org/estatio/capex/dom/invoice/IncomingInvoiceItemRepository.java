package org.estatio.capex.dom.invoice;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.capex.dom.project.Project;
import org.estatio.dom.asset.Property;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.tax.Tax;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = IncomingInvoiceItem.class
)
public class IncomingInvoiceItemRepository {

    @Programmatic
    public IncomingInvoiceItem findByInvoiceAndCharge(final IncomingInvoice invoice, final Charge charge) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        IncomingInvoiceItem.class,
                        "findByInvoiceAndCharge",
                        "invoice", invoice,
                        "charge", charge
                ));
    }

    @Programmatic
    public IncomingInvoiceItem create(
            final BigInteger sequence,
            final IncomingInvoice invoice,
            final Charge charge,
            final String description,
            final BigDecimal netAmount,
            final BigDecimal vatAmount,
            final BigDecimal grossAmount,
            final Tax tax,
            final LocalDate dueDate,
            final LocalDate startDate,
            final LocalDate endDate,
            final Property property,
            final Project project,
            final BudgetItem budgetItem) {
        final IncomingInvoiceItem invoiceItem = new IncomingInvoiceItem(sequence, invoice, charge, description, netAmount, vatAmount, grossAmount, tax, dueDate, startDate, endDate, property, project, budgetItem);
        serviceRegistry2.injectServicesInto(invoiceItem);
        repositoryService.persistAndFlush(invoiceItem);
        return invoiceItem;
    }

    @Programmatic
    public IncomingInvoiceItem upsert(
            final BigInteger sequence,
            final IncomingInvoice invoice,
            final Charge charge,
            final String description,
            final BigDecimal netAmount,
            final BigDecimal vatAmount,
            final BigDecimal grossAmount,
            final Tax tax,
            final LocalDate dueDate,
            final LocalDate startDate,
            final LocalDate endDate,
            final Property property,
            final Project project,
            final BudgetItem budgetItem) {
        IncomingInvoiceItem invoiceItem = findByInvoiceAndCharge(invoice, charge);
        if (invoiceItem == null) {
            invoiceItem = create(
                    sequence,
                    invoice,
                    charge,
                    description,
                    netAmount,
                    vatAmount,
                    grossAmount,
                    tax,
                    dueDate,
                    startDate,
                    endDate,
                    property,
                    project,
                    budgetItem);
        } else {
            updateInvoiceItem(
                    invoiceItem,
                    sequence,
                    description,
                    netAmount,
                    vatAmount,
                    grossAmount,
                    tax,
                    dueDate,
                    startDate,
                    endDate,
                    property,
                    project,
                    budgetItem);
        }

        // also copy over this first item's property onto the header
        invoice.setProperty(property);

        return invoiceItem;
    }

    private void updateInvoiceItem(
            final IncomingInvoiceItem invoiceItem,
            final BigInteger sequence,
            final String description,
            final BigDecimal netAmount,
            final BigDecimal vatAmount,
            final BigDecimal grossAmount,
            final Tax tax,
            final LocalDate dueDate,
            final LocalDate startDate,
            final LocalDate endDate,
            final Property property,
            final Project project,
            final BudgetItem budgetItem){
        invoiceItem.setSequence(sequence);
        invoiceItem.setDescription(description);
        invoiceItem.setNetAmount(netAmount);
        invoiceItem.setVatAmount(vatAmount);
        invoiceItem.setGrossAmount(grossAmount);
        invoiceItem.setTax(tax);
        invoiceItem.setDueDate(dueDate);
        invoiceItem.setStartDate(startDate);
        invoiceItem.setEndDate(endDate);
        invoiceItem.setFixedAsset(property);
        invoiceItem.setProject(project);
        invoiceItem.setBudgetItem(budgetItem);
    }

    @Programmatic
    public List<IncomingInvoiceItem> findByProjectAndCharge(final Project project, final Charge charge) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        IncomingInvoiceItem.class,
                        "findByProjectAndCharge",
                        "project", project,
                        "charge", charge
                ));
    }

    @Programmatic
    public List<IncomingInvoiceItem> findByBudgetItem(final BudgetItem budgetItem) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        IncomingInvoiceItem.class,
                        "findByBudgetItem",
                        "budgetItem", budgetItem
                ));
    }

    @Programmatic
    public List<IncomingInvoiceItem> findByProject(final Project project) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        IncomingInvoiceItem.class,
                        "findByProject",
                        "project", project
                ));
    }

    @Programmatic
    public List<IncomingInvoiceItem> listAll() {
        return repositoryService.allInstances(IncomingInvoiceItem.class);
    }

    @Inject
    RepositoryService repositoryService;
    @Inject
    ServiceRegistry2 serviceRegistry2;

}
