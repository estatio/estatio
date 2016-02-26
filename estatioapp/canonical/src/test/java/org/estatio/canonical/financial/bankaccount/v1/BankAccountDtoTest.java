package org.estatio.canonical.financial.bankaccount.v1;

import javax.xml.bind.JAXBContext;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.schema.common.v1.BookmarkObjectState;
import org.apache.isis.schema.common.v1.OidDto;

import org.isisaddons.module.fakedata.dom.FakeDataService;

import org.estatio.canonical.financial.v1.BankAccountDto;

public class BankAccountDtoTest {

    private FakeDataService fake;
    private JaxbMarshaller jaxbMarshaller;

    @Before
    public void setUp() throws Exception {
        fake = new FakeDataService();
        fake.init();

        jaxbMarshaller = new JaxbMarshaller();
    }

    @Test
    public void roundtrip() throws Exception {

        // given
        final String accountNumber = "ACCT: " + fake.strings().digits(10);
        final String branchCode = randomBranchCode(fake);
        final String bankId = "" + fake.ints().any();
        final String externalReference = fake.strings().fixed(12);
        final String iban = "IBAN: " + fake.strings().digits(12);
        final String name = fake.name().fullName();
        final String nationalBankCode = "BANK CODE: " + fake.strings().fixed(6);
        final String nationalCheckCode = "CHECK CODE: " + fake.strings().fixed(1);
        final String ownerId = "" + fake.ints().any();
        final String reference = fake.strings().fixed(8);

        final BankAccountDto dto = new BankAccountDto();
        dto.setAccountNumber(accountNumber);
        dto.setBank(newOidDto("Bank", bankId));
        dto.setBranchCode(branchCode);
        dto.setExternalReference(externalReference);
        dto.setIban(iban);
        dto.setName(name);
        dto.setNationalBankCode(nationalBankCode);
        dto.setNationalCheckCode(nationalCheckCode);
        dto.setOwner(newOidDto("Party", ownerId));
        dto.setReference(reference);

        // when
        final String xml = jaxbMarshaller.toXml(dto);

        // then
        System.out.println(xml);
        final JAXBContext context = JAXBContext.newInstance(BankAccountDto.class);

        final BankAccountDto dtoAfter = (BankAccountDto) jaxbMarshaller.fromXml(context, xml);

        // when
        final String xmlAfter = jaxbMarshaller.toXml(dtoAfter);

        // then
        Assertions.assertThat(xmlAfter).isEqualTo(xml);

    }

    static String randomBranchCode(final FakeDataService fakeDataService) {
        return fakeDataService.strings().digits(2) + "-" + fakeDataService.strings().digits(2) + "-" + fakeDataService
                .strings().digits(2);
    }

    static OidDto newOidDto(
            final String type,
            final String identifier) {
        final OidDto oidDto = new OidDto();
        oidDto.setObjectType(type);
        oidDto.setObjectIdentifier(identifier);
        oidDto.setObjectState(BookmarkObjectState.PERSISTENT);
        return oidDto;
    }

}