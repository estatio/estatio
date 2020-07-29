package org.estatio.module.capex.app.taskreminder;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Sets;

import org.assertj.core.util.Lists;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.linking.DeepLinkService;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelRepository;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelType;
import org.incode.module.communications.dom.impl.commchannel.EmailAddress;

import org.estatio.module.application.spiimpl.email.EmailService2;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType;
import org.estatio.module.capex.dom.order.approval.OrderApprovalStateTransition;
import org.estatio.module.task.dom.state.StateTransitionService;
import org.estatio.module.task.dom.task.Task;
import org.estatio.module.task.dom.task.TaskRepository;
import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.role.PartyRoleType;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskReminderService_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock EmailService2 mockEmailService;

    @Mock CommunicationChannelRepository mockCommunicationChannelRepository;

    @Mock DeepLinkService mockDeepLinkService;

    @Mock ClockService mockClockService;

    @Test
    public void sendReminder() throws Exception {
        // given
        final TaskReminderService taskReminderService = new TaskReminderService();
        final Person person = new Person();
        person.setName("John Doe");

        final EmailAddress emailAddress = new EmailAddress();
        emailAddress.setEmailAddress("test@estatio.org");
        final PartyRoleType partyRoleType = new PartyRoleType("FOO", "FOO");

        final List<Task> overdueTasks = Lists.newArrayList();
        overdueTasks.add(new Task(partyRoleType, person, "Description", LocalDateTime.now().minusMonths(1), "", null));

        taskReminderService.emailService = mockEmailService;
        taskReminderService.communicationChannelRepository = mockCommunicationChannelRepository;
        taskReminderService.deepLinkService = mockDeepLinkService;
        taskReminderService.clockService = mockClockService;

        // expect
        context.checking(new Expectations() {{
            oneOf(mockCommunicationChannelRepository).findByOwnerAndType(person, CommunicationChannelType.EMAIL_ADDRESS);
            will(returnValue(Sets.newTreeSet(Arrays.asList(emailAddress))));

            oneOf(mockDeepLinkService).deepLinkFor(overdueTasks.get(0));
            will(returnValue(new URI("http://localhost:8080/wicket/entity/task.Task:0")));

            oneOf(mockEmailService).send(
                    Arrays.asList("test@estatio.org"),
                    Lists.emptyList(),
                    Lists.emptyList(),
                    "no-reply-reminders@ecpnv.com",
                    "You have 1 overdue task in Estatio",
                    "Dear John Doe,\n\nThis is a friendly reminder that you have 1 overdue task(s) in Estatio:\n<ul><li>http://localhost:8080/wicket/entity/task.Task:0</li></ul>");

            oneOf(mockClockService).now();
            will(returnValue(LocalDate.parse("2018-08-01")));

            oneOf(mockCommunicationChannelRepository).findByOwnerAndType(person, CommunicationChannelType.EMAIL_ADDRESS);
            will(returnValue(Sets.newTreeSet(Arrays.asList(emailAddress))));

            oneOf(mockClockService).now();
            will(returnValue(LocalDate.parse("2018-08-01")));
        }});

        // when
        taskReminderService.sendReminder(person, overdueTasks);

        // then
        assertThat(taskReminderService.disableSendReminder(person, overdueTasks)).isEqualTo("A reminder has been sent to John Doe today already");
    }

    @Mock TaskRepository mockTaskRepository;

    @Mock StateTransitionService mockStateTransitionService;

    @Test
    public void findIncompleteItalianApprovalTasks_works() throws Exception {

        // given
        final TaskReminderService taskReminderService = new TaskReminderService();
        taskReminderService.taskRepository = mockTaskRepository;
        taskReminderService.stateTransitionService = mockStateTransitionService;
        Task taskNonIta = new Task(null, null, null, null, null, null){
            @Override public String getAtPath() {
                return "/FRA";
            }
        };
        Task taskAtPathNull = new Task(null, null, null, null, null, null){
            @Override public String getAtPath() {
                return null;
            }
        };
        Task taskOtherTransitionType = new Task(null,null, null, null, null, null){
            @Override public String getAtPath() {
                return "/ITA";
            }
        };
        Task taskForCompletion = new Task(null,null,null,null,null, null){
            @Override public String getAtPath() {
                return "/ITA";
            }
        };
        Task taskForApproval = new Task(null,null,null,null,null, null){
            @Override public String getAtPath() {
                return "/ITA";
            }
        };
        Task taskForAdvise = new Task(null,null,null,null,null, null){
            @Override public String getAtPath() {
                return "/ITA";
            }
        };

        final IncomingInvoiceApprovalStateTransition stateTransitionComplete = new IncomingInvoiceApprovalStateTransition();
        stateTransitionComplete.setTransitionType(IncomingInvoiceApprovalStateTransitionType.COMPLETE);
        final IncomingInvoiceApprovalStateTransition stateTransitionApprove = new IncomingInvoiceApprovalStateTransition();
        stateTransitionApprove.setTransitionType(IncomingInvoiceApprovalStateTransitionType.APPROVE);
        final IncomingInvoiceApprovalStateTransition stateTransitionAdvise = new IncomingInvoiceApprovalStateTransition();
        stateTransitionAdvise.setTransitionType(IncomingInvoiceApprovalStateTransitionType.ADVISE_TO_APPROVE);

        // expect
        context.checking(new Expectations(){{
            oneOf(mockTaskRepository).findTasksIncomplete();
            will(returnValue(Arrays.asList(taskNonIta, taskAtPathNull, taskOtherTransitionType, taskForApproval, taskForCompletion, taskForAdvise)));
            oneOf(mockStateTransitionService).findFor(taskOtherTransitionType);
            will(returnValue(new OrderApprovalStateTransition()));
            allowing(mockStateTransitionService).findFor(taskForCompletion);
            will(returnValue(stateTransitionComplete));
            allowing(mockStateTransitionService).findFor(taskForApproval);
            will(returnValue(stateTransitionApprove));
            allowing(mockStateTransitionService).findFor(taskForAdvise);
            will(returnValue(stateTransitionAdvise));
        }});

        // when
        final List<Task> incompleteItalianApprovalTasks = taskReminderService.findIncompleteItalianApprovalTasks();

        // then
        assertThat(incompleteItalianApprovalTasks).hasSize(2);
        assertThat(incompleteItalianApprovalTasks).contains(taskForApproval);
        assertThat(incompleteItalianApprovalTasks).contains(taskForAdvise);


    }
}