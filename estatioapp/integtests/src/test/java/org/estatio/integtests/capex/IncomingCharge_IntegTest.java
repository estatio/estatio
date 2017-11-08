package org.estatio.integtests.capex;

import java.util.List;
import java.util.SortedSet;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureResult;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.fixturescripts.FixtureScripts;

import org.estatio.module.capex.fixtures.charge.IncomingChargeFixture;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;
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
            final List<Charge> charges = chargeRepository.listAll();
            assertThat(charges).isNotEmpty();

            // and when
            final Charge france = chargeRepository.findByReference("FRANCE");

            // then
            assertThat(france).isNotNull();
            final SortedSet<Charge> franceChildren = france.getChildren();
            assertThat(franceChildren).isNotEmpty();
            assertThat(france.getParent()).isNull();

            // and when
            final Charge marketing = chargeRepository.findByReference("MARKETING");

            // then
            assertThat(marketing).isNotNull();
            final SortedSet<Charge> marketingChildren = marketing.getChildren();
            assertThat(marketingChildren).isEmpty();
            assertThat(marketing.getParent()).isEqualTo(france);

        }
    }

    @Inject
    ChargeRepository chargeRepository;

}
