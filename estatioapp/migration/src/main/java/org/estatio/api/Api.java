/*
 *  Copyright 2012-2014 Eurocommercial Properties NV
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
package org.estatio.api;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.inject.Inject;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.estatio.dom.agreement.AgreementRole;
import org.estatio.dom.agreement.AgreementRoleCommunicationChannelType;
import org.estatio.dom.agreement.AgreementRoleCommunicationChannelTypes;
import org.estatio.dom.agreement.AgreementRoleType;
import org.estatio.dom.agreement.AgreementRoleTypes;
import org.estatio.dom.agreement.Agreements;
import org.estatio.dom.asset.FixedAssetRoleType;
import org.estatio.dom.asset.Properties;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyType;
import org.estatio.dom.asset.Unit;
import org.estatio.dom.asset.UnitType;
import org.estatio.dom.asset.Units;
import org.estatio.dom.asset.financial.FixedAssetFinancialAccounts;
import org.estatio.dom.bankmandate.BankMandate;
import org.estatio.dom.bankmandate.BankMandates;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.ChargeGroup;
import org.estatio.dom.charge.ChargeGroups;
import org.estatio.dom.charge.Charges;
import org.estatio.dom.communicationchannel.CommunicationChannel;
import org.estatio.dom.communicationchannel.CommunicationChannelContributions;
import org.estatio.dom.communicationchannel.CommunicationChannelType;
import org.estatio.dom.communicationchannel.CommunicationChannels;
import org.estatio.dom.communicationchannel.EmailAddresses;
import org.estatio.dom.communicationchannel.PhoneOrFaxNumbers;
import org.estatio.dom.communicationchannel.PostalAddress;
import org.estatio.dom.communicationchannel.PostalAddresses;
import org.estatio.dom.financial.FinancialAccountTransaction;
import org.estatio.dom.financial.FinancialAccountTransactions;
import org.estatio.dom.financial.FinancialAccounts;
import org.estatio.dom.financial.bankaccount.BankAccount;
import org.estatio.dom.financial.bankaccount.BankAccounts;
import org.estatio.dom.financial.utils.IBANValidator;
import org.estatio.dom.geography.Countries;
import org.estatio.dom.geography.Country;
import org.estatio.dom.geography.State;
import org.estatio.dom.geography.States;
import org.estatio.dom.guarantee.Guarantee;
import org.estatio.dom.guarantee.GuaranteeType;
import org.estatio.dom.guarantee.Guarantees;
import org.estatio.dom.index.Index;
import org.estatio.dom.index.Indices;
import org.estatio.dom.invoice.CollectionNumerators;
import org.estatio.dom.invoice.Invoices;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.lease.InvoicingFrequency;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseConstants;
import org.estatio.dom.lease.LeaseItem;
import org.estatio.dom.lease.LeaseItemStatus;
import org.estatio.dom.lease.LeaseItemType;
import org.estatio.dom.lease.LeaseStatus;
import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.lease.LeaseTermForFixed;
import org.estatio.dom.lease.LeaseTermForIndexable;
import org.estatio.dom.lease.LeaseTermForServiceCharge;
import org.estatio.dom.lease.LeaseTermForTax;
import org.estatio.dom.lease.LeaseTermForTurnoverRent;
import org.estatio.dom.lease.LeaseTermFrequency;
import org.estatio.dom.lease.LeaseType;
import org.estatio.dom.lease.LeaseTypes;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.lease.Occupancies;
import org.estatio.dom.lease.Occupancy;
import org.estatio.dom.lease.Occupancy.OccupancyReportingType;
import org.estatio.dom.lease.breaks.BreakExerciseType;
import org.estatio.dom.lease.breaks.BreakType;
import org.estatio.dom.party.Organisation;
import org.estatio.dom.party.Organisations;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.Person;
import org.estatio.dom.party.PersonGenderType;
import org.estatio.dom.party.Persons;
import org.estatio.dom.party.relationship.PartyRelationship;
import org.estatio.dom.party.relationship.PartyRelationshipType;
import org.estatio.dom.party.relationship.PartyRelationships;
import org.estatio.dom.tax.Tax;
import org.estatio.dom.tax.TaxRate;
import org.estatio.dom.tax.Taxes;
import org.estatio.dom.utils.JodaPeriodUtils;
import org.estatio.dom.utils.StringUtils;
import org.estatio.services.clock.ClockService;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Hidden
public class Api extends AbstractFactoryAndRepository {

    private static final Logger LOG = LoggerFactory.getLogger(Api.class);

    @Override
    public String getId() {
        return "api";
    }

    public String iconName() {
        return "Api";
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.IDEMPOTENT)
    public void putCountry(
            @Named("code") String code,
            @Named("alpha2Code") String alpha2Code,
            @Named("name") String name) {
        Country country = countries.findCountry(code);
        if (country == null) {
            country = countries.createCountry(code, alpha2Code, name);
        }
    }

    private Country fetchCountry(String countryCode) {
        return fetchCountry(countryCode, true);
    }

    private Country fetchCountry(String countryCode, boolean exception) {
        Country country = countries.findCountry(countryCode);
        if (country == null && exception) {
            throw new ApplicationException(String.format("Country with code %1$s not found", countryCode));
        }
        return country;
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.IDEMPOTENT)
    public void putState(
            @Named("code") String reference,
            @Named("name") String name,
            @Named("countryCode") String countryCode) {
        Country country = fetchCountry(countryCode);
        State state = states.findState(countryCode);
        if (state == null) {
            state = states.newState(reference, name, country);
        }
        state.setName(name);
        state.setCountry(country);
    }

    private State fetchState(String stateCode, boolean exception) {
        State country = states.findState(stateCode);
        if (country == null && exception) {
            throw new ApplicationException(String.format("State with code %1$s not found", stateCode));
        }
        return country;
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.IDEMPOTENT)
    public void putLeaseType(
            @Named("reference") String reference,
            @Named("name") String name) {
        LeaseType leaseType = leaseTypes.findOrCreate(reference, name);
        if (ObjectUtils.compare(name, leaseType.getName()) != 0) {
            leaseType.setName(name);
        }
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.IDEMPOTENT)
    public void putCharge(
            @Named("reference") String reference,
            @Named("name") String name,
            @Named("description") @Optional String description,
            @Named("taxReference") String taxReference,
            @Named("sortOrder") @Optional String sortOrder,
            @Named("chargeGroupReference") String chargeGroupReference,
            @Named("chargeGroupName") String chargeGroupName,
            @Named("externalReference") @Optional String externalReference) {
        Tax tax = taxes.findOrCreate(taxReference, taxReference);
        ChargeGroup chargeGroup = fetchOrCreateChargeGroup(chargeGroupReference, chargeGroupName);
        Charge charge = charges.newCharge(reference, name, description, tax, chargeGroup);
        charge.setExternalReference(externalReference);
        charge.setSortOrder(sortOrder);
    }

    private Charge fetchCharge(String chargeReference) {
        Charge charge = charges.findCharge(chargeReference);
        if (charge == null) {
            throw new ApplicationException(String.format("Charge with reference %s not found.", chargeReference));
        }
        return charge;
    }

    private ChargeGroup fetchOrCreateChargeGroup(String reference, String name) {
        ChargeGroup chargeGroup = chargeGroups.findChargeGroup(reference);
        if (chargeGroup == null) {
            chargeGroup = chargeGroups.createChargeGroup(reference, name);
        }
        chargeGroup.setName(name);
        return chargeGroup;
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.IDEMPOTENT)
    public void putTax(
            @Named("reference") String reference,
            @Named("name") String name,
            @Named("description") String description,
            @Named("externalReference") @Optional String externalReference,
            @Named("ratePercentage") BigDecimal percentage,
            @Named("rateStartDate") LocalDate startDate,
            @Named("rateExternalReference") @Optional String rateExternalReference) {
        Tax tax = taxes.findOrCreate(reference, name);
        tax.setExternalReference(externalReference);
        tax.setDescription(description);
        TaxRate rate = tax.newRate(startDate, percentage);
        rate.setExternalReference(rateExternalReference);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.IDEMPOTENT)
    public void putPerson(
            @Named("reference") String reference,
            @Named("initials") @Optional String initials,
            @Named("firstName") String firstName,
            @Named("lastName") String lastName,
            @Named("Gender") @Optional String gender) {
        Person person = (Person) parties.findPartyByReference(reference);
        if (person == null) {
            person = persons.newPerson(
                    reference,
                    initials,
                    firstName,
                    lastName,
                    gender == null ? PersonGenderType.UNKNOWN : PersonGenderType.valueOf(gender));
        }
        person.setFirstName(firstName);
        person.setLastName(lastName);
    }

    @ActionSemantics(Of.IDEMPOTENT)
    public void putOrganisation(
            @Named("reference") String reference,
            @Named("name") String name,
            @Named("vatCode") @Optional String vatCode,
            @Named("fiscalCode") @Optional String fiscalCode) {
        Organisation org = (Organisation) parties.findPartyByReferenceOrNull(reference);
        if (org == null) {
            org = organisations.newOrganisation(reference, name);
        }
        org.setName(name);
        org.setFiscalCode(fiscalCode);
        org.setVatCode(vatCode);
    }

    private Party fetchParty(String partyReference) {
        Party party = parties.findPartyByReferenceOrNull(partyReference);
        if (party == null) {
            throw new ApplicationException(String.format("Party with reference %s not found.", partyReference));
        }
        return party;
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.IDEMPOTENT)
    public void putProperty(
            @Named("reference") String reference,
            @Named("name") String name,
            @Named("countryCode") String countryCode,
            @Named("city") String city,
            @Named("type") String type,
            @Named("acquireDate") @Optional LocalDate acquireDate,
            @Named("disposalDate") @Optional LocalDate disposalDate,
            @Named("openingDate") @Optional LocalDate openingDate,
            @Named("ownerReference") @Optional String ownerReference,
            @Named("numeratorFormat") @Optional String numeratorFormat,
            @Named("externalReference") @Optional String externalReference
            ) {
        Party owner = fetchParty(ownerReference);
        Property property = fetchProperty(reference, true);
        property.setName(name);
        property.setCountry(fetchCountry(countryCode));
        property.setCity(city);
        property.setType(PropertyType.valueOf(type));
        property.setAcquireDate(acquireDate);
        property.setDisposalDate(disposalDate);
        property.setOpeningDate(openingDate);
        property.setExternalReference(externalReference);
        property.addRoleIfDoesNotExist(owner, FixedAssetRoleType.PROPERTY_OWNER, null, null);
        if (numeratorFormat != null)
            collectionNumerators.createInvoiceNumberNumerator(property, numeratorFormat, BigInteger.ZERO);
    }

    private Property fetchProperty(String reference, boolean createIfNotFond) {
        if (reference == null) {
            return null;
        }
        Property property = properties.findPropertyByReferenceElseNull(reference);
        if (property == null) {
            if (!createIfNotFond)
                throw new ApplicationException(String.format("Property with reference %s not found.", reference));
            property = properties.newProperty(reference, null, PropertyType.MIXED, null, null, null);
        }
        return property;
    }

    // //////////////////////////////////////

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
            @Named("dehorsArea") @Optional BigDecimal dehorsArea,
            @Named("address1") @Optional String address1,
            @Named("city") @Optional String city,
            @Named("postalCode") @Optional String postalCode,
            @Named("stateCode") @Optional String stateCode,
            @Named("countryCode") @Optional String countryCode) {
        Property property = fetchProperty(propertyReference, false);
        Unit unit = units.findUnitByReference(reference);
        if (unit == null) {
            unit = property.newUnit(reference, name, UnitType.BOUTIQUE);
        }
        // set attributes
        unit.setName(name);
        unit.setType(UnitType.valueOf(type));
        unit.changeDates(startDate, endDate);
        unit.setArea(area);
        unit.setSalesArea(salesArea);
        unit.setStorageArea(storageArea);
        unit.setMezzanineArea(mezzanineArea);
        unit.setDehorsArea(dehorsArea);
        CommunicationChannel cc = communicationChannelContributions.findCommunicationChannelForType(unit, CommunicationChannelType.POSTAL_ADDRESS);
        if (cc == null) {
            communicationChannelContributions.newPostal(unit, CommunicationChannelType.POSTAL_ADDRESS, countries.findCountry(countryCode), states.findState(stateCode), address1, null, null, postalCode, city);
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
    public void putPropertyPostalAddress(
            @Named("propertyReference") String propertyReference,
            @Named("address1") @Optional String address1,
            @Named("address2") @Optional String address2,
            @Named("city") String city,
            @Named("postalCode") @Optional String postalCode,
            @Named("stateCode") @Optional String stateCode,
            @Named("countryCode") String countryCode) {
        final Property property = properties.findPropertyByReferenceElseNull(propertyReference);
        if (property == null) {
            throw new ApplicationException(String.format("Property with reference %s not found.", propertyReference));
        }
        final CommunicationChannel comm = communicationChannelContributions.findCommunicationChannelForType(property, null);
        if (comm == null) {
            communicationChannelContributions.newPostal(property, CommunicationChannelType.POSTAL_ADDRESS, countries.findCountry(countryCode), states.findState(stateCode), address1, address2, null, postalCode, city);
        }
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.IDEMPOTENT)
    public void putPartyCommunicationChannels(
            @Named("partyReference") String partyReference,
            @Named("reference") @Optional String reference,
            @Named("address1") @Optional String address1,
            @Named("address2") @Optional String address2,
            @Named("address3") @Optional String address3,
            @Named("city") @Optional String city,
            @Named("postalCode") @Optional String postalCode,
            @Named("stateCode") @Optional String stateCode,
            @Named("countryCode") @Optional String countryCode,
            @Named("phoneNumber") @Optional String phoneNumber,
            @Named("faxNumber") @Optional String faxNumber,
            @Named("emailAddress") @Optional String emailAddress,
            @Named("legal") @Optional Boolean legal
            ) {
        Party party = fetchParty(partyReference);
        if (party == null)
            throw new ApplicationException(String.format("Party with reference [%s] not found", partyReference));

        // Address
        if (address1 != null) {
            Country country = fetchCountry(countryCode);
            PostalAddress comm = (PostalAddress) postalAddresses.findByAddress(party, address1, postalCode, city, country);
            if (comm == null) {
                comm = communicationChannels.newPostal(party, CommunicationChannelType.POSTAL_ADDRESS, address1, address2, null, postalCode, city, states.findState(stateCode), countries.findCountry(countryCode));
                comm.setReference(reference);
            }
            if (legal) {
                comm.setLegal(true);
            }
        }
        // Phone
        if (phoneNumber != null) {
            CommunicationChannel comm = phoneOrFaxNumbers.findByPhoneOrFaxNumber(party, phoneNumber);
            if (comm == null) {
                comm = communicationChannels.newPhoneOrFax(party, CommunicationChannelType.PHONE_NUMBER, phoneNumber);
                comm.setReference(reference);
            }
        }
        // Fax
        if (faxNumber != null) {
            CommunicationChannel comm = phoneOrFaxNumbers.findByPhoneOrFaxNumber(party, faxNumber);
            if (comm == null) {
                comm = communicationChannels.newPhoneOrFax(party, CommunicationChannelType.FAX_NUMBER, faxNumber);
                comm.setReference(reference);
            }
        }
        // Email
        if (emailAddress != null) {
            CommunicationChannel comm = emailAddresses.findByEmailAddress(party, emailAddress);
            if (comm == null) {
                comm = communicationChannels.newEmail(party, CommunicationChannelType.EMAIL_ADDRESS, emailAddress);
                comm.setReference(reference);
            }
        }

    }

    @ActionSemantics(Of.IDEMPOTENT)
    public void putPartyEmailAddress(
            @Named("partyReference") String partyReference,
            @Named("emailAddress") String emailAddress,
            @Named("legal") @Optional Boolean legal
            ) {
        Party party = fetchParty(partyReference);

        CommunicationChannel comm = emailAddresses.findByEmailAddress(party, emailAddress);
        if (comm == null) {
            comm = communicationChannels.newEmail(party, CommunicationChannelType.EMAIL_ADDRESS, emailAddress);
            if (legal != null) {
                comm.setLegal(legal);
            }
        }

    }

    @ActionSemantics(Of.IDEMPOTENT)
    public void putPartyContact(
            @Named("partyReference") String partyReference,
            @Named("sequence") Integer sequence,
            @Named("name") String name,
            @Named("type") String type,
            @Named("value") String value
            ) {
        String toPartyReference = partyReference.concat("-").concat(sequence.toString());
        Person toPerson = (Person) parties.findPartyByReferenceOrNull(toPartyReference);
        String UNKOWN_LASTNAME = "*Unknown";
        if (toPerson == null) {
            Party fromParty = fetchParty(partyReference);
            PartyRelationship relationShip = partyRelationships.newRelatedPerson(
                    fromParty,
                    toPartyReference,
                    "",
                    "",
                    UNKOWN_LASTNAME,
                    PersonGenderType.UNKNOWN,
                    PartyRelationshipType.CONTACT.toTitle(),
                    null,
                    null, null);
            toPerson = (Person) relationShip.getTo();
        }
        Person person = (Person) toPerson;
        switch (type) {
        case "name":
            if (person.getLastName().equals(UNKOWN_LASTNAME)) {
                String nameParts[] = value.split(" ");
                setNames(person, nameParts);
            }
            break;

        case "phone":
            if (phoneOrFaxNumbers.findByPhoneOrFaxNumber(toPerson, value) == null) {
                communicationChannels.newPhoneOrFax(toPerson, CommunicationChannelType.PHONE_NUMBER, value);
            }
            break;

        case "email":
            if (person.getLastName().equals(UNKOWN_LASTNAME)) {
                String namePart = value.split("@")[0];
                String nameParts[] = namePart.split("\\.");
                setNames(person, nameParts);
            }
            if (emailAddresses.findByEmailAddress(toPerson, value) == null) {
                communicationChannels.newEmail(toPerson, CommunicationChannelType.EMAIL_ADDRESS, value.toLowerCase());
            }
            break;

        default:
            break;
        }

    }

    private void setNames(Person person, String[] nameParts) {
        if (nameParts.length > 1) {
            person.change(
                    PersonGenderType.UNKNOWN,
                    "",
                    StringUtils.capitalize(nameParts[0].toLowerCase()),
                    StringUtils.capitalize(nameParts[1].toLowerCase()));
        }
        if (nameParts.length == 1) {
            person.change(
                    PersonGenderType.UNKNOWN,
                    "",
                    "",
                    StringUtils.capitalize(nameParts[0].toLowerCase()));
        }
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.IDEMPOTENT)
    public void putPropertyActor(
            @Named("propertyReference") String propertyReference,
            @Named("partyReference") String partyReference,
            @Named("type") String typeStr,
            @Named("startDate") @Optional LocalDate startDate,
            @Named("endDate") @Optional LocalDate endDate) {
        final Property property = fetchProperty(propertyReference, false);
        final Party party = fetchParty(partyReference);
        final FixedAssetRoleType type = FixedAssetRoleType.valueOf(typeStr);
        property.addRoleIfDoesNotExist(party, type, startDate, endDate);
    }

    @ActionSemantics(Of.IDEMPOTENT)
    public void putLease(
            @Named("reference") String reference,
            @Named("name") String name,
            @Named("tenantReference") String tenantReference,
            @Named("landlordReference") String landlordReference,
            @Named("type") String type,
            @Named("status") String statusStr,
            @Named("startDate") @Optional LocalDate startDate,
            @Named("endDate") @Optional LocalDate endDate,
            @Named("tenancyStartDate") @Optional LocalDate tenancyStartDate,
            @Named("tenancyEndDate") @Optional LocalDate tenancyEndDate,
            @Named("propertyReference") @Optional String propertyReference
            ) {
        Party tenant = fetchParty(tenantReference);
        Party landlord = fetchParty(landlordReference);
        Lease lease = leases.findLeaseByReferenceElseNull(reference);
        LeaseType leaseType = leaseTypes.findOrCreate(type, null);
        LeaseStatus status = LeaseStatus.valueOf(statusStr);
        if (lease == null) {
            lease = leases.newLease(reference, name, leaseType, startDate, endDate, tenancyStartDate, tenancyEndDate, landlord, tenant);
        }
        lease.setTenancyStartDate(tenancyStartDate);
        lease.setTenancyEndDate(tenancyEndDate);
        lease.setStatus(status);
    }

    private Lease fetchLease(String leaseReference) {
        Lease lease;
        lease = leases.findLeaseByReference(leaseReference.trim().replaceAll("~", "+"));
        if (lease == null) {
            throw new ApplicationException(String.format("Lease with reference %s not found.", leaseReference));
        }
        return lease;
    }

    @ActionSemantics(Of.IDEMPOTENT)
    public void putLeaseLink(@Named("leaseReference") String leaseReference, @Named("previousLeaseReference") String previousLeaseReference) {
        Lease lease = fetchLease(leaseReference);
        Lease previousLease = fetchLease(previousLeaseReference);
        lease.setPrevious(previousLease);
    }

    @ActionSemantics(Of.IDEMPOTENT)
    public void putOccupancy(
            @Named("leaseReference") String leaseReference,
            @Named("unitReference") @Optional String unitReference,
            @Named("startDate") @Optional LocalDate startDate,
            @Named("endDate") @Optional LocalDate endDate,
            @Named("tenancyStartDate") @Optional LocalDate tenancyStartDate,
            @Named("tenancyEndDate") @Optional LocalDate tenancyEndDate,
            @Named("size") @Optional String size,
            @Named("brand") @Optional String brand,
            @Named("sector") @Optional String sector,
            @Named("activity") @Optional String activity,
            @Named("reportTurnover") @Optional String reportTurnover,
            @Named("reportRent") @Optional String reportRent,
            @Named("reportOCR") @Optional String reportOCR) {
        Lease lease = fetchLease(leaseReference);
        Unit unit = units.findUnitByReference(unitReference);
        if (unitReference != null && unit == null) {
            throw new ApplicationException(String.format("Unit with reference %s not found.", unitReference));
        }
        Occupancy occupancy = occupancies.findByLeaseAndUnitAndStartDate(lease, unit, startDate);
        if (occupancy == null) {
            occupancy = occupancies.newOccupancy(lease, unit, startDate);
        }

        occupancy.setEndDate(endDate);
        occupancy.setUnitSizeName(size);
        occupancy.setBrandName(brand != null ? brand.replaceAll("\\p{C}", "").trim() : null);
        occupancy.setSectorName(sector);
        occupancy.setActivityName(activity);
        occupancy.setReportTurnover(reportTurnover != null ? OccupancyReportingType.valueOf(reportTurnover) : OccupancyReportingType.NO);
        occupancy.setReportRent(reportRent != null ? OccupancyReportingType.valueOf(reportRent) : OccupancyReportingType.NO);
        occupancy.setReportOCR(reportOCR != null ? OccupancyReportingType.valueOf(reportOCR) : OccupancyReportingType.NO);
    }

    @ActionSemantics(Of.IDEMPOTENT)
    public void putLeaseItem(
            @Named("leaseReference") String leaseReference,
            @Named("tenantReference") String tenantReference,
            @Named("unitReference") @Optional String unitReference,
            @Named("type") @Optional String leaseItemTypeName,
            @Named("sequence") BigInteger sequence,
            @Named("startDate") @Optional LocalDate startDate,
            @Named("endDate") @Optional LocalDate endDate,
            @Named("chargeReference") @Optional String chargeReference,
            @Named("nextDueDate") @Optional LocalDate nextDueDate,
            @Named("invoicingFrequency") @Optional String invoicingFrequency,
            @Named("paymentMethod") @Optional String paymentMethod,
            @Named("status") @Optional String status) {
        Lease lease = fetchLease(leaseReference);

        @SuppressWarnings("unused")
        Unit unit = fetchUnit(unitReference);

        LeaseItemType itemType = fetchLeaseItemType(leaseItemTypeName);
        Charge charge = fetchCharge(chargeReference);
        //
        LeaseItem item = lease.findItem(itemType, startDate, sequence);
        if (item == null) {
            item = lease.newItem(itemType, charge, InvoicingFrequency.valueOf(invoicingFrequency), PaymentMethod.valueOf(paymentMethod), startDate);
            item.setSequence(sequence);
        }
        item.setNextDueDate(nextDueDate);
        final LeaseItemStatus leaseItemStatus = LeaseItemStatus.valueOfElse(status, LeaseItemStatus.ACTIVE);
    }

    private LeaseItemType fetchLeaseItemType(String type) {
        LeaseItemType itemType = LeaseItemType.valueOf(type);
        if (itemType == null) {
            throw new ApplicationException(String.format("Type with reference %s not found.", type));
        }
        return itemType;
    }

    @ActionSemantics(Of.IDEMPOTENT)
    public void putLeasePostalAddress(
            @Named("partyReference") String partyReference,
            @Named("agreementRoleType") String agreementRoleType,
            @Named("leaseReference") @Optional String leaseReference,
            @Named("address1") @Optional String address1,
            @Named("address2") @Optional String address2,
            @Named("address3") @Optional String address3,
            @Named("postalCode") @Optional String postalCode,
            @Named("city") @Optional String city,
            @Named("stateCode") @Optional String stateCode,
            @Named("countryCode") @Optional String countryCode, @Named("isInvoiceAddress") @Optional BigInteger isInvoiceAddress
            ) {
        if (address1 != null && partyReference != null && leaseReference != null) {
            Lease lease = fetchLease(leaseReference);
            Party party = fetchParty(partyReference);
            AgreementRoleCommunicationChannelType agreementRoleCommunicationChannelType = agreementRoleCommunicationChannelTypes.findByTitle(isInvoiceAddress.compareTo(BigInteger.ZERO) == 0 ? LeaseConstants.ARCCT_INVOICE_ADDRESS : LeaseConstants.ARCCT_ADMINISTRATION_ADDRESS);
            if (agreementRoleCommunicationChannelType == null)
                throw new ApplicationException(String.format("AgreementRoleCommunicationChannelType not found."));
            PostalAddress address = (PostalAddress) postalAddresses.findByAddress(party, address1, postalCode, city, fetchCountry(countryCode));
            if (address == null) {
                address = communicationChannels.newPostal(party, CommunicationChannelType.POSTAL_ADDRESS, address1, address2, null, postalCode, city, fetchState(stateCode, false), fetchCountry(countryCode, false));
            }
            AgreementRoleType art = agreementRoleTypes.findByTitle(StringUtils.capitalize(agreementRoleType.toLowerCase()));
            if (art == null)
                throw new ApplicationException(String.format("AgreementRoleType %s not found.", agreementRoleType));
            AgreementRole role = lease.findRole(party, art, clockService.now());
            if (role == null)
                throw new ApplicationException(String.format("Role for %s, %s not found.", partyReference, agreementRoleType));
            role.addCommunicationChannel(agreementRoleCommunicationChannelType, address);
        }
    }

    @ActionSemantics(Of.IDEMPOTENT)
    public void putLeaseTermForIndexableRent(
            // start generic fields
            @Named("leaseReference") String leaseReference,
            @Named("tenantReference") String tenantReference,
            @Named("unitReference") @Optional String unitReference,
            @Named("itemSequence") BigInteger itemSequence,
            @Named("itemType") String itemType,
            @Named("itemStartDate") LocalDate itemStartDate,
            @Named("sequence") BigInteger sequence,
            @Named("startDate") @Optional LocalDate startDate,
            @Named("endDate") @Optional LocalDate endDate,
            @Named("status") @Optional String statusStr,
            // end generic fields
            @Named("reviewDate") @Optional LocalDate reviewDate,
            @Named("effectiveDate") @Optional LocalDate effectiveDate,
            @Named("baseValue") @Optional BigDecimal baseValue,
            @Named("indexedValue") @Optional BigDecimal indexedValue,
            @Named("settledValue") @Optional BigDecimal settledValue,
            @Named("levellingValue") @Optional BigDecimal levellingValue,
            @Named("levellingPercentage") @Optional BigDecimal levellingPercentage,
            @Named("indexReference") @Optional String indexReference,
            @Named("indexationFrequency") @Optional String indexationFrequency,
            @Named("indexationPercentage") @Optional BigDecimal indexationPercentage,
            @Named("baseIndexReference") @Optional String baseIndexReference,
            @Named("baseIndexStartDate") @Optional LocalDate baseIndexStartDate,
            @Named("baseIndexEndDate") @Optional LocalDate baseIndexEndDate,
            @Named("baseIndexValue") @Optional BigDecimal baseIndexValue,
            @Named("nextIndexReference") @Optional String nextIndexReference,
            @Named("nextIndexStartDate") @Optional LocalDate nextIndexStartDate,
            @Named("nextIndexEndDate") @Optional LocalDate nextIndexEndDate,
            @Named("nextIndexValue") @Optional BigDecimal nextIndexValue) {
        LeaseTermForIndexable term = (LeaseTermForIndexable) putLeaseTerm(
                leaseReference,
                unitReference,
                itemSequence,
                itemType,
                itemStartDate,
                startDate,
                endDate,
                sequence,
                statusStr);
        Index index = indices.findOrCreateIndex(indexReference, indexReference);
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
        term.setLevellingPercentage(levellingPercentage);
    }

    @ActionSemantics(Of.IDEMPOTENT)
    public void putLeaseTermForTurnoverRent(
            // start generic fields
            @Named("leaseReference") String leaseReference,
            @Named("tenantReference") String tenantReference,
            @Named("unitReference") @Optional String unitReference,
            @Named("itemSequence") BigInteger itemSequence,
            @Named("itemType") String itemType,
            @Named("itemStartDate") LocalDate itemStartDate,
            @Named("sequence") BigInteger sequence,
            @Named("startDate") @Optional LocalDate startDate,
            @Named("endDate") @Optional LocalDate endDate,
            @Named("status") @Optional String status,
            // end generic fields
            @Named("turnoverRentRule") @Optional String turnoverRentRule,
            @Named("auditedTurnover") @Optional BigDecimal auditedTurnover,
            @Named("auditedTurnoverRent") @Optional BigDecimal auditedTurnoverRent) {
        LeaseTermForTurnoverRent term = (LeaseTermForTurnoverRent) putLeaseTerm(leaseReference, unitReference, itemSequence, itemType, itemStartDate, startDate, endDate, sequence, status);
        if (term != null) {
            term.setTurnoverRentRule(turnoverRentRule);
            term.setAuditedTurnover(auditedTurnover);
            term.setAuditedTurnoverRent(auditedTurnoverRent);
        }
    }

    @ActionSemantics(Of.IDEMPOTENT)
    public void putLeaseTermForServiceCharge(
            // start generic fields
            @Named("leaseReference") String leaseReference,
            @Named("tenantReference") String tenantReference,
            @Named("unitReference") @Optional String unitReference,
            @Named("itemSequence") BigInteger itemSequence,
            @Named("itemType") String itemType,
            @Named("itemStartDate") LocalDate itemStartDate,
            @Named("sequence") BigInteger sequence,
            @Named("startDate") @Optional LocalDate startDate,
            @Named("endDate") @Optional LocalDate endDate,
            @Named("status") @Optional String status,
            // end generic fields
            @Named("auditedValue") @Optional BigDecimal auditedValue,
            @Named("budgetedValue") @Optional BigDecimal budgetedValue) {
        LeaseTermForServiceCharge term = (LeaseTermForServiceCharge) putLeaseTerm(leaseReference, unitReference, itemSequence, itemType, itemStartDate, startDate, endDate, sequence, status);
        if (term != null) {
            term.setAuditedValue(auditedValue);
            term.setBudgetedValue(budgetedValue);
        }
    }

    @ActionSemantics(Of.IDEMPOTENT)
    public void putLeaseTermForTax(
            // start generic fields
            @Named("leaseReference") String leaseReference,
            @Named("tenantReference") String tenantReference,
            @Named("unitReference") @Optional String unitReference,
            @Named("itemSequence") BigInteger itemSequence,
            @Named("itemType") String itemType,
            @Named("itemStartDate") LocalDate itemStartDate,
            @Named("sequence") BigInteger sequence,
            @Named("startDate") @Optional LocalDate startDate,
            @Named("endDate") @Optional LocalDate endDate,
            @Named("status") @Optional String status,
            // end generic fields
            @Named("taxPercentage") @Optional BigDecimal taxPercentage,
            @Named("recoverablePercentage") @Optional BigDecimal recoverablePercentage,
            @Named("taxable") @Optional Boolean taxable,
            @Named("taxValue") @Optional BigDecimal taxValue,
            @Named("paymentDate") @Optional LocalDate paymentDate,
            @Named("registrationDate") @Optional LocalDate registrationDate,
            @Named("registrationNumber") @Optional String registrationNumber,
            @Named("officeCode") @Optional String officeCode,
            @Named("officeName") @Optional String officeName,
            @Named("description") @Optional String description) {
        LeaseTermForTax term = (LeaseTermForTax) putLeaseTerm(leaseReference, unitReference, itemSequence, itemType, itemStartDate, startDate, endDate, sequence, status);
        term.setTaxPercentage(taxPercentage);
        term.setRecoverablePercentage(recoverablePercentage);
        term.setInvoicingDisabled(taxable);
        term.setTaxValue(taxValue);
        term.setPaymentDate(paymentDate);
        term.setRegistrationDate(registrationDate);
        term.setRegistrationNumber(registrationNumber);
        term.setOfficeCode(officeCode);
        term.setOfficeName(officeName);
        term.setDescription(description);
    }

    @ActionSemantics(Of.IDEMPOTENT)
    public void putLeaseTermForFixed(
            // start generic fields
            @Named("leaseReference") String leaseReference,
            @Named("tenantReference") String tenantReference,
            @Named("unitReference") @Optional String unitReference,
            @Named("itemSequence") BigInteger itemSequence,
            @Named("itemType") String itemType,
            @Named("itemStatus") String itemStatus,
            @Named("itemStartDate") @Optional LocalDate itemStartDate,
            @Named("itemEndDate") @Optional LocalDate itemEndDate,
            @Named("chargeReference") @Optional String chargeReference,
            @Named("invoicingFrequency") @Optional String invoicingFrequency,
            @Named("sequence") BigInteger sequence,
            @Named("startDate") @Optional LocalDate startDate,
            @Named("endDate") @Optional LocalDate endDate,
            @Named("status") @Optional String status,
            // end generic fields
            @Named("value") @Optional BigDecimal value) {

        putLeaseItem(
                leaseReference,
                tenantReference,
                unitReference,
                itemType,
                BigInteger.ONE,
                itemStartDate,
                itemEndDate,
                chargeReference,
                null,
                invoicingFrequency,
                "DIRECT_DEBIT",
                itemStatus);
        LeaseTermForFixed term = (LeaseTermForFixed) putLeaseTerm(
                leaseReference,
                unitReference,
                itemSequence,
                itemType,
                itemStartDate,
                startDate,
                endDate,
                sequence,
                status);
        term.setValue(value);
    }

    private LeaseTerm putLeaseTerm(
            final String leaseReference,
            final String unitReference,
            final BigInteger itemSequence,
            final String itemType,
            final LocalDate itemStartDate,
            final LocalDate startDate,
            final LocalDate endDate,
            final BigInteger sequence,
            final String statusStr) {
        Lease lease = fetchLease(leaseReference);
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
        LeaseTerm term = item.findTermWithSequence(sequence);
        if (term == null) {
            if (sequence.equals(BigInteger.ONE)) {
                term = item.newTerm(startDate, endDate);
            } else {
                LeaseTerm previousTerm = item.findTermWithSequence(sequence.subtract(BigInteger.ONE));
                term = previousTerm.createNext(startDate, endDate);
            }
            term.setSequence(sequence);
        }
        term.setStatus(org.estatio.dom.lease.LeaseTermStatus.valueOf(statusStr));
        return term;
    }

    @ActionSemantics(Of.IDEMPOTENT)
    public void putBankAccount(
            // start generic fields
            @Named("reference") String reference,
            @Named("name") @Optional String name,
            @Named("ownerReference") String ownerReference,
            @Named("bankAccountType") @Optional String bankAccountType,
            @Named("propertyReference") @Optional String propertyReference,
            @Named("iban") @Optional String iban,
            @Named("countryCode") @Optional String countryCode,
            @Named("nationalCheckCode") @Optional String nationalCheckCode,
            @Named("nationalBankCode") @Optional String nationalBankCode,
            @Named("branchCode") @Optional String branchCode,
            @Named("accountNumber") @Optional String accountNumber,
            @Named("externalReference") @Optional String externalReference
            ) {
        if (IBANValidator.valid(iban)) {
            BankAccount bankAccount = (BankAccount) financialAccounts.findAccountByReference(reference);
            Party owner = parties.findPartyByReference(ownerReference);
            if (owner == null)
                return;
            if (bankAccount == null) {
                bankAccount = bankAccounts.newBankAccount(owner, reference, name == null ? reference : name);
            }
            bankAccount.setIban(iban);
            bankAccount.verifyIban();
            if (propertyReference != null) {
                Property property = properties.findPropertyByReferenceElseNull(propertyReference);
                if (property == null) {
                    throw new IllegalArgumentException(propertyReference.concat(" not found"));
                }
                fixedAssetFinancialAccounts.findOrCreate(property, bankAccount);
            }
        }
    }

    @ActionSemantics(Of.IDEMPOTENT)
    public void putBankMandate(
            @Named("reference") String reference,
            @Named("sepaMandateIdentifier") @Optional String sepaMandateIdentifier,
            @Named("name") @Optional String name,
            @Named("leaseReference") String leaseReference,
            @Named("debtorReference") String debtorReference,
            @Named("creditorReference") String creditorReference,
            @Named("bankAccountReference") String bankAccountReference,
            @Named("startDate") LocalDate startDate,
            @Named("endDate") @Optional LocalDate endDate) {
        BankMandate bankMandate = (BankMandate) agreements.findAgreementByReference(reference);
        BankAccount bankAccount = (BankAccount) financialAccounts.findAccountByReference(bankAccountReference);
        if (bankAccount == null)
            throw new ApplicationException(String.format("BankAccount with reference %1$s not found", bankAccountReference));
        Lease lease = fetchLease(leaseReference);
        if (bankMandate == null) {
            Party debtor = fetchParty(debtorReference);
            Party creditor = fetchParty(creditorReference);
            bankMandate = bankMandates.newBankMandate(reference, name, startDate, endDate, debtor, creditor, bankAccount);
        }
        bankMandate.setBankAccount(bankAccount);
        bankMandate.setName(name);
        bankMandate.setStartDate(startDate);
        bankMandate.setEndDate(endDate);
        bankMandate.setSepaMandateIdentifier(sepaMandateIdentifier);
        lease.paidBy(bankMandate);
    }

    @ActionSemantics(Of.IDEMPOTENT)
    public void putGuarantee(
            // Guarantee
            @Named("reference") String reference,
            @Named("name") String name,
            @Named("leaseReference") String leaseReference,
            @Named("startDate") LocalDate startDate,
            @Named("endDate") @Optional LocalDate endDate,
            @Named("terminationDate") @Optional LocalDate terminationDate,
            @Named("guaranteeType") GuaranteeType guaranteeType,
            @Named("description") @Optional String description,
            @Named("monthsRent") @Optional BigDecimal monthsRent,
            @Named("monthsServiceCharge") @Optional BigDecimal monthsServiceCharge,
            @Named("maximumAmount") @Optional BigDecimal maximumAmount,
            // Transaction
            @Named("transactionDate") @Optional LocalDate transactionDate,
            @Named("transactionDescription") @Optional String transactionDescription,
            @Named("amount") @Optional BigDecimal amount) {
        Guarantee guarantee = guarantees.findByReference(reference);
        if (guarantee == null) {
            Lease lease = fetchLease(leaseReference);
            guarantee = guarantees.newGuarantee(
                    lease,
                    reference,
                    name,
                    guaranteeType,
                    startDate,
                    endDate,
                    description,
                    maximumAmount, null);
        }
        guarantee.setTerminationDate(terminationDate);
        guarantee.setDescription(description);

        FinancialAccountTransaction transaction = financialAccountTransactions.findTransaction(guarantee.getFinancialAccount(), transactionDate, BigInteger.ONE);
        if (transaction == null) {
            transaction = financialAccountTransactions.newTransaction(guarantee.getFinancialAccount(), transactionDate, transactionDescription, amount);
        }
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.IDEMPOTENT)
    public void putBreakOption(
            @Named("leaseReference") String leaseReference,
            @Named("breakType") String breakTypeStr,
            @Named("breakExcerciseType") String breakExcerciseTypeStr,
            @Named("breakDate") LocalDate breakDate,
            @Named("notificationDate") LocalDate notificationDate,
            @Named("notificationPeriod") @Optional String notificationPeriodStr,
            @Named("description") @Optional String description
            ) {
        Lease lease = fetchLease(leaseReference);
        BreakType breakType = BreakType.valueOf(breakTypeStr);
        BreakExerciseType breakExerciseType = BreakExerciseType.valueOf(breakExcerciseTypeStr);
        if (notificationDate != null) {
            Period period = new Period(notificationDate, breakDate);
            notificationPeriodStr = JodaPeriodUtils.asSimpleString(period);
        }
        if (lease.validateNewBreakOption(breakDate, notificationPeriodStr, breakExerciseType, breakType, description) == null) {
            lease.newBreakOption(breakDate, notificationPeriodStr, breakExerciseType, breakType, description);
        }
    }

    // //////////////////////////////////////

    @Inject
    private Agreements agreements;

    @Inject
    private BankMandates bankMandates;

    @Inject
    private ClockService clockService;

    @Inject
    private Countries countries;

    @Inject
    private States states;

    @Inject
    private Units units;

    @Inject
    private Properties properties;

    @Inject
    private Parties parties;

    @Inject
    private Organisations organisations;

    @Inject
    private Persons persons;

    @Inject
    private CommunicationChannelContributions communicationChannelContributions;

    @Inject
    private CommunicationChannels communicationChannels;

    @Inject
    private PostalAddresses postalAddresses;

    @Inject
    private EmailAddresses emailAddresses;

    @Inject
    private PhoneOrFaxNumbers phoneOrFaxNumbers;

    @Inject
    private Leases leases;

    @Inject
    private AgreementRoleTypes agreementRoleTypes;

    @Inject
    private Occupancies occupancies;

    @Inject
    private Taxes taxes;

    @Inject
    private Charges charges;

    @Inject
    private ChargeGroups chargeGroups;

    @Inject
    private Indices indices;

    @Inject
    private FinancialAccounts financialAccounts;

    @Inject
    private BankAccounts bankAccounts;

    @Inject
    private Invoices invoices;

    @Inject
    private CollectionNumerators collectionNumerators;

    @Inject
    private AgreementRoleCommunicationChannelTypes agreementRoleCommunicationChannelTypes;

    @Inject
    private LeaseTypes leaseTypes;

    @Inject
    private FixedAssetFinancialAccounts fixedAssetFinancialAccounts;

    @Inject
    private Guarantees guarantees;

    @Inject
    private FinancialAccountTransactions financialAccountTransactions;

    @Inject
    private PartyRelationships partyRelationships;

}
