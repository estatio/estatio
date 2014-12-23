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
package org.estatio.dom.instance;

import java.util.List;
import javax.inject.Inject;
import com.google.common.eventbus.Subscribe;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancies;
import org.apache.isis.applib.Identifier;
import org.apache.isis.applib.annotation.ActionInteraction;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.eventbus.ActionInteractionEvent;

@DomainService
public class EstatioPartitions {


    /**
     * Hide the {@link org.isisaddons.module.security.dom.tenancy.ApplicationTenancies#allTenancies()} action because
     * it returns objects of the wrong compile-time type.
     */
    @Programmatic
    @Subscribe
    public void on(ApplicationTenancies.AllTenanciesEvent ev) {
        switch (ev.getPhase()) {
            case HIDE:
                ev.hide();
        }
        return;
    }

    public static class AllPartitionsEvent extends ActionInteractionEvent<EstatioPartitions> {
        public AllPartitionsEvent(EstatioPartitions source, Identifier identifier, Object... args) {
            super(source, identifier, args);
        }
    }

    @ActionInteraction(AllPartitionsEvent.class)
    @MemberOrder(name="Security", sequence = "90.4")
    @ActionSemantics(ActionSemantics.Of.SAFE)
    public List<EstatioPartition> allPartitions() {
        @SuppressWarnings("rawTypes")
        final List tenancies = applicationTenancies.allTenancies();
        return tenancies;
    }

    @Inject
    private ApplicationTenancies applicationTenancies;

}
