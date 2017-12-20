package org.incode.module.communications.dom.impl.comms;

import java.util.List;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.isisaddons.module.command.dom.CommandJdo;
import org.isisaddons.module.command.dom.T_backgroundCommands;

@Mixin
public class Communication_backgroundCommands extends T_backgroundCommands<Communication> {

    public Communication_backgroundCommands(final Communication domainObject) {
        super(domainObject);
    }

    public static class ActionDomainEvent extends T_backgroundCommands.ActionDomainEvent {}

    @Action(
            semantics = SemanticsOf.SAFE,
            domainEvent = ActionDomainEvent.class
    )
    public List<CommandJdo> $$() {
        return super.$$();
    }

}
