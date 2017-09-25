package org.estatio.app.services.user;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.services.message.MessageService;

import org.isisaddons.module.security.app.user.MeService;
import org.isisaddons.module.security.dom.role.ApplicationRole;
import org.isisaddons.module.security.dom.role.ApplicationRoleRepository;
import org.isisaddons.module.security.dom.user.ApplicationUser;
import org.isisaddons.module.security.dom.user.ApplicationUserRepository;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "estatio.EstatioImpersonateMenu"
)
@DomainServiceLayout(
        menuBar = DomainServiceLayout.MenuBar.TERTIARY,
        menuOrder = "30"
)
public class EstatioImpersonateMenu {

    @Action(restrictTo = RestrictTo.PROTOTYPING)
    @MemberOrder(sequence = "1")
    public void impersonate(
            final ApplicationUser applicationUser,
            @Nullable
            final List<ApplicationRole> applicationRoleList) {

        if(estatioUserService.isImpersonating()) {
            stopImpersonating();
        }

        final List<String> roleNames = asRoleNames(applicationRoleList);

        estatioUserService.setUser(applicationUser.getUsername(), roleNames);

        messageService.informUser("Now impersonating " + applicationUser.getName());

    }
    public List<ApplicationRole> default1Impersonate() {
        return Lists.newArrayList(meService.me().getRoles());
    }

    private List<String> asRoleNames(final List<ApplicationRole> applicationRoleList) {
        if(applicationRoleList == null) {
            return Collections.emptyList();
        }
        return applicationRoleList.stream().
                                   map(ApplicationRole::getName).
                                   collect(Collectors.toList());
    }

    public boolean hideImpersonate() {
        return !estatioUserService.isAvailable();
    }



    @Action(restrictTo = RestrictTo.PROTOTYPING)
    @MemberOrder(sequence = "2")
    public void stopImpersonating() {

        estatioUserService.reset();

        messageService.informUser("No longer impersonating another user");

    }

    public boolean hideStopImpersonating() {
        return !estatioUserService.isAvailable() || !estatioUserService.isImpersonating();
    }




    @Inject
    MessageService messageService;

    @Inject
    EstatioUserService estatioUserService;

    @Inject
    ApplicationUserRepository applicationUserRepository;

    @Inject
    MeService meService;

    @Inject
    ApplicationRoleRepository applicationRoleRepository;


}
