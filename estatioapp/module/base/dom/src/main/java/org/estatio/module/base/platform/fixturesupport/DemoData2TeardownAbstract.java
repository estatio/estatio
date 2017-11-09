package org.estatio.module.base.platform.fixturesupport;

import org.incode.module.fixturesupport.dom.scripts.TeardownFixtureAbstract;

public abstract class DemoData2TeardownAbstract<D extends DemoData2<D,T>, T> extends
        TeardownFixtureAbstract {

    private final Class<D> demoDataClass;
    protected DemoData2TeardownAbstract(final Class<D> demoDataClass) {
        this.demoDataClass = demoDataClass;
    }

    @Override
    protected void execute(final ExecutionContext ec) {

        final D anyConstant = demoDataClass.getEnumConstants()[0];
        final Class<T> domainClass = anyConstant.toDomainClass();

        deleteFrom(domainClass);
    }

}

