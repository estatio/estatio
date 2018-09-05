package org.estatio.module.lease.contributions;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseRepository;

@Mixin()
public class Property_verifyServiceChargeItems {

    private final Property property;

    public Property_verifyServiceChargeItems(final Property property) {
        this.property = property;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public Property verifyServiceChargeItems(final LocalDate verificationDate) {
        leaseRepository.findLeasesByProperty(property).forEach(
                l->{
                    Lists.newArrayList(l.getItems()).forEach(
                            item->{
                                final List<LeaseItemType> typesForServiceCharges = Arrays.asList(
                                        LeaseItemType.SERVICE_CHARGE,
                                        LeaseItemType.MARKETING,
                                        LeaseItemType.PROPERTY_TAX
                                );
                                if (typesForServiceCharges.contains(item.getType())) {
                                    item.verifyUntil(verificationDate);
                                }
                            }
                    );
                }
        );
        return property;
    }

    @Inject
    private LeaseRepository leaseRepository;

}
