package org.estatio.module.capex.integtests.task;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.estatio.module.asset.fixtures.person.enums.Person_enum;
import org.estatio.module.capex.app.taskreminder.TaskOverview;
import org.estatio.module.capex.fixtures.incominginvoice.enums.IncomingInvoice_enum;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;
import org.estatio.module.capex.seed.DocumentTypesAndTemplatesForCapexFixture;
import org.estatio.module.charge.fixtures.incoming.builders.IncomingChargesFraXlsxFixture;
import org.estatio.module.financial.fixtures.bankaccount.enums.BankAccount_enum;
import org.estatio.module.party.dom.Person;
import org.estatio.module.task.dom.task.Task;
import org.estatio.module.task.dom.task.TaskRepository;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskRepository_IntegTest extends CapexModuleIntegTestAbstract {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, new IncomingChargesFraXlsxFixture());
                executionContext.executeChild(this, new DocumentTypesAndTemplatesForCapexFixture());
                executionContext.executeChild(this, IncomingInvoice_enum.fakeInvoice2Pdf.builder());
                executionContext.executeChild(this, Person_enum.FleuretteRenaudFr.builder());
            }
        });
    }

    @Test
    public void happyCase_taskPriorityOrder() {
        // given
        final List<Task> tasks = taskRepository.findTasksIncomplete();
        assertThat(tasks).hasSize(2);
        assertThat(tasks.get(0).getPriority()).isNull();

        // when
        Task noPriorityTask = tasks.get(0);
        // Set priority to 0 instead of NULL, because the default NULL ordering with DESC of the in-mem database is NULLS FIRST (HSQLDB)
        noPriorityTask.setPriority(0);
        Task priorityTask = tasks.get(1);
        priorityTask.setPriority(1);
        final List<Task> sortedTasks = taskRepository.findTasksIncomplete();

        // then
        assertThat(sortedTasks.get(0)).isEqualTo(priorityTask); // Task with priority is at top of the list
    }

    @Inject
    TaskRepository taskRepository;
}
