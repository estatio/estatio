package org.estatio.dom.agreement.commchantype;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.dom.agreement.AgreementRoleCommunicationChannelTypeRepository;

@DomainService(nature = NatureOfService.DOMAIN)
public class AgreementRoleCommunicationChannelTypeService {

    @PostConstruct
    @Programmatic
    public void init() {

    }

    @Inject
    List<AgreementRoleCommunicationChannelTypeServiceSupport> supportServices;

    @Inject
    AgreementRoleCommunicationChannelTypeRepository agreementRoleCommunicationChannelTypeRepository;


}
