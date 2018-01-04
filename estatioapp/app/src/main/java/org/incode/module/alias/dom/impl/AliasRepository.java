package org.incode.module.alias.dom.impl;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.bookmark.Bookmark;
import org.apache.isis.applib.services.bookmark.BookmarkService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.incode.module.alias.dom.spi.AliasType;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = Alias.class
)
public class AliasRepository {

    //region > findByAliased (programmatic)
    @Programmatic
    public List<Alias> findByAliased(final Object aliased) {
        if(aliased == null) {
            return null;
        }
        final Bookmark bookmark = bookmarkService.bookmarkFor(aliased);
        if(bookmark == null) {
            return null;
        }
        return repositoryService.allMatches(
                new QueryDefault<>(Alias.class,
                        "findByAliased",
                        "aliasedStr", bookmark.toString()));
    }
    //endregion

    //region > create (programmatic)
    @Programmatic
    public Alias create(
            final Object aliased,
            final String atPath,
            final AliasType aliasType,
            final String aliasReference) {

        Class<? extends Alias> aliasSubtype = subtypeClassFor(aliased);

        final Alias alias = repositoryService.instantiate(aliasSubtype);

        alias.setAtPath(atPath);
        alias.setAliasTypeId(aliasType.getId());
        alias.setReference(aliasReference);

        final Bookmark bookmark = bookmarkService.bookmarkFor(aliased);
        alias.setAliased(aliased);
        alias.setAliasedStr(bookmark.toString());

        repositoryService.persist(alias);

        return alias;
    }

    private Class<? extends Alias> subtypeClassFor(final Object aliased) {
        Class<?> aliasedDomainClass = aliased.getClass();
        for (SubtypeProvider subtypeProvider : subtypeProviders) {
            Class<? extends Alias> aliasSubtype = subtypeProvider.subtypeFor(aliasedDomainClass);
            if(aliasSubtype != null) {
                return aliasSubtype;
            }
        }
        throw new IllegalStateException(String.format(
                "No subtype of Alias was found for '%s'; implement the AliasRepository.SubtypeProvider SPI",
                aliasedDomainClass.getName()));
    }

    //endregion

    //region > remove (programmatic)
    @Programmatic
    public void remove(Alias alias) {
        repositoryService.removeAndFlush(alias);
    }
    //endregion


    //region > SubtypeProvider SPI

    /**
     * SPI to be implemented (as a {@link DomainService}) for any domain object to which {@link Alias}es can be
     * attached.
     */
    public interface SubtypeProvider {
        @Programmatic
        Class<? extends Alias> subtypeFor(Class<?> domainObject);
    }
    /**
     * Convenience adapter to help implement the {@link SubtypeProvider} SPI.
     */
    public abstract static class SubtypeProviderAbstract implements SubtypeProvider {
        private final Class<?> aliasedDomainType;
        private final Class<? extends Alias> aliasSubtype;

        protected SubtypeProviderAbstract(final Class<?> aliasedDomainType, final Class<? extends Alias> aliasSubtype) {
            this.aliasedDomainType = aliasedDomainType;
            this.aliasSubtype = aliasSubtype;
        }

        @Override
        public Class<? extends Alias> subtypeFor(final Class<?> candidateAliasedDomainType) {
            return aliasedDomainType.isAssignableFrom(candidateAliasedDomainType) ? aliasSubtype : null;
        }
    }

    //endregion

    //region > injected services

    @javax.inject.Inject
    RepositoryService repositoryService;

    @javax.inject.Inject ServiceRegistry2 serviceRegistry;

    @javax.inject.Inject
    BookmarkService bookmarkService;

    @Inject
    List<SubtypeProvider> subtypeProviders;

    //endregion

}
