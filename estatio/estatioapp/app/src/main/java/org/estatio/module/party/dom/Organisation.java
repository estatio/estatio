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
package org.estatio.module.party.dom;

import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.Persistent;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.base.Strings;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.schema.utils.jaxbadapters.PersistentEntityAdapter;

import org.incode.module.base.dom.types.ReferenceType;

import org.estatio.module.base.dom.apptenancy.WithApplicationTenancyCountry;
import org.estatio.module.base.dom.apptenancy.WithApplicationTenancyPathPersisted;
import org.estatio.module.party.app.services.ChamberOfCommerceCodeLookUpService;
import org.estatio.module.party.app.services.OrganisationNameNumberViewModel;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        schema = "dbo" // Isis' ObjectSpecId inferred from @Discriminator
)
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Discriminator("org.estatio.dom.party.Organisation")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByChamberOfCommerceCode", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.party.dom.Organisation "
                        + "WHERE chamberOfCommerceCode == :chamberOfCommerceCode "),
        @javax.jdo.annotations.Query(
                name = "findByAtPathMissingChamberOfCommerceCode", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.party.dom.Organisation "
                        + "WHERE chamberOfCommerceCode == null "
                        + "&& applicationTenancyPath.startsWith(:atPath)")

})
@DomainObject(editing = Editing.DISABLED)
@XmlJavaTypeAdapter(PersistentEntityAdapter.class)
@NoArgsConstructor
public class Organisation
        extends Party
        implements WithApplicationTenancyCountry, WithApplicationTenancyPathPersisted {

    public Organisation(final String reference) {
        super(reference);
    }

    /**
     * for testing.
     */
    public Organisation(final ApplicationTenancyRepository applicationTenancyRepository) {
        super(applicationTenancyRepository);
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(length = FiscalCodeType.Meta.MAX_LEN)
    @Property(optionality = Optionality.OPTIONAL)
    @MemberOrder(sequence = "1")
    @Getter @Setter
    private String fiscalCode;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(length = VatCodeType.Meta.MAX_LEN)
    @Property(optionality = Optionality.OPTIONAL)
    @MemberOrder(sequence = "1")
    @Getter @Setter
    private String vatCode;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(length = CocCodeType.Meta.MAX_LEN)
    @Property(optionality = Optionality.OPTIONAL)
    @Getter @Setter
    private String chamberOfCommerceCode;

    // //////////////////////////////////////

    @Getter @Setter
    private boolean verified;

    // //////////////////////////////////////

    @Persistent(mappedBy = "organisation")
    @CollectionLayout(defaultView = "table")
    @Getter @Setter
    private SortedSet<OrganisationPreviousName> previousNames = new TreeSet<>();


    // //////////////////////////////////////

    public Organisation change(
            final @Parameter(optionality = Optionality.OPTIONAL, regexPattern = ReferenceType.Meta.REGEX, regexPatternReplacement = ReferenceType.Meta.REGEX_DESCRIPTION) String vatCode,
            final @Parameter(optionality = Optionality.OPTIONAL, regexPattern = ReferenceType.Meta.REGEX, regexPatternReplacement = ReferenceType.Meta.REGEX_DESCRIPTION) String fiscalCode,
            final @Parameter(optionality = Optionality.OPTIONAL, regexPattern = ReferenceType.Meta.REGEX, regexPatternReplacement = ReferenceType.Meta.REGEX_DESCRIPTION) String chamberOfCommerceCode) {
        setVatCode(vatCode);
        setFiscalCode(fiscalCode);
        if (getChamberOfCommerceCode()!=null && !getChamberOfCommerceCode().equals(chamberOfCommerceCode)){
            setVerified(false);
        }
        setChamberOfCommerceCode(chamberOfCommerceCode);
        return this;
    }

    public String default0Change() {
        return getVatCode();
    }

    public String default1Change() {
        return getFiscalCode();
    }

    public String default2Change() {
        return getChamberOfCommerceCode();
    }

    public Organisation changeName(
            final String name,
            final LocalDate previousNameEndDate) {
        if (!name.equals(getName())) {
            OrganisationPreviousName organisationPreviousName = organisationPreviousNameRepository.newOrganisationPreviousName(getName(), previousNameEndDate);
            getPreviousNames().add(organisationPreviousName);
            setVerified(false);
        }

        setName(name);

        return this;
    }

    public String default0ChangeName() {
        return getName();
    }

    public LocalDate default1ChangeName() {
        return getClockService().now();
    }

    public String validate0ChangeName(final String name) {
        return name.equals(this.getName()) ? "New name must be different from the current name" : null;
    }

    public String validate1ChangeName(final LocalDate previousNameEndDate) {
        return previousNameEndDate.isAfter(getClockService().now()) ? "You can not select a future end date" : null;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    public Organisation verify(
            final OrganisationNameNumberViewModel organisationCheck
    ){

            if (!Strings.isNullOrEmpty(organisationCheck.getChamberOfCommerceCode())) {
                setChamberOfCommerceCodeIfNotAlready(organisationCheck.getChamberOfCommerceCode());
            }
            if (!Strings.isNullOrEmpty(organisationCheck.getOrganisationName()) && !organisationCheck.getOrganisationName().equals(getName())) {
                changeName(organisationCheck.getOrganisationName(), organisationCheck.getEntryDate().minusDays(1));
            }
            setVerified(true);

        return this;
    }

    public List<OrganisationNameNumberViewModel> choices0Verify(){
        if (getChamberOfCommerceCode()==null) {
            return chamberOfCommerceCodeLookUpService.getChamberOfCommerceCodeCandidatesByOrganisation(this);
        } else {
            return Arrays.asList(chamberOfCommerceCodeLookUpService.getChamberOfCommerceCodeCandidatesByCode(this));
        }
    }

    public boolean hideVerify(){
        return isVerified();
    }

    @Programmatic
    public void setChamberOfCommerceCodeIfNotAlready(final String chamberOfCommerceCode) {
        if (Strings.isNullOrEmpty(getChamberOfCommerceCode()))
            setChamberOfCommerceCode(chamberOfCommerceCode);
    }


    @Inject
    public OrganisationPreviousNameRepository organisationPreviousNameRepository;

    @Inject
    ChamberOfCommerceCodeLookUpService chamberOfCommerceCodeLookUpService;

    public static class CocCodeType {

        private CocCodeType() {}

        public static class Meta {

            /**
             * TODO: review
             */
            public static final int MAX_LEN = 150;

            private Meta() {}

        }

    }

    public static class FiscalCodeType {

        private FiscalCodeType() {}

        public static class Meta {

            /**
             * TODO: review
             */
            public static final int MAX_LEN = 30;

            private Meta() {}

        }

    }

    public static class VatCodeType {

        private VatCodeType() {}

        public static class Meta {

            /**
             * TODO: review
             */
            public static final int MAX_LEN = 30;

            private Meta() {}

        }

    }
}
