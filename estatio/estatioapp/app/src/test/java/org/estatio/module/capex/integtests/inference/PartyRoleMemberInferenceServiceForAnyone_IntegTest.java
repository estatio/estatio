/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.module.capex.integtests.inference;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.sudo.SudoService;

import org.estatio.module.asset.fixtures.person.enums.Person_enum;
import org.estatio.module.capex.dom.invoice.inference.PartyRoleMemberInferenceServiceForAnyone;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;
import org.estatio.module.party.dom.role.PartyRoleTypeEnum;

public class PartyRoleMemberInferenceServiceForAnyone_IntegTest extends CapexModuleIntegTestAbstract {


    @Inject
    PartyRoleMemberInferenceServiceForAnyone partyRoleMemberInferenceServiceForAnyone;

    @Before
    public void setupData() {

        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {

                executionContext.executeChild(this, Person_enum.JonathanIncomingInvoiceManagerGb.builder());
                executionContext.executeChild(this, Person_enum.DylanOfficeAdministratorGb.builder());
                executionContext.executeChild(this, Person_enum.EmmaTreasurerGb.builder());
                executionContext.executeChild(this, Person_enum.FloellaAssetManagerGb.builder());


            }
        });

    }


    @Test
    public void infer_members_of_takes_role_into_account() throws Exception {

        sudoService.sudo(Person_enum.JonathanIncomingInvoiceManagerGb.getSecurityUserName(), () -> {

            Assertions.assertThat(partyRoleMemberInferenceServiceForAnyone.inferMembersOf(PartyRoleTypeEnum.OFFICE_ADMINISTRATOR).size()).isEqualTo(1);
            Assertions.assertThat(partyRoleMemberInferenceServiceForAnyone.inferMembersOf(PartyRoleTypeEnum.TREASURER).size()).isEqualTo(1);
            Assertions.assertThat(partyRoleMemberInferenceServiceForAnyone.inferMembersOf(PartyRoleTypeEnum.INCOMING_INVOICE_MANAGER).size()).isEqualTo(1);
            Assertions.assertThat(partyRoleMemberInferenceServiceForAnyone.inferMembersOf(null).size()).isEqualTo(4);

        });

    }

    @Inject SudoService sudoService;

}