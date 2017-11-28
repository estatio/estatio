package org.estatio.module.capex.fixtures.document;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.capex.fixtures.document.personas.IncomingPdfForFakeInvoice1;
import org.estatio.module.capex.fixtures.document.personas.IncomingPdfForFakeOrder1;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public class IncomingPdfFixture extends FixtureScript {

    @Getter @Setter
    private String runAs;

    @Override
    protected void execute(final ExecutionContext executionContext) {

        executionContext.executeChild(this, new IncomingPdfForFakeOrder1());
        executionContext.executeChild(this, new IncomingPdfForFakeInvoice1());

    }

}
