package org.estatio.module.lease.dom;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.factory.FactoryService;

import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;

@DomainService(nature = NatureOfService.DOMAIN, repositoryFor = LeaseItemSource.class)
public class LeaseItemSourceRepository extends UdoDomainRepositoryAndFactory<LeaseItemSource> {

    public LeaseItemSourceRepository() {
        super(LeaseItemSourceRepository.class, LeaseItemSource.class);
    }

    public LeaseItemSource newSource(final LeaseItem item, final LeaseItem sourceItem){
        final LeaseItemSource leaseItemSource = factoryService.instantiate(LeaseItemSource.class);
        leaseItemSource.setItem(item);
        leaseItemSource.setSourceItem(sourceItem);
        persist(leaseItemSource);
        return leaseItemSource;
    }

    public LeaseItemSource findOrCreateSource(final LeaseItem item, final LeaseItem sourceItem){
        final LeaseItemSource sourceFound = findUnique(item, sourceItem);
        return sourceFound == null ? newSource(item, sourceItem) : sourceFound;
    }

    @Programmatic
    public List<LeaseItemSource> findByItem(final LeaseItem item) {
        return repositoryService.allMatches(new QueryDefault<>(LeaseItemSource.class,
                "findByItem", "item", item));
    }

    @Programmatic
    public List<LeaseItemSource> findBySourceItem(final LeaseItem sourceItem) {
        return repositoryService.allMatches(new QueryDefault<>(LeaseItemSource.class,
                "findBySourceItem", "sourceItem", sourceItem));
    }

    public LeaseItemSource findUnique(final LeaseItem item, final LeaseItem sourceItem) {
        return repositoryService.uniqueMatch(new QueryDefault<>(LeaseItemSource.class,
                "findByItemAndSourceItem", "item", item, "sourceItem", sourceItem));
    }

    @Inject
    FactoryService factoryService;

}
