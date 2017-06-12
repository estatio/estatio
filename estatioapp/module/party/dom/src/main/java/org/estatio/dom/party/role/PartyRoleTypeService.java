package org.estatio.dom.party.role;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

@DomainService(nature = NatureOfService.DOMAIN)
public class PartyRoleTypeService {

    @PostConstruct
    @Programmatic
    public void init() {

        for (PartyRoleTypeServiceSupport supportService : supportServices) {
            List<IPartyRoleType> partyRoleTypes = supportService.listAll();
            for (IPartyRoleType partyRoleType : partyRoleTypes) {
                partyRoleType.findOrCreateUsing(partyRoleTypeRepository);
            }
        }
    }

    @Inject
    List<PartyRoleTypeServiceSupport> supportServices;

    @Inject
    PartyRoleTypeRepository partyRoleTypeRepository;


}
