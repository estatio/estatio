package org.estatio.capex.dom.task;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.eventbus.AbstractDomainEvent;
import org.apache.isis.applib.services.eventbus.EventBusService;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.capex.dom.invoice.task.TaskForIncomingInvoiceRepository;
import org.estatio.dom.roles.EstatioRole;

public interface TaskTransition<
                        DO extends TaskStateOwner<DO, TS>,
                        TT extends TaskTransition<DO, TT, TS>,
                        TS extends TaskState<DO, TS>> {

    @Programmatic
    List<TS> getFromStates();

    @Programmatic
    TS getToState();

    /**
     * All available instances of this class, used to search for the following transition.
     */
    List<TT> allValues();


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
    boolean canApply(final DO domainObject, final ServiceRegistry2 serviceRegistry2);

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
     * {@link #canApply(TaskStateOwner, ServiceRegistry2)} also returns <tt>true</tt>.
     *
     * <p>
     *     Typically implementations might want to cache results from
     *     {@link #canApply(TaskStateOwner, ServiceRegistry2)} (lookup {@link QueryResultsCache} from provided
     *     {@link ServiceRegistry2}).
     * </p>
     */
    @Programmatic
    Task<?, DO, TT, TS> createTask(
            final DO domainObject,
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
         * @param candidateTransition
         * @param serviceRegistry2
         * @return
         */
        public static <
                DO extends TaskStateOwner<DO, TS>,
                TT extends TaskTransition<DO, TT, TS>,
                TS extends TaskState<DO, TS>
        > boolean canApply(
                final DO domainObject,
                final TT candidateTransition,
                final ServiceRegistry2 serviceRegistry2) {
            return isFromState(candidateTransition, domainObject.getTaskState()) &&
                    candidateTransition.canApply(domainObject, serviceRegistry2);
        }

        /**
         * Apply the transition to the domain object and, if supported, create a {@link Task} for the <i>next</i> transition after that
         *
         * <p>
         *     Only called if {@link #canApply(TaskStateOwner, ServiceRegistry2)}
         * </p>
         * @param domainObject
         * @param taskTransition - the transition being applied
         * @param serviceRegistry2 - lookup other services if necessary
         * @return
         */
        public static <
                DO extends TaskStateOwner<DO, TS>,
                TT extends TaskTransition<DO, TT, TS>,
                TS extends TaskState<DO, TS>
        > Task<?, DO, TT, TS> apply(
                final DO domainObject,
                final TT taskTransition,
                final ServiceRegistry2 serviceRegistry2) {

            final EventBusService eventBusService = serviceRegistry2.lookupService(EventBusService.class);
            final Event<DO, TT, TS> event = new Event<>(domainObject, taskTransition);

            event.setEventPhase(AbstractDomainEvent.Phase.EXECUTING);
            eventBusService.post(event);

            // transition the domain object to its next state
            final TS nextState = taskTransition.getToState();
            domainObject.setTaskState(nextState);

            // for wherever we might go next, we spin through all possible transitions,
            // and create a task for the first one that applies to this particular domain object.
            Task<?, DO, TT, TS> task = null;
            final List<TT> allTransitions = taskTransition.allValues();
            for (TT candidateNextTransition : allTransitions) {
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

        static <DO extends TaskStateOwner<DO, TS>,
                TT extends TaskTransition<DO, TT, TS>,
                TS extends TaskState<DO, TS>
        > boolean isFromState(
                final TT taskTransition,
                final TS fromState) {
            final List<TS> fromStates = taskTransition.getFromStates();
            return fromStates == null || fromStates.contains(fromState);
        }

    }

    class Event<
            DO extends TaskStateOwner<DO, TS>,
            TT extends TaskTransition<DO, TT, TS>,
            TS extends TaskState<DO, TS>
            > extends AbstractDomainEvent<TaskTransition<DO, TT, ?>> {

        private final DO domainObject;
        private final TT taskTransition;

        public Event(
                final DO domainObject,
                final TT taskTransition) {
            this.taskTransition = taskTransition;
            this.domainObject = domainObject;
        }

        public TT getTaskTransition() {
            return taskTransition;
        }

        public DO getDomainObject() {
            return domainObject;
        }
    }

    abstract class TaskCompletionSubscriberAbstract<
            DO extends TaskStateOwner<DO, TS>,
            TT extends TaskTransition<DO, TT, TS>,
            TS extends TaskState<DO, TS>
        > extends AbstractSubscriber {

        @com.google.common.eventbus.Subscribe
        public void on(TaskTransition.Event<DO, TT, TS> event) {
            if(event.getEventPhase() == AbstractDomainEvent.Phase.EXECUTING) {
                final TT taskTransition = event.getTaskTransition();
                final DO domainObject = event.getDomainObject();
                final List<Task<?, DO, TT, TS>> tasks =
                        findTasksByDomainObjectAndTransition(domainObject, taskTransition);
                for (final Task task : tasks) {
                    task.completed();
                }
            }
        }

        /**
         * Mandatory hook
         */
        protected abstract List<Task<?, DO, TT, TS>> findTasksByDomainObjectAndTransition(final DO domainObject, final TT taskTransition);

        @Inject
        TaskForIncomingInvoiceRepository repository;
    }

}
