package org.estatio.capex.dom.state;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.SemanticsOf;

/**
 * Subclasses should be annotated using: @Mixin(method = "act")
 */
public abstract class DomainObject_viewTransitionsAbstract<
        DO,
        ST extends StateTransitionAbstract<DO, ST, STT, S>,
        STT extends StateTransitionType<DO, ST, STT, S>,
        S extends State<S>
        > {

    protected final DO domainObject;
    private final Class<ST> stateTransitionClass;

    public DomainObject_viewTransitionsAbstract(final DO domainObject, final Class<ST> stateTransitionClass) {
        this.domainObject = domainObject;
        this.stateTransitionClass = stateTransitionClass;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public List<ST> act() {
        return stateTransitionRepositoryGeneric.findByDomainObject(domainObject, stateTransitionClass);
    }

    @Inject
    protected StateTransitionRepositoryGeneric stateTransitionRepositoryGeneric;


}
