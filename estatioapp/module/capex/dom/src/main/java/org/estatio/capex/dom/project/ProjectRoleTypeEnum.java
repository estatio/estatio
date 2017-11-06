package org.estatio.capex.dom.project;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.module.party.dom.role.IPartyRoleType;
import org.estatio.module.party.dom.role.PartyRoleTypeServiceSupportAbstract;

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

