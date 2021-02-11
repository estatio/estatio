package org.estatio.module.lease.dom.amendments;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.asset.dom.Property;

@Mixin
public class Property_maintainLeaseAmendments {

    private final Property property;

    public Property_maintainLeaseAmendments(Property property) {
        this.property = property;
    }

    @Action()
    public LeaseAmendmentManager $$(@Nullable final LeaseAmendmentTemplate leaseAmendmentTemplate, @Nullable final LeaseAmendmentState leaseAmendmentState) {
        final LeaseAmendmentManager leaseAmendmentManager = new LeaseAmendmentManager(property, leaseAmendmentTemplate,
                leaseAmendmentState);
        serviceRegistry2.injectServicesInto(leaseAmendmentManager);
        return leaseAmendmentManager;
    }

    public List<LeaseAmendmentTemplate> choices0$$() {
        final List<LeaseAmendmentTemplate> templatesForAtPath = Arrays.asList(LeaseAmendmentTemplate.values())
                .stream()
                .filter(lat -> property.getAtPath().startsWith(lat.getAtPath()))
                .collect(Collectors.toList());
            return templatesForAtPath
                    .stream()
                    .filter(lat -> lat.getLeaseAmendmentType() == LeaseAmendmentType.COVID_WAVE_2)
                    .collect(Collectors.toList());
    }

    @Inject ServiceRegistry2 serviceRegistry2;

}
