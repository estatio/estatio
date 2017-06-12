package org.estatio.capex.dom.project;

import java.util.Arrays;
import java.util.List;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.incode.module.base.dom.TitledEnum;
import org.incode.module.base.dom.utils.StringUtils;

import org.estatio.dom.party.role.IPartyRoleType;
import org.estatio.dom.party.role.PartyRoleTypeServiceSupport;

public enum ProjectRoleTypeEnum implements TitledEnum, IPartyRoleType {

    PROJECT_MANAGER;

    @Override
    public String getKey() {
        return this.name();
    }

    @Override
    public String getTitle() {
        return title();
    }

    public String title() {
        return StringUtils.enumTitle(this.toString());
    }





    @DomainService(nature = NatureOfService.DOMAIN)
    public static class ListAll implements PartyRoleTypeServiceSupport {
        @Override
        public List<IPartyRoleType> listAll() {
            return Arrays.asList(ProjectRoleTypeEnum.values());
        }
    }

}
