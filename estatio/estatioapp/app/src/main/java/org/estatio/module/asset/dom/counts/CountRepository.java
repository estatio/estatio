package org.estatio.module.asset.dom.counts;

import java.math.BigInteger;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.module.asset.dom.Property;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = Count.class
)
public class CountRepository {

    @Programmatic
    public Count findUnique(
            final Property property,
            final Type type,
            final LocalDate date
    ) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        Count.class,
                        "findUnique",
                        "property", property,
                        "type", type,
                        "date", date));
    }

    @Programmatic
    public Count create(
            final Property property,
            final Type type,
            final LocalDate date,
            final BigInteger value) {
        final Count count = new Count(property, type, date, value);
        serviceRegistry2.injectServicesInto(count);
        repositoryService.persistAndFlush(count);
        return count;
    }

    @Programmatic
    public Count upsert(
            final Property property,
            final Type type,
            final LocalDate date,
            final BigInteger value
    ) {
        Count count = findUnique(property, type, date);
        if (count == null) {
            count = create(property, type, date, value);
        } else {
            count.setValue(value);
        }
        return count;
    }

    @Programmatic
    public List<Count> findByPropertyAndType(final Property property, final Type type) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                Count.class,
                "findByPropertyAndType",
                "property", property, "type", type));
    }

    public void remove(final Count count){
        repositoryService.removeAndFlush(count);
    }

    @Programmatic
    public List<Count> listAll() {
        return repositoryService.allInstances(Count.class);
    }

    @Inject
    RepositoryService repositoryService;

    @Inject
    ServiceRegistry2 serviceRegistry2;

}
