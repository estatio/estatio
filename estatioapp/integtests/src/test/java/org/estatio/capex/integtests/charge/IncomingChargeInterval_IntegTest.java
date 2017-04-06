package org.estatio.capex.integtests.charge;

import java.util.List;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureResult;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.fixturescripts.FixtureScripts;

import org.estatio.capex.dom.charge.IncomingCharge;
import org.estatio.capex.dom.charge.IncomingChargeRepository;
import org.estatio.capex.fixture.charge.IncomingChargeFixture;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.integtests.EstatioIntegrationTest;

public class IncomingChargeInterval_IntegTest extends EstatioIntegrationTest {


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

    public static class LoadFixtures extends IncomingChargeInterval_IntegTest {

        List<FixtureResult> fixtureResults;

        @Test
        public void happyCase() throws Exception {

            // when
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(final ExecutionContext executionContext) {
                    executionContext.executeChild(this, new IncomingChargeFixture());
                    fixtureResults = executionContext.getResults();
                }
            });

            // then
            final List<IncomingCharge> incomingCharges = incomingChargeRepository.listAll();

            Assertions.assertThat(incomingCharges).hasSize(30);
        }
    }

    @Inject
    IncomingChargeRepository incomingChargeRepository;

}
