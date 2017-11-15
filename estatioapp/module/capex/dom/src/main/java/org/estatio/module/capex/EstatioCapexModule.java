package org.estatio.module.capex;

import java.util.Set;

import com.google.common.collect.Sets;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.incode.module.fixturesupport.dom.scripts.TeardownFixtureAbstract;

import org.estatio.module.assetfinancial.EstatioAssetFinancialModule;
import org.estatio.module.base.platform.applib.Module;
import org.estatio.module.base.platform.applib.ModuleAbstract;
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
import org.estatio.module.capex.dom.payment.approval.PaymentBatchApprovalStateTransition;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.capex.dom.project.ProjectItem;
import org.estatio.module.capex.dom.project.ProjectRole;
import org.estatio.module.capex.dom.task.Task;
import org.estatio.module.invoice.EstatioInvoiceModule;
import org.estatio.module.invoice.dom.InvoiceAttribute;
import org.estatio.module.invoice.dom.paperclips.PaperclipForInvoice;

public class EstatioCapexModule extends ModuleAbstract {

    public EstatioCapexModule() {}

    @Override
    public Set<Module> getDependencies(){
        return Sets.newHashSet(
                new EstatioInvoiceModule(),
                new EstatioAssetFinancialModule(),
                new EstatioBudgetModule()
                );
    }


    @Override
    public FixtureScript getRefDataSetupFixture() {
        return new FixtureScript() {
            @Override
            protected void execute(final FixtureScript.ExecutionContext executionContext) {
            }
        };
    }

    @Override
    public FixtureScript getTeardownFixture() {
        return new TeardownFixtureAbstract() {
            @Override
            protected void execute(final FixtureScript.ExecutionContext executionContext) {

                deleteFrom(CodaMapping.class);
                deleteFrom(CodaElement.class);

                // TODO: convert to appropriate SQL
                deleteFrom(InvoiceAttribute.class);    // for this module's subtype
                deleteFrom(PaperclipForInvoice.class); // for this module's subtype
                deleteFrom(IncomingInvoiceItem.class);
                deleteFrom(IncomingInvoice.class);

                deleteFrom(PaymentBatchApprovalStateTransition.class);
                deleteFrom(IncomingInvoiceApprovalStateTransition.class);
                deleteFrom(OrderApprovalStateTransition.class);
                deleteFrom(BankAccountVerificationStateTransition.class);
                deleteFrom(IncomingDocumentCategorisationStateTransition.class);
                deleteFrom(Task.class);

                deleteFrom(PaymentBatch.class);

                deleteFrom(OrderItemInvoiceItemLink.class);

                deleteFrom(PaperclipForOrder.class);
                deleteFrom(OrderItem.class);
                deleteFrom(Order.class);

                deleteFrom(ProjectRole.class);
                deleteFrom(ProjectItem.class);
                deleteFrom(Project.class);
            }
        };
    }



    public abstract static class ActionDomainEvent<S>
            extends org.apache.isis.applib.services.eventbus.ActionDomainEvent<S> { }

    public abstract static class CollectionDomainEvent<S,T>
            extends org.apache.isis.applib.services.eventbus.CollectionDomainEvent<S,T> { }

    public abstract static class PropertyDomainEvent<S,T>
            extends org.apache.isis.applib.services.eventbus.PropertyDomainEvent<S,T> { }

}
