package org.estatio.capex.integtests.time;

import java.util.List;
import java.util.SortedSet;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureResult;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.fixturescripts.FixtureScripts;

import org.estatio.capex.dom.time.TimeInterval;
import org.estatio.capex.dom.time.TimeIntervalRepository;
import org.estatio.capex.fixture.time.TimeIntervalFixture;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class TimeInterval_IntegTest extends EstatioIntegrationTest {


    @Inject
    FixtureScripts fixtureScripts;


    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());
            }
        });
    }

    @Before
    public void setUp() {
    }

    public static class LoadFixtures extends TimeInterval_IntegTest {

        List<FixtureResult> fixtureResults;

        @Test
        public void happyCase() throws Exception {

            // when
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(final ExecutionContext executionContext) {
                    executionContext.executeChild(this, new TimeIntervalFixture());
                    fixtureResults = executionContext.getResults();
                }
            });

            // then
            final List<TimeInterval> timeIntervals = timeIntervalRepository.listAll();

            assertThat(timeIntervals).isNotEmpty();

            // and when
            final TimeInterval f2016 = timeIntervalRepository.findByName("F2016");

            // then
            assertThat(f2016).isNotNull();
            final SortedSet<TimeInterval> f2016Children = f2016.getChildren();
            assertThat(f2016Children).isEmpty();
            assertThat(f2016.getParent()).isNull();

            // and when
            final TimeInterval n2016 = timeIntervalRepository.findByName("2016");

            // then
            assertThat(n2016).isNotNull();
            final SortedSet<TimeInterval> n2016Children = n2016.getChildren();
            assertThat(n2016Children).hasSize(4);
            assertThat(n2016.getParent()).isNull();


            // and when
            final TimeInterval n2016q3 = timeIntervalRepository.findByName("2016Q3");

            // then
            assertThat(n2016q3).isNotNull();
            final SortedSet<TimeInterval> n2016q3Children = n2016q3.getChildren();
            assertThat(n2016q3Children).isEmpty();
            assertThat(n2016q3.getParent()).isEqualTo(n2016);

        }
    }

    @Inject
    TimeIntervalRepository timeIntervalRepository;

}
