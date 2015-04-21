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
package org.estatio.webapp.services.other;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.UdoDomainService;
import org.estatio.dom.apptenancy.EstatioApplicationTenancies;
import org.estatio.dom.asset.Properties;
import org.estatio.dom.asset.Property;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.Leases;

@DomainService()
@DomainServiceLayout(
        menuBar = DomainServiceLayout.MenuBar.SECONDARY,
        menuOrder = "40")
public class EstatioUpgradeService extends UdoDomainService<EstatioUpgradeService> {

    public EstatioUpgradeService() {
        super(EstatioUpgradeService.class);
    }

    private Map<String, String> isisProperties;

    @Programmatic
    @PostConstruct
    public void init(final Map<String, String> isisProperties) {
        super.init(isisProperties);
        this.isisProperties = isisProperties;
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(cssClass = "btn-warning")
    public String upgrade() {
        upgradeProperty();
        return "";
    }

    void upgradeProperty() {
        for (Property property : properties.allProperties()) {
            property.setApplicationTenancyPath(appTenancies.findOrCreatePropertyTenancy(appTenancies.findOrCreateCountryTenancy(property.getCountry()), property.getReference()).getPath());
        }
        getContainer().informUser("Upgrade Properties done");
    }

    void upgradeLease() {
        for (Lease lease : leases.allLeases()) {
            lease.setApplicationTenancyPath(appTenancies.findOrCreateLeaseTenancy(lease).getPath());
        }
        getContainer().informUser("Upgrade Properties done");
    }

    @Inject
    private Properties properties;

    @Inject Leases leases;

    @Inject
    private EstatioApplicationTenancies appTenancies;

}
