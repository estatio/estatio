package org.estatio.module.coda.dom.doc;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.charge.dom.Charge;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class DerivedObjectUpdater_effectiveXxxOf_Test {

    private Project project = new Project();
    private Charge charge = new Charge();

    private CodaDocLine codaDocLine;

    @Before
    public void setUp() throws Exception {
        codaDocLine = new CodaDocLine();

        codaDocLine.setExtRefProject(project);
        codaDocLine.setExtRefWorkTypeCharge(charge);
    }

    @Test
    public void given_analysis_and_proforma() throws Exception {
        // given
        codaDocLine.setLineType(LineType.ANALYSIS);
        codaDocLine.setAccountCodeEl5(CodaDocLine.EL5_FOR_PRO_FORMA);

        // when, then
        assertThat(DerivedObjectUpdater.effectiveChargeOf(codaDocLine)).isNull();
        assertThat(DerivedObjectUpdater.effectiveProjectOf(codaDocLine)).isNull();
    }

    @Test
    public void given_analysis_and_not_proforma() throws Exception {
        // given
        codaDocLine.setLineType(LineType.ANALYSIS);
        codaDocLine.setAccountCodeEl5("ABCDEF");

        // when, then
        assertThat(DerivedObjectUpdater.effectiveChargeOf(codaDocLine)).isSameAs(charge);
        assertThat(DerivedObjectUpdater.effectiveProjectOf(codaDocLine)).isSameAs(project);
    }

    @Test
    public void given_summary_and_proforma() throws Exception {
        // given
        codaDocLine.setLineType(LineType.SUMMARY);
        codaDocLine.setAccountCodeEl5(CodaDocLine.EL5_FOR_PRO_FORMA);

        // when, then
        assertThat(DerivedObjectUpdater.effectiveChargeOf(codaDocLine)).isSameAs(charge);
        assertThat(DerivedObjectUpdater.effectiveProjectOf(codaDocLine)).isSameAs(project);
    }

    private static BigDecimal bd(final String val) {
        return new BigDecimal(val);
    }

}