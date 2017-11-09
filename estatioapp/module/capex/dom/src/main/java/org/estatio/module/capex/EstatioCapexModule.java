package org.estatio.module.capex;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.incode.module.fixturesupport.dom.scripts.TeardownFixtureAbstract;

public class EstatioCapexModule {

    private EstatioCapexModule() {}


    public abstract static class ActionDomainEvent<S>
            extends org.apache.isis.applib.services.eventbus.ActionDomainEvent<S> { }

    public abstract static class CollectionDomainEvent<S,T>
            extends org.apache.isis.applib.services.eventbus.CollectionDomainEvent<S,T> { }

    public abstract static class PropertyDomainEvent<S,T>
            extends org.apache.isis.applib.services.eventbus.PropertyDomainEvent<S,T> { }




    public static class Setup extends FixtureScript {

        static boolean prereqsRun = false;

        @Override
        protected void execute(final ExecutionContext executionContext) {
            if(!prereqsRun) {
                prereqsRun = true;
            }
        }
    }

    public static class Teardown extends TeardownFixtureAbstract {
        @Override
        protected void execute(final ExecutionContext executionContext) {
        }
    }


}
