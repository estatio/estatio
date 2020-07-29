package org.estatio.module.capex.integtests.charge;

import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.capex.fixtures.charge.builders.IncomingChargesItaXlsxFixture;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;

import static org.assertj.core.api.Assertions.assertThat;

public class ChoicesItalianWorkTypes extends CapexModuleIntegTestAbstract {

    @Inject
    private ChargeRepository chargeRepository;

    @Before
    public void setUp() throws Exception {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext ec) {
                //                    ec.executeChild(this, new DocumentTypesAndTemplatesForCapexFixture());
                ec.executeChild(this, new IncomingChargesItaXlsxFixture());
            }
        });
    }

    @Test
    public void happyCase() throws Exception {
        // when
        List<Charge> italianWorkTypeChoices = chargeRepository.choicesItalianWorkTypes();

        // then
        assertThat(italianWorkTypeChoices).hasSize(17);
        assertThat(italianWorkTypeChoices.get(0).getReference()).startsWith("ITWT");
    }

}
