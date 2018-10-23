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

import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.capex.dom.order.OrderRepository;
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
        correctComessaIfNecessary();

        if (getCodiceFornitore() != null && getFornitore() != null)
            importSeller();
        if (getNumero() != null && getCentro() != null)
            importOrder();
        if (deriveProjectReference() != null && deriveChargeReference() != null)
            createProjectItemIfNotAlready();

        return this;
    }

    private void importOrder() {
        OrderImport newLine = getOrderImport();
        serviceRegistry2.injectServicesInto(newLine);
        newLine.importData(null);
    }

    private OrderImport getOrderImport() {
        return new OrderImport(
                getCentro(),
                "ITA_ORDER_INVOICE",
                deriveOrderNumber(),
                null,
                getData(),
                getData(),
                getCodiceFornitore(),
                "IT01",
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
    }

    private void importIntegrazioneLine() {
        final Order order = orderRepository.findByOrderNumber(deriveOrderNumber());

        if (order == null)
            throw new IllegalStateException(String.format("Trying to add line to invoice number %s", deriveOrderNumber()));

        final OrderImport newLine = getOrderImport();
        serviceRegistry2.injectServicesInto(newLine);

        // todo
    }

    public String deriveOrderNumber() {
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
            if (getProgressivoCentro() != null) {
                builder.append(getProgressivoCentro());
            }
            builder.append("/");
            if (getCommessa() != null) {
                builder.append(getCommessa());
            }
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

    private void correctComessaIfNecessary() {
        if (getCommessa() != null && getCommessa().trim().endsWith("R")) {
            setCommessa(getCommessa().trim().replace("R", ""));
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
            return ProjectImportAdapter.ITA_PROJECT_PREFIX + getCommessa();
        return ProjectImportAdapter.deriveProjectReference(getCommessa(), getCentro());
    }

    private LocalDate deriveStartDate() {
        return getData() == null ? null : getData().withMonthOfYear(1).withDayOfMonth(1);
    }

    private LocalDate deriveEndDate() {
        return deriveStartDate() == null ? null : deriveStartDate().plusYears(1).minusDays(1);
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

    @Inject
    private OrderRepository orderRepository;

}

