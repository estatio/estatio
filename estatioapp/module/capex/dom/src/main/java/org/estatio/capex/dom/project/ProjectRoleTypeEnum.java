package org.estatio.capex.dom.project;

import java.util.List;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.dom.party.Person;
import org.estatio.dom.party.role.IPartyRoleType;
import org.estatio.dom.party.role.PartyRoleType;
import org.estatio.dom.party.role.PartyRoleTypeServiceSupportAbstract;

public enum ProjectRoleTypeEnum implements IPartyRoleType {

    PROJECT_MANAGER;

    @Override
    public String getKey() {
        return this.name();
    }


    @DomainService(nature = NatureOfService.DOMAIN)
    public static class SupportService extends PartyRoleTypeServiceSupportAbstract<ProjectRoleTypeEnum> {
        public SupportService() {
            super(ProjectRoleTypeEnum.class);
        }
    }

}

