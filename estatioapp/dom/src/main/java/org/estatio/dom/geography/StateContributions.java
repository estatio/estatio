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
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.NotContributed.As;
import org.estatio.dom.UdoDomainService;

/**
 * Decouples {@link Country} from {@link State}s.
 * 
 * <p>
 * TODO: no good reason to do this; they are in the same cluster.  Map in the usual fashion. 
 */
@DomainService(menuOrder = "80")
@Hidden
public class StateContributions extends UdoDomainService<StateContributions> {

    public StateContributions() {
        super(StateContributions.class);
    }

    // //////////////////////////////////////

    @NotInServiceMenu
    @NotContributed(As.ACTION)
    @ActionSemantics(Of.SAFE)
    public List<State> states(final Country country) {
        return states.findStatesByCountry(country);
    }
    

    // //////////////////////////////////////

    private States states;
    public final void injectStates(final States states) {
        this.states = states;
    }



}
