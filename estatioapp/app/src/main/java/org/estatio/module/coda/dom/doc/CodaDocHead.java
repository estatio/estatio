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

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;

import org.estatio.module.capex.dom.invoice.IncomingInvoice;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        // TODO: REVIEW: EST-1862: an alternative design would be to use the cmpCode/docCode/docNum as the unique (application) key.
        identityType = IdentityType.DATASTORE,
        schema = "coda",
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
            final IncomingInvoice incomingInvoice) {

        this.cmpCode = cmpCode;
        this.docCode = docCode;
        this.docNum = docNum;
        this.incomingInvoice = incomingInvoice;
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

    // TODO: REVIEW: EST-1862: have left this null, but perhaps that is wrong and there would always be a corresponding Estatio incoming invoice?
    @Column(allowsNull = "true")
    @Property
    @Getter @Setter
    private IncomingInvoice incomingInvoice;

    @javax.jdo.annotations.Persistent(mappedBy = "docHead", defaultFetchGroup = "true")
    @CollectionLayout(defaultView = "table", paged = 999)
    @Getter @Setter
    private SortedSet<CodaDocLine> lines = new TreeSet<>();

    @Programmatic
    public void upsertLine(final int lineNum, final String extRef3, final String extRef5) {
        lineRepository.upsert(this, lineNum, extRef3, extRef5);
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
    CodaDocLineRepository lineRepository;
}
