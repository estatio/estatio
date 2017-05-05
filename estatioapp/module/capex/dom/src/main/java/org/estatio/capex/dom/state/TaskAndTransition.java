package org.estatio.capex.dom.state;

import java.util.List;

import com.google.common.collect.Lists;

import org.estatio.capex.dom.task.Task;

import lombok.Getter;

/**
 * Tuple class, provided to return from finders.
 */
@Getter
public class TaskAndTransition<
        DO,
        ST extends StateTransition<DO, ST, STT, S>,
        STT extends StateTransitionChart<DO, ST, STT, S>,
        S extends State<S>
        > {
    private final Task task;
    private final ST transition;

    public TaskAndTransition(final Task task, final ST transition) {
        this.task = task;
        this.transition = transition;
    }

    public static <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionChart<DO, ST, STT, S>,
            S extends State<S>
            > List<TaskAndTransition<DO,ST,STT,S>> from(List<Object[]> resultList) {
        List<TaskAndTransition<DO,ST,STT,S>> results = Lists.newArrayList();
        for (Object[] result : resultList) {
            results.add(from(result));
        }
        return results;
    }

    public static <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionChart<DO, ST, STT, S>,
            S extends State<S>
    > TaskAndTransition<DO,ST,STT,S> from(Object[] result) {
        return new TaskAndTransition<DO, ST, STT, S>((Task)result[0], (ST)result[1]);
    }
}
