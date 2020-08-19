package org.estatio.module.capex.integtests.app.taskreminder;

import java.net.URI;

import javax.inject.Inject;

import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.module.asset.fixtures.person.enums.Person_enum;
import org.estatio.module.capex.app.taskreminder.TaskReminderService;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;
import org.estatio.module.party.dom.role.PartyRoleTypeEnum;
import org.estatio.module.party.dom.role.PartyRoleTypeRepository;
import org.estatio.module.task.dom.task.Task;
import org.estatio.module.task.dom.task.TaskRepository;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskReminderService_IntegTest extends CapexModuleIntegTestAbstract {

    @Before
    public void setupData() {

        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final FixtureScript.ExecutionContext ec) {
                ec.executeChildren(this,
                        Person_enum.OlivePropertyManagerFr);
            }
        });

    }


    @Test
    public void deeplink_for_test() throws Exception {

        // given
        assertThat(taskRepository.listAll()).isEmpty();
        Task task = new Task(partyRoleTypeRepository.findByKey(PartyRoleTypeEnum.INCOMING_INVOICE_MANAGER.getKey()), null, "x", LocalDateTime
                .now(), "x", null);
        repositoryService.persistAndFlush(task);
        transactionService.nextTransaction();
        assertThat(taskRepository.listAll()).hasSize(1);

        // when

        final URI uri = taskReminderService.deepLinkFor(task);

        // then
        assertThat(uri.toString()).startsWith("https://estatio.int.ecpnv.com/wicket/entity/task.Task:");
        final String oidString = uri.toString().replace("https://estatio.int.ecpnv.com/wicket/entity/task.Task:", "");
        assertThat(oidString.length()).isGreaterThanOrEqualTo(1);
        assertThat(oidString.matches("\\d*"));

    }

    @Inject TaskReminderService taskReminderService;
    @Inject PartyRoleTypeRepository partyRoleTypeRepository;
    @Inject RepositoryService repositoryService;
    @Inject TaskRepository taskRepository;


}
