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
package org.estatio.dom.geography;

import java.util.Collections;
import java.util.List;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.base.dom.types.ReferenceType;

import org.estatio.dom.UdoDomainRepositoryAndFactory;

@DomainService(repositoryFor = State.class)
@DomainServiceLayout(
        named = "Other",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "80.6")
public class StateRepository
        extends UdoDomainRepositoryAndFactory<State> {

    public StateRepository() {
        super(StateRepository.class, State.class);
    }

    // //////////////////////////////////

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public State newState(
            final @Parameter(regexPattern = ReferenceType.Meta.REGEX, regexPatternReplacement = ReferenceType.Meta.REGEX_DESCRIPTION) String reference,
            final String name,
            final Country country) {
        final State state = newTransientInstance();
        return createState(reference, name, country, state);
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(name = "Other", sequence = "2")
    public List<State> allStates() {
        return allInstances();
    }

    // //////////////////////////////////////

    @Programmatic
    public State createState(final String reference, final String name, final Country country, final State state) {
        state.setReference(reference);
        state.setName(name);
        state.setCountry(country);
        persist(state);
        return state;
    }

    @Programmatic
    public State findState(final String reference) {
        return firstMatch("findByReference", "reference", reference);
    }

    @Programmatic
    public List<State> findStatesByCountry(final Country country) {
        return country != null ? allMatches("findByCountry", "country", country) : Collections.<State> emptyList();
    }

}
