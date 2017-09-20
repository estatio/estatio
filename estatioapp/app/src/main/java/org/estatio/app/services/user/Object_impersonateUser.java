package org.estatio.app.services.user;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.services.factory.FactoryService;

import org.isisaddons.module.security.dom.role.ApplicationRole;
import org.isisaddons.module.security.dom.user.ApplicationUser;

import org.estatio.dom.party.Person;

@Mixin(method = "act")
public class Object_impersonateUser {

    private final Object object;

    public Object_impersonateUser(final Object object) {
        this.object = object;
    }

    @Action(restrictTo = RestrictTo.PROTOTYPING)
    @MemberOrder(sequence = "90.1")
    public Object act(
            final ApplicationUser applicationUser,
            @Nullable
            final List<ApplicationRole> applicationRoleList) {

        estatioImpersonateMenu.impersonate(applicationUser, applicationRoleList);
        return object;
    }
    public List<ApplicationRole> default1Act() {
        return estatioImpersonateMenu.default1Impersonate();
    }

    public boolean hideAct() {
        return estatioImpersonateMenu.hideImpersonate() || objectIsImpersonatablePerson();
    }

    private boolean objectIsImpersonatablePerson() {
        return object instanceof Person && !factoryService.mixin(Person_startImpersonating.class, object).hideAct();
    }

    @Inject
    EstatioImpersonateMenu estatioImpersonateMenu;

    @Inject FactoryService factoryService;

}
