package org.estatio.module.financial.canonical.v1;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.apache.isis.applib.services.jaxb.JaxbService;

import org.estatio.module.financial.dom.BankAccount;

public class BankAccountDtoFactory_Test {

    private JaxbService.Simple jaxbService = new JaxbService.Simple();

    private BankAccountDtoFactory bankAccountDtoFactory;

    @Before
    public void setUp() throws Exception {
        bankAccountDtoFactory = new BankAccountDtoFactory();
    }


    @Ignore
    @Test
    public void happy_case() throws Exception {

        BankAccount ba = new BankAccount();


    }
}