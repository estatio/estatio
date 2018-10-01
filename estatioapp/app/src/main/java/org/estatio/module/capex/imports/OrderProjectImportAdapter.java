package org.estatio.module.capex.imports;

import java.math.BigDecimal;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.isisaddons.module.excel.dom.ExcelFixture2;
import org.isisaddons.module.excel.dom.ExcelMetaDataEnabled;
import org.isisaddons.module.excel.dom.FixtureAwareRowHandler;

import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.capex.dom.project.ProjectItemRepository;
import org.estatio.module.capex.dom.project.ProjectRepository;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.party.imports.OrganisationImport;

import lombok.Getter;
import lombok.Setter;

public class OrderProjectImportAdapter implements FixtureAwareRowHandler<OrderProjectImportAdapter>, ExcelMetaDataEnabled {

    private static final Logger LOG = LoggerFactory.getLogger(OrderProjectImportAdapter.class);

    @Getter @Setter @Nullable
    private String excelSheetName;

    @Getter @Setter @Nullable
    private Integer excelRowNumber;

    @Getter @Setter @Nullable
    private Integer numero;

    @Getter @Setter @Nullable
    private String centro;

    @Getter @Setter @Nullable
    private Integer progressivoCentro;

    @Getter @Setter @Nullable
    private Integer commessa;

    @Getter @Setter @Nullable
    private Integer workType;

    @Getter @Setter @Nullable
    private Integer integrazione;

    @Getter @Setter @Nullable
    private LocalDate data;

    @Getter @Setter @Nullable
    private String oggetto;

    @Getter @Setter @Nullable
    private String fornitore;

    @Getter @Setter @Nullable
    private String codiceFornitore;

    @Getter @Setter @Nullable
    private BigDecimal importoNettoIVA;

    @Getter @Setter @Nullable
    private BigDecimal cassaProfess;

    @Getter @Setter @Nullable
    private BigDecimal importoTotale;

    @Getter @Setter @Nullable
    private BigDecimal iva;

    @Getter @Setter @Nullable
    private BigDecimal totaleConIVA;

    @Getter @Setter @Nullable
    private String autorizzato;

    @Getter @Setter @Nullable
    private String note;


    /**
     * To allow for usage within fixture scripts also.
     */
    @Setter
    private FixtureScript.ExecutionContext executionContext;

    /**
     * To allow for usage within fixture scripts also.
     */
    @Setter
    private ExcelFixture2 excelFixture2;

    public OrderProjectImportAdapter handle(final OrderProjectImportAdapter previousRow){
        if (getCodiceFornitore()!=null && getFornitore()!=null) importSeller();
        if (getNumero()!=null && getCentro()!=null) importOrder();
        if (deriveProjectReference()!=null && deriveChargeReference()!=null) createProjectItemIfNotAlready();
        return this;
    }

    private void importOrder() {
        OrderImport newLine = new OrderImport(
                getCentro(),
                "CAPEX",
                deriverOrderNumber(),
                null,
                getData(),
                getData(),
                getCodiceFornitore(),
                null,
                "/ITA",
                "APPROVED",
                getAutorizzato(),
                getData(),
                deriveChargeReference(),
                limitLength(clean(getOggetto()), 254),
                getImportoTotale(),
                getIva(),
                getTotaleConIVA(),
                deriveStartDate(), // TODO: ask users concerning the period (how to derive the financial year)
                deriveEndDate(),
                getCentro(),
                deriveProjectReference()
        );
        serviceRegistry2.injectServicesInto(newLine);
        newLine.importData(null);
    }

    public String deriverOrderNumber(){
        return getNumero()!=null ? getNumero().toString() : null;
    }

    private void importSeller() {
        OrganisationImport organisationImport = new OrganisationImport();
        serviceRegistry2.injectServicesInto(organisationImport);
        organisationImport.setName(clean(getFornitore()));
        organisationImport.setAtPath("/ITA");
        organisationImport.setReference(getCodiceFornitore());
        organisationImport.importData(null);
    }

    private void createProjectItemIfNotAlready(){
        Project project = projectRepository.findByReference(deriveProjectReference());
        if (project==null) {
            LOG.error(String.format("Project not found for order number %s and project reference %s", getNumero().toString(), deriveProjectReference()));
            return;
        }
        Charge charge = chargeRepository.findByReference(deriveChargeReference());
        if (charge==null) {
            LOG.error(String.format("Charge not found for order number %s and charge reference %s", getNumero().toString(), deriveChargeReference()));
            return;
        }
        projectItemRepository.findOrCreate(project, charge, charge.getName(), null, null, null, null,null);
    }

    private String deriveChargeReference(){
        if (getWorkType()==null) return null;
        Charge oldCharge = chargeRepository.findByReference(IncomingChargeImportAdapter.ITA_OLD_WORKTYPE_PREFIX + workTypeCodeFromNo(getWorkType()));
        return oldCharge.getExternalReference()!=null ? oldCharge.getExternalReference() : oldCharge.getReference();
    }

    private String workTypeCodeFromNo(final Integer worktype){
        if (worktype<10) return "00".concat(worktype.toString());
        if (worktype<100) return "0".concat(worktype.toString());
        return worktype.toString();
    }

    private String deriveProjectReference(){
        if (getCommessa()==null) return null;
        return ProjectImportAdapter.ITA_PROJECT_PREFIX + getCommessa().toString();
    }

    private LocalDate deriveStartDate(){
        if (getData()==null) return null;
        return getData().getMonthOfYear() < 7 ?
                new LocalDate(getData().getYear()-1, 7, 1) :
                new LocalDate(getData().getYear(), 7, 1);
    }

    private LocalDate deriveEndDate(){
        if (deriveStartDate()==null) return null;
        return deriveStartDate().plusYears(1).minusDays(1);
    }

    private String clean(final String input){
        if (input==null){
            return null;
        }
        String result = input.trim();
        return result.trim();
    }

    String limitLength(final String input, final int length) {
        if (input==null) return input;
        if (input.length()<=length){
            return input;
        } else {
            return input.substring(0, length);
        }
    }

    @Override
    public void handleRow(final OrderProjectImportAdapter previousRow) {
        if(executionContext != null && excelFixture2 != null) {
            if (executionContext.getParameterAsBoolean("testMode")!=null && executionContext.getParameterAsBoolean("testMode")){
                executionContext.addResult(excelFixture2, this.handle(previousRow));
            } else {
                this.handle(previousRow);
            }
        }
    }

    @Inject ServiceRegistry2 serviceRegistry2;

    @Inject ProjectRepository projectRepository;

    @Inject ProjectItemRepository projectItemRepository;

    @Inject ChargeRepository chargeRepository;

}

