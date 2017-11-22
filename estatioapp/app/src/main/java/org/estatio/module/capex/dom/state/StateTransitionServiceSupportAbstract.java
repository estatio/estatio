package org.estatio.module.capex.dom.state;

import java.util.Objects;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.metamodel.MetaModelService3;

import org.estatio.module.capex.dom.task.Task;

@DomainService(nature = NatureOfService.DOMAIN)
public abstract class StateTransitionServiceSupportAbstract<
        DO,
        ST extends StateTransition<DO, ST, STT, S>,
        STT extends StateTransitionType<DO, ST, STT, S>,
        S extends State<S>
        > implements StateTransitionServiceSupport<DO, ST, STT, S> {

    private final Class<STT> stateTransitionTypeClass;
    private final Class<ST> stateTransitionClass;

    public StateTransitionServiceSupportAbstract(
            final Class<STT> stateTransitionTypeClass,
            final Class<ST> stateTransitionClass) {

        this.stateTransitionTypeClass = stateTransitionTypeClass;
        this.stateTransitionClass = stateTransitionClass;
    }

    @Override
    public boolean supports(final StateTransitionType<?, ?, ?, ?> transitionType) {
        return stateTransitionTypeClass.isAssignableFrom(transitionType.getClass());
    }

    @Override
    public Class<ST> transitionClassFor(final StateTransitionType<?, ?, ?, ?> prototypeTransitionType) {
        if(supports(prototypeTransitionType)) {
            return stateTransitionClass;
        }
        return null;
    }

    @Override
    public boolean supports(final String transitionType) {
        String objectType = metaModelService3.toObjectType(stateTransitionClass);
        return Objects.equals(objectType, transitionType);
    }

    @Override
    public ST pendingTransitionOf(final DO domainObject) {
        return getRepository().findByDomainObjectAndCompleted(domainObject, false);
    }

    @Override
    public ST mostRecentlyCompletedTransitionOf(final DO domainObject) {
        return getRepository().findByDomainObjectAndCompleted(domainObject, true);
    }

    @Override
    public S currentStateOf(final DO domainObject) {
        final ST mostRecentlyCompletedTransitionIfAny = mostRecentlyCompletedTransitionOf(domainObject);
        return mostRecentlyCompletedTransitionIfAny != null
                ? mostRecentlyCompletedTransitionIfAny.getToState()
                : null;
    }

    @Override
    public STT[] allTransitionTypes() {
        return stateTransitionTypeClass.getEnumConstants();
    }

    @Override
    public ST findFor(final Task task) {
        return getRepository().findByTask(task);
    }

    protected abstract StateTransitionRepository<DO, ST, STT, S> getRepository();

    @Inject
    MetaModelService3 metaModelService3;
}
