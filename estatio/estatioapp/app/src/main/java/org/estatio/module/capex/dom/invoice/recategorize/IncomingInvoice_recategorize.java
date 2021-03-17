package org.estatio.module.capex.dom.invoice.recategorize;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.inject.Inject;

import com.google.common.eventbus.Subscribe;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.applib.services.xactn.TransactionService2;

import org.isisaddons.module.security.app.user.MeService;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;

import org.estatio.module.asset.dom.role.FixedAssetRoleTypeEnum;
import org.estatio.module.capex.dom.documents.LookupAttachedPdfService;
import org.estatio.module.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransition;
import org.estatio.module.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransitionType;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.module.task.dom.state.StateTransitionService;
import org.estatio.module.task.dom.task.Task;
import org.estatio.module.invoice.dom.DocumentTypeData;
import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.PersonRepository;
import org.estatio.module.party.dom.role.PartyRoleTypeEnum;
import org.estatio.module.party.dom.role.PartyRoleTypeRepository;

/**
 * TODO: inline this mixin (then Task_recategorizeIncomingInvoice has to be refactored as well....)
 */
@Mixin(method = "act")
public class IncomingInvoice_recategorize {

    private final IncomingInvoice incomingInvoice;

    public IncomingInvoice_recategorize(IncomingInvoice incomingInvoice) {
        this.incomingInvoice = incomingInvoice;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    @ActionLayout(cssClassFa = "mail-reply", cssClass = "btn-danger")
    public Document act(@Nullable final String comment) {
        Document document = lookupPdf();
        document.setType(DocumentTypeData.INCOMING.findUsing(documentTypeRepository));

        stateTransitionService.trigger(
                document,
                IncomingDocumentCategorisationStateTransition.class,
                IncomingDocumentCategorisationStateTransitionType.RESET,
                comment,
                comment);

        // use events to cascade delete, eg paperclips and state transitions/tasks
        incomingInvoiceRepository.delete(incomingInvoice);

        return document;
    }

    public String disableAct() {
        final Document documentIfAny = lookupPdf();
        if(documentIfAny == null) {
            return "Cannot locate document";
        }
        if(incomingInvoice.getApprovalState() != IncomingInvoiceApprovalState.NEW) {
            return "Only NEW invoices can be recategorized";
        }
        if (incomingInvoice.isReported()){
            return "This invoice is reported and cannot be recategorized";
        }
        final Person meAsPerson = personRepository.me();
        if (meAsPerson==null) return "Your login is not linked to a person in Estatio";
        if (
                !(
                    meAsPerson.hasPartyRoleType(FixedAssetRoleTypeEnum.PROPERTY_MANAGER.findUsing(partyRoleTypeRepository))
                    ||
                    meAsPerson.hasPartyRoleType(PartyRoleTypeEnum.OFFICE_ADMINISTRATOR.findUsing(partyRoleTypeRepository))
                    ||
                    meAsPerson.hasPartyRoleType(PartyRoleTypeEnum.INCOMING_INVOICE_MANAGER.findUsing(partyRoleTypeRepository))
                    ||
                    meAsPerson.hasPartyRoleType(PartyRoleTypeEnum.CORPORATE_ADMINISTRATOR.findUsing(partyRoleTypeRepository))
                )
                ){
            return String.format("You need role %s, %s, %s or %s to recategorize", FixedAssetRoleTypeEnum.PROPERTY_MANAGER.getKey(), PartyRoleTypeEnum.OFFICE_ADMINISTRATOR, PartyRoleTypeEnum.INCOMING_INVOICE_MANAGER, PartyRoleTypeEnum.CORPORATE_ADMINISTRATOR);
        }
        return null;
    }

    public boolean hideAct() {
        if (meService.me().getAtPath().startsWith("/ITA")) return true;
        return incomingInvoice.getApprovalState() == IncomingInvoiceApprovalState.PAID;
    }

    private Document lookupPdf() {
        final Optional<Document> document = lookupAttachedPdfService.lookupIncomingInvoicePdfFrom(incomingInvoice);
        return document.orElse(null);
    }

    @Inject
    DocumentTypeRepository documentTypeRepository;

    @Inject
    LookupAttachedPdfService lookupAttachedPdfService;

    @Inject
    IncomingInvoiceRepository incomingInvoiceRepository;

    @Inject
    StateTransitionService stateTransitionService;

    @Inject
    PersonRepository personRepository;

    @Inject
    PartyRoleTypeRepository partyRoleTypeRepository;

    @Inject
    private MeService meService;

    /////////////////////////////////////////////////////////////////


    @DomainService(nature = NatureOfService.DOMAIN)
    public static class InvoiceDeletionCascadePaperclipsSubscriber extends AbstractSubscriber {

        @Subscribe
        @org.axonframework.eventhandling.annotation.EventHandler
        public void on(IncomingInvoice.ObjectRemovingEvent event) {
            final IncomingInvoice incomingInvoice = event.getSource();
            paperclipRepository.deleteIfAttachedTo(incomingInvoice, PaperclipRepository.Policy.PAPERCLIPS_ONLY);
            transactionService2.flushTransaction();
        }

        @Inject
        private PaperclipRepository paperclipRepository;
        @Inject
        private TransactionService2 transactionService2;

    }

    @DomainService(nature = NatureOfService.DOMAIN)
    public static class InvoiceDeletionCascadeStateTransitionsAndTasksSubscriber extends AbstractSubscriber {

        @Subscribe
        @org.axonframework.eventhandling.annotation.EventHandler
        public void on(IncomingInvoice.ObjectRemovingEvent event) {
            final IncomingInvoice incomingInvoice = event.getSource();
            final List<IncomingInvoiceApprovalStateTransition> transitions =
                    repository.findByDomainObject(incomingInvoice);
            final List<Task> tasksToDelete =
                    transitions.stream()
                        .map(IncomingInvoiceApprovalStateTransition::getTask)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
            for (IncomingInvoiceApprovalStateTransition transition : transitions) {
                repositoryService.removeAndFlush(transition);
            }
            for (Task task : tasksToDelete) {
                repositoryService.removeAndFlush(task);
            }
        }

        @Inject
        private IncomingInvoiceApprovalStateTransition.Repository repository;
        @Inject
        private RepositoryService repositoryService;

    }


}
