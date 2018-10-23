package org.estatio.module.party.imports;

import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.excel.dom.ExcelFixture;
import org.isisaddons.module.excel.dom.ExcelFixtureRowHandler;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.estatio.module.base.dom.Importable;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRoleTypeEnum;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.OrganisationRepository;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.dom.role.PartyRoleRepository;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.dom.viewmodels.OrganisationImport"
)
public class OrganisationImport implements ExcelFixtureRowHandler, Importable {

    private static final Logger LOG = LoggerFactory.getLogger(OrganisationImport.class);

    @Getter @Setter
    private String atPath;

    @Getter @Setter
    private String reference;

    @Getter @Setter
    private String name;

    @Getter @Setter
    private String fiscalCode;

    @Getter @Setter
    private String vatCode;

    @Getter @Setter
    private String chamberOfCommerceCode;

//    @Override
//    public List<Class> importAfter() {
//        return Lists.newArrayList();
//    }

    @Override
    public List<Object> handleRow(final FixtureScript.ExecutionContext executionContext, final ExcelFixture excelFixture, final Object previousRow) {
        return importData(previousRow);
    }

    @Override
    public List<Object> importData(final Object previousRow) {

        final ApplicationTenancy applicationTenancy = applicationTenancyRepository.findByPath(atPath);
        Organisation org = (Organisation) partyRepository.findPartyByReference(reference);
        if (org == null) {
            try {
                if (applicationTenancy == null){
                    throw new IllegalArgumentException("atPath not found");
                }
                org = organisationRepository.newOrganisation(reference, false, name, applicationTenancy);
            } catch (Exception e) {
                LOG.error("Error importing organisation : " + reference, e);
            }
        }
        org.setApplicationTenancyPath(atPath);
        org.setName(name);
        org.setFiscalCode(fiscalCode);
        org.setVatCode(vatCode);
        org.addRole(IncomingInvoiceRoleTypeEnum.SUPPLIER);
        if (chamberOfCommerceCode != null && !chamberOfCommerceCode.matches("[ \t]+")) {
            org.setChamberOfCommerceCode(chamberOfCommerceCode.replace(" ",""));
        }

        return Lists.newArrayList(org);

    }

    @Inject
    private PartyRepository partyRepository;

    @Inject
    private OrganisationRepository organisationRepository;
    
    @Inject
    private ApplicationTenancyRepository applicationTenancyRepository;

    
}
