package org.estatio.module.application.contributions.impersonation;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.RestrictTo;

import org.isisaddons.module.security.dom.role.ApplicationRole;
import org.isisaddons.module.security.dom.user.ApplicationUser;
import org.isisaddons.module.security.dom.user.ApplicationUserRepository;

import org.estatio.module.application.platform.security.EstatioImpersonateMenu;
import org.estatio.module.application.platform.security.EstatioUserService;
import org.estatio.module.party.dom.Person;

@Mixin(method = "act")
public class Person_startImpersonating {

    private final Person person;

    public Person_startImpersonating(final Person object) {
        this.person = object;
    }

    @Action(restrictTo = RestrictTo.PROTOTYPING)
    @MemberOrder(sequence = "90.1")
    public Object act(
            @Nullable
            final List<ApplicationRole> applicationRoleList) {
        estatioImpersonateMenu.impersonate(findApplicationUser(), applicationRoleList);
        return person;
    }
    public List<ApplicationRole> default0Act() {
        return estatioImpersonateMenu.default1Impersonate();
    }

    public boolean hideAct() {
        return estatioImpersonateMenu.hideImpersonate() || findApplicationUser() == null;
    }

    private ApplicationUser findApplicationUser() {
        if(person.getUsername() == null) {
            return null;
        }
        return applicationUserRepository.findByUsername(person.getUsername());
    }

    @Inject
    EstatioImpersonateMenu estatioImpersonateMenu;

    @Inject
    EstatioUserService estatioUserService;

    @Inject
    ApplicationUserRepository applicationUserRepository;



}
