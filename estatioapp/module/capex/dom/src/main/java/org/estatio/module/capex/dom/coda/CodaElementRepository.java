package org.estatio.module.capex.dom.coda;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.jdosupport.IsisJdoSupport;

import org.incode.module.base.dom.utils.StringUtils;

import org.estatio.capex.dom.coda.QCodaElement;
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
    public List<CodaElement> searchByCodeOrName(final String regex){
        return allMatches(
                "searchByCodeOrName",
                "regex", regex);
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

    public List<CodaElement> autoComplete(final String pattern){
        return searchByCodeOrName(StringUtils.wildcardToCaseInsensitiveRegex(pattern));
    }

    @Inject
    IsisJdoSupport isisJdoSupport;

}
