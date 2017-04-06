package org.estatio.capex.integtests.time;

import java.util.List;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
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
            // given

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

            Assertions.assertThat(timeIntervals).isNotEmpty();
        }
    }

    @Inject
    TimeIntervalRepository timeIntervalRepository;

}
