package org.estatio.module.financial.dom;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.Party;

public class BankAccount_friendlyName_Test {

    private BankAccount bankAccount;
    private Party stubParty = new Organisation("OWNER");

    @Before
    public void setUp() throws Exception {

        bankAccount = new BankAccount();
        bankAccount.setOwner(stubParty);
    }

    @Test
    public void name_same_as_iban_extra_chars_at_end() throws Exception {

        assertThatWhenNameUnchangedThen("FR7630004029990001016450273XXXX", "FR76 3000 4029 9900 0101 6450 273X XXX - OWNER");
    }

    @Test
    public void name_same_as_iban_typical_case() throws Exception {

        assertThatWhenNameUnchangedThen("FR7630004029990001016450273", "FR76 3000 4029 9900 0101 6450 273 - OWNER");
    }

    @Test
    public void name_same_as_iban_a_bit_shorter() throws Exception {

        assertThatWhenNameUnchangedThen("FR76300040299900010164", "FR76 3000 4029 9900 0101 64 - OWNER");
    }

    @Test
    public void name_same_as_iban_five_chars() throws Exception {

        assertThatWhenNameUnchangedThen("FR763", "FR76 3 - OWNER");
    }

    @Test
    public void name_same_as_iban_three_chars() throws Exception {

        assertThatWhenNameUnchangedThen("FR7", "FR7 - OWNER");
    }

    @Test
    public void name_different_to_iban() throws Exception {

        // given
        bankAccount.setName("ne plus utilier: FR7630004029990001016450273");
        bankAccount.setIban("FR7630004029990001016450273");

        // when
        final String friendlyName = bankAccount.friendlyName();

        // then
        Assertions.assertThat(friendlyName).isEqualTo("ne plus utilier: FR7630004029990001016450273 - OWNER");
    }

    private void assertThatWhenNameUnchangedThen(final String iban, final String expected) {

        // given
        bankAccount.setName(iban);
        bankAccount.setIban(iban);

        // when
        final String friendlyName = bankAccount.friendlyName();

        // then
        Assertions.assertThat(friendlyName).isEqualTo(expected);
    }



}