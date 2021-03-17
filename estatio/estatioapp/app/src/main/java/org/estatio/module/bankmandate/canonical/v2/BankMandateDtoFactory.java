package org.estatio.module.bankmandate.canonical.v2;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.canonical.bankmandate.v2.BankMandateDto;
import org.estatio.canonical.bankmandate.v2.Status;
import org.estatio.module.bankmandate.dom.BankMandate;
import org.estatio.module.bankmandate.dom.Scheme;
import org.estatio.module.bankmandate.dom.SequenceType;
import org.estatio.module.base.platform.applib.DtoFactoryAbstract;

@DomainService(
        nature = NatureOfService.DOMAIN,
        objectType = "bankmandate.canonical.v2.BankMandateDtoFactory"
)
public class BankMandateDtoFactory extends DtoFactoryAbstract<BankMandate, BankMandateDto> {

    public BankMandateDtoFactory(){
        super(BankMandate.class, BankMandateDto.class);
    }

    @Programmatic
    public BankMandateDto newDto(final BankMandate bankMandate) {
        final BankMandateDto dto = new BankMandateDto();
        dto.setMajorVersion("2");
        dto.setMinorVersion("0");

        dto.setSelf(mappingHelper.oidDtoFor(bankMandate));
        dto.setAtPath(bankMandate.getAtPath());

        dto.setReference(fixup(bankMandate.getReference()));
        dto.setScheme(toDto(bankMandate.getScheme()));
        dto.setSequenceType(toDto(bankMandate.getSequenceType()));
        dto.setSignatureDate(asXMLGregorianCalendar(bankMandate.getSignatureDate()));
        dto.setBankAccount(mappingHelper.oidDtoFor(bankMandate.getBankAccount()));

        dto.setStatus(Status.OPEN); // not currently in the estatio dom, so hard-coded for now

        return dto;
    }

    private static org.estatio.canonical.bankmandate.v2.SequenceType toDto(final SequenceType sequenceType) {
        if(sequenceType == null) {
            return null;
        }
        switch (sequenceType) {
        case FIRST:
            return org.estatio.canonical.bankmandate.v2.SequenceType.FIRST;
        case RECURRENT:
            return org.estatio.canonical.bankmandate.v2.SequenceType.RECURRENT;
        default:
            // shouldn't happen, above switch is complete.
            throw new IllegalArgumentException(String.format(
                    "Sequence type '%s' not recognized.", sequenceType));
        }
    }

    private static org.estatio.canonical.bankmandate.v2.Scheme toDto(final Scheme scheme) {
        if(scheme == null) {
            return null;
        }
        switch (scheme) {
        case CORE:
            return org.estatio.canonical.bankmandate.v2.Scheme.CORE;
        case B2B:
            return org.estatio.canonical.bankmandate.v2.Scheme.B_2_B;
        default:
            // shouldn't happen, above switch is complete.
            throw new IllegalArgumentException(String.format(
                    "Scheme '%s' not recognized.", scheme));
        }
    }

    // TODO: We've added a suffix because agreement names must be unique, remove after closing https://incodehq.atlassian.net/browse/EST-684
    static String fixup(final String reference) {
        if (reference.endsWith("-M")){
            return reference.substring(0,reference.length()-2);
        }
        return reference;
    }

}
