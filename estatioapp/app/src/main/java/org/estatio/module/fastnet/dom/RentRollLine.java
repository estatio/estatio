package org.estatio.module.fastnet.dom;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.Indices;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Uniques;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.incode.module.base.dom.utils.TitleBuilder;

import org.estatio.module.base.dom.Importable;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseRepository;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "fastnet",
        table = "RentRollLine"
)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.IDENTITY,
        column = "id")
@Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@Queries({
        @Query(
                name = "findByKeyToLeaseExternalReference", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.fastnet.dom.RentRollLine "
                        + "WHERE keyToLeaseExternalReference == :keyToLeaseExternalReference "
                        + "ORDER BY exportDate DESC"),
        @Query(
                name = "findByObjektsNummerAndEvdInSd", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.fastnet.dom.RentRollLine "
                        + "WHERE objektsNummer == :objektsNummer && "
                        + "evdInSd == :evdInSd "),
        @Query(
                name = "findByExportDate", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.fastnet.dom.RentRollLine "
                        + "WHERE exportDate == :exportDate "),
        @Query(
                name = "findUniqueExportDates",
                value = "SELECT DISTINCT exportDate "
                        + "FROM org.estatio.module.fastnet.dom.RentRollLine "
                        + "ORDER BY exportDate DESC")
})
@Indices({
        @Index(name = "RentRollLine_kontraktNr_IDX", members = { "kontraktNr" }),
        @Index(name = "RentRollLine_objektsNummer_IDX", members = { "objektsNummer" })
})
@Uniques({
        @Unique(
                name = "RentRollLine_objektsNummer_evdInSd_UNQ",
                members = { "objektsNummer", "evdInSd" }
        )
})
@DomainObject(
        editing = Editing.DISABLED,
        objectType = "org.estatio.module.fastnet.dom.RentRollLine"
)
public class RentRollLine implements Importable {

    public RentRollLine() {
    }

    public String title(){
        return TitleBuilder.start()
                .withReference(getKeyToLeaseExternalReference())
                .withName(getExportDate())
                .toString();
    }

    @Getter @Setter
    @Column(allowsNull = "false")
    private String status;

    @Getter @Setter
    @Column(allowsNull = "false")
    private String klientNummer;

    @Getter @Setter
    @Column(allowsNull = "false")
    private String klientNamn;

    @Getter @Setter
    @Column(allowsNull = "false")
    private String fastighetsNummer;

    @Getter @Setter
    @Column(allowsNull = "false")
    private String fastighetsBeteckning;

    @Getter @Setter
    @Column(allowsNull = "false")
    private String objektsNummer;

    // lease number --> lease#externalReference
    @Getter @Setter
    @Column(allowsNull = "true")
    private String kontraktNr;

    @Getter @Setter
    @Column(allowsNull = "true")
    private String keyToLeaseExternalReference;

    @Getter @Setter
    @Column(allowsNull = "true")
    private String kundNr;

    @Getter @Setter
    @Column(allowsNull = "true")
    private String forvaltaransvarig;

    @Getter @Setter
    @Column(allowsNull = "false")
    private String objektTyp;

    @Getter @Setter
    @Column(allowsNull = "false")
    private String objektTypKod;

    @Getter @Setter
    @Column(allowsNull = "false")
    private String beskrivning;

    @Getter @Setter
    @Column(allowsNull = "false")
    @PropertyLayout(named = "TYPE_OF_PREMISES")
    private String typeOfPremises;

    @Getter @Setter
    @Column(allowsNull = "true")
    private BigDecimal yta;

    @Getter @Setter
    @Column(allowsNull = "true")
    private String betpertyp;

    @Getter @Setter
    @Column(allowsNull = "true")
    private BigDecimal periodhyra;

    @Getter @Setter
    @Column(allowsNull = "true")
    private BigDecimal arshyra;

    @Getter @Setter
    @Column(allowsNull = "true")
    @PropertyLayout(named = "ARSHYRA_PER_KVM")
    private BigDecimal arshyraPerKvm;

    @Getter @Setter
    @Column(allowsNull = "true")
    private BigDecimal arsmoms;

    @Getter @Setter
    @Column(allowsNull = "true")
    private String hyresgast;

    @Getter @Setter
    @Column(allowsNull = "true")
    private String uppsagd;

    @Getter @Setter
    @Column(allowsNull = "true")
    // NOTE: We take the string here because the excel import file we have to handle does not use the date format
    private String inflyttningsDatum;

    // NOTE: Example - this is a kind of work-a-round we could use for all dates imported as string
    public LocalDate getInflyttningsDatumAsDate() {
        return stringToDate(getInflyttningsDatum());
    }

    @Getter @Setter
    @Column(allowsNull = "true")
    private String avflyttningsDatum;

    @Getter @Setter
    @Column(allowsNull = "true")
    private String senastuppsagd;

    @Getter @Setter
    @Column(allowsNull = "true")
    private String kontraktFrom;

    @Getter @Setter
    @Column(allowsNull = "true")
    private String kontraktTom;

    @Getter @Setter
    @Column(allowsNull = "true")
    @PropertyLayout(named = "UPPSAGNINGSTID_HV")
    private String uppsagningstidHv;

    @Getter @Setter
    @Column(allowsNull = "true")
    @PropertyLayout(named = "FORLANGNINGSTID_HV")
    private String forlangningstidHv;

    @Getter @Setter
    @Column(allowsNull = "true")
    private String moms;

    @Getter @Setter
    @Column(allowsNull = "true")
    private BigDecimal indextal;

    @Getter @Setter
    @Column(allowsNull = "true")
    private BigDecimal indexandel;

    @Getter @Setter
    @Column(allowsNull = "true")
    private BigDecimal bashyra;

    @Getter @Setter
    @Column(allowsNull = "true")
    private BigDecimal indextillagg;

    @Getter @Setter
    @Column(allowsNull = "true")
    private BigDecimal hyrainklindex;

    @Getter @Setter
    @Column(allowsNull = "true")
    private BigDecimal fastighetsskatt;

    @Getter @Setter
    @Column(allowsNull = "true")
    private BigDecimal fskattproc;

    @Getter @Setter
    @Column(allowsNull = "true")
    private BigDecimal varme;

    @Getter @Setter
    @Column(allowsNull = "true")
    private BigDecimal el;

    @Getter @Setter
    @Column(allowsNull = "true")
    private BigDecimal kyla;

    @Getter @Setter
    @Column(allowsNull = "true")
    private BigDecimal va;

    @Getter @Setter
    @Column(allowsNull = "true")
    private BigDecimal kabeltv;

    @Getter @Setter
    @Column(allowsNull = "true")
    private BigDecimal ovrigt;

    @Getter @Setter
    @Column(allowsNull = "true")
    @PropertyLayout(named = "SA_TILLAGG")
    private BigDecimal saTillagg;

    @Getter @Setter
    @Column(allowsNull = "true")
    private BigDecimal okand;

    @Getter @Setter
    @Column(allowsNull = "true")
    private BigDecimal total;

    @Getter @Setter
    @Column(allowsNull = "true")
    @PropertyLayout(named = "TOTAL_KVM")
    private BigDecimal totalKvm;

    @Getter @Setter
    @Column(allowsNull = "true")
    private BigDecimal rabatt;

    @Getter @Setter
    @Column(allowsNull = "true")
    @PropertyLayout(named = "PROC_ANDRING")
    private BigDecimal procAndring;

    @Getter @Setter
    @Column(allowsNull = "true", length = 512)
    private String notering;

    @Getter @Setter
    @Column(allowsNull = "true")
    private String inflyttningsKod;

    @Getter @Setter
    @Column(allowsNull = "true")
    private String utflyttningsKod;

    @Getter @Setter
    @Column(allowsNull = "true")
    private String utskrivetDatum;

    @Getter @Setter
    @Column(allowsNull = "true")
    @PropertyLayout(named = "UPPSAGNINGSTID_HG")
    private String uppsagningstidHg;

    @Getter @Setter
    @Column(allowsNull = "true")
    @PropertyLayout(named = "FORLANGNINGSTID_HG")
    private String forlangningstidHg;

    @Getter @Setter
    @Column(allowsNull = "true")
    @PropertyLayout(named = "SENASTUPPSAGD_HG")
    private String senastuppsagdHg;

    @Getter @Setter
    @Column(allowsNull = "true")
    private String uppsagDav;

    @Getter @Setter
    @Column(allowsNull = "true")
    private String regDatum;

    @Getter @Setter
    @Column(allowsNull = "true")
    private String vakantFrom;

    @Getter @Setter
    @Column(allowsNull = "true")
    private BigDecimal omsattning;

    @Getter @Setter
    @Column(allowsNull = "true")
    private BigDecimal omsattProc;

    @Getter @Setter
    @Column(allowsNull = "true")
    private BigDecimal omsattningHyra;

    @Getter @Setter
    @Column(allowsNull = "true")
    private BigDecimal omsMinHyra;

    @Getter @Setter
    @Column(allowsNull = "true")
    private String omsBasDat;

    @Getter @Setter
    @Column(allowsNull = "true")
    private BigDecimal omsBasIndex;

    @Getter @Setter
    @Column(allowsNull = "true")
    private BigDecimal omsAndelProc;

    @Getter @Setter
    @Column(allowsNull = "true")
    private BigDecimal omsIndexBel;

    @Getter @Setter
    @Column(allowsNull = "true")
    private BigDecimal omsHyraInklIndex;

    @Getter @Setter
    @Column(allowsNull = "true")
    private BigDecimal omsOverskut;

    @Getter @Setter
    @Column(allowsNull = "true")
    private BigDecimal marknBidrag;

    @Getter @Setter
    @Column(allowsNull = "true")
    @PropertyLayout(named = "Extra_upps_1_Senast_Hg")
    private String extraUpps1SenastHg;

    @Getter @Setter
    @Column(allowsNull = "true")
    @PropertyLayout(named = "Extra_upps_1_Utflkod_Hg")
    private String extraUpps1UtflkodHg;

    @Getter @Setter
    @Column(allowsNull = "true")
    @PropertyLayout(named = "Extra_upps_1_Kontrakt_tom_Hg")
    private String extraUpps1KontraktTomHg;

    @Getter @Setter
    @Column(allowsNull = "true")
    @PropertyLayout(named = "Extra_uppstid_1_Hg")
    private String extraUppstid1Hg;

    @Getter @Setter
    @Column(allowsNull = "true")
    @PropertyLayout(named = "Extra_villkor_1_Hg")
    private String extraVillkor1Hg;

    @Getter @Setter
    @Column(allowsNull = "true")
    @PropertyLayout(named = "Extra_upps_2_Senast_Hg")
    private String extraUpps2SenastHg;

    @Getter @Setter
    @Column(allowsNull = "true")
    @PropertyLayout(named = "Extra_upps_2_Utflkod_Hg")
    private String extraUpps2UtflkodHg;

    @Getter @Setter
    @Column(allowsNull = "true")
    @PropertyLayout(named = "Extra_upps_2_Kontrakt_tom_Hg")
    private String extraUpps2KontraktTomHg;

    @Getter @Setter
    @Column(allowsNull = "true")
    @PropertyLayout(named = "Extra_uppstid_2_Hg")
    private String extraUppstid2Hg;

    @Getter @Setter
    @Column(allowsNull = "true")
    private String populärNamn;

    @Getter @Setter
    @Column(allowsNull = "true")
    @PropertyLayout(named = "rental_unit_start_date")
    // rental_unit_start_date is when the unit first started
    // space_units_start_date is when the latest area is effective from
    private String rentalUnitStartDate;

    @Getter @Setter
    @Column(allowsNull = "true")
    @PropertyLayout(named = "rental_unit_start_date")
    private String rentalUnitEndDate;

    @Getter @Setter
    @Column(allowsNull = "true")
    @PropertyLayout(named = "extra_upps_3_senast_hg")
    private String extraUpps3SenastHg;

    @Getter @Setter
    @Column(allowsNull = "true")
    @PropertyLayout(named = "extra_upps_3_kontrakt_tom_hg")
    private String extraUpps3KontraktTomtHg;

    @Getter @Setter
    @Column(allowsNull = "true")
    @PropertyLayout(named = "extra_villkor_3_hg")
    private String extraVillkor3Hg;

    @Getter @Setter
    @Column(allowsNull = "true")
    @PropertyLayout(named = "type_of_deposit")
    private String typeOfDeposit;

    @Getter @Setter
    @Column(allowsNull = "true")
    @PropertyLayout(named = "deposit_debit")
    private BigDecimal depositDebit;

    @Getter @Setter
    @Column(allowsNull = "true")
    @PropertyLayout(named = "deposit_ref")
    private String depositRef;

    @Getter @Setter
    @Column(allowsNull = "true")
    @PropertyLayout(named = "bank_name")
    private String bankName;

    @Getter @Setter
    @Column(allowsNull = "true")
    @PropertyLayout(named = "expiring_date")
    private String expiringDate;

    @Getter @Setter
    @Column(allowsNull = "true")
    @PropertyLayout(named = "space_units_start_date")
    // rental_unit_start_date is when the unit first started
    // space_units_start_date is when the latest area is effective from
    private String spaceUnitsStartDate;

    @Getter @Setter
    @Column(allowsNull = "false")
    @PropertyLayout(named = "evd-in-sd")
    private LocalDateTime evdInSd;

    @Getter @Setter
    @Column(allowsNull = "false")
    private boolean futureRentRollLine;

    @Getter @Setter
    @Column(allowsNull = "false")
    @PropertyLayout(named = "import_date")
    private LocalDateTime importDate;

    @Getter @Setter
    @Column(allowsNull = "false")
    private LocalDate exportDate;

    @Getter @Setter
    @Column(allowsNull = "true")
    private LocalDate applied;

    @Getter @Setter
    @Column(allowsNull = "true")
    private ImportStatus importStatus;

    @Action(semantics = SemanticsOf.SAFE)
    public List<ChargingLine> getChargingLines() {
        return chargingLineRepository.findByKeyToLeaseExternalReferenceAndExportDate(getKeyToLeaseExternalReference(), getExportDate());
    }

    @PropertyLayout(hidden = Where.PARENTED_TABLES)
    public Lease getLease() {
        final List<Lease> matches = leaseRepository.matchLeaseByExternalReference(getKeyToLeaseExternalReference());
        return matches.size() == 1 ? matches.get(0) : null;
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT, associateWith = "chargingLines", associateWithSequence = "1")
    public RentRollLine apply(List<ChargingLine> lines) {
        lines.forEach(ChargingLine::apply);
        return this;
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT, associateWith = "chargingLines", associateWithSequence = "2")
    public RentRollLine discard(List<ChargingLine> lines) {
        lines.forEach(ChargingLine::discard);
        return this;
    }

    @Override
    public List<Object> importData(final Object previousRow) {
        if (rentRollLineRepository.findByObjektsNummerAndEvdInSd(getObjektsNummer(), getEvdInSd()) == null) {
            setKeyToLeaseExternalReference(keyToLeaseExternalReference());
            setExportDate(getImportDate().toLocalDate());
            repositoryService.persistAndFlush(this);
        }
        return Collections.emptyList();
    }

    String keyToLeaseExternalReference() {
        return getKontraktNr() != null ? getKontraktNr().substring(2) : null;
    }

    private LocalDate stringToDate(final String dateString) {
        return dateString != null ? LocalDate.parse(dateString) : null;
    }

    @Inject
    RentRollLineRepository rentRollLineRepository;

    @Inject
    RepositoryService repositoryService;

    @Inject
    ChargingLineRepository chargingLineRepository;

    @Inject
    LeaseRepository leaseRepository;

}
