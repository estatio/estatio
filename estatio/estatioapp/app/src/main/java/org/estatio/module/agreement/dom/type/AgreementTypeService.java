package org.estatio.module.agreement.dom.type;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

@DomainService(nature = NatureOfService.DOMAIN)
public class AgreementTypeService {

    @PostConstruct
    @Programmatic
    public void init() {

        for (AgreementTypeServiceSupport supportService : supportServices) {
            List<IAgreementType> agreementTypes = supportService.listAll();
            for (IAgreementType agreementType : agreementTypes) {
                agreementType.findOrCreateUsing(agreementTypeRepository);
            }
        }
    }

    @Inject
    List<AgreementTypeServiceSupport> supportServices;

    @Inject
    AgreementTypeRepository agreementTypeRepository;


}
