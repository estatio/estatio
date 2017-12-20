package org.incode.platform.dom.classification.integtests.dom.classification.dom.classification.otherwithatpath;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.incode.module.classification.dom.spi.ApplicationTenancyService;
import org.incode.platform.dom.classification.integtests.demo.dom.otherwithatpath.OtherObjectWithAtPath;

@DomainService(nature = NatureOfService.DOMAIN)
public class AppTenancyServiceForOtherObjectWithAtPath implements ApplicationTenancyService {

    @Override
    public String atPathFor(final Object domainObjectToClassify) {
        if (domainObjectToClassify instanceof OtherObjectWithAtPath) {
            return ((OtherObjectWithAtPath) domainObjectToClassify).getAtPath();
        }
        return null;
    }
}
