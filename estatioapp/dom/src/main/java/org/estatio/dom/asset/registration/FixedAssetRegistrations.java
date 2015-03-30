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
package org.estatio.dom.asset.registration;

import java.util.List;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.asset.FixedAsset;

@DomainService(repositoryFor = FixedAssetRegistration.class)
@DomainServiceLayout(
        named = "Fixed Assets",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "10.3")
public class FixedAssetRegistrations extends UdoDomainRepositoryAndFactory<FixedAssetRegistration> {

    public FixedAssetRegistrations() {
        super(FixedAssetRegistrations.class, FixedAssetRegistration.class);
    }

    // //////////////////////////////////////

    @Programmatic
    public List<FixedAssetRegistration> findBySubject(
            final FixedAsset asset) {
        return allMatches("findBySubject",
                "subject", asset);
    }

    // //////////////////////////////////////

    @Programmatic
    public List<FixedAssetRegistration> findBySubjectAndType(
            final FixedAsset asset,
            final FixedAssetRegistrationType type) {
        return allMatches("findBySubject",
                "subject", asset,
                "type", type);
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE, restrictTo = RestrictTo.PROTOTYPING)
    @MemberOrder(name = "Fixed Assets", sequence = "99")
    public List<FixedAssetRegistration> allRegistrations() {
        return allInstances();
    }

}
