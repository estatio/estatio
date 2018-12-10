package org.estatio.module.capex.dom.task;

import java.util.List;
import java.util.concurrent.Callable;

import org.assertj.core.util.Lists;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.module.party.dom.Person;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskRepository_findIncompleteByPersonAssignedTo_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    QueryResultsCache mockQueryResultsCache;

    List<Task> tasks;
    Person person;

    private TaskRepository taskRepository;

    @Before
    public void setUp() throws Exception {
        tasks = Lists.newArrayList(newTask(), newTask());

        person = new Person();

        taskRepository = new TaskRepository();
        taskRepository.queryResultsCache = mockQueryResultsCache;

        context.checking(new Expectations() {{
            allowing(mockQueryResultsCache).execute(
                    with(any(Callable.class)),
                    with(any(Class.class)),
                    with(equal("findIncompleteByPersonAssignedTo")),
                    with(equal(new Object[]{person})));
            will(returnValue(tasks));
        }});
    }

    @Test
    public void findIncompleteByPersonAssignedTo() {

        // when
        final List<Task> tasks = taskRepository.findIncompleteByPersonAssignedTo(person);

        // then
        assertThat(tasks).hasSameElementsAs(this.tasks);
        assertThat(tasks).isNotSameAs(this.tasks);

        // and so...
        assertThat(this.tasks).hasSize(2);
        tasks.add(newTask());
        assertThat(this.tasks).hasSize(2);

    }

    private static Task newTask() {
        return new Task(null, null, null, null, null);
    }

}
