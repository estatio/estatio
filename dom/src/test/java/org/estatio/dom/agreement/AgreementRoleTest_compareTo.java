/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
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

import org.joda.time.LocalDate;
import org.junit.Before;

import org.apache.isis.core.unittestsupport.comparable.ComparableContractTest_compareTo;

import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyForTesting;

public class AgreementRoleTest_compareTo extends ComparableContractTest_compareTo<AgreementRole> {

    private Agreement agreement1;
    private Agreement agreement2;

    private Party party1;
    private Party party2;
    
    private AgreementRoleType type1;
    private AgreementRoleType type2;
    
    @Before
    public void setup() {
        
        agreement1 = new AgreementForTesting();
        agreement2 = new AgreementForTesting();
        agreement1.setReference("A");
        agreement2.setReference("B");
        
        party1 = new PartyForTesting();
        party2 = new PartyForTesting();
        party1.setName("A");
        party2.setName("B");
        
        type1 = new AgreementRoleType();
        type2 = new AgreementRoleType();
        type1.setTitle("Abc");
        type2.setTitle("Def");
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List<List<AgreementRole>> orderedTuples() {
        return listOf(
                    listOf(
                            newAgreementRole(null, null, null, null), 
                            newAgreementRole(agreement1, null, null, null), 
                            newAgreementRole(agreement1, null, null, null), 
                            newAgreementRole(agreement2, null, null, null)
                            ), 
                    listOf(
                            newAgreementRole(agreement1, null, null, null), 
                            newAgreementRole(agreement1, party1, null, null), 
                            newAgreementRole(agreement1, party1, null, null), 
                            newAgreementRole(agreement1, party2, null, null)
                            ), 
                    listOf(
                            newAgreementRole(agreement1, party1, null, null), 
                            newAgreementRole(agreement1, party1, new LocalDate(2013,4,1), null), 
                            newAgreementRole(agreement1, party1, new LocalDate(2013,4,1), null), 
                            newAgreementRole(agreement1, party1, new LocalDate(2013,3,1), null)
                            ),
                    listOf(
                            newAgreementRole(agreement1, party1, new LocalDate(2013,4,1), null), 
                            newAgreementRole(agreement1, party1, new LocalDate(2013,4,1), type1), 
                            newAgreementRole(agreement1, party1, new LocalDate(2013,4,1), type1), 
                            newAgreementRole(agreement1, party1, new LocalDate(2013,4,1), type2)
                            ) 
                );
    }

    private AgreementRole newAgreementRole(Agreement agreement, Party party, LocalDate date, AgreementRoleType art) {
        final AgreementRole ar = new AgreementRole();
        ar.setAgreement(agreement);
        ar.setParty(party);
        ar.setStartDate(date);
        ar.setType(art);
        return ar;
    }


}
