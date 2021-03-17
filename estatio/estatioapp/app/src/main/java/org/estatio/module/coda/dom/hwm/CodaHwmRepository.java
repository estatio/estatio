package org.estatio.module.coda.dom.hwm;

import java.util.List;

import org.joda.time.LocalDateTime;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.repository.RepositoryService;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = CodaHwm.class,
        objectType = "coda.CodaHwmRepository"
)
public class CodaHwmRepository {

    public static final LocalDateTime LAST_RAN_DEFAULT = new LocalDateTime(2000,1,1,0,0);

    @Programmatic
    public List<CodaHwm> listAll() {
        return repositoryService.allInstances(CodaHwm.class);
    }

    @Programmatic
    public CodaHwm findByFeedNameAndCmpCode(
            final String feedName,
            final String cmpCode) {
        return repositoryService.uniqueMatch(
                new org.apache.isis.applib.query.QueryDefault<>(
                        CodaHwm.class,
                        "findByFeedNameAndCmpCode",
                        "feedName", feedName,
                        "cmpCode", cmpCode));
    }

    /**
     * If does not previously exist, then creates with a {@link CodaHwm#getLastRan() lastRan} set to an
     * epoch date of {@link CodaHwmRepository#LAST_RAN_DEFAULT}, namely 1-Jan-2000.
     *
     * @param feedName
     * @param cmpCode
     * @return
     */
    @Programmatic
    public CodaHwm findOrCreate(
            final String feedName,
            final String cmpCode) {
        CodaHwm hwm = findByFeedNameAndCmpCode(feedName, cmpCode);
        if (hwm == null) {
            hwm = repositoryService.persist(new CodaHwm(feedName, cmpCode, LAST_RAN_DEFAULT));
        }
        return hwm;
    }

    @Programmatic
    public CodaHwm update(
            final String feedName,
            final String cmpCode,
            final LocalDateTime lastRan) {
        CodaHwm hwm = findByFeedNameAndCmpCode(feedName, cmpCode);
        if (hwm == null) {
            throw new IllegalArgumentException(
                    String.format("No CodaHwm found for feedName: '%s', cmpCode: '%s'", feedName, cmpCode));
        } else {
            hwm.setLastRan(lastRan);
        }
        return hwm;
    }

    @javax.inject.Inject
    RepositoryService repositoryService;
}
