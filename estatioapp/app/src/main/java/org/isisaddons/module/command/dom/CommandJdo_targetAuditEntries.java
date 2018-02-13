package org.isisaddons.module.command.dom;

import java.util.List;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.isisaddons.module.audit.dom.AuditEntry;
import org.isisaddons.module.audit.dom.AuditingServiceRepository;
import org.isisaddons.module.command.CommandModule;

@Mixin(method = "coll")
public class CommandJdo_targetAuditEntries {


    public static class ActionDomainEvent
            extends CommandModule.ActionDomainEvent<CommandJdo_targetAuditEntries> { }


    private final CommandJdo commandJdo;
    public CommandJdo_targetAuditEntries(final CommandJdo commandJdo) {
        this.commandJdo = commandJdo;
    }


    @Action(
            domainEvent = ActionDomainEvent.class,
            semantics = SemanticsOf.SAFE
    )
    @ActionLayout(
            contributed = Contributed.AS_ASSOCIATION
    )
    @CollectionLayout(
            defaultView = "table"
    )
    @MemberOrder(sequence = "100.100")
    public List<AuditEntry> coll() {
        final String targetStr = commandJdo.getTargetStr();
        return auditingServiceRepository.findRecentByTarget(targetStr);
    }


    @javax.inject.Inject
    AuditingServiceRepository auditingServiceRepository;
    
}
