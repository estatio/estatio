/*
 *  Copyright 2015 Eurocommercial Properties NV
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
package org.estatio.dom.document;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.Persistent;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.value.Blob;

import org.estatio.dom.WithIntervalMutable;
import org.estatio.dom.valuetypes.LocalDateInterval;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType = javax.jdo.annotations.IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = javax.jdo.annotations.IdGeneratorStrategy.IDENTITY,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = javax.jdo.annotations.VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Inheritance(
        strategy = javax.jdo.annotations.InheritanceStrategy.NEW_TABLE)
@Discriminator(
        strategy = javax.jdo.annotations.DiscriminatorStrategy.CLASS_NAME,
        column = "discriminator")
@javax.jdo.annotations.Indices({
        @javax.jdo.annotations.Index(
                name = "Document_name_IDX",
                members = { "name" })
})
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByName", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.document.Document "
                        + "WHERE name.matches(:pattern) ")
})
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_CHILD)
@DomainObject(editing = Editing.DISABLED)
public class Document implements Comparable<Document>, WithIntervalMutable<Document> {

    @Title()
    @Column(allowsNull = "false")
    @Getter @Setter
    private String name;

    // //////////////////////////////////////

    @Persistent(defaultFetchGroup = "false")
    private Blob file;

    @Property(hidden = Where.ANYWHERE, optionality = Optionality.OPTIONAL)
    public Blob getFile() {
        return file;
    }

    @Persistent
    public void setFile(Blob file) {
        this.file = file;
    }

    public Blob download() {
        return getFile();
    }

    public Document upload(Blob blob) {
        setFile(blob);
        return this;
    }

    // //////////////////////////////////////

    @Column(allowsNull = "false")
    @Getter @Setter
    private DocumentType type;

    // //////////////////////////////////////

    @javax.inject.Inject
    @SuppressWarnings("unused")
    private ClockService clockService;

    // //////////////////////////////////////

    @Override
    public int compareTo(Document o) {
        return this.getName().compareTo(o.getName());
    }

    // //////////////////////////////////////

    @Getter @Setter
    private LocalDate startDate;

    @Getter @Setter
    private LocalDate endDate;

    // //////////////////////////////////////

    private WithIntervalMutable.Helper<Document> intervalHelper = new WithIntervalMutable.Helper<Document>(this);

    WithIntervalMutable.Helper<Document> getHelper() {
        return intervalHelper;
    }

    @Override
    public LocalDateInterval getInterval() {
        return new LocalDateInterval(getStartDate(), getEndDate());
    }

    @Override
    public LocalDateInterval getEffectiveInterval() {
        return getInterval();
    }

    @Override
    public boolean isCurrent() {
        return false;
    }

    @Override
    public Document changeDates(
            final @ParameterLayout(named = "Start date") @Parameter(optionality = Optionality.OPTIONAL) LocalDate startDate,
            final @ParameterLayout(named = "Start date") @Parameter(optionality = Optionality.OPTIONAL) LocalDate endDate) {
        return getHelper().changeDates(startDate, endDate);
    }

    @Override
    public LocalDate default0ChangeDates() {
        return getHelper().default0ChangeDates();
    }

    @Override
    public LocalDate default1ChangeDates() {
        return getHelper().default1ChangeDates();
    }

    @Override
    public String validateChangeDates(LocalDate startDate, LocalDate endDate) {
        return getHelper().validateChangeDates(startDate, endDate);
    }

}
