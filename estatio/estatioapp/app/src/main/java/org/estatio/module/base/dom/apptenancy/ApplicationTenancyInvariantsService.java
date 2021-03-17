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
package org.estatio.module.base.dom.apptenancy;

import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.estatio.module.base.dom.UdoDomainObject2;
import org.estatio.module.base.dom.UdoDomainService;


/**
 * Centralizes rules governing the moving of {@link org.isisaddons.module.security.dom.tenancy.ApplicationTenancy}.
 */
@DomainService(nature = NatureOfService.DOMAIN)
public class ApplicationTenancyInvariantsService extends UdoDomainService<ApplicationTenancyInvariantsService> {

    // //////////////////////////////////////

    private ApplicationTenancySubscriberAlgorithmRegistry algorithmRegistry;

    public ApplicationTenancyInvariantsService() {
        super(ApplicationTenancyInvariantsService.class);
    }

    // //////////////////////////////////////

    @PostConstruct
    public void init(final Map<String, String> properties) {
        super.init(properties);

        algorithmRegistry = new ApplicationTenancySubscriberAlgorithmRegistry();
        algorithmRegistry.addAlgorithms();
    }


    // //////////////////////////////////////

    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void on(final ApplicationTenancyEventChanged ev) {
        on(ev, ApplicationTenancyEventChanged.class);
    }

    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void on(final ApplicationTenancyEventMovedDown ev) {
        on(ev, ApplicationTenancyEventMovedDown.class);
    }

    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void on(final ApplicationTenancyEventMovedUp ev) {
        on(ev, ApplicationTenancyEventMovedUp.class);
    }

    private void on(
            final ApplicationTenancyEventChanged ev,
            final Class<? extends ApplicationTenancyEventChanged> eventClass) {
        final UdoDomainObject2 source = ev.getSource();
        final List<ApplicationTenancySubscriberAlgorithm> algorithms = algorithmRegistry.lookup(source, eventClass);

        for (final ApplicationTenancySubscriberAlgorithm algorithm : algorithms) {
            getContainer().injectServicesInto(algorithm);

            switch (ev.getEventPhase()) {
                case HIDE:
                    algorithm.hide(ev, source);
                    break;
                case DISABLE:
                    algorithm.disable(ev, source);
                    break;
                case VALIDATE:
                    algorithm.validate(ev, source);
                    break;
                case EXECUTING:
                    algorithm.executing(ev, source);
                    break;
                case EXECUTED:
                    algorithm.executed(ev, source);
                    break;
            }
        }
    }

}
