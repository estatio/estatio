package org.estatio.module.capex.app.taskreminder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.bookmark.BookmarkService2;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.linking.DeepLinkService;
import org.apache.isis.applib.services.metamodel.MetaModelService5;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelRepository;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelType;
import org.incode.module.communications.dom.impl.commchannel.EmailAddress;
import org.incode.module.document.dom.impl.paperclips.Paperclip;

import org.estatio.module.application.spiimpl.email.EmailService2;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType;
import org.estatio.module.capex.dom.state.StateTransition;
import org.estatio.module.capex.dom.state.StateTransitionService;
import org.estatio.module.capex.dom.task.Task;
import org.estatio.module.capex.dom.task.TaskRepository;
import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.role.PartyRoleType;
import org.estatio.module.party.dom.role.PartyRoleTypeEnum;

@DomainService(
        nature = NatureOfService.DOMAIN,
        objectType = "org.estatio.module.capex.app.taskreminder.TaskReminderService"
)
public class TaskReminderService {

    public static final String FROM_EMAIL_ADDRESS = "no-reply-reminders@ecpnv.com";

    private static final Logger LOG = LoggerFactory.getLogger(TaskReminderService.class);

    @Programmatic
    private List<Person> getPersonsWithAssignedTasks() {
        return taskRepository.findTasksIncomplete().stream()
                .filter(task -> task.getPersonAssignedTo() != null)
                .map(Task::getPersonAssignedTo)
                .distinct()
                .collect(Collectors.toList());
    }

    @Programmatic
    public List<TaskOverview> getTaskOverviews() {
        final List<Person> personsWithTask = getPersonsWithAssignedTasks();
        return personsWithTask.stream()
                .map(this::getTaskOverviewForPerson)
                .collect(Collectors.toList());
    }

    @Programmatic
    public TaskOverview getTaskOverviewForPerson(final Person person) {
        return serviceRegistry.injectServicesInto(new TaskOverview(person));
    }

    @Programmatic
    public void sendReminder(final Person person, final List<Task> overdueTasks) {
        doSendReminder(
                person,
                overdueTasks,
                "You have %d overdue task in Estatio",
                "You have %d overdue tasks in Estatio",
                "Dear %s,\n\nThis is a friendly reminder that you have %d overdue task(s) in Estatio:"
        );
    }

    @Programmatic
    public String disableSendReminder(final Person person, final List<Task> overdueTasks) {
        if (communicationChannelRepository.findByOwnerAndType(person, CommunicationChannelType.EMAIL_ADDRESS).isEmpty()) {
            return String.format("No email address is known for %s", person.getName());
        }

        if (overdueTasks.stream().anyMatch(task -> task.getRemindedOn() != null && task.getRemindedOn().isEqual(clockService.now()))) {
            return String.format("A reminder has been sent to %s today already", person.getName());
        }

        return overdueTasks.isEmpty() ? String.format("%s does not have any overdue tasks", person.getName()) : null;
    }

    @Programmatic
    public void sendRemindersToAllItalianApprovers(){
        for (Person approver : approversHavingIncompleteApprovalTasks()) {
            final List<Task> taskList = findIncompleteItalianApprovalTasks().stream()
                    .filter(t->t.getPersonAssignedTo()!=null)
                    .filter(t -> t.getPersonAssignedTo().equals(approver))
                    .collect(Collectors.toList());
            if (disableSendReminderToApprover(approver, taskList)==null) {
                sendReminderToApprover(approver, taskList);
            } else {
                // maybe log something if there is a need for it
            }
        }
    }

    @Programmatic
    public void sendReminderToApprover(final Person person, final List<Task> approvalTasks) {
        doSendReminder(
                person,
                approvalTasks,
                "You have %d approval task in Estatio",
                "You have %d approval tasks in Estatio",
                "Dear %s,\n\nThis is a friendly reminder that you have %d approval task(s) in Estatio:"
        );
    }

    @Programmatic
    public String disableSendReminderToApprover(final Person person, final List<Task> approvalTasks) {
        if (communicationChannelRepository.findByOwnerAndType(person, CommunicationChannelType.EMAIL_ADDRESS).isEmpty()) {
            return String.format("No email address is known for %s", person.getName());
        }
        if (approvalTasks.stream().anyMatch(task -> task.getRemindedOn() != null && task.getRemindedOn().isEqual(clockService.now()))) {
            return String.format("A reminder has been sent to %s today already", person.getName());
        }
        return approvalTasks.isEmpty() ? String.format("%s does not have any approval tasks", person.getName()) : null;
    }

    @Programmatic
    private List<Person> approversHavingIncompleteApprovalTasks(){
        return findIncompleteItalianApprovalTasks().stream()
                .filter(t->t.getPersonAssignedTo()!=null)
                .map(t->t.getPersonAssignedTo())
                .distinct()
                .collect(Collectors.toList());
    }

    @Programmatic
    public List<Task> findIncompleteItalianApprovalTasks(){
        final List<IncomingInvoiceApprovalStateTransitionType> approvalStateTransitionTypes = Arrays
                .asList(IncomingInvoiceApprovalStateTransitionType.APPROVE,
                        IncomingInvoiceApprovalStateTransitionType.APPROVE_AS_COUNTRY_DIRECTOR,
                        IncomingInvoiceApprovalStateTransitionType.APPROVE_AS_CENTER_MANAGER,
                        IncomingInvoiceApprovalStateTransitionType.APPROVE_WHEN_APPROVED_BY_CENTER_MANAGER,
                        IncomingInvoiceApprovalStateTransitionType.ADVISE_TO_APPROVE);
        return taskRepository.findTasksIncomplete().stream()
                .filter(t->t.getAtPath()!=null)
                .filter(t -> t.getAtPath().startsWith("/ITA"))
                .filter(t -> stateTransitionService.findFor(t).getClass().isAssignableFrom(
                        IncomingInvoiceApprovalStateTransition.class))
                .filter(t->approvalStateTransitionTypes.contains(stateTransitionService.findFor(t).getTransitionType()))
                .collect(Collectors.toList());
    }

    @Programmatic
    public void doSendReminder(final Person person, final List<Task> tasks, final String subjectStringSingle, final String subjectStringPlural, final String bodyString){
        final EmailAddress address = (EmailAddress) communicationChannelRepository.findByOwnerAndType(person, CommunicationChannelType.EMAIL_ADDRESS).first();
        final String subject = tasks.size() == 1 ? String.format(subjectStringSingle, tasks.size()) : String.format(subjectStringPlural, tasks.size());
        final String body = String.format(bodyString + "\n<ul>", person.getName(), tasks.size())
                + tasks.stream()
                .map(task -> String.format("<li>%s</li>", deepLinkService.deepLinkFor(task)))
                .collect(Collectors.joining())
                + "</ul>";
        LOG.info(String.format("Sending reminder to %s ", person.getReference()));
        emailService.send(Collections.singletonList(address.getEmailAddress()), Collections.emptyList(), Collections.emptyList(), FROM_EMAIL_ADDRESS, subject, body);

        tasks.forEach(task -> task.setRemindedOn(clockService.now()));
    }

    @Inject
    TaskRepository taskRepository;

    @Inject
    CommunicationChannelRepository communicationChannelRepository;

    @Inject
    ClockService clockService;

    @Inject
    EmailService2 emailService;

    @Inject
    DeepLinkService deepLinkService;

    @Inject
    private ServiceRegistry2 serviceRegistry;

    @Inject
    private BookmarkService2 bookmarkService;

    @Inject
    private MetaModelService5 metaModelService;

    @Inject
    StateTransitionService stateTransitionService;

}
