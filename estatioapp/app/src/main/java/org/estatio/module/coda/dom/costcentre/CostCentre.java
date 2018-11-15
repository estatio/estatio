package org.estatio.module.coda.dom.costcentre;

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
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Property;

import org.estatio.module.asset.dom.PropertyRepository;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "dbo",
        table = "CostCentre"
)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.IDENTITY,
        column = "id")
@Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@Queries({
        @Query(
                name = "findByElement3", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.dom.costcentre.CostCentre "
                        + "WHERE element3 == :element3 ")
})
@Unique(name = "CostCentre_element3_UNQ", members = { "element3" })
@DomainObject(
        objectType = "coda.CostCentre",
        editing = Editing.DISABLED,
        bounded = true
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class CostCentre implements Comparable<CostCentre> {

    public static String GENERAL_EXTREF3_PART2 = "GEN";

    public CostCentre() {}
    public CostCentre(final String element3, final String extRef3Segment2) {
        this.element3 = element3;
        this.extRef3Segment2 = extRef3Segment2;
    }

    public String title() {
        return String.format("%s %s", element3, extRef3Segment2);
    }

    @Column(allowsNull = "false", length = 36)
    @Property()
    @Getter @Setter
    private String element3;

    @Column(allowsNull = "false", length = 3)
    @Property()
    @Getter @Setter
    private String extRef3Segment2;

    public boolean isGeneral() {
        return GENERAL_EXTREF3_PART2.equals(getExtRef3Segment2());
    }

    public org.estatio.module.asset.dom.Property getProperty() {
        return !isGeneral()
                ? propertyRepository.findPropertyByReference(getExtRef3Segment2())
                : null;
    }


    //region > compareTo, toString
    @Override
    public int compareTo(final CostCentre other) {
        return ComparisonChain.start()
                .compare(getElement3(), other.getElement3())
                .result();
    }

    @Override
    public String toString() {
        return "CostCentre{" +
                "element3='" + element3 + '\'' +
                ", extRef3Segment2='" + extRef3Segment2 + '\'' +
                '}';
    }

    //endregion

    @Inject
    PropertyRepository propertyRepository;

}
