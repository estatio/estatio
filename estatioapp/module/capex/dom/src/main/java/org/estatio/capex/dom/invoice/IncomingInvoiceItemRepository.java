package org.estatio.capex.dom.invoice;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.capex.dom.project.Project;
import org.estatio.capex.dom.util.PeriodUtil;
import org.estatio.dom.asset.Property;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.party.Party;
import org.estatio.dom.tax.Tax;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = IncomingInvoiceItem.class
)
public class IncomingInvoiceItemRepository {

    @Programmatic
    public IncomingInvoiceItem findByInvoiceAndChargeAndSequence(final IncomingInvoice invoice, final Charge charge, final BigInteger sequence) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        IncomingInvoiceItem.class,
                        "findByInvoiceAndChargeAndSequence",
                        "invoice", invoice,
                        "charge", charge,
                        "sequence", sequence
                ));
    }

    @Programmatic
    public IncomingInvoiceItem create(
            final BigInteger sequence,
            final IncomingInvoice invoice,
            final IncomingInvoiceType incomingInvoiceType,
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
        final IncomingInvoiceItem invoiceItem = new IncomingInvoiceItem(sequence, invoice, incomingInvoiceType, charge, description, netAmount, vatAmount, grossAmount, tax, dueDate, startDate, endDate, property, project, budgetItem);
        serviceRegistry2.injectServicesInto(invoiceItem);
        repositoryService.persistAndFlush(invoiceItem);
        invoice.recalculateAmounts();

        invoice.invalidateApproval();

        return invoiceItem;
    }

    @Programmatic
    public IncomingInvoiceItem upsert(
            final BigInteger sequence,
            final IncomingInvoice invoice,
            final IncomingInvoiceType incomingInvoiceType,
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
        IncomingInvoiceItem invoiceItem = findByInvoiceAndChargeAndSequence(invoice, charge, sequence);
        if (invoiceItem == null) {
            invoiceItem = create(
                    sequence,
                    invoice,
                    incomingInvoiceType,
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

        IncomingInvoice invoice = (IncomingInvoice) invoiceItem.getInvoice();
        invoice.recalculateAmounts();
    }

    @Programmatic
    public void addItem(
            final IncomingInvoice incomingInvoice,
            final IncomingInvoiceType type,
            final Charge charge,
            final String description,
            final BigDecimal netAmount,
            final BigDecimal vatAmount,
            final BigDecimal grossAmount,
            final Tax tax,
            final LocalDate dueDate,
            final String period,
            final Property property,
            final Project project,
            final BudgetItem budgetItem){
        final BigInteger sequence = incomingInvoice.nextItemSequence();
        upsert(
            sequence,
            incomingInvoice,
            type,
            charge,
            description,
            netAmount,
            vatAmount,
            grossAmount,
            tax,
            dueDate,
            PeriodUtil.yearFromPeriod(period).startDate(),
            PeriodUtil.yearFromPeriod(period).endDate(),
            property,
            project,
            budgetItem);
    }

    @Programmatic
    public void mergeItems(final IncomingInvoiceItem sourceItem, final IncomingInvoiceItem targetItem) {
        if (sourceItem==null || targetItem==null) return;
        if (sourceItem.equals(targetItem)) return;
        targetItem.addAmounts(sourceItem.getNetAmount(), sourceItem.getVatAmount(), sourceItem.getGrossAmount());
        sourceItem.removeItem();
        targetItem.invalidateApproval();
    }

    @Programmatic
    public List<IncomingInvoiceItem> findBySeller(final Party seller) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        IncomingInvoiceItem.class,
                        "findBySeller",
                        "seller", seller
                ));
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

    @Programmatic
    public List<IncomingInvoiceItem> invoiceItemsNotOnProjectItem(final Project project){
        List<Charge> chargesOnProject = Lists.newArrayList(project.getItems()).stream().map(x->x.getCharge()).collect(Collectors.toList());
        return findByProject(project).stream()
                .filter(x->!chargesOnProject.contains(x.getCharge()))
                .collect(Collectors.toList());
    }

    @Inject
    RepositoryService repositoryService;
    @Inject
    ServiceRegistry2 serviceRegistry2;

}
