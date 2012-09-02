package com.eurocommercialproperties.estatio.objstore.dflt.api;

import java.math.BigDecimal;
import org.joda.time.LocalDate;

import com.eurocommercialproperties.estatio.api.Api;
import com.eurocommercialproperties.estatio.dom.asset.Properties;
import com.eurocommercialproperties.estatio.dom.asset.Property;
import com.eurocommercialproperties.estatio.dom.asset.PropertyActor;
import com.eurocommercialproperties.estatio.dom.asset.PropertyActorType;
import com.eurocommercialproperties.estatio.dom.asset.PropertyActors;
import com.eurocommercialproperties.estatio.dom.asset.PropertyType;
import com.eurocommercialproperties.estatio.dom.asset.Unit;
import com.eurocommercialproperties.estatio.dom.asset.Units;
import com.eurocommercialproperties.estatio.dom.asset.UnitType;
import com.eurocommercialproperties.estatio.dom.communicationchannel.CommunicationChannel;
import com.eurocommercialproperties.estatio.dom.communicationchannel.CommunicationChannels;
import com.eurocommercialproperties.estatio.dom.geography.Countries;
import com.eurocommercialproperties.estatio.dom.geography.Country;
import com.eurocommercialproperties.estatio.dom.geography.State;
import com.eurocommercialproperties.estatio.dom.geography.States;
import com.eurocommercialproperties.estatio.dom.party.Organisation;
import com.eurocommercialproperties.estatio.dom.party.Parties;
import com.eurocommercialproperties.estatio.dom.party.Party;
import com.eurocommercialproperties.estatio.dom.party.Person;
import com.eurocommercialproperties.estatio.dom.lease.Lease;
import com.eurocommercialproperties.estatio.dom.lease.Leases;


import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.ApplicationException;

public class ApiDefault extends AbstractFactoryAndRepository implements Api {

    // {{ Id, iconName
    @Override
    public String getId() {
        return "api";
    }

    public String iconName() {
        return "Api";
    }

    // }}

    @Override
    public void putCountry(String code, String alpha2Code, String name) {
        Country country = countries.findByReference(code);
        if (country == null) {
            country = countries.newCountry(code, name);
        }
        country.setName(name);
        country.setAlpha2Code(alpha2Code);
    }

    @Override
    public void putState(String code, String name, String countryCode) {
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

    @Override
    public void putOrganisation(String reference, String name) {
        Organisation org = parties.findOrganisationByReference(reference);
        if (org == null) {
            org = parties.newOrganisation(name);
            org.setReference(reference);
        }
        org.setName(name);
    }

    @Override
    public void putPerson(String reference, String initials, String firstName, String lastName) {
        // TODO Add check for return type
        Person person = (Person) parties.findPartyByReference(reference);
        if (person == null) {
            person = parties.newPerson(initials, firstName, lastName);
            person.setReference(reference);
        }
        person.setFirstName(firstName);
        person.setLastName(lastName);
    }

    @Override
    public void putPropertyPostalAddress(String propertyReference, String address1, String address2, String city, String postalCode, String stateCode, String countryCode) {
        Property property = properties.findByReference(propertyReference);
        if (property == null) {
            throw new ApplicationException(String.format("Property with reference %s not found.", propertyReference));
        }
        //TODO: Find if communication channel exists
        CommunicationChannel comm = communicationChannels.newPostalAddress(address1, address2, postalCode, city, states.findByReference(stateCode), countries.findByReference(countryCode));
        property.addCommunicationChannel(comm);
        
    }

    @Override
    public void putPropertyOwner(String reference, String ownerReference) {
        // TODO Auto-generated method stub
    }
    
    
    @Override
    public void putPropertyActor(String propertyReference, String partyReference, String type, LocalDate from, LocalDate thru){
        Property property = properties.findByReference(propertyReference);
        Party party = parties.findPartyByReference(partyReference);
        if (party == null) {
            throw new ApplicationException(String.format("Party with reference %s not found.", partyReference));
        }
        if (property == null) {
            throw new ApplicationException(String.format("Property with reference %s not found.", propertyReference));
        }
        PropertyActor actor = propertyActors.findPropertyActor(property, party, PropertyActorType.valueOf(type), from, thru);
        if (actor == null) {
            actor = propertyActors.newPropertyActor(property, party, PropertyActorType.valueOf(type), from, thru);
        }

    }
    

    @Override
    public void putProperty(String reference, String name, String type, LocalDate acquireDate, LocalDate disposalDate, LocalDate openingDate, String ownerReference) {
        Party owner = parties.findOrganisationByReference(ownerReference);
        if (owner == null) {
            throw new ApplicationException(String.format("Owner with reference %s not found.", ownerReference));
        }
        Property property = properties.findByReference(reference);
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
    
    @Override
    public void putUnit(String reference, String propertyReference, String ownerReference, String name, String type, LocalDate from, LocalDate thru,   
            BigDecimal area, BigDecimal salesArea, BigDecimal storageArea, BigDecimal mezzanineArea, BigDecimal terraceArea, 
            String address1, String city, String postalCode, String stateCode, String countryCode)
 {
//        Party owner = parties.findOrganisationByReference(ownerReference);
//        if (owner == null) {
//            throw new ApplicationException(String.format("Owner with reference %s not found.", ownerReference));
//        }
        Property property = properties.findByReference(propertyReference);
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
        //TODO: set communicationchannel
    }
    
    public void putLease( 
        String leaseReference,
        String propertyReference, 
        String unitReference, 
        String leaseName, 
        String tenantReference, 
        String leaseType, 
        LocalDate startDate, 
        LocalDate endDate, 
        LocalDate terminationDate, 
        String brand, 
        String sector, 
        String activity, 
        String reportTurnover, 
        String reportRent, 
        String reportOCR, 
        BigDecimal indexationLevelingPercentage, 
        BigDecimal turnoverRentPercentage, 
        String paymentMethod, 
        String parentLeaseCode){
        Party tenant = parties.findOrganisationByReference(tenantReference);
        if (tenant == null) {
            throw new ApplicationException(String.format("Tenant with reference %s not found.", tenantReference));
        }
        Lease lease = leases.findByReference(leaseReference);
        if (lease == null){
            leases.newLease(leaseReference, leaseName);
        }
        
        
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
    
    public void setPropertyActorRepository(final PropertyActors propertyActors){
        this.propertyActors = propertyActors;
    }

    private CommunicationChannels communicationChannels;
    
    public void setCommunicationChannelRepository(final CommunicationChannels communicationChannels){
        this.communicationChannels = communicationChannels;
    }
    
    private Leases leases;
    
    public void setLeaseRepository(final Leases leases){
        this.leases = leases;
    }
}
