package org.estatio.module.base.platform.fixturesupport;

import org.incode.module.fixturesupport.dom.scripts.TeardownFixtureAbstract;

public class DemoData2Teardown<D extends DemoData2<D,T>, T> extends
        TeardownFixtureAbstract {

    private final Class<D> demoDataClass;
    public DemoData2Teardown(final Class<D> demoDataClass) {
        this.demoDataClass = demoDataClass;
        setLocalName(demoDataClass.getCanonicalName());
    }

    @Override
    protected void execute(final ExecutionContext ec) {

        final D anyConstant = demoDataClass.getEnumConstants()[0];
        final Class<T> domainClass = anyConstant.toDomainClass();

        deleteFrom(domainClass);
    }

}

