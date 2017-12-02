package org.estatio.module.capex.fixtures.document.personas;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.capex.fixtures.document.enums.IncomingPdf_enum;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public class IncomingPdfForFakeInvoice2 extends FixtureScript {

    @Getter @Setter
    private String runAs;

    public static String resourceName = IncomingPdf_enum.FakeInvoice2.getResourceName();

    @Override
    protected void execute(final FixtureScript.ExecutionContext executionContext) {

        executionContext.executeChild(this, IncomingPdf_enum.FakeInvoice2.builder().setRunAs(runAs));

    }


}
