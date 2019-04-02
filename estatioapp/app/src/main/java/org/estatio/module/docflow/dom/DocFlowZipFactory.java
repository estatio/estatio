package org.estatio.module.docflow.dom;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

@DomainService(nature = NatureOfService.DOMAIN)
public class DocFlowZipFactory {

    DocFlowZip createTransient(
            final long sdId,
            final String atPath,
            final String sha256) {

        DocFlowZip docFlowZip = new DocFlowZip(sdId, atPath, sha256);
        return serviceRegistry2.injectServicesInto(docFlowZip);
    }


    @Inject
    ServiceRegistry2 serviceRegistry2;


}
