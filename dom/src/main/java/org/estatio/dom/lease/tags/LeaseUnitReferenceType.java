package org.estatio.dom.lease.tags;

import com.google.common.collect.Ordering;

import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.DomainObjectContainer;

import org.estatio.dom.PowerType;
import org.estatio.dom.utils.StringUtils;

public enum LeaseUnitReferenceType implements PowerType<LeaseUnitReference> {
    
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
    
    public static Ordering<LeaseUnitReferenceType> ORDERING_BY_TYPE = 
            Ordering.<LeaseUnitReferenceType> natural().nullsFirst();

}
