package org.estatio.dom.lease;

import com.google.common.collect.Ordering;

import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.DomainObjectContainer;

import org.estatio.dom.utils.StringUtils;

public enum LeaseUnitReferenceType {
    BRAND(LeaseUnitBrand.class), 
    SECTOR(LeaseUnitSector.class), 
    ACTIVITY(LeaseUnitActivity.class);

    private final Class<? extends LeaseUnitReference> clss;
    public static final Ordering<LeaseUnitReferenceType> ORDERING_NATURAL = Ordering.<LeaseUnitReferenceType> natural().nullsFirst();

    private LeaseUnitReferenceType(Class<? extends LeaseUnitReference> clss) {
        this.clss = clss;
    }

    public String title() {
        return StringUtils.enumTitle(this.name());
    }

    public LeaseUnitReference create(DomainObjectContainer container) {
        try {
            LeaseUnitReference instance = container.newTransientInstance(clss);
            return instance;
        } catch (Exception ex) {
            throw new ApplicationException(ex);
        }
    }
}
