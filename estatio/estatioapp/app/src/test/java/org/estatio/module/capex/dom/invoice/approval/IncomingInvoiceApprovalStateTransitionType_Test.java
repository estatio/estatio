package org.estatio.module.capex.dom.invoice.approval;

import java.util.TreeSet;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import org.estatio.module.capex.dom.invoice.IncomingInvoiceRoleTypeEnum;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.role.PartyRole;
import org.estatio.module.party.dom.role.PartyRoleType;

public class IncomingInvoiceApprovalStateTransitionType_Test {


    @Test
    public void hasPreferredManagerAndDirector() {

        // given
        PartyRoleType roleType = new PartyRoleType();
        PartyRole role = new PartyRole();
        role.setRoleType(roleType);
        Organisation buyer = new Organisation();
        buyer.setRoles(new TreeSet<>());
        buyer.getRoles().add(role);

        // when
        roleType.setKey(IncomingInvoiceRoleTypeEnum.ECP_MGT_COMPANY.getKey());
        // then
        Assertions.assertThat(IncomingInvoiceApprovalStateTransitionType.hasPreferredManagerAndDirector(buyer)).isTrue();


        // when
        roleType.setKey(IncomingInvoiceRoleTypeEnum.ECP.getKey());
        // then
        Assertions.assertThat(IncomingInvoiceApprovalStateTransitionType.hasPreferredManagerAndDirector(buyer)).isFalse();


    }

}