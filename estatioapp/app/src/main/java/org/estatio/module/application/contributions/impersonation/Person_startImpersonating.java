package org.estatio.module.application.contributions.impersonation;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.axonframework.eventhandling.annotation.EventHandler;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.services.eventbus.AbstractDomainEvent;
import org.apache.isis.applib.services.factory.FactoryService;

import org.isisaddons.module.security.dom.role.ApplicationRole;
import org.isisaddons.module.security.dom.role.ApplicationRoleRepository;
import org.isisaddons.module.security.dom.user.ApplicationUser;
import org.isisaddons.module.security.dom.user.ApplicationUserRepository;

import org.incode.module.userimpersonate.app.ImpersonationService;
import org.incode.module.userimpersonate.contributions.Object_impersonateUser;

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
            @ParameterLayout(describedAs = "If set, then the roles specified below are used.  Otherwise uses roles of the user.")
            final boolean useExplicitRolesBelow,
            @ParameterLayout(describedAs = "Only used if 'useExplicitRolesBelow' is set, otherwise is ignored.")
            @Nullable
            final List<ApplicationRole> applicationRoleList) {
        impersonationService.impersonate(findApplicationUser(), useExplicitRolesBelow, applicationRoleList);
        return person;
    }
    public boolean default0Act() {
        return false;
    }
    public List<ApplicationRole> default1Act() {
        return applicationRoleRepository.allRoles();
    }

    public boolean hideAct() {
        return impersonationService.hideImpersonate() || findApplicationUser() == null;
    }

    private ApplicationUser findApplicationUser() {
        if(person.getUsername() == null) {
            return null;
        }
        return applicationUserRepository.findByUsername(person.getUsername());
    }

    @Inject
    ImpersonationService impersonationService;

    @Inject
    ApplicationRoleRepository applicationRoleRepository;

    @Inject
    ApplicationUserRepository applicationUserRepository;



    @DomainService(nature = NatureOfService.DOMAIN)
    public static class HideMixinIfMixeeIsPerson extends AbstractSubscriber {

        @EventHandler
        public void on(Object_impersonateUser.ActionDomainEvent ev) {
            if(ev.getEventPhase() == AbstractDomainEvent.Phase.HIDE) {
                if(shouldHide(ev)) {
                    ev.hide();
                }
            }
        }

        boolean shouldHide(final Object_impersonateUser.ActionDomainEvent ev) {
            final Object domainObject = ev.getMixedIn();
            return domainObject instanceof Person &&
                    !factoryService.mixin(Person_startImpersonating.class, domainObject).hideAct();
        }

        @Inject
        FactoryService factoryService;
    }

}
