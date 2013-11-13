/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
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
package org.estatio.dom.lease.contributed;

import java.util.Collection;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NotContributed;
import org.apache.isis.applib.annotation.NotContributed.As;
import org.apache.isis.applib.annotation.NotInServiceMenu;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;

import org.estatio.dom.WithInterval;
import org.estatio.dom.agreement.AgreementRole;
import org.estatio.dom.agreement.AgreementRoleHolder;
import org.estatio.dom.agreement.AgreementType;
import org.estatio.dom.agreement.AgreementTypes;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseConstants;


/**
 * These contributions act upon {@link AgreementRoleHolder}, and from its 
 * {@link AgreementRoleHolder#getAgreements()set of} {@link AgreementRole}, project to the corresponding
 * {@link Lease}s.
 * 
 * <p>
 * An alternative design would be to simply do a repository query against the database; this would be more efficient 
 * (avoid an N+1 search as is the current design).  However, that query would be quite complex, having to traverse
 * from {@link Lease} to {@link AgreementRole} to {@link org.estatio.dom.party.Party}. 
 */
@Hidden
public class LeaseContributions { 

    /**
     * A contributed collection of the current {@link Lease}s of the {@link AgreementRoleHolder}.
     * 
     * <p>
     * All {@link Lease} are {@link #allLeases(AgreementRoleHolder) contributed} as an action. 
     */
    @NotInServiceMenu
    @NotContributed(As.ACTION) // ie contributed collection
    @Render(Type.EAGERLY)
    @MemberOrder(sequence="80")
    public Collection<Lease> currentLeases(final AgreementRoleHolder agreementRoleHolder) {
        final AgreementType agreementType = agreementTypes.find(LeaseConstants.AT_LEASE);
        return Lists.newArrayList(
                Iterables.transform(
                        Iterables.filter(
                                agreementRoleHolder.getAgreements(),
                                whetherCurrentAndAgreementTypeIs(agreementType)), 
                        AgreementRole.Functions.<Lease>agreementOf()));
    }

    private static Predicate<AgreementRole> whetherCurrentAndAgreementTypeIs(final AgreementType agreementType) {
        return Predicates.and(
                AgreementRole.Predicates.whetherAgreementTypeIs(agreementType),
                WithInterval.Predicates.<AgreementRole>whetherCurrentIs(true));
    }

    
    // //////////////////////////////////////
    
    /**
     * A contributed action of all {@link Lease}s of the {@link AgreementRoleHolder}.
     * 
     * <p>
     * The current {@link Lease}s are {@link #currentLeases(AgreementRoleHolder) contributed} as a collection. 
     */
    @NotInServiceMenu
    @Named("List All")
    @NotContributed(As.ASSOCIATION) // ie contributed action
    public Collection<Lease> allLeases(final AgreementRoleHolder agreementRoleHolder) {
        final AgreementType agreementType = agreementTypes.find(LeaseConstants.AT_LEASE);
        return Lists.newArrayList(
                Iterables.transform(
                        Iterables.filter(
                                agreementRoleHolder.getAgreements(),
                                AgreementRole.Predicates.whetherAgreementTypeIs(agreementType)), 
                        AgreementRole.Functions.<Lease>agreementOf()));
    }

    
    // //////////////////////////////////////

    private AgreementTypes agreementTypes;

    public final void injectAgreementTypes(final AgreementTypes agreementTypes) {
        this.agreementTypes = agreementTypes;
    }

}
