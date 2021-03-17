package org.incode.module.communications.dom.spi;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.isisaddons.module.security.app.user.MeService;
import org.isisaddons.module.security.dom.user.ApplicationUser;

public interface CurrentUserEmailAddressProvider {

    @Programmatic
    String currentUserEmailAddress();

    /**
     * Default implementation.
     */
    @DomainService(
            nature = NatureOfService.DOMAIN
    )
    public static class UsingMeService implements CurrentUserEmailAddressProvider {

        @Override
        public String currentUserEmailAddress() {
            if(meService == null) {
                throw new IllegalStateException("Security module has not been added to the AppManifest, and no other implementation of CurrentUserEmailAddressProvider has been supplied.");
            }
            final ApplicationUser currentUser = meService.me();
            return currentUser.getEmailAddress();
        }

        @Inject
        MeService meService;

    }

}
