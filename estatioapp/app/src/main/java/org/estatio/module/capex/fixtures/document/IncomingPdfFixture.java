package org.estatio.module.capex.fixtures.document;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.capex.fixtures.document.enums.IncomingPdf_enum;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public class IncomingPdfFixture extends FixtureScript {

    @Getter @Setter
    private String runAs;

    @Override
    protected void execute(final ExecutionContext executionContext) {

        executionContext.executeChild(this, IncomingPdf_enum.FakeOrder1.builder().setRunAs(runAs));
        executionContext.executeChild(this, IncomingPdf_enum.FakeInvoice1.builder().setRunAs(runAs));

    }

}
