package org.estatio.module.base.spiimpl.commandreplay;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.command.Command;
import org.apache.isis.applib.services.command.CommandWithDto;
import org.apache.isis.schema.cmd.v1.CommandDto;
import org.apache.isis.schema.utils.CommandDtoUtils;

import org.isisaddons.module.audit.dom.AuditingServiceRepository;
import org.isisaddons.module.command.dom.CommandJdo;
import org.isisaddons.module.command.replay.spi.CommandReplayAnalyserAbstract;
import org.isisaddons.module.command.replay.spi.CommandReplayAnalyserResultStr;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class CommandReplayAnalyserResultStrSimplified extends CommandReplayAnalyserAbstract {

    public static final String ANALYSIS_KEY = "isis.services."
            + CommandReplayAnalyserResultStrSimplified.class.getSimpleName()
            + ".analysis";

    public CommandReplayAnalyserResultStrSimplified() {
        super(ANALYSIS_KEY);
    }

    /**
     * Hook for the slave.
     *
     * Unlike {@link CommandReplayAnalyserResultStr}, this checks only that both are null or both are non-null.
     */
    protected String doAnalyzeReplay(final Command command, final CommandDto dto) {

        if (!(command instanceof CommandJdo)) {
            return null;
        }

        final CommandJdo commandJdo = (CommandJdo) command;

        final String masterResultStr =
                CommandDtoUtils.getUserData(dto, CommandWithDto.USERDATA_KEY_RETURN_VALUE);
        if (masterResultStr == null) {
            return null;
        }

        final String slaveResultStr = commandJdo.getResultStr();
        if(slaveResultStr != null) {
            return null;
        }

        return "Results differ.  Master was '%s', slave is null";
    }

    @Inject
    AuditingServiceRepository auditingServiceRepository;


}
