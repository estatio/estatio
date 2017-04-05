package org.estatio.dom.viewmodels;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.excel.dom.ExcelFixture;
import org.isisaddons.module.excel.dom.ExcelFixtureRowHandler;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannel;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelRepository;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelType;
import org.incode.module.communications.dom.impl.commchannel.EmailAddressRepository;
import org.incode.module.communications.dom.impl.commchannel.PhoneOrFaxNumberRepository;
import org.incode.module.communications.dom.impl.commchannel.PostalAddress;
import org.incode.module.communications.dom.impl.commchannel.PostalAddressRepository;
import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.dom.impl.CountryRepository;
import org.incode.module.country.dom.impl.StateRepository;

import org.estatio.dom.Importable;
import org.estatio.dom.agreement.AgreementRoleCommunicationChannelType;
import org.estatio.dom.agreement.AgreementRoleCommunicationChannelTypeRepository;
import org.estatio.dom.agreement.AgreementRoleType;
import org.estatio.dom.agreement.AgreementRoleTypeRepository;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseConstants;
import org.estatio.dom.lease.LeaseRepository;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyRepository;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.dom.viewmodels.CommunicationChannelImport"
)
public class CommunicationChannelImport implements ExcelFixtureRowHandler, Importable {

    private static final Logger LOG = LoggerFactory.getLogger(CommunicationChannelImport.class);

    @Getter @Setter
    private String partyReference;

    @Getter @Setter
    private String partyContact;

    @Getter @Setter
    private String partyChamberOfCommerce;

    @Getter @Setter
    private String leaseReference;

    @Getter @Setter
    private String leaseName;

    @Getter @Setter
    private String addressType;

    @Getter @Setter
    private String address1;

    @Getter @Setter
    private String address2;

    @Getter @Setter
    private String address3;

    @Getter @Setter
    private String city;

    @Getter @Setter
    private String postalCode;

    @Getter @Setter
    private String stateCode;

    @Getter @Setter
    private String countryCode;

    @Getter @Setter
    private String phoneNumber;

    @Getter @Setter
    private String faxNumber;

    @Getter @Setter
    private String emailAddress;

    @Getter @Setter
    private Boolean legal;

    @Getter @Setter
    private String comments;

    static int counter = 0;

//    @Override
//    public List<Class> importAfter() {
//        return Lists.newArrayList(PropertyImport.class, OrganisationImport.class);
//    }

    @Override
    public List<Object> handleRow(FixtureScript.ExecutionContext executionContext, ExcelFixture excelFixture, Object previousRow) {
        return importData(previousRow);
    }

    // REVIEW: other import view models have @Action annotation here...  but in any case, is this view model actually ever surfaced in the UI?
    public List<Object> importData() {
        return importData(null);
    }

    @Programmatic
    @Override
    public List<Object> importData(final Object previousRow) {

        List<Object> results = new ArrayList<>();

        //Party
        final Party party = fetchParty(partyReference);
        if (party == null)
            throw new ApplicationException(String.format("Party with reference [%s] not found", partyReference));




        // Address
        if (address1 != null || address2 != null || address3 != null) {
            final Country country = countryRepository.findCountry(countryCode);

            PostalAddress comm = (PostalAddress) postalAddressRepository.findByAddress(party, address1, address2, address3, postalCode, city, country);

            if (comm == null) {
                comm = communicationChannelRepository.newPostal(party, CommunicationChannelType.POSTAL_ADDRESS, address1, address2, address3, postalCode, city, stateRepository.findState(stateCode), countryRepository.findCountry(countryCode));
            }
            if (legal != null && legal) {
                comm.setLegal(true);
            }

            // attach to lease
            if (leaseReference != null) {
                Lease lease = fetchLease(leaseReference);
                if (lease != null && addressType != null) {
                    final AgreementRoleType art = agreementRoleTypeRepository.find(LeaseConstants.AgreementRoleType.TENANT);
                    final AgreementRoleCommunicationChannelType arcct = agreementRoleCommunicationChannelTypeRepository.findByTitle(addressType);
                    lease.findRoleWithType(art, lease.getStartDate()).addCommunicationChannel(arcct, comm, lease.getStartDate());
                }
            }
        }

        // Phone
        if (phoneNumber != null) {
            CommunicationChannel comm = phoneOrFaxNumberRepository.findByPhoneOrFaxNumber(party, phoneNumber);
            if (comm == null) {
                comm = communicationChannelRepository.newPhoneOrFax(party, CommunicationChannelType.PHONE_NUMBER, phoneNumber);
                comm.setReference(leaseReference);
            }
        }
        // Fax
        if (faxNumber != null) {
            CommunicationChannel comm = phoneOrFaxNumberRepository.findByPhoneOrFaxNumber(party, faxNumber);
            if (comm == null) {
                comm = communicationChannelRepository.newPhoneOrFax(party, CommunicationChannelType.FAX_NUMBER, faxNumber);
                comm.setReference(leaseReference);
            }
        }
        // Email
        if (emailAddress != null) {
            CommunicationChannel comm = emailAddressRepository.findByEmailAddress(party, emailAddress);
            if (comm == null) {
                comm = communicationChannelRepository.newEmail(party, CommunicationChannelType.EMAIL_ADDRESS, emailAddress);
                comm.setReference(leaseReference);
            }
        }

        return results;

    }

    private Party fetchParty(final String partyReference) {
        final Party party = partyRepository.findPartyByReference(partyReference);
        if (party == null) {
            throw new ApplicationException(String.format("Party with reference %s not found.", partyReference));
        }
        return party;
    }

    private Lease fetchLease(final String leaseReference) {
        final Lease lease;
        lease = leaseRepository.findLeaseByReference(leaseReference.trim().replaceAll("~", "+"));
        if (lease == null) {
            throw new ApplicationException(String.format("Lease with reference %s not found.", leaseReference));
        }
        return lease;
    }

    //region > injected services
    @Inject
    private PartyRepository partyRepository;

    @Inject
    LeaseRepository leaseRepository;

    @Inject
    StateRepository stateRepository;

    @Inject
    CountryRepository countryRepository;

    @Inject
    private CommunicationChannelRepository communicationChannelRepository;

    @Inject
    private PostalAddressRepository postalAddressRepository;

    @Inject
    private EmailAddressRepository emailAddressRepository;

    @Inject
    private PhoneOrFaxNumberRepository phoneOrFaxNumberRepository;

    @Inject
    private AgreementRoleTypeRepository agreementRoleTypeRepository;

    @Inject
    private AgreementRoleCommunicationChannelTypeRepository agreementRoleCommunicationChannelTypeRepository;

    //endregion

}
