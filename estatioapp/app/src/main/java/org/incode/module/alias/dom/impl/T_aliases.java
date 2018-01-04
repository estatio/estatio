package org.incode.module.alias.dom.impl;

import org.apache.isis.applib.annotation.*;
import org.incode.module.alias.AliasModule;

import javax.inject.Inject;
import java.util.List;

public abstract class T_aliases<T> {

    //region  > (injected)
    @Inject
    AliasRepository aliasRepository;
    //endregion

    //region > constructor
    private final T aliased;

    public T_aliases(final T aliased) {
        this.aliased = aliased;
    }

    public T getAliased() {
        return aliased;
    }
    //endregion

    //region > $$

    public static class DomainEvent extends AliasModule.ActionDomainEvent<T_aliases> { } { }
    @Action(
            domainEvent = DomainEvent.class,
            semantics = SemanticsOf.SAFE
    )
    @ActionLayout(
            contributed = Contributed.AS_ASSOCIATION
    )
    @CollectionLayout(
            named = "Aliases",
            defaultView = "table"
    )
    public List<Alias> $$() {
        return aliasRepository.findByAliased(this.aliased);
    }

    //endregion


}
