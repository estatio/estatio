package org.estatio.module.capex.dom.state;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.metamodel.MetaModelService3;

import org.estatio.module.capex.dom.task.Task;

@DomainService(nature = NatureOfService.DOMAIN)
public class StateTransitionServiceSupportGeneric  {

    @Programmatic
    public <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
            >
    boolean supports(
            final StateTransitionType<?, ?, ?, ?> transitionType,
            final Class<STT> stateTransitionTypeClass) {
        return stateTransitionTypeClass.isAssignableFrom(transitionType.getClass());
    }

    @Programmatic
    public <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
            >
    boolean supports(
            final String transitionType,
            final Class<ST> stateTransitionClass) {
        String objectType = metaModelService3.toObjectType(stateTransitionClass);
        return Objects.equals(objectType, transitionType);
    }

    @Programmatic
    public <
            DO,
            ST extends StateTransitionAbstract<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
            >
    ST pendingTransitionOf(
            final DO domainObject,
            final Class<ST> stateTransitionClass) {
        return stateTransitionRepositoryGeneric.findByDomainObjectAndCompleted(domainObject, false, stateTransitionClass);
    }

    @Programmatic
    public <
            DO,
            ST extends StateTransitionAbstract<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
            >
    ST mostRecentlyCompletedTransitionOf(final DO domainObject, final Class<ST> stateTransitionClass) {
        return stateTransitionRepositoryGeneric.findByDomainObjectAndCompleted(domainObject, true, stateTransitionClass);
    }

    @Programmatic
    public <
            DO,
            ST extends StateTransitionAbstract<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
            >
    S currentStateOf(
            final DO domainObject,
            final Class<ST> stateTransitionClass) {
        final ST mostRecentlyCompletedTransitionIfAny = mostRecentlyCompletedTransitionOf(domainObject, stateTransitionClass);
        return mostRecentlyCompletedTransitionIfAny != null
                ? mostRecentlyCompletedTransitionIfAny.getToState()
                : null;
    }

    @Programmatic
    public <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
            >
    STT[] allTransitionTypes(final Class<STT> stateTransitionTypeClass) {
        return stateTransitionTypeClass.getEnumConstants();
    }

    @Programmatic
    public <
            DO,
            ST extends StateTransitionAbstract<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
            >
    ST findFor(final Task task, final Class<ST> stateTransitionClass) {
        return stateTransitionRepositoryGeneric.findByTask(task, stateTransitionClass);
    }

    @Programmatic
    public <
            DO,
            ST extends StateTransitionAbstract<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>>
    List<STT> transitionTypesFrom(
            final S currState,
            final Class<STT> stateTransitionTypeClass) {
        final STT[] stateTransitionTypes = allTransitionTypes(stateTransitionTypeClass);

        final List<STT> result = Lists.newArrayList();
        for (STT stateTransitionType : stateTransitionTypes) {
            final List<S> fromStates = stateTransitionType.getFromStates();
            if(fromStates != null && fromStates.contains(currState)) {
                result.add(stateTransitionType);
            }
        }
        return result;
    }

    @Inject
    protected StateTransitionRepositoryGeneric stateTransitionRepositoryGeneric;

    @Inject
    protected MetaModelService3 metaModelService3;
}
