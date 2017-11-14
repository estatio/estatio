package org.estatio.module.numerator.fixtures.data;

import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.base.platform.fixturesupport.DemoData2;
import org.estatio.module.base.platform.fixturesupport.DemoData2Persist;
import org.estatio.module.base.platform.fixturesupport.DemoData2Teardown;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(chain = true)
public enum NumeratorExampleObject_enum implements DemoData2<NumeratorExampleObject_enum, NumeratorExampleObject> {

    Kal("Kal"),
    Oxf("Oxf");

    private final String name;

    @Override
    public NumeratorExampleObject asDomainObject(final ServiceRegistry2 serviceRegistry2) {
        return NumeratorExampleObject.builder()
                .name(name)
                .build();
    }

    public static class PersistScript extends DemoData2Persist<NumeratorExampleObject_enum, NumeratorExampleObject> {
        public PersistScript() {
            super(NumeratorExampleObject_enum.class);
        }
    }

    public static class DeleteScript
            extends DemoData2Teardown<NumeratorExampleObject_enum, NumeratorExampleObject> {
        public DeleteScript() {
            super(NumeratorExampleObject_enum.class);
        }
    }


}
