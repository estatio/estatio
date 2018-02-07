package org.estatio.module.base.spiimpl.commandreplay;

import java.util.Objects;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.conmap.spi.CommandDtoProcessorService;
import org.apache.isis.applib.services.command.Command;
import org.apache.isis.core.runtime.system.transaction.IsisTransaction;
import org.apache.isis.schema.cmd.v1.CommandDto;
import org.apache.isis.schema.utils.CommandDtoUtils;

import org.isisaddons.module.audit.dom.AuditingServiceRepository;
import org.isisaddons.module.command.dom.CommandJdo;
import org.isisaddons.module.command.replay.spi.CommandReplayAnalyserAbstract;

/**
 * We ignore deleted audit entries because these can vary between master and slave
 * (cascade delete means that there may be fewer on the slave, for example)
 */
@DomainService(
        nature = NatureOfService.DOMAIN
)
public class CommandReplayAnalyserNonDeletedAuditEntries extends CommandReplayAnalyserAbstract implements
        CommandDtoProcessorService {

    public static final String ANALYSIS_KEY = "isis.services."
            + CommandReplayAnalyserNonDeletedAuditEntries.class.getSimpleName()
            + ".analysis";

    public static final String USERDATA_KEY_NUMBER_NON_DELETED_AUDIT_ENTRIES = "numberNonDeletedAuditEntries";

    public CommandReplayAnalyserNonDeletedAuditEntries() {
        super(ANALYSIS_KEY);
    }

    /**
     * Hook for the master, enriches the DTO.
     */
    @Override
    public CommandDto process(
            final Command command, final CommandDto commandDto) {

        if(command instanceof CommandJdo) {

            final long nonDeletedAuditEntries = countNonDeletedAuditEntriesFor(command);

            CommandDtoUtils.setUserData(commandDto,
                    USERDATA_KEY_NUMBER_NON_DELETED_AUDIT_ENTRIES, ""+nonDeletedAuditEntries);
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
                CommandDtoUtils.getUserData(dto, USERDATA_KEY_NUMBER_NON_DELETED_AUDIT_ENTRIES);

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
                    masterNumAuditEntriesStr, USERDATA_KEY_NUMBER_NON_DELETED_AUDIT_ENTRIES);
        }


        final long slaveNumAuditEntries = countNonDeletedAuditEntriesFor(command);

        if (masterNumAuditEntries == slaveNumAuditEntries) {
            return null;
        }

        return String.format("Number of (non-deleted) audit entries differs.  Master was %d (slave is %d)",
                masterNumAuditEntries, slaveNumAuditEntries);

    }

    private long countNonDeletedAuditEntriesFor(final Command command) {
        return auditingServiceRepository.findByTransactionId(command.getTransactionId())
                .stream()
                .filter(entry -> !Objects.equals(
                        entry.getPostValue(),
                        IsisTransaction.Placeholder.DELETED.toString()))
                .count();
    }

    @Inject
    AuditingServiceRepository auditingServiceRepository;


}
