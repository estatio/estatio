/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
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
package org.estatio.dom.asset;

import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberGroups;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;

import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.Status;
import org.estatio.dom.WithIntervalMutable;
import org.estatio.dom.party.Party;
import org.estatio.dom.valuetypes.LocalDateInterval;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
@javax.jdo.annotations.Queries({
    @javax.jdo.annotations.Query(
        name = "findByAssetAndPartyAndType", language = "JDOQL", 
        value = "SELECT " +
        		"FROM org.estatio.dom.asset.FixedAssetRole " +
        		"WHERE asset == :asset " +
        		"&& party == :party " +
        		"&& type == :type"),
	@javax.jdo.annotations.Query(
        name = "findByAssetAndPartyAndTypeAndStartDate", language = "JDOQL", 
        value = "SELECT " +
                "FROM org.estatio.dom.asset.FixedAssetRole " +
                "WHERE asset == :asset " +
                "&& party == :party " +
	            "&& type == :type " + 
                "&& startDate == :startDate"),
    @javax.jdo.annotations.Query(
        name = "findByAssetAndPartyAndTypeAndEndDate", language = "JDOQL", 
        value = "SELECT " +
                "FROM org.estatio.dom.asset.FixedAssetRole " +
                "WHERE asset == :asset " +
                "&& party == :party " +
                "&& type == :type " + 
                "&& endDate == :endDate"),
})
@Bookmarkable(BookmarkPolicy.AS_CHILD)
@MemberGroups({"General", "Dates", "Related"})
public class FixedAssetRole extends EstatioTransactionalObject<FixedAssetRole, Status> implements WithIntervalMutable<FixedAssetRole> {

    public FixedAssetRole() {
        super("asset, party, startDate desc, type", Status.LOCKED, Status.UNLOCKED);
    }

    // //////////////////////////////////////

    private Status status;

    @MemberOrder(sequence = "4.5")
    @Disabled
    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public void setStatus(final Status status) {
        this.status = status;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name="ASSET_ID")
    private FixedAsset asset;

    @Title(sequence = "3", prepend = ":")
    @MemberOrder(sequence = "1")
    @Hidden(where = Where.REFERENCES_PARENT)
    @Disabled
    public FixedAsset getAsset() {
        return asset;
    }

    public void setAsset(final FixedAsset asset) {
        this.asset = asset;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name="PARTY_ID")
    private Party party;

    @Title(sequence = "2", prepend = ":")
    @MemberOrder(sequence = "2")
    @Hidden(where = Where.REFERENCES_PARENT)
    @Disabled
    public Party getParty() {
        return party;
    }

    public void setParty(final Party party) {
        this.party = party;
    }

    // //////////////////////////////////////

    private FixedAssetRoleType type;

    @Disabled
    @MemberOrder(sequence = "3")
    @Title(sequence = "1")
    public FixedAssetRoleType getType() {
        return type;
    }

    public void setType(final FixedAssetRoleType type) {
        this.type = type;
    }

    // //////////////////////////////////////

    private LocalDate startDate;

    @MemberOrder(name="Dates", sequence = "4")
    @Optional
    @Disabled
    @Override
    public LocalDate getStartDate() {
        return startDate;
    }

    @Override
    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }


    @javax.jdo.annotations.Persistent
    private LocalDate endDate;

    @MemberOrder(name="Dates", sequence = "5")
    @Optional
    @Disabled
    @Override
    public LocalDate getEndDate() {
        return endDate;
    }

    @Override
    public void setEndDate(final LocalDate endDate) {
        this.endDate = endDate;
    }

    // //////////////////////////////////////

    @MemberOrder(name="endDate", sequence="1")
    @ActionSemantics(Of.IDEMPOTENT)
    @Override
    public FixedAssetRole changeDates(
            final @Named("Start Date") LocalDate startDate, 
            final @Named("End Date") LocalDate endDate) {
        setStartDate(startDate);
        setEndDate(endDate);
        return this;
    }

    public String disableChangeDates(
            final LocalDate startDate, 
            final LocalDate endDate) {
        return getStatus().isLocked()? "Cannot modify when locked": null;
    }
    
    @Override
    public LocalDate default0ChangeDates() {
        return getStartDate();
    }
    @Override
    public LocalDate default1ChangeDates() {
        return getEndDate();
    }
    
    @Override
    public String validateChangeDates(
            final LocalDate startDate, 
            final LocalDate endDate) {
        return startDate.isBefore(endDate)?null:"Start date must be before end date";
    }

    // //////////////////////////////////////

    @Override
    @Programmatic
    public LocalDateInterval getInterval() {
        return LocalDateInterval.including(getStartDate(), getEndDate());
    }

    // //////////////////////////////////////

    @MemberOrder(name="Related", sequence = "9.1")
    @Named("Previous Role")
    @Hidden(where=Where.ALL_TABLES)
    @Disabled
    @Optional
    @Override
    public FixedAssetRole getPrevious() {
        return getStartDate() != null
                ?fixedAssetRoles.findByAssetAndPartyAndTypeAndEndDate(getAsset(), getParty(), getType(), getStartDate().minusDays(1))
                :null;
    }

    @MemberOrder(name="Related", sequence = "9.1")
    @Named("Next Role")
    @Hidden(where=Where.ALL_TABLES)
    @Disabled
    @Optional
    @Override
    public FixedAssetRole getNext() {
        return getEndDate() != null
                ?fixedAssetRoles.findByAssetAndPartyAndTypeAndStartDate(getAsset(), getParty(), getType(), getEndDate().plusDays(1))
                :null;
    }

    // //////////////////////////////////////

    private FixedAssetRoles fixedAssetRoles;
    public void injectFixedAssetRoles(FixedAssetRoles fixedAssetRoles) {
        this.fixedAssetRoles = fixedAssetRoles;
    }
    
}
