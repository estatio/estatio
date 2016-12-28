package org.estatio.canonical.bankmandate.v1;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.canonical.DtoFactoryAbstract;
import org.estatio.canonical.DtoMappingHelper;
import org.estatio.dom.bankmandate.*;
import org.estatio.dom.bankmandate.SequenceType;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class BankMandateDtoFactory extends DtoFactoryAbstract {

    @Programmatic
    public BankMandateDto newDto(final BankMandate bankMandate) {
        final BankMandateDto dto = new BankMandateDto();

        dto.setSelf(mappingHelper.oidDtoFor(bankMandate));

        dto.setReference(fixup(bankMandate.getReference()));
        dto.setScheme(toDto(bankMandate.getScheme()));
        dto.setSequenceType(toDto2(bankMandate.getSequenceType()));
        dto.setSignatureDate(asXMLGregorianCalendar(bankMandate.getSignatureDate()));
        dto.setBankAccount(mappingHelper.oidDtoFor(bankMandate.getBankAccount()));

        dto.setStatus(Status.OPEN); // not currently in the estatio dom, so hard-coded for now

        return dto;
    }

    private org.estatio.canonical.bankmandate.v1.SequenceType toDto2(final SequenceType sequenceType) {
        switch (sequenceType) {
        case FIRST:
            return org.estatio.canonical.bankmandate.v1.SequenceType.FIRST;
        case RECURRENT:
            return org.estatio.canonical.bankmandate.v1.SequenceType.RECURRENT;
        default:
            // shouldn't happen, above switch is complete.
            throw new IllegalArgumentException(String.format(
                    "Sequence type '%s' not recognized.", sequenceType));
        }
    }

    private Scheme toDto(final org.estatio.dom.bankmandate.Scheme scheme) {
        switch (scheme) {
        case CORE:
            return org.estatio.canonical.bankmandate.v1.Scheme.CORE;
        case B2B:
            return org.estatio.canonical.bankmandate.v1.Scheme.B2B;
        default:
            // shouldn't happen, above switch is complete.
            throw new IllegalArgumentException(String.format(
                    "Scheme '%s' not recognized.", scheme));
        }
    }

    // TODO: We've added a suffix because agreement names must be unique, remove after closing https://incodehq.atlassian.net/browse/EST-684
    String fixup(final String reference) {
        if (reference.endsWith("-M")){
            return reference.substring(0,reference.length()-2);
        }
        return reference;
    }

    @Inject
    DtoMappingHelper mappingHelper;
}
