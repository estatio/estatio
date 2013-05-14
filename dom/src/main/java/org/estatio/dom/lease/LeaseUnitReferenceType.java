package org.estatio.dom.lease;

import com.google.common.collect.Ordering;

import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.DomainObjectContainer;

public enum LeaseUnitReferenceType {
    BRAND("Brand", LeaseUnitBrand.class), SECTOR("Sector", LeaseUnitSector.class), ACTIVITY("Activity", LeaseUnitActivity.class);

    private final String title;
    private final Class<? extends LeaseUnitReference> clss;
    public static final Ordering<LeaseUnitReferenceType> ORDERING_NATURAL = Ordering.<LeaseUnitReferenceType> natural().nullsFirst();

    private LeaseUnitReferenceType(String title, Class<? extends LeaseUnitReference> clss) {
        this.title = title;
        this.clss = clss;
    }

    public String title() {
        return title;
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
