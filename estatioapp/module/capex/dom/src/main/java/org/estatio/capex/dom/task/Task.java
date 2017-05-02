package org.estatio.capex.dom.task;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDateTime;

import org.apache.isis.applib.annotation.DomainObject;

import org.estatio.dom.roles.EstatioRole;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "dbo"
)
@javax.jdo.annotations.Discriminator(
        strategy = DiscriminatorStrategy.VALUE_MAP,
        column = "discriminator",
        value = "org.estatio.capex.dom.task.Task"
)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@Queries({
        @Query(
                name = "find", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.task.Task ")
})
@DomainObject(objectType = "task.Task")
public abstract class Task<T extends Task<T>> implements Comparable<T> {

    @Column(allowsNull = "true")
    @Getter @Setter
    private String description;

    @Getter @Setter
    @Column(allowsNull = "false")
    private LocalDateTime createdOn;

    @Getter @Setter
    @Column(allowsNull = "true")
    private LocalDateTime startedOn;

    @Column(allowsNull = "false")
    @Getter @Setter
    private boolean completed;

    @Getter @Setter
    @Column(allowsNull = "true")
    private LocalDateTime completedOn;

    @Getter @Setter
    @Column(allowsNull = "false")
    private EstatioRole assignedTo;

    @Override
    public int compareTo(final Task other) {
        return org.apache.isis.applib.util.ObjectContracts.compare(this, other, "createdOn");
    }

}
