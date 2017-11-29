package org.estatio.module.capex.fixtures.document.enums;

import org.apache.isis.applib.fixturescripts.EnumWithBuilderScript;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.module.capex.fixtures.document.builders.IncomingPdfBuilder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(chain = true)
public enum IncomingPdf_enum implements EnumWithBuilderScript<Document, IncomingPdfBuilder> {

    FakeInvoice1(IncomingPdf_enum.class, "fakeInvoice1.pdf"),
    FakeInvoice2(IncomingPdf_enum.class, "fakeInvoice2.pdf"),
    FakeOrder1(IncomingPdf_enum.class, "fakeOrder1.pdf"),
    FakeOrder2(IncomingPdf_enum.class, "fakeOrder2.pdf"),
    ;

    private final Class<?> contextClass;
    private final String resourceName;

    @Override
    public IncomingPdfBuilder toFixtureScript() {
        return new IncomingPdfBuilder()
                .setContextClass(contextClass)
                .setResourceName(resourceName);
    }

}
