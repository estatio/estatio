package org.estatio.module.party;

import java.util.Set;

import com.google.common.collect.Sets;

import org.estatio.module.base.platform.applib.Module;
import org.estatio.module.numerator.EstatioNumeratorModule;

public final class EstatioPartyModule implements Module {

    public EstatioPartyModule(){}

    @Override
    public Set<Module> getDependencies() {
        return Sets.newHashSet(new EstatioNumeratorModule());
    }

    public abstract static class ActionDomainEvent<S>
            extends org.apache.isis.applib.services.eventbus.ActionDomainEvent<S> { }

    public abstract static class CollectionDomainEvent<S,T>
            extends org.apache.isis.applib.services.eventbus.CollectionDomainEvent<S,T> { }

    public abstract static class PropertyDomainEvent<S,T>
            extends org.apache.isis.applib.services.eventbus.PropertyDomainEvent<S,T> { }

}
