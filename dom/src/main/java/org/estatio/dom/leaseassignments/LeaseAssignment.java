/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.dom.leaseassignments;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Optional;

import org.estatio.dom.EstatioMutableObject;
import org.estatio.dom.JdoColumnLength;
import org.estatio.dom.lease.Lease;

//TODO: is this in scope?
@javax.jdo.annotations.PersistenceCapable(identityType=IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy=IdGeneratorStrategy.NATIVE, 
        column="id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER, 
        column = "version")
public class LeaseAssignment extends EstatioMutableObject<LeaseAssignment> {

    
    public LeaseAssignment() {
        // TODO: I made this up...
        super("nextLease,assignmentDate");
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name="previousLeaseId")
    private Lease previousLease;

    @Optional
    public Lease getPreviousLease() {
        return previousLease;
    }

    public void setPreviousLease(final Lease previousLease) {
        this.previousLease = previousLease;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name="nextLeaseId")
    private Lease nextLease;

    @Optional
    public Lease getNextLease() {
        return nextLease;
    }

    public void setNextLease(final Lease nextLease) {
        this.nextLease = nextLease;
    }

    // //////////////////////////////////////

    private LocalDate assignmentDate;

    @javax.jdo.annotations.Column(allowsNull="false")
    public LocalDate getAssignmentDate() {
        return assignmentDate;
    }

    public void setAssignmentDate(final LocalDate assignmentDate) {
        this.assignmentDate = assignmentDate;
    }

    // //////////////////////////////////////

    private LeaseAssignmentType type;

    @javax.jdo.annotations.Column(allowsNull="false", length=JdoColumnLength.TYPE_ENUM)
    public LeaseAssignmentType getType() {
        return type;
    }

    public void setType(final LeaseAssignmentType assignmentType) {
        this.type = assignmentType;
    }


}
