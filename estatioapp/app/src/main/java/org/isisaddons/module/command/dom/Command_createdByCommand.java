package org.isisaddons.module.command.dom;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.HasTransactionId;
import org.apache.isis.applib.services.command.Command;
import org.apache.isis.core.runtime.system.transaction.IsisTransaction;

import org.isisaddons.module.audit.dom.AuditEntry;
import org.isisaddons.module.audit.dom.AuditingServiceRepository;
import org.isisaddons.module.command.CommandModule;

/**
 * Mixin to return the {@link Command} that was used to initially persist the target of <i>this</i> {@link Command}.
 *
 * <p>
 *     The audit trail is inspected, searching for the first [NEW] {@link AuditEntry}.  From this the corresponding
 *     {@link Command} is retrieved, correlated on {@link HasTransactionId#getTransactionId() transactionId}.
 * </p>
 */
@Mixin(method = "act")
public class Command_createdByCommand {

    public static class ActionDomainEvent
            extends CommandModule.ActionDomainEvent<Command_createdByCommand> { }


    private final CommandJdo commandJdo;
    public Command_createdByCommand(final CommandJdo commandJdo) {
        this.commandJdo = commandJdo;
    }


    @Action(
            domainEvent = ActionDomainEvent.class,
            semantics = SemanticsOf.SAFE
    )
    @ActionLayout(
            contributed = Contributed.AS_ACTION,
            named = "Created by"
    )
    @MemberOrder(name = "targetStr", sequence = "2")
    public CommandJdo prop() {
        return findCreationCommand();
    }

    public String disableProp() {
        return findCreationCommand() == null ? "No command found": null;
    }

    private CommandJdo findCreationCommand() {
        final String targetStr = commandJdo.getTargetStr();
        final AuditEntry firstEntry = auditingServiceRepository.findFirstByTarget(targetStr);
        if(firstEntry == null) {
            return null;
        }
        final String preValue = firstEntry.getPreValue();
        if(! IsisTransaction.Placeholder.NEW.toString().equals(preValue)) {
            return null;
        }
        final CommandJdo commandIfAny =
                commandServiceJdoRepository.findByTransactionId(firstEntry.getTransactionId());
        return commandIfAny;
    }

    @javax.inject.Inject
    AuditingServiceRepository auditingServiceRepository;
    @javax.inject.Inject
    CommandServiceJdoRepository commandServiceJdoRepository;

}
