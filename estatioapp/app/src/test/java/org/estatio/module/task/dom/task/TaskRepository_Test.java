package org.estatio.module.task.dom.task;

import java.util.List;
import java.util.concurrent.Callable;

import org.apache.isis.applib.services.repository.RepositoryService;
import org.assertj.core.util.Lists;
import org.estatio.module.party.dom.PersonRepository;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.module.party.dom.Person;


import static org.assertj.core.api.Assertions.assertThat;


public class TaskRepository_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    QueryResultsCache mockQueryResultsCache;

    @Mock
    RepositoryService mockRepositoryService;

    @Mock
    PersonRepository mockPersonRepository;

    List<Task> tasks;
    List<Task> tasksSorted;
    Person person;

    private TaskRepository taskRepository;

    @Before
    public void setUp() throws Exception {
        LocalDateTime dateTime1 = new LocalDateTime(2018, 01, 01, 00, 00);
        LocalDateTime dateTime2 = new LocalDateTime(2017, 01, 01, 00, 00);

        Task task1 = newTask(dateTime1, 1);
        Task task2 = newTask(dateTime2, null);
        Task task3 = newTask(dateTime1, null);

        tasks = Lists.newArrayList(task3, task1, task2);
        tasksSorted = Lists.newArrayList(task1, task2, task3);

        person = new Person();

        taskRepository = new TaskRepository();
        taskRepository.queryResultsCache = mockQueryResultsCache;
        taskRepository.repositoryService = mockRepositoryService;
        taskRepository.personRepository = mockPersonRepository;

    }

    @Test
    public void findIncompleteByPersonAssignedTo() {
        context.checking(new Expectations() {{
            allowing(mockQueryResultsCache).execute(
                    with(any(Callable.class)),
                    with(any(Class.class)),
                    with(equal("findIncompleteByPersonAssignedTo")),
                    with(equal(new Object[]{person})));
            will(returnValue(tasks));
        }});

        // when
        final List<Task> tasks = taskRepository.findIncompleteByPersonAssignedTo(person);

        // then
        assertThat(tasks).hasSameElementsAs(this.tasks);
        assertThat(tasks).isNotSameAs(this.tasks);

        // and so...
        assertThat(this.tasks).hasSize(3);
        tasks.add(newTask(null, null));
        assertThat(this.tasks).hasSize(3);

    }

    @Test
    public void findIncompleteForMe_priorityOrder() {

        // expect
        context.checking(new Expectations() {{
            allowing(mockQueryResultsCache).execute(
                    with(any(Callable.class)),
                    with(any(Class.class)),
                    with(equal("meAsPerson")),
                    with(any(Object.class)));
            will(returnValue(person));

            allowing(mockQueryResultsCache).execute(
                    with(any(Callable.class)),
                    with(any(Class.class)),
                    with(equal("findIncompleteByUnassignedForRoles")),
                    with(any(Object.class)));
            will(returnValue(Lists.newArrayList()));

            allowing(mockQueryResultsCache).execute(
                    with(any(Callable.class)),
                    with(any(Class.class)),
                    with(any(String.class)),
                    with(any(Object.class)));
            will(returnValue(tasks));
        }});

        // when
        final List<Task> tasks = taskRepository.findIncompleteForMe();

        // then
        assertThat(tasks).isEqualTo(this.tasksSorted);

    }

    private static Task newTask(LocalDateTime createdOn, Integer priority) {
        return new Task(null, null, null, createdOn, null, priority);
    }

}
