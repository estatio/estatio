package org.estatio.capex.dom.coda;

import java.util.Optional;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.jdosupport.IsisJdoSupport;

import org.estatio.dom.UdoDomainRepositoryAndFactory;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = CodaElement.class
)
public class CodaElementRepository extends UdoDomainRepositoryAndFactory<CodaElement> {

    public CodaElementRepository() {
        super(CodaElementRepository.class, CodaElement.class);
    }

    @Programmatic
    public java.util.List<CodaElement> listAll() {
        return allInstances(CodaElement.class);
    }

    @Programmatic
    public Optional<CodaElement> findByLevelAndCode(
            final CodaElementLevel level,
            final String code
    ) {
        final QCodaElement q = QCodaElement.candidate();
        return isisJdoSupport.executeQuery(CodaElement.class,
                q.level.eq(level)
                        .and(q.code.eq(code)))
                .stream().findFirst();
    }

    @Programmatic
    public Optional<CodaElement> findByLevelAndName(
            final CodaElementLevel level,
            final String name
    ) {
        final QCodaElement q = QCodaElement.candidate();
        return isisJdoSupport.executeQuery(CodaElement.class,
                q.level.eq(level)
                        .and(q.code.eq(name)))
                .stream().findFirst();
    }

    @Programmatic
    public CodaElement create(
            final CodaElementLevel level,
            final String code,
            final String name) {
        final CodaElement instance = newTransientInstance(CodaElement.class);
        instance.setCode(code);
        instance.setLevel(level);
        instance.setName(name);
        persistIfNotAlready(instance);
        return instance;
    }

    @Programmatic
    public CodaElement findOrCreate(
            final CodaElementLevel level,
            final String code,
            final String name) {
        Optional<CodaElement> codaElement = findByLevelAndCode(level, code);

        if (!codaElement.isPresent()) {
            return create(level, code, name);



        } else {
            return codaElement.get();
        }
    }

    @Inject
    IsisJdoSupport isisJdoSupport;

}
