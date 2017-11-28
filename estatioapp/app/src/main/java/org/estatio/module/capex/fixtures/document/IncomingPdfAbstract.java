package org.estatio.module.capex.fixtures.document;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.inject.Inject;

import com.google.common.io.Resources;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.sudo.SudoService;
import org.apache.isis.applib.value.Blob;

import org.estatio.module.capex.app.DocumentMenu;

public abstract class IncomingPdfAbstract extends FixtureScript {

    protected void uploadDocuments(final List<String> resourceNames, final String runAs){

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
