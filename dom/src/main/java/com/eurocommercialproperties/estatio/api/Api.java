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
import java.math.BigInteger;

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
import com.eurocommercialproperties.estatio.dom.communicationchannel.CommunicationChannelType;
import com.eurocommercialproperties.estatio.dom.communicationchannel.CommunicationChannels;
import com.eurocommercialproperties.estatio.dom.geography.Countries;
import com.eurocommercialproperties.estatio.dom.geography.Country;
import com.eurocommercialproperties.estatio.dom.geography.State;
import com.eurocommercialproperties.estatio.dom.geography.States;
import com.eurocommercialproperties.estatio.dom.invoice.Charge;
import com.eurocommercialproperties.estatio.dom.invoice.Charges;
import com.eurocommercialproperties.estatio.dom.lease.LeaseTermForIndexableRent;
import com.eurocommercialproperties.estatio.dom.lease.InvoicingFrequency;
import com.eurocommercialproperties.estatio.dom.lease.Lease;
import com.eurocommercialproperties.estatio.dom.lease.LeaseActorType;
import com.eurocommercialproperties.estatio.dom.lease.LeaseItem;
import com.eurocommercialproperties.estatio.dom.lease.LeaseItemType;
import com.eurocommercialproperties.estatio.dom.lease.LeaseItems;
import com.eurocommercialproperties.estatio.dom.lease.LeaseTermStatus;
import com.eurocommercialproperties.estatio.dom.lease.LeaseUnit;
import com.eurocommercialproperties.estatio.dom.lease.LeaseUnits;
import com.eurocommercialproperties.estatio.dom.lease.Leases;
import com.eurocommercialproperties.estatio.dom.lease.PaymentMethodType;
import com.eurocommercialproperties.estatio.dom.party.Organisation;
import com.eurocommercialproperties.estatio.dom.party.Parties;
import com.eurocommercialproperties.estatio.dom.party.Party;
import com.eurocommercialproperties.estatio.dom.party.Person;
import com.eurocommercialproperties.estatio.dom.tax.Tax;
import com.eurocommercialproperties.estatio.dom.tax.Taxes;

import org.joda.time.LocalDate;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;

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
    public void putCharge(@Named("code") String code, @Named("reference") String reference, @Named("description") String description, @Named("taxReference") String taxReference) {
        Tax tax = taxes.findTaxByReference(taxReference);
        Charge charge = charges.findChargeByReference(reference);
        if (charge == null) {
            charge = charges.newCharge(reference);
        }
        charge.setDescription(description);
        charge.setCode(code);
        charge.setTax(tax);
    }

    @ActionSemantics(Of.IDEMPOTENT)
    public void putTax(@Named("reference") String reference, @Named("name") String name) {
        Tax tax = taxes.findTaxByReference(reference);
        if (tax == null) {
            tax = taxes.newTax(reference);
        }
        tax.setName(name);
    }

    @ActionSemantics(Of.IDEMPOTENT)
    public void putTaxRate(@Named("code") String code, @Named("alpha2Code") String alpha2Code, @Named("name") String name) {
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
        Organisation org = (Organisation) parties.findPartyByReference(reference);
        if (org == null) {
            org = parties.newOrganisation(name);
            org.setReference(reference);
        }
        org.setName(name);
    }

    @ActionSemantics(Of.IDEMPOTENT)
    public void putProperty(
            @Named("reference") String reference, 
            @Named("name") String name, 
            @Named("type") String type, 
            @Named("acquireDate") @Optional LocalDate acquireDate, 
            @Named("disposalDate") @Optional LocalDate disposalDate, 
            @Named("openingDate") @Optional LocalDate openingDate,
            @Named("ownerReference") @Optional String ownerReference) {
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
    public void putPropertyPostalAddress(
            @Named("propertyReference") String propertyReference, 
            @Named("address1") @Optional String address1, 
            @Named("address2") @Optional String address2, 
            @Named("city") String city, 
            @Named("postalCode") @Optional String postalCode,
            @Named("stateCode") @Optional String stateCode, 
            @Named("countryCode") String countryCode) {
        Property property = properties.findPropertyByReference(propertyReference);
        if (property == null) {
            throw new ApplicationException(String.format("Property with reference %s not found.", propertyReference));
        }
        CommunicationChannel comm = property.findCommunicationChannelForType(null);
        if (comm == null) {
            comm = communicationChannels.newPostalAddress(address1, address2, postalCode, city, states.findByReference(stateCode), countries.findByReference(countryCode));
        }
        property.getCommunicationChannels().add(comm);
    }

    @ActionSemantics(Of.IDEMPOTENT)
    public void putPartyCommunicationChannels(@Named("partyReference") String partyReference, @Named("address1") @Optional String address1, @Named("address2") @Optional String address2, @Named("city") @Optional String city, @Named("postalCode") @Optional String postalCode,
            @Named("stateCode") @Optional String stateCode, @Named("countryCode") @Optional String countryCode, @Named("phoneNumber") @Optional String phoneNumber, @Named("faxNumber") @Optional String faxNumber) {
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
    public void putPropertyActor(@Named("propertyReference") String propertyReference, @Named("partyReference") String partyReference, @Named("type") String type, @Named("startDate") @Optional LocalDate startDate, @Named("endDate") @Optional LocalDate endDate) {
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
    public void putUnit(
            @Named("reference") String reference, 
            @Named("propertyReference") String propertyReference, 
            @Named("ownerReference") @Optional String ownerReference, 
            @Named("name") String name, @Named("type") String type, 
            @Named("startDate") @Optional LocalDate startDate,
            @Named("endDate") @Optional LocalDate endDate, 
            @Named("area") @Optional BigDecimal area, 
            @Named("salesArea") @Optional BigDecimal salesArea, 
            @Named("storageArea") @Optional BigDecimal storageArea, 
            @Named("mezzanineArea") @Optional BigDecimal mezzanineArea,
            @Named("terraceArea") @Optional BigDecimal terraceArea, 
            @Named("address1") @Optional String address1, 
            @Named("city") @Optional String city, 
            @Named("postalCode") @Optional String postalCode, 
            @Named("stateCode") @Optional String stateCode, 
            @Named("countryCode") @Optional String countryCode) {
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
        CommunicationChannel cc = unit.findCommunicationChannelForType(CommunicationChannelType.POSTAL_ADDRESS);
        if (cc==null){
            cc = communicationChannels.newPostalAddress(address1, null, postalCode, city, states.findByReference(stateCode), countries.findByReference(countryCode));
            unit.addCommunicationChannel(cc);
        }
    }

    @ActionSemantics(Of.IDEMPOTENT)
    public void putLease(@Named("reference") String reference, @Named("name") String name, @Named("tenantReference") String tenantReference, @Named("landlordReference") String landlordReference, @Named("type") @Optional String type, @Named("startDate") @Optional LocalDate startDate,
            @Named("endDate") @Optional LocalDate endDate, @Named("terminationDate") @Optional LocalDate terminationDate, @Named("parentLeaseReference") @Optional String parentLeaseReference, @Named("propertyReference") @Optional String propertyReference) {
        Party tenant = parties.findPartyByReference(tenantReference);
        if (tenant == null) {
            throw new ApplicationException(String.format("Tenant with reference %s not found.", tenantReference));
        }
        Party landlord = parties.findPartyByReference(landlordReference);
        if (landlord == null) {
            throw new ApplicationException(String.format("Landlord with reference %s not found.", landlordReference));
        }
        Lease parentLease;
        if (parentLeaseReference != null){
            parentLease = leases.findByReference(parentLeaseReference);
            if (parentLease == null) {
                 throw new ApplicationException(String.format("Landlord with reference %s not found.", landlordReference));
            }
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

    @ActionSemantics(Of.IDEMPOTENT)
    public void putLeaseUnit(@Named("leaseReference") String leaseReference, @Named("unitReference") @Optional String unitReference, @Named("startDate") @Optional LocalDate startDate, @Named("endDate") @Optional LocalDate endDate, @Named("tenancyStartDate") @Optional LocalDate tenancyStartDate,
            @Named("tenancyEndDate") @Optional LocalDate tenancyEndDate, @Named("brand") @Optional String brand, @Named("sector") @Optional String sector, @Named("activity") @Optional String activity) {
        Lease lease = leases.findByReference(leaseReference);
        if (lease == null) {
            throw new ApplicationException(String.format("Lease with reference %s not found.", leaseReference));
        }
        Unit unit = units.findByReference(unitReference);
        if (unitReference != null && unit == null) {
            throw new ApplicationException(String.format("Unit with reference %s not found.", unitReference));
        }
        LeaseUnit leaseUnit = leaseUnits.find(lease, unit, startDate);
        if (leaseUnit == null) {
            leaseUnit = lease.addUnit(unit);
            leaseUnit.setStartDate(startDate);
        }
        leaseUnit.setStartDate(startDate);
        leaseUnit.setEndDate(endDate);
        leaseUnit.setTenancyStartDate(tenancyStartDate);
        leaseUnit.setTenancyEndDate(tenancyEndDate);
        leaseUnit.setBrand(brand);
        leaseUnit.setSector(sector);
        leaseUnit.setActivity(activity);
    }

    @ActionSemantics(Of.IDEMPOTENT)
    public void putLeaseItem(@Named("leaseReference") String leaseReference, 
            @Named("tenantReference") String tenantReference, 
            @Named("unitReference") @Optional String unitReference, 
            @Named("type") @Optional String type, 
            @Named("sequence") BigInteger sequence,
            @Named("startDate") @Optional LocalDate startDate, 
            @Named("endDate") @Optional LocalDate endDate, 
            @Named("tenancyStartDate") @Optional LocalDate tenancyStartDate, 
            @Named("tenancyEndDate") @Optional LocalDate tenancyEndDate, 
            @Named("chargeReference") @Optional String chargeReference,
            @Named("nextDueDate") @Optional LocalDate nextDueDate, 
            @Named("invoicingFrequency") @Optional String invoicingFrequency, 
            @Named("paymentMethod") @Optional String paymentMethod) {
        Lease lease = leases.findByReference(leaseReference);
        if (lease == null) {
            throw new ApplicationException(String.format("Lease with reference %s not found.", leaseReference));
        }
        Unit unit;
        if (unitReference != null) {
            unit = units.findByReference(unitReference);
            if (unit == null) {
                throw new ApplicationException(String.format("Unit with reference %s not found.", unitReference));
            }
        }
        LeaseItemType itemType = LeaseItemType.valueOf(type);
        if (itemType == null) {
            throw new ApplicationException(String.format("Type with reference %s not found.", type));
        }
        LeaseItem item = lease.findItem(itemType, startDate, sequence);
        if (item == null) {
            item = lease.addItem();
        }
        Charge charge = charges.findChargeByReference(chargeReference);
        if (charge == null) {
            throw new ApplicationException(String.format("Type with reference %s not found.", type));
        }
        item.setStartDate(startDate);
        item.setEndDate(endDate);
        item.setTenancyStartDate(tenancyStartDate);
        item.setTenancyEndDate(tenancyEndDate);
        item.setType(itemType);
        item.setSequence(sequence);
        item.setInvoicingFrequency(InvoicingFrequency.valueOf(invoicingFrequency));
        item.setPaymentMethod(PaymentMethodType.valueOf(paymentMethod));
        item.setCharge(charge);
    }

    @ActionSemantics(Of.IDEMPOTENT)
    public void putLeaseTerm(@Named("leaseReference") String leaseReference, 
                             @Named("tenantReference") String tenantReference, 
                             @Named("unitReference") @Optional String unitReference, 
                             @Named("itemSequence") BigInteger itemSequence, 
                             @Named("itemType") String itemType,
                             @Named("itemStartDate") LocalDate itemStartDate, 
                             @Named("startDate") @Optional LocalDate startDate, 
                             @Named("endDate") @Optional LocalDate endDate, 
                             @Named("reviewDate") @Optional LocalDate reviewDate, 
                             @Named("effectiveDate") @Optional LocalDate effectiveDate,
                             @Named("value") @Optional BigDecimal value, 
                             @Named("baseValue") @Optional BigDecimal baseValue, 
                             @Named("indexationValue") @Optional BigDecimal indexationValue, 
                             @Named("levellingValue") @Optional BigDecimal levellingValue,
                             @Named("levellingPercentage") @Optional BigDecimal levellingPercentage, 
                             @Named("indexationPercentage") @Optional BigDecimal indexationPercentage, 
                             @Named("baseIndexReference") @Optional String baseIndexReference, 
                             @Named("baseIndexStartDate") @Optional LocalDate baseIndexStartDate,
                             @Named("baseIndexEndDate") @Optional LocalDate baseIndexEndDate, 
                             @Named("baseIndexValue") @Optional BigDecimal baseIndexValue, 
                             @Named("nextIndexReference") @Optional String nextIndexReference, 
                             @Named("nextIndexStartDate") @Optional LocalDate nextIndexStartDate,
                             @Named("nextIndexEndDate") @Optional LocalDate nextIndexEndDate, 
                             @Named("nextIndexValue") @Optional BigDecimal nextIndexValue, 
                             @Named("status") @Optional String status) {
        Lease lease = leases.findByReference(leaseReference);
        if (lease == null) {
            throw new ApplicationException(String.format("Leaseitem with reference %1$s not found.", leaseReference));
        }
        Unit unit;
        if (unitReference != null) {
            unit = units.findByReference(unitReference);
            if (unitReference != null && unit == null) {
                throw new ApplicationException(String.format("Unit with reference %s not found.", unitReference));
            }
        }
        LeaseItemType leaseItemType = LeaseItemType.valueOf(itemType);
        if (itemType == null) {
            throw new ApplicationException(String.format("Type with reference %s not found.", itemType));
        }
        LeaseItem item = lease.findItem(leaseItemType, itemStartDate, itemSequence);
        if (item == null) {
            throw new ApplicationException(String.format("LeaseItem with reference %1$s, %2$s, %3$s, %4$s not found.", leaseReference, leaseItemType.toString(), itemStartDate.toString(), itemSequence.toString()));
        }
        LeaseTermForIndexableRent term = (LeaseTermForIndexableRent) item.findTerm(startDate);
        if (term == null) {
            term = (LeaseTermForIndexableRent) item.addIndexableTerm();
        }

        term.setStartDate(startDate);
        term.setEndDate(endDate);
        term.setReviewDate(reviewDate);
        term.setEffectiveDate(effectiveDate);

        term.setValue(value);
        term.setBaseValue(baseValue);

        term.setBaseIndexStartDate(baseIndexStartDate);
        term.setBaseIndexEndDate(baseIndexEndDate);
        term.setBaseIndexValue(baseIndexValue);

        term.setNextIndexStartDate(nextIndexStartDate);
        term.setNextIndexEndDate(nextIndexEndDate);
        term.setNextIndexValue(nextIndexValue);

        term.setIndexationPercentage(indexationPercentage);
        term.setLevellingValue(levellingValue);
        term.setLevellingPercentage(levellingPercentage);

        term.setStatus(LeaseTermStatus.valueOf(status));
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

    private LeaseUnits leaseUnits;

    public void setLeaseUnitsRepository(final LeaseUnits leaseUnits) {
        this.leaseUnits = leaseUnits;
    }

    private LeaseItems leaseItems;

    public void setLeaseItemsRepository(final LeaseItems leaseItems) {
        this.leaseItems = leaseItems;
    }

    private Taxes taxes;

    public void setTaxesRepsitory(final Taxes taxes) {
        this.taxes = taxes;
    }

    private Charges charges;

    public void setChargesRepo(final Charges charges) {
        this.charges = charges;
    }

}
