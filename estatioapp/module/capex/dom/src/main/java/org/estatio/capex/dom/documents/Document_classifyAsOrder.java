package org.estatio.capex.dom.documents;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.EstatioCapexDomModule;
import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransition;
import org.estatio.capex.dom.task.Task;
import org.estatio.dom.asset.Property;
import org.estatio.dom.invoice.DocumentTypeData;

@Mixin(method = "act")
public class Document_classifyAsOrder extends Document_classifyAsAbstract {

    // workaround for ISIS-1628
    private final Document document;

    public Document_classifyAsOrder(final Document document) {
        super(document, DocumentTypeData.INCOMING_ORDER);
        this.document = document;
    }

    // workaround for ISIS-1628
    @Override
    public Object act(Property property) {
        return super.act(property);
    }

    @Override
    public boolean hideAct() {
        return super.hideAct();
    }

    @Mixin(method="act")
    public static class Task_categoriseAsOrder
            extends
            Task._mixinAbstract<Document_classifyAsOrder, Document> {

        protected final Task task;
        public Task_categoriseAsOrder(final Task task) {
            super(task, Document_classifyAsOrder.class);
            this.task = task;
        }

        public static class DomainEvent extends EstatioCapexDomModule.ActionDomainEvent<Task_categoriseAsOrder> {}

        @Action(domainEvent = DomainEvent.class)
        @ActionLayout(contributed= Contributed.AS_ACTION)
        public Task act(final Property property, final boolean goToNext) {
            mixin().act(property);
            return taskToReturn(goToNext, task);
        }

        public boolean hideAct() {
            return super.hideAct() || mixin().hideAct();
        }

        @Override
        protected Document doGetDomainObjectIfAny() {
            final IncomingDocumentCategorisationStateTransition transition = repository.findByTask(this.task);
            return transition != null ? transition.getDocument() : null;
        }

        @Inject
        IncomingDocumentCategorisationStateTransition.Repository repository;

    }

}
