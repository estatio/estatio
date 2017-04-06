package org.estatio.capex.integtests.charge;

import java.util.List;
import java.util.SortedSet;

import javax.inject.Inject;

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

import static org.assertj.core.api.Assertions.assertThat;

public class IncomingCharge_IntegTest extends EstatioIntegrationTest {


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

    public static class LoadFixtures extends IncomingCharge_IntegTest {

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
            assertThat(incomingCharges).isNotEmpty();

            // and when
            final IncomingCharge france = incomingChargeRepository.findByName("FRANCE");

            // then
            assertThat(france).isNotNull();
            final SortedSet<IncomingCharge> franceChildren = france.getChildren();
            assertThat(franceChildren).isNotEmpty();
            assertThat(france.getParent()).isNull();

            // and when
            final IncomingCharge marketing = incomingChargeRepository.findByName("MARKETING");

            // then
            assertThat(marketing).isNotNull();
            final SortedSet<IncomingCharge> marketingChildren = marketing.getChildren();
            assertThat(marketingChildren).isEmpty();
            assertThat(marketing.getParent()).isEqualTo(france);

        }
    }

    @Inject
    IncomingChargeRepository incomingChargeRepository;

}
