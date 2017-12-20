package org.incode.platform.dom.docfragment.integtests.demo.fixture.data;

import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.incode.module.fixturesupport.dom.data.DemoData;
import org.incode.module.fixturesupport.dom.data.DemoDataPersistAbstract;
import org.incode.platform.dom.docfragment.integtests.demo.dom.customer.DemoCustomer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(chain = true)
public enum DemoCustomerData implements DemoData<DemoCustomerData, DemoCustomer> {

    Mr_Joe_Bloggs("Mr", "Joe", "Bloggs", "/"),
    Ms_Joanna_Smith("Ms", "Joanna", "Smith", "/ITA"),
    Mrs_Betty_Flintstone("Mrs", "Betty", "Flintstone", "/FRA"),
    ;

    private final String title;
    private final String firstName;
    private final String lastName;
    private final String atPath;

    @Programmatic
    public DemoCustomer asDomainObject() {
        return DemoCustomer.builder()
                .title(title)
                .firstName(firstName)
                .lastName(lastName)
                .atPath(atPath)
                .build();
    }

    @Programmatic
    public DemoCustomer persistUsing(final ServiceRegistry2 serviceRegistry) {
        return Util.persist(this, serviceRegistry);
    }

    @Programmatic
    public DemoCustomer findUsing(final ServiceRegistry2 serviceRegistry) {
        return Util.firstMatch(this, serviceRegistry);
    }

    public static class PersistScript extends DemoDataPersistAbstract<PersistScript, DemoCustomerData, DemoCustomer> {
        public PersistScript() {
            super(DemoCustomerData.class);
        }
    }

}
