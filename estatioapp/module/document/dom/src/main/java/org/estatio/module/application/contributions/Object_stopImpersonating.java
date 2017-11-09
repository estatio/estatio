package org.estatio.module.application.contributions;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.RestrictTo;

import org.estatio.module.application.platform.security.EstatioImpersonateMenu;

@Mixin(method = "act")
public class Object_stopImpersonating {

    private final Object object;

    public Object_stopImpersonating(final Object object) {
        this.object = object;
    }

    @Action(restrictTo = RestrictTo.PROTOTYPING)
    @MemberOrder(sequence = "90.2")
    public Object act() {

        estatioImpersonateMenu.stopImpersonating();
        return object;
    }

    public boolean hideAct() {
        return estatioImpersonateMenu.hideStopImpersonating();
    }


    @Inject
    EstatioImpersonateMenu estatioImpersonateMenu;


}
