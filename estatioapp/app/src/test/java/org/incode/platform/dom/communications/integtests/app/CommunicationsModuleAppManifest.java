package org.incode.platform.dom.communications.integtests.app;

import org.apache.isis.applib.AppManifestAbstract;

import org.isisaddons.module.command.CommandModule;
import org.isisaddons.module.fakedata.FakeDataModule;
import org.isisaddons.module.freemarker.dom.FreeMarkerModule;
import org.isisaddons.module.pdfbox.dom.PdfBoxModule;

import org.incode.module.communications.dom.CommunicationsModuleDomModule;
import org.incode.module.country.dom.CountryModule;
import org.incode.module.document.dom.DocumentModule;
import org.incode.platform.dom.communications.integtests.app.services.DemoAppApplicationModuleServicesSubmodule;
import org.incode.platform.dom.communications.integtests.demo.ExampleDomDemoDomSubmodule;

/**
 * Bootstrap the application.
 */
public class CommunicationsModuleAppManifest extends AppManifestAbstract {

    public static final Builder BUILDER = Builder.forModules(CommunicationsModuleDomModule.class,
            DocumentModule.class,
            CountryModule.class,
            ExampleDomDemoDomSubmodule.class,
            DemoAppApplicationModuleServicesSubmodule.class,
            PdfBoxModule.class,
            CommandModule.class,
            FreeMarkerModule.class,
            FakeDataModule.class
    );


    public CommunicationsModuleAppManifest() {
        super(BUILDER);
    }

}
