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
package com.eurocommercialproperties.estatio.dom.party;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;

import org.apache.isis.applib.AbstractDomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ObjectType;
import org.apache.isis.applib.value.Date;

@javax.jdo.annotations.PersistenceCapable(schema = "party", identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(strategy = IdGeneratorStrategy.IDENTITY)
@ObjectType("PREG")
public class PartyRegistration extends AbstractDomainObject {

    // {{ Party (property)
    private Party party;

    @javax.jdo.annotations.Column(name = "PARTY_ID")
    @MemberOrder(sequence = "1")
    public Party getParty() {
        return party;
    }

    public void setParty(final Party party) {
        this.party = party;
    }

    // }}

    // {{ PartyRegistrationType (property)
    private PartyRegistrationType partyRegistrationType;

    @MemberOrder(sequence = "1")
    public PartyRegistrationType getPartyRegistrationType() {
        return partyRegistrationType;
    }

    public void setPartyRegistrationType(final PartyRegistrationType partyRegistrationType) {
        this.partyRegistrationType = partyRegistrationType;
    }

    // }}

    // {{ FromDate (property)
    private Date fromDate;

    @MemberOrder(sequence = "1")
    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(final Date fromDate) {
        this.fromDate = fromDate;
    }

    // }}

    // {{ ThruDate (property)
    private Date thruDate;

    @MemberOrder(sequence = "1")
    public Date getThruDate() {
        return thruDate;
    }

    public void setThruDate(final Date thruDate) {
        this.thruDate = thruDate;
    }
    // }}

}
