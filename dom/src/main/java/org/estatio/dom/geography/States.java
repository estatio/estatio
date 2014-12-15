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

import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.annotation.ActionSemantics.Of;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.RegexValidation;

@DomainService(repositoryFor = State.class)
@DomainServiceLayout(
        named = "Other",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "80.6"
)
public class States
        extends EstatioDomainService<State> {

    public States() {
        super(States.class, State.class);
    }

    // //////////////////////////////////

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public State newState(
            final @Named("Reference") @RegEx(validation = RegexValidation.REFERENCE, caseSensitive = true) String reference, 
            final @Named("Name") String name, 
            final Country country) {
        final State state = newTransientInstance();
        return createState(reference, name, country, state);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @MemberOrder(name="Other", sequence = "2")
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
    public State findState(final @Named("Reference") String reference) {
        return firstMatch("findByReference", "reference", reference);
    }

    @Programmatic
    public List<State> findStatesByCountry(final Country country) {
        return country != null? allMatches("findByCountry", "country", country): Collections.<State>emptyList();
    }


}
