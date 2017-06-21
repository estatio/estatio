package org.estatio.capex.dom.documents.categorisation.tasks;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.documents.categorisation.Document_nextCategorisationTaskRoleAssignedTo;
import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransition;
import org.estatio.capex.dom.task.Task;
import org.estatio.capex.dom.task.Task_mixinPropAbstract;
import org.estatio.dom.party.role.PartyRoleType;

@Mixin(method = "prop")
public class Task_nextCategorisationTaskRoleAssignedTo
        extends
        Task_mixinPropAbstract<Document_nextCategorisationTaskRoleAssignedTo, Document> {

    protected final Task task;

    public Task_nextCategorisationTaskRoleAssignedTo(final Task task) {
        super(task, Document_nextCategorisationTaskRoleAssignedTo.class);
        this.task = task;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public PartyRoleType prop() {
        return mixin().prop();
    }

    public boolean hideProp() {
        return super.hideProp() || mixin().hideProp();
    }

    @Override
    protected Document doGetDomainObjectIfAny() {
        final IncomingDocumentCategorisationStateTransition transition = transitionRepository.findByTask(this.task);
        return transition != null ? transition.getDocument() : null;
    }

    @Inject
    IncomingDocumentCategorisationStateTransition.Repository transitionRepository;

}
