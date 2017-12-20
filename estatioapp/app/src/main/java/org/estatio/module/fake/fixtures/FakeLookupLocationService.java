package org.estatio.module.fake.fixtures;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.isisaddons.wicket.gmap3.cpt.applib.Location;

import org.estatio.module.asset.dom.location.LocationLookupService;

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
