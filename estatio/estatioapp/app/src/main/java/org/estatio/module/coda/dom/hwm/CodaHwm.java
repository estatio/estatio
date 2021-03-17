package org.estatio.module.coda.dom.hwm;

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

import org.joda.time.LocalDateTime;

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Property;

import org.estatio.module.coda.dom.doc.CodaDocHead;
import org.estatio.module.coda.dom.supplier.CodaSupplier;

import lombok.Getter;
import lombok.Setter;

/**
 * Manages the "high water mark" for data feeds from Coda, for different feeds, on a per cmpCode basis.
 *
 * <p>
 *     Expect there to be multiple instances for {@link CodaDocHead} (for each cmpCode, eg "IT01", "IT05" etc)
 *     as well as a single instance for {@link CodaSupplier} (just "IT01"; the suppliers are the same across all companies in Coda so need to copy them over individually).
 * </p>
 */
@PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "dbo",
        table = "CodaHwm"
)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.IDENTITY,
        column = "id")
@Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@Queries({
        @Query(
                name = "findByFeedNameAndCmpCode", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.dom.hwm.CodaHwm "
                        + "WHERE feedName == :feedName "
                        + "   && cmpCode  == :cmpCode ")
})
@Unique(name = "CodaHwm_feedName_cmpCode", members = { "feedName", "cmpCode" })
@DomainObject(
        objectType = "coda.CodaHwm",
        editing = Editing.DISABLED
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class CodaHwm implements Comparable<CodaHwm> {

    public CodaHwm() {}
    public CodaHwm(final String feedName, final String cmpCode, final LocalDateTime lastRan) {
        this.feedName = feedName;
        this.cmpCode = cmpCode;
        this.lastRan = lastRan;
    }

    public String title() {
        return String.format("%s %s", feedName, cmpCode);
    }

    @Column(allowsNull = "false", length = 36)
    @Property()
    @Getter @Setter
    private String feedName;

    @Column(allowsNull = "true", length = 12)
    @Property()
    @Getter @Setter
    private String cmpCode;

    /**
     * Represents the "high-water-mark" for this feed.
     */
    @Column(allowsNull = "false")
    @Property()
    @Getter @Setter
    private LocalDateTime lastRan;


    //region > compareTo, toString
    @Override
    public int compareTo(final CodaHwm other) {
        return ComparisonChain.start()
                .compare(getFeedName(), other.getFeedName())
                .compare(getCmpCode(), other.getCmpCode())
                .result();
    }

    @Override
    public String toString() {
        return "CodaHwm{" +
                "feedName='" + feedName + '\'' +
                ", cmpCode='" + cmpCode + '\'' +
                ", lastRan=" + lastRan +
                '}';
    }

    //endregion

}
