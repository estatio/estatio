package org.incode.module.country.dom.impl;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.api.Action;
import org.jmock.api.Invocation;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.query.Query;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.incode.module.unittestsupport.dom.repo.FinderInteraction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jmock.Expectations.anything;

public class StateRepositoryTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    RepositoryService mockRepositoryService;

    FinderInteraction[] finderInteraction = new FinderInteraction[] {null};

    StateRepository stateRepository;

    Country country;

    @Before
    public void setup() {

        country = new Country();

        stateRepository = new StateRepository();
        stateRepository.repositoryService = mockRepositoryService;

        Matcher<Class> anyClass = anything();
        Matcher<long[]> anyLongs = anything();
        context.checking(new Expectations() {{
            allowing(mockRepositoryService).firstMatch(with(any(Query.class)));
            will(new Capture(finderInteraction, FinderInteraction.FinderMethod.FIRST_MATCH));
            allowing(mockRepositoryService).allInstances(with(anyClass), with(anyLongs));
            will(new Capture(finderInteraction, FinderInteraction.FinderMethod.ALL_INSTANCES));
            allowing(mockRepositoryService).allMatches(with(any(Query.class)));
            will(new Capture(finderInteraction, FinderInteraction.FinderMethod.ALL_MATCHES));
        }});
    }

    public static class FindStateByReference extends StateRepositoryTest {

        @Test
        public void happyCase() {

            stateRepository.findState("*REF?1*");

            assertThat(finderInteraction[0].getFinderMethod()).isEqualTo(FinderInteraction.FinderMethod.FIRST_MATCH);
            assertThat(finderInteraction[0].getResultType()).isEqualTo(State.class);
            assertThat(finderInteraction[0].getQueryName()).isEqualTo("findByReference");
            assertThat(finderInteraction[0].getArgumentsByParameterName().get("reference")).isEqualTo((Object) "*REF?1*");
            assertThat(finderInteraction[0].getArgumentsByParameterName()).hasSize(1);
        }
    }

    public static class FindStatesByCountry extends StateRepositoryTest {

        @Test
        public void happyCase() {

            stateRepository.findStatesByCountry(country);

            assertThat(finderInteraction[0].getFinderMethod()).isEqualTo(FinderInteraction.FinderMethod.ALL_MATCHES);
            assertThat(finderInteraction[0].getResultType()).isEqualTo(State.class);
            assertThat(finderInteraction[0].getQueryName()).isEqualTo("findByCountry");
            assertThat(finderInteraction[0].getArgumentsByParameterName().get("country")).isEqualTo((Object) country);
            assertThat(finderInteraction[0].getArgumentsByParameterName()).hasSize(1);
        }

    }

    public static class AllStates extends StateRepositoryTest {

        @Test
        public void happyCase() {

            stateRepository.allStates();

            assertThat(finderInteraction[0].getFinderMethod()).isEqualTo(FinderInteraction.FinderMethod.ALL_INSTANCES);
        }
    }

    public static class Capture implements Action {
        private final FinderInteraction[] finderInteraction;
        private final FinderInteraction.FinderMethod finderMethod;
        public Capture(final FinderInteraction[] finderInteraction, final FinderInteraction.FinderMethod finderMethod) {
            assert finderInteraction.length == 1;
            this.finderInteraction = finderInteraction;
            this.finderMethod = finderMethod;
        }

        @Override
        public void describeTo(final Description description) {
            final QueryDefault<?> queryIfAny = finderInteraction[0].getQueryDefault();
            description.appendText("Captures " + finderMethod);
            if(queryIfAny != null) {
                description.appendText(" of " + queryIfAny.getDescription());
            }
        }

        @Override
        public Object invoke(final Invocation invocation) throws Throwable {
            final Object arg0 = invocation.getParameter(0);
            finderInteraction[0] = new FinderInteraction(  arg0 instanceof Query ? (Query<?>) arg0 : null, finderMethod);
            return null;
        }
    }
}
