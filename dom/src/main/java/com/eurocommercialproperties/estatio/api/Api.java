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
package com.eurocommercialproperties.estatio.api;

import java.math.BigDecimal;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.joda.time.LocalDate;

import com.eurocommercialproperties.estatio.dom.asset.Properties;
import com.eurocommercialproperties.estatio.dom.asset.Property;
import com.eurocommercialproperties.estatio.dom.asset.PropertyActor;
import com.eurocommercialproperties.estatio.dom.asset.PropertyActorType;
import com.eurocommercialproperties.estatio.dom.asset.PropertyActors;
import com.eurocommercialproperties.estatio.dom.asset.PropertyType;
import com.eurocommercialproperties.estatio.dom.asset.Unit;
import com.eurocommercialproperties.estatio.dom.asset.UnitType;
import com.eurocommercialproperties.estatio.dom.asset.Units;
import com.eurocommercialproperties.estatio.dom.communicationchannel.CommunicationChannel;
import com.eurocommercialproperties.estatio.dom.communicationchannel.CommunicationChannels;
import com.eurocommercialproperties.estatio.dom.geography.Countries;
import com.eurocommercialproperties.estatio.dom.geography.Country;
import com.eurocommercialproperties.estatio.dom.geography.State;
import com.eurocommercialproperties.estatio.dom.geography.States;
import com.eurocommercialproperties.estatio.dom.lease.Lease;
import com.eurocommercialproperties.estatio.dom.lease.LeaseActorType;
import com.eurocommercialproperties.estatio.dom.lease.Leases;
import com.eurocommercialproperties.estatio.dom.party.Organisation;
import com.eurocommercialproperties.estatio.dom.party.Parties;
import com.eurocommercialproperties.estatio.dom.party.Party;
import com.eurocommercialproperties.estatio.dom.party.Person;

@Hidden
public class Api extends AbstractFactoryAndRepository {

    // {{ Id, iconName
    @Override
    public String getId() {
        return "api";
    }

    public String iconName() {
        return "Api";
    }

    // }}

    @ActionSemantics(Of.IDEMPOTENT)
    public void putCountry(@Named("code") String code, @Named("alpha2Code") String alpha2Code, @Named("name") String name) {
        Country country = countries.findByReference(code);
        if (country == null) {
            country = countries.newCountry(code, name);
        }
        country.setName(name);
        country.setAlpha2Code(alpha2Code);
    }

    @ActionSemantics(Of.IDEMPOTENT)
    public void putState(@Named("code") String code, @Named("name") String name, @Named("countryCode") String countryCode) {
        Country country = countries.findByReference(countryCode);
        if (country == null) {
            throw new ApplicationException(String.format("Country with code %1$s not found", countryCode));
        }
        State state = states.findByReference(countryCode);
        if (state == null) {
            state = states.newState(code, name, country);
        }
        state.setName(name);
        state.setCountry(country);
    }

    @ActionSemantics(Of.IDEMPOTENT)
    public void putPerson(@Named("reference") String reference, @Named("initials") @Optional String initials, @Named("firstName") String firstName, @Named("lastName") String lastName) {
        // TODO Add check for return type
        Person person = (Person) parties.findPartyByReference(reference);
        if (person == null) {
            person = parties.newPerson(initials, firstName, lastName);
            person.setReference(reference);
        }
        person.setFirstName(firstName);
        person.setLastName(lastName);
    }
    
    @ActionSemantics(Of.IDEMPOTENT)
    public void putOrganisation(@Named("reference") String reference, @Named("name") String name) {
        Organisation org = parties.findOrganisationByReference(reference);
        if (org == null) {
            org = parties.newOrganisation(name);
            org.setReference(reference);
        }
        org.setName(name);
    }

    @ActionSemantics(Of.IDEMPOTENT)
    public void putProperty(@Named("reference") String reference, @Named("name") String name, @Named("type") String type, @Named("acquireDate") @Optional LocalDate acquireDate, @Named("disposalDate") @Optional LocalDate disposalDate, @Named("openingDate") @Optional LocalDate openingDate,
            @Named("ownerReference") @Optional String ownerReference)  {
        Party owner = parties.findOrganisationByReference(ownerReference);
        if (owner == null) {
            throw new ApplicationException(String.format("Owner with reference %s not found.", ownerReference));
        }
        Property property = properties.findPropertyByReference(reference);
        if (property == null) {
            property = properties.newProperty(reference, name);
        }
        property.setName(name);
        property.setType(PropertyType.valueOf(type));
        property.setAcquireDate(acquireDate);
        property.setDisposalDate(disposalDate);
        property.setOpeningDate(openingDate);
        property.addActor(owner, PropertyActorType.PROPERTY_OWNER, null, null);
    }


    @ActionSemantics(Of.IDEMPOTENT)
    public void putPropertyPostalAddress(@Named("propertyReference") String propertyReference, @Named("address1") @Optional String address1, @Named("address2") @Optional String address2, @Named("city") String city, @Named("postalCode") @Optional String postalCode,
            @Named("stateCode") @Optional String stateCode, @Named("countryCode") String countryCode)  {
        Property property = properties.findPropertyByReference(propertyReference);
        if (property == null) {
            throw new ApplicationException(String.format("Property with reference %s not found.", propertyReference));
        }
        // TODO: Find if communication channel exists
        CommunicationChannel comm = communicationChannels.newPostalAddress(address1, address2, postalCode, city, states.findByReference(stateCode), countries.findByReference(countryCode));
        property.addCommunicationChannel(comm);

    }

    @ActionSemantics(Of.IDEMPOTENT)
    public void putPartyCommunicationChannels(@Named("partyReference") String partyReference, @Named("address1") @Optional String address1, @Named("address2") @Optional String address2, @Named("city") @Optional String city, @Named("postalCode") @Optional String postalCode,
            @Named("stateCode") @Optional String stateCode, @Named("countryCode") @Optional String countryCode, @Named("phoneNumber") @Optional String phoneNumber, @Named("faxNumber") @Optional String faxNumber)  {
        Party party = parties.findPartyByReference(partyReference);
        if (party == null) {
            throw new ApplicationException(String.format("Property with reference %s not found.", partyReference));
        }
        // TODO: Find if communication channel exists
        if (address1 != null) {
            CommunicationChannel comm = communicationChannels.newPostalAddress(address1, address2, postalCode, city, states.findByReference(stateCode), countries.findByReference(countryCode));
            party.addCommunicationChannel(comm);
        }
        if (phoneNumber != null) {
            CommunicationChannel comm = communicationChannels.newPhoneNumber(phoneNumber);
            party.addCommunicationChannel(comm);
        }
        if (faxNumber != null) {
            CommunicationChannel comm = communicationChannels.newFaxNumber(faxNumber);
            party.addCommunicationChannel(comm);
        }
    }
 
    @ActionSemantics(Of.IDEMPOTENT)
    public void putPropertyOwner(@Named("Reference") String reference, @Named("Reference") String ownerReference) {
        // TODO Auto-generated method stub
    }

    @ActionSemantics(Of.IDEMPOTENT)
    public void putPropertyActor(@Named("propertyReference") String propertyReference, @Named("partyReference") String partyReference, @Named("type") String type, @Named("startDate") @Optional LocalDate startDate, @Named("endDate") @Optional LocalDate endDate)  {
        Property property = properties.findPropertyByReference(propertyReference);
        Party party = parties.findPartyByReference(partyReference);
        if (party == null) {
            throw new ApplicationException(String.format("Party with reference %s not found.", partyReference));
        }
        if (property == null) {
            throw new ApplicationException(String.format("Property with reference %s not found.", propertyReference));
        }
        PropertyActor actor = propertyActors.findPropertyActor(property, party, PropertyActorType.valueOf(type), startDate, endDate);
        if (actor == null) {
            actor = propertyActors.newPropertyActor(property, party, PropertyActorType.valueOf(type), startDate, endDate);
        }

    }

    @ActionSemantics(Of.IDEMPOTENT)
    public void putUnit(@Named("reference") String reference, @Named("propertyReference") String propertyReference, @Named("ownerReference") String ownerReference, @Named("name") String name, @Named("type") String type, @Named("startDate") @Optional LocalDate startDate,
            @Named("endDate") @Optional LocalDate endDate, @Named("area") @Optional BigDecimal area, @Named("salesArea") @Optional BigDecimal salesArea, @Named("storageArea") @Optional BigDecimal storageArea, @Named("mezzanineArea") @Optional BigDecimal mezzanineArea,
            @Named("terraceArea") @Optional BigDecimal terraceArea, @Named("address1") @Optional String address1, @Named("city") @Optional String city, @Named("postalCode") @Optional String postalCode, @Named("stateCode") @Optional String stateCode, @Named("countryCode") @Optional String countryCode) 
    {
        Property property = properties.findPropertyByReference(propertyReference);
        if (property == null) {
            throw new ApplicationException(String.format("Property with reference %s not found.", ownerReference));
        }
        Unit unit = units.findByReference(reference);
        if (unit == null) {
            unit = property.newUnit(reference, name);
        }
        // set attributes
        unit.setName(name);
        unit.setType(UnitType.valueOf(type));
        unit.setArea(area);
        unit.setSalesArea(salesArea);
        unit.setStorageArea(storageArea);
        unit.setMezzanineArea(mezzanineArea);
        unit.setTerraceArea(terraceArea);

        // CommunicationChannel
        CommunicationChannel cc = communicationChannels.newPostalAddress(address1, null, postalCode, city, states.findByReference(stateCode), countries.findByReference(countryCode));
        unit.addCommunicationChannel(cc);
    }
    
    @ActionSemantics(Of.IDEMPOTENT)
    public void putLease(@Named("reference") String reference, @Named("name") String name, @Named("tenantReference") String tenantReference, @Named("landlordReference") String landlordReference, @Named("type") String type, @Named("startDate") LocalDate startDate, @Named("endDate") @Optional LocalDate endDate, @Named("terminationDate") @Optional LocalDate terminationDate, @Named("parentLeaseReference") @Optional String parentLeaseReference, @Named("propertyReference") @Optional String propertyReference) {
        Party tenant = parties.findPartyByReference(tenantReference);
        if (tenant == null) {
            throw new ApplicationException(String.format("Tenant with reference %s not found.", tenantReference));
        }
        Party landlord = parties.findPartyByReference(landlordReference);
        if (landlord == null) {
            throw new ApplicationException(String.format("Landlord with reference %s not found.", landlordReference));
        }
        Lease parentLease = leases.findByReference(parentLeaseReference);
        if (parentLease == null) {
            // throw new
            // ApplicationException(String.format("Landlord with reference %s not found.",
            // landlordReference));
        }
        Lease lease = leases.findByReference(reference);
        if (lease == null) {
            lease = leases.newLease(reference, name);
        }
        if (name != null) {
            lease.setName(name);
        }
        lease.setStartDate(startDate);
        lease.setEndDate(endDate);
        lease.setTerminationDate(terminationDate);
        lease.addActor(landlord, LeaseActorType.LANDLORD, null, null);
        lease.addActor(tenant, LeaseActorType.TENANT, null, null);
    }


    private Countries countries;

    public void setCountryRepository(final Countries countries) {
        this.countries = countries;
    }

    private States states;

    public void setStateRepository(final States states) {
        this.states = states;
    }

    private Units units;

    public void setUnitRepository(final Units units) {
        this.units = units;
    }

    private Properties properties;

    public void setPropertyRepository(final Properties properties) {
        this.properties = properties;
    }

    private Parties parties;

    public void setPartyRepository(final Parties parties) {
        this.parties = parties;
    }

    private PropertyActors propertyActors;

    public void setPropertyActorRepository(final PropertyActors propertyActors) {
        this.propertyActors = propertyActors;
    }

    private CommunicationChannels communicationChannels;

    public void setCommunicationChannelRepository(final CommunicationChannels communicationChannels) {
        this.communicationChannels = communicationChannels;
    }

    private Leases leases;

    public void setLeaseRepository(final Leases leases) {
        this.leases = leases;
    }

}
 
