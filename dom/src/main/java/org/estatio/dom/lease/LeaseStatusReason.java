package org.estatio.dom.lease;

import javax.jdo.annotations.IdentityType;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Where;

import org.estatio.dom.JdoColumnLength;

@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.Query(
        name = "findByLease", language = "JDOQL",
        value = "SELECT FROM org.estatio.dom.lease.LeaseStatusReason " +
                "WHERE lease == :lease")
public class LeaseStatusReason {

    private Lease lease;

    @javax.jdo.annotations.Column(name = "leaseId", allowsNull = "false")
    @Hidden(where = Where.PARENTED_TABLES)
    public Lease getLease() {
        return lease;
    }

    public void setLease(final Lease lease) {
        this.lease = lease;
    }

    // //////////////////////////////////////

    private LeaseStatus status;

    @javax.jdo.annotations.Column(allowsNull = "false", length = JdoColumnLength.STATUS_ENUM)
    public LeaseStatus getStatus() {
        return status;
    }

    public void setStatus(final LeaseStatus status) {
        this.status = status;
    }

    // //////////////////////////////////////

    private String reason;

    @javax.jdo.annotations.Column(allowsNull = "false", length = JdoColumnLength.NOTES)
    public String getReason() {
        return reason;
    }

    public void setReason(final String reason) {
        this.reason = reason;
    }

    // //////////////////////////////////////

    private String user;

    @javax.jdo.annotations.Column(allowsNull = "false", length = JdoColumnLength.USER_NAME)
    public String getUser() {
        return user;
    }

    public void setUser(final String user) {
        this.user = user;
    }

    // //////////////////////////////////////

    private long timestamp;

    @javax.jdo.annotations.Column(allowsNull = "false")
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final long timestamp) {
        this.timestamp = timestamp;
    }

}
