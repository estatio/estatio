/*
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
package org.estatio.dom.party.relationship;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Where;

import org.estatio.dom.party.Party;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.dom.party.relationship.PartyRelationshipView"
)
public class PartyRelationshipView  {

    //region > constructors, title
    public PartyRelationshipView() {

    }

    public PartyRelationshipView(PartyRelationship partyRelationship, Party fromParty) {
        this.partyRelationship = partyRelationship;
        if (fromParty.equals(partyRelationship.getFrom())) {
            this.from = partyRelationship.getFrom();
            this.to = partyRelationship.getTo();
        } else {
            this.from = partyRelationship.getTo();
            this.to = partyRelationship.getFrom();
        }
        this.relationshipType = partyRelationship.getRelationshipType();
        this.startDate = partyRelationship.getStartDate();
        this.endDate = partyRelationship.getEndDate();
        this.description = partyRelationship.getDescription();
    }

    public String title() {
        return String.format("%s is %s of %s",
                getFrom().getName(),
                getRelationshipType().fromTitle(),
                getTo().getName());
    }

    //endregion


    @Property(hidden = Where.EVERYWHERE)
    @Getter @Setter
    private PartyRelationship partyRelationship;


    @Property(hidden = Where.EVERYWHERE)
    @MemberOrder(sequence = "1")
    @Getter @Setter
    private Party from;


    @MemberOrder(sequence = "2")
    @Getter @Setter
    private Party to;


    @MemberOrder(sequence = "3")
    @Property(hidden = Where.EVERYWHERE)
    @Getter @Setter
    private PartyRelationshipType relationshipType;


    // derived property
    public String getRelationshipToTitle() {
        return getRelationshipType().toTitle();
    }


    @Property(hidden = Where.EVERYWHERE)
    public String getRelationshipFromTitle() {
        return getRelationshipType().toTitle();
    }


    @MemberOrder(sequence = "4")
    @Property(hidden = Where.PARENTED_TABLES)
    @Getter @Setter
    private LocalDate startDate;


    //region > changeDates (action)
    public PartyRelationshipView changeDates(
            final LocalDate startDate,
            final LocalDate endDate) {
        return new PartyRelationshipView(partyRelationship.changeDates(startDate, endDate), getFrom());
    }

    public LocalDate default0ChangeDates() {
        return partyRelationship.default0ChangeDates();
    }

    public LocalDate default1ChangeDates() {
        return partyRelationship.default1ChangeDates();
    }

    public String validateChangeDates(final LocalDate startDate, final LocalDate endDate) {
        return partyRelationship.validateChangeDates(startDate, endDate);
    }
    //endregion


    @Property(hidden = Where.PARENTED_TABLES)
    @Getter @Setter
    private LocalDate endDate;



    @Getter @Setter
    private String description;

    //region > changeDescription (action)
    public PartyRelationshipView changeDescription(
            final String description) {
        return new PartyRelationshipView(partyRelationship.changeDescription(description), getFrom());
    }
    //endregion

}
