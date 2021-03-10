package org.incode.platform.dom.communications.integtests.app;

import org.apache.isis.applib.AppManifestAbstract;

import org.isisaddons.module.command.CommandModule;
import org.isisaddons.module.fakedata.FakeDataModule;
import org.isisaddons.module.freemarker.dom.FreeMarkerModule;
import org.isisaddons.module.pdfbox.dom.PdfBoxModule;

import org.incode.module.communications.CommunicationsModule;
import org.incode.module.country.CountryModule;
import org.incode.module.document.DocumentModule;
import org.incode.platform.dom.communications.integtests.app.services.FakeCommsServiceModule;
import org.incode.platform.dom.communications.integtests.demo.CommunicationsModuleDemoDomSubmodule;

/**
 * Bootstrap the application.
 */
public class CommunicationsModuleAppManifest extends AppManifestAbstract {

    public static final Builder BUILDER = Builder.forModules(CommunicationsModule.class,
            DocumentModule.class,
            CountryModule.class,
            CommunicationsModuleDemoDomSubmodule.class,
            FakeCommsServiceModule.class,
            PdfBoxModule.class,
            CommandModule.class,
            FreeMarkerModule.class,
            FakeDataModule.class
    );


    public CommunicationsModuleAppManifest() {
        super(BUILDER);
    }

}
