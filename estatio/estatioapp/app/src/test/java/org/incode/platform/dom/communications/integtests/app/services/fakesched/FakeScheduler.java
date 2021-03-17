package org.incode.platform.dom.communications.integtests.app.services.fakesched;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.xactn.TransactionService;
import org.apache.isis.core.runtime.authentication.standard.SimpleSession;

import org.isisaddons.module.command.dom.BackgroundCommandExecutionFromBackgroundCommandServiceJdo;
import org.isisaddons.module.command.dom.BackgroundCommandServiceJdoRepository;
import org.isisaddons.module.command.dom.CommandJdo;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "incodeCommunicationsDemo.FakeScheduler"
)
@DomainServiceLayout(
        named = "Fakes",
        menuBar = DomainServiceLayout.MenuBar.SECONDARY,
        menuOrder = "110"
)
public class FakeScheduler {

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public void runBackgroundCommands(
            @ParameterLayout(named = "Wait for (ms)")
            final Integer waitFor) throws InterruptedException {

        List<CommandJdo> commands = backgroundCommandRepository.findBackgroundCommandsNotYetStarted();
        if(commands.isEmpty()) {
            throw new IllegalStateException("There are no commands not yet started");
        }

        transactionService.nextTransaction();

        BackgroundCommandExecutionFromBackgroundCommandServiceJdo backgroundExec =
                new BackgroundCommandExecutionFromBackgroundCommandServiceJdo();
        final SimpleSession session = new SimpleSession("scheduler_user", new String[] { "admin_role" });

        final Thread thread = new Thread(() -> backgroundExec.execute(session, null));
        thread.start();

        thread.join(waitFor);

        commands = backgroundCommandRepository.findBackgroundCommandsNotYetStarted();
        if(!commands.isEmpty()) {
            throw new IllegalStateException(String.format("There are still %d not yet started", commands.size()));
        }
    }

    // using a validateXxx rather than a hideXxx because of https://issues.apache.org/jira/browse/ISIS-1593
    public String validateRunBackgroundCommands(final Integer waitFor) {
        List<CommandJdo> commands = backgroundCommandRepository.findBackgroundCommandsNotYetStarted();
        return commands.isEmpty() ? "No background commands to run" : null;
    }

    public Integer default0RunBackgroundCommands() {
        return 5000;
    }

    @Inject
    protected BackgroundCommandServiceJdoRepository backgroundCommandRepository;

    @Inject
    protected TransactionService transactionService;

}
