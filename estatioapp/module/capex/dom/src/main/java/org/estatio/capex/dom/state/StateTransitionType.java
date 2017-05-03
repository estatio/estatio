package org.estatio.capex.dom.state;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.eventbus.AbstractDomainEvent;
import org.apache.isis.applib.services.eventbus.EventBusService;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.capex.dom.invoice.task.TaskForIncomingInvoiceRepository;
import org.estatio.capex.dom.task.Task;
import org.estatio.dom.roles.EstatioRole;

public interface StateTransitionType<
                        STT extends StateTransitionType<STT, S>,
                        S extends State<S>> {

    @Programmatic
    List<S> getFromStates();

    @Programmatic
    S getToState();

    /**
     * All available instances of this class, used to search for the following transition.
     */
    List<STT> allValues();


    /**
     * Whether the provided domain object can make <i>this</i> transition.
     *
     * <p>
     *     The domain object's current state will, at least, be compatible with <i>this</i> transition's
     *     {@link #getFromStates() from state}(s).  It is <i>not</i> necessary for there to be any
     *     {@link #assignTaskTo() task role} associated with this transition, however.
     * </p>
     */
    @Programmatic
    boolean canApply(final Object domainObject, final ServiceRegistry2 serviceRegistry2);

    /**
     * The {@link EstatioRole task role}, if any, that any {@link Task} wrapping this transition must be routed to.
     *
     * <p>
     *     Said another way: a {@link Task} can only created as a wrapper around this transition if a
     *     {@link EstatioRole task role} has been provided.
     * </p>
     */
    @Programmatic
    EstatioRole assignTaskTo();

    /**
     * Only called if {@link #assignTaskTo()} is non-null, and
     * {@link #canApply(Object, ServiceRegistry2)} also returns <tt>true</tt>.
     *
     * <p>
     *     Typically implementations might want to cache results from
     *     {@link #canApply(Object, ServiceRegistry2)} (lookup {@link QueryResultsCache} from provided
     *     {@link ServiceRegistry2}).
     * </p>
     */
    @Programmatic
    Task createTask(
            final Object domainObject,
            final ServiceRegistry2 serviceRegistry2);

    class Util {

        private Util(){}

        /**
         * Whether the domain object can make the suggested transition.
         *
         * <p>
         *     The
         * </p>
         *
         * @param domainObject
         * @param candidateTransitionType
         * @param serviceRegistry2
         * @return
         */
        public static <
                STT extends StateTransitionType<STT, S>,
                S extends State<S>
        > boolean canApply(
                final Object domainObject,
                final S fromState,
                final STT candidateTransitionType,
                final ServiceRegistry2 serviceRegistry2) {
            return isFromState(candidateTransitionType, fromState) &&
                    candidateTransitionType.canApply(domainObject, serviceRegistry2);
        }

        /**
         * Apply the transition to the domain object and, if supported, create a {@link Task} for the <i>next</i> transition after that
         *
         * <p>
         *     Only called if {@link #canApply(StateOwner, ServiceRegistry2)}
         * </p>
         * @param domainObject
         * @param transitionType - the transition being applied
         * @param serviceRegistry2 - lookup other services if necessary
         * @return
         */
        public static <
                STT extends StateTransitionType<STT, S>,
                S extends State<S>
        > Task apply(
                final Object domainObject,
                final STT transitionType,
                final ServiceRegistry2 serviceRegistry2) {

            final EventBusService eventBusService = serviceRegistry2.lookupService(EventBusService.class);
            final Event<STT, S> event = new Event<>(domainObject, transitionType);

            event.setEventPhase(AbstractDomainEvent.Phase.EXECUTING);
            eventBusService.post(event);

            // transition the domain object to its next state
            final S nextState = transitionType.getToState();
            domainObject.setState(nextState);

            // for wherever we might go next, we spin through all possible transitions,
            // and create a task for the first one that applies to this particular domain object.
            Task task = null;
            final List<STT> allTransitionsTypes = transitionType.allValues();
            for (STT candidateNextTransition : allTransitionsTypes) {
                if (candidateNextTransition.assignTaskTo() == null) {
                    continue;
                }
                if (!canApply(domainObject, candidateNextTransition, serviceRegistry2)) {
                    continue;
                }
                task = candidateNextTransition.createTask(domainObject, serviceRegistry2);
                if (task != null) {
                    break;
                }
            }

            event.setEventPhase(AbstractDomainEvent.Phase.EXECUTED);
            eventBusService.post(event);
            return task;
        }

        static <DO extends StateOwner<DO, TS>,
                TT extends StateTransitionType<DO, TT, TS>,
                TS extends State<DO, TS>
        > boolean isFromState(
                final TT taskTransition,
                final TS fromState) {
            final List<TS> fromStates = taskTransition.getFromStates();
            return fromStates == null || fromStates.contains(fromState);
        }

    }

    class Event<
            STT extends StateTransitionType<STT, S>,
            S extends State<S>
            > extends AbstractDomainEvent<StateTransitionType<STT, S>> {

        private final Object domainObject;
        private final STT transitionType;

        public Event(
                final Object domainObject,
                final STT transitionType) {
            this.transitionType = transitionType;
            this.domainObject = domainObject;
        }

        public STT getTransitionType() {
            return transitionType;
        }

        public Object getDomainObject() {
            return domainObject;
        }
    }

    abstract class TaskCompletionSubscriberAbstract<
            STT extends StateTransitionType<STT, S>,
            S extends State<S>
        > extends AbstractSubscriber {

        @com.google.common.eventbus.Subscribe
        public void on(StateTransitionType.Event<STT, S> event) {
            if(event.getEventPhase() == AbstractDomainEvent.Phase.EXECUTING) {
                final STT transitionType = event.getTransitionType();
                final Object domainObject = event.getDomainObject();
                final List<Task> tasks =
                        findTasksByDomainObjectAndTransition(domainObject, transitionType);
                for (final Task task : tasks) {
                    task.completed();
                }
            }
        }

        /**
         * Mandatory hook
         */
        protected abstract List<Task> findTasksByDomainObjectAndTransition(final Object domainObject, final STT transitionType);

        @Inject
        TaskForIncomingInvoiceRepository repository;
    }

}
