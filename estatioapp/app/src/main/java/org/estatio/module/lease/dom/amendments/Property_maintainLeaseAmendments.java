package org.estatio.module.lease.dom.amendments;

import javax.annotation.Nullable;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;

import org.estatio.module.asset.dom.Property;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Mixin
public class Property_maintainLeaseAmendments {

    private final Property property;

    public Property_maintainLeaseAmendments(Property property) {
        this.property = property;
    }

    @Action()
    public LeaseAmendmentManager $$(@Nullable final LeaseAmendmentTemplate leaseAmendmentTemplate, @Nullable final LeaseAmendmentState leaseAmendmentState) {
        return new LeaseAmendmentManager(property, leaseAmendmentTemplate, leaseAmendmentState);
    }

    public List<LeaseAmendmentTemplate> choices0$$() {
        final List<LeaseAmendmentTemplate> templatesForAtPath = Arrays.asList(LeaseAmendmentTemplate.values())
                .stream()
                .filter(lat -> property.getAtPath().startsWith(lat.getAtPath()))
                .collect(Collectors.toList());
        if (property.getAtPath().startsWith("/ITA")) {
            return templatesForAtPath
                    .stream()
                    .filter(lat -> lat.getLeaseAmendmentType() == LeaseAmendmentType.COVID_WAVE_2)
                    .collect(Collectors.toList());
        }

        return templatesForAtPath;
    }

}
