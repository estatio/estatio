package org.estatio.capex.dom.impmgr;

import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;

import org.estatio.dom.financial.bankaccount.BankAccount;
import org.estatio.dom.financial.bankaccount.BankAccountRepository;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyRepository;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.capex.dom.impmgr.BankAccountImportLine"
)
public class BankAccountImportLine {

    public BankAccountImportLine(){}

    public BankAccountImportLine(
            final String elmcode,
            final String bankName,
            final String add1,
            final String add2,
            final String postcode,
            final String country,
            final String acnum,
            final String swift,
            final String iban) {
        this.elmcode = elmcode;
        this.acname = bankName;
        this.add1 = add1;
        this.add2 = add2;
        this.postcode = postcode;
        this.country = country;
        this.acnum = acnum;
        this.swift = swift;
        this.iban = iban;
    }

    @Getter @Setter
    public String elmcode; // seller ref

    @Getter @Setter
    private String acname; // bank name

    @Getter @Setter
    private String add1; // bank address

    @Getter @Setter
    private String add2; // bank city

    @Getter @Setter
    private String postcode;

    @Getter @Setter
    private String country;

    @Getter @Setter
    private String acnum; // ??

    @Getter @Setter
    private String swift; // use as party refence for bank TODO: check if OK

    @Getter @Setter
    private String iban;

    private static final Logger LOG = LoggerFactory.getLogger(BankAccountImportLine.class);



    public List<Object> importLine() {

        Party bankParty = supplierImportService.findOrCreateOrganisationAndAddressByReference(
                getSwift(),
                getAcname(),
                getAdd1(),
                getPostcode(),
                getAdd2(),
                getCountry());

        Party owner = partyRepository.findPartyByReference(getElmcode());

        if (owner == null){
            String message = String.format("No party found for %s while trying to create bank account", getElmcode());
            LOG.debug(message);
            return null;
        }

        BankAccount bankAccount = bankAccountRepository.findBankAccountByReference(owner, getAcname());


        if (bankAccount == null){

            bankAccount = bankAccountRepository.newBankAccount(owner, getIban(), getSwift());
            bankAccount.setBank(bankParty);

        } else {

            bankAccount.setBank(bankParty);

        }

        return Lists.newArrayList(bankAccount);

    }

    @Inject
    SupplierImportService supplierImportService;
    @Inject
    BankAccountRepository bankAccountRepository;
    @Inject
    PartyRepository partyRepository;

}
