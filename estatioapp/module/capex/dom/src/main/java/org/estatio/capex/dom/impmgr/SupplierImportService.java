package org.estatio.capex.dom.impmgr;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.i18n.TranslatableString;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelRepository;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelType;
import org.incode.module.communications.dom.impl.commchannel.PostalAddress;
import org.incode.module.communications.dom.impl.commchannel.PostalAddressRepository;
import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.dom.impl.CountryRepository;

import org.estatio.dom.party.Organisation;
import org.estatio.dom.party.OrganisationRepository;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyRepository;

@DomainService(
        nature = NatureOfService.DOMAIN,
        objectType = "org.estatio.capex.dom.impmgr.SupplierImportService"
)
public class SupplierImportService {

    private static final String ATPATH = "/FRA"; // this import is for France

    private static final Logger LOG = LoggerFactory.getLogger(SupplierImportService.class);

    public Organisation findOrCreateOrganisationAndAddressByName(final String reference, final String name, final String address, final String postcode, final String city, final String country){

        final ApplicationTenancy applicationTenancy = applicationTenancyRepository.findByPath(ATPATH);

        Organisation organisation = null;
        if (partyRepository.findParties(name).size()>0){
            organisation = (Organisation) partyRepository.findParties(name).get(0);
            if (partyRepository.findParties(name).size()>1){
                String message = TranslatableString.tr("More than one seller found for %s; first found is taken", reference).toString();
                LOG.debug(message);
            }
        }

        if (organisation == null) {
            organisation = organisationRepository.newOrganisation(reference, false, name, applicationTenancy);
        }

        findOrCreatePostalAddress(organisation, address, postcode, city, country);

        return organisation;
    }

    public Organisation findOrCreateOrganisationAndAddressByReference(final String reference, final String name, final String address, final String postcode, final String city, final String country){

        final ApplicationTenancy applicationTenancy = applicationTenancyRepository.findByPath(ATPATH);

        Organisation organisation = (Organisation) partyRepository.findPartyByReference(reference);
        if (organisation == null) {
            organisation = organisationRepository.newOrganisation(reference, false, name, applicationTenancy);
        }

        findOrCreatePostalAddress(organisation, address, postcode, city, country);
        return  organisation;
    }


    private void findOrCreatePostalAddress(
            final Party organisation,
            final String address,
            final String postcode,
            final String city,
            final String country){

        Country countryObj = countryRepository.findCountryByAlpha2Code(country);

        PostalAddress postalAddress = postalAddressRepository.findByAddress(organisation, address, postcode, city, countryObj);

        if (postalAddress == null){

            postalAddress = communicationChannelRepository.newPostal(
                    organisation,
                    CommunicationChannelType.POSTAL_ADDRESS,
                    address,
                    null,
                    null,
                    postcode,
                    city,
                    null,
                    countryObj
            );

        }
    }

    @Inject ApplicationTenancyRepository applicationTenancyRepository;
    @Inject OrganisationRepository organisationRepository;
    @Inject PartyRepository partyRepository;
    @Inject PostalAddressRepository postalAddressRepository;
    @Inject CommunicationChannelRepository communicationChannelRepository;
    @Inject CountryRepository countryRepository;

}
