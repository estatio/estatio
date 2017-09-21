package org.estatio.capex.dom.task;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.dom.party.Person;

@Mixin(method = "act")
public class Person_transferTasks {

    protected final Person mixee;

    public Person_transferTasks(final Person mixee) {
        this.mixee = mixee;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public TransferTaskManager act(final Person transferToOrFrom) {
        final TransferTaskManager taskManager = new TransferTaskManager(mixee, transferToOrFrom, TransferTaskManager.Mode.SAME_ROLES);
        return serviceRegistry2.injectServicesInto(taskManager);
    }


    @Inject
    ServiceRegistry2 serviceRegistry2;


}
