package org.estatio.module.coda.dom.doc;

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
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Where;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        // TODO: REVIEW: EST-1862: an alternative design would be to use the docHead/lineNum as the unique (application) key.
        identityType = IdentityType.DATASTORE,
        schema = "dbo",
        table = "CodaDocLine"
)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.IDENTITY,
        column = "id")
@Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@Queries({
        @Query(
                name = "findByDocHeadAndLineNum", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.dom.doc.CodaDocLine "
                        + "WHERE docHead == :docHead && "
                        + "      lineNum == :lineNum ")
})
@Unique(name = "CodaDocLine_docHead_lineNum_UNQ", members = { "docHead", "lineNum" })
@DomainObject(
        objectType = "coda.CodaDocLine",
        editing = Editing.DISABLED
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class CodaDocLine implements Comparable<CodaDocLine> {

    public CodaDocLine(){}
    public CodaDocLine(
            final CodaDocHead docHead,
            final int lineNum,
            final String extRef3,
            final String extRef5) {
        this.docHead = docHead;
        this.lineNum = lineNum;
        this.extRef3 = extRef3;
        this.extRef5 = extRef5;
    }


    @Column(allowsNull = "false", name = "docHeadId")
    @Property(hidden = Where.PARENTED_TABLES)
    @Getter @Setter
    private CodaDocHead docHead;

    @Column(allowsNull = "false")
    @Property()
    @Getter @Setter
    private int lineNum;

    @Column(allowsNull = "false", length = 32)
    @Property()
    @Getter @Setter
    private String extRef3;

    // TODO: REVIEW: EST-1862: have left optional because the new format doesn't require this to be populated.
    @Column(allowsNull = "true", length = 32)
    @Property()
    @Getter @Setter
    private String extRef5;


    //region > compareTo, toString
    @Override
    public int compareTo(final CodaDocLine other) {
        return ComparisonChain.start()
                .compare(getDocHead(), other.getDocHead())
                .compare(getLineNum(), other.getLineNum())
                .result();
    }

    @Override public String toString() {
        return "CodaDocLine{" +
                "docHead=" + docHead +
                ", lineNum=" + lineNum +
                ", extRef3='" + extRef3 + '\'' +
                ", extRef5='" + extRef5 + '\'' +
                '}';
    }

    //endregion

}
