package org.estatio.module.lease.dom.amendments;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;

import org.estatio.module.asset.dom.Property;

@Mixin
public class Property_createLeaseAmendments {

    private final Property property;

    public Property_createLeaseAmendments(Property property) {
        this.property = property;
    }

    @Action()
    public LeaseAmendmentManager $$(final LeaseAmendmentType leaseAmendmentType) {
        return new LeaseAmendmentManager(property, leaseAmendmentType);
    }

}
