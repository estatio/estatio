package com.eurocommercialproperties.estatio.jdo;

import com.eurocommercialproperties.estatio.dom.geography.Countries;
import com.eurocommercialproperties.estatio.dom.geography.Country;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.query.QueryDefault;

public class CountriesJdo extends Countries {

    @Override
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public Country findByReference(@Named("Reference") String reference) {
        //getContainer().flush();
        /* Todo: throws error
            java.lang.IllegalStateException: state is: COMMITTED
            at org.apache.isis.core.commons.ensure.Ensure.ensureThatState(Ensure.java:111)
            at org.apache.isis.runtimes.dflt.runtime.system.transaction.IsisTransaction.flush(IsisTransaction.java:304)
            at org.apache.isis.runtimes.dflt.runtime.system.transaction.IsisTransactionManager.flushTransaction(IsisTransactionManager.java:271)
            at org.apache.isis.runtimes.dflt.runtime.persistence.internal.RuntimeContextFromSession$7.flush(RuntimeContextFromSession.java:217)
            at org.apache.isis.core.metamodel.services.container.DomainObjectContainerDefault.flush(DomainObjectContainerDefault.java:198)
            at com.eurocommercialproperties.estatio.jdo.CountriesJdo.findByReference(CountriesJdo.java:18)
         */
        return firstMatch(queryForFindCountryByReference(reference));
    }

    private static QueryDefault<Country> queryForFindCountryByReference(String reference) {
        return new QueryDefault<Country>(Country.class, "countries_findCountryByReference", "r", reference);
    }
}
