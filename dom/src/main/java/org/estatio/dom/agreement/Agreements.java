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
package org.estatio.dom.agreement;

import java.util.List;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.NotContributed;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.party.Party;

@Hidden
public class Agreements extends EstatioDomainService<Agreement> {

    public Agreements() {
        super(Agreements.class, Agreement.class);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    public Agreement findAgreementByReference(final String reference) {
        return firstMatch("findByReference", 
                "reference", reference);
    }

    @ActionSemantics(Of.SAFE)
    @NotContributed
    public List<Agreement> findByAgreementTypeAndRoleTypeAndParty(
            final AgreementType agreementType, final AgreementRoleType agreementRoleType, final Party party) {
        return allMatches("findByAgreementTypeAndRoleTypeAndParty", 
                "agreementType", agreementType, 
                "roleType", agreementRoleType, 
                "party", party);
    }

}
