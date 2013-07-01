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
package org.estatio.api;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.estatio.dom.agreement.AgreementRole;
import org.estatio.dom.agreement.AgreementRoleCommunicationChannels;
import org.estatio.dom.agreement.AgreementRoleTypes;
import org.estatio.dom.agreement.AgreementRoleCommunicationChannelType;
import org.estatio.dom.asset.FixedAssetRole;
import org.estatio.dom.asset.FixedAssetRoleType;
import org.estatio.dom.asset.FixedAssetRoles;
import org.estatio.dom.asset.Properties;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyType;
import org.estatio.dom.asset.Unit;
import org.estatio.dom.asset.UnitType;
import org.estatio.dom.asset.Units;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.Charges;
import org.estatio.dom.communicationchannel.CommunicationChannel;
import org.estatio.dom.communicationchannel.CommunicationChannelType;
import org.estatio.dom.communicationchannel.CommunicationChannels;
import org.estatio.dom.communicationchannel.PostalAddress;
import org.estatio.dom.communicationchannel.PostalAddresses;
import org.estatio.dom.financial.BankAccount;
import org.estatio.dom.financial.BankAccountType;
import org.estatio.dom.financial.FinancialAccounts;
import org.estatio.dom.geography.Countries;
import org.estatio.dom.geography.Country;
import org.estatio.dom.geography.State;
import org.estatio.dom.geography.States;
import org.estatio.dom.index.Index;
import org.estatio.dom.index.Indices;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.lease.InvoicingFrequency;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseConstants;
import org.estatio.dom.lease.LeaseItem;
import org.estatio.dom.lease.LeaseItemType;
import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.lease.LeaseTermForIndexableRent;
import org.estatio.dom.lease.LeaseTermForServiceCharge;
import org.estatio.dom.lease.LeaseTermForTurnoverRent;
import org.estatio.dom.lease.LeaseTermFrequency;
import org.estatio.dom.lease.LeaseUnit;
import org.estatio.dom.lease.LeaseUnits;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.lease.UnitForLease;
import org.estatio.dom.party.Organisation;
import org.estatio.dom.party.Organisations;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.Person;
import org.estatio.dom.party.Persons;
import org.estatio.dom.tax.Tax;
import org.estatio.dom.tax.Taxes;
import org.estatio.services.clock.ClockService;
import org.joda.time.LocalDate;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;

//@Hidden
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
        Country country = countries.findCountryByReference(code);
        if (country == null) {
            country = countries.newCountry(code, name);
        }
        country.setName(name);
        country.setAlpha2Code(alpha2Code);
    }

    private Country fetchCountry(String countryCode) {
        return fetchCountry(countryCode, true);
    }

    private Country fetchCountry(String countryCode, boolean exception) {
        Country country = countries.findCountryByReference(countryCode);
        if (country == null && exception) {
            throw new ApplicationException(String.format("Country with code %1$s not found", countryCode));
        }
        return country;
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.IDEMPOTENT)
    public void putState(@Named("code") String code, @Named("name") String name, @Named("countryCode") String countryCode) {
        Country country = fetchCountry(countryCode);
        State state = states.findStateByReference(countryCode);
        if (state == null) {
            state = states.newState(code, name, country);
        }
        state.setName(name);
        state.setCountry(country);
    }

    private State fetchState(String stateCode) {
        return fetchState(stateCode, true);
    }

    private State fetchState(String stateCode, boolean exception) {
        State country = states.findStateByReference(stateCode);
        if (country == null && exception) {
            throw new ApplicationException(String.format("State with code %1$s not found", stateCode));
        }
        return country;
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.IDEMPOTENT)
    public void putCharge(@Named("code") String code, @Named("reference") String reference, @Named("description") String description, @Named("taxReference") String taxReference) {
        Tax tax = fetchTax(taxReference);
        Charge charge = charges.findChargeByReference(reference);
        if (charge == null) {
            charge = charges.newCharge(reference);
        }
        charge.setDescription(description);
        charge.setCode(code);
        charge.setTax(tax);
    }

    private Charge fetchCharge(String type, String chargeReference) {
        Charge charge = charges.findChargeByReference(chargeReference);
        if (charge == null) {
            throw new ApplicationException(String.format("Type with reference %s not found.", type));
        }
        return charge;
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.IDEMPOTENT)
    public void putTax(@Named("reference") String reference, @Named("name") String name, @Named("description") String decription, @Named("percentage") BigDecimal percentage, @Named("startDate") LocalDate startDate) {
        Tax tax = fetchTax(reference);
        if (tax == null) {
            tax = taxes.newTax(reference);
            tax.setName(name);
        }
        tax.newRate(startDate, percentage);
    }

    private Tax fetchTax(String reference) {
        return fetchTax(reference, false);
    }

    private Tax fetchTax(String reference, boolean exception) {
        Tax tax = taxes.findTaxByReference(reference);
        if (tax == null && exception)
            throw new ApplicationException(String.format("Tax with reference %1$s not found", reference));
        return tax;
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.IDEMPOTENT)
    public void putPerson(@Named("reference") String reference, @Named("initials") @Optional String initials, @Named("firstName") String firstName, @Named("lastName") String lastName) {
        // TODO Add check for return type
        Person person = (Person) parties.findPartyByReferenceOrName(reference);
        if (person == null) {
            person = persons.newPerson(initials, firstName, lastName);
            person.setReference(reference);
        }
        person.setFirstName(firstName);
        person.setLastName(lastName);
    }

    @ActionSemantics(Of.IDEMPOTENT)
    public void putOrganisation(@Named("reference") String reference, @Named("name") String name) {
        Organisation org = (Organisation) parties.findPartyByReferenceOrName(reference);
        if (org == null) {
            org = organisations.newOrganisation(reference, name);
            org.setReference(reference);
        }
        org.setName(name);
    }

    private Party fetchParty(String partyReference) {
        Party party = parties.findPartyByReferenceOrName(partyReference);
        if (party == null) {
            throw new ApplicationException(String.format("Party with reference %s not found.", partyReference));
        }
        return party;
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.IDEMPOTENT)
    public void putProperty(@Named("reference") String reference, @Named("name") String name, @Named("type") String type, @Named("acquireDate") @Optional LocalDate acquireDate, @Named("disposalDate") @Optional LocalDate disposalDate, @Named("openingDate") @Optional LocalDate openingDate,
            @Named("ownerReference") @Optional String ownerReference) {
        Party owner = fetchParty(ownerReference);
        Property property = fetchProperty(reference, true);
        property.setName(name);
        property.setPropertyType(PropertyType.valueOf(type));
        property.setAcquireDate(acquireDate);
        property.setDisposalDate(disposalDate);
        property.setOpeningDate(openingDate);
        property.addRole(owner, FixedAssetRoleType.PROPERTY_OWNER, null, null);
    }

    private Property fetchProperty(String reference, boolean createIfNotFond) {
        Property property = properties.findPropertyByReference(reference);
        if (property == null) {
            if (!createIfNotFond)
                throw new ApplicationException(String.format("Property with reference %s not found.", reference));
            property = properties.newProperty(reference, null);
        }
        return property;
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.IDEMPOTENT)
    public void putUnit(@Named("reference") String reference, @Named("propertyReference") String propertyReference, @Named("ownerReference") @Optional String ownerReference, @Named("name") String name, @Named("type") String type, @Named("startDate") @Optional LocalDate startDate,
            @Named("endDate") @Optional LocalDate endDate, @Named("area") @Optional BigDecimal area, @Named("salesArea") @Optional BigDecimal salesArea, @Named("storageArea") @Optional BigDecimal storageArea, @Named("mezzanineArea") @Optional BigDecimal mezzanineArea,
            @Named("terraceArea") @Optional BigDecimal terraceArea, @Named("address1") @Optional String address1, @Named("city") @Optional String city, @Named("postalCode") @Optional String postalCode, @Named("stateCode") @Optional String stateCode, @Named("countryCode") @Optional String countryCode) {
        Property property = fetchProperty(propertyReference, false);
        Unit unit = units.findUnitByReference(reference);
        if (unit == null) {
            unit = property.newUnit(reference, name);
        }
        // set attributes
        unit.setName(name);
        unit.setUnitType(UnitType.valueOf(type));
        unit.setArea(area);
        unit.setSalesArea(salesArea);
        unit.setStorageArea(storageArea);
        unit.setMezzanineArea(mezzanineArea);
        unit.setTerraceArea(terraceArea);
        CommunicationChannel cc = unit.findCommunicationChannelForType(CommunicationChannelType.POSTAL_ADDRESS);
        if (cc == null) {
            cc = communicationChannels.newPostalAddress(unit, address1, null, postalCode, city, states.findStateByReference(stateCode), countries.findCountryByReference(countryCode));
        }
    }

    private Unit fetchUnit(String unitReference) {
        if (unitReference != null) {
            Unit unit = units.findUnitByReference(unitReference);
            if (unit == null) {
                throw new ApplicationException(String.format("Unit with reference %s not found.", unitReference));
            }
            return unit;
        }
        return null;
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.IDEMPOTENT)
    public void putPropertyPostalAddress(@Named("propertyReference") String propertyReference, @Named("address1") @Optional String address1, @Named("address2") @Optional String address2, @Named("city") String city, @Named("postalCode") @Optional String postalCode,
            @Named("stateCode") @Optional String stateCode, @Named("countryCode") String countryCode) {
        Property property = properties.findPropertyByReference(propertyReference);
        if (property == null) {
            throw new ApplicationException(String.format("Property with reference %s not found.", propertyReference));
        }
        CommunicationChannel comm = property.findCommunicationChannelForType(null);
        if (comm == null) {
            comm = communicationChannels.newPostalAddress(property, address1, address2, postalCode, city, states.findStateByReference(stateCode), countries.findCountryByReference(countryCode));
        }
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.IDEMPOTENT)
    public void putPartyCommunicationChannels(@Named("partyReference") String partyReference, @Named("reference") @Optional String reference, @Named("address1") @Optional String address1, @Named("address2") @Optional String address2, @Named("city") @Optional String city,
            @Named("postalCode") @Optional String postalCode, @Named("stateCode") @Optional String stateCode, @Named("countryCode") @Optional String countryCode, @Named("phoneNumber") @Optional String phoneNumber, @Named("faxNumber") @Optional String faxNumber) {
        Party party = fetchParty(partyReference);
        // Address
        if (address1 != null) {
            CommunicationChannel comm = communicationChannels.findByReferenceAndType(reference, CommunicationChannelType.POSTAL_ADDRESS);
            if (comm == null) {
                comm = communicationChannels.newPostalAddress(party, address1, address2, postalCode, city, states.findStateByReference(stateCode), countries.findCountryByReference(countryCode));
                comm.setReference(reference);
            }
        }
        // Phone
        if (phoneNumber != null) {
            CommunicationChannel comm = communicationChannels.findByReferenceAndType(reference, CommunicationChannelType.PHONE_NUMBER);
            if (comm == null) {
                comm = communicationChannels.newPhoneNumber(party, phoneNumber);
                comm.setReference(reference);
            }
        }
        // Fax
        if (faxNumber != null) {
            CommunicationChannel comm = communicationChannels.findByReferenceAndType(reference, CommunicationChannelType.FAX_NUMBER);
            if (comm == null) {
                comm = communicationChannels.newFaxNumber(party, faxNumber);
                comm.setReference(reference);
            }
        }
    }

    @ActionSemantics(Of.IDEMPOTENT)
    public void putPartyPostalAddress(@Named("partyReference") String partyReference, @Named("reference") @Optional String reference, @Named("address1") @Optional String address1, @Named("address2") @Optional String address2, @Named("city") @Optional String city,
            @Named("postalCode") @Optional String postalCode, @Named("stateCode") @Optional String stateCode, @Named("countryCode") @Optional String countryCode, @Named("phoneNumber") @Optional String phoneNumber, @Named("faxNumber") @Optional String faxNumber) {
        Party party = fetchParty(partyReference);
        // Address
        if (address1 != null) {
            CommunicationChannel comm = communicationChannels.findByReferenceAndType(reference, CommunicationChannelType.POSTAL_ADDRESS);
            if (comm == null) {
                comm = communicationChannels.newPostalAddress(party, address1, address2, postalCode, city, states.findStateByReference(stateCode), countries.findCountryByReference(countryCode));
                comm.setReference(reference);
            }
        }
        // Phone
        if (phoneNumber != null) {
            CommunicationChannel comm = communicationChannels.findByReferenceAndType(reference, CommunicationChannelType.PHONE_NUMBER);
            if (comm == null) {
                comm = communicationChannels.newPhoneNumber(party, phoneNumber);
                comm.setReference(reference);
            }
        }
        // Fax
        if (faxNumber != null) {
            CommunicationChannel comm = communicationChannels.findByReferenceAndType(reference, CommunicationChannelType.FAX_NUMBER);
            if (comm == null) {
                comm = communicationChannels.newFaxNumber(party, faxNumber);
                comm.setReference(reference);
            }
        }
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.IDEMPOTENT)
    public void putPropertyActor(@Named("propertyReference") String propertyReference, @Named("partyReference") String partyReference, @Named("type") String type, @Named("startDate") @Optional LocalDate startDate, @Named("endDate") @Optional LocalDate endDate) {
        Property property = fetchProperty(propertyReference, false);
        Party party = fetchParty(partyReference);
        FixedAssetRole actor = fixedAssetRoles.findRole(property, party, FixedAssetRoleType.valueOf(type), startDate, endDate);
        if (actor == null) {
            fixedAssetRoles.newRole(property, party, FixedAssetRoleType.valueOf(type), startDate, endDate);
        }
    }

    @ActionSemantics(Of.IDEMPOTENT)
    public void putLease(@Named("reference") String reference, @Named("name") String name, @Named("tenantReference") String tenantReference, @Named("landlordReference") String landlordReference, @Named("type") @Optional String type, @Named("startDate") @Optional LocalDate startDate,
            @Named("endDate") @Optional LocalDate endDate, @Named("terminationDate") @Optional LocalDate terminationDate, @Named("propertyReference") @Optional String propertyReference) {
        Party tenant = fetchParty(tenantReference);
        Party landlord = fetchParty(landlordReference);
        Lease lease = leases.findLeaseByReference(reference);
        if (lease == null) {
            lease = leases.newLease(reference, name, startDate, null, endDate, landlord, tenant);
        }
        if (name != null) {
            lease.setName(name);
        }
        lease.setTerminationDate(terminationDate);
    }

    private Lease fetchLease(String leaseReference) {
        Lease lease;
        lease = leases.findLeaseByReference(leaseReference);
        if (lease == null) {
            throw new ApplicationException(String.format("Lease with reference %s not found.", leaseReference));
        }
        return lease;
    }

    @ActionSemantics(Of.IDEMPOTENT)
    public void putLeaseLink(@Named("leaseReference") String leaseReference, @Named("previousLeaseReference") String previousLeaseReference) {
        Lease lease = fetchLease(leaseReference);
        Lease previousLease = fetchLease(previousLeaseReference);
        lease.modifyPrevious(previousLease);
    }

    @ActionSemantics(Of.IDEMPOTENT)
    public void putLeaseUnit(@Named("leaseReference") String leaseReference, @Named("unitReference") @Optional String unitReference, @Named("startDate") @Optional LocalDate startDate, @Named("endDate") @Optional LocalDate endDate, @Named("tenancyStartDate") @Optional LocalDate tenancyStartDate,
            @Named("tenancyEndDate") @Optional LocalDate tenancyEndDate, @Named("brand") @Optional String brand, @Named("sector") @Optional String sector, @Named("activity") @Optional String activity) {
        Lease lease = fetchLease(leaseReference);
        UnitForLease unit = (UnitForLease) units.findUnitByReference(unitReference);
        if (unitReference != null && unit == null) {
            throw new ApplicationException(String.format("Unit with reference %s not found.", unitReference));
        }
        LeaseUnit leaseUnit = leaseUnits.findByLeaseAndUnitAndStartDate(lease, unit, startDate);
        if (leaseUnit == null) {
            leaseUnit = lease.addUnit(unit);
            leaseUnit.setStartDate(startDate);
        }

        leaseUnit.setStartDate(startDate);
        leaseUnit.setEndDate(endDate);
        leaseUnit.setBrand(brand);
        leaseUnit.setSector(sector);
        leaseUnit.setActivity(activity);
    }

    @ActionSemantics(Of.IDEMPOTENT)
    public void putLeaseItem(@Named("leaseReference") String leaseReference, @Named("tenantReference") String tenantReference, @Named("unitReference") @Optional String unitReference, @Named("type") @Optional String type, @Named("sequence") BigInteger sequence,
            @Named("startDate") @Optional LocalDate startDate, @Named("endDate") @Optional LocalDate endDate, @Named("chargeReference") @Optional String chargeReference, @Named("nextDueDate") @Optional LocalDate nextDueDate, @Named("invoicingFrequency") @Optional String invoicingFrequency,
            @Named("paymentMethod") @Optional String paymentMethod) {
        Lease lease = fetchLease(leaseReference);
        Unit unit = fetchUnit(unitReference);
        LeaseItemType itemType = fetchLeaseItemType(type);
        Charge charge = fetchCharge(type, chargeReference);
        //
        LeaseItem item = lease.findItem(itemType, startDate, sequence);
        if (item == null) {
            item = lease.newItem(itemType);
        }
        item.setStartDate(startDate);
        item.setEndDate(endDate);
        item.setType(itemType);
        item.setSequence(sequence);
        item.setInvoicingFrequency(InvoicingFrequency.valueOf(invoicingFrequency));
        item.setPaymentMethod(PaymentMethod.valueOf(paymentMethod));
        item.setCharge(charge);
    }

    private LeaseItemType fetchLeaseItemType(String type) {
        LeaseItemType itemType = LeaseItemType.valueOf(type);
        if (itemType == null) {
            throw new ApplicationException(String.format("Type with reference %s not found.", type));
        }
        return itemType;
    }

    @ActionSemantics(Of.IDEMPOTENT)
    public void putLeasePostalAddress(@Named("partyReference") String partyReference, @Named("leaseReference") @Optional String leaseReference, @Named("address1") @Optional String address1, @Named("address2") @Optional String address2, @Named("postalCode") @Optional String postalCode,
            @Named("city") @Optional String city, @Named("stateCode") @Optional String stateCode, @Named("countryCode") @Optional String countryCode, @Named("type") @Optional AgreementRoleCommunicationChannelType type) {
        if (address1 != null && partyReference != null && leaseReference != null) {
            Lease lease = fetchLease(leaseReference);
            Party party = fetchParty(partyReference);
            PostalAddress address = (PostalAddress) postalAddresses.findByAddress(address1, postalCode, city, fetchCountry(countryCode));
            if (address == null) {
                address = communicationChannels.newPostalAddress(party, address1, address2, postalCode, city, fetchState(stateCode), fetchCountry(countryCode));
            }
            party.addToCommunicationChannels(address);
            AgreementRole role = lease.findRoleWithType(agreementRoleTypes.findByTitle(LeaseConstants.ART_TENANT), clockService.now());
            role.addCommunicationChannel(type, address);

        }
    }

    @ActionSemantics(Of.IDEMPOTENT)
    public void putLeaseTermForIndexableRent(
            // start generic fields
            @Named("leaseReference") String leaseReference, @Named("tenantReference") String tenantReference, @Named("unitReference") @Optional String unitReference, @Named("itemSequence") BigInteger itemSequence, @Named("itemType") String itemType, @Named("itemStartDate") LocalDate itemStartDate,
            @Named("sequence") BigInteger sequence,
            @Named("startDate") @Optional LocalDate startDate,
            @Named("endDate") @Optional LocalDate endDate,
            @Named("status") @Optional String status,
            // end generic fields
            @Named("reviewDate") @Optional LocalDate reviewDate, @Named("effectiveDate") @Optional LocalDate effectiveDate, @Named("baseValue") @Optional BigDecimal baseValue, @Named("indexedValue") @Optional BigDecimal indexedValue, @Named("settledValue") @Optional BigDecimal settledValue,
            @Named("levellingValue") @Optional BigDecimal levellingValue, @Named("levellingPercentage") @Optional BigDecimal levellingPercentage, @Named("indexReference") @Optional String indexReference, @Named("indexationFrequency") @Optional String indexationFrequency,
            @Named("indexationPercentage") @Optional BigDecimal indexationPercentage, @Named("baseIndexReference") @Optional String baseIndexReference, @Named("baseIndexStartDate") @Optional LocalDate baseIndexStartDate, @Named("baseIndexEndDate") @Optional LocalDate baseIndexEndDate,
            @Named("baseIndexValue") @Optional BigDecimal baseIndexValue, @Named("nextIndexReference") @Optional String nextIndexReference, @Named("nextIndexStartDate") @Optional LocalDate nextIndexStartDate, @Named("nextIndexEndDate") @Optional LocalDate nextIndexEndDate,
            @Named("nextIndexValue") @Optional BigDecimal nextIndexValue) {
        LeaseTermForIndexableRent term = (LeaseTermForIndexableRent) putLeaseTerm(leaseReference, unitReference, itemSequence, itemType, itemStartDate, startDate, endDate, sequence, status);
        if (term != null) {
            Index index = indices.findIndexByReference(indexReference);
            LeaseTermFrequency indexationFreq = LeaseTermFrequency.valueOf(indexationFrequency);
            term.setIndex(index);
            term.setFrequency(indexationFreq);
            term.setEffectiveDate(effectiveDate);
            term.setBaseValue(baseValue);
            term.setIndexedValue(indexedValue);
            term.setSettledValue(settledValue);
            term.setBaseIndexStartDate(baseIndexStartDate);
            term.setBaseIndexValue(baseIndexValue);
            term.setNextIndexStartDate(nextIndexStartDate);
            term.setNextIndexValue(nextIndexValue);
            term.setIndexationPercentage(indexationPercentage);
            term.setLevellingValue(levellingValue);
            term.setLevellingPercentage(levellingPercentage);
        }
    }

    @ActionSemantics(Of.IDEMPOTENT)
    public void putLeaseTermForTurnoverRent(
            // start generic fields
            @Named("leaseReference") String leaseReference, @Named("tenantReference") String tenantReference, @Named("unitReference") @Optional String unitReference, @Named("itemSequence") BigInteger itemSequence, @Named("itemType") String itemType, @Named("itemStartDate") LocalDate itemStartDate,
            @Named("sequence") BigInteger sequence, @Named("startDate") @Optional LocalDate startDate, @Named("endDate") @Optional LocalDate endDate, @Named("status") @Optional String status,
            // end generic fields
            @Named("turnoverRentRule") @Optional String turnoverRentRule, @Named("budgetedTurnover") @Optional BigDecimal budgetedTurnover, @Named("auditedTurnover") @Optional BigDecimal auditedTurnover, @Named("turnoverRentValue") @Optional BigDecimal turnoverRentValue) {
        LeaseTermForTurnoverRent term = (LeaseTermForTurnoverRent) putLeaseTerm(leaseReference, unitReference, itemSequence, itemType, itemStartDate, startDate, endDate, sequence, status);
        if (term != null) {
            term.setTurnoverRentValue(turnoverRentValue);
            term.setBudgetedTurnover(budgetedTurnover);
            term.setAuditedTurnover(auditedTurnover);
            term.setTurnoverRentRule(turnoverRentRule);
        }
    }

    @ActionSemantics(Of.IDEMPOTENT)
    public void putLeaseTermForServiceCharge(
            // start generic fields
            @Named("leaseReference") String leaseReference, @Named("tenantReference") String tenantReference, @Named("unitReference") @Optional String unitReference, @Named("itemSequence") BigInteger itemSequence, @Named("itemType") String itemType, @Named("itemStartDate") LocalDate itemStartDate,
            @Named("sequence") BigInteger sequence, @Named("startDate") @Optional LocalDate startDate, @Named("endDate") @Optional LocalDate endDate, @Named("status") @Optional String status,
            // end generic fields
            @Named("auditedValue") @Optional BigDecimal auditedValue, @Named("budgetedValue") @Optional BigDecimal budgetedValue) {
        LeaseTermForServiceCharge term = (LeaseTermForServiceCharge) putLeaseTerm(leaseReference, unitReference, itemSequence, itemType, itemStartDate, startDate, endDate, sequence, status);
        if (term != null) {
            term.setAuditedValue(auditedValue);
            term.setBudgetedValue(budgetedValue);
        }
    }

    private LeaseTerm putLeaseTerm(String leaseReference, String unitReference, BigInteger itemSequence, String itemType, LocalDate itemStartDate, LocalDate startDate, LocalDate endDate, BigInteger sequence, String status) {
        Lease lease = leases.findLeaseByReference(leaseReference);
        if (lease == null) {
            throw new ApplicationException(String.format("Leaseitem with reference %1$s not found.", leaseReference));
        }
        Unit unit;
        if (unitReference != null) {
            unit = units.findUnitByReference(unitReference);
            if (unitReference != null && unit == null) {
                throw new ApplicationException(String.format("Unit with reference %s not found.", unitReference));
            }
        }
        LeaseItemType leaseItemType = fetchLeaseItemType(itemType);
        LeaseItem item = lease.findItem(leaseItemType, itemStartDate, itemSequence);
        if (item == null) {
            throw new ApplicationException(String.format("LeaseItem with reference %1$s, %2$s, %3$s, %4$s not found.", leaseReference, leaseItemType.toString(), itemStartDate.toString(), itemSequence.toString()));
        }
        // check if the date is within range of lease
        if (lease.getTerminationDate() == null || lease.getTerminationDate().compareTo(startDate) >= 0) {
            LeaseTerm term = item.findTermWithSequence(sequence);
            if (term == null) {
                if (sequence.equals(BigInteger.ONE)) {
                    term = item.createInitialTerm();
                } else {
                    LeaseTerm previousTerm = item.findTermWithSequence(sequence.subtract(BigInteger.ONE));
                    term = item.createNextTerm(previousTerm);
                    if (startDate != null)
                        previousTerm.setEndDate(startDate.minusDays(1));
                }
                term.setSequence(sequence);
            }
            term.setStatus(org.estatio.dom.Status.valueOf(status));
            term.setStartDate(startDate);
            // will be overwritten if there is a next term
            term.setEndDate(lease.getTerminationDate());
            return term;
        }
        return null;
    }

    @ActionSemantics(Of.IDEMPOTENT)
    public void putBankAccount(
            // start generic fields
            @Named("reference") @Optional String reference, @Named("name") @Optional String name, @Named("bankAccountType") @Optional String bankAccountType, @Named("ownerReference") String ownerReference, @Named("iban") @Optional String iban,
            @Named("nationalCheckCode") @Optional String nationalCheckCode, @Named("nationalBankCode") @Optional String nationalBankCode, @Named("branchCode") @Optional String branchCode, @Named("accountNumber") @Optional String accountNumber) {
        BankAccount bankAccount = (BankAccount) financialAccounts.findAccountByReference(reference);
        Party owner = parties.findPartyByReferenceOrName(ownerReference);
        if (owner == null)
            return;
        if (bankAccount == null) {
            bankAccount = financialAccounts.newBankAccount(owner, iban);
        }
        bankAccount.setReference(reference);
        bankAccount.setAccountNumber(accountNumber);
        bankAccount.setBranchCode(branchCode);
        bankAccount.setName(name);
        bankAccount.setNationalBankCode(nationalBankCode);
        bankAccount.setNationalCheckCode(nationalCheckCode);
        bankAccount.setBankAccountType(BankAccountType.valueOf(bankAccountType));
    }

    // //////////////////////////////////////

    private ClockService clockService;

    public void setClockService(ClockService clockService) {
        this.clockService = clockService;
    }

    private Countries countries;

    public void injectCountries(final Countries countries) {
        this.countries = countries;
    }

    private States states;

    public void injectStates(final States states) {
        this.states = states;
    }

    private Units<Unit> units;

    public void injectUnits(final Units<Unit> units) {
        this.units = units;
    }

    private Properties properties;

    public void injectProperties(final Properties properties) {
        this.properties = properties;
    }

    private Parties parties;

    public void injectParties(final Parties parties) {
        this.parties = parties;
    }

    private Organisations organisations;

    public void injectOrganisations(final Organisations organisations) {
        this.organisations = organisations;
    }

    private Persons persons;

    public void injectOrganisations(final Persons persons) {
        this.persons = persons;
    }

    private FixedAssetRoles fixedAssetRoles;

    public void injectFixedAssetRoles(final FixedAssetRoles fixedAssetRoles) {
        this.fixedAssetRoles = fixedAssetRoles;
    }

    private CommunicationChannels communicationChannels;

    public void injectCommunicationChannels(final CommunicationChannels communicationChannels) {
        this.communicationChannels = communicationChannels;
    }

    private PostalAddresses postalAddresses;

    public void injectPostalAddresses(PostalAddresses postalAddresses) {
        this.postalAddresses = postalAddresses;
    }

    private Leases leases;

    public void injectLeaseRepository(final Leases leases) {
        this.leases = leases;
    }

    private AgreementRoleCommunicationChannels agreementRoleCommunicationChannels;

    public void injectAgreementRoleCommunicationChannels(AgreementRoleCommunicationChannels agreementRoleCommunicationChannels) {
        this.agreementRoleCommunicationChannels = agreementRoleCommunicationChannels;
    }

    private AgreementRoleTypes agreementRoleTypes;

    public void injectAgreementRoleTypes(AgreementRoleTypes agreementRoleTypes) {
        this.agreementRoleTypes = agreementRoleTypes;
    }

    private LeaseUnits leaseUnits;

    public void injectLeaseUnits(final LeaseUnits leaseUnits) {
        this.leaseUnits = leaseUnits;
    }

    private Taxes taxes;

    public void injectTaxes(final Taxes taxes) {
        this.taxes = taxes;
    }

    private Charges charges;

    public void injectCharges(final Charges charges) {
        this.charges = charges;
    }

    private Indices indices;

    public void injectIndices(final Indices indices) {
        this.indices = indices;
    }

    private FinancialAccounts financialAccounts;

    public void injectFinancialAccounts(FinancialAccounts financialAccounts) {
        this.financialAccounts = financialAccounts;
    }

}
