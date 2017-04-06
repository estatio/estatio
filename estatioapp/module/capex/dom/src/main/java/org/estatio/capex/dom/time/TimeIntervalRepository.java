package org.estatio.capex.dom.time;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = TimeInterval.class
)
public class TimeIntervalRepository {

    @Programmatic
    public List<TimeInterval> listAll() {
        return repositoryService.allInstances(TimeInterval.class);
    }

    @Programmatic
    public TimeInterval findByName(final String name) {
        return repositoryService.uniqueMatch(
                new org.apache.isis.applib.query.QueryDefault<>(
                        TimeInterval.class,
                        "findByName",
                        "name", name));
    }

    @Programmatic
    public List<TimeInterval> findByStartDateAndCalendarType(final LocalDate startDate, final CalendarType calendarType) {
        return repositoryService.allMatches(
                new org.apache.isis.applib.query.QueryDefault<>(
                        TimeInterval.class,
                        "findByStartDateAndCalendarType",
                        "startDate", startDate,
                        "calendarType", calendarType));
    }

    @Programmatic
    public TimeInterval create(
            final String name,
            final LocalDate startDate,
            final LocalDate endDate,
            final CalendarType calendarType,
            final TimeInterval naturalParent,
            final TimeInterval financialParent) {
        final TimeInterval timeInterval = new TimeInterval(name, startDate, endDate, calendarType, naturalParent, financialParent);
        serviceRegistry2.injectServicesInto(timeInterval);
        repositoryService.persist(timeInterval);
        return timeInterval;
    }

    @Programmatic
    public TimeInterval findOrCreate(
            final String name,
            final LocalDate startDate,
            final LocalDate endDate,
            final CalendarType calendarType,
            final TimeInterval naturalParent,
            final TimeInterval financialParent) {

        TimeInterval timeInterval = findByName(name);
        if (timeInterval == null) {
            timeInterval = create(name, startDate, endDate, calendarType, naturalParent, financialParent);
        }
        return timeInterval;
    }

    @Inject
    RepositoryService repositoryService;

    @Inject
    ServiceRegistry2 serviceRegistry2;
}
