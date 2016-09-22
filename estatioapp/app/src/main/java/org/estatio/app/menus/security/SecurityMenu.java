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
package org.estatio.app.menus.security;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.appfeat.ApplicationFeatureRepository;
import org.apache.isis.applib.services.appfeat.ApplicationMemberType;
import org.apache.isis.core.metamodel.services.appfeat.ApplicationFeatureType;

import org.isisaddons.module.security.dom.permission.ApplicationPermission;
import org.isisaddons.module.security.dom.permission.ApplicationPermissionRepository;

import org.estatio.dom.UdoDomainService;

@DomainService(nature = NatureOfService.VIEW_MENU_ONLY)
@DomainServiceLayout(
        menuBar = DomainServiceLayout.MenuBar.SECONDARY,
        named = "Security",
        menuOrder = "800"
)
public class SecurityMenu extends UdoDomainService<SecurityMenu> {

    public SecurityMenu() {
        super(SecurityMenu.class);
    }


    @Action(semantics = SemanticsOf.SAFE)
    public List<ApplicationPermission> orphanedPermissions() {

        final List<String> packageNames = applicationFeatureRepository.packageNames();
        final Set<String> availableClasses = Sets.newTreeSet();
        for (String packageName : packageNames) {
            appendClasses(packageName, ApplicationMemberType.PROPERTY, availableClasses);
            appendClasses(packageName, ApplicationMemberType.COLLECTION, availableClasses);
            appendClasses(packageName, ApplicationMemberType.ACTION, availableClasses);
        }

        final List<ApplicationPermission> orphaned = Lists.newArrayList();

        final List<ApplicationPermission> permissions = permissionRepository.allPermissions();
        for (ApplicationPermission permission : permissions) {
            final ApplicationFeatureType featureType = permission.getFeatureType();
            final String featureFqn = permission.getFeatureFqn();

            switch (featureType) {

            case PACKAGE:
                if(!packageNames.contains(featureFqn)) {
                    orphaned.add(permission);
                }
                break;
            case CLASS:
                if(!availableClasses.contains(featureFqn)) {
                    orphaned.add(permission);
                }
                break;
            case MEMBER:

                final List<String> split = Splitter.on('#').splitToList(featureFqn);
                final String fqClassName = split.get(0);
                final String memberName = split.get(1);

                final int lastDot = fqClassName.lastIndexOf('.');
                final String packageName = fqClassName.substring(0, lastDot);
                final String className = fqClassName.substring(lastDot + 1);

                final List<String> memberNames = memberNamesOf(packageName, className);

                if(!memberNames.contains(memberName)) {
                    orphaned.add(permission);
                }
                break;
            }
        }

        return orphaned;
    }

    private void appendClasses(
            final String packageName, final ApplicationMemberType memberType, final Set<String> availableClasses) {
        for (String s : applicationFeatureRepository.classNamesContainedIn(packageName,
                memberType)) {
            availableClasses.add(packageName + "." + s);
        }
    }

    private List<String> memberNamesOf(final String packageName, final String className) {
        final List<String> memberNames = Lists.newArrayList();
        appendMembers(packageName, className, ApplicationMemberType.PROPERTY, memberNames);
        appendMembers(packageName, className, ApplicationMemberType.COLLECTION, memberNames);
        appendMembers(packageName, className, ApplicationMemberType.ACTION, memberNames);
        return memberNames;
    }

    private void appendMembers(
            final String packageName,
            final String className,
            final ApplicationMemberType applicationMemberType,
            final List<String> memberNames) {
        final List<String> memberNamesOf =
                applicationFeatureRepository.memberNamesOf(packageName, className, applicationMemberType);
        memberNames.addAll(memberNamesOf);
    }

    @Inject
    ApplicationFeatureRepository applicationFeatureRepository;

    @Inject
    ApplicationPermissionRepository permissionRepository;

}
