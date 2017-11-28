package org.estatio.module.capex.fixtures.document.personas;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.module.capex.fixtures.document.IncomingPdfAbstract;
import org.estatio.module.capex.fixtures.document.enums.IncomingPdf_enum;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public class IncomingPdfForFakeOrder1 extends IncomingPdfAbstract {

    public final static IncomingPdf_enum data = IncomingPdf_enum.FakeOrder1;

    @Getter @Setter
    private String runAs;

    @Override
    protected void execute(final ExecutionContext executionContext) {

        final Document document = data.uploadUsing(serviceRegistry, getRunAs(), executionContext);
        executionContext.addResult(this,data.getResourceName(), document );

    }


}
