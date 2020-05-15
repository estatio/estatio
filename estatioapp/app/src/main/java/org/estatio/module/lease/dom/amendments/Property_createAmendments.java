package org.estatio.module.lease.dom.amendments;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;

import org.estatio.module.asset.dom.Property;

@Mixin
public class Property_createAmendments {

    private final Property property;

    public Property_createAmendments(Property property) {
        this.property = property;
    }

    @Action()
    public LeaseAmendmentManager $$(final AmendmentProposalType proposal) {
        return new LeaseAmendmentManager(property, proposal);
    }

}
