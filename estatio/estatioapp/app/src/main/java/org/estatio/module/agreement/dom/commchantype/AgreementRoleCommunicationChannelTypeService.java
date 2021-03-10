package org.estatio.module.agreement.dom.commchantype;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.module.agreement.dom.AgreementRoleCommunicationChannelTypeRepository;

@DomainService(nature = NatureOfService.DOMAIN)
public class AgreementRoleCommunicationChannelTypeService {

    @PostConstruct
    @Programmatic
    public void init() {

        for (AgreementRoleCommunicationChannelTypeServiceSupport supportService : supportServices) {
            List<IAgreementRoleCommunicationChannelType> types = supportService.listAll();
            for (IAgreementRoleCommunicationChannelType type : types) {
                type.findOrCreateUsing(agreementRoleCommunicationChannelTypeRepository);
            }
        }

    }

    @Inject
    List<AgreementRoleCommunicationChannelTypeServiceSupport> supportServices;

    @Inject
    AgreementRoleCommunicationChannelTypeRepository agreementRoleCommunicationChannelTypeRepository;


}
