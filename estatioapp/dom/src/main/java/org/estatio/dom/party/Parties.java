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

import java.util.List;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.utils.StringUtils;

@DomainService(repositoryFor = Party.class)
@DomainServiceLayout(
        named = "Parties",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "20.1")
public class Parties extends UdoDomainRepositoryAndFactory<Party> {

    public Parties() {
        super(Parties.class, Party.class);
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "1")
    public List<Party> findParties(
            final @ParameterLayout(named = "Reference or Name", describedAs = "May include wildcards '*' and '?'") String referenceOrName) {
        return allMatches("matchByReferenceOrName",
                "referenceOrName", StringUtils.wildcardToCaseInsensitiveRegex(referenceOrName));
    }

    @Action(semantics = SemanticsOf.SAFE, hidden = Where.EVERYWHERE)
    public Party matchPartyByReferenceOrName(final String referenceOrName) {
        return firstMatch("matchByReferenceOrName",
                "referenceOrName", StringUtils.wildcardToCaseInsensitiveRegex(referenceOrName));
    }

    @Action(semantics = SemanticsOf.SAFE, hidden = Where.EVERYWHERE)
    public Party findPartyByReference(final String reference) {
        return mustMatch("findByReference", "reference", reference);
    }

    @Action(semantics = SemanticsOf.SAFE, hidden = Where.EVERYWHERE)
    public Party findPartyByReferenceOrNull(final String reference) {
        return firstMatch("findByReference", "reference", reference);
    }

    // //////////////////////////////////////

    @Action(hidden = Where.EVERYWHERE)
    public List<Party> autoComplete(final String searchPhrase) {
        return searchPhrase.length() > 2
                ? findParties("*" + searchPhrase + "*")
                : Lists.<Party> newArrayList();
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE, restrictTo = RestrictTo.PROTOTYPING)
    @MemberOrder(sequence = "99")
    public List<Party> allParties() {
        return allInstances();
    }

}
