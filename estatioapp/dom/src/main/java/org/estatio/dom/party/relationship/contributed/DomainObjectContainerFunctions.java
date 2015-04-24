package org.estatio.dom.party.relationship.contributed;

import com.google.common.base.Function;
import org.apache.isis.applib.DomainObjectContainer;

public final class DomainObjectContainerFunctions {

    private DomainObjectContainerFunctions(){}

    public static <T> Function<T, String> titleOfUsing(final DomainObjectContainer container) {
        return new Function<T, String>() {
            @Override
            public String apply(final T input) {
                return container.titleOf(input);
            }
        };
    }

}
