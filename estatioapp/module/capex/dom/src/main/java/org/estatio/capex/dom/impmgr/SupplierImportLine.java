package org.estatio.capex.dom.impmgr;

import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.services.message.MessageService;

import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.dom.impl.CountryRepository;

import org.estatio.dom.Importable;
import org.estatio.module.bankaccount.dom.BankAccountRepository;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.OrganisationRepository;
import org.estatio.module.party.dom.PartyRepository;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.capex.dom.impmgr.SupplierImportLine"
)
public class SupplierImportLine implements Importable {

    public SupplierImportLine(){}

    public SupplierImportLine(
            final String supplierName,
            final String chamberOfCommerceCode,
            final String iban,
            final String bic,
            final String country) {
        this.supplierName = supplierName;
        this.chamberOfCommerceCode = chamberOfCommerceCode;
        this.iban = iban;
        this.bic = bic;
        this.country = country;
    }

    @Getter @Setter
    public String supplierName;

    @Getter @Setter
    private String chamberOfCommerceCode;

    @Getter @Setter
    private String iban;

    @Getter @Setter
    private String bic;

    @Getter @Setter
    private String country;

    private String message;

    @Override
    public List<Object> importData(Object previousRow) {

        SupplierImportLine previous = (SupplierImportLine) previousRow;
        if (getSupplierName()==null && getChamberOfCommerceCode()==null){
            setSupplierName(previous.getSupplierName());
            setChamberOfCommerceCode(previous.getChamberOfCommerceCode());
        }
        if (getCountry()==null){
            setCountry(previous.getCountry());
        }

        final Country countryObj = countryRepository.findCountry(getCountry());

        this.message = "";
        Organisation organisation;
        organisation = findExistingOrganisation();

        if (bankAccountRepository.findByReference(getIban()).size()>0) {
            message = message.concat(String.format("More than one iban found for %s. ", getIban()));
            // only set chamber of commerce code - do not create organisation or bank account
            setChamberOfCommerceCodeIfEmpty(organisation);
        } else {
            if (organisation == null) {
                organisation = organisationRepository.newOrganisation(null, true, getSupplierName(), countryObj);
            }
            bankAccountRepository.newBankAccount(organisation, getIban(), getBic());
            setChamberOfCommerceCodeIfEmpty(organisation);
        }

        if (message!=""){
            messageService.warnUser(message);
        }

        return Lists.newArrayList(message);

    }

    private Organisation findExistingOrganisation(){
        Organisation organisation = null;
        if (getChamberOfCommerceCode()!=null){
            organisation = organisationRepository.findByChamberOfCommerceCode(getChamberOfCommerceCode());
        }
        if (organisation==null && partyRepository.findParties(getSupplierName()).size()>0){
            organisation = (Organisation) partyRepository.findParties(getSupplierName()).get(0);
            if (partyRepository.findParties(getSupplierName()).size()>1){
                message = message.concat(String.format("More than one supplier found for %s; first found is taken. ", getSupplierName()));
            }
        }
        return organisation;
    }

    private void setChamberOfCommerceCodeIfEmpty(final Organisation organisation){
        if (organisation != null) {
            if (organisation.getChamberOfCommerceCode() == null) {
                organisation.setChamberOfCommerceCode(this.getChamberOfCommerceCode());
            } else {
                if (!organisation.getChamberOfCommerceCode().equals(this.getChamberOfCommerceCode())) {
                    message = message.concat(String.format("ChamberOfCommercode %s in upload is different from value %s2 in system. ", this.getChamberOfCommerceCode(), organisation.getChamberOfCommerceCode()));
                }
            }
        }
    }

    @Inject OrganisationRepository organisationRepository;
    @Inject PartyRepository partyRepository;
    @Inject CountryRepository countryRepository;
    @Inject BankAccountRepository bankAccountRepository;
    @Inject MessageService messageService;

}
