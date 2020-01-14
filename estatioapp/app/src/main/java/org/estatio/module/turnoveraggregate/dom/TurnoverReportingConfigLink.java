package org.estatio.module.turnoveraggregate.dom;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;

import org.estatio.module.turnover.dom.TurnoverReportingConfig;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE
        ,schema = "dbo"
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@Queries({
        @Query(
                name = "findUnique", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.turnoveraggregate.dom.TurnoverReportingConfigLink "
                        + "WHERE turnoverReportingConfig == :turnoverReportingConfig "
                        + "&& aggregationChild == :aggregationChild "),
        @Query(
                name = "findByTurnoverReportingConfig", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.turnoveraggregate.dom.TurnoverReportingConfigLink "
                        + "WHERE turnoverReportingConfig == :turnoverReportingConfig "),
})
@Unique(name = "TurnoverReportingConfigLink_turnoverReportingConfig_aggregationChild_UNQ", members = { "turnoverReportingConfig", "aggregationChild" })
@DomainObject(
        editing = Editing.DISABLED,
        objectType = "org.estatio.module.turnoveraggregate.dom.TurnoverReportingConfigLink"
)
public class TurnoverReportingConfigLink {

    public TurnoverReportingConfigLink(final TurnoverReportingConfig config, final TurnoverReportingConfig child){
        this.turnoverReportingConfig = config;
        this.aggregationChild = child;
    }

    @Getter @Setter
    @Column(name = "turnoverReportingConfigId", allowsNull = "false")
    private TurnoverReportingConfig turnoverReportingConfig;

    @Getter @Setter
    @Column(name = "aggregationChildId", allowsNull = "false")
    private TurnoverReportingConfig aggregationChild;

}
