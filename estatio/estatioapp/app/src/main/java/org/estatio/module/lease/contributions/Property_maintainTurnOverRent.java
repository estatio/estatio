package org.estatio.module.lease.contributions;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Named;

import org.incode.module.country.dom.impl.CountryRepository;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseTermRepository;
import org.estatio.module.lease.imports.LeaseTermForTurnoverRentManager;

@Mixin()
public class Property_maintainTurnOverRent {

    private final Property property;

    public Property_maintainTurnOverRent(final Property property) {
        this.property = property;
    }

    @ActionLayout(contributed = Contributed.AS_ACTION)
    public LeaseTermForTurnoverRentManager maintainTurnoverRent(
            @Named("Start date") final LocalDate startDate) {
        return new LeaseTermForTurnoverRentManager(property, startDate);
    }

    public List<LocalDate> choices0MaintainTurnoverRent() {
        return leaseTermRepository.findStartDatesByPropertyAndType(property, LeaseItemType.TURNOVER_RENT);
    }

    public boolean hideMaintainTurnoverRent(){
        return property.getCountry().equals(countryRepository.findCountry("SWE"));
    }

    @Inject
    LeaseTermRepository leaseTermRepository;

    @Inject
    CountryRepository countryRepository;

}
