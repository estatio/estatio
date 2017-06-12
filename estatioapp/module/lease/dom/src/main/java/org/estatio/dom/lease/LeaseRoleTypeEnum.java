package org.estatio.dom.lease;

import java.util.Arrays;
import java.util.List;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.dom.party.role.IPartyRoleType;
import org.estatio.dom.party.role.PartyRoleTypeServiceSupport;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum LeaseRoleTypeEnum implements IPartyRoleType {
    LANDLORD("Landlord"),
    TENANT("Tenant");

    @Override
    public String getKey() {
        return this.name();
    }

    @Getter
    private String title;


    public static class Meta {
        public final static int MAX_LEN = 30;

        private Meta() {
        }
    }


    @DomainService(nature = NatureOfService.DOMAIN)
    public static class ListAll implements PartyRoleTypeServiceSupport {
        @Override
        public List<IPartyRoleType> listAll() {
            return Arrays.asList(LeaseRoleTypeEnum.values());
        }
    }

}
