package org.estatio.module.asset.dom.erv;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.module.asset.dom.Unit;
import org.estatio.module.currency.dom.Currency;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = EstimatedRentalValue.class
)
public class EstimatedRentalValueRepository {

    @Programmatic
    public EstimatedRentalValue findUnique(
            final Unit unit,
            final LocalDate date,
            final Type type
    ) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        EstimatedRentalValue.class,
                        "findUnique",
                        "unit", unit,
                        "date", date,
                        "type", type));
    }

    @Programmatic
    public EstimatedRentalValue create(
            final Unit unit,
            final LocalDate date,
            final Type type,
            final BigDecimal value,
            final Currency currency) {
        final EstimatedRentalValue estimatedRentalValue =
                new EstimatedRentalValue(
                        unit,
                        date,
                        type,
                        value,
                        currency);
        serviceRegistry2.injectServicesInto(estimatedRentalValue);
        repositoryService.persistAndFlush(estimatedRentalValue);
        return estimatedRentalValue;
    }

    @Programmatic
    public EstimatedRentalValue upsert(
            final Unit unit,
            final LocalDate date,
            final Type type,
            final BigDecimal value,
            final Currency currency
    ) {
        EstimatedRentalValue estimatedRentalValue = findUnique(unit, date, type);
        if (estimatedRentalValue == null) {
            estimatedRentalValue = create(unit, date, type, value, currency);
        } else {
            estimatedRentalValue.setValue(value);
            estimatedRentalValue.setCurrency(currency);
        }
        return estimatedRentalValue;
    }

    @Programmatic
    public List<EstimatedRentalValue> findByUnitAndType(final Unit unit, final Type type) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                EstimatedRentalValue.class,
                "findByUnitAndType",
                "unit", unit,
                "type", type));
    }

    @Programmatic
    public List<EstimatedRentalValue> listAll() {
        return repositoryService.allInstances(EstimatedRentalValue.class);
    }

    @Inject
    RepositoryService repositoryService;

    @Inject
    ServiceRegistry2 serviceRegistry2;
}
