package org.estatio.module.capex.integtests.charge;

import java.util.List;
import java.util.SortedSet;

import javax.inject.Inject;

import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureResult;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.fixturescripts.FixtureScripts;

import org.estatio.module.charge.fixtures.incoming.builders.CapexChargeHierarchyXlsxFixture;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;

import static org.assertj.core.api.Assertions.assertThat;

public class IncomingCharge_IntegTest extends CapexModuleIntegTestAbstract {


    @Inject
    FixtureScripts fixtureScripts;


    public static class LoadFixtures extends IncomingCharge_IntegTest {

        List<FixtureResult> fixtureResults;

        @Test
        public void happyCase() throws Exception {

            // when
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(final ExecutionContext executionContext) {
                    executionContext.executeChild(this, new CapexChargeHierarchyXlsxFixture());
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
