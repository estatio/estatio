package org.estatio.module.capex.integtests.task.overview;

import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixtures.TickingFixtureClock;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.wrapper.DisabledException;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelRepository;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelType;

import org.estatio.module.asset.fixtures.person.enums.Person_enum;
import org.estatio.module.capex.app.taskreminder.TaskOverview;
import org.estatio.module.capex.app.taskreminder.TaskReminderService;
import org.estatio.module.capex.dom.task.Task;
import org.estatio.module.capex.dom.task.TaskRepository;
import org.estatio.module.capex.fixtures.incominginvoice.enums.IncomingInvoice_enum;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;
import org.estatio.module.capex.seed.DocumentTypesAndTemplatesForCapexFixture;
import org.estatio.module.charge.fixtures.incoming.builders.IncomingChargesFraXlsxFixture;
import org.estatio.module.party.dom.Person;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskOverview_IntegTest extends CapexModuleIntegTestAbstract {

    public static class OverdueTasks extends TaskOverview_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(final ExecutionContext executionContext) {
                    executionContext.executeChild(this, new IncomingChargesFraXlsxFixture());
                    executionContext.executeChild(this, new DocumentTypesAndTemplatesForCapexFixture());
                    executionContext.executeChild(this, IncomingInvoice_enum.fakeInvoice2Pdf.builder());
                    executionContext.executeChild(this, Person_enum.DanielOfficeAdministratorFr.builder());
                    executionContext.executeChild(this, Person_enum.FleuretteRenaudFr.builder());
                }
            });
        }

        @Test
        public void noOverdueTasks() {
            // given
            final Person person = Person_enum.DanielOfficeAdministratorFr.findUsing(serviceRegistry);
            final TaskOverview overview = serviceRegistry.injectServicesInto(new TaskOverview(person));

            //   then
            assertThat(overview.getListOfTasksOverdue()).isEmpty();
        }

        @Test
        public void overdueTasks() {
            // given
            final Person person = Person_enum.FleuretteRenaudFr.findUsing(serviceRegistry);
            final List<Task> unassigned = taskRepository.findIncompleteByUnassigned();
            assertThat(unassigned).hasSize(2);

            // when
            unassigned.forEach(task -> task.setPersonAssignedTo(person));
            TickingFixtureClock.replaceExisting().addDate(0, 0, 20);
            final TaskOverview overview = serviceRegistry.injectServicesInto(new TaskOverview(person));

            // then
            assertThat(overview.getListOfTasksOverdue()).hasSize(1); // order excluded
        }
    }

    public static class SendReminder extends TaskOverview_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(final ExecutionContext executionContext) {
                    executionContext.executeChild(this, new IncomingChargesFraXlsxFixture());
                    executionContext.executeChild(this, new DocumentTypesAndTemplatesForCapexFixture());
                    executionContext.executeChild(this, IncomingInvoice_enum.fakeInvoice2Pdf.builder());
                    executionContext.executeChild(this, Person_enum.DanielOfficeAdministratorFr.builder());
                    executionContext.executeChild(this, Person_enum.FleuretteRenaudFr.builder());
                }
            });
        }

        @Test
        public void sadCase_noEmailAddress() {
            // given
            final Person person = Person_enum.FleuretteRenaudFr.findUsing(serviceRegistry);
            final List<Task> unassigned = taskRepository.findIncompleteByUnassigned();
            assertThat(unassigned).hasSize(2);

            unassigned.forEach(task -> task.setPersonAssignedTo(person));
            TickingFixtureClock.replaceExisting().addDate(0, 0, 20);
            final TaskOverview overview = serviceRegistry.injectServicesInto(new TaskOverview(person));
            assertThat(overview.getListOfTasksOverdue()).hasSize(2);

            // then
            expectedExceptions.expect(DisabledException.class);
            expectedExceptions.expectMessage("No email address is known for Renaud, Fleurette");

            // when
            wrap(overview).sendReminder();
        }

        @Test
        public void sadCase_noOverdueTasks() {
            // given
            final Person person = Person_enum.FleuretteRenaudFr.findUsing(serviceRegistry);
            communicationChannelRepository.newEmail(person, CommunicationChannelType.EMAIL_ADDRESS, "fleuretterenaud@acme.org");
            final TaskOverview overview = serviceRegistry.injectServicesInto(new TaskOverview(person));

            // then
            expectedExceptions.expect(DisabledException.class);
            expectedExceptions.expectMessage("Renaud, Fleurette does not have any overdue tasks");

            // when
            wrap(overview).sendReminder();
        }

        @Inject
        CommunicationChannelRepository communicationChannelRepository;

    }

    @Inject
    TaskRepository taskRepository;

    @Inject
    ClockService clockService;

    @Inject
    TaskReminderService taskReminderService;

}
