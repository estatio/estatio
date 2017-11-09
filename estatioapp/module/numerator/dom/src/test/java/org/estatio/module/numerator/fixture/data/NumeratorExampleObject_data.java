package org.estatio.module.numerator.fixture.data;

import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.base.platform.fixturesupport.DemoData2;
import org.estatio.module.base.platform.fixturesupport.DemoData2PersistAbstract;
import org.estatio.module.numerator.fixture.dom.NumeratorExampleObject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(chain = true)
public enum NumeratorExampleObject_data implements DemoData2<NumeratorExampleObject_data, NumeratorExampleObject> {

    Kal("Kal"),
    Oxf("Oxf");

    private final String name;

    @Override
    public NumeratorExampleObject asDomainObject(final ServiceRegistry2 serviceRegistry2) {
        return NumeratorExampleObject.builder()
                .name(name)
                .build();
    }

    public static class PersistScript extends DemoData2PersistAbstract<PersistScript, NumeratorExampleObject_data, NumeratorExampleObject> {
        public PersistScript() {
            super(NumeratorExampleObject_data.class);
        }
    }

}
