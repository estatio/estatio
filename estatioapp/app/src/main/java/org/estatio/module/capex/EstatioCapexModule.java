package org.estatio.module.capex;

import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Sets;

import org.apache.isis.applib.Module;
import org.apache.isis.applib.ModuleAbstract;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.fixturescripts.teardown.TeardownFixtureAbstract2;

import org.incode.module.document.DocumentModule;

import org.estatio.canonical.EstatioCanonicalModule;
import org.estatio.module.assetfinancial.EstatioAssetFinancialModule;
import org.estatio.module.budget.EstatioBudgetModule;
import org.estatio.module.capex.dom.bankaccount.verification.BankAccountVerificationStateTransition;
import org.estatio.module.capex.dom.coda.CodaElement;
import org.estatio.module.capex.dom.coda.CodaMapping;
import org.estatio.module.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransition;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.capex.dom.order.OrderItem;
import org.estatio.module.capex.dom.order.approval.OrderApprovalStateTransition;
import org.estatio.module.capex.dom.order.paperclips.PaperclipForOrder;
import org.estatio.module.capex.dom.orderinvoice.OrderItemInvoiceItemLink;
import org.estatio.module.capex.dom.payment.PaymentBatch;
import org.estatio.module.capex.dom.payment.PaymentLine;
import org.estatio.module.capex.dom.payment.approval.PaymentBatchApprovalStateTransition;
import org.estatio.module.capex.dom.project.ProjectItemTerm;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.capex.dom.project.ProjectItem;
import org.estatio.module.capex.dom.project.ProjectRole;
import org.estatio.module.capex.dom.task.Task;
import org.estatio.module.invoice.EstatioInvoiceModule;
import org.estatio.module.invoice.dom.Invoice;
import org.estatio.module.invoice.dom.InvoiceItem;
import org.estatio.module.invoice.dom.attr.InvoiceAttribute;
import org.estatio.module.invoice.dom.paperclips.PaperclipForInvoice;
import org.estatio.module.order.EstatioOrderAttributeModule;
import org.estatio.module.order.dom.attr.OrderAttribute;

@XmlRootElement(name = "module")
public class EstatioCapexModule extends ModuleAbstract {

    public EstatioCapexModule() {}

    @Override
    public Set<Module> getDependencies(){
        return Sets.newHashSet(
                new EstatioInvoiceModule(),
                new EstatioAssetFinancialModule(),
                new EstatioBudgetModule(),

                new DocumentModule(),
                new EstatioCanonicalModule(),
                new EstatioOrderAttributeModule()
        );
    }


    @Override
    public FixtureScript getTeardownFixture() {
        return new TeardownFixtureAbstract2() {

            @Override
            protected void execute(final FixtureScript.ExecutionContext executionContext) {

                String sql;

                deleteFrom(PaymentBatchApprovalStateTransition.class);
                deleteFrom(IncomingInvoiceApprovalStateTransition.class);
                deleteFrom(OrderApprovalStateTransition.class);
                deleteFrom(BankAccountVerificationStateTransition.class);
                deleteFrom(IncomingDocumentCategorisationStateTransition.class);
                deleteFrom(Task.class);

                deleteFrom(CodaMapping.class);
                deleteFrom(CodaElement.class);

                // OrderItemInvoiceItemLink
                sql = String.format(
                        "DELETE FROM \"%s\".\"%s\" "
                                + "WHERE \"%s\" IN "
                                + "(SELECT \"id\" FROM \"%s\".\"%s\" WHERE \"%s\" = '%s') ",
                        schemaOf(OrderItemInvoiceItemLink.class), tableOf(OrderItemInvoiceItemLink.class),
                        "invoiceItemId",
                        schemaOf(InvoiceItem.class), tableOf(InvoiceItem.class), // supertype of IncomingInvoiceItem
                        discriminatorColumnOf(InvoiceItem.class),
                        discriminatorValueOf(IncomingInvoiceItem.class)
                );
                this.isisJdoSupport.executeUpdate(sql);


                // OrderAttribute
                sql = String.format(
                        "DELETE FROM \"%s\".\"%s\" "
                                + "WHERE \"%s\" IN "
                                + "(SELECT \"id\" FROM \"%s\".\"%s\") ",
                        schemaOf(OrderAttribute.class), tableOf(OrderAttribute.class),
                        "orderId",
                        schemaOf(Order.class), tableOf(Order.class)
                );
                this.isisJdoSupport.executeUpdate(sql);


                // InvoiceAttribute
                sql = String.format(
                        "DELETE FROM \"%s\".\"%s\" "
                                + "WHERE \"%s\" IN "
                                + "(SELECT \"id\" FROM \"%s\".\"%s\" WHERE \"%s\" = '%s') ",
                        schemaOf(InvoiceAttribute.class), tableOf(InvoiceAttribute.class),
                        "invoiceId",
                        schemaOf(Invoice.class), tableOf(Invoice.class), // supertype of IncomingInvoice
                        discriminatorColumnOf(Invoice.class),
                        discriminatorValueOf(IncomingInvoice.class)
                );
                this.isisJdoSupport.executeUpdate(sql);

                // PaperclipForInvoice
                sql = String.format(
                        "DELETE FROM \"%s\".\"%s\" "
                                + "WHERE \"%s\" IN "
                                + "(SELECT \"id\" FROM \"%s\".\"%s\" WHERE \"%s\" = '%s') ",
                        schemaOf(PaperclipForInvoice.class), tableOf(PaperclipForInvoice.class),
                        "invoiceId",
                        schemaOf(Invoice.class), tableOf(Invoice.class), // supertype of IncomingInvoice
                        discriminatorColumnOf(Invoice.class),
                        discriminatorValueOf(IncomingInvoice.class)
                );
                this.isisJdoSupport.executeUpdate(sql);

                deleteFrom(PaymentLine.class);
                deleteFrom(PaymentBatch.class);

                deleteFrom(IncomingInvoiceItem.class);
                deleteFrom(IncomingInvoice.class);

                deleteFrom(PaymentBatch.class);

                deleteFrom(PaperclipForOrder.class);
                deleteFrom(OrderItem.class);
                deleteFrom(Order.class);

                deleteFrom(ProjectItemTerm.class);
                deleteFrom(ProjectRole.class);
                deleteFrom(ProjectItem.class);
                deleteFrom(Project.class);
            }
        };
    }




}
