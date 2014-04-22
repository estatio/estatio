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
package org.estatio.dom.bankguarantee;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.AutoComplete;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.NotPersisted;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Where;

import org.estatio.dom.agreement.Agreement;
import org.estatio.dom.agreement.AgreementRole;
import org.estatio.dom.party.Party;

@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByReference", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.bankguarantee.BankGuarantee "
                        + "WHERE reference == :reference"),
        @javax.jdo.annotations.Query(
                name = "matchByReferenceOrName", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.bankguarantee.BankGuarantee "
                        + "WHERE reference.matches(:referenceOrName)"
                        + "|| name.matches(:referenceOrName)")
})
@AutoComplete(repository = BankGuarantees.class, action = "autoComplete")
@Bookmarkable
public class BankGuarantee
        extends Agreement {

    @Override
    @NotPersisted
    @Hidden(where = Where.PARENTED_TABLES)
    public Party getPrimaryParty() {
        final AgreementRole ar = getPrimaryAgreementRole();
        return partyOf(ar);
    }

    @Override
    @NotPersisted
    public Party getSecondaryParty() {
        final AgreementRole ar = getSecondaryAgreementRole();
        return partyOf(ar);
    }

    @Programmatic
    protected AgreementRole getPrimaryAgreementRole() {
        return findCurrentOrMostRecentAgreementRole(BankGuaranteeConstants.ART_CREDITOR);
    }

    @Programmatic
    protected AgreementRole getSecondaryAgreementRole() {
        return findCurrentOrMostRecentAgreementRole(BankGuaranteeConstants.ART_DEBTOR);
    }

    // //////////////////////////////////////

    private LocalDate terminationDate;

    @Optional
    public LocalDate getTerminationDate() {
        return terminationDate;
    }

    public void setTerminationDate(LocalDate terminationDate) {
        this.terminationDate = terminationDate;
    }

}
