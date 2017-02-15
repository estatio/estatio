/*
 *  Copyright 2012-date Eurocommercial Properties NV
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
package org.estatio.app.services.metamodel;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.jdosupport.IsisJdoSupport;
import org.apache.isis.applib.services.metamodel.MetaModelService2;
import org.apache.isis.applib.services.repository.RepositoryService;

import lombok.Getter;
import lombok.Setter;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "org.estatio.app.services.metamodel.BadCommandTargets"
)
@DomainServiceLayout(
        menuBar = DomainServiceLayout.MenuBar.SECONDARY,
        named = "Prototyping",
        menuOrder = "980"
)
public class BadCommandTargets {

    @XmlRootElement
    public static class BadTarget {
        @Getter @Setter
        private String objectType;
        public BadTarget() {}
        public BadTarget(final String objectType) {
            this.objectType = objectType;
        }
    }

    @Action(semantics = SemanticsOf.SAFE)
    public List<BadTarget> findBadCommandTargets() {

        Set<String> badObjectTypes = Sets.newTreeSet();

        List<Map<String, Object>> rows = isisJdoSupport
                .executeSql("select distinct(substring(target, 1, charindex(':', target)-1)) as objectType from isiscommand.Command order by 1");
        for (Map<String, Object> row : rows) {
            String targetStr = (String) row.get("objectType");
            addIfBad(badObjectTypes, targetStr);
        }

        return Lists.newArrayList(
                FluentIterable.from(badObjectTypes)
                              .transform(x -> new BadTarget(x))
                              .toList());
    }

    private void addIfBad(final Set<String> badObjectTypes, final String objectType) {
        final String objectTypeToTest = objectType.startsWith("*") ? objectType.substring(1) : objectType;
        if(!badObjectTypes.contains(objectType)) {
            try {
                metaModelService.fromObjectType(objectTypeToTest);
            } catch (Exception e) {
                badObjectTypes.add(objectType);
            }
        }
    }

    @Inject
    MetaModelService2 metaModelService;

    @Inject
    IsisJdoSupport isisJdoSupport;

    @Inject
    RepositoryService repositoryService;

}
