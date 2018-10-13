package org.estatio.module.coda.dom.hwm;

import java.util.List;

import org.joda.time.LocalDateTime;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.module.coda.dom.supplier.CodaBankAccount;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = CodaHwm.class
)
public class CodaHwmRepository {

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

    @Programmatic
    public CodaHwm create(
            final String feedName,
            final String cmpCode,
            final LocalDateTime lastRan) {
        return repositoryService.persist(new CodaHwm(feedName, cmpCode, lastRan));
    }

    /**
     * Similar to {@link #upsert(String, String, LocalDateTime)}, but will NOT update any fields for
     * a {@link CodaBankAccount} that already exists.
     */
    @Programmatic
    public CodaHwm findOrCreate(
            final String feedName,
            final String cmpCode,
            final LocalDateTime lastRan) {
        CodaHwm hwm = findByFeedNameAndCmpCode(feedName, cmpCode);
        if (hwm == null) {
            hwm = create(feedName, cmpCode, lastRan);
        }
        return hwm;
    }

    /**
     * Same as {@link #findByFeedNameAndCmpCode(String, String)} , but will update any non-key fields
     * if the {@link CodaHwm} already exists.
     */
    @Programmatic
    public CodaHwm upsert(
            final String feedName,
            final String cmpCode,
            final LocalDateTime lastRan) {
        CodaHwm hwm = findByFeedNameAndCmpCode(feedName, cmpCode);
        if (hwm == null) {
            hwm = create(feedName, cmpCode, lastRan);
        } else {
            hwm.setLastRan(lastRan);
        }
        return hwm;
    }

    @javax.inject.Inject
    RepositoryService repositoryService;
}
