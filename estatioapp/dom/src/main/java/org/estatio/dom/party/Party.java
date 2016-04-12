/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
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
package org.estatio.dom.party;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.IsisApplibModule.ActionDomainEvent;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Where;

import org.estatio.app.security.EstatioRole;
import org.estatio.dom.EstatioDomainObject;
import org.estatio.dom.JdoColumnLength;
import org.estatio.dom.RegexValidation;
import org.estatio.dom.WithNameComparable;
import org.estatio.dom.WithReferenceUnique;
import org.estatio.dom.agreement.AgreementRole;
import org.estatio.dom.agreement.AgreementRoleHolder;
import org.estatio.dom.communicationchannel.CommunicationChannelOwner;
import org.estatio.dom.utils.TitleBuilder;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Discriminator(
        strategy = DiscriminatorStrategy.CLASS_NAME,
        column = "discriminator")
@javax.jdo.annotations.Uniques({
        @javax.jdo.annotations.Unique(
                name = "Party_reference_UNQ", members = "reference")
})
@javax.jdo.annotations.Indices({
        // to cover the 'findByReferenceOrName' query
        // both in this superclass and the subclasses
        @javax.jdo.annotations.Index(
                name = "Party_reference_name_IDX", members = { "reference", "name" })
})
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "matchByReferenceOrName", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.party.Party "
                        + "WHERE reference.matches(:referenceOrName) "
                        + "   || name.matches(:referenceOrName)"),
        @javax.jdo.annotations.Query(
                name = "findByReference", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.party.Party "
                        + "WHERE reference == :reference") })
@DomainObject(editing = Editing.DISABLED, autoCompleteAction = "autoComplete", autoCompleteRepository = Parties.class)
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_ROOT)
public abstract class Party
        extends EstatioDomainObject<Party>
        implements WithNameComparable<Party>, WithReferenceUnique, CommunicationChannelOwner, AgreementRoleHolder {

    public Party() {
        super("name");
    }

    public String title() {
        return TitleBuilder.start()
                .withName(getName())
                .withReference(getReference())
                .toString();
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull = "false", length = JdoColumnLength.REFERENCE)
    @Property(editing = Editing.DISABLED, regexPattern = RegexValidation.REFERENCE)
    @Getter @Setter
    private String reference;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull = "false", length = JdoColumnLength.Party.NAME)
    @Getter @Setter
    private String name;

    /**
     * Provided so that subclasses can override and disable.
     */
    public String disableName() {
        return null;
    }

    // //////////////////////////////////////

    @Property(hidden = Where.EVERYWHERE)
    @javax.jdo.annotations.Persistent(mappedBy = "party")
    @Getter @Setter
    private SortedSet<AgreementRole> agreements = new TreeSet<>();

    // //////////////////////////////////////

    @Property(hidden = Where.EVERYWHERE)
    @javax.jdo.annotations.Persistent(mappedBy = "party")
    @Getter @Setter
    private SortedSet<PartyRegistration> registrations = new TreeSet<>();

    // //////////////////////////////////////

    @Column(length = JdoColumnLength.Party.FISCAL_CODE)
    @Property(optionality = Optionality.OPTIONAL)
    @Getter @Setter
    private String fiscalCode;

    // //////////////////////////////////////

    public static class RemoveEvent extends ActionDomainEvent<Party> {
        private static final long serialVersionUID = 1L;

        public Party getReplacement() {
            return (Party) (this.getArguments().isEmpty() ? null : getArguments().get(0));
        }
    }

    @Action(domainEvent = Party.RemoveEvent.class)
    public void remove() {
        removeAndReplace(null);
    }

    @Action(domainEvent = Party.RemoveEvent.class)
    public void removeAndReplace(@Parameter(optionality = Optionality.OPTIONAL) Party replaceWith) {
        getContainer().remove(this);
        getContainer().flush();
    }

    public boolean hideRemoveAndReplace(Party party) {
        return !EstatioRole.ADMINISTRATOR.isApplicableFor(getUser());
    }

    public String validateRemoveAndReplace(final Party party) {
        return party != this ? null : "Cannot replace a party with itself";
    }

}
