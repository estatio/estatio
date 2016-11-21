package org.estatio.integtests.fakes;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.isisaddons.wicket.gmap3.cpt.applib.Location;
import org.isisaddons.wicket.gmap3.cpt.service.LocationLookupService;

@DomainService(nature = NatureOfService.DOMAIN, menuOrder = "1")
public class FakeLookupLocationService extends LocationLookupService {
    public String getId() {
        return getClass().getName();
    }

    @Override
    public Location lookup(final String description) {
        return null;
    }
}
