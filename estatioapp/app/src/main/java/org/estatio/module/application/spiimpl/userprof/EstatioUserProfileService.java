package org.estatio.module.application.spiimpl.userprof;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.userprof.UserProfileService;

import org.isisaddons.module.security.app.user.MeService;
import org.isisaddons.module.security.dom.user.ApplicationUser;

import org.estatio.module.base.dom.UdoDomainService;

@DomainService(nature = NatureOfService.VIEW_MENU_ONLY)
@DomainServiceLayout(
        menuBar = DomainServiceLayout.MenuBar.TERTIARY,
        menuOrder = "10.1"
)
public class EstatioUserProfileService extends UdoDomainService<EstatioUserProfileService>
                implements UserProfileService {

    public EstatioUserProfileService() {
        super(EstatioUserProfileService.class);
    }


    @Programmatic
    @Override
    public String userProfileName() {
        final ApplicationUser currentUser = meService.me();
        final String atPath = currentUser.getAtPath();
        if(atPath != null) {
            return String.format("%s @ %s", currentUser.getName(), atPath);
        } else {
            return String.format("%s", currentUser.getName());
        }
    }


    // //////////////////////////////////////

    @javax.inject.Inject
    MeService meService;

}
