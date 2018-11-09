package org.estatio.module.coda.dom.doc;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;

import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRoleTypeEnum;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.dom.role.PartyRoleType;
import org.estatio.module.party.dom.role.PartyRoleTypeRepository;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        // TODO: REVIEW: EST-1862: an alternative design would be to use the cmpCode/docCode/docNum as the unique (application) key.
        identityType = IdentityType.DATASTORE,
        schema = "dbo",
        table = "CodaDocHead"
)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.IDENTITY,
        column = "id")
@Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@Queries({
        @Query(
                name = "findByCmpCodeAndDocCodeAndDocNum", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.dom.doc.CodaDocHead "
                        + "WHERE cmpCode == :cmpCode && "
                        + "      docCode == :docCode && "
                        + "      docNum  == :docNum ")
})
@Unique(name = "CodaDocHead_cmpCode_docCode_docNum_UNQ", members = { "cmpCode", "docCode", "docNum" })
@DomainObject(
        objectType = "coda.CodaDocHead",
        editing = Editing.DISABLED
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class CodaDocHead implements Comparable<CodaDocHead> {

    public CodaDocHead(){}
    public CodaDocHead(
            final String cmpCode,
            final String docCode,
            final String docNum,
            final LocalDate inputDate,
            final LocalDate docDate,
            final String codaPeriod,
            final String location) {

        this.cmpCode = cmpCode;
        this.docCode = docCode;
        this.docNum = docNum;
        this.inputDate = inputDate;
        this.docDate = docDate;
        this.codaPeriod = codaPeriod;
        this.location = location;

        this.numberOfLines = 0;

        //
        // and set all the 'derived' stuff to its initial value
        //

        resetValidationAndDerivations();
    }

    void resetValidationAndDerivations() {

        // use setters so that DN is aware
        setHandling(Handling.ATTENTION);

        setLineValidationStatus(ValidationStatus.NOT_CHECKED);

        setCmpCodeValidationStatus(ValidationStatus.NOT_CHECKED);
        setCmpCodeBuyer(null);

        setSummaryLineIsPresentValidationStatus(ValidationStatus.NOT_CHECKED);
        setAnalysisLineIsPresentValidationStatus(ValidationStatus.NOT_CHECKED);

        setReasonInvalid(null);

        Lists.newArrayList(getLines()).forEach(CodaDocLine::resetValidationAndDerivations);
    }

    public String title() {
        return String.format("%s | %s | %s", getCmpCode(), getDocCode(), getDocNum());
    }

    @Column(allowsNull = "false", length = 12)
    @Property()
    @Getter @Setter
    private String cmpCode;

    @Column(allowsNull = "false", length = 12)
    @Property()
    @Getter @Setter
    private String docCode;

    @Column(allowsNull = "false", length = 12)
    @Property()
    @Getter @Setter
    private String docNum;

    @Column(allowsNull = "true")
    @javax.jdo.annotations.Persistent
    @Property()
    @Getter @Setter
    private LocalDate inputDate;

    @Column(allowsNull = "true")
    @javax.jdo.annotations.Persistent
    @Property()
    @Getter @Setter
    private LocalDate docDate;

    /**
     * For example, '2019/1' meaning the first period of the financial year 2018-2019, ie July 2018
     * ("take away 6 months from the date you think it is").
     *
     * The biggest we have seen in the DB is 2014/9999.  Clearly a hack, and none for 'FR-GEN', but anyway...
     */
    @Column(allowsNull = "false", length = 9)
    @javax.jdo.annotations.Persistent
    @Property()
    @Getter @Setter
    private String codaPeriod;

    @Column(allowsNull = "false")
    @Property()
    @Getter @Setter
    private int numberOfLines;



    @Column(allowsNull = "true", length = 4000)
    @Property()
    @PropertyLayout(multiLine = 5)
    @Getter @Setter
    private String reasonInvalid;

    @Programmatic
    public void appendInvalidReason(final String reasonFormat, Object... args) {
        final String reason = String.format(reasonFormat, args);
        String reasonInvalid = getReasonInvalid();
        if(reasonInvalid != null) {
            reasonInvalid += "\n";
        } else {
            reasonInvalid = "";
        }
        reasonInvalid += reason;
        setReasonInvalid(reasonInvalid);
    }

    @Column(allowsNull = "false", length = 20)
    @Property()
    @Getter @Setter
    private ValidationStatus cmpCodeValidationStatus;

    @Column(allowsNull = "true", name="cmpCodeBuyerId")
    @Property()
    @Getter @Setter
    private Organisation cmpCodeBuyer;



    @Column(allowsNull = "true", name="incomingInvoiceId")
    @Property
    @Getter @Setter
    private IncomingInvoice incomingInvoice;

    /**
     * Cascade delete of all {@link CodaDocLine}s if the parent {@link CodaDocHead} is deleted.
     */
    @javax.jdo.annotations.Persistent(mappedBy = "docHead", defaultFetchGroup = "true", dependent = "true")
    @CollectionLayout(defaultView = "table", paged = 999)
    @Getter @Setter
    private SortedSet<CodaDocLine> lines = new TreeSet<>();

    @Programmatic
    public CodaDocLine summaryDocLine() {
        return findFirstLineByType(LineType.SUMMARY);
    }

    @Programmatic
    public CodaDocLine analysisDocLine() {
        return findFirstLineByType(LineType.ANALYSIS);
    }

    CodaDocLine findFirstLineByType(final LineType lineType) {
        return Lists.newArrayList(getLines()).stream()
                .filter(x -> x.getLineType() == lineType)
                .findFirst()
                .orElse(null);
    }


    @Column(allowsNull = "false", length = 20)
    @Property()
    @Getter @Setter
    private ValidationStatus lineValidationStatus;

    @Column(allowsNull = "false", length = 20)
    @Property()
    @Getter @Setter
    private ValidationStatus summaryLineIsPresentValidationStatus;

    @Column(allowsNull = "false", length = 20)
    @Property()
    @Getter @Setter
    private ValidationStatus analysisLineIsPresentValidationStatus;

    /**
     * How this document should be handled (override {@link #getLineValidationStatus() validation status}).
     */
    @Column(allowsNull = "false", length = 30)
    @Property()
    @Getter @Setter
    private Handling handling;

    @Programmatic
    public void handleAs(Handling handling) {
        if(this.getHandling() == Handling.SYNCED) {
            return;
        }
        this.setHandling(handling);
        Lists.newArrayList(getLines()).forEach(codaDocLine -> codaDocLine.setHandling(handling));
    }

    /**
     * Indicates the location of this document.
     */
    @Column(allowsNull = "true", length = 12)
    @Property()
    @Getter @Setter
    private String location;

    /**
     * Necessary to decouple because some of the validation relies on Coda WSDL, which is proprietary
     * and so cannot be in the open source package.
     */
    public interface LineValidator {
        void validateSummaryDocLine(CodaDocLine summaryDocLine);

        void validateAnalysisDocLine(CodaDocLine analysisDocLine);

        LineValidator NOOP = new LineValidator() {
            @Override public void validateSummaryDocLine(final CodaDocLine summaryDocLine) {
            }

            @Override public void validateAnalysisDocLine(final CodaDocLine analysisDocLine) {

            }
        };
    }

    @Programmatic
    public void validate() {
        validateUsing(this.lineValidator);
    }

    void validateUsing(LineValidator lineValidator) {

        //
        // validate buyer
        //
        final String buyerRef = getCmpCode();
        final Party buyerParty = partyRepository.findPartyByReference(buyerRef);
        final PartyRoleType ecpRoleType =
                IncomingInvoiceRoleTypeEnum.ECP.findUsing(partyRoleTypeRepository);
        if(buyerParty == null) {
            setCmpCodeValidationStatus(ValidationStatus.INVALID);
            appendInvalidReason("No buyer party found for cmpCode '%s'", buyerRef);
        } else if(!(buyerParty instanceof Organisation)) {
            setCmpCodeValidationStatus(ValidationStatus.INVALID);
            appendInvalidReason("Party found for cmpCode '%s' is not an Organisation", buyerRef);
        } else if(! buyerParty.hasPartyRoleType(ecpRoleType)) {
            setCmpCodeValidationStatus(ValidationStatus.INVALID);
            appendInvalidReason("Organisation '%s' does not have ECP role", buyerRef);
        }

        // if get this far and not yet invalid, then must be valid.
        if(getCmpCodeValidationStatus() != ValidationStatus.INVALID) {
            setCmpCodeValidationStatus(ValidationStatus.VALID);
            setCmpCodeBuyer((Organisation) buyerParty);
        }

        //
        // TODO: validate location from header
        //


        //
        // validate lines
        //
        validateLines(lineValidator);

    }

    private void validateLines(final LineValidator lineValidator) {

        final CodaDocLine summaryDocLine = summaryDocLine();
        if(summaryDocLine != null) {
            lineValidator.validateSummaryDocLine(summaryDocLine);
        }
        final CodaDocLine analysisDocLine = analysisDocLine();
        if(analysisDocLine != null) {
            lineValidator.validateAnalysisDocLine(analysisDocLine);
        }


        final long numInvalidLines = Lists.newArrayList(getLines()).stream()
                .filter(CodaDocLine::isInvalid)
                .count();

        if (numInvalidLines == 0) {
            setLineValidationStatus(ValidationStatus.VALID);
        } else {
            setLineValidationStatus(ValidationStatus.INVALID);
            appendInvalidReason(numInvalidLines + " line"
                    + (numberOfLines == 1 ? " is" : "s are")
                    + " invalid");
        }
    }


    @Programmatic
    public boolean isValid() {
        return getReasonInvalid() == null;
    }
    @Programmatic
    public boolean isInvalid() {
        return !isValid();
    }

    //region > compareTo, toString
    @Override
    public int compareTo(final CodaDocHead other) {
        return ComparisonChain.start()
                .compare(getCmpCode(), other.getCmpCode())
                .compare(getDocCode(), other.getDocCode())
                .compare(getDocNum(), other.getDocNum())
                .result();
    }

    @Override
    public String toString() {
        return "CodaDocHead{" +
                "companyCode='" + getCmpCode()+ '\'' +
                ", docCode='" + getDocCode() + '\'' +
                ", docNum='" + getDocNum() + '\'' +
                '}';
    }

    //endregion

    @Inject
    PartyRepository partyRepository;

    @Inject
    PartyRoleTypeRepository partyRoleTypeRepository;

    @Inject
    CodaDocHead.LineValidator lineValidator;

}
