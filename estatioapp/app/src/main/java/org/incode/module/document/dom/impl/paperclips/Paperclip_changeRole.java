package org.incode.module.document.dom.impl.paperclips;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.document.DocumentModule;

@Mixin
public class Paperclip_changeRole {

    //region > constructor
    private final Paperclip paperclip;

    public Paperclip_changeRole(final Paperclip paperclip) {
        this.paperclip = paperclip;
    }
    //endregion


    public static class ActionDomainEvent extends DocumentModule.ActionDomainEvent<Paperclip_changeRole>  { }
    @Action(
            semantics = SemanticsOf.IDEMPOTENT,
            domainEvent = ActionDomainEvent.class
    )
    public Paperclip $$(
            @Parameter(optionality = Optionality.OPTIONAL, maxLength = Paperclip.RoleNameType.Meta.MAX_LEN)
            @ParameterLayout(named = "Document role")
            final String roleName
    ) {
        paperclip.setRoleName(roleName);
        return paperclip;
    }

    public String default0$$() {
        return paperclip.getRoleName();
    }



}
