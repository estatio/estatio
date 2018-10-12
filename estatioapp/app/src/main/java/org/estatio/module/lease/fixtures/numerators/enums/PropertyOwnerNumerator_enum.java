package org.estatio.module.lease.fixtures.numerators.enums;

import org.apache.isis.applib.fixturescripts.PersonaWithBuilderScript;
import org.apache.isis.applib.fixturescripts.PersonaWithFinder;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.fixtures.property.enums.PropertyAndUnitsAndOwnerAndManager_enum;
import org.estatio.module.lease.fixtures.numerators.builders.PropertyOwnerNumeratorBuilder;
import org.estatio.module.lease.dom.EstatioApplicationTenancyRepositoryForLease;
import org.estatio.module.lease.dom.invoicing.NumeratorForCollectionRepository;
import org.estatio.module.numerator.dom.Numerator;
import org.estatio.module.party.dom.Organisation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(chain = true)
public enum PropertyOwnerNumerator_enum implements PersonaWithFinder<Numerator>, PersonaWithBuilderScript<Numerator, PropertyOwnerNumeratorBuilder> {

    BudNl   (PropertyAndUnitsAndOwnerAndManager_enum.BudNl),
    RonIt   (PropertyAndUnitsAndOwnerAndManager_enum.RonIt),
    GraIt   (PropertyAndUnitsAndOwnerAndManager_enum.GraIt),
    HanSe   (PropertyAndUnitsAndOwnerAndManager_enum.HanSe),
    KalNl   (PropertyAndUnitsAndOwnerAndManager_enum.KalNl),
    MacFr   (PropertyAndUnitsAndOwnerAndManager_enum.MacFr),
    MnsFr   (PropertyAndUnitsAndOwnerAndManager_enum.MnsFr),
    OxfGb   (PropertyAndUnitsAndOwnerAndManager_enum.OxfGb),
    VivFr   (PropertyAndUnitsAndOwnerAndManager_enum.VivFr);

    private final PropertyAndUnitsAndOwnerAndManager_enum propertyAndUnitsAndOwnerAndManager_d;

    @Override
    public Numerator findUsing(final ServiceRegistry2 serviceRegistry) {

        final EstatioApplicationTenancyRepositoryForLease estatioApplicationTenancyRepository =
                serviceRegistry.lookupService(EstatioApplicationTenancyRepositoryForLease.class);

        final Property property = propertyAndUnitsAndOwnerAndManager_d.findUsing(serviceRegistry);
        final Organisation owner = propertyAndUnitsAndOwnerAndManager_d.getOwner_d().findUsing(serviceRegistry);
        final ApplicationTenancy applicationTenancy =
                estatioApplicationTenancyRepository.findOrCreateTenancyFor(property, owner);

        final NumeratorForCollectionRepository repository = serviceRegistry.lookupService(NumeratorForCollectionRepository.class);
        return repository.findInvoiceNumberNumerator(property, applicationTenancy);
    }

    @Override
    public PropertyOwnerNumeratorBuilder builder() {
        return new PropertyOwnerNumeratorBuilder()
                .setPrereq((f,ec) -> f.setProperty(f.objectFor(propertyAndUnitsAndOwnerAndManager_d, ec)))
                .setPrereq((f,ec) -> f.setOwner(f.objectFor(propertyAndUnitsAndOwnerAndManager_d.getOwner_d(), ec)))
                ;
    }
}
