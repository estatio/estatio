package org.estatio.capex.dom.dobj;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.capex.dom.state.State;
import org.estatio.capex.dom.state.StateTransitionAbstract;
import org.estatio.capex.dom.state.StateTransitionRepositoryGeneric;
import org.estatio.capex.dom.state.StateTransitionType;

/**
 * Subclasses should be annotated using: @Mixin(method = "coll")
 */
public abstract class DomainObject_transitionsAbstract<
        DO,
        ST extends StateTransitionAbstract<DO, ST, STT, S>,
        STT extends StateTransitionType<DO, ST, STT, S>,
        S extends State<S>
        > {

    protected final DO domainObject;
    private final Class<ST> stateTransitionClass;

    public DomainObject_transitionsAbstract(final DO domainObject, final Class<ST> stateTransitionClass) {
        this.domainObject = domainObject;
        this.stateTransitionClass = stateTransitionClass;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<ST> coll() {
        return stateTransitionRepositoryGeneric.findByDomainObject(domainObject, stateTransitionClass);
    }

    @Inject
    protected StateTransitionRepositoryGeneric stateTransitionRepositoryGeneric;


}
