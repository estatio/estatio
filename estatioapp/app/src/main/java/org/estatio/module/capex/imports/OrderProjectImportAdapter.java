package org.estatio.module.capex.imports;

import java.math.BigDecimal;
import java.util.Set;

import javax.annotation.Nullable;
import javax.inject.Inject;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;

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
    private String commessa;

    @Getter @Setter @Nullable
    private String workType;

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

    public OrderProjectImportAdapter handle(final OrderProjectImportAdapter previousRow) {
        correctCodiceFornitoreIfNecessary();

        if (previousRow != null && getNumero() != null && previousRow.getNumero() != null && getNumero().equals(previousRow.getNumero())) {
            mergeIntegrazioneLines(previousRow);
        }

        if (getCodiceFornitore() != null && getFornitore() != null)
            importSeller();
        if (getNumero() != null && getCentro() != null)
            importOrder();
        if (deriveProjectReference() != null && deriveChargeReference() != null)
            createProjectItemIfNotAlready();
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

    private void mergeIntegrazioneLines(final OrderProjectImportAdapter previousLine) {
        // update oggetto
        final String newOggetto = mergeOggettos(previousLine);
        setOggetto(newOggetto);

        // keep integrazione of this line
        // keep date of this line TODO: is this desirable?

        // update importo netto iva
        if (previousLine.getImportoNettoIVA() != null && getImportoNettoIVA() != null) {
            setImportoNettoIVA(previousLine.getImportoNettoIVA().add(getImportoNettoIVA()));
        } else if (previousLine.getImportoNettoIVA() != null && getImportoNettoIVA() == null) {
            setImportoNettoIVA(previousLine.getImportoNettoIVA());
        }

        // update cassaProfess
        if (previousLine.getCassaProfess() != null && getCassaProfess() != null) {
            setCassaProfess(previousLine.getCassaProfess().add(getCassaProfess()));
        } else if (previousLine.getCassaProfess() != null && getCassaProfess() == null) {
            setCassaProfess(previousLine.getCassaProfess());
        }

        // update importo totale
        if (previousLine.getImportoTotale() != null && getImportoTotale() != null) {
            setImportoTotale(previousLine.getImportoTotale().add(getImportoTotale()));
        } else if (previousLine.getImportoTotale() != null && getImportoTotale() == null) {
            setImportoTotale(previousLine.getImportoTotale());
        }

        // update iva
        if (previousLine.getIva() != null && getIva() != null) {
            setIva(previousLine.getIva().add(getIva()));
        } else if (previousLine.getIva() != null && getIva() == null) {
            setIva(previousLine.getIva());
        }

        // update totale con iva
        if (previousLine.getTotaleConIVA() != null && getTotaleConIVA() != null) {
            setTotaleConIVA(previousLine.getTotaleConIVA().add(getTotaleConIVA()));
        } else if (previousLine.getTotaleConIVA() != null && getTotaleConIVA() == null) {
            setTotaleConIVA(previousLine.getTotaleConIVA());
        }

    }

    private String mergeOggettos(final OrderProjectImportAdapter predecessor) {
        Set<String> previousOggetto = Sets.newLinkedHashSet(Splitter.on(" ").split(predecessor.getOggetto()));
        Set<String> currentOggetto = Sets.newLinkedHashSet(Splitter.on(" ").split(getOggetto()));
        return String.join(" ", Sets.union(previousOggetto, currentOggetto));
    }

    // TODO: add new order number format
    public String deriverOrderNumber() {
        if (getNumero() == null)
            return null;
        StringBuilder builder = new StringBuilder();
        builder.append(getNumero().toString());
        builder.append("/");
        if (getCentro() != null)
            builder.append(getCentro());
        builder.append("/");

        if (getProgressivoCentro() != null && getCommessa() != null) {
            builder.append(getProgressivoCentro().toString());
            builder.append("/");
            builder.append(getCommessa());
        } else if (getCommessa() != null && getWorkType() != null) {
            builder.append(getCommessa());
            builder.append("/");
            builder.append(getWorkType());
        } else {
            builder.append("/");
        }

        return builder.toString();
    }

    private void importSeller() {
        OrganisationImport organisationImport = new OrganisationImport();
        serviceRegistry2.injectServicesInto(organisationImport);
        organisationImport.setName(clean(getFornitore()));
        organisationImport.setAtPath("/ITA");
        organisationImport.setReference(getCodiceFornitore());
        organisationImport.importData(null);
    }

    private void createProjectItemIfNotAlready() {
        Project project = projectRepository.findByReference(deriveProjectReference());
        if (project == null) {
            LOG.error(String.format("Project not found for order number %s and project reference %s", getNumero().toString(), deriveProjectReference()));
            return;
        }
        Charge charge = chargeRepository.findByReference(deriveChargeReference());
        if (charge == null) {
            LOG.error(String.format("Charge not found for order number %s and charge reference %s", getNumero().toString(), deriveChargeReference()));
            return;
        }
        projectItemRepository.findOrCreate(project, charge, charge.getName(), null, null, null, null, null);
    }

    private void correctCodiceFornitoreIfNecessary() {
        if (getCodiceFornitore() != null && !getCodiceFornitore().startsWith("IT")) {
            setCodiceFornitore("IT" + getCodiceFornitore());
        }
    }

    private String deriveChargeReference() {
        if (getWorkType() == null)
            return null;

        if (getWorkType().length() == 3) { // TODO: bit hacky, but new work types in ORDINI ECP are only 3 character work types
            Charge newCharge = chargeRepository.findByReference(IncomingChargeImportAdapter.ITA_WORKTYPE_PREFIX + getWorkType());

            if (newCharge == null)
                return null;

            return newCharge.getExternalReference() != null ? newCharge.getExternalReference() : newCharge.getReference();
        } else {
            Charge oldCharge = chargeRepository.findByReference(IncomingChargeImportAdapter.ITA_OLD_WORKTYPE_PREFIX + workTypeCodeFromNo(getWorkType()));

            if (oldCharge == null)
                return null;

            return oldCharge.getExternalReference() != null ? oldCharge.getExternalReference() : oldCharge.getReference();
        }
    }

    private String workTypeCodeFromNo(final String worktype) {
        if (worktype.length() == 1)
            return "00".concat(worktype);
        if (worktype.length() == 2)
            return "0".concat(worktype);
        return worktype;
    }

    private String deriveProjectReference() {
        if (getCommessa() == null)
            return null;
        if (getCentro() == null)
            return ProjectImportAdapter.ITA_PROJECT_PREFIX + getCommessa().toString();
        return ProjectImportAdapter.deriveProjectReference(getCommessa(), getCentro());
    }

    private LocalDate deriveStartDate() {
        if (getData() == null)
            return null;
        return getData().getMonthOfYear() < 7 ?
                new LocalDate(getData().getYear() - 1, 7, 1) :
                new LocalDate(getData().getYear(), 7, 1);
    }

    private LocalDate deriveEndDate() {
        if (deriveStartDate() == null)
            return null;
        return deriveStartDate().plusYears(1).minusDays(1);
    }

    private String clean(final String input) {
        if (input == null) {
            return null;
        }
        String result = input.trim();
        return result.trim();
    }

    String limitLength(final String input, final int length) {
        if (input == null)
            return input;
        if (input.length() <= length) {
            return input;
        } else {
            return input.substring(0, length);
        }
    }

    @Override
    public void handleRow(final OrderProjectImportAdapter previousRow) {
        if (executionContext != null && excelFixture2 != null) {
            if (executionContext.getParameterAsBoolean("testMode") != null && executionContext.getParameterAsBoolean("testMode")) {
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

