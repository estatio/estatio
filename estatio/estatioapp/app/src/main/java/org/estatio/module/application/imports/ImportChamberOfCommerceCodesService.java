package org.estatio.module.application.imports;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;
import org.isisaddons.module.excel.dom.WorksheetSpec;

import org.estatio.module.agreement.dom.AgreementRoleRepository;
import org.estatio.module.agreement.dom.role.AgreementRoleType;
import org.estatio.module.agreement.dom.role.AgreementRoleTypeRepository;
import org.estatio.module.asset.dom.Property;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;
import org.estatio.module.lease.dom.LeaseStatus;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.OrganisationRepository;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.imports.ChamberOfCommerceImportLine;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "org.estatio.module.application.imports.ImportChamberOfCommerceCodesService"
)
public class ImportChamberOfCommerceCodesService {

    private static final Logger LOG = LoggerFactory.getLogger(ImportChamberOfCommerceCodesService.class);

    @Action(semantics = SemanticsOf.SAFE)
    public Blob downloadManualChamberOfCommerceCodeEntrySheet(final String fileName) {
        final List<ChamberOfCommerceImportLine> toCompleteForFra = getLinesForAtPath("/FRA");
        final List<ChamberOfCommerceImportLine> toCompleteForBel = getLinesForAtPath("/BEL");

        return excelService.toExcel(ListUtils.union(toCompleteForFra, toCompleteForBel), ChamberOfCommerceImportLine.class, "CoCCodeImport", fileName.concat(".xlsx"));
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public void importChamberOfCommerceCodes(final Blob sheet) {
        WorksheetSpec spec = new WorksheetSpec(ChamberOfCommerceImportLine.class, "CoCCodeImport");
        List<ChamberOfCommerceImportLine> lines = excelService.fromExcel(sheet, spec);
        for (ChamberOfCommerceImportLine line : lines) {
            Organisation org = (Organisation) partyRepository.findPartyByReference(line.getReference());
            if (org == null) {
                LOG.error(String.format("No organisation found for reference %s", line.getReference()));
            } else {
                org.setChamberOfCommerceCode(line.getCode());
            }
        }
    }

    private List<ChamberOfCommerceImportLine> getLinesForAtPath(final String atPath) {
        return organisationRepository.findByAtPathMissingChamberOfCommerceCode(atPath)
                .stream()
                .filter(org ->
                        org.getRoles().isEmpty() ||
                                org.hasPartyRoleType(LeaseAgreementRoleTypeEnum.TENANT) ||
                                incomingInvoiceRepository.findBySellerAndApprovalStates(org, Arrays.asList(IncomingInvoiceApprovalState.values())).isEmpty()
                )
                .map(org -> {
                    AgreementRoleType tenantType = agreementRoleTypeRepository.find(LeaseAgreementRoleTypeEnum.TENANT);
                    String propertiesIfAnyWithLeaseStatus = agreementRoleRepository.findByPartyAndType(org, tenantType)
                            .stream()
                            .filter(role->role.getAgreement().getClass().isAssignableFrom(Lease.class))
                            .map(role -> ((Lease) role.getAgreement()))
                            .map(lease -> {
                                LeaseStatus status = lease.getStatus();
                                Property propertyIfAny = lease.getProperty();

                                return propertyIfAny != null ?
                                        String.format("%s [%s]", propertyIfAny.getName(), status.title()) :
                                        status.title();
                            })
                            .distinct()
                            .collect(Collectors.joining(", "));

                    return new ChamberOfCommerceImportLine(org.getReference(), org.getName(), propertiesIfAnyWithLeaseStatus);
                })
                .collect(Collectors.toList());
    }

    @Inject ExcelService excelService;

    @Inject PartyRepository partyRepository;

    @Inject OrganisationRepository organisationRepository;

    @Inject IncomingInvoiceRepository incomingInvoiceRepository;

    @Inject AgreementRoleTypeRepository agreementRoleTypeRepository;

    @Inject AgreementRoleRepository agreementRoleRepository;

}
