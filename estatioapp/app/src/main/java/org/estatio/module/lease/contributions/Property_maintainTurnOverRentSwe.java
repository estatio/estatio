package org.estatio.module.lease.contributions;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.services.clock.ClockService;

import org.incode.module.country.dom.impl.CountryRepository;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.lease.imports.LeaseTermForTurnoverRentFixedImportManager;

@Mixin()
public class Property_maintainTurnOverRentSwe {

    private final Property property;

    public Property_maintainTurnOverRentSwe(final Property property) {
        this.property = property;
    }

    @ActionLayout(contributed = Contributed.AS_ACTION)
    public LeaseTermForTurnoverRentFixedImportManager maintainTurnoverRent(
            final int year
    ) {
        return new LeaseTermForTurnoverRentFixedImportManager(property, year);
    }

    public int default0MaintainTurnoverRent() {
        return clockService.now().getYear();
    }

    public boolean hideMaintainTurnoverRent(){
        return !property.getCountry().equals(countryRepository.findCountry("SWE"));
    }

    @Inject
    ClockService clockService;

    @Inject
    CountryRepository countryRepository;

}
