package org.incode.platform.dom.document.integtests.app;

import org.apache.isis.applib.AppManifestAbstract;

import org.isisaddons.module.command.CommandModule;
import org.isisaddons.module.fakedata.FakeDataModule;
import org.isisaddons.module.freemarker.dom.FreeMarkerModule;
import org.isisaddons.module.stringinterpolator.StringInterpolatorModule;
import org.isisaddons.module.xdocreport.dom.XDocReportModule;

import org.incode.module.docrendering.freemarker.FreemarkerDocRenderingModule;
import org.incode.module.docrendering.stringinterpolator.StringInterpolatorDocRenderingModule;
import org.incode.module.docrendering.gotenberg.dom.impl.GotenbergRenderingModule;
import org.incode.module.docrendering.xdocreport.XDocReportDocRenderingModule;
import org.incode.module.document.DocumentModule;
import org.incode.platform.dom.document.integtests.dom.document.DocumentModuleIntegrationSubmodule;

/**
 * Bootstrap the application.
 */
public class DocumentModuleAppManifest extends AppManifestAbstract {

    public static final Builder BUILDER = Builder.forModules(
            DocumentModule.class, // dom module
            DocumentModuleIntegrationSubmodule.class,
            DocumentAppModule.class,

            CommandModule.class,
            FakeDataModule.class,

            FreemarkerDocRenderingModule.class,
            FreeMarkerModule.class,

            StringInterpolatorDocRenderingModule.class,
            StringInterpolatorModule.class,

            GotenbergRenderingModule.class,
            XDocReportDocRenderingModule.class,
            XDocReportModule.class
    );

    public DocumentModuleAppManifest() {
        super(BUILDER);
    }

}
