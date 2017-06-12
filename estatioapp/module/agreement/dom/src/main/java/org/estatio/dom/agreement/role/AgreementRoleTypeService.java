package org.estatio.dom.agreement.role;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

@DomainService(nature = NatureOfService.DOMAIN)
public class AgreementRoleTypeService {

    @PostConstruct
    @Programmatic
    public void init() {

    }

    @Inject
    List<AgreementRoleTypeServiceSupport> supportServices;

    @Inject
    AgreementRoleTypeRepository agreementRoleTypeRepository;


}
