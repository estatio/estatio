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
package org.estatio.dom.assetfinancial;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.VersionStrategy;

import com.google.common.base.Function;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.utils.TitleBuilder;

import org.estatio.dom.UdoDomainObject2;
import org.estatio.dom.apptenancy.WithApplicationTenancyProperty;
import org.estatio.asset.dom.FixedAsset;
import org.estatio.asset.dom.FixedAssetRole;
import org.estatio.dom.financial.FinancialAccount;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        schema = "dbo" // Isis' ObjectSpecId inferred from @DomainObject#objectType
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByFixedAsset", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.assetfinancial.FixedAssetFinancialAccount "
                        + "WHERE fixedAsset == :fixedAsset"),
        @javax.jdo.annotations.Query(
                name = "findByFixedAssetAndFinancialAccount", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.assetfinancial.FixedAssetFinancialAccount "
                        + "WHERE fixedAsset == :fixedAsset "
                        + "&& financialAccount == :financialAccount"),
        @javax.jdo.annotations.Query(
                name = "findByFinancialAccount", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.assetfinancial.FixedAssetFinancialAccount "
                        + "WHERE financialAccount == :financialAccount")
})
@Unique(name = "FixedAssetFinancialAccount_fixedAsset_financialAccount_IDX", members = { "fixedAsset", "financialAccount" })
@DomainObject(
        objectType = "org.estatio.dom.asset.financial.FixedAssetFinancialAccount"  // backwards compatibility with audit logs.
)
public class FixedAssetFinancialAccount
        extends UdoDomainObject2<FixedAssetFinancialAccount>
        implements WithApplicationTenancyProperty {

    public FixedAssetFinancialAccount() {
        super("fixedAsset,financialAccount");
    }

    public FixedAssetFinancialAccount(FixedAsset fixedAsset, FinancialAccount financialAccount) {
        super("fixedAsset,financialAccount");
        setFinancialAccount(financialAccount);
        setFixedAsset(fixedAsset);
    }

    public String title() {
        return TitleBuilder.start()
                .withTupleElement(getFixedAsset())
                .withTupleElement(getFinancialAccount())
                .toString();
    }

    // //////////////////////////////////////

    @PropertyLayout(
            named = "Application Level",
            describedAs = "Determines those users for whom this object is available to view and/or modify."
    )
    public ApplicationTenancy getApplicationTenancy() {
        return getFixedAsset().getApplicationTenancy();
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "fixedAssetId", allowsNull = "false")
    @MemberOrder(sequence = "1")
    @Property(editing = Editing.DISABLED)
    @PropertyLayout(named = "Property")
    @Getter @Setter
    private FixedAsset fixedAsset;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "financialAccountId", allowsNull = "false")
    @MemberOrder(sequence = "1")
    @Property(editing = Editing.DISABLED)
    @PropertyLayout(named = "Bank account")
    @Getter @Setter
    private FinancialAccount financialAccount;

    // //////////////////////////////////////

    public final static class Functions {

        private Functions() {
        }

        /**
         * A {@link com.google.common.base.Function} that obtains the {@link FixedAssetFinancialAccount#getFinancialAccount() account}.
         */
        public static <T extends FinancialAccount> Function<FixedAssetFinancialAccount, T> financialAccountOf() {
            return new Function<FixedAssetFinancialAccount, T>() {
                @SuppressWarnings("unchecked")
                public T apply(final FixedAssetFinancialAccount fixedAssetFinancialAccount) {
                    return (T) (fixedAssetFinancialAccount != null ? fixedAssetFinancialAccount.getFinancialAccount() : null);
                }
            };
        }

        /**
         * A {@link Function} that obtains the {@link FixedAssetRole#getAsset() asset}..
         */
        public static <T extends FixedAsset> Function<FixedAssetRole, T> assetOf() {
            return new Function<FixedAssetRole, T>() {
                @SuppressWarnings("unchecked")
                public T apply(final FixedAssetRole fixedAssetRole) {
                    return (T) (fixedAssetRole != null ? fixedAssetRole.getAsset() : null);
                }
            };
        }
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public void remove() {
        getContainer().remove(this);
        getContainer().flush();
    }

}