package org.isisaddons.module.base.platform.fixturesupport;

import java.util.List;

import com.google.common.collect.Lists;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import lombok.Getter;

public class DemoData2Persist<D extends DemoData2<D,T>, T> extends FixtureScript {

    private final Class<D> demoDataClass;

    public DemoData2Persist(final Class<D> demoDataClass) {
        this.demoDataClass = demoDataClass;
    }

    /**
     * The number of objects to create.
     */
    @Getter
    private Integer number;
    public void setNumber(final Integer number) {
        this.number = number;
    }

    /**
     * The objects created by this fixture (output).
     */
    @Getter
    private final List<T> objects = Lists.newArrayList();


    @Override
    protected void execute(final FixtureScript.ExecutionContext ec) {

        final D[] enumConstants = demoDataClass.getEnumConstants();
        final int max = enumConstants.length;

        // defaults
        final int number = defaultParam("number", ec, max);

        // validate
        if(number < 0 || number > max) {
            throw new IllegalArgumentException(String.format("number must be in range [0,%d)", max));
        }

        for (int i = 0; i < number; i++) {
            final T domainObject = enumConstants[i].upsertUsing(serviceRegistry);
            ec.addResult(this, domainObject);
            objects.add(domainObject);
        }
    }

    @javax.inject.Inject
    ServiceRegistry2 serviceRegistry;

}

