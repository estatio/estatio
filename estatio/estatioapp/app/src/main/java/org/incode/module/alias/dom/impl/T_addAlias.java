package org.incode.module.alias.dom.impl;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.alias.AliasModule;
import org.incode.module.alias.dom.spi.AliasType;
import org.incode.module.alias.dom.spi.AliasTypeRepository;
import org.incode.module.alias.dom.spi.ApplicationTenancyRepository;

public abstract class T_addAlias<T> {

    //region  > (injected)
    @Inject
    AliasRepository aliasRepository;
    @Inject
    List<ApplicationTenancyRepository> applicationTenancyRepositories;
    @Inject
    List<AliasTypeRepository> aliasTypeRepositories;
    //endregion

    //region > constructor
    private final T aliased;

    public T_addAlias(final T aliased) {
        this.aliased = aliased;
    }

    public Object getAliased() {
        return aliased;
    }
    //endregion

    //region > $$
    public static class DomainEvent extends AliasModule.ActionDomainEvent<T_addAlias> { }

    @Action(
            domainEvent = DomainEvent.class,
            semantics = SemanticsOf.NON_IDEMPOTENT
    )
    @ActionLayout(
            cssClassFa = "fa-plus",
            named = "Add",
            contributed = Contributed.AS_ACTION
    )
    @MemberOrder(name = "aliases", sequence = "1")
    public Object $$(
            @Parameter(maxLength = AliasModule.JdoColumnLength.AT_PATH)
            @ParameterLayout(named = "Application tenancy")
            final String applicationTenancyPath,
            final AliasType aliasType,
            @Parameter(maxLength = AliasModule.JdoColumnLength.ALIAS_REFERENCE)
            @ParameterLayout(named = "Alias reference")
            final String alias) {
        aliasRepository.create(this.aliased, applicationTenancyPath, aliasType, alias);
        return this.aliased;
    }

    public Collection<String> choices0$$() {
        final List<String> combined = Lists.newArrayList();
        FluentIterable.from(applicationTenancyRepositories)
                .forEach(applicationTenancyRepository -> {
                    final Collection<String> aliasTypes = applicationTenancyRepository
                            .atPathsFor(this.aliased);
                    if(aliasTypes != null && !aliasTypes.isEmpty()) {
                        combined.addAll(aliasTypes);
                    }
                });
        return combined;
    }

    public Collection<AliasType> choices1$$(final String applicationTenancyPath) {
        final List<AliasType> combined = Lists.newArrayList();
        FluentIterable.from(aliasTypeRepositories)
                .forEach(aliasTypeRepository -> {
                    final Collection<AliasType> aliasTypes = aliasTypeRepository
                            .aliasTypesFor(this.aliased, applicationTenancyPath);
                    if(aliasTypes != null && !aliasTypes.isEmpty()) {
                        combined.addAll(aliasTypes);
                    }
                });
        return combined;
    }


    //endregion

}
