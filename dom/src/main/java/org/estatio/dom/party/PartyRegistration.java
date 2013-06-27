/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.estatio.dom.party;

import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberGroups;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Where;

import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.WithInterval;
import org.estatio.dom.valuetypes.LocalDateInterval;

// TODO: is this in scope?
@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Version(strategy=VersionStrategy.VERSION_NUMBER, column="VERSION")
@MemberGroups({"General", "Dates", "Tags", "Related"})
public class PartyRegistration extends EstatioTransactionalObject<PartyRegistration> implements WithInterval<PartyRegistration> {

    public PartyRegistration() {
        // TODO: I made this up...
        super("party, startDate desc");
    }
    
    // //////////////////////////////////////

    private Party party;

    //@javax.jdo.annotations.Column(name = "PARTY_ID")
    @MemberOrder(sequence = "1")
    public Party getParty() {
        return party;
    }

    public void setParty(final Party party) {
        this.party = party;
    }

    // //////////////////////////////////////

    private PartyRegistrationType partyRegistrationType;

    @MemberOrder(sequence = "1")
    public PartyRegistrationType getPartyRegistrationType() {
        return partyRegistrationType;
    }

    public void setPartyRegistrationType(final PartyRegistrationType partyRegistrationType) {
        this.partyRegistrationType = partyRegistrationType;
    }


    // //////////////////////////////////////

    private LocalDate startDate;

    @MemberOrder(name="Dates", sequence = "1")
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }
    
    
    private LocalDate endDate;

    @MemberOrder(name="Dates", sequence = "1")
    @Optional
    @Disabled
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(final LocalDate endDate) {
        this.endDate = endDate;
    }
    
    @Override
    @Programmatic
    public LocalDateInterval getInterval() {
        return LocalDateInterval.including(getStartDate(), getEndDate());
    }

    @Hidden // TODO (where=Where.ALL_TABLES)
    @MemberOrder(name="Related", sequence = "9.1")
    @Named("Previous Registration")
    @Disabled
    @Optional
    @Override
    public PartyRegistration getPrevious() {
        return null;
    }

    @Hidden // TODO (where=Where.ALL_TABLES)
    @MemberOrder(name="Related", sequence = "9.2")
    @Named("Next Registration")
    @Disabled
    @Optional
    @Override
    public PartyRegistration getNext() {
        return null;
    }

    // //////////////////////////////////////
    
}
