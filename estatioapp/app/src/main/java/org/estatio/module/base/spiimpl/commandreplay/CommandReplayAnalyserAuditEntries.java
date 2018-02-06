package org.estatio.module.base.spiimpl.commandreplay;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.conmap.spi.CommandDtoProcessorService;
import org.apache.isis.applib.services.command.Command;
import org.apache.isis.schema.cmd.v1.CommandDto;
import org.apache.isis.schema.utils.CommandDtoUtils;

import org.isisaddons.module.audit.dom.AuditEntry;
import org.isisaddons.module.audit.dom.AuditingServiceRepository;
import org.isisaddons.module.command.dom.CommandJdo;
import org.isisaddons.module.command.replay.spi.CommandReplayAnalyserAbstract;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class CommandReplayAnalyserAuditEntries extends CommandReplayAnalyserAbstract implements
        CommandDtoProcessorService {

    public static final String ANALYSIS_KEY = "isis.services."
            + CommandReplayAnalyserAuditEntries.class.getSimpleName()
            + ".analysis";

    public static final String USERDATA_KEY_NUMBER_AUDIT_ENTRIES = "numberAuditEntries";

    public CommandReplayAnalyserAuditEntries() {
        super(ANALYSIS_KEY);
    }

    /**
     * Hook for the master, enriches the DTO.
     */
    @Override
    public CommandDto process(
            final Command command, final CommandDto commandDto) {

        if(command instanceof CommandJdo) {

            final List<AuditEntry> auditEntries =
                    auditingServiceRepository.findByTransactionId(command.getTransactionId());

            CommandDtoUtils.setUserData(commandDto,
                    USERDATA_KEY_NUMBER_AUDIT_ENTRIES, ""+auditEntries.size());
        }
        return commandDto;
    }

    /**
     * Hook for the slave.
     */
    protected String doAnalyzeReplay(final Command command, final CommandDto dto) {

        if (!(command instanceof CommandJdo)) {
            return null;
        }

        final String masterNumAuditEntriesStr =
                CommandDtoUtils.getUserData(dto, USERDATA_KEY_NUMBER_AUDIT_ENTRIES);

        if (masterNumAuditEntriesStr == null) {
            return null;
        }

        final int masterNumAuditEntries;
        try {
            masterNumAuditEntries = Integer.parseInt(masterNumAuditEntriesStr);

        } catch (NumberFormatException ex) {
            return String.format(
                    "Unable to check number of audit entries; "
                            + "could not parse '%s' (value of '%s' userdata) in XML",
                    masterNumAuditEntriesStr, USERDATA_KEY_NUMBER_AUDIT_ENTRIES);
        }


        final List<AuditEntry> auditEntries =
                auditingServiceRepository.findByTransactionId(command.getTransactionId());

        final int slaveNumAuditEntries = auditEntries.size();
        if (masterNumAuditEntries == slaveNumAuditEntries) {
            return null;
        }

        return String.format("Number of audit entries differs.  Master was %d (slave is %d)",
                masterNumAuditEntries, slaveNumAuditEntries);

    }

    @Inject
    AuditingServiceRepository auditingServiceRepository;


}
