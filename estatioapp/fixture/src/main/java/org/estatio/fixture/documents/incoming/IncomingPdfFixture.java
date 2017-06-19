package org.estatio.fixture.documents.incoming;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;
import com.google.common.io.Resources;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.sudo.SudoService;
import org.apache.isis.applib.value.Blob;

import org.estatio.capex.dom.documents.DocumentMenu;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public class IncomingPdfFixture extends FixtureScript {

    @Getter @Setter
    private String runAs;

    @Override
    protected void execute(final ExecutionContext executionContext) {

        List<String> resourceNames = Lists.newArrayList(
            "fakeOrder1.pdf",
            "fakeInvoice1.pdf"
        );

        for (String resourceName : resourceNames) {
            final URL url = Resources.getResource(getClass(), resourceName);
            byte[] bytes;
            try {
                bytes = Resources.toByteArray(url);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            final Blob blob = new Blob(resourceName, "application/pdf", bytes);
            if(runAs != null) {
                sudoService.sudo(runAs, (Runnable) () -> wrap(documentMenu).upload(blob));
            } else {
                wrap(documentMenu).upload(blob);
            }
        }
    }

    @Inject
    DocumentMenu documentMenu;
    @Inject
    SudoService sudoService;

}
