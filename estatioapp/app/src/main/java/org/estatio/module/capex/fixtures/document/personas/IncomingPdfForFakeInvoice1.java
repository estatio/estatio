package org.estatio.module.capex.fixtures.document.personas;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.capex.fixtures.document.enums.IncomingPdf_enum;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public class IncomingPdfForFakeInvoice1 extends FixtureScript {

    public final static IncomingPdf_enum data = IncomingPdf_enum.FakeInvoice1;

    @Getter @Setter
    private String runAs;

    @Override
    protected void execute(final FixtureScript.ExecutionContext executionContext) {

        executionContext.setParameter("runAs", runAs);
        executionContext.executeChild(this, data.toFixtureScript());

    }


}
