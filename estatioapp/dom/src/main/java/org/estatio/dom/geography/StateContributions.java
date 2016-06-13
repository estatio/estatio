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

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.UdoDomainService;

/**
 * Decouples {@link Country} from {@link State}s.
 * <p/>
 * <p/>
 * TODO: no good reason to do this; they are in the same cluster.  Map in the usual fashion.
 */
@DomainService(menuOrder = "80", nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY)
public class StateContributions extends UdoDomainService<StateContributions> {

    public StateContributions() {
        super(StateContributions.class);
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<State> states(final Country country) {
        return stateRepository.findStatesByCountry(country);
    }

    // //////////////////////////////////////

    @Inject
    private StateRepository stateRepository;

}
