package org.estatio.module.capex.fixtures.document.enums;

import java.io.IOException;
import java.net.URL;

import com.google.common.io.Resources;

import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.sudo.SudoService;
import org.apache.isis.applib.services.wrapper.WrapperFactory;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.base.platform.fixturesupport.DataEnum;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.module.capex.app.DocumentMenu;
import org.estatio.module.capex.fixtures.document.builders.IncomingPdfBuilder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(chain = true)
public enum IncomingPdf_enum implements DataEnum<Document, IncomingPdfBuilder> {

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
