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

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.VersionStrategy;

import org.estatio.dom.EstatioTransactionalObject;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.MemberOrder;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Version(strategy=VersionStrategy.VERSION_NUMBER, column="VERSION")
public class PartyRole extends EstatioTransactionalObject {

    
    // {{ Party (property)
    //@javax.jdo.annotations.Column(name="PARTY_ID")
    private Party party;

    @MemberOrder(sequence = "1")
    public Party getParty() {
        return party;
    }

    public void setParty(final Party party) {
        this.party = party;
    }

    // }}

    // {{ PartyRoleType (property)
    private PartyRoleType partyRoleType;

    @MemberOrder(sequence = "1")
    public PartyRoleType getPartyRoleType() {
        return partyRoleType;
    }

    public void setPartyRoleType(final PartyRoleType partyRoleType) {
        this.partyRoleType = partyRoleType;
    }

    // }}

    // {{ StartDateDate (property)
    // REVIEW: doesn't this require a @Persistent
    private LocalDate startDateDate;

    @MemberOrder(sequence = "1")
    public LocalDate getStartDateDate() {
        return startDateDate;
    }

    public void setStartDateDate(final LocalDate startDateDate) {
        this.startDateDate = startDateDate;
    }

    // }}

    // {{ EndDateDate (property)
    // REVIEW: doesn't this require a @Persistent
    private LocalDate endDateDate;

    @MemberOrder(sequence = "1")
    public LocalDate getEndDateDate() {
        return endDateDate;
    }

    public void setEndDateDate(final LocalDate endDateDate) {
        this.endDateDate = endDateDate;
    }
    // }}

}
