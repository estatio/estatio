package org.estatio.capex.dom.task.policy;

import java.util.Optional;

import javax.inject.Inject;

import com.google.common.eventbus.Subscribe;

import org.axonframework.eventhandling.annotation.EventHandler;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.metamodel.MetaModelService3;

import org.estatio.capex.dom.EstatioCapexDomModule;
import org.estatio.capex.dom.state.State;
import org.estatio.capex.dom.state.StateTransition;
import org.estatio.capex.dom.state.StateTransitionService;
import org.estatio.capex.dom.state.StateTransitionType;
import org.estatio.capex.dom.task.Task;
import org.estatio.capex.dom.task.Task_mixinActAbstract;
import org.estatio.capex.dom.triggers.DomainObject_triggerAbstract;
import org.estatio.dom.party.Person;
import org.estatio.dom.party.PersonRepository;
import org.estatio.dom.togglz.EstatioTogglzFeature;

@DomainService(nature = NatureOfService.DOMAIN)
@DomainServiceLayout(menuOrder = "1")
public class EnforceTaskAssignmentPolicySubscriber extends org.apache.isis.applib.AbstractSubscriber {

    public static interface WithStateTransitionClass {
        Class<?> getStateTransitionClass();
    }

    @EventHandler
    @Subscribe
    public void on(Task_mixinActAbstract.ActionDomainEvent ev) {
        if(ev.getSemantics().isSafeInNature()) {
            return;
        }

        final Class stateTransitionClass = ev.getStateTransitionClass();
        final Task task = (Task) ev.getMixedIn();
        final StateTransition transition = stateTransitionService.findFor(task);
        if(transition == null) {
            // shouldn't occur
            return;
        }
        final Class taskTransitionClass = stateTransitionService.transitionClassFor(transition.getTransitionType());
        if(stateTransitionClass != taskTransitionClass) {
            // just ignore; this mixin action on task doesn't apply to the domain object that the task applies to.

            // or, maybe should hide (and then we can delete code in the subtypes of the mixinAbstract
            return;
        }

        final Object domainObject = transition.getDomainObject();
        applyPolicy(stateTransitionClass, domainObject, ev);
    }


    @EventHandler
    @Subscribe
    public void on(DomainObject_triggerAbstract.ActionDomainEvent ev) {
        if(ev.getSemantics().isSafeInNature()) {
            return;
        }

        final Class stateTransitionClass = ev.getStateTransitionClass();
        final Object domainObject = ev.getMixedIn();
        applyPolicy(stateTransitionClass, domainObject, ev);

    }

    private void applyPolicy(
            final Class stateTransitionClass,
            final Object domainObject,
            final EstatioCapexDomModule.ActionDomainEvent<?> evv) {
        Optional<String> reasonIfAny = applyPolicy(stateTransitionClass, domainObject);
        reasonIfAny.ifPresent(evv::disable);
    }

    private <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
            > Optional<String> applyPolicy(
            final Class<ST> stateTransitionClass,
            final DO entityOrViewModel) {

        if(EstatioTogglzFeature.ApproveByProxy.isActive()) {
            return Optional.empty();
        }

        final DO entity = unwrapIfRequired(entityOrViewModel);

        final ST pendingTransition = stateTransitionService.pendingTransitionOf(entity, stateTransitionClass);

        if (pendingTransition == null){
            return Optional.empty();
        }

        final Task task = pendingTransition.getTask();
        if(task == null) {
            return Optional.empty();
        }

        final Person meAsPerson = personRepository.me();

        final Person taskAssignedTo = task.getPersonAssignedTo();
        if(taskAssignedTo == null || taskAssignedTo == meAsPerson) {
            return Optional.empty();
        }

        return Optional.of(String.format("Task assigned to %s", taskAssignedTo.getReference()));
    }

    private <DO> DO unwrapIfRequired(final DO entityOrViewModel) {
        final DO entity;
        if(entityOrViewModel instanceof ViewModelWrapper) {
            final ViewModelWrapper entityOrViewModel1 = (ViewModelWrapper) entityOrViewModel;
            entity = (DO)entityOrViewModel1.getDomainObject();
        } else {
            entity = entityOrViewModel;
        }
        return entity;
    }

    @Inject
    MetaModelService3 metaModelService3;

    @Inject
    StateTransitionService stateTransitionService;

    @Inject
    PersonRepository personRepository;

}
