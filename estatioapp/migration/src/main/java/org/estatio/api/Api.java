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
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancies;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.estatio.dom.apptenancy.EstatioApplicationTenancies;
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
import org.estatio.dom.valuetypes.ApplicationTenancyLevel;
import org.estatio.services.clock.ClockService;

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
            @Named("code") final String code,
            @Named("alpha2Code") final String alpha2Code,
            @Named("name") final String name) {
        Country country = countries.findCountry(code);
        if (country == null) {
            country = countries.createCountry(code, alpha2Code, name);
        }
    }

    private Country fetchCountry(final String countryCode) {
        return fetchCountry(countryCode, true);
    }

    private Country fetchCountry(final String countryCode, final boolean exception) {
        final Country country = countries.findCountry(countryCode);
        if (country == null && exception) {
            throw new ApplicationException(String.format("Country with code %1$s not found", countryCode));
        }
        return country;
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.IDEMPOTENT)
    public void putState(
            @Named("code") final String reference,
            @Named("name") final String name,
            @Named("countryCode") final String countryCode) {
        final Country country = fetchCountry(countryCode);
        State state = states.findState(countryCode);
        if (state == null) {
            state = states.newState(reference, name, country);
        }
        state.setName(name);
        state.setCountry(country);
    }

    private State fetchState(final String stateCode, final boolean exception) {
        final State country = states.findState(stateCode);
        if (country == null && exception) {
            throw new ApplicationException(String.format("State with code %1$s not found", stateCode));
        }
        return country;
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.IDEMPOTENT)
    public void putLeaseType(
            @Named("reference") final String reference,
            @Named("name") final String name) {
        final LeaseType leaseType = leaseTypes.findOrCreate(reference, name);
        if (ObjectUtils.compare(name, leaseType.getName()) != 0) {
            leaseType.setName(name);
        }
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.IDEMPOTENT)
    public void putCharge(
            @Named("atPath") final String atPath,
            @Named("reference") final String reference,
            @Named("name") final String name,
            @Named("description") @Optional final String description,
            @Named("taxReference") final String taxReference,
            @Named("sortOrder") @Optional final String sortOrder,
            @Named("chargeGroupReference") final String chargeGroupReference,
            @Named("chargeGroupName") final String chargeGroupName,
            @Named("externalReference") @Optional final String externalReference) {

        final ChargeGroup chargeGroup = fetchOrCreateChargeGroup(chargeGroupReference, chargeGroupName);

        final ApplicationTenancy applicationTenancy = applicationTenancies.findTenancyByPath(atPath);

        final Tax tax = taxes.findOrCreate(taxReference, taxReference, applicationTenancy);
        final Charge charge = charges.newCharge(applicationTenancy , reference, name, description, tax, chargeGroup);

        charge.setExternalReference(externalReference);
        charge.setSortOrder(sortOrder);
    }

    private Charge fetchCharge(final String chargeReference) {
        final Charge charge = charges
                .findByReference(chargeReference);
        if (charge == null) {
            throw new ApplicationException(String.format("Charge with reference %s not found.", chargeReference));
        }
        return charge;
    }

    private ChargeGroup fetchOrCreateChargeGroup(final String reference, final String name) {
        ChargeGroup chargeGroup = chargeGroups.findChargeGroup(reference);
        if (chargeGroup == null) {
            chargeGroup = chargeGroups.createChargeGroup(reference, name);
        }
        chargeGroup.setName(name);
        return chargeGroup;
    }


    // //////////////////////////////////////

    @ActionSemantics(Of.IDEMPOTENT)
    public void putApplicationTenancy(
            @Named("path") final String path,
            @Named("name") final String name) {
        ApplicationTenancy applicationTenancy = applicationTenancies.findTenancyByPath(path);
        if (applicationTenancy == null) {
            final ApplicationTenancyLevel parentLevel = ApplicationTenancyLevel.of(path).parent();
            final ApplicationTenancy parentApplicationTenancy =
                    parentLevel != null
                            ? applicationTenancies.findTenancyByPath(parentLevel.getPath())
                            : null;
            applicationTenancy = applicationTenancies.newTenancy(name, path, parentApplicationTenancy);
        }
        applicationTenancy.setName(name);

        // TODO: EST-467, to remove
        getContainer().flush();
    }

    private ApplicationTenancy fetchApplicationTenancy(final String path) {
        final ApplicationTenancy applicationTenancy = applicationTenancies.findTenancyByPath(path);
        if (applicationTenancy == null) {
            throw new ApplicationException(String.format("Application tenancy with path %s not found.", path));
        }
        return applicationTenancy;
    }


    // //////////////////////////////////////

    @ActionSemantics(Of.IDEMPOTENT)
    public void putTax(
            @Named("atPath") final String atPath,
            @Named("reference") final String reference,
            @Named("name") final String name,
            @Named("description") final String description,
            @Named("externalReference") @Optional final String externalReference,
            @Named("ratePercentage") final BigDecimal percentage,
            @Named("rateStartDate") final LocalDate startDate,
            @Named("rateExternalReference") @Optional final String rateExternalReference) {

        final ApplicationTenancy applicationTenancy = applicationTenancies.findTenancyByPath(atPath);

        final Tax tax = taxes.findOrCreate(reference, name, applicationTenancy);
        tax.setExternalReference(externalReference);
        tax.setDescription(description);
        final TaxRate rate = tax.newRate(startDate, percentage);
        rate.setExternalReference(rateExternalReference);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.IDEMPOTENT)
    public void putPerson(
            @Named("atPath") final String atPath,
            @Named("reference") final String reference,
            @Named("initials") @Optional final String initials,
            @Named("firstName") final String firstName,
            @Named("lastName") final String lastName,
            @Named("Gender") @Optional final String gender) {
        final ApplicationTenancy applicationTenancy = applicationTenancies.findTenancyByPath(atPath);
        Person person = (Person) parties.findPartyByReference(reference);
        if (person == null) {
            person = persons.newPerson(
                    reference,
                    initials,
                    firstName,
                    lastName,
                    gender == null ? PersonGenderType.UNKNOWN : PersonGenderType.valueOf(gender), applicationTenancy);
        }
        person.setApplicationTenancyPath(applicationTenancy.getPath());
        person.setFirstName(firstName);
        person.setLastName(lastName);
    }

    @ActionSemantics(Of.IDEMPOTENT)
    public void putOrganisation(
            @Named("atPath") final String atPath,
            @Named("reference") final String reference,
            @Named("name") final String name,
            @Named("vatCode") @Optional final String vatCode,
            @Named("fiscalCode") @Optional final String fiscalCode) {

        final ApplicationTenancy applicationTenancy = applicationTenancies.findTenancyByPath(atPath);

        Organisation org = (Organisation) parties.findPartyByReferenceOrNull(reference);
        if (org == null) {
            org = organisations.newOrganisation(reference, name, applicationTenancy);
        }
        org.setApplicationTenancyPath(atPath);
        org.setName(name);
        org.setFiscalCode(fiscalCode);
        org.setVatCode(vatCode);
    }

    private Party fetchParty(final String partyReference) {
        final Party party = parties.findPartyByReferenceOrNull(partyReference);
        if (party == null) {
            throw new ApplicationException(String.format("Party with reference %s not found.", partyReference));
        }
        return party;
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.IDEMPOTENT)
    public void putProperty(
            @Named("reference") final String reference,
            @Named("name") final String name,
            @Named("countryCode") final String countryCode,
            @Named("city") final String city,
            @Named("type") final String type,
            @Named("acquireDate") @Optional final LocalDate acquireDate,
            @Named("disposalDate") @Optional final LocalDate disposalDate,
            @Named("openingDate") @Optional final LocalDate openingDate,
            @Named("ownerReference") @Optional final String ownerReference,
            @Named("numeratorFormat") @Optional final String numeratorFormat,
            @Named("externalReference") @Optional final String externalReference,
            @Named("countryAtPath") final String countryAtPath) {
        final Party owner = fetchParty(ownerReference);
        final Country country = fetchCountry(countryCode);
        final ApplicationTenancy countryAppTenancy = fetchApplicationTenancy(countryAtPath);
        final Property property = fetchProperty(reference, countryAppTenancy, true);
        property.setName(name);
        property.setCountry(country);
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

    private Property fetchProperty(
            final String reference,
            final ApplicationTenancy countryAppTenancy,
            final boolean createIfNotFond) {
        if (reference == null) {
            return null;
        }
        Property property = properties.findPropertyByReferenceElseNull(reference);
        if (property == null) {
            if (!createIfNotFond)
                throw new ApplicationException(String.format("Property with reference %s not found.", reference));
            property = properties.newProperty(reference, null, PropertyType.MIXED, null, null, null, countryAppTenancy);
        }
        return property;
    }


    // //////////////////////////////////////

    @ActionSemantics(Of.IDEMPOTENT)
    public void putUnit(
            @Named("reference") final String reference,
            @Named("propertyReference") final String propertyReference,
            @Named("ownerReference") @Optional final String ownerReference,
            @Named("name") final String name, @Named("type") final String type,
            @Named("startDate") @Optional final LocalDate startDate,
            @Named("endDate") @Optional final LocalDate endDate,
            @Named("area") @Optional final BigDecimal area,
            @Named("salesArea") @Optional final BigDecimal salesArea,
            @Named("storageArea") @Optional final BigDecimal storageArea,
            @Named("mezzanineArea") @Optional final BigDecimal mezzanineArea,
            @Named("dehorsArea") @Optional final BigDecimal dehorsArea,
            @Named("address1") @Optional final String address1,
            @Named("city") @Optional final String city,
            @Named("postalCode") @Optional final String postalCode,
            @Named("stateCode") @Optional final String stateCode,
            @Named("countryCode") @Optional final String countryCode) {
        final Property property = fetchProperty(propertyReference, null, false);
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
        final CommunicationChannel cc = communicationChannelContributions.findCommunicationChannelForType(unit, CommunicationChannelType.POSTAL_ADDRESS);
        if (cc == null) {
            communicationChannelContributions.newPostal(unit, CommunicationChannelType.POSTAL_ADDRESS, countries.findCountry(countryCode), states.findState(stateCode), address1, null, null, postalCode, city);
        }
    }

    private Unit fetchUnit(final String unitReference) {
        if (unitReference != null) {
            final Unit unit = units.findUnitByReference(unitReference);
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
            @Named("propertyReference") final String propertyReference,
            @Named("address1") @Optional final String address1,
            @Named("address2") @Optional final String address2,
            @Named("city") final String city,
            @Named("postalCode") @Optional final String postalCode,
            @Named("stateCode") @Optional final String stateCode,
            @Named("countryCode") final String countryCode) {
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
            @Named("partyReference") final String partyReference,
            @Named("reference") @Optional final String reference,
            @Named("address1") @Optional final String address1,
            @Named("address2") @Optional final String address2,
            @Named("address3") @Optional final String address3,
            @Named("city") @Optional final String city,
            @Named("postalCode") @Optional final String postalCode,
            @Named("stateCode") @Optional final String stateCode,
            @Named("countryCode") @Optional final String countryCode,
            @Named("phoneNumber") @Optional final String phoneNumber,
            @Named("faxNumber") @Optional final String faxNumber,
            @Named("emailAddress") @Optional final String emailAddress,
            @Named("legal") @Optional final Boolean legal
            ) {
        final Party party = fetchParty(partyReference);
        if (party == null)
            throw new ApplicationException(String.format("Party with reference [%s] not found", partyReference));

        // Address
        if (address1 != null) {
            final Country country = fetchCountry(countryCode);
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
            @Named("partyReference") final String partyReference,
            @Named("emailAddress") final String emailAddress,
            @Named("legal") @Optional final Boolean legal
            ) {
        final Party party = fetchParty(partyReference);

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
            @Named("partyReference") final String partyReference,
            @Named("sequence") final Integer sequence,
            @Named("name") final String name,
            @Named("type") final String type,
            @Named("value") final String value
            ) {
        final String toPartyReference = partyReference.concat("-").concat(sequence.toString());
        Person toPerson = (Person) parties.findPartyByReferenceOrNull(toPartyReference);
        final String UNKOWN_LASTNAME = "*Unknown";
        if (toPerson == null) {
            final Party fromParty = fetchParty(partyReference);
            final PartyRelationship relationShip = partyRelationships.newRelatedPerson(
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
        final Person person = (Person) toPerson;
        switch (type) {
        case "name":
            if (person.getLastName().equals(UNKOWN_LASTNAME)) {
                final String[] nameParts = value.split(" ");
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
                final String namePart = value.split("@")[0];
                final String[] nameParts = namePart.split("\\.");
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

    private void setNames(final Person person, final String[] nameParts) {
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
            @Named("propertyReference") final String propertyReference,
            @Named("partyReference") final String partyReference,
            @Named("type") final String typeStr,
            @Named("startDate") @Optional final LocalDate startDate,
            @Named("endDate") @Optional final LocalDate endDate) {
        final Property property = fetchProperty(propertyReference, null, false);
        final Party party = fetchParty(partyReference);
        final FixedAssetRoleType type = FixedAssetRoleType.valueOf(typeStr);
        property.addRoleIfDoesNotExist(party, type, startDate, endDate);
    }

    @ActionSemantics(Of.IDEMPOTENT)
    public void putLease(
            @Named("reference") final String reference,
            @Named("name") final String name,
            @Named("tenantReference") final String tenantReference,
            @Named("landlordReference") final String landlordReference,
            @Named("type") final String type,
            @Named("status") final String statusStr,
            @Named("startDate") @Optional final LocalDate startDate,
            @Named("endDate") @Optional final LocalDate endDate,
            @Named("tenancyStartDate") @Optional final LocalDate tenancyStartDate,
            @Named("tenancyEndDate") @Optional final LocalDate tenancyEndDate,
            @Named("propertyReference") @Optional final String propertyReference
            ) {
        final Party tenant = fetchParty(tenantReference);
        final Party landlord = fetchParty(landlordReference);
        Lease lease = leases.findLeaseByReferenceElseNull(reference);
        final LeaseType leaseType = leaseTypes.findOrCreate(type, null);
        final LeaseStatus status = LeaseStatus.valueOf(statusStr);
        final Property property = fetchProperty(propertyReference, null, false);

        if (lease == null) {
            lease = leases.newLease(property.getApplicationTenancy(), reference, name, leaseType, startDate, endDate, tenancyStartDate, tenancyEndDate, landlord, tenant);
        }
        lease.setTenancyStartDate(tenancyStartDate);
        lease.setTenancyEndDate(tenancyEndDate);
        lease.setStatus(status);
    }

    private Lease fetchLease(final String leaseReference) {
        final Lease lease;
        lease = leases.findLeaseByReference(leaseReference.trim().replaceAll("~", "+"));
        if (lease == null) {
            throw new ApplicationException(String.format("Lease with reference %s not found.", leaseReference));
        }
        return lease;
    }

    @ActionSemantics(Of.IDEMPOTENT)
    public void putLeaseLink(@Named("leaseReference") final String leaseReference, @Named("previousLeaseReference") final String previousLeaseReference) {
        final Lease lease = fetchLease(leaseReference);
        final Lease previousLease = fetchLease(previousLeaseReference);
        lease.setPrevious(previousLease);
    }

    @ActionSemantics(Of.IDEMPOTENT)
    public void putOccupancy(
            @Named("leaseReference") final String leaseReference,
            @Named("unitReference") @Optional final String unitReference,
            @Named("startDate") @Optional final LocalDate startDate,
            @Named("endDate") @Optional final LocalDate endDate,
            @Named("tenancyStartDate") @Optional final LocalDate tenancyStartDate,
            @Named("tenancyEndDate") @Optional final LocalDate tenancyEndDate,
            @Named("size") @Optional final String size,
            @Named("brand") @Optional final String brand,
            @Named("sector") @Optional final String sector,
            @Named("activity") @Optional final String activity,
            @Named("reportTurnover") @Optional final String reportTurnover,
            @Named("reportRent") @Optional final String reportRent,
            @Named("reportOCR") @Optional final String reportOCR) {
        final Lease lease = fetchLease(leaseReference);
        final Unit unit = units.findUnitByReference(unitReference);
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
            @Named("leaseReference") final String leaseReference,
            @Named("tenantReference") final String tenantReference,
            @Named("unitReference") @Optional final String unitReference,
            @Named("type") @Optional final String leaseItemTypeName,
            @Named("sequence") final BigInteger sequence,
            @Named("startDate") @Optional final LocalDate startDate,
            @Named("endDate") @Optional final LocalDate endDate,
            @Named("chargeReference") @Optional final String chargeReference,
            @Named("nextDueDate") @Optional final LocalDate nextDueDate,
            @Named("invoicingFrequency") @Optional final String invoicingFrequency,
            @Named("paymentMethod") @Optional final String paymentMethod,
            @Named("status") @Optional final String status,
            @Named("atPath") final String leaseItemAtPath) {
        final Lease lease = fetchLease(leaseReference);

        final Unit unit = fetchUnit(unitReference);
        final ApplicationTenancy unitApplicationTenancy = unit.getApplicationTenancy();

        final ApplicationTenancy countryApplicationTenancy = unitApplicationTenancy.getParent();
        final Charge charge = fetchCharge(chargeReference);

        ApplicationTenancy leaseApplicationTenancy = lease.getApplicationTenancy();
        final ApplicationTenancy leaseItemApplicationTenancy = fetchApplicationTenancy(leaseItemAtPath);

        if(!leaseApplicationTenancy.getChildren().contains(leaseItemApplicationTenancy)) {
            throw new ApplicationException(
                    String.format("Lease item's appTenancy '%s' not child of lease's appTenancy '%s'.", leaseApplicationTenancy.getName(), leaseItemAtPath));
        }

        //
        final LeaseItemType itemType = fetchLeaseItemType(leaseItemTypeName);
        LeaseItem item = lease.findItem(itemType, startDate, sequence);
        if (item == null) {
            item = lease.newItem(itemType, charge, InvoicingFrequency.valueOf(invoicingFrequency), PaymentMethod.valueOf(paymentMethod), startDate, leaseItemApplicationTenancy);
            item.setSequence(sequence);
        }
        item.setNextDueDate(nextDueDate);
        final LeaseItemStatus leaseItemStatus = LeaseItemStatus.valueOfElse(status, LeaseItemStatus.ACTIVE);
    }

    private LeaseItemType fetchLeaseItemType(final String type) {
        final LeaseItemType itemType = LeaseItemType.valueOf(type);
        if (itemType == null) {
            throw new ApplicationException(String.format("Type with reference %s not found.", type));
        }
        return itemType;
    }

    @ActionSemantics(Of.IDEMPOTENT)
    public void putLeasePostalAddress(
            @Named("partyReference") final String partyReference,
            @Named("agreementRoleType") final String agreementRoleType,
            @Named("leaseReference") @Optional final String leaseReference,
            @Named("address1") @Optional final String address1,
            @Named("address2") @Optional final String address2,
            @Named("address3") @Optional final String address3,
            @Named("postalCode") @Optional final String postalCode,
            @Named("city") @Optional final String city,
            @Named("stateCode") @Optional final String stateCode,
            @Named("countryCode") @Optional final String countryCode, @Named("isInvoiceAddress") @Optional final BigInteger isInvoiceAddress
            ) {
        if (address1 != null && partyReference != null && leaseReference != null) {
            final Lease lease = fetchLease(leaseReference);
            final Party party = fetchParty(partyReference);
            final AgreementRoleCommunicationChannelType agreementRoleCommunicationChannelType = agreementRoleCommunicationChannelTypes.findByTitle(isInvoiceAddress.compareTo(BigInteger.ZERO) == 0 ? LeaseConstants.ARCCT_INVOICE_ADDRESS : LeaseConstants.ARCCT_ADMINISTRATION_ADDRESS);
            if (agreementRoleCommunicationChannelType == null)
                throw new ApplicationException(String.format("AgreementRoleCommunicationChannelType not found."));
            PostalAddress address = (PostalAddress) postalAddresses.findByAddress(party, address1, postalCode, city, fetchCountry(countryCode));
            if (address == null) {
                address = communicationChannels.newPostal(party, CommunicationChannelType.POSTAL_ADDRESS, address1, address2, null, postalCode, city, fetchState(stateCode, false), fetchCountry(countryCode, false));
            }
            final AgreementRoleType art = agreementRoleTypes.findByTitle(StringUtils.capitalize(agreementRoleType.toLowerCase()));
            if (art == null)
                throw new ApplicationException(String.format("AgreementRoleType %s not found.", agreementRoleType));
            final AgreementRole role = lease.findRole(party, art, clockService.now());
            if (role == null)
                throw new ApplicationException(String.format("Role for %s, %s not found.", partyReference, agreementRoleType));
            role.addCommunicationChannel(agreementRoleCommunicationChannelType, address);
        }
    }

    @ActionSemantics(Of.IDEMPOTENT)
    public void putLeaseTermForIndexableRent(
            // start generic fields
            @Named("atPath") final String atPath,
            @Named("leaseReference") final String leaseReference,
            @Named("tenantReference") final String tenantReference,
            @Named("unitReference") @Optional final String unitReference,
            @Named("itemSequence") final BigInteger itemSequence,
            @Named("itemType") final String itemType,
            @Named("itemStartDate") final LocalDate itemStartDate,
            @Named("sequence") final BigInteger sequence,
            @Named("startDate") @Optional final LocalDate startDate,
            @Named("endDate") @Optional final LocalDate endDate,
            @Named("status") @Optional final String statusStr,
            // end generic fields
            @Named("reviewDate") @Optional final LocalDate reviewDate,
            @Named("effectiveDate") @Optional final LocalDate effectiveDate,
            @Named("baseValue") @Optional final BigDecimal baseValue,
            @Named("indexedValue") @Optional final BigDecimal indexedValue,
            @Named("settledValue") @Optional final BigDecimal settledValue,
            @Named("levellingValue") @Optional final BigDecimal levellingValue,
            @Named("levellingPercentage") @Optional final BigDecimal levellingPercentage,
            @Named("indexReference") @Optional final String indexReference,
            @Named("indexationFrequency") @Optional final String indexationFrequency,
            @Named("indexationPercentage") @Optional final BigDecimal indexationPercentage,
            @Named("baseIndexReference") @Optional final String baseIndexReference,
            @Named("baseIndexStartDate") @Optional final LocalDate baseIndexStartDate,
            @Named("baseIndexEndDate") @Optional final LocalDate baseIndexEndDate,
            @Named("baseIndexValue") @Optional final BigDecimal baseIndexValue,
            @Named("nextIndexReference") @Optional final String nextIndexReference,
            @Named("nextIndexStartDate") @Optional final LocalDate nextIndexStartDate,
            @Named("nextIndexEndDate") @Optional final LocalDate nextIndexEndDate,
            @Named("nextIndexValue") @Optional final BigDecimal nextIndexValue) {
        final LeaseTermForIndexable term = (LeaseTermForIndexable) putLeaseTerm(
                leaseReference,
                unitReference,
                itemSequence,
                itemType,
                itemStartDate,
                startDate,
                endDate,
                sequence,
                statusStr);
        final ApplicationTenancy applicationTenancy = applicationTenancies.findTenancyByPath(atPath);
        final Index index = indices.findOrCreateIndex(applicationTenancy , indexReference, indexReference);
        final LeaseTermFrequency indexationFreq = LeaseTermFrequency.valueOf(indexationFrequency);
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
            @Named("leaseReference") final String leaseReference,
            @Named("tenantReference") final String tenantReference,
            @Named("unitReference") @Optional final String unitReference,
            @Named("itemSequence") final BigInteger itemSequence,
            @Named("itemType") final String itemType,
            @Named("itemStartDate") final LocalDate itemStartDate,
            @Named("sequence") final BigInteger sequence,
            @Named("startDate") @Optional final LocalDate startDate,
            @Named("endDate") @Optional final LocalDate endDate,
            @Named("status") @Optional final String status,
            // end generic fields
            @Named("turnoverRentRule") @Optional final String turnoverRentRule,
            @Named("auditedTurnover") @Optional final BigDecimal auditedTurnover,
            @Named("auditedTurnoverRent") @Optional final BigDecimal auditedTurnoverRent) {
        final LeaseTermForTurnoverRent term = (LeaseTermForTurnoverRent) putLeaseTerm(leaseReference, unitReference, itemSequence, itemType, itemStartDate, startDate, endDate, sequence, status);
        if (term != null) {
            term.setTurnoverRentRule(turnoverRentRule);
            term.setAuditedTurnover(auditedTurnover);
            term.setAuditedTurnoverRent(auditedTurnoverRent);
        }
    }

    @ActionSemantics(Of.IDEMPOTENT)
    public void putLeaseTermForServiceCharge(
            // start generic fields
            @Named("leaseReference") final String leaseReference,
            @Named("tenantReference") final String tenantReference,
            @Named("unitReference") @Optional final String unitReference,
            @Named("itemSequence") final BigInteger itemSequence,
            @Named("itemType") final String itemType,
            @Named("itemStartDate") final LocalDate itemStartDate,
            @Named("sequence") final BigInteger sequence,
            @Named("startDate") @Optional final LocalDate startDate,
            @Named("endDate") @Optional final LocalDate endDate,
            @Named("status") @Optional final String status,
            // end generic fields
            @Named("auditedValue") @Optional final BigDecimal auditedValue,
            @Named("budgetedValue") @Optional final BigDecimal budgetedValue) {
        final LeaseTermForServiceCharge term = (LeaseTermForServiceCharge) putLeaseTerm(leaseReference, unitReference, itemSequence, itemType, itemStartDate, startDate, endDate, sequence, status);
        if (term != null) {
            term.setAuditedValue(auditedValue);
            term.setBudgetedValue(budgetedValue);
        }
    }

    @ActionSemantics(Of.IDEMPOTENT)
    public void putLeaseTermForTax(
            // start generic fields
            @Named("leaseReference") final String leaseReference,
            @Named("tenantReference") final String tenantReference,
            @Named("unitReference") @Optional final String unitReference,
            @Named("itemSequence") final BigInteger itemSequence,
            @Named("itemType") final String itemType,
            @Named("itemStartDate") final LocalDate itemStartDate,
            @Named("sequence") final BigInteger sequence,
            @Named("startDate") @Optional final LocalDate startDate,
            @Named("endDate") @Optional final LocalDate endDate,
            @Named("status") @Optional final String status,
            // end generic fields
            @Named("taxPercentage") @Optional final BigDecimal taxPercentage,
            @Named("recoverablePercentage") @Optional final BigDecimal recoverablePercentage,
            @Named("taxable") @Optional final Boolean taxable,
            @Named("taxValue") @Optional final BigDecimal taxValue,
            @Named("paymentDate") @Optional final LocalDate paymentDate,
            @Named("registrationDate") @Optional final LocalDate registrationDate,
            @Named("registrationNumber") @Optional final String registrationNumber,
            @Named("officeCode") @Optional final String officeCode,
            @Named("officeName") @Optional final String officeName,
            @Named("description") @Optional final String description) {
        final LeaseTermForTax term = (LeaseTermForTax) putLeaseTerm(leaseReference, unitReference, itemSequence, itemType, itemStartDate, startDate, endDate, sequence, status);
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
            @Named("leaseReference") final String leaseReference,
            @Named("tenantReference") final String tenantReference,
            @Named("unitReference") @Optional final String unitReference,
            @Named("itemSequence") final BigInteger itemSequence,
            @Named("itemType") final String itemType,
            @Named("itemStatus") final String itemStatus,
            @Named("itemStartDate") @Optional final LocalDate itemStartDate,
            @Named("itemEndDate") @Optional final LocalDate itemEndDate,
            @Named("chargeReference") @Optional final String chargeReference,
            @Named("invoicingFrequency") @Optional final String invoicingFrequency,
            @Named("sequence") final BigInteger sequence,
            @Named("startDate") @Optional final LocalDate startDate,
            @Named("endDate") @Optional final LocalDate endDate,
            @Named("status") @Optional final String status,
            @Named("atPath") final String atPath,
            // end generic fields
            @Named("value") @Optional final BigDecimal value) {

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
                itemStatus, atPath);
        final LeaseTermForFixed term = (LeaseTermForFixed) putLeaseTerm(
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
        final Lease lease = fetchLease(leaseReference);
        final Unit unit;
        if (unitReference != null) {
            unit = units.findUnitByReference(unitReference);
            if (unitReference != null && unit == null) {
                throw new ApplicationException(String.format("Unit with reference %s not found.", unitReference));
            }
        }
        final LeaseItemType leaseItemType = fetchLeaseItemType(itemType);
        final LeaseItem item = lease.findItem(leaseItemType, itemStartDate, itemSequence);
        if (item == null) {
            throw new ApplicationException(String.format("LeaseItem with reference %1$s, %2$s, %3$s, %4$s not found.", leaseReference, leaseItemType.toString(), itemStartDate.toString(), itemSequence.toString()));
        }
        LeaseTerm term = item.findTermWithSequence(sequence);
        if (term == null) {
            if (sequence.equals(BigInteger.ONE)) {
                term = item.newTerm(startDate, endDate);
            } else {
                final LeaseTerm previousTerm = item.findTermWithSequence(sequence.subtract(BigInteger.ONE));
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
            @Named("reference") final String reference,
            @Named("name") @Optional final String name,
            @Named("ownerReference") final String ownerReference,
            @Named("bankAccountType") @Optional final String bankAccountType,
            @Named("propertyReference") @Optional final String propertyReference,
            @Named("iban") @Optional final String iban,
            @Named("countryCode") @Optional final String countryCode,
            @Named("nationalCheckCode") @Optional final String nationalCheckCode,
            @Named("nationalBankCode") @Optional final String nationalBankCode,
            @Named("branchCode") @Optional final String branchCode,
            @Named("accountNumber") @Optional final String accountNumber,
            @Named("externalReference") @Optional final String externalReference
            ) {
        if (IBANValidator.valid(iban)) {
            BankAccount bankAccount = (BankAccount) financialAccounts.findAccountByReference(reference);
            final Party owner = parties.findPartyByReference(ownerReference);
            if (owner == null)
                return;
            if (bankAccount == null) {
                bankAccount = bankAccounts.newBankAccount(owner, reference, name == null ? reference : name);
            }
            bankAccount.setIban(iban);
            bankAccount.verifyIban();
            if (propertyReference != null) {
                final Property property = properties.findPropertyByReferenceElseNull(propertyReference);
                if (property == null) {
                    throw new IllegalArgumentException(propertyReference.concat(" not found"));
                }
                fixedAssetFinancialAccounts.findOrCreate(property, bankAccount);
            }
        }
    }

    @ActionSemantics(Of.IDEMPOTENT)
    public void putBankMandate(
            @Named("reference") final String reference,
            @Named("sepaMandateIdentifier") @Optional final String sepaMandateIdentifier,
            @Named("name") @Optional final String name,
            @Named("leaseReference") final String leaseReference,
            @Named("debtorReference") final String debtorReference,
            @Named("creditorReference") final String creditorReference,
            @Named("bankAccountReference") final String bankAccountReference,
            @Named("startDate") final LocalDate startDate,
            @Named("endDate") @Optional final LocalDate endDate) {
        BankMandate bankMandate = (BankMandate) agreements.findAgreementByReference(reference);
        final BankAccount bankAccount = (BankAccount) financialAccounts.findAccountByReference(bankAccountReference);
        if (bankAccount == null)
            throw new ApplicationException(String.format("BankAccount with reference %1$s not found", bankAccountReference));
        final Lease lease = fetchLease(leaseReference);
        final Party debtor = fetchParty(debtorReference);
        final Party creditor = fetchParty(creditorReference);

        if (bankMandate == null) {

            lease.newMandate(bankAccount, reference, startDate, endDate);
            bankMandate = lease.getPaidBy();

            bankMandate.setName(name);

            // EST-467, previously was:
            // bankMandate = bankMandates.newBankMandate(reference, name, startDate, endDate, debtor, creditor, bankAccount);
        }

        // upsert
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
            @Named("reference") final String reference,
            @Named("name") final String name,
            @Named("leaseReference") final String leaseReference,
            @Named("startDate") final LocalDate startDate,
            @Named("endDate") @Optional final LocalDate endDate,
            @Named("terminationDate") @Optional final LocalDate terminationDate,
            @Named("guaranteeType") final GuaranteeType guaranteeType,
            @Named("description") @Optional final String description,
            @Named("monthsRent") @Optional final BigDecimal monthsRent,
            @Named("monthsServiceCharge") @Optional final BigDecimal monthsServiceCharge,
            @Named("maximumAmount") @Optional final BigDecimal maximumAmount,
            // Transaction
            @Named("transactionDate") @Optional final LocalDate transactionDate,
            @Named("transactionDescription") @Optional final String transactionDescription,
            @Named("amount") @Optional final BigDecimal amount) {
        Guarantee guarantee = guarantees.findByReference(reference);
        if (guarantee == null) {
            final Lease lease = fetchLease(leaseReference);
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
            @Named("leaseReference") final String leaseReference,
            @Named("breakType") final String breakTypeStr,
            @Named("breakExcerciseType") final String breakExcerciseTypeStr,
            @Named("breakDate") final LocalDate breakDate,
            @Named("notificationDate") final LocalDate notificationDate,
            @Named("notificationPeriod") @Optional String notificationPeriodStr,
            @Named("description") @Optional final String description
            ) {
        final Lease lease = fetchLease(leaseReference);
        final BreakType breakType = BreakType.valueOf(breakTypeStr);
        final BreakExerciseType breakExerciseType = BreakExerciseType.valueOf(breakExcerciseTypeStr);
        if (notificationDate != null) {
            final Period period = new Period(notificationDate, breakDate);
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

    @Inject
    private ApplicationTenancies applicationTenancies;

    @Inject
    private EstatioApplicationTenancies estatioApplicationTenancies;


}
