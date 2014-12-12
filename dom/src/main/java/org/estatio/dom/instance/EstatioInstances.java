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
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Programmatic;
import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.geography.Country;

@DomainService(repositoryFor = EstatioInstance.class)
@DomainServiceLayout(
        named="Security",
        menuBar = DomainServiceLayout.MenuBar.SECONDARY,
        menuOrder = "100.40.50"
)
public class EstatioInstances extends EstatioDomainService<EstatioInstance> {

    public EstatioInstances() {
        super(EstatioInstances.class, EstatioInstance.class);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "1")
    public List<EstatioInstance> allEstatioInstances() {
        return allInstances();
    }

    // //////////////////////////////////////

    @Programmatic
    public EstatioInstance newInstance(
            final String path,
            final ApplicationTenancy applicationTenancy,
            final Country country,
            final EstatioInstance parent) {
        final EstatioInstance estatioInstance = newTransientInstance();
        estatioInstance.setPath(path);
        estatioInstance.setApplicationTenancy(applicationTenancy);
        estatioInstance.setParent(parent);
        estatioInstance.setCountry(country);
        persist(estatioInstance);
        return estatioInstance;
    }

    @Programmatic
    public EstatioInstance findEstatioInstanceByHierarchy(
            final Hierarchy hierarchy) {
        return hierarchy != null
                ? findEstatioInstanceByPath(hierarchy.getPath())
                : null;
    }

    @Programmatic
    public EstatioInstance findEstatioInstanceByPath(
            final String path) {
        return firstMatch("findByPath", "path", path);
    }

    @Programmatic
    public EstatioInstance findEstatioInstanceByName(
            final String name) {
        return firstMatch("findByName", "name", name);
    }

}
